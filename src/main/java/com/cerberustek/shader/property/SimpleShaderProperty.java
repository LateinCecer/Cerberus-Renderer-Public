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

package com.cerberustek.shader.property;

import com.cerberustek.shader.Shader;
import com.cerberustek.shader.uniform.Uniform;
import org.jetbrains.annotations.NotNull;

public abstract class SimpleShaderProperty<T, D extends Uniform<T>> implements ShaderProperty<T, D> {

    private final String name;

    private T value;

    protected SimpleShaderProperty(@NotNull String name) {
        this.name = name;
    }

    protected SimpleShaderProperty(@NotNull String name, @NotNull T value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public D inforce(@NotNull Shader shader) {
        D uniform = shader.getUniform(name, getUniformType());
        if (uniform == null)
            return null;

        if (!get().equals(uniform.get())) {
            uniform.set(value);
            return uniform;
        }
        return null;
    }

    @Override
    public D extract(@NotNull Shader shader) {
        return shader.getUniform(name, getUniformType());
    }

    @Override
    public void set(@NotNull T value) {
        this.value = value;
    }

    @Override
    @NotNull
    public T get() {
        return value;
    }

    @Override
    @NotNull
    public String getName() {
        return name;
    }
}
