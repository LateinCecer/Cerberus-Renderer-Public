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

import com.cerberustek.logic.math.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class Monitor {

    private final long monitorId;

    public Monitor(long monitorId) {
        this.monitorId = monitorId;
    }

    public GLFWVidMode getVideoMode() {
        return glfwGetVideoMode(monitorId);
    }

    public Vector2i getSize() {
        GLFWVidMode vidMode = getVideoMode();
        return new Vector2i(vidMode.width(), vidMode.height());
    }

    public int getRefreshRate() {
        return getVideoMode().refreshRate();
    }

    public int getBlueBits() {
        return getVideoMode().blueBits();
    }

    public int getGreenBits() {
        return getVideoMode().greenBits();
    }

    public int getRedBits() {
        return getVideoMode().redBits();
    }

    public long getMonitorId() {
        return monitorId;
    }

    public String getName() {
        return glfwGetMonitorName(monitorId);
    }

    public Vector2i getPhysicalSize() {
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        glfwGetMonitorPhysicalSize(monitorId, width, height);
        Vector2i out = new Vector2i(width.get(0), height.get(0));
        MemoryUtil.memFree(width);
        MemoryUtil.memFree(height);
        return out;
    }

    public static Monitor getPrimary() {
        return new Monitor(glfwGetPrimaryMonitor());
    }

    public static Monitor[] getMonitors() {
        PointerBuffer pointers = glfwGetMonitors();
        if (pointers != null) {
            Monitor[] out = new Monitor[pointers.capacity()];
            for (int i = 0; i < out.length; i++)
                out[i] = new Monitor(pointers.get(i));
            return out;
        }
        return new Monitor[0];
    }
}
