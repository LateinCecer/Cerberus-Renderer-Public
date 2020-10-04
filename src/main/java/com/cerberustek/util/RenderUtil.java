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

package com.cerberustek.util;

import com.cerberustek.CerberusRegistry;
import com.cerberustek.geometry.ComponentType;
import com.cerberustek.geometry.DataType;
import com.cerberustek.logic.math.Vector2i;
import org.lwjgl.BufferUtils;

import java.nio.LongBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL43.GL_MAX_SHADER_STORAGE_BLOCK_SIZE;
import static org.lwjgl.opengl.GL43.GL_MAX_VERTEX_ATTRIB_BINDINGS;

public class RenderUtil {

    public static void setViewport(Vector2i size) {
        glViewport(0, 0, size.getX(), size.getY());
    }

    public static void initDefault() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glFrontFace(GL_CW);
        glCullFace(GL_BACK);
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_TEXTURE_3D);
        glEnable(GL_NEAREST);

        // Gamma correction is handled by the bloom shader
        // for more control than the default sRGB OpenGL
        // framebuffers
        // glEnable(GL_FRAMEBUFFER_SRGB);
    }

    public static void printSystemInfo(long startTime) {
        LongBuffer buf = BufferUtils.createLongBuffer(1);
        glGetInteger64v(GL_MAX_SHADER_STORAGE_BLOCK_SIZE, buf);
        CerberusRegistry.getInstance().info("... created GL-Capabilities in " +
                (System.currentTimeMillis() - startTime) + "ms!");
        CerberusRegistry.getInstance().info("System Information: \n" +
                "\t# Renderer: " + glGetString(GL_RENDERER) + "\n" +
                "\t# Open-GL version: " + glGetString(GL_VERSION) + "\n" +
                "\t# Operating System: " + HardwareMonitor.getInstance().getOperatingSystemName() + "\n" +
                "\t# Version: " + HardwareMonitor.getInstance().getOperatingSystemVersion() + "\n" +
                "\t# Architecture: " + HardwareMonitor.getInstance().getArchitecture() + "\n" +
                "\t# Max Color Attachments: " + glGetInteger(GL_MAX_COLOR_ATTACHMENTS) + "\n" +
                "\t# Max Vertex Attribute Bindings: " + glGetInteger(GL_MAX_VERTEX_ATTRIB_BINDINGS) + "\n" +
                "\t# Max Vertex Attributes: " + glGetInteger(GL_MAX_VERTEX_ATTRIBS) + "\n" +
                "\t# Max SSBO block size: " + (buf.get(0) / (long) (1024 * 1024)) + "MB");
    }

    public static String getRenderer() {
        return glGetString(GL_RENDERER);
    }

    public static String getVersion() {
        return glGetString(GL_VERSION);
    }

    public static void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public static void getFormat(int glTypeId, ComponentType[] component, DataType[] data) {
        switch (glTypeId) {
            case GL_FLOAT:
                component[0] = ComponentType.FLOAT;
                data[0] = DataType.SCALAR;
                return;
            case GL_FLOAT_VEC2:
                component[0] = ComponentType.FLOAT;
                data[0] = DataType.VEC2;
                return;
            case GL_FLOAT_VEC3:
                component[0] = ComponentType.FLOAT;
                data[0] = DataType.VEC3;
                return;
            case GL_FLOAT_VEC4:
                component[0] = ComponentType.FLOAT;
                data[0] = DataType.VEC4;
                return;
            case GL_FLOAT_MAT2:
                component[0] = ComponentType.FLOAT;
                data[0] = DataType.MAT2;
                return;
            case GL_FLOAT_MAT3:
                component[0] = ComponentType.FLOAT;
                data[0] = DataType.MAT3;
                return;
            case GL_FLOAT_MAT4:
                component[0] = ComponentType.FLOAT;
                data[0] = DataType.MAT4;
                return;
            case GL_FLOAT_MAT2x3:
            case GL_FLOAT_MAT2x4:
            case GL_FLOAT_MAT3x2:
            case GL_FLOAT_MAT3x4:
            case GL_FLOAT_MAT4x2:
            case GL_FLOAT_MAT4x3:
                component[0] = ComponentType.FLOAT;
                data[0] = null;
                return;
            case GL_INT:
                component[0] = ComponentType.INT;
                data[0] = DataType.SCALAR;
                return;
            case GL_INT_VEC2:
                component[0] = ComponentType.INT;
                data[0] = DataType.VEC2;
                return;
            case GL_INT_VEC3:
                component[0] = ComponentType.INT;
                data[0] = DataType.VEC3;
                return;
            case GL_INT_VEC4:
                component[0] = ComponentType.INT;
                data[0] = DataType.VEC4;
                return;
            case GL_UNSIGNED_INT:
                component[0] = ComponentType.UNSIGNED_INT;
                data[0] = DataType.SCALAR;
                return;
            case GL_UNSIGNED_INT_VEC2:
                component[0] = ComponentType.UNSIGNED_INT;
                data[0] = DataType.VEC2;
                return;
            case GL_UNSIGNED_INT_VEC3:
                component[0] = ComponentType.UNSIGNED_INT;
                data[0] = DataType.VEC3;
                return;
            case GL_UNSIGNED_INT_VEC4:
                component[0] = ComponentType.UNSIGNED_INT;
                data[0] = DataType.VEC4;
                return;
            case GL_DOUBLE:
                component[0] = ComponentType.DOUBLE;
                data[0] = DataType.SCALAR;
                return;
            case GL_DOUBLE_VEC2:
                component[0] = ComponentType.DOUBLE;
                data[0] = DataType.VEC2;
                return;
            case GL_DOUBLE_VEC3:
                component[0] = ComponentType.DOUBLE;
                data[0] = DataType.VEC3;
                return;
            case GL_DOUBLE_VEC4:
                component[0] = ComponentType.DOUBLE;
                data[0] = DataType.VEC4;
                return;
            case GL_DOUBLE_MAT2:
                component[0] = ComponentType.DOUBLE;
                data[0] = DataType.MAT2;
                return;
            case GL_DOUBLE_MAT3:
                component[0] = ComponentType.DOUBLE;
                data[0] = DataType.MAT3;
                return;
            case GL_DOUBLE_MAT4:
                component[0] = ComponentType.DOUBLE;
                data[0] = DataType.MAT4;
                return;
            case GL_DOUBLE_MAT2x3:
            case GL_DOUBLE_MAT2x4:
            case GL_DOUBLE_MAT3x2:
            case GL_DOUBLE_MAT3x4:
            case GL_DOUBLE_MAT4x2:
            case GL_DOUBLE_MAT4x3:
                component[0] = ComponentType.DOUBLE;
                data[0] = null;
                return;
            default:
                component[0] = null;
                data[0] = null;
        }
    }
}
