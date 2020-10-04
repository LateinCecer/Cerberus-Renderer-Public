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

import com.cerberustek.shader.uniform.Uniform;

import java.util.Collection;

public interface ShaderPropertyMap {

    <T, D extends Uniform<T>> ShaderProperty<T, D> getProperty(String name, Class<T> valueType);

    <T, D extends Uniform<T>> ShaderProperty<T, D> getProperty(String name);

    <T, D extends Uniform<T>> void setProperty(ShaderProperty<T, D> property);

    <T, D extends Uniform<T>> void removeProperty(ShaderProperty<T, D> property);

    void removeProperty(String name);

    int length();

    @SuppressWarnings("rawtypes")
    Collection<ShaderProperty> properties();

    Collection<String> keys();
}
