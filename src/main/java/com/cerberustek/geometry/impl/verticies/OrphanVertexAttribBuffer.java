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

import java.nio.ByteBuffer;

public class OrphanVertexAttribBuffer implements VertexAttribBinding {

    private StaticVertexAttribBuffer one;
    private StaticVertexAttribBuffer two;

    private boolean bufferSwitch = false;

    public void genBuffers() {
        if (one != null || two != null)
            throw new IllegalStateException("The buffer pointers have already been generated");

        one = new StaticVertexAttribBuffer();
        two = new StaticVertexAttribBuffer();

        one.genBuffers();
        two.genBuffers();
    }

    /**
     * Will update the buffer content.
     *
     * This method should be called after the buffer has been drawn
     * for the current frame in order to insure good render performance.
     *
     * @param buffer new data
     * @param offset new offset
     * @param size new buffer size
     * @param relativeOffset new relative size
     */
    public void load(ByteBuffer buffer, int offset, int size, int relativeOffset) {
        (bufferSwitch ? two : one).load(buffer, offset, size, relativeOffset);
        switchBuffers();
    }

    @Override
    public int getBindingIndex() {
        return buffer().getBindingIndex();
    }

    @Override
    public void bindBuffer(int bindingIndex) {
        buffer().bindBuffer(bindingIndex);
    }

    @Override
    public void unbind() {
        buffer().unbind();
    }

    @Override
    public int getRelativeOffset() {
        return buffer().getRelativeOffset();
    }

    @Override
    public GlBufferObject bufferObject() {
        return (bufferSwitch ? two : one).bufferObject();
    }

    @Override
    public void destroy() {
        if (one != null)
            one.destroy();
        if (two != null)
            two.destroy();
    }

    private StaticVertexAttribBuffer buffer() {
        return bufferSwitch ? one : two;
    }

    @SuppressWarnings("DuplicatedCode")
    private void switchBuffers() {
        if (bufferSwitch) {
            if (one.getBindingIndex() > 0) {
                two.bindBuffer(one.getBindingIndex());
                one.unbind();
            }
            bufferSwitch = false;
        } else {
            if (two.getBindingIndex() > 0) {
                one.bindBuffer(two.getBindingIndex());
                two.unbind();
            }
            bufferSwitch = true;
        }
    }
}
