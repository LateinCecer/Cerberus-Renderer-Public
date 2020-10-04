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

import com.cerberustek.exceptions.IllegalStructSizeException;
import com.cerberustek.logic.math.Vector2f;
import com.cerberustek.shader.GLSLType;
import com.cerberustek.shader.ssbo.MemoryLayout;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class GLSLDynamicVec2fArray extends DynamicStructArray<Vector2f> {

    private long byteSize;
    private int padding;

    public GLSLDynamicVec2fArray() {}

    public GLSLDynamicVec2fArray(Vector2f[] data) {
        super(data);
    }

    @Override
    public GLSLType getType() {
        return GLSLType.FLOAT_VEC2;
    }

    @Override
    public void bake(MemoryLayout layout, long off) throws IllegalStructSizeException {
        this.offset = off;

        if (layout == MemoryLayout.STD140) {
            this.byteSize = size() * 16;
            padding = 8;
        } else {
            this.byteSize = size() * 8;
            padding = 0;
        }
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void packageData(ByteBuffer byteBuffer) {
        byte[] padding = new byte[this.padding];
        Arrays.fill(padding, (byte) 0);

        for (Vector2f v : data) {
            byteBuffer.putFloat(v.getX());
            byteBuffer.putFloat(v.getY());
            byteBuffer.put(padding);
        }
    }

    @Override
    public long byteSize() {
        return byteSize;
    }
}
