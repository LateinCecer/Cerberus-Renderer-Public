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
import com.cerberustek.logic.math.Vector3f;
import com.cerberustek.shader.GLSLType;
import com.cerberustek.shader.ssbo.MemoryLayout;
import com.cerberustek.shader.ssbo.StructElement;

import java.nio.ByteBuffer;

public class GLSLVec3f implements StructElement<Vector3f> {

    private Vector3f data;
    private long offset;

    public GLSLVec3f(Vector3f data) {
        this.data = data;
    }

    @Override
    public Vector3f get() {
        return data;
    }

    @Override
    public void set(Vector3f value) {
        this.data = value;
    }

    @Override
    public GLSLType getType() {
        return GLSLType.FLOAT_VEC3;
    }

    @Override
    public void bake(MemoryLayout layout, long off) throws IllegalStructSizeException {
        this.offset = off;
    }

    @Override
    public void packageData(ByteBuffer byteBuffer) {
        byteBuffer.putFloat(data.getX());
        byteBuffer.putFloat(data.getY());
        byteBuffer.putFloat(data.getZ());
        byteBuffer.putFloat(0); // padding
    }

    @Override
    public long byteSize() {
        return 16;
    }

    @Override
    public long byteOffset() {
        return offset;
    }
}
