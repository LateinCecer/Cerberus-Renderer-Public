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
import com.cerberustek.shader.ssbo.MemoryLayout;
import com.cerberustek.shader.ssbo.Struct;
import com.cerberustek.shader.ssbo.StructEntry;
import com.cerberustek.shader.ssbo.StructQuery;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class GLSLStruct implements Struct {

    private final ArrayList<StructEntry> contents = new ArrayList<>();

    private long byteSize;
    private long offset;
    private int padding;

    @Override
    public StructEntry query(StructQuery request) {
        StructEntry entry = getElement(request.getIndex());
        if (request.hasNext()) {
            if (entry instanceof Struct)
                return ((Struct) entry).query(request.next());
            return null;
        } else
            return entry;
    }

    @Override
    public void append(StructEntry entry) {
        contents.add(entry);
    }

    @Override
    public void insert(StructEntry entry, int index) throws IndexOutOfBoundsException {
        contents.add(index, entry);
    }

    @Override
    public void remove() {
        contents.remove(contents.size() - 1);
    }

    @Override
    public void remove(int index) throws IndexOutOfBoundsException {
        contents.remove(index);
    }

    @Override
    public StructEntry getElement(int index) {
        return contents.get(index);
    }

    @Override
    public <T extends StructEntry> T getElement(int index, Class<T> clazz) {
        StructEntry entry = contents.get(index);
        if (entry != null) {
            try {
                return clazz.cast(entry);
            } catch (ClassCastException ignore) {}
        }
        return null;
    }

    @Override
    public ByteBuffer packageData() {
        ByteBuffer buffer = BufferUtils.createByteBuffer((int) byteSize);
        packageData(buffer);
        return buffer;
    }

    @Override
    public int size() {
        return contents.size();
    }

    @Override
    public void bake(MemoryLayout layout, long off) throws IllegalStructSizeException {
        this.offset = off;

        long offset = off;

        for (StructEntry entry : contents) {
            entry.bake(layout, offset);
            offset += entry.byteSize();
        }

        // add Padding
        offset -= off;
        int mod = (int) (offset % 16);
        if (mod != 0) {
            padding = 16 - mod;
            byteSize = offset + padding;
        } else {
            padding = 0;
            byteSize = offset;
        }
    }

    @Override
    public void packageData(ByteBuffer byteBuffer) {
        for (StructEntry entry : contents)
            entry.packageData(byteBuffer);

        // add padding to the back of the struct
        byte[] padding = new byte[this.padding];
        Arrays.fill(padding, (byte) 0);
        byteBuffer.put(padding);
    }

    @Override
    public long byteSize() {
        return byteSize;
    }

    @Override
    public long byteOffset() {
        return offset;
    }
}
