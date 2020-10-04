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
 * Partition of an Index Buffer.
 */
public interface IndexBuffer extends Destroyable, Drawable {

    /**
     * Will upload index data to the GPU
     * @param indices index buffer
     * @throws IndexOutOfBoundsException Exception thrown, when
     *          the <code>indices</code> buffer size does not
     *          match the buffer size allocated for this index
     *          buffer
     */
    void addIndices(int[] indices);

    /**
     * Will bind the index buffer
     *
     * ################# GL30 #################
     * use glBindBufferRange(GL_ELEMENT_ARRAY_BUFFER, 0, bufferId, off * sizeof, len * sizeof);
     */
    void bind();

    /**
     * Returns the buffer object of the index
     * buffer.
     *
     * This buffer object usually belongs to
     * a index buffer array object and should
     * not be deleted from here.
     * @return gl buffer object
     */
    GlBufferObject bufferObject();

    /**
     * Returns data type
     * @return data type
     */
    ComponentType getType();

    /**
     * Returns the size of the index buffer in elements
     * @return buffer size
     */
    int size();

    /**
     * Returns the size of the index buffer in bytes
     * @return buffer byte size
     */
    long byteSize();

    /**
     * Returns the draw offset on the index buffer
     * @return offset
     */
    long off();
}
