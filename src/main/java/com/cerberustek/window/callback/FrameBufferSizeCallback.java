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

package com.cerberustek.window.callback;

import com.cerberustek.CerberusEvent;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.events.FrameBufferSizeEvent;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.window.Window;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;

public class FrameBufferSizeCallback extends GLFWFramebufferSizeCallback implements CerberusCallback {

    @Override
    public void init(Window window) {
        set(window.id());
    }

    @Override
    public void invoke(long window, int width, int height) {
        CerberusRegistry.getInstance().getService(CerberusEvent.class).executeFullEIF(
                new FrameBufferSizeEvent(window, new Vector2i(width, height)));
    }
}
