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

package com.cerberustek.window.impl;

import com.cerberustek.exceptions.GLFWInitializationException;
import com.cerberustek.resource.impl.BufferedImageResource;
import com.cerberustek.settings.Settings;
import com.cerberustek.window.callback.*;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.util.RenderUtil;
import com.cerberustek.util.TextureUtil;
import com.cerberustek.window.HeadlessMonitor;
import com.cerberustek.window.Monitor;
import com.cerberustek.window.Window;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ConcurrentModificationException;
import java.util.HashSet;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class WindowImpl implements Window {

    private final HashSet<CerberusCallback> callbacks = new HashSet<>();

    private boolean fullScreen;
    private boolean vsync;
    private boolean resizeable;
    private boolean visible;
    private boolean initialized;

    private long windowId;
    private long threadId;

    private int timeBufferSwap = 0;

    private String title;
    private String version;
    private String renderer;

    private Vector2i position;
    private Vector2i size;

    private Monitor monitor;
    private final Settings settings;

    public WindowImpl(Settings settings) {
        this.settings = settings;
        title = "Cerberus-Engine";
        fullScreen = false;
        vsync = false;
        resizeable = false;
        visible = false;
    }

    @Override
    public void loadCallbacks(Settings settings) {
        if (settings.getBoolean("callback_error", false))
            addCallback(new ErrorCallback());
        if (settings.getBoolean("callback_framebuffersize", true))
            addCallback(new FrameBufferSizeCallback());
        if (settings.getBoolean("callback_window_close", false))
            addCallback(new WindowCloseCallback());
        if (settings.getBoolean("callback_window_focus", true))
            addCallback(new WindowFocusCallback());
        if (settings.getBoolean("callback_window_iconify", false))
            addCallback(new WindowIconifyCallback());
        if (settings.getBoolean("callback_window_maximize", false))
            addCallback(new WindowMaximizeCallback());
        if (settings.getBoolean("callback_window_refresh", false))
            addCallback(new WindowRefreshCallback());
        if (settings.getBoolean("callback_window_reposition", false))
            addCallback(new WindowRepositionCallback());
        if (settings.getBoolean("callback_window_size", true))
            addCallback(new WindowSizeCallback());
    }

    @Override
    public void addCallbackWithoutUpdate(CerberusCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void addCallback(CerberusCallback callback) {
        callbacks.add(callback);
        CerberusRegistry.getInstance().getService(CerberusRenderer.class).tryGLTask((deltaT) ->
                    callback.init(this));
    }

    @Override
    public void removeCallback(CerberusCallback callback) {
        callbacks.remove(callback);
    }

    @Override
    public void requestClose() {
        glfwSetWindowShouldClose(windowId, true);
    }

    @Override
    public boolean isCloseRequested() {
        return glfwWindowShouldClose(windowId);
    }

    @Override
    public void setFullscreenWithoutUpdate(boolean fullscreen) {
        this.fullScreen = fullscreen;
    }

    @Override
    public void setFullScreen(boolean fullScreen) {
        if (fullScreen)
            glfwSetWindowMonitor(windowId, monitor.getMonitorId(), getPosition().getX(), getPosition().getY(),
                monitor.getSize().getX(), monitor.getSize().getY(), monitor.getRefreshRate());
        else
            glfwSetWindowMonitor(windowId, NULL, getPosition().getX() / 4, getPosition().getY() / 4,
                    monitor.getSize().getX() / 2, monitor.getSize().getY() / 2, monitor.getRefreshRate());
        this.fullScreen = fullScreen;
    }

    @Override
    public boolean isFullScreen() {
        return fullScreen;
    }

    @Override
    public void enableVsyncWithoutUpdate(boolean value) {
        this.vsync = value;
    }

    @Override
    public void enableVsync(boolean value) {
        this.vsync = value;
        glfwSwapInterval(value ? GLFW_TRUE : GLFW_FALSE);
    }

    @Override
    public boolean vsync() {
        return vsync;
    }

    @Override
    public void setResizableWithoutUpdate(boolean value) {
        this.resizeable = value;
    }

    @Override
    public void setResizable(boolean value) {
        glfwWindowHint(GLFW_RESIZABLE, value ? GLFW_TRUE : GLFW_FALSE);
        this.resizeable = value;
    }

    @Override
    public boolean isResizable() {
        return this.resizeable;
    }

    @Override
    public void setVisibleWithoutUpdate(boolean value) {
        this.visible = value;
    }

    @Override
    public void setVisible(boolean value) {
        glfwWindowHint(GLFW_VISIBLE, value ? GLFW_TRUE : GLFW_FALSE);
        this.visible = value;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void setTitleWithoutUpdate(String title) {
        this.title = title;
    }

    @Override
    public void setTitle(String title) {
        glfwSetWindowTitle(windowId, title);
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setSize(Vector2i size) {
        this.size = size;
        glfwSetWindowSize(windowId, size.getX(), size.getY());
    }

    @Override
    public Vector2i getSize() {
        if (isInitialized() && isGlThread()) {
            IntBuffer width = MemoryUtil.memAllocInt(1);
            IntBuffer height = MemoryUtil.memAllocInt(1);

            glfwGetWindowSize(windowId, width, height);
            Vector2i out = new Vector2i(width.get(0), height.get(0));

            MemoryUtil.memFree(width);
            MemoryUtil.memFree(height);

            return out;
        } else
            return size;
    }

    @Override
    public void setPosition(Vector2i position) {
        this.position = position;
        glfwSetWindowPos(windowId, position.getX(), position.getY());
    }

    @Override
    public Vector2i getPosition() {
        if (isGlThread()) {
            /*IntBuffer xPos = BufferUtils.createIntBuffer(1);
            IntBuffer yPos = BufferUtils.createIntBuffer(1);
            glfwGetWindowPos(windowId, xPos, yPos);
            position = new Vector2i(xPos.get(0), yPos.get(0));
            MemoryUtil.memFree(yPos);
            MemoryUtil.memFree(xPos);*/
            return position;
        } else
            return position;
    }

    @Override
    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public Monitor getMonitor() {
        return monitor;
    }

    @Override
    public void setWindowIcon(BufferedImageResource icon16res, BufferedImageResource icon32res) {
        if (isInitialized() && isGlThread()) {
            ByteBuffer icon16 = icon16res.load();
            icon16.rewind();
            ByteBuffer icon32 = icon32res.load();
            icon32.rewind();

            try (GLFWImage.Buffer buffer = GLFWImage.malloc(2)) {
                buffer.position(0).width(icon16res.getSize().getX()).height(icon16res.getSize().getY()).pixels(icon16);
                buffer.position(1).width(icon32res.getSize().getX()).height(icon32res.getSize().getY()).pixels(icon32);

                buffer.rewind();
                glfwSetWindowIcon(windowId, buffer);
            }
        }
    }

    @Override
    public boolean isGlThread() {
        return isGlThread(Thread.currentThread());
    }

    @Override
    public boolean isGlThread(Thread thread) {
        return thread.getId() == threadId;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public boolean isHeadless() {
        return false;
    }

    @Override
    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public long id() {
        return windowId;
    }

    @Override
    public long thread() {
        return threadId;
    }

    @Override
    public void destroy() {
        initialized = false;
        GL.destroy();
        glfwTerminate();
        callbacks.forEach(CerberusCallback::free);
    }

    @Override
    public void init() {
        long currentTime = System.currentTimeMillis();
        CerberusRegistry.getInstance().info("Opening window...");

        if (!glfwInit())
            throw new GLFWInitializationException("Could not initialize GLFW! Is an instance already current" +
                    " in context?");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, visible ? GLFW_TRUE : GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, resizeable ? GLFW_TRUE : GLFW_FALSE);
        glfwWindowHint(GLFW_VISIBLE, visible ? GLFW_TRUE : GLFW_FALSE);

        if (visible) {
            if (monitor == null)
                monitor = Monitor.getPrimary();
            size = new Vector2i(monitor.getSize().getX() / 2, monitor.getSize().getY() / 2);
        } else {
            if (monitor == null)
                monitor = new HeadlessMonitor(0);

            size = new Vector2i(settings.getInteger("window_width", 800),
                    settings.getInteger("window_height", 600));
            fullScreen = false;
        }
        windowId = glfwCreateWindow(size.getX(), size.getY(), title,
                fullScreen ? monitor.getMonitorId() : NULL, NULL);

        setPosition(new Vector2i(monitor.getSize().getX() / 4, monitor.getSize().getY() / 4));
        glfwMakeContextCurrent(windowId);
        glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));

        while (true) {
            try {
                callbacks.forEach(callback -> callback.init(this));
                break;
            } catch (ConcurrentModificationException e) {
                // Try again
            }
        }

        enableVsync(vsync);
        GL.createCapabilities();
        RenderUtil.initDefault();
        if (fullScreen)
            RenderUtil.setViewport(monitor.getSize());
        else
            RenderUtil.setViewport(monitor.getSize().div(2));

        glfwPollEvents();
        threadId = Thread.currentThread().getId();
        initialized = true;

        RenderUtil.printSystemInfo(currentTime);

        renderer = glGetString(GL_RENDERER);
        version = glGetString(GL_VERSION);

        try (FileInputStream icon16stream = new FileInputStream("textures/icon16.png");
                FileInputStream icon32stream = new FileInputStream("textures/icon32.png")) {

            BufferedImageResource icon16 = new BufferedImageResource(ImageIO.read(icon16stream), 0);
            BufferedImageResource icon32 = new BufferedImageResource(ImageIO.read(icon32stream), 0);

            setWindowIcon(icon16, icon32);

        } catch (IOException e) {
            CerberusRegistry.getInstance().warning("Failed to setup window icon: " + e);
        }
    }

    @Override
    public void update(double delta) {
        long timeSwapStart = System.nanoTime();

        glfwSwapBuffers(windowId);
        glfwPollEvents();

        timeBufferSwap = (int) (System.nanoTime() - timeSwapStart);
    }

    @Override
    public double timeBufferSwap() {
        return timeBufferSwap * 1e-6;
    }

    @Override
    public String getRenderer() {
        return renderer;
    }

    @Override
    public String getGLVersion() {
        return version;
    }

    @Override
    public void set() {
        TextureUtil.unbindRenderbuffer();
        TextureUtil.unbindFramebuffer();
        RenderUtil.setViewport(getSize());
    }

    @Override
    public Vector2i getScreenSize() {
        if (isInitialized() && isGlThread()) {
            IntBuffer width = MemoryUtil.memAllocInt(1);
            IntBuffer height = MemoryUtil.memAllocInt(1);

            glfwGetFramebufferSize(windowId, width, height);
            Vector2i out = new Vector2i(width.get(0), height.get(0));

            MemoryUtil.memFree(width);
            MemoryUtil.memFree(height);

            return out;
        } else
            return getSize();
    }
}
