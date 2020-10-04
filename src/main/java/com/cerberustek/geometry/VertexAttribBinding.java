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

import com.cerberustek.Destroyable;
import com.cerberustek.buffer.GlBufferObject;

/**
 * Contains access to a single vertex attribute.
 *
 * The bind function should not be called from outside of the
 * engine core in most cases.
 */
public interface VertexAttribBinding extends Destroyable {

    /**
     * Returns the binding index of the vertex attribute binding.
     * @return index of the attribute
     */
    int getBindingIndex();

    /**
     * Binds the data buffer to the buffer binding
     * @param bindingIndex the binding index of the buffer binding
     */
    void bindBuffer(int bindingIndex);

    /**
     * Will mark the buffer as unbound.
     *
     * This method will not really unbind the gl buffer, it will
     * rather mark the vertex attribute as unbound. If the
     * attribute binding is bound, while the buffer is deleted,
     * the buffer will be unbound.
     * Otherwise unbinding will not be necessary, because for a
     * new buffer to be bound, the old one does not have to be
     * unbound first.
     */
    void unbind();

    /**
     * Returns the relative offset of the buffer binding.
     *
     * The offset describes the offset between each vertex
     * element.
     * @return relative offset
     */
    int getRelativeOffset();

    /**
     * Returns the buffer object.
     * @return buffer object
     */
    GlBufferObject bufferObject();
}
