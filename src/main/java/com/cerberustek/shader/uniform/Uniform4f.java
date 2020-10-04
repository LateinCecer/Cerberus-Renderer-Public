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
import com.cerberustek.logic.math.Quaternionf;
import com.cerberustek.logic.math.Vector4f;
import com.cerberustek.shader.Shader;

import static org.lwjgl.opengl.GL20.*;

public class Uniform4f extends SimpleUniform<Vector4f> {

    public Uniform4f(int id, String name, Quaternionf data) {
        super(id, name, new Vector4f(data.getX(), data.getY(), data.getZ(), data.getW()));
    }

    public Uniform4f(Shader shader, String name, Quaternionf data) throws GLUnknownUniformException {
        this(shader.genUniformId(name), name, data);
    }

    public Uniform4f(int id, String name, Vector4f data) {
        super(id, name, data);
    }

    public Uniform4f(Shader shader, String name, Vector4f data) throws GLUnknownUniformException {
        this(shader.genUniformId(name), name, data);
    }

    @Override
    public Uniform<Vector4f> update() {
        glUniform4f(getId(), get().getX(), get().getY(), get().getZ(), get().getW());
        return this;
    }

    public Uniform4f set(Quaternionf data) {
        this.get().set(data.getX(), data.getY(), data.getZ(), data.getW());
        return this;
    }
}
