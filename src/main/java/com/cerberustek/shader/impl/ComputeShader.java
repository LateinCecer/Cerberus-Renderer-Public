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

package com.cerberustek.shader.impl;

import com.cerberustek.logic.math.Vector3i;
import com.cerberustek.shader.Shader;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.glGetProgramiv;
import static org.lwjgl.opengl.GL43.GL_COMPUTE_WORK_GROUP_SIZE;

public class ComputeShader extends Shader {

    /** local compute work group size */
    private Vector3i localGroupSize;

    @Override
    public boolean compile() {
        if (!super.compile())
            return false;

        // retrieve relevant data
        IntBuffer buffer = BufferUtils.createIntBuffer(3);
        glGetProgramiv(getProgramId(), GL_COMPUTE_WORK_GROUP_SIZE, buffer);
        localGroupSize = new Vector3i(buffer.get(0), buffer.get(1), buffer.get(2));

        return true;
    }

    /**
     * Will return the local work group size of this compute
     * shader instance.
     *
     * This value can only be retrieved from the shader, once
     * the shader code was compiled successfully.
     * @return local compute work group size
     */
    public Vector3i getLocalGroupSize() {
        return localGroupSize;
    }
}
