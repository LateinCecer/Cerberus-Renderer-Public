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

package com.cerberustek.shader.ssbo.impl.struct;

import com.cerberustek.shader.GLSLType;
import com.cerberustek.shader.ssbo.MemoryLayout;
import com.cerberustek.shader.ssbo.StructElement;

import java.nio.ByteBuffer;

public class GLSLUnsignedByte implements StructElement<Integer> {

    private byte value;
    private long offset;

    public GLSLUnsignedByte(int value) {
        this.value = (byte) value;
    }

    @Override
    public Integer get() {
        return (int) value;
    }

    @Override
    public void set(Integer value) {
        this.value = (byte) ((int) value);
    }

    @Override
    public GLSLType getType() {
        return GLSLType.UNSIGNED_BYTE;
    }

    @Override
    public void bake(MemoryLayout layout, long off) {
        this.offset = off;
    }

    @Override
    public void packageData(ByteBuffer byteBuffer) {
        byteBuffer.put(value);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 0);
    }

    @Override
    public long byteSize() {
        return 4;
    }

    @Override
    public long byteOffset() {
        return offset;
    }
}
