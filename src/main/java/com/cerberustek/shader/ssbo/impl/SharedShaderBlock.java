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

package com.cerberustek.shader.ssbo.impl;

import com.cerberustek.buffer.GlBufferObject;
import com.cerberustek.shader.ssbo.ShaderBlock;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

/**
 * Shader block for both UBO's and SSBO's, depending on the buffer object in
 * use.
 */
public class SharedShaderBlock implements ShaderBlock {

    private final GlBufferObject bufferObject;
    private final int bindingIndex;
    private final long offset;
    private final long size;

    public SharedShaderBlock(GlBufferObject bufferObject, int bindingIndex, long offset, long size) {
        this.bufferObject = bufferObject;
        this.bindingIndex = bindingIndex;
        this.offset = offset;
        this.size = size;
    }

    @Override
    public int getBindingIndex() {
        return bindingIndex;
    }

    @Override
    public long byteOffset() {
        return offset;
    }

    @Override
    public long byteLength() {
        return size;
    }

    @Override
    public void bind() {
        bufferObject.bind(bindingIndex, offset, size);
    }

    @Override
    public void bind(int bindingIndex) {
        bufferObject.bind(bindingIndex, offset, size);
    }

    @Override
    public void upload(ByteBuffer data) {
        if (data.capacity() > size)
            throw new IndexOutOfBoundsException();

        bufferObject.bind();
        bufferObject.bufferSubData(data, offset);
    }

    @Override
    public void upload(ByteBuffer data, long destOff) {
        if (data.capacity() + destOff > size)
            throw new IndexOutOfBoundsException();

        bufferObject.bind();
        bufferObject.bufferSubData(data, offset + destOff);
    }

    @Override
    public ByteBuffer download() {
        ByteBuffer buffer = BufferUtils.createByteBuffer((int) size);

        bufferObject.bind();
        bufferObject.getBufferSubData(buffer, offset);
        return buffer;
    }

    @Override
    public ByteBuffer download(long srcOff, int length) {
        if (srcOff + length > size)
            throw new IndexOutOfBoundsException();
        ByteBuffer buffer = BufferUtils.createByteBuffer(length);

        bufferObject.bind();
        bufferObject.getBufferSubData(buffer, offset + srcOff);
        return buffer;
    }

    @Override
    public void download(ByteBuffer buffer, long srcOff) {
        if (srcOff + buffer.capacity() > size)
            throw new IndexOutOfBoundsException();

        bufferObject.bind();
        bufferObject.getBufferSubData(buffer, offset + srcOff);
    }
}
