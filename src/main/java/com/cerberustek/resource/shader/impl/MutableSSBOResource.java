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
import com.cerberustek.buffer.BufferUsage;
import com.cerberustek.resource.shader.SSBOResource;
import com.cerberustek.shader.ssbo.ShaderStorageBufferObject;
import com.cerberustek.shader.ssbo.impl.MutableSSBO;

public class MutableSSBOResource implements SSBOResource {

    private final ByteBufferResource initialBuffer;
    private final BufferUsage usage;
    private final long[] cuts;
    private final int[] bindingIndices;
    private final long bufferSize; // buffer size in bytes

    public MutableSSBOResource(ByteBufferResource initialData, BufferUsage usage, long[] cuts, int[] bindingIndices) {
        this.initialBuffer = initialData;
        this.bufferSize = -1;
        this.usage = usage;
        this.cuts = cuts;
        this.bindingIndices = bindingIndices;
    }

    public MutableSSBOResource(long bufferSize, BufferUsage usage, long[] cuts, int[] bindingIndices) {
        this.initialBuffer = null;
        this.bufferSize = bufferSize;
        this.usage = usage;
        this.cuts = cuts;
        this.bindingIndices = bindingIndices;
    }

    @Override
    public ShaderStorageBufferObject load() {
        MutableSSBO ssbo = new MutableSSBO(usage);
        ssbo.genBuffers();
        ssbo.bind();

        if (initialBuffer != null)
            ssbo.bufferData(initialBuffer.load(), cuts, bindingIndices);
        else
            ssbo.bufferData(bufferSize, cuts, bindingIndices);
        return ssbo;
    }
}
