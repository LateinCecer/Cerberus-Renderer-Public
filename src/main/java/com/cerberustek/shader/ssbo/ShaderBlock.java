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

import java.nio.ByteBuffer;

public interface ShaderBlock {

    /**
     * Returns the binding index of the Shader Storage
     * Block.
     * @return binding index
     */
    int getBindingIndex();

    /**
     * Byte offset inside the buffer object.
     * @return byte offset
     */
    long byteOffset();

    /**
     * Byte length inside the buffer object.
     * @return byte length
     */
    long byteLength();

    /**
     * Will bind the buffer data to the appropriate block
     * binding.
     */
    void bind();

    /**
     * Will bind the buffer data to the specified block
     * binding.
     * @param bindingIndex binding index to bind to
     */
    void bind(int bindingIndex);

    /**
     * Will upload the data buffer to the GPU. The data will
     * be saved to the shader block's share of the buffer.
     *
     * If the specified data buffers capacity does not match
     * the shader blocks byte length, this method will throw
     * an array out of bounds exception.
     *
     * @param data byte buffer
     */
    void upload(ByteBuffer data);

    /**
     * Will upload the data buffer to the GPU. The data will
     * be saved to the shader block's share of the buffer.
     *
     * If the specified destOff parameter does not
     * match the shader blocks byte length, this method will
     * throw an array out of bounds exception.
     * The destination offset is specified relative to the
     * start of this buffer block.
     *
     * @param data byte buffer
     * @param destOff destination offset in the shader buffer
     */
    void upload(ByteBuffer data, long destOff);

    /**
     * Will download the shader blocks contents as a byte buffer.
     * @return download the shader blocks data from the GPU
     */
    ByteBuffer download();

    /**
     * Will download the shader blocks contents as a byte buffer.
     *
     * If the specified destOff parameter does not
     * match the shader blocks byte length, this method will
     * throw an array out of bounds exception.
     * The destination offset is specified relative to the
     * start of this buffer block.
     *
     * @param srcOff source offset
     * @param length length
     * @return buffered data
     */
    ByteBuffer download(long srcOff, int length);

    /**
     * Will download the shader blocks contents as a byte buffer.
     *
     * The srcOff offset parameter is specified relative to the
     * start of the shader block. If the buffer capacity is
     * greater than the byte buffer or the shader block, this
     * method will throw an index out of bounds exception.
     *
     * @param buffer byte buffer
     * @param srcOff source offset
     */
    void download(ByteBuffer buffer, long srcOff);
}

