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
import com.cerberustek.logic.math.Vector2d;
import com.cerberustek.shader.GLSLType;
import com.cerberustek.shader.ssbo.MemoryLayout;

import java.nio.ByteBuffer;

public class GLSLDynamicVec2dArray extends DynamicStructArray<Vector2d> {

    private long byteSize;

    public GLSLDynamicVec2dArray() {}

    public GLSLDynamicVec2dArray(Vector2d[] data) {
        super(data);
    }

    @Override
    public GLSLType getType() {
        return GLSLType.DOUBLE_VEC2;
    }

    @Override
    public void bake(MemoryLayout layout, long off) throws IllegalStructSizeException {
        this.offset = off;
        this.byteSize = size() * 16;
    }

    @Override
    public void packageData(ByteBuffer byteBuffer) {
        for (Vector2d v : data) {
            byteBuffer.putDouble(v.getX());
            byteBuffer.putDouble(v.getY());
        }
    }

    @Override
    public long byteSize() {
        return byteSize;
    }
}
