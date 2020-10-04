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
import static org.lwjgl.opengl.GL44.*;

public enum GlBufferTarget {

    ELEMENT_ARRAY(GL_ELEMENT_ARRAY_BUFFER),
    ARRAY(GL_ARRAY_BUFFER),
    SHADER_STORAGE(GL_SHADER_STORAGE_BUFFER),
    DISPATCH_INDIRECT(GL_DISPATCH_INDIRECT_BUFFER),
    UNIFORM(GL_UNIFORM_BUFFER),
    ATOMIC_COUNTER(GL_ATOMIC_COUNTER_BUFFER),
    TRANSFORM_FEEDBACK(GL_TRANSFORM_FEEDBACK_BUFFER);

    private final int glId;

    GlBufferTarget(int glId) {
        this.glId = glId;
    }

    /**
     * Returns the open gl id of the
     * buffer target.
     * @return gl id
     */
    public int getGlId() {
        return glId;
    }
}
