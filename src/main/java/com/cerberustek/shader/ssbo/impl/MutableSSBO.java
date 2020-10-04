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

import com.cerberustek.buffer.BufferUsage;
import com.cerberustek.buffer.GlBufferObject;
import com.cerberustek.buffer.GlBufferTarget;
import com.cerberustek.buffer.impl.SimpleGlBufferObject;
import com.cerberustek.shader.ssbo.MutableShaderBlockContainer;
import com.cerberustek.shader.ssbo.ShaderBlock;
import com.cerberustek.shader.ssbo.ShaderStorageBufferObject;
import org.jetbrains.annotations.NotNull;

import java.nio.*;

import static org.lwjgl.opengl.GL15.*;

public class MutableSSBO implements ShaderStorageBufferObject, MutableShaderBlockContainer {

    private GlBufferObject pointer;
    private BufferUsage usage;
    private long byteSize;

    private ShaderBlock[] blocks;

    public MutableSSBO(@NotNull BufferUsage usage) {
        this.usage = usage;
    }

    @Override
    public void bufferData(ByteBuffer buffer, long[] cuts, int[] bindingIndices) {
        if (pointer == null)
            throw new IllegalStateException("Buffer has not been initialized");

        pointer.bufferData(buffer, usage);
        byteSize = buffer.capacity();
        partitionBuffer(cuts, bindingIndices);
    }

    @Override
    public void bufferData(ShortBuffer buffer, long[] cuts, int[] bindingIndices) {
        if (pointer == null)
            throw new IllegalStateException("Buffer has not been initialized");

        pointer.bufferData(buffer, usage);
        byteSize = buffer.capacity() * 2;
        partitionBuffer(cuts, bindingIndices);
    }

    @Override
    public void bufferData(IntBuffer buffer, long[] cuts, int[] bindingIndices) {
        if (pointer == null)
            throw new IllegalStateException("Buffer has not been initialized");

        pointer.bufferData(buffer, usage);
        byteSize = buffer.capacity() * 4;
        partitionBuffer(cuts, bindingIndices);
    }

    @Override
    public void bufferData(LongBuffer buffer, long[] cuts, int[] bindingIndices) {
        if (pointer == null)
            throw new IllegalStateException("Buffer has not been initialized");

        pointer.bufferData(buffer, usage);
        byteSize = buffer.capacity() * 8;
        partitionBuffer(cuts, bindingIndices);
    }

    @Override
    public void bufferData(FloatBuffer buffer, long[] cuts, int[] bindingIndices) {
        if (pointer == null)
            throw new IllegalStateException("Buffer has not been initialized");

        pointer.bufferData(buffer, usage);
        byteSize = buffer.capacity() * 4;
        partitionBuffer(cuts, bindingIndices);
    }

    @Override
    public void bufferData(DoubleBuffer buffer, long[] cuts, int[] bindingIndices) {
        if (pointer == null)
            throw new IllegalStateException("Buffer has not been initialized");

        pointer.bufferData(buffer, usage);
        byteSize = buffer.capacity() * 8;
        partitionBuffer(cuts, bindingIndices);
    }

    @Override
    public void bufferData(long bufferSize, long[] cuts, int[] bindingIndices) {
        if (pointer == null)
            throw new IllegalStateException("Buffer has not been initialized");

        pointer.bufferData(bufferSize, usage);
        this.byteSize = bufferSize;
        partitionBuffer(cuts, bindingIndices);
    }

    @SuppressWarnings("DuplicatedCode")
    private void partitionBuffer(long[] cuts, int[] bindingIndices) {
        if (cuts.length + 1 != bindingIndices.length)
            throw new IllegalArgumentException("There has to be exactly one more binding index than" +
                    " cuts for block partitioning");

        blocks = new ShaderBlock[cuts.length + 1];

        if (blocks.length == 1) {
            blocks[0] = new SharedShaderBlock(pointer, bindingIndices[0], 0, byteSize);
        } else {

            blocks[0] = new SharedShaderBlock(pointer, bindingIndices[0], 0, cuts[0]);
            for (int i = 0; i < cuts.length; i++)
                blocks[i + 1] = new SharedShaderBlock(pointer, bindingIndices[i + 1], cuts[i], i + 1 < cuts.length ? cuts[i + 1] : byteSize);
        }
    }

    @Override
    public void genBuffers() {
        if (pointer != null)
            throw new IllegalStateException("SSBO buffer does already exit!");
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
        if (index < blocks.length)
            return blocks[index];
        throw new IndexOutOfBoundsException("Shader block with index " + index + " does not exit!");
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

    public BufferUsage getUsage() {
        return usage;
    }

    public void setUsage(BufferUsage usage) {
        this.usage = usage;
    }
}
