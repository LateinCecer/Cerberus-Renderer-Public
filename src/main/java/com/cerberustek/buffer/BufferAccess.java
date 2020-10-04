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

package com.cerberustek.buffer;

import static org.lwjgl.opengl.GL15.*;

public enum BufferAccess {

    READ_ONLY(GL_READ_ONLY),
    WRITE_ONLY(GL_WRITE_ONLY),
    READ_WRITE(GL_READ_WRITE);

    private final int gl;

    BufferAccess(int gl) {
        this.gl = gl;
    }

    public int glCode() {
        return gl;
    }

    public static BufferAccess valueOf(int glCode) {
        switch (glCode) {
            case GL_READ_ONLY:
                return READ_ONLY;
            case GL_WRITE_ONLY:
                return WRITE_ONLY;
            case GL_READ_WRITE:
                return READ_WRITE;
            default:
                return null;
        }
    }
}
