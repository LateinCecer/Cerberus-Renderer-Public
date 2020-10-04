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
import com.cerberustek.logic.math.Vector3d;
import com.cerberustek.shader.GLSLType;
import com.cerberustek.shader.ssbo.MemoryLayout;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class GLSLStaticVec3dArray extends StaticStructArray<Vector3d> {

    private long byteSize;
    private int padding;

    public GLSLStaticVec3dArray(int size) {
        super(new Vector3d[size]);
    }

    public GLSLStaticVec3dArray(Vector3d[] data) {
        super(data);
    }

    @Override
    public GLSLType getType() {
        return GLSLType.DOUBLE_VEC3;
    }

    @Override
    public void bake(MemoryLayout layout, long off) throws IllegalStructSizeException {
        this.offset = off;

        if (layout == MemoryLayout.STD140) {
            byteSize = size() * 32;
            padding = 8;
        } else {
            byteSize = size() * 24;
            padding = 0;
        }
    }

    @Override
    public void packageData(ByteBuffer byteBuffer) {
        byte[] padding = new byte[this.padding];
        Arrays.fill(padding, (byte) 0);

        for (Vector3d v : data) {
            byteBuffer.putDouble(v.getX());
            byteBuffer.putDouble(v.getY());
            byteBuffer.putDouble(v.getZ());
            byteBuffer.put(padding);
        }
    }

    @Override
    public long byteSize() {
        return byteSize;
    }
}
