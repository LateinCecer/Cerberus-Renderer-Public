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

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL44.*;

public enum BufferFlag {

    GLSL_ONLY(0),
    MAP_READ(GL_MAP_READ_BIT),
    MAP_WRITE(GL_MAP_WRITE_BIT),
    DYNAMIC_STORAGE(GL_DYNAMIC_STORAGE_BIT),
    MAP_PERSISTANT(GL_MAP_PERSISTENT_BIT),
    MAP_COHERANT(GL_MAP_COHERENT_BIT),
    CLIENT_STORAGE(GL_CLIENT_STORAGE_BIT),
    MAP_FLUSH_EXPLICIT(GL_MAP_FLUSH_EXPLICIT_BIT);

    private final int gl;

    BufferFlag(int glCode) {
        this.gl = glCode;
    }

    public int glCode() {
        return gl;
    }
}
