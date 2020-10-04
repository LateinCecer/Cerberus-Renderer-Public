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

import com.cerberustek.data.MetaConvertible;
import com.cerberustek.data.MetaLoadable;
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.uniform.Uniform;
import org.jetbrains.annotations.NotNull;

public interface ShaderProperty<T, D extends Uniform<T>> extends MetaConvertible, MetaLoadable {

    /**
     * This method will set the uniform variable of the
     * shader property for the specified shader.
     *
     * This will only return the updated uniform, if the
     * shader contains the uniform and if the uniforms value
     * in the shader currently is different from the
     * property value. Otherwise this method will return
     * null.
     * Keep in mind, that this method will not per say
     * update the uniform. It will only set it's value.
     *
     * @param shader shader
     * @return updated uniform
     */
    D inforce(@NotNull Shader shader);

    D extract(@NotNull Shader shader);

    void set(@NotNull T value);

    @NotNull T get();

    @NotNull String getName();

    @NotNull Class<D> getUniformType();
}
