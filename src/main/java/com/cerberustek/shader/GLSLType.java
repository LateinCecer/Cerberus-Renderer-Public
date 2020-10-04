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

package com.cerberustek.shader;

import com.cerberustek.geometry.ComponentType;
import com.cerberustek.geometry.DataType;

import static org.lwjgl.opengl.GL11.GL_DOUBLE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL21.GL_FLOAT_MAT4x3;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL40.GL_DOUBLE_MAT4x3;

public enum GLSLType {

    FLOAT(GL_FLOAT, ComponentType.FLOAT, DataType.SCALAR),
    FLOAT_VEC2(GL_FLOAT_VEC2, ComponentType.FLOAT, DataType.VEC2),
    FLOAT_VEC3(GL_FLOAT_VEC3, ComponentType.FLOAT, DataType.VEC3),
    FLOAT_VEC4(GL_FLOAT_VEC4, ComponentType.FLOAT, DataType.VEC4),
    FLOAT_MAT2(GL_FLOAT_MAT2, ComponentType.FLOAT, DataType.MAT2),
    FLOAT_MAT3(GL_FLOAT_MAT3, ComponentType.FLOAT, DataType.MAT3),
    FLOAT_MAT4(GL_FLOAT_MAT4, ComponentType.FLOAT, DataType.MAT4),
    FLOAT_MAT2x3(GL_FLOAT_MAT2x3, ComponentType.FLOAT, DataType.MAT2x3),
    FLOAT_MAT2x4(GL_FLOAT_MAT2x4, ComponentType.FLOAT, DataType.MAT2x4),
    FLOAT_MAT3x2(GL_FLOAT_MAT3x2, ComponentType.FLOAT, DataType.MAT3x2),
    FLOAT_MAT3x4(GL_FLOAT_MAT3x4, ComponentType.FLOAT, DataType.MAT3x4),
    FLOAT_MAT4x2(GL_FLOAT_MAT4x2, ComponentType.FLOAT, DataType.MAT4x2),
    FLOAT_MAT4x3(GL_FLOAT_MAT4x3, ComponentType.FLOAT, DataType.MAT4x3),
    INT(GL_INT, ComponentType.INT, DataType.SCALAR),
    INT_VEC2(GL_INT_VEC2, ComponentType.INT, DataType.VEC2),
    INT_VEC3(GL_INT_VEC3, ComponentType.INT, DataType.VEC3),
    INT_VEC4(GL_INT_VEC4, ComponentType.INT, DataType.VEC4),
    UNSIGNED_INT(GL_UNSIGNED_INT, ComponentType.UNSIGNED_INT, DataType.SCALAR),
    UNSIGNED_INT_VEC2(GL_UNSIGNED_INT_VEC2, ComponentType.UNSIGNED_INT, DataType.VEC2),
    UNSIGNED_INT_VEC3(GL_UNSIGNED_INT_VEC3, ComponentType.UNSIGNED_INT, DataType.VEC3),
    UNSIGNED_INT_VEC4(GL_UNSIGNED_INT_VEC4, ComponentType.UNSIGNED_INT, DataType.VEC4),
    DOUBLE(GL_DOUBLE, ComponentType.DOUBLE, DataType.SCALAR),
    DOUBLE_VEC2(GL_DOUBLE_VEC2, ComponentType.DOUBLE, DataType.VEC2),
    DOUBLE_VEC3(GL_DOUBLE_VEC3, ComponentType.DOUBLE, DataType.VEC3),
    DOUBLE_VEC4(GL_DOUBLE_VEC4, ComponentType.DOUBLE, DataType.VEC4),
    DOUBLE_MAT2(GL_DOUBLE_MAT2, ComponentType.DOUBLE, DataType.MAT2),
    DOUBLE_MAT3(GL_DOUBLE_MAT3, ComponentType.DOUBLE, DataType.MAT3),
    DOUBLE_MAT4(GL_DOUBLE_MAT4, ComponentType.DOUBLE, DataType.MAT4),
    DOUBLE_MAT2x3(GL_DOUBLE_MAT2x3, ComponentType.DOUBLE, DataType.MAT2x3),
    DOUBLE_MAT2x4(GL_DOUBLE_MAT2x4, ComponentType.DOUBLE, DataType.MAT2x4),
    DOUBLE_MAT3x2(GL_DOUBLE_MAT3x2, ComponentType.DOUBLE, DataType.MAT3x2),
    DOUBLE_MAT3x4(GL_DOUBLE_MAT3x4, ComponentType.DOUBLE, DataType.MAT3x4),
    DOUBLE_MAT4x2(GL_DOUBLE_MAT4x2, ComponentType.DOUBLE, DataType.MAT4x2),
    DOUBLE_MAT4x3(GL_DOUBLE_MAT4x3, ComponentType.DOUBLE, DataType.MAT4x3),

    BYTE(GL_BYTE, ComponentType.BYTE, DataType.SCALAR),
    UNSIGNED_BYTE(GL_UNSIGNED_BYTE, ComponentType.UNSIGNED_BYTE, DataType.SCALAR),
    SHORT(GL_SHORT, ComponentType.SHORT, DataType.SCALAR),
    UNSIGNED_SHORT(GL_UNSIGNED_SHORT, ComponentType.UNSIGNED_SHORT, DataType.SCALAR);

    private final DataType dataType;
    private final ComponentType componentType;
    private final int glId;

    GLSLType(int id, ComponentType componentType, DataType dataType) {
        this.glId = id;
        this.componentType = componentType;
        this.dataType = dataType;
    }

    public DataType getDataType() {
        return dataType;
    }

    public ComponentType getComponentType() {
        return componentType;
    }

    public int getGlId() {
        return glId;
    }

    public static GLSLType valueOf(int glId) {
        switch (glId) {
            case GL_FLOAT:
                return FLOAT;
            case GL_FLOAT_VEC2:
                return FLOAT_VEC2;
            case GL_FLOAT_VEC3:
                return FLOAT_VEC3;
            case GL_FLOAT_VEC4:
                return FLOAT_VEC4;
            case GL_FLOAT_MAT2:
                return FLOAT_MAT2;
            case GL_FLOAT_MAT3:
                return FLOAT_MAT3;
            case GL_FLOAT_MAT4:
                return FLOAT_MAT4;
            case GL_FLOAT_MAT2x3:
                return FLOAT_MAT2x3;
            case GL_FLOAT_MAT2x4:
                return FLOAT_MAT2x4;
            case GL_FLOAT_MAT3x2:
                return FLOAT_MAT3x2;
            case GL_FLOAT_MAT3x4:
                return FLOAT_MAT3x4;
            case GL_FLOAT_MAT4x2:
                return FLOAT_MAT4x2;
            case GL_FLOAT_MAT4x3:
                return FLOAT_MAT4x3;
            case GL_INT:
                return INT;
            case GL_INT_VEC2:
                return INT_VEC2;
            case GL_INT_VEC3:
                return INT_VEC3;
            case GL_INT_VEC4:
                return INT_VEC4;
            case GL_UNSIGNED_INT:
                return UNSIGNED_INT;
            case GL_UNSIGNED_INT_VEC2:
                return UNSIGNED_INT_VEC2;
            case GL_UNSIGNED_INT_VEC3:
                return UNSIGNED_INT_VEC3;
            case GL_UNSIGNED_INT_VEC4:
                return UNSIGNED_INT_VEC4;
            case GL_DOUBLE:
                return DOUBLE;
            case GL_DOUBLE_VEC2:
                return DOUBLE_VEC2;
            case GL_DOUBLE_VEC3:
                return DOUBLE_VEC3;
            case GL_DOUBLE_VEC4:
                return DOUBLE_VEC4;
            case GL_DOUBLE_MAT2:
                return DOUBLE_MAT2;
            case GL_DOUBLE_MAT3:
                return DOUBLE_MAT3;
            case GL_DOUBLE_MAT4:
                return DOUBLE_MAT4;
            case GL_DOUBLE_MAT2x3:
                return DOUBLE_MAT2x3;
            case GL_DOUBLE_MAT2x4:
                return DOUBLE_MAT2x4;
            case GL_DOUBLE_MAT3x2:
                return DOUBLE_MAT3x2;
            case GL_DOUBLE_MAT3x4:
                return DOUBLE_MAT3x4;
            case GL_DOUBLE_MAT4x2:
                return DOUBLE_MAT4x2;
            case GL_DOUBLE_MAT4x3:
                return DOUBLE_MAT4x3;
            case GL_BYTE:
                return BYTE;
            case GL_UNSIGNED_BYTE:
                return UNSIGNED_BYTE;
            case GL_SHORT:
                return SHORT;
            case GL_UNSIGNED_SHORT:
                return UNSIGNED_SHORT;
            default:
                return null;
        }
    }
}
