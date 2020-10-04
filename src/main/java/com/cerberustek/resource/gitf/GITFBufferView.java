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

package com.cerberustek.resource.gitf;

import com.cerberustek.data.MetaData;
import com.cerberustek.data.impl.elements.DocElement;
import com.cerberustek.exceptions.GITFFormatException;

import java.nio.ByteBuffer;

public class GITFBufferView implements GITFEntry {

    private GITFBuffer buffer;
    private int byteOffset;
    private int byteLength;
    private int byteStride;
    private int target;

    @Override
    public void read(GITFReader reader, MetaData data) throws GITFFormatException {
        if (!(data instanceof DocElement))
            throw new GITFFormatException();
        DocElement doc = (DocElement) data;

        Integer bufferID = doc.valueInt("buffer");
        Integer off = doc.valueInt("byteOffset");
        Integer len = doc.valueInt("byteLength");

        if (bufferID == null || off == null || len == null)
            throw new GITFFormatException("Missing data");

        buffer = reader.getBuffer(bufferID);
        byteOffset = off;
        byteLength = len;

        byteStride = doc.value("byteStride", 0);
        target = doc.value("target", -1);
    }

    @Override
    public MetaData write() throws GITFFormatException {
        return null;
    }

    public void getData(byte[] data) {
        getData(data, 0);
    }

    public void getData(byte[] data, int off) {
        if (buffer == null)
            throw new NullPointerException("Buffer not defined");

        buffer.getData(byteOffset, data, off, byteLength);
    }

    public ByteBuffer asByteBuffer() {
        byte[] data = new byte[byteLength];
        buffer.getData(byteOffset, data);
        return ByteBuffer.wrap(data);
    }

    public GITFBuffer getBuffer() {
        return buffer;
    }

    public int getByteOffset() {
        return byteOffset;
    }

    public int getByteLength() {
        return byteLength;
    }

    public int getByteStride() {
        return byteStride;
    }

    public int getTarget() {
        return target;
    }
}
