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
import com.cerberustek.geometry.impl.verticies.StaticVertexDivisorBuffer;

import java.nio.*;

public class StaticDivisorAttribResource<T extends Buffer> implements VertexAttribResource {

    private final BufferedResource<T> bufferResource;
    private final int offset;
    private final int size;
    private final int relOffset;
    private final int divisor;

    public StaticDivisorAttribResource(BufferedResource<T> bufferedResource, int offset, int size,
                                       int relOffset, int divisor) {
        this.bufferResource = bufferedResource;
        this.offset = offset;
        this.size = size;
        this.relOffset = relOffset;
        this.divisor = divisor;
    }

    @Override
    public VertexAttribBinding load() {
        StaticVertexDivisorBuffer out = new StaticVertexDivisorBuffer();
        out.genBuffers();

        T buffer = bufferResource.load();
        if (buffer instanceof ByteBuffer)
            out.load((ByteBuffer) buffer, offset, size, relOffset, divisor);
        else if (buffer instanceof ShortBuffer)
            out.load((ShortBuffer) buffer, offset, size, relOffset, divisor);
        else if (buffer instanceof IntBuffer)
            out.load((IntBuffer) buffer, offset, size, relOffset, divisor);
        else if (buffer instanceof FloatBuffer)
            out.load((FloatBuffer) buffer, offset, size, relOffset, divisor);
        else if (buffer instanceof LongBuffer)
            out.load((LongBuffer) buffer, offset, size, relOffset, divisor);
        else if (buffer instanceof DoubleBuffer)
            out.load((DoubleBuffer) buffer, offset, size, relOffset, divisor);

        return out;
    }
}
