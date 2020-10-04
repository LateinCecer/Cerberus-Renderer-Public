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

package com.cerberustek.shader.uniform;

import com.cerberustek.exceptions.GLUnknownUniformException;
import com.cerberustek.logic.math.Matrix4f;
import com.cerberustek.shader.Shader;
import com.cerberustek.util.BufferUtil;

import static org.lwjgl.opengl.GL20.*;

public class UniformMatrix4f extends SimpleUniform<Matrix4f> {

    public UniformMatrix4f(int id, String name, Matrix4f data) {
        super(id, name, data);
    }

    public UniformMatrix4f(Shader shader, String name, Matrix4f data) throws GLUnknownUniformException {
        this(shader.genUniformId(name), name, data);
    }

    @Override
    public Uniform<Matrix4f> update() {
        glUniformMatrix4fv(getId(), true, BufferUtil.createFlippedBuffer(get()));
        return this;
    }
}
