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

public enum DrawMode {

    LINES(GL_LINES),
    QUADS(GL_QUADS),
    TRIANGLES(GL_TRIANGLES);

    private final int gl_name;

    DrawMode(int gl_name) {
        this.gl_name = gl_name;
    }

    public int glMode() {
        return gl_name;
    }

    public static DrawMode valueOf(int glMode) {
        switch (glMode) {
            case GL_LINES:
                return LINES;
            case GL_QUADS:
                return QUADS;
            default:
                return TRIANGLES;
        }
    }
}
