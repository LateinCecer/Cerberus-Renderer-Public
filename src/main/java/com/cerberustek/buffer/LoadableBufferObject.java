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

package com.cerberustek.buffer;

import com.cerberustek.geometry.ComponentType;
import com.cerberustek.resource.buffered.BufferedResource;

import java.nio.Buffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

public abstract class LoadableBufferObject<T extends Buffer> implements GlBufferObject {

    private int pointer;
    private GlBufferTarget target;
    private BufferUsage usage;
    private ComponentType type;

    public LoadableBufferObject(GlBufferTarget target, BufferUsage usage) {
        this.target = target;
        this.usage = usage;
    }

    public void load(BufferedResource<T> resource) {
        this.type = resource.getBufferType();
    }

    public void genBuffers() {
        if (pointer != 0)
            throw new IllegalStateException("The buffer pointer has already been initialized!");
        pointer = glGenBuffers();
    }

    @Override
    public GlBufferTarget getTarget() {
        return target;
    }

    public BufferUsage getUsage() {
        return usage;
    }

    public ComponentType getType() {
        return type;
    }

    @Override
    public void bind() {
        glBindBuffer(target.getGlId(), pointer);
    }

    @Override
    public void bind(long offset, long size) {
        glBindBufferRange(target.getGlId(), 0, pointer, offset, size);
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
    public void destroy() {
        glDeleteBuffers(pointer);
    }
}
