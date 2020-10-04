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

package com.cerberustek.geometry.impl;

import com.cerberustek.CerberusRegistry;
import com.cerberustek.buffer.GlBufferObject;
import com.cerberustek.buffer.GlBufferTarget;
import com.cerberustek.buffer.impl.SimpleGlBufferObject;
import com.cerberustek.geometry.BufferFlag;
import com.cerberustek.geometry.ComponentType;
import com.cerberustek.geometry.IndexBuffer;
import com.cerberustek.geometry.IndexBufferArray;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL44.*;

public class StaticIndexBufferArray implements IndexBufferArray {

    private GlBufferObject ibo;
    private long size;
    private IndexBuffer[] elements;

    @Override
    public void genBuffers() {
        ibo = new SimpleGlBufferObject(GlBufferTarget.ELEMENT_ARRAY, glGenBuffers());
    }

    @Override
    public void bind(int index) {
        IndexBuffer buffer = getIndexBuffer(index);
        if (buffer != null)
            buffer.bind();
        else
            throw new ArrayIndexOutOfBoundsException();
    }

    @Override
    public void bind() {
        ibo.bind();
    }

    @Override
    public GlBufferObject bufferObject() {
        return ibo;
    }

    @Override
    public IndexBuffer getIndexBuffer(int index) {
        return elements[index];
    }

    @Override
    public boolean contains(IndexBuffer buffer) {
        return buffer.bufferObject() == ibo;
    }

    @Override
    public void allocate(long byteSize) {
        ibo.bind();
        // For dynamic buffers use flag                    GL_MAP_WRITE_BIT
        glBufferStorage(GL_ELEMENT_ARRAY_BUFFER, byteSize, 0);
        size = byteSize;
    }

    @Override
    public void allocate(long byteSize, BufferFlag flag) {
        ibo.bind();
        glBufferStorage(GL_ELEMENT_ARRAY_BUFFER, byteSize, flag.glCode());
        size = byteSize;
    }

    @Override
    public void allocate(int size, ComponentType type) {
        allocate((long) size * type.sizeof());
    }

    @Override
    public void allocate(int[] indices) {
        ibo.bind();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        size = indices.length * 4;
    }

    @Override
    public void allocate(short[] indices) {
        ibo.bind();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        size = indices.length * 2;
    }

    @Override
    public void partition(long[] cuts, ComponentType[] dataTypes) {
        if (cuts.length + 1 != dataTypes.length)
            throw new IllegalArgumentException("There has to be exactly one more data type can cuts");

        if (dataTypes.length == 1) {
            elements = new IndexBuffer[] {
                    new StaticIndexBinding(ibo, 0, (int) (byteSize() / dataTypes[0].sizeof()), dataTypes[0])
            };
        } else {
            elements = new IndexBuffer[cuts.length + 1];

            elements[0] = new StaticIndexBinding(ibo, 0, (int) (cuts[0] / dataTypes[0].sizeof()), dataTypes[0]);
            for (int i = 1; i < cuts.length; i++) {
                elements[i] = new StaticIndexBinding(ibo, cuts[i - 1],
                        (int) ((cuts[i] - cuts[i - 1]) / dataTypes[i].sizeof()), dataTypes[i]);
            }
            elements[cuts.length] = new StaticIndexBinding(ibo, cuts[cuts.length - 1],
                    (int) ((byteSize() - cuts[cuts.length - 1]) / dataTypes[cuts.length].sizeof()),
                    dataTypes[cuts.length]);
        }
    }

    @Override
    public void partition(long[] cuts, ComponentType dataType) {
        if (cuts.length == 0) {
            elements = new IndexBuffer[] {
                    new StaticIndexBinding(ibo, 0, (int) (size / dataType.sizeof()), dataType)
            };
        } else {
            elements = new IndexBuffer[cuts.length + 1];

            elements[0] = new StaticIndexBinding(ibo, 0, (int) (cuts[0] / dataType.sizeof()), dataType);
            for (int i = 1; i < cuts.length; i++) {
                elements[i] = new StaticIndexBinding(ibo, cuts[i - 1],
                        (int) ((cuts[i] - cuts[i - 1]) / dataType.sizeof()), dataType);
            }
            elements[cuts.length] = new StaticIndexBinding(ibo, cuts[cuts.length - 1],
                    (int) ((byteSize() - cuts[cuts.length - 1]) / dataType.sizeof()), dataType);
        }
    }

    @Override
    public void partition(long[] cuts, ComponentType[] dataTypes, Class<? extends IndexBuffer>[] bufferClass) {
        if (dataTypes.length <= 0)
            throw new IllegalArgumentException("There has to be at least one data type");
        if (cuts.length + 1 != dataTypes.length)
            throw new IllegalArgumentException("There has to be exactly one more data type can cuts");
        if (dataTypes.length != bufferClass.length)
            throw new IllegalArgumentException("The amount of data types and buffer classes have to be equal");

        if (dataTypes.length == 1) {
            elements = new IndexBuffer[] {
                    invokeBufferInstance(bufferClass[0], 0, (int) (size / dataTypes[0].sizeof()), dataTypes[0])
            };
        } else {
            elements = new IndexBuffer[cuts.length + 1];

            elements[0] = invokeBufferInstance(bufferClass[0], 0, (int) (cuts[0] / dataTypes[0].sizeof()), dataTypes[0]);
            for (int i = 1; i < cuts.length; i++) {
                elements[i] = invokeBufferInstance(bufferClass[i], cuts[i - 1],
                        (int) ((cuts[i] - cuts[i - 1]) / dataTypes[i].sizeof()), dataTypes[i]);
            }
            elements[cuts.length] = invokeBufferInstance(bufferClass[cuts.length], cuts[cuts.length - 1],
                    (int) ((byteSize() - cuts[cuts.length - 1]) / dataTypes[cuts.length].sizeof()),
                    dataTypes[cuts.length]);
        }
    }

    private static final Class<?>[] params = new Class<?>[] {
            GlBufferObject.class,
            Long.class,
            Integer.class,
            ComponentType.class
    };

    private <T extends IndexBuffer> T invokeBufferInstance(Class<T> clazz, long off, int size, ComponentType type) {
        try {
            Constructor<? extends IndexBuffer> constructor = clazz.getConstructor(params);
            return clazz.cast(constructor.newInstance(ibo, off, size, type));
        } catch (NoSuchMethodException e) {
            CerberusRegistry.getInstance().warning("Could not catch buffer constructor for class "
                    + clazz);
        } catch (IllegalAccessException e) {
            CerberusRegistry.getInstance().warning("Could not access buffer constructor for class "
                    + clazz);
        } catch (InstantiationException e) {
            CerberusRegistry.getInstance().warning("Could not instantiate index buffer partition:\n" + e);
        } catch (InvocationTargetException e) {
            CerberusRegistry.getInstance().warning("Could not invoke index buffer partition for class "
                    + clazz);
        }
        return null;
    }

    @Override
    public int size() {
        return elements.length;
    }

    @Override
    public long byteSize() {
        return size;
    }

    @Override
    public void destroy() {
        ibo.destroy();
    }
}
