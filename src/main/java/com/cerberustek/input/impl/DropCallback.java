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

package com.cerberustek.input.impl;

import com.cerberustek.window.Window;
import com.cerberustek.input.InputDevice;
import org.lwjgl.glfw.GLFWDropCallback;

import java.io.File;

import static org.lwjgl.glfw.GLFW.*;

public class DropCallback extends GLFWDropCallback implements InputDevice {

    private Window window;
    private File[] files;

    @Override
    public void init(Window window) {
        this.window = window;
        set(window.id());
    }

    @Override
    public void invoke(long window, int count, long names) {
        if (window == this.window.id() && count >= 0) {
            files = new File[count];
            for (int i = 0; i < count; i++)
                files[i] = new File(getName(names, i));
        }
    }

    @Override
    public void destroy() {
        free();
    }

    public File[] getFiles() {
        return files;
    }

    public String getClipboard() {
        return glfwGetClipboardString(window.id());
    }

    public void setClipboard(String text) {
        glfwSetClipboardString(window.id(), text);
    }
}
