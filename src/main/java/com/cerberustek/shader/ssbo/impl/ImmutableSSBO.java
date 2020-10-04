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

import com.cerberustek.geometry.BufferFlag;
import com.cerberustek.geometry.ComponentType;
import com.cerberustek.buffer.GlBufferObject;
import com.cerberustek.buffer.GlBufferTarget;
import com.cerberustek.buffer.impl.SimpleGlBufferObject;
import com.cerberustek.shader.ssbo.ImmutableShaderBlockContainer;
import com.cerberustek.shader.ssbo.ShaderBlock;
import com.cerberustek.shader.ssbo.ShaderStorageBufferObject;

import java.nio.*;

import static org.lwjgl.opengl.GL15.*;

public class ImmutableSSBO implements ShaderStorageBufferObject, ImmutableShaderBlockContainer {

    private GlBufferObject pointer;
    private long byteSize;
    private ShaderBlock[] blocks;

    @Override
    public void allocate(long byteSize) {
        this.byteSize = byteSize;
        pointer.bufferStorage(byteSize);
    }

    @Override
    public void allocate(long byteSize, BufferFlag... flags) {
        this.byteSize = byteSize;
        pointer.bufferStorage(byteSize, flags);
    }

    @Override
    public void allocate(int size, ComponentType componentType) {
        this.byteSize = size * componentType.sizeof();
        pointer.bufferStorage(this.byteSize);
    }

    @Override
    public void allocate(int size, ComponentType componentType, BufferFlag... flags) {
        this.byteSize = size * componentType.sizeof();
        pointer.bufferStorage(this.byteSize, flags);
    }

    @Override
    public void allocate(ByteBuffer buffer, BufferFlag... flags) {
        this.byteSize = buffer.capacity();
        pointer.bufferStorage(buffer, flags);
    }

    @Override
    public void allocate(ByteBuffer buffer) {
        this.byteSize = buffer.capacity();
        pointer.bufferStorage(buffer);
    }

    @Override
    public void allocate(ShortBuffer buffer, BufferFlag... flags) {
        this.byteSize = buffer.capacity() * 2;
        pointer.bufferStorage(buffer, flags);
    }

    @Override
    public void allocate(ShortBuffer buffer) {
        this.byteSize = buffer.capacity() * 2;
        pointer.bufferStorage(buffer);
    }

    @Override
    public void allocate(IntBuffer buffer, BufferFlag... flags) {
        this.byteSize = buffer.capacity() * 4;
        pointer.bufferStorage(buffer, flags);
    }

    @Override
    public void allocate(IntBuffer buffer) {
        this.byteSize = buffer.capacity() * 4;
        pointer.bufferStorage(buffer);
    }

    @Override
    public void allocate(FloatBuffer buffer, BufferFlag... flags) {
        this.byteSize = buffer.capacity() * 4;
        pointer.bufferStorage(buffer, flags);
    }

    @Override
    public void allocate(FloatBuffer buffer) {
        this.byteSize = buffer.capacity() * 4;
        pointer.bufferStorage(buffer);
    }

    @Override
    public void allocate(DoubleBuffer buffer, BufferFlag... flags) {
        this.byteSize = buffer.capacity() * 8;
        pointer.bufferStorage(buffer, flags);
    }

    @Override
    public void allocate(DoubleBuffer buffer) {
        this.byteSize = buffer.capacity() * 8;
        pointer.bufferStorage(buffer);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void partition(long[] cuts, int[] bindingIndices) {
        if (cuts.length + 1 != bindingIndices.length)
            throw new IllegalArgumentException("There has to be exactly one more binding indices" +
                    " than partitioning cuts!");

        blocks = new ShaderBlock[cuts.length + 1];
        if (cuts.length == 0) {
            blocks[0] = new SharedShaderBlock(pointer, bindingIndices[0], 0, byteSize);
        } else {

            blocks[0] = new SharedShaderBlock(pointer, bindingIndices[0], 0, cuts[0]);
            for (int i = 0; i < cuts.length; i++)
                blocks[i + 1] = new SharedShaderBlock(pointer, bindingIndices[i + 1], cuts[i],
                        i + 1 < cuts.length ? cuts[i + 1] : byteSize);
        }
    }

    @Override
    public void genBuffers() {
        if (pointer != null)
            throw new IllegalStateException("The buffer object already has been generated!");

        pointer = new SimpleGlBufferObject(GlBufferTarget.SHADER_STORAGE, glGenBuffers());
    }

    @Override
    public void bind(int index) {
        if (index < blocks.length)
            blocks[index].bind();
    }

    @Override
    public void bind(int index, int bindingIndex) {
        if (index < blocks.length)
            blocks[index].bind(bindingIndex);
    }

    @Override
    public void bind() {
        pointer.bind();
    }

    @Override
    public GlBufferObject getPointer() {
        return pointer;
    }

    @Override
    public ShaderBlock getBlock(int index) {
        return index < blocks.length ? blocks[index] : null;
    }

    @Override
    public boolean contains(ShaderBlock block) {
        for (ShaderBlock b : blocks) {
            if (b.equals(block))
                return true;
        }
        return false;
    }

    @Override
    public int size() {
        return blocks != null ? blocks.length : 0;
    }

    @Override
    public long byteSize() {
        return byteSize;
    }

    @Override
    public void destroy() {
        pointer.destroy();
    }
}
