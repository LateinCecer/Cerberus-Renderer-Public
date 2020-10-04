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
import com.cerberustek.logic.math.Matrix4f;
import com.cerberustek.shader.GLSLType;
import com.cerberustek.shader.ssbo.MemoryLayout;

import java.nio.ByteBuffer;

public class GLSLStaticMat4fArray extends StaticStructArray<Matrix4f> {

    private long byteSize;

    public GLSLStaticMat4fArray(int size) {
        super(new Matrix4f[size]);
    }

    public GLSLStaticMat4fArray(Matrix4f[] data) {
        super(data);
    }

    @Override
    public GLSLType getType() {
        return GLSLType.FLOAT_MAT4;
    }

    @Override
    public void bake(MemoryLayout layout, long off) throws IllegalStructSizeException {
        this.offset = off;
        this.byteSize = size() * 64;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void packageData(ByteBuffer byteBuffer) {
        for (Matrix4f mat : data) {
            byteBuffer.putFloat(mat.get(0, 0));
            byteBuffer.putFloat(mat.get(1, 0));
            byteBuffer.putFloat(mat.get(2, 0));
            byteBuffer.putFloat(mat.get(3, 0));

            byteBuffer.putFloat(mat.get(0, 1));
            byteBuffer.putFloat(mat.get(1, 1));
            byteBuffer.putFloat(mat.get(2, 1));
            byteBuffer.putFloat(mat.get(3, 1));

            byteBuffer.putFloat(mat.get(0, 2));
            byteBuffer.putFloat(mat.get(1, 2));
            byteBuffer.putFloat(mat.get(2, 2));
            byteBuffer.putFloat(mat.get(3, 2));

            byteBuffer.putFloat(mat.get(0, 3));
            byteBuffer.putFloat(mat.get(1, 3));
            byteBuffer.putFloat(mat.get(2, 3));
            byteBuffer.putFloat(mat.get(3, 3));
        }
    }

    @Override
    public long byteSize() {
        return byteSize;
    }
}
