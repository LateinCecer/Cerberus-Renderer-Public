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

package com.cerberustek.resource.shader.impl;

import com.cerberustek.resource.buffered.ByteBufferResource;
import com.cerberustek.geometry.BufferFlag;
import com.cerberustek.resource.shader.SSBOResource;
import com.cerberustek.shader.ssbo.ShaderStorageBufferObject;
import com.cerberustek.shader.ssbo.impl.ImmutableSSBO;

public class ImmutableSSBOResource implements SSBOResource {

    private final long size;
    private final ByteBufferResource buffer;
    private final BufferFlag[] flags;
    private final long[] cuts;
    private final int[] bindingIndices;

    public ImmutableSSBOResource(ByteBufferResource dataResource, long[] cuts, int[] bindingIndices, BufferFlag... flags) {
        this.size = -1;
        this.buffer = dataResource;
        this.flags = flags;
        this.cuts = cuts;
        this.bindingIndices = bindingIndices;
    }

    public ImmutableSSBOResource(long size, long[] cuts, int[] bindingIndices, BufferFlag... flags) {
        this.size = size;
        this.buffer = null;
        this.cuts = cuts;
        this.bindingIndices = bindingIndices;
        this.flags = flags;
    }

    @Override
    public ShaderStorageBufferObject load() {
        ImmutableSSBO ssbo = new ImmutableSSBO();
        ssbo.genBuffers();
        ssbo.bind();
        if (size > 0)
            ssbo.allocate(size, flags);
        else {
            if (buffer == null)
                throw new IllegalStateException("Either pre-buffered data, or the buffer object size have" +
                        " to be provided to initiate an immutable SSBO");

            ssbo.allocate(buffer.load(), flags);
        }

        ssbo.partition(cuts, bindingIndices);
        return ssbo;
    }
}
