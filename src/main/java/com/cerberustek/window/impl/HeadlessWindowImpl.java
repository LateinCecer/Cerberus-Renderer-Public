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

import com.cerberustek.CerberusEvent;
import com.cerberustek.events.ExceptionEvent;
import com.cerberustek.exceptions.EGLException;
import com.cerberustek.resource.impl.BufferedImageResource;
import com.cerberustek.settings.Settings;
import com.cerberustek.window.callback.CerberusCallback;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.util.RenderUtil;
import com.cerberustek.util.TextureUtil;
import com.cerberustek.window.Monitor;
import com.cerberustek.window.Window;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

import static org.lwjgl.egl.EGL10.*;
import static org.lwjgl.egl.EGL12.*;
import static org.lwjgl.egl.EGL14.*;
import static org.lwjgl.egl.EXTDeviceEnumeration.*;
import static org.lwjgl.egl.EXTDeviceQuery.*;
import static org.lwjgl.egl.EXTPlatformBase.*;
import static org.lwjgl.egl.EXTPlatformDevice.EGL_PLATFORM_DEVICE_EXT;
import static org.lwjgl.opengl.GL11.*;

public class HeadlessWindowImpl implements Window {

    private boolean initiated;
    private boolean shouldClose;

    private long displayId;
    private long threadId;

    private long timeBufferSwap = 0;

    private String version;
    private String renderer;

    private Monitor monitor;
    private final Settings settings;

    private Vector2i pBufferSize;

    public HeadlessWindowImpl(Settings settings) {
        this.settings = settings;
        this.initiated = false;
        this.shouldClose = false;
    }

    @Override
    public void loadCallbacks(Settings settings) {
        // we don't do that here
    }

    @Override
    public void addCallbackWithoutUpdate(CerberusCallback callback) {
        // we don't do that here
    }

    @Override
    public void addCallback(CerberusCallback callback) {
        // we don't do that here
    }

    @Override
    public void removeCallback(CerberusCallback callback) {
        // we don't do that here
    }

    @Override
    public void requestClose() {
        shouldClose = true;
    }

    @Override
    public boolean isCloseRequested() {
        return shouldClose;
    }

    @Override
    public void setFullscreenWithoutUpdate(boolean fullscreen) {
        // we don't do that here
    }

    @Override
    public void setFullScreen(boolean fullScreen) {
        // we don't do that here
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void enableVsyncWithoutUpdate(boolean value) {
        // we don't do that here
    }

    @Override
    public void enableVsync(boolean value) {
        // we don't do that here
    }

    @Override
    public boolean vsync() {
        return false;
    }

    @Override
    public void setResizableWithoutUpdate(boolean value) {
        // we don't do that here
    }

    @Override
    public void setResizable(boolean value) {
        // we don't do that here
    }

    @Override
    public boolean isResizable() {
        return false;
    }

    @Override
    public void setVisibleWithoutUpdate(boolean value) {
        // we don't do that here
    }

    @Override
    public void setVisible(boolean value) {
        // we don't do that here
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public void setTitleWithoutUpdate(String title) {
        // we don't do that here
    }

    @Override
    public void setTitle(String title) {
        // we don't do that here
    }

    @Override
    public String getTitle() {
        return "Cerberus-Compute (EGL-Display)";
    }

    @Override
    public void setSize(Vector2i size) {
        // we don't do that here
    }

    @Override
    public Vector2i getSize() {
        return pBufferSize;
    }

    @Override
    public void setPosition(Vector2i position) {
        // we don't do that here
    }

    @Override
    public Vector2i getPosition() {
        return new Vector2i(0, 0);
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
    public void setWindowIcon(BufferedImageResource icon16, BufferedImageResource icon32) {
        // we don't do that here
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
        return initiated;
    }

    @Override
    public boolean isHeadless() {
        return true;
    }

    @Override
    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public long id() {
        return displayId;
    }

    @Override
    public long thread() {
        return threadId;
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
    public void destroy() {
        initiated = false;
        GL.destroy();
        eglTerminate(displayId);
    }

    @Override
    public void init() {
        long currentTime = System.currentTimeMillis();

        PointerBuffer pointer = BufferUtils.createPointerBuffer(16);
        IntBuffer numDevices = BufferUtils.createIntBuffer(1);
        eglQueryDevicesEXT(pointer, numDevices);

        CerberusRegistry.getInstance().debug("There is a total of " + numDevices.get(0) + " EGL devices available:");
        for (int i = 0; i < numDevices.get(0); i++) {
            CerberusRegistry.getInstance().debug("----------------------[" + i + "]---------------------");
            String support = eglQueryDeviceStringEXT(pointer.get(i), EGL_EXTENSIONS);
            CerberusRegistry.getInstance().debug(" + Support: " + support);
        }
        CerberusRegistry.getInstance().debug("");


        // https://developer.nvidia.com/blog/egl-eye-opengl-visualization-without-x-server/
        // 1. Initialize EGL
        int displayIndex = settings.getInteger("display", -1);
        if (displayIndex < 0 || displayIndex >= numDevices.get(0)) {
            displayId = eglGetDisplay(EGL_DEFAULT_DISPLAY);
        } else {
            displayId = eglGetPlatformDisplayEXT(EGL_PLATFORM_DEVICE_EXT, pointer.get(displayIndex), (IntBuffer) null);
        }

        if (displayId == MemoryUtil.NULL) {
            CerberusRegistry.getInstance().critical("Failed to grab EGL window");
            return;
        }
        EGLError error = pullError();
        if (error != EGLError.SUCCESS) {
            CerberusRegistry.getInstance().critical("An error occurred while trying to load the EGL display: "
                    + error.name());
            return;
        }

        IntBuffer minor = BufferUtils.createIntBuffer(1);
        IntBuffer major = BufferUtils.createIntBuffer(1);
        eglInitialize(displayId, major, minor);

        CerberusRegistry.getInstance().info("Initiated EGL Display version " + major.get(0) + "." + minor.get(0));


        pBufferSize = new Vector2i(settings.getInteger("window_width", 9),
                settings.getInteger("window_height", 9));

        long config;
        long surface;
        // 2. Select an appropriate configuration
        if (settings.getBoolean("use_surface", true)) {

            IntBuffer configAttribs = BufferUtils.createIntBuffer(13);
            configAttribs.put(EGL_SURFACE_TYPE);
            configAttribs.put(EGL_PBUFFER_BIT);
            configAttribs.put(EGL_BLUE_SIZE);
            configAttribs.put(settings.getInteger("cc_blue", 8));
            configAttribs.put(EGL_GREEN_SIZE);
            configAttribs.put(settings.getInteger("cc_green", 8));
            configAttribs.put(EGL_RED_SIZE);
            configAttribs.put(settings.getInteger("cc_red", 8));
            configAttribs.put(EGL_DEPTH_SIZE);
            configAttribs.put(settings.getInteger("cc_depth", 8));
            configAttribs.put(EGL_RENDERABLE_TYPE);
            configAttribs.put(EGL_OPENGL_BIT);
            configAttribs.put(EGL_NONE);
            configAttribs.flip();


            PointerBuffer eglCfg = BufferUtils.createPointerBuffer(1);
            IntBuffer numConfigs = BufferUtils.createIntBuffer(1);

            eglChooseConfig(displayId, configAttribs, eglCfg, numConfigs);
            config = eglCfg.get(0);

            CerberusRegistry.getInstance().debug("Available configurations: " + numConfigs.get(0));

            if (config == MemoryUtil.NULL) {
                CerberusRegistry.getInstance().critical("Failed to grab display configuration");
                eglTerminate(displayId);
                return;
            }
            error = pullError();
            if (error != EGLError.SUCCESS) {
                CerberusRegistry.getInstance().critical("An error occurred while trying to fetch the EGL display" +
                        " configuration: " + error.name());
                eglTerminate(displayId);
                return;
            }

            // 3. Create a surface
            IntBuffer pbufferAttribs = BufferUtils.createIntBuffer(5);
            pbufferAttribs.put(EGL_WIDTH);
            pbufferAttribs.put(pBufferSize.getX());
            pbufferAttribs.put(EGL_HEIGHT);
            pbufferAttribs.put(pBufferSize.getY());
            pbufferAttribs.put(EGL_NONE);
            pbufferAttribs.flip();

            surface = eglCreatePbufferSurface(displayId, config, pbufferAttribs);
            if (surface == MemoryUtil.NULL) {
                CerberusRegistry.getInstance().critical("Unable to create display surface");
                eglTerminate(displayId);
            }
            error = pullError();
            if (error != EGLError.SUCCESS) {
                CerberusRegistry.getInstance().critical("An error occurred while trying to create a surface for the EGL" +
                        " display: " + error.name());
                eglTerminate(displayId);
                return;
            }
        } else {
            PointerBuffer eglCfg = BufferUtils.createPointerBuffer(1);
            IntBuffer numConfigs = BufferUtils.createIntBuffer(1);
            eglChooseConfig(displayId, null, eglCfg, numConfigs);

            config = eglCfg.get(0);
            CerberusRegistry.getInstance().debug("Available configurations: " + numConfigs.get(0));

            if (config == MemoryUtil.NULL) {
                CerberusRegistry.getInstance().critical("Failed to grab display configuration");
                eglTerminate(displayId);
                return;
            }
            surface = EGL_NO_SURFACE;
        }

        // 4. Bind the API
        eglBindAPI(EGL_OPENGL_API);

        error = pullError();
        if (error != EGLError.SUCCESS) {
            CerberusRegistry.getInstance().critical("An error occurred while trying to bind the OpenGL API: "
                    + error.name());
            eglTerminate(displayId);
            return;
        }

        // 5. Create a context and make it current
        long context = eglCreateContext(displayId, config, EGL_NO_CONTEXT, (IntBuffer) null);
        eglMakeCurrent(displayId, surface, surface, context);

        error = pullError();
        if (error != EGLError.SUCCESS) {
            CerberusRegistry.getInstance().critical("An error occurred while trying to make the egl context current: "
                    + error.name());
            eglTerminate(displayId);
            return;
        }

        // 6. Init OpenGL
        GL.createCapabilities();
        RenderUtil.initDefault();

        threadId = Thread.currentThread().getId();
        initiated = true;

        RenderUtil.printSystemInfo(currentTime);

        renderer = glGetString(GL_RENDERER);
        version = glGetString(GL_VERSION);
    }

    @Override
    public void update(double v) {
        long timeSwapStart = System.nanoTime();

        EGLError error = pullError();
        if (error != EGLError.SUCCESS) {
            CerberusRegistry.getInstance().getService(CerberusEvent.class)
                    .executeFullEIF(new ExceptionEvent(CerberusRenderer.class, new EGLException(error)));
        }

        timeBufferSwap = (int) (System.nanoTime() - timeSwapStart);
    }

    public EGLError pullError() {
        return EGLError.fromEGL(eglGetError());
    }

    @Override
    public void set() {
        TextureUtil.unbindRenderbuffer();
        TextureUtil.unbindFramebuffer();
        RenderUtil.setViewport(pBufferSize);
    }

    @Override
    public Vector2i getScreenSize() {
        return pBufferSize;
    }
}
