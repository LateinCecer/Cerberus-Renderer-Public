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

package com.cerberustek.resource.impl;

import com.cerberustek.resource.buffered.BufferedResource;
import com.cerberustek.resource.model.VertexAttribResource;
import com.cerberustek.geometry.VertexAttribBinding;
import com.cerberustek.geometry.impl.verticies.StaticVertexAttribBuffer;

import java.nio.*;

public class StaticVertexAttribResource<T extends Buffer> implements VertexAttribResource {

    private final BufferedResource<T> buffer;
    private final int offset;
    private final int size;
    private final int stride;

    public StaticVertexAttribResource(BufferedResource<T> buffer, int offset, int size, int stride) {
        this.buffer = buffer;
        this.offset = offset;
        this.size = size;
        this.stride = stride;
    }

    @Override
    public VertexAttribBinding load() {
        StaticVertexAttribBuffer out = new StaticVertexAttribBuffer();
        out.genBuffers();

        T buf = buffer.load();
        if (buf instanceof ByteBuffer)
            out.load((ByteBuffer) buf, offset, size, stride);
        else if (buf instanceof ShortBuffer)
            out.load((ShortBuffer) buf, offset, size, stride);
        else if (buf instanceof IntBuffer)
            out.load((IntBuffer) buf, offset, size, stride);
        else if (buf instanceof FloatBuffer)
            out.load((FloatBuffer) buf, offset, size, stride);
        else if (buf instanceof LongBuffer)
            out.load((LongBuffer) buf, offset, size, stride);
        else if (buf instanceof DoubleBuffer)
            out.load((DoubleBuffer) buf, offset, size, stride);

        return out;
    }
}
