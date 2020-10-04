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

package com.cerberustek.buffer.impl;

import com.cerberustek.geometry.BufferFlag;
import com.cerberustek.buffer.BufferAccess;
import com.cerberustek.buffer.BufferUsage;
import com.cerberustek.buffer.GlBufferObject;
import com.cerberustek.buffer.GlBufferTarget;

import java.nio.*;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL44.*;

public class SimpleGlBufferObject implements GlBufferObject {

    private final GlBufferTarget target;
    private final int pointer;

    public SimpleGlBufferObject(GlBufferTarget target, int pointer) {
        this.target = target;
        this.pointer = pointer;
    }

    /**
     * Will generate a new buffer object
     * @param target buffer target
     */
    public SimpleGlBufferObject(GlBufferTarget target) {
        this.target = target;
        this.pointer = glGenBuffers();
    }

    @Override
    public GlBufferTarget getTarget() {
        return target;
    }

    @Override
    public void bind() {
        glBindBuffer(target.getGlId(), pointer);
    }

    @Override
    public void bind(GlBufferTarget target) {
        glBindBuffer(target.getGlId(), pointer);
    }

    @Override
    public void bind(long offset, long size) {
        glBindBufferRange(target.getGlId(), 0, pointer, offset, size);
    }

    @Override
    public void bind(GlBufferTarget target, long offset, long size) {
        glBindBufferRange(target.getGlId(), 0, pointer, offset, size);
    }

    @Override
    public void bind(int bindingIndex, long offset, long size) {
        glBindBufferRange(target.getGlId(), bindingIndex, pointer, offset, size);
    }

    @Override
    public void bind(GlBufferTarget bufferTarget, int bindingIndex, long offset, long size) {
        glBindBufferRange(bufferTarget.getGlId(), bindingIndex, pointer, offset, size);
    }

    @Override
    public void bind(int bindingIndex) {
        glBindBufferBase(target.getGlId(), bindingIndex, pointer);
    }

    @Override
    public void bind(GlBufferTarget bufferTarget, int bindingIndex) {
        glBindBufferBase(bufferTarget.getGlId(), bindingIndex, pointer);
    }

    @Override
    public void bufferData(ByteBuffer buffer, BufferUsage usage) {
        glBufferData(target.getGlId(), buffer, usage.getGl());
    }

    @Override
    public void bufferData(ShortBuffer buffer, BufferUsage usage) {
        glBufferData(target.getGlId(), buffer, usage.getGl());
    }

    @Override
    public void bufferData(IntBuffer buffer, BufferUsage usage) {
        glBufferData(target.getGlId(), buffer, usage.getGl());
    }

    @Override
    public void bufferData(LongBuffer buffer, BufferUsage usage) {
        glBufferData(target.getGlId(), buffer, usage.getGl());
    }

    @Override
    public void bufferData(FloatBuffer buffer, BufferUsage usage) {
        glBufferData(target.getGlId(), buffer, usage.getGl());
    }

    @Override
    public void bufferData(DoubleBuffer buffer, BufferUsage usage) {
        glBufferData(target.getGlId(), buffer, usage.getGl());
    }

    @Override
    public void bufferData(long size, BufferUsage usage) {
        glBufferData(target.getGlId(), size, usage.getGl());
    }

    private int makeFlag(BufferFlag... flags) {
        if (flags.length == 0)
            return 0;

        int out = flags[0].glCode();
        for (int i = 1; i < flags.length; i++)
            out = out | flags[i].glCode();
        return out;
    }

    @Override
    public void bufferStorage(ByteBuffer buffer, BufferFlag... flag) {
        glBufferStorage(target.getGlId(), buffer, makeFlag(flag));
    }

    @Override
    public void bufferStorage(ShortBuffer buffer, BufferFlag... flag) {
        glBufferStorage(target.getGlId(), buffer, makeFlag(flag));
    }

    @Override
    public void bufferStorage(IntBuffer buffer, BufferFlag... flag) {
        glBufferStorage(target.getGlId(), buffer, makeFlag(flag));
    }

    @Override
    public void bufferStorage(FloatBuffer buffer, BufferFlag... flag) {
        glBufferStorage(target.getGlId(), buffer, makeFlag(flag));
    }

    @Override
    public void bufferStorage(DoubleBuffer buffer, BufferFlag... flag) {
        glBufferStorage(target.getGlId(), buffer, makeFlag(flag));
    }

    @Override
    public void bufferStorage(long size, BufferFlag... flag) {
        glBufferStorage(target.getGlId(), size, makeFlag(flag));
    }

    @Override
    public void bufferSubData(ByteBuffer buffer, long offset) {
        glBufferSubData(target.getGlId(), offset, buffer);
    }

    @Override
    public void bufferSubData(ShortBuffer buffer, long offset) {
        glBufferSubData(target.getGlId(), offset, buffer);
    }

    @Override
    public void bufferSubData(IntBuffer buffer, long offset) {
        glBufferSubData(target.getGlId(), offset, buffer);
    }

    @Override
    public void bufferSubData(LongBuffer buffer, long offset) {
        glBufferSubData(target.getGlId(), offset, buffer);
    }

    @Override
    public void bufferSubData(FloatBuffer buffer, long offset) {
        glBufferSubData(target.getGlId(), offset, buffer);
    }

    @Override
    public void bufferSubData(DoubleBuffer buffer, long offset) {
        glBufferSubData(target.getGlId(), offset, buffer);
    }

    @Override
    public void getBufferSubData(ByteBuffer buffer, long offset) {
        glGetBufferSubData(target.getGlId(), offset, buffer);
    }

    @Override
    public void getBufferSubData(ShortBuffer buffer, long offset) {
        glGetBufferSubData(target.getGlId(), offset, buffer);
    }

    @Override
    public void getBufferSubData(IntBuffer buffer, long offset) {
        glBufferSubData(target.getGlId(), offset, buffer);
    }

    @Override
    public void getBufferSubData(LongBuffer buffer, long offset) {
        glBufferSubData(target.getGlId(), offset, buffer);
    }

    @Override
    public void getBufferSubData(FloatBuffer buffer, long offset) {
        glBufferSubData(target.getGlId(), offset, buffer);
    }

    @Override
    public void getBufferSubData(DoubleBuffer buffer, long offset) {
        glBufferSubData(target.getGlId(), offset, buffer);
    }

    private int makeAccessBit(BufferAccess access, BufferFlag... flags) {
        if (flags.length == 0)
            return access.glCode();

        int bit = access.glCode();
        for (BufferFlag flag : flags)
            bit = bit | flag.glCode();

        return bit;
    }

    @Override
    public ByteBuffer mapBuffer(BufferAccess access, BufferFlag... flags) {
        return glMapBuffer(target.getGlId(), makeAccessBit(access, flags));
    }

    @Override
    public ByteBuffer mapBuffer(BufferAccess access, ByteBuffer old_buffer, BufferFlag... flags) {
        return glMapBuffer(target.getGlId(), makeAccessBit(access, flags), old_buffer);
    }

    @Override
    public ByteBuffer mapBuffer(BufferAccess access, long byteSize, ByteBuffer old_buffer, BufferFlag... flags) {
        return glMapBuffer(target.getGlId(), makeAccessBit(access, flags), byteSize, old_buffer);
    }

    @Override
    public ByteBuffer mapBufferRange(BufferAccess access, long off, long len, BufferFlag... flags) {
        return glMapBufferRange(target.getGlId(), off, len, makeAccessBit(access, flags));
    }

    @Override
    public ByteBuffer mapBufferRange(BufferAccess access, long off, long len, ByteBuffer old_buffer, BufferFlag... flags) {
        return glMapBufferRange(target.getGlId(), off, len, makeAccessBit(access, flags), old_buffer);
    }

    @Override
    public void flushMappedBufferRange(long off, long len) {
        glFlushMappedBufferRange(target.getGlId(), off, len);
    }

    @Override
    public void unmapBuffer() {
        glUnmapBuffer(target.getGlId());
    }

    @Override
    public void unbind() {
        glBindBuffer(target.getGlId(), 0);
    }

    @Override
    public int getPointer() {
        return pointer;
    }

    @Override
    public GlBufferObject clone(GlBufferTarget coneTarget) {
        return new SimpleGlBufferObject(coneTarget, pointer);
    }

    @Override
    public void destroy() {
        glDeleteBuffers(pointer);
    }
}
