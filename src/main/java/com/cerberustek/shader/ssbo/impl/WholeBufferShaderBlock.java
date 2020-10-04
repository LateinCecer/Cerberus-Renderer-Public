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
import com.cerberustek.buffer.GlBufferTarget;
import com.cerberustek.shader.ssbo.ShaderBlock;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL43.*;

public class WholeBufferShaderBlock implements ShaderBlock {

    private final GlBufferObject pointer;
    private final int binding;
    private final long size;

    public WholeBufferShaderBlock(GlBufferObject pointer, int binding) {
        this.pointer = pointer;
        this.binding = binding;

        pointer.bind();
        size = glGetBufferParameteri64(pointer.getTarget().getGlId(), GL_BUFFER_SIZE);
    }

    @Override
    public int getBindingIndex() {
        return binding;
    }

    @Override
    public long byteOffset() {
        return 0;
    }

    @Override
    public long byteLength() {
        return size;
    }

    @Override
    public void bind() {
        //pointer.unbind();
        pointer.bind(GlBufferTarget.SHADER_STORAGE, binding);
    }

    @Override
    public void bind(int bindingIndex) {
        //pointer.unbind();
        pointer.bind(GlBufferTarget.SHADER_STORAGE, bindingIndex);
    }

    @Override
    public void upload(ByteBuffer data) {
        if (data.capacity() > size)
            throw new IndexOutOfBoundsException();

        pointer.bind();
        pointer.bufferSubData(data, 0);
    }

    @Override
    public void upload(ByteBuffer data, long destOff) {
        if (data.capacity() + destOff > size)
            throw new IndexOutOfBoundsException();

        pointer.bind();
        pointer.bufferSubData(data, destOff);
    }

    @Override
    public ByteBuffer download() {
        ByteBuffer buffer = BufferUtils.createByteBuffer((int) size);

        pointer.bind();
        pointer.getBufferSubData(buffer, 0);
        return buffer;
    }

    @Override
    public ByteBuffer download(long srcOff, int length) {
        if (srcOff + length > size)
            throw new IndexOutOfBoundsException();
        ByteBuffer buffer = BufferUtils.createByteBuffer(length);

        pointer.bind();
        pointer.getBufferSubData(buffer, srcOff);
        return buffer;
    }

    @Override
    public void download(ByteBuffer buffer, long srcOff) {
        if (srcOff + buffer.capacity() > size)
            throw new IndexOutOfBoundsException();

        pointer.bind();
        pointer.getBufferSubData(buffer, srcOff);
    }
}
