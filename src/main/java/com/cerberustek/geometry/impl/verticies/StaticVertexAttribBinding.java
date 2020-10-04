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

import com.cerberustek.buffer.GlBufferObject;
import com.cerberustek.geometry.VertexAttribBinding;

import static org.lwjgl.opengl.GL43.*;

public class StaticVertexAttribBinding implements VertexAttribBinding {

    private final GlBufferObject pointer;
    private final int offset;
    private final int size;
    private final int stride;

    private int bindingIndex;

    public StaticVertexAttribBinding(GlBufferObject pointer, int offset, int size, int stride) {
        this.pointer = pointer;
        this.offset = offset;
        this.size = size;
        this.stride = stride;

        this.bindingIndex = -1;
    }

    @Override
    public int getBindingIndex() {
        return bindingIndex;
    }

    @Override
    public void bindBuffer(int bindingIndex) {
        if (this.bindingIndex == bindingIndex)
            return; // buffer is already bound
        this.bindingIndex = bindingIndex;
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
        // The buffer should be deleted outside of this attribute,
        // because it could be used for other attributes
    }
}
