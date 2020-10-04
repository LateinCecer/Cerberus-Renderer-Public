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
import com.cerberustek.geometry.VertexAttribBinding;

import java.nio.*;
import java.util.Objects;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL43.*;

public class StaticVertexAttribBuffer implements VertexAttribBinding {

    private GlBufferObject pointer;

    private int offset;
    private int size;
    private int stride;

    private int bindingIndex;

    public StaticVertexAttribBuffer() {
        pointer = null;
        bindingIndex = -1;
    }

    public void genBuffers() {
        pointer = new SimpleGlBufferObject(GlBufferTarget.ARRAY, glGenBuffers());
    }

    public void load(ByteBuffer buffer, int offset, int size, int stride) {
//        glBindBuffer(GL_ARRAY_BUFFER, pointer);
//        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        pointer.bind();
        pointer.bufferData(buffer, BufferUsage.STATIC_DRAW);

        this.offset = offset;
        this.size = size;
        this.stride = stride;
    }

    public void load(ShortBuffer buffer, int offset, int size, int stride) {
//        glBindBuffer(GL_ARRAY_BUFFER, pointer);
//        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        pointer.bind();
        pointer.bufferData(buffer, BufferUsage.STATIC_DRAW);

        this.offset = offset;
        this.size = size;
        this.stride = stride;
    }

    public void load(IntBuffer buffer, int offset, int size, int stride) {
//        glBindBuffer(GL_ARRAY_BUFFER, pointer);
//        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        pointer.bind();
        pointer.bufferData(buffer, BufferUsage.STATIC_DRAW);

        this.offset = offset;
        this.size = size;
        this.stride = stride;
    }

    public void load(FloatBuffer buffer, int offset, int size, int stride) {
//        glBindBuffer(GL_ARRAY_BUFFER, pointer);
//        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        pointer.bind();
        pointer.bufferData(buffer, BufferUsage.STATIC_DRAW);

        this.offset = offset;
        this.size = size;
        this.stride = stride;
    }

    public void load(LongBuffer buffer, int offset, int size, int stride) {
//        glBindBuffer(GL_ARRAY_BUFFER, pointer);
//        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        pointer.bind();
        pointer.bufferData(buffer, BufferUsage.STATIC_DRAW);

        this.offset = offset;
        this.size = size;
        this.stride = stride;
    }

    public void load(DoubleBuffer buffer, int offset, int size, int stride) {
//        glBindBuffer(GL_ARRAY_BUFFER, pointer);
//        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        pointer.bind();
        pointer.bufferData(buffer, BufferUsage.STATIC_DRAW);

        this.offset = offset;
        this.size = size;
        this.stride = stride;
    }

    @Override
    public int getBindingIndex() {
        return bindingIndex;
    }

    @Override
    public void bindBuffer(int bindingIndex) {
        if (this.bindingIndex == bindingIndex)
            return;
        glBindVertexBuffer(bindingIndex, pointer.getPointer(), offset, size);
    }

    @Override
    public void unbind() {
        this.bindingIndex = -1;
    }

    @Override
    public int getRelativeOffset() {
        return stride;
    }

    @Override
    public GlBufferObject bufferObject() {
        return pointer;
    }

    @Override
    public void destroy() {
        if (bindingIndex >= 0)
            glBindVertexBuffer(bindingIndex, 0, 0, 0);

        pointer.destroy();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StaticVertexAttribBuffer that = (StaticVertexAttribBuffer) o;
        return pointer == that.pointer &&
                offset == that.offset &&
                size == that.size &&
                stride == that.stride;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pointer, offset, size, stride);
    }
}
