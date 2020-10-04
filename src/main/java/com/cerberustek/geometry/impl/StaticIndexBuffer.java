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

package com.cerberustek.geometry.impl;

import com.cerberustek.buffer.GlBufferObject;
import com.cerberustek.buffer.GlBufferTarget;
import com.cerberustek.buffer.impl.SimpleGlBufferObject;
import com.cerberustek.geometry.ComponentType;
import com.cerberustek.geometry.DrawMode;
import com.cerberustek.geometry.IndexBuffer;

import static org.lwjgl.opengl.GL15.*;

public class StaticIndexBuffer implements IndexBuffer {

    private GlBufferObject pointer;
    private StaticIndexBinding binding;

    public void genBuffers() {
        pointer = new SimpleGlBufferObject(GlBufferTarget.ELEMENT_ARRAY, glGenBuffers());
    }

    @Override
    public void addIndices(int[] indices) {
        binding = new StaticIndexBinding(pointer, 0, indices.length, ComponentType.UNSIGNED_INT);
        binding.addIndices(indices);
    }

    @Override
    public void draw(DrawMode renderMode) {
        if (binding != null)
            binding.draw(renderMode);
    }

    @Override
    public void drawInstanced(DrawMode renderMode, int count) {
        if (binding != null)
            binding.drawInstanced(renderMode, count);
    }

    @Override
    public void bind() {
        if (binding != null)
            binding.bind();
    }

    @Override
    public void unbind() {
        if (binding != null)
            binding.unbind();
    }

    @Override
    public GlBufferObject bufferObject() {
        return pointer;
    }

    @Override
    public ComponentType getType() {
        return binding == null ? null : binding.getType();
    }

    @Override
    public int size() {
        return binding == null ? 0 : binding.size();
    }

    @Override
    public long byteSize() {
        return binding == null ? 0 : binding.byteSize();
    }

    @Override
    public long off() {
        return binding == null ? 0 : binding.off();
    }

    @Override
    public void destroy() {
        if (binding != null)
            binding.destroy();
        pointer.destroy();
    }
}
