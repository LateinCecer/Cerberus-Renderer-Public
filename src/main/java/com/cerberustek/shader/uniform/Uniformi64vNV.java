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
import com.cerberustek.shader.Shader;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.opengl.NVShaderBufferLoad.*;

public class Uniformi64vNV extends SimpleUniform<long[]> {

    public Uniformi64vNV(int id, @NotNull String name, long @NotNull [] data) {
        super(id, name, data);
    }

    public Uniformi64vNV(@NotNull Shader shader, @NotNull String name, long @NotNull [] data) throws GLUnknownUniformException {
        super(shader.genUniformId(name), name, data);
    }

    @Override
    public Uniform<long[]> update() {
        glUniformui64vNV(getId(), get());
        return this;
    }
}
