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

import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

public class StaticIndexBinding implements IndexBuffer {

    private final GlBufferObject ibo;
    // index offset in bytes
    private final long indexOffset;
    // index size in elements
    private final int indexSize;
    private final ComponentType dataType;

    public StaticIndexBinding(GlBufferObject ibo, long off, int len, ComponentType dataType) {
        this.ibo = ibo;
        this.indexOffset = off;
        this.indexSize = len;
        this.dataType = dataType;
    }

    public StaticIndexBinding(int ibo, long off, int len, ComponentType dataType) {
        this(new SimpleGlBufferObject(GlBufferTarget.ELEMENT_ARRAY, ibo), off, len, dataType);
    }

    @Override
    public void addIndices(int[] indices) {
        if (indices.length != indexSize)
            throw new IndexOutOfBoundsException("The allocated index buffer size" +
                    " has to match the actual data buffer size");

        ibo.bind();
        if (indexOffset == 0)
            // ibo.bind();
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        else
            //ibo.bind(off(), dataType.sizeof() * (long) indexSize);
            glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, indexOffset, indices);
    }

    /**
     * Will draw the index buffer that is currently
     * bound.
     *
     * Make sure that the current vertex buffer
     * bound before using this method!
     * This method will not check if there is a
     * valid vertex buffer bound for performance
     * reasons. */
    @Override
    public void draw(DrawMode renderMode) {
        ibo.bind();
        glDrawElements(renderMode.glMode(), indexSize, dataType.getGlId(), indexOffset);
    }

    @Override
    public void drawInstanced(DrawMode renderMode, int count) {
        ibo.bind();
        glDrawElementsInstanced(renderMode.glMode(), indexSize, dataType.getGlId(), indexOffset, count);
    }

    /**
     * Will bind the index buffer.
     *
     * Take in mind, that this will not just bind the
     * partition of the index buffer that is allocated
     * by the static buffer object.
     * For performance reasons this method will bind
     * the entire index buffer.
     */
    @Override
    public void bind() {
        ibo.bind();
    }

    @Override
    public GlBufferObject bufferObject() {
        return ibo;
    }

    /**
     * Will unbind the index buffer.
     *
     * In most use cases doing this is highly discouraged,
     * because following draw calls will just rebind the
     * buffer and there is no need to unbind it first,
     * causing performance loss.
     */
    @Override
    public void unbind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    @Override
    public ComponentType getType() {
        return dataType;
    }

    @Override
    public int size() {
        return indexSize;
    }

    @Override
    public long byteSize() {
        return (long) indexSize * dataType.sizeof();
    }

    @Override
    public long off() {
        return indexOffset;
    }

    @Override
    public void destroy() {
        // This has nothing to do here. The IBO should
        // always be deleted by the same entity, that
        // allocated it.
    }
}
