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
import com.cerberustek.resource.buffered.ByteBufferResource;
import com.cerberustek.resource.impl.StaticByteBufferResource;
import com.cerberustek.resource.impl.StaticIndexBufferArrayResource;
import com.cerberustek.resource.impl.StaticVertexAttribResource;
import com.cerberustek.resource.model.IndexBufferArrayResource;
import com.cerberustek.resource.model.VertexAttribResource;
import com.cerberustek.geometry.ComponentType;
import com.cerberustek.geometry.DataType;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

public class GITFAccessor implements GITFEntry {

    private GITFBufferView bufferView;
    private int byteOffset;
    private int count;
    private MetaData min;
    private MetaData max;
    private DataType type;
    private ComponentType componentType;

    @Override
    public void read(GITFReader reader, MetaData data) throws GITFFormatException {
        if (!(data instanceof DocElement))
            throw new GITFFormatException();
        DocElement doc = (DocElement) data;

        Integer bufferView = doc.valueInt("bufferView");
        if (bufferView == null)
            throw new GITFFormatException("No buffer view specified for accessor");
        this.bufferView = reader.getBufferView(bufferView);

        byteOffset = doc.value("byteOffset", 0);

        Integer componentType = doc.valueInt("componentType");
        if (componentType == null)
            throw new GITFFormatException("No component type specified for accessor");
        this.componentType = ComponentType.valueOf(componentType);
        if (this.componentType == null)
            throw new GITFFormatException("Unknown component type: " + componentType);

        Integer count = doc.valueInt("count");
        if (count == null)
            throw new GITFFormatException("No element count specified for accessor");
        this.count = count;

        min = doc.extract("min");
        max = doc.extract("max");

        String type = doc.valueString("type");
        if (type == null)
            throw new GITFFormatException("No type specified for accessor");
        this.type = DataType.fromName(type);
        if (this.type == null)
            throw new GITFFormatException("Unknown type: " + type);
    }

    @Override
    public MetaData write() {
        return null;
    }

    public GITFBufferView getBufferView() {
        return bufferView;
    }

    public int getByteOffset() {
        return byteOffset;
    }

    public int getCount() {
        return count;
    }

    public MetaData getMin() {
        return min;
    }

    public MetaData getMax() {
        return max;
    }

    public DataType getType() {
        return type;
    }

    public ComponentType getComponentType() {
        return componentType;
    }

    public VertexAttribResource asVertexAttrib() {
        GITFBufferView bufferView = getBufferView();
        ByteBufferResource bufferResource = new StaticByteBufferResource(bufferView.asByteBuffer());

        return new StaticVertexAttribResource(bufferResource,
                getByteOffset(),
                getComponentType().sizeof() * getType().getNumComponents(),
                0);
    }

    public IndexBufferArrayResource asIndexBufferArray() {
        GITFBufferView bufferView = getBufferView();
        ByteBufferResource bufferResource = new StaticByteBufferResource(bufferView.asByteBuffer());
        ByteBuffer byteBuffer = bufferResource.load();
        ShortBuffer intBuffer = byteBuffer.asShortBuffer();

        // System.out.println("Accessor count: " + getCount());
        // System.out.println("Buffer cap> byte: " + byteBuffer.capacity() + ", int: " + intBuffer.capacity());


        int[] data = new int[getCount()];
        short[] sdata = new short[getCount()];
        intBuffer.get(sdata);
        for (int i = 0; i < data.length; i++)
            data[i] = Short.toUnsignedInt(sdata[i]);

        System.out.println("Indicies: " + Arrays.toString(data));

        return new StaticIndexBufferArrayResource(data, new long[]{}, getComponentType());
    }
}
