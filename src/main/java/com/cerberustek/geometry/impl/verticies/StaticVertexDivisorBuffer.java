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

package com.cerberustek.geometry.impl.verticies;

import com.cerberustek.buffer.BufferUsage;
import com.cerberustek.buffer.GlBufferObject;
import com.cerberustek.buffer.GlBufferTarget;
import com.cerberustek.buffer.impl.SimpleGlBufferObject;
import com.cerberustek.geometry.VertexDivisorBinding;

import java.nio.*;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL43.*;

public class StaticVertexDivisorBuffer implements VertexDivisorBinding {

    private GlBufferObject pointer;

    private int offset;
    private int size;
    private int relativeOffset;
    private int divisor;

    private int bindingIndex;

    public StaticVertexDivisorBuffer() {
        this.bindingIndex = -1;
        this.pointer = null;
    }

    public void genBuffers() {
        pointer = new SimpleGlBufferObject(GlBufferTarget.ARRAY, glGenBuffers());
    }

    public void load(ByteBuffer data, int offset, int size, int relativeOffset, int divisor) {
//        glBindBuffer(GL_ARRAY_BUFFER, pointer);
//        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        pointer.bind();
        pointer.bufferData(data, BufferUsage.STATIC_DRAW);

        this.offset = offset;
        this.size = size;
        this.relativeOffset = relativeOffset;
        this.divisor = divisor;
    }

    public void load(FloatBuffer data, int offset, int size, int relativeOffset, int divisor) {
//        glBindBuffer(GL_ARRAY_BUFFER, pointer);
//        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        pointer.bind();
        pointer.bufferData(data, BufferUsage.STATIC_DRAW);

        this.offset = offset;
        this.size = size;
        this.relativeOffset = relativeOffset;
        this.divisor = divisor;
    }

    public void load(IntBuffer data, int offset, int size, int relativeOffset, int divisor) {
//        glBindBuffer(GL_ARRAY_BUFFER, pointer);
//        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        pointer.bind();
        pointer.bufferData(data, BufferUsage.STATIC_DRAW);

        this.offset = offset;
        this.size = size;
        this.relativeOffset = relativeOffset;
        this.divisor = divisor;
    }

    public void load(DoubleBuffer data, int offset, int size, int relativeOffset, int divisor) {
//        glBindBuffer(GL_ARRAY_BUFFER, pointer);
//        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        pointer.bind();
        pointer.bufferData(data, BufferUsage.STATIC_DRAW);

        this.offset = offset;
        this.size = size;
        this.relativeOffset = relativeOffset;
        this.divisor = divisor;
    }

    public void load(LongBuffer data, int offset, int size, int relativeOffset, int divisor) {
//        glBindBuffer(GL_ARRAY_BUFFER, pointer);
//        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        pointer.bind();
        pointer.bufferData(data, BufferUsage.STATIC_DRAW);

        this.offset = offset;
        this.size = size;
        this.relativeOffset = relativeOffset;
        this.divisor = divisor;
    }

    public void load(ShortBuffer data, int offset, int size, int relativeOffset, int divisor) {
//        glBindBuffer(GL_ARRAY_BUFFER, pointer);
//        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        pointer.bind();
        pointer.bufferData(data, BufferUsage.STATIC_DRAW);

        this.offset = offset;
        this.size = size;
        this.relativeOffset = relativeOffset;
        this.divisor = divisor;
    }

    @Override
    public int getBindingIndex() {
        return bindingIndex;
    }

    @Override
    public void bindBuffer(int bindingIndex) {
        if (this.bindingIndex == bindingIndex)
            return; // already bound
        glBindVertexBuffer(bindingIndex, pointer.getPointer(), offset, size);
        glVertexBindingDivisor(bindingIndex, divisor);
    }

    @Override
    public void unbind() {
        this.bindingIndex = -1;
    }

    @Override
    public int getRelativeOffset() {
        return relativeOffset;
    }

    @Override
    public GlBufferObject bufferObject() {
        return pointer;
    }

    @Override
    public void destroy() {
        if (bindingIndex >= 0) {
            glVertexBindingDivisor(bindingIndex, 0);
            glBindVertexBuffer(bindingIndex, 0, 0, 0);
        }
        pointer.destroy();
    }

    @Override
    public int getDivisor() {
        return divisor;
    }

    @Override
    public void setDivisor(int divisor) {
        this.divisor = divisor;
    }
}
