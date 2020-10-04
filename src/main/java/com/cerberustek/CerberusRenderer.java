/*
 * Cerberus-Renderer is a OpenGL-based rendering engine.
 * Visit https://cerberustek.com for more details
 * Copyright (c)  2020  Adrian Paskert
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. See the file LICENSE included with this
 * distribution for more information.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.cerberustek;

import com.cerberustek.commands.*;
import com.cerberustek.events.ExceptionEvent;
import com.cerberustek.events.GLFWInitEvent;
import com.cerberustek.events.GracefulShutdownEvent;
import com.cerberustek.exceptions.IllegalContextException;
import com.cerberustek.geometry.GeometryBoard;
import com.cerberustek.geometry.impl.GeometryBoardImpl;
import com.cerberustek.gui.CFXManager;
import com.cerberustek.gui.impl.CFXManagerImpl;
import com.cerberustek.input.InputBoard;
import com.cerberustek.input.impl.InputBoardImpl;
import com.cerberustek.material.MaterialBoard;
import com.cerberustek.material.impl.MaterialBoardImpl;
import com.cerberustek.pipeline.RenderPipeline;
import com.cerberustek.pipeline.impl.RenderPipelineImpl;
import com.cerberustek.resource.Resource;
import com.cerberustek.service.CerberusService;
import com.cerberustek.service.terminal.TerminalExecutor;
import com.cerberustek.settings.Settings;
import com.cerberustek.settings.impl.SettingsImpl;
import com.cerberustek.shader.ShaderBoard;
import com.cerberustek.shader.impl.ShaderBoardImpl;
import com.cerberustek.texture.TextureBoard;
import com.cerberustek.texture.impl.TextureBoardImpl;
import com.cerberustek.util.PriorityConsumer;
import com.cerberustek.util.PropertyMap;
import com.cerberustek.util.impl.SimplePropertyMap;
import com.cerberustek.window.Window;
import com.cerberustek.window.impl.HeadlessWindowImpl;
import com.cerberustek.window.impl.WindowImpl;
import com.cerberustek.worker.*;
import com.cerberustek.worker.impl.WorkerBossImpl;

import java.io.File;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CerberusRenderer implements CerberusService {

    public final static String VERSION = "0.91";
    public final static String SETTINGS_FILE = "config/settings.properties";

    public final static String GROUP_RENDER = "renderer";
    public final static String GROUP_INPUT = "input";
    public final static String GROUP_OTHER = "miscellaneous";

    public final static String PERMISSION_COMMAND = "de.cerberus.renderer";
    public final static String PERMISSION_COMMAND_FPS = PERMISSION_COMMAND + ".fps";
    public final static String PERMISSION_COMMAND_WORKER = PERMISSION_COMMAND + ".worker";
    public final static String PERMISSION_COMMAND_SHADER = PERMISSION_COMMAND + ".shader";
    public final static String PERMISSION_COMMAND_TEXTURE = PERMISSION_COMMAND + ".texture";
    public final static String PERMISSION_COMMAND_GEOMETRY = PERMISSION_COMMAND + ".geometry";

    private final WorkerBoss boss;
    private final Settings settings;
    @SuppressWarnings("rawtypes")
    private final PropertyMap<Resource> propertyMap;
    private final Queue<PriorityConsumer> executerHandlers = new PriorityQueue<>();
    private final Queue<PriorityConsumer> parallelHandlers = new PriorityQueue<>();
    private final HashSet<PriorityConsumer> parallelSubscribers = new HashSet<>();
    private final RenderPipeline pipeline;
    private final TextureBoard textureBoard;
    private final ShaderBoard shaderBoard;
    private final GeometryBoard geometryBoard;
    private final MaterialBoard materialBoard;
    private final Window window;
    private final ParallelThread parallelThread;
    private final CFXManager guiManager;
    private final InputBoard inputBoard;

    private WorkerTask renderTask;
    private double parallelDelta;
    private long droppedFrames;

    public CerberusRenderer() {
        this(false);
    }

    public CerberusRenderer(boolean headless) {
        this.boss = new WorkerBossImpl();
        this.settings = new SettingsImpl(new File(SETTINGS_FILE), false);
        this.propertyMap = new SimplePropertyMap<>();
        this.pipeline = new RenderPipelineImpl();
        this.textureBoard = new TextureBoardImpl();
        this.shaderBoard = new ShaderBoardImpl();
        this.geometryBoard = new GeometryBoardImpl();
        this.materialBoard = new MaterialBoardImpl();
        if (headless)
            this.window = new HeadlessWindowImpl(settings);
        else
            this.window = new WindowImpl(settings);
        this.parallelThread = new ParallelThread();
        this.guiManager = new CFXManagerImpl();
        this.inputBoard = new InputBoardImpl();
    }

    @Override
    public void start() {
        CerberusRegistry.getInstance().info("Starting renderer...");
        settings.init();

        boss.createGroup(GROUP_RENDER, WorkerPriority.ABSOLUTE);
        boss.createGroup(GROUP_INPUT, WorkerPriority.HIGH);
        boss.createGroup(GROUP_OTHER, WorkerPriority.LOW);

        boss.createWorker(WorkerPriority.ABSOLUTE, GROUP_RENDER, GROUP_INPUT);
        boss.createWorker(WorkerPriority.MEDIUM, GROUP_OTHER);

        boss.changeStatus(WorkerStatus.STARTING);
        parallelThread.start();

        boss.submitTopTask((t) -> {
            window.setFullscreenWithoutUpdate(settings.getBoolean("fullscreen", false));
            window.setResizableWithoutUpdate(settings.getBoolean("resizeable", true));
            window.enableVsyncWithoutUpdate(settings.getBoolean("vertical_synchronization", true));
            window.setVisibleWithoutUpdate(settings.getBoolean("visible", true));

            window.loadCallbacks(settings);

            window.init();
            shaderBoard.init();
            geometryBoard.init();
            inputBoard.init();
            guiManager.init();

            submitGLTask(d -> CerberusRegistry.getInstance().getService(CerberusEvent.class).executeFullEIT(new GLFWInitEvent()));
        }, WorkerPriority.ABSOLUTE, GROUP_RENDER);

        int frameCap = settings.getInteger("framecap", 144);
        if (frameCap > 0) {
            double deltaTime = 1000d / (double) frameCap;
            renderTask = boss.submitTask(this::renderFrame, WorkerPriority.ABSOLUTE, GROUP_RENDER, -1, deltaTime);
        } else
            renderTask = boss.submitTask(this::renderFrame, WorkerPriority.ABSOLUTE, GROUP_RENDER, -1, 1);

        // parallelTask = new WaitingConsumerTask(WorkerPriority.ABSOLUTE, this::renderParallel);
        // boss.submitTask(parallelTask, GROUP_PARALLEL);
        // boss.submitTask(this::renderFrame, WorkerPriority.ABSOLUTE, GROUP_RENDER, -1, 1);


        TerminalExecutor executor = CerberusRegistry.getInstance().getTerminal().getExecutor();
        executor.registerCommand(new FPSCommand());
        executor.registerCommand(new WorkerCommand());
        executor.registerCommand(new ShaderCommand());
        executor.registerCommand(new TextureCommand());
        executor.registerCommand(new GeometryCommand());

        CerberusRegistry.getInstance().getService(CerberusEvent.class).addListener((RenderPipelineImpl) pipeline);


        CerberusRegistry.getInstance().info("... done starting renderer!");
    }

    private void renderFrame(double delta, int repetition) {
        try {
            // repetition is always -1, due to the render cycle being endless (:
            // I know that may not be the most elegant solution, but it's still the
            // nicest one I could come up with.
            if (window.isCloseRequested())
                stopGracefully();

            if (window.isInitialized()) {
                if (!executerHandlers.isEmpty()) {
                    try {
                        PriorityConsumer consumer;
                        while ((consumer = executerHandlers.poll()) != null)
                            consumer.accept(delta);
                    } catch (ConcurrentModificationException e) {
                        CerberusRegistry.getInstance().warning("Skipped executor handles!");
                        CerberusRegistry.getInstance().getService(CerberusEvent.class)
                                .executeFullEIF(new ExceptionEvent(CerberusRenderer.class, e));
                    }
                }

                long startTime = System.nanoTime();
                // Wait for the parallel thread to finish
                while (!parallelThread.hasRun) {

                    // Try to execute an other render task instead
                    if (!executeOtherRenderTask()) {
                        // if there is no other task to be executed,
                        // just wait for the parallel thread to finish

                        try {
                            synchronized (CerberusRenderer.this) {
                                CerberusRenderer.this.wait();
                            }
                        } catch (InterruptedException e) {
                            CerberusRegistry.getInstance().warning("Failed to wait for the parallel thread to finish");
                            CerberusRegistry.getInstance().getService(CerberusEvent.class)
                                    .executeFullEIF(new ExceptionEvent(CerberusRenderer.class, e));
                        }
                    }
                }
                parallelDelta = (double) (System.nanoTime() - startTime) * 1e-6;

                synchronized (parallelThread) {
                    parallelThread.notifyAll();
                }

                pipeline.update(delta);
                window.update(delta);
            }
        } catch (Exception e) {
            CerberusRegistry.getInstance().getService(CerberusEvent.class)
                    .executeFullEIF(new ExceptionEvent(CerberusRenderer.class, e));
            droppedFrames++;
        }
    }

    /**
     * Will execute a task on the render thread other than
     * the main render loop.
     *
     * This could, for example, be a call to upload some data
     * to the gpu.
     */
    private boolean executeOtherRenderTask() {
        if (!window.isGlThread())
            throw new IllegalContextException();

        WorkerGroup group = boss.getGroup(GROUP_RENDER);
        Collection<WorkerTask> tasks = group.getTasks();

        long currentTime = System.nanoTime();

        WorkerTask task = null;
        float significance = 0;

        for (WorkerTask t : tasks) {
            if (t.equals(renderTask))
                continue;

            float currentSignificance = t.getSignificance(currentTime);
            if (currentSignificance >= 0 && (task == null || currentSignificance > significance)) {
                task = t;
                significance = currentSignificance;
            }
        }

        if (task != null) {
            task.execute(currentTime);
            group.gracefullDecomissionTask(task);
            return true;
        }
        return false;
    }

    /**
     * Used to execute stuff parallel to the current frame.
     *
     * Parallel executor handles (both subscribed and non-
     * subscribed) are run from here.
     *
     * @param delta time since the task was submitted
     */
    private void renderParallel(double delta) {
        PriorityConsumer current;
        // run non-subscribed handles
        if (!parallelHandlers.isEmpty()) {
            try {
                while ((current = parallelHandlers.poll()) != null)
                    current.accept(delta);
            } catch (ConcurrentModificationException e) {
                CerberusRegistry.getInstance().debug("Skipped parallel executor handles!");
                CerberusRegistry.getInstance().getService(CerberusEvent.class)
                        .executeFullEIF(new ExceptionEvent(CerberusRenderer.class, e));
            }
        }

        // run subscribed handles
        if (!parallelSubscribers.isEmpty()) {
            try {
                for (PriorityConsumer parallelSubscriber : parallelSubscribers)
                    parallelSubscriber.accept(delta);
            } catch (ConcurrentModificationException e) {
                CerberusRegistry.getInstance().debug("Skipped parallel executor subscribers!");
                CerberusRegistry.getInstance().getService(CerberusEvent.class)
                        .executeFullEIF(new ExceptionEvent(CerberusRenderer.class, e));
            }
        }

        pipeline.getScene().update(delta);
    }

    private void stopGracefully() {
        CerberusRegistry.getInstance().getService(CerberusEvent.class).executeFullEIF(new GracefulShutdownEvent(
                this, "GLFW window close request", Thread.currentThread().getStackTrace()));
    }

    @Override
    public void stop() {
        CerberusRegistry.getInstance().info("Shutting down renderer...");

        boss.submitTopTask(t -> {
            window.requestClose();
            CerberusRegistry.getInstance().info("Disassembling render pipeline...");
            pipeline.destroy();
            settings.destroy();
            guiManager.destroy();
            CerberusRegistry.getInstance().info("Cleaning shader cache...");
            shaderBoard.destroy();
            CerberusRegistry.getInstance().info("Cleaning geometry cache...");
            geometryBoard.destroy();
            CerberusRegistry.getInstance().info("Cleaning material cache...");
            materialBoard.destroy();
            CerberusRegistry.getInstance().info("Cleaning texture cache...");
            textureBoard.destroy();
            CerberusRegistry.getInstance().info("Terminating GLFW and GLEW...");
            window.destroy();


            boss.changeStatus(WorkerStatus.TERMINATING);

            // stop the parallel thread
            if (parallelThread.isRunning) {
                parallelThread.requestStop();

                synchronized (parallelThread) {
                    parallelThread.notifyAll();
                }
            }
            CerberusRegistry.getInstance().info("... done!");
        }, GROUP_RENDER);
    }

    @Override
    public Class<? extends CerberusService> serviceClass() {
        return CerberusRenderer.class;
    }

    @Override
    public Collection<Thread> getThreads() {
        return boss.getThreads();
    }

    /**
     * Returns the Worker used for scheduling rendering tasks.
     *
     * @return WorkerBoss
     */
    public WorkerBoss getWorker() {
        return boss;
    }

    /**
     * Submit a task to be executed on a thread with open-gl capabilities
     * initialized.
     *
     * @param consumer task
     * @return Worker Task
     */
    public WorkerTask submitGLTask(Consumer<Double> consumer) {
        return boss.submitTask(consumer, WorkerPriority.MEDIUM, GROUP_RENDER);
    }

    /**
     * Submit a task to be executed on a thread with open-gl capabilities
     * initialized.
     *
     * @param consumer task
     * @param delay delay before start
     * @return Worker task
     */
    public WorkerTask submitGLTask(Consumer<Double> consumer, int delay) {
        return boss.submitTask(consumer, WorkerPriority.MEDIUM, GROUP_RENDER, delay);
    }

    /**
     * Submit a task to be executed on a thread with open-gl capabilities
     * initialized.
     *
     * @param consumer task
     * @param repetitions repetitions
     * @return Worker task
     */
    public WorkerTask submitGLTask(BiConsumer<Double, Integer> consumer, int repetitions) {
        return boss.submitTask(consumer, WorkerPriority.MEDIUM, GROUP_RENDER, repetitions);
    }

    /**
     * Submit a task to be executed on a thread with open-gl capabilities
     * initialized.
     *
     * @param consumer task
     * @param delay delay in between repetitions
     * @param repetitions repetitions
     * @return Worker task
     */
    public WorkerTask submitGLTask(BiConsumer<Double, Integer> consumer, int repetitions, int delay) {
        return boss.submitTask(consumer, WorkerPriority.MEDIUM, GROUP_RENDER, repetitions, delay);
    }

    /**
     * Decommissions a Worker task form the GL-Thread.
     * @param task task to decommission
     */
    public void decomissionGLTask(WorkerTask task) {
        boss.decomissionTask(task, GROUP_RENDER);
    }

    /**
     * Will try to execute the task instantly.
     *
     * If the current thread is not a GL-Thread, this method will submit
     * the task to the render queue. If the task can be executed in the
     * same thread, this return null. Otherwise it will return the WorkerTask
     * object under which the task was placed in the queue.
     *
     * @param consumer task
     * @return WorkerTask
     */
    public WorkerTask tryGLTask(Consumer<Double> consumer) {
        if (window.isInitialized() && window.isGlThread())
            consumer.accept((double) 0);
        else
            return submitGLTask(consumer);
        return null;
    }

    /**
     * Will try to execute the task instantly.
     *
     * If the current thread is not a GL-thread, this method will
     * offer the specified consumer to the executor handle for
     * execution prior to the next frame drawing.
     * If the queue can currently accept tasks, this method will
     * return true, otherwise it will return false.
     *
     * @param consumer task
     * @return success
     */
    public boolean tryNextGLTask(PriorityConsumer consumer) {
        if (window.isInitialized() && window.isGlThread())
            consumer.accept((double) 0);
        else
            return executerHandlers.offer(consumer);
        return true;
    }

    /**
     * Will execute the task prior to drawing the next
     * frame.
     *
     * This method will add the task to the executor
     * handles. If the executor queue does not accept
     * the task, this method will return false, otherwise
     * it will return true.
     *
     * @param consumer consumer
     * @return success
     */
    public boolean nextGLTask(PriorityConsumer consumer) {
        return executerHandlers.offer(consumer);
    }

    /**
     * Will execute the task prior to the parallel subscriber
     * handles and thus parallel to the current frame drawing
     * and prior to the next frame drawing.
     *
     * This method will add the task to the parallel executor
     * handles. If the executor queue does not accept the
     * task, this method will return false, otherwise
     * it will return true.
     * Keep in mind, that the delta value passed to the
     * consumer is <bold>not</bold> the time passed since
     * the frame, like it would be with GL-tasks. It re-
     * presents the time the last parallel execution task
     * took to run.
     *
     * @param consumer consumer
     * @return success
     */
    public boolean nextParallelTask(PriorityConsumer consumer) {
        return parallelHandlers.offer(consumer);
    }

    /**
     * Will add a consumer to the parallel subscriber
     * handles.
     *
     * The parallel subscriber handles behave similar to
     * the parallel task (<code>nextParallelTask(...)</code>),
     * the difference being, that subscribed consumers
     * will execute for <bold>every</bold> parallel frame
     * call until either they are removed, or the engine shuts
     * down.
     * Subscribed tasks will run after the non-subscribed
     * parallel tasks.
     *
     * @param consumer parallel consumer task to add
     */
    public void subscribeParallelTask(PriorityConsumer consumer) {
        parallelSubscribers.add(consumer);
    }

    /**
     * Will remove a consumer from the parallel
     * subscriber handles.
     *
     * @param consumer consumer task to remove
     */
    public void unsubscribeParallelTask(PriorityConsumer consumer) {
        parallelSubscribers.remove(consumer);
    }

    /**
     * Will remove all subscribed parallel executor
     * handlers from the system.
     *
     * Since the entire engine is heavily multithreaded,
     * it can happen, that some subscribed tasks still
     * get executed in the next frame due to synchronization
     * issues in the threading API, so don't rely on
     * this call to remove all subscribed handles
     * instantaneously.
     */
    public void clearParallelSubscribers() {
        parallelSubscribers.clear();
    }

    /**
     * Returns the settings currently used by the engine.
     *
     * @return Settings
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * Returns the engines main display window.
     *
     * @return main window
     */
    public Window getWindow() {
        return window;
    }

    /**
     * Returns the RenderPipeline.
     *
     * @return RenderPipeline
     */
    public RenderPipeline getPipeline() {
        return pipeline;
    }

    /**
     * Returns the TextureBoard.
     *
     * @return TextureBoard
     */
    public TextureBoard getTextureBoard() {
        return textureBoard;
    }

    /**
     * Returns the ShaderBoard.
     *
     * @return ShaderBoard
     */
    public ShaderBoard getShaderBoard() {
        return shaderBoard;
    }

    /**
     * Returns the GeometryBoard.
     *
     * @return GeometryBoard
     */
    public GeometryBoard getGeometryBoard() {
        return geometryBoard;
    }

    /**
     * Returns the MaterialBoard.
     *
     * @return MaterialBoard
     */
    public MaterialBoard getMaterialBoard() {
        return materialBoard;
    }

    /**
     * Returns the InputBoard.
     *
     * @return InputBoard
     */
    public InputBoard getInputBoard() {
        return inputBoard;
    }

    /**
     * Returns the Gui Manager.
     *
     * @return Gui Manager
     */
    public CFXManager getGUIManager() {
        return guiManager;
    }

    /**
     * Returns the resource PropertyMap
     *
     * @return Resource PropertyMap
     */
    @SuppressWarnings("rawtypes")
    public PropertyMap<Resource> getPropertyMap() {
        return propertyMap;
    }

    /**
     * Returns the time the render thread had to wait for the
     * parallel render assembly to finish during the last frame
     * in ms.
     *
     * This number should ideally be as close to 0 as possible.
     *
     * @return wait time in ms
     */
    public double getParallelDelta() {
        return parallelDelta;
    }

    /**
     * Returns the total number of dropped frames since
     * the render engine was started.
     * @return dropped frames since start
     */
    public long countDroppedFrames() {
        return droppedFrames;
    }

    /**
     * The parallel rendering thread.
     *
     * This thread will run parallel to the rendering of
     * each frame and in doing so prepare the next frame
     * for rendering.
     * This is sometimes referred at as the 'render
     * assembly'.
     */
    private class ParallelThread extends Thread {

        private boolean isRunning;
        private boolean hasRun;

        @Override
        public void run() {
            isRunning = true;
            hasRun = true;

            long lastTime = System.nanoTime();
            long currentTime;

            while (isRunning) {
                try {
                    synchronized (this) {
                        this.wait();
                    }
                } catch (InterruptedException e) {
                    CerberusRegistry.getInstance().warning("Failed to pause parallel render thread");
                    CerberusRegistry.getInstance().getService(CerberusEvent.class)
                            .executeFullEIF(new ExceptionEvent(CerberusRenderer.class, e));
                }
                hasRun = false;

                currentTime = System.nanoTime();
                double passed = (double) (currentTime - lastTime) / 1E9;
                lastTime = currentTime;

                CerberusRenderer.this.renderParallel(passed);
                hasRun = true;

                // notify the render thread in case it paused
                synchronized (CerberusRenderer.this) {
                    CerberusRenderer.this.notifyAll();
                }
            }
        }

        /**
         * Returns if the parallel thread is done with the
         * preparations for the next frame.
         *
         * If this is not the case and the rendering loop is
         * already finished with rendering the current frame,
         * the main render thread will wait for this thread
         * to finish preparing this next frame (a.k.a. wait
         * for this method to return true).
         *
         * @return as run
         */
        private boolean hasRun() {
            return hasRun;
        }

        /**
         * Returns true, if the parallel rendering system is
         * currently active.
         *
         * This method should always return true while the
         * render engine is running.
         *
         * @return is parallel rendering active
         */
        private boolean isRunning() {
            return isRunning;
        }

        /**
         * Will cause the parallel rendering thread to stop
         * after the current run.
         *
         * This method should only be called by the internal
         * render engine core.
         */
        private void requestStop() {
            isRunning = false;
        }
    }
}
