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
import com.cerberustek.data.impl.tags.IntTag;
import com.cerberustek.data.impl.tags.StringTag;
import com.cerberustek.exceptions.GITFFormatException;

import java.io.*;
import java.nio.ByteBuffer;

public class GITFBuffer implements GITFEntry {

    private ByteBuffer buffer;
    private File file;

    public GITFBuffer() {}

    public GITFBuffer(ByteBuffer buffer, File file) {
        this.buffer = buffer;
        this.file = file;
    }

    @Override
    public void read(GITFReader reader, MetaData data) throws GITFFormatException {
        if (!(data instanceof DocElement))
            throw new GITFFormatException();

        DocElement bufferDoc = (DocElement) data;

        Integer byteLength = bufferDoc.valueInt("byteLength");
        String uri = bufferDoc.valueString("uri");

        if (byteLength == null || uri == null)
            throw new GITFFormatException();

        file = reader.loadUri(uri);
        if (file == null || !file.exists())
            throw new GITFFormatException("Resource not found!");

        buffer = ByteBuffer.allocateDirect(byteLength);

        try (InputStream inputStream = new FileInputStream(file)) {

            byte[] buf = new byte[4096];
            for (int i = inputStream.read(buf); i != -1; i = inputStream.read(buf))
                buffer.put(buf, 0, i);

            buffer.rewind();
        } catch (IOException e) {
            throw new GITFFormatException("Unable to access bin file!");
        }
    }

    @Override
    public MetaData write() throws GITFFormatException {
        if (buffer == null || file == null)
            throw new GITFFormatException("No data contained in buffer!");

        DocElement data = new DocElement();

        data.insert(new IntTag("byteLength", buffer.capacity()));
        data.insert(new StringTag("uri", file.getName()));

        // write data to bin
        try (OutputStream outputStream = new FileOutputStream(file)) {

            buffer.rewind();
            byte[] buf = new byte[4096];

            while (buffer.remaining() > 0) {
                int toWrite = Math.min(buffer.remaining(), buf.length);
                buffer.get(buf, 0, toWrite);
                outputStream.write(buf, 0, toWrite);
            }
            outputStream.flush();

        } catch (IOException e) {
            throw new GITFFormatException("Unable to write to bin file!");
        }
        return data;
    }

    public void getData(int srcPos, byte[] data) {
        getData(srcPos, data, 0, data.length);
    }

    public synchronized void getData(int srcPos, byte[] data, int off, int len) {
        buffer.position(srcPos);
        if (len > buffer.remaining())
            throw new ArrayIndexOutOfBoundsException();
        if (len + off > data.length)
            throw new ArrayIndexOutOfBoundsException();

        buffer.get(data, off, len);
    }
}
