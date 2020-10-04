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

package com.cerberustek.window;

import com.cerberustek.Destroyable;
import com.cerberustek.Initable;
import com.cerberustek.resource.impl.BufferedImageResource;
import com.cerberustek.settings.Settings;
import com.cerberustek.texture.RenderTarget;
import com.cerberustek.window.callback.CerberusCallback;
import com.cerberustek.Updatable;
import com.cerberustek.logic.math.Vector2i;

public interface Window extends Updatable, Destroyable, Initable, RenderTarget {

    void loadCallbacks(Settings settings);
    void addCallbackWithoutUpdate(CerberusCallback callback);
    void addCallback(CerberusCallback callback);
    void removeCallback(CerberusCallback callback);

    void requestClose();
    boolean isCloseRequested();

    void setFullscreenWithoutUpdate(boolean fullscreen);
    void setFullScreen(boolean fullScreen);
    boolean isFullScreen();

    void enableVsyncWithoutUpdate(boolean value);
    void enableVsync(boolean value);
    boolean vsync();

    void setResizableWithoutUpdate(boolean value);
    void setResizable(boolean value);
    boolean isResizable();

    void setVisibleWithoutUpdate(boolean value);
    void setVisible(boolean value);
    boolean isVisible();

    void setTitleWithoutUpdate(String title);
    void setTitle(String title);
    String getTitle();

    void setSize(Vector2i size);
    Vector2i getSize();

    void setPosition(Vector2i position);
    Vector2i getPosition();

    void setMonitor(Monitor monitor);
    Monitor getMonitor();

    void setWindowIcon(BufferedImageResource icon16, BufferedImageResource icon32);

    boolean isGlThread();
    boolean isGlThread(Thread thread);

    boolean isInitialized();

    /**
     * Returns true, if the window instance is headless.
     * @return is headless
     */
    boolean isHeadless();

    void clear();

    long id();
    long thread();

    /**
     * Time the last buffer swap and event pull
     * took in milli seconds.
     *
     * @return time taken for buffer swap
     */
    double timeBufferSwap();

    /**
     * Returns the name of the renderer used
     * @return name of the renderer
     */
    String getRenderer();

    /**
     * Returns the used version of OpenGL
     * @return OpenGL version
     */
    String getGLVersion();
}
