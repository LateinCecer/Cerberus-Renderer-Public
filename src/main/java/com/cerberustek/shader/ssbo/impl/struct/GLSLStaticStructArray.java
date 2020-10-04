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
import com.cerberustek.shader.GLSLType;
import com.cerberustek.shader.ssbo.MemoryLayout;
import com.cerberustek.shader.ssbo.Struct;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class GLSLStaticStructArray extends StaticStructArray<Struct> {

    private long byteSize;
    private int padding;

    public GLSLStaticStructArray(int size) {
        super(new Struct[size]);
    }

    public GLSLStaticStructArray(Struct[] data) {
        super(data);
    }

    @Override
    public GLSLType getType() {
        return null;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void bake(MemoryLayout layout, long off) throws IllegalStructSizeException {
        this.offset = off;

        if (size() == 0) {
            byteSize = 16;
            padding = 16;
            return;
        }

        long offset = off;
        long structSize = 0;
        long structSizeIncrement = 0;

        for (int i = 0; i < size(); i++) {
            data[i].bake(layout, offset);

            if (i == 0) {
                structSize = data[i].byteSize();

                // round up to the next multiple of 16
                int mod = (int) (structSize % 16);
                if (mod != 0) {
                    padding = 16 - mod;
                    structSizeIncrement = structSize + padding;
                } else {
                    structSizeIncrement = structSize;
                    padding = 0;
                }
            } else {
                if (data[i].byteSize() != structSize)
                    throw new IllegalStructSizeException(data[i].byteSize(), structSize);
            }

            offset += structSizeIncrement;
        }
        byteSize = offset - off;
    }

    @Override
    public void packageData(ByteBuffer byteBuffer) {
        byte[] padding = new byte[this.padding];
        Arrays.fill(padding, (byte) 0);

        for (Struct s : data) {
            s.packageData(byteBuffer);
            byteBuffer.put(padding);
        }
    }

    @Override
    public long byteSize() {
        return byteSize;
    }
}
