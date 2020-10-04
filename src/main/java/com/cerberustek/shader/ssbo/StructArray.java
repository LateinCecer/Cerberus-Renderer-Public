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

package com.cerberustek.shader.ssbo;

import com.cerberustek.shader.GLSLType;

public interface StructArray<T> extends StructEntry, Iterable<T> {

    /**
     * Returns the type of the array.
     * @return the array's type
     */
    GLSLType getType();

    /**
     * Returns the element at the specified index.
     * @param index index
     * @return element
     */
    T get(int index);

    /**
     * Sets the element at the specified index location.
     * @param index index
     * @param value new element value
     */
    void set(int index, T value);

    /**
     * Returns the size of the struct array in elements.
     * @return size in elements
     */
    int size();
}
