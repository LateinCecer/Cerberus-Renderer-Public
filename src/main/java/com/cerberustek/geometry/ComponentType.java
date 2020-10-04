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

package com.cerberustek.geometry;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public enum ComponentType {

    BYTE(GL_BYTE),
    INT(GL_INT),
    SHORT(GL_SHORT),
    UNSIGNED_BYTE(GL_UNSIGNED_BYTE),
    UNSIGNED_INT(GL_UNSIGNED_INT),
    UNSIGNED_SHORT(GL_UNSIGNED_SHORT),
    FLOAT(GL_FLOAT),
    HALF_FLOAT(GL_HALF_FLOAT),
    DOUBLE(GL_DOUBLE);

    private final int glId;

    ComponentType(int gl) {
        this.glId = gl;
    }

    public int getGlId() {
        return glId;
    }

    /**
     * Returns the size of the data type in bytes
     * @return type size in bytes
     */
    public int sizeof() {
        switch (this) {
            case INT:
            case UNSIGNED_INT:
            case FLOAT:
                return 4;
            case HALF_FLOAT:
            case SHORT:
            case UNSIGNED_SHORT:
                return 2;
            case BYTE:
            case UNSIGNED_BYTE:
                return 1;
            case DOUBLE:
                return 8;
            default:
                return 0;
        }
    }

    public static ComponentType valueOf(int glType) {
        switch (glType) {
            case GL_BYTE:
                return BYTE;
            case GL_INT:
                return INT;
            case GL_SHORT:
                return SHORT;
            case GL_UNSIGNED_BYTE:
                return UNSIGNED_BYTE;
            case GL_UNSIGNED_SHORT:
                return UNSIGNED_SHORT;
            case GL_UNSIGNED_INT:
                return UNSIGNED_INT;
            case GL_FLOAT:
                return FLOAT;
            case GL_DOUBLE:
                return DOUBLE;
            default:
                return null;
        }
    }
}
