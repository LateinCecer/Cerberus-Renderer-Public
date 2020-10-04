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

package com.cerberustek.util;

import com.cerberustek.geometry.BufferFlag;
import com.cerberustek.geometry.Vertex;
import com.cerberustek.logic.math.Matrix4f;
import com.cerberustek.logic.math.Vector2f;
import com.cerberustek.logic.math.Vector3f;
import com.cerberustek.texture.ImageType;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.List;

public class BufferUtil {

    public static final BufferFlag[] FLAGS_GPGPU_STATIC = new BufferFlag[] {BufferFlag.GLSL_ONLY};
    public static final BufferFlag[] FLAGS_GPGPU_MAP_OUPUT = new BufferFlag[] {BufferFlag.MAP_READ, BufferFlag.MAP_PERSISTANT, BufferFlag.MAP_COHERANT};
    public static final BufferFlag[] FLAGS_GPGPU_MAP_INPUT = new BufferFlag[] {BufferFlag.MAP_WRITE, BufferFlag.MAP_PERSISTANT, BufferFlag.MAP_COHERANT};
    public static final BufferFlag[] FLAGS_GPGPU_DATA_OUTPUT = new BufferFlag[] {BufferFlag.DYNAMIC_STORAGE};
    public static final BufferFlag[] FLAGS_GPGPU_DATA_INPUT = new BufferFlag[] {BufferFlag.DYNAMIC_STORAGE, BufferFlag.CLIENT_STORAGE};

    public static ByteBuffer createFlippedBuffer(BufferedImage image, ImageType type) {
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        ByteBuffer buffer = BufferUtils.createByteBuffer(pixels.length * type.byteSize());
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        switch (type.getFormat()) {
            case RGBA:
                for (int pixel : pixels) {
                    buffer.put((byte) ((pixel >> 16) & 0xFF));
                    buffer.put((byte) ((pixel >> 8) & 0xFF));
                    buffer.put((byte) (pixel & 0xFF));
                    buffer.put((byte) ((pixel >> 24) & 0xFF));
                }
                break;
            case RGB:
                for (int pixel : pixels) {
                    buffer.put((byte) ((pixel >> 16) & 0xFF));
                    buffer.put((byte) ((pixel >> 8) & 0xFF));
                    buffer.put((byte) (pixel & 0xFF));
                }
                break;
            case BGRA:
                for (int pixel : pixels) {
                    buffer.put((byte) (pixel & 0xFF));
                    buffer.put((byte) ((pixel >> 8) & 0xFF));
                    buffer.put((byte) ((pixel >> 16) & 0xFF));
                    buffer.put((byte) ((pixel >> 24) & 0xFF));
                }
                break;
            case BGR:
                for (int pixel : pixels) {
                    buffer.put((byte) (pixel & 0xFF));
                    buffer.put((byte) ((pixel >> 8) & 0xFF));
                    buffer.put((byte) ((pixel >> 16) & 0xFF));
                }
                break;
            default:
                for (int pixel : pixels)
                    buffer.putInt(pixel);
        }
        buffer.flip();
        return buffer;
    }

    public static IntBuffer createFlippedBuffer(int... indices) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer buffer = stack.mallocInt(indices.length);
            buffer.put(indices);
            buffer.flip();
            return buffer;
        }
    }

    public static FloatBuffer createFlippedBuffer(Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++)
                    buffer.put(value.get(i, j));
            }
            buffer.flip();
            return buffer;
        }
    }

    @SuppressWarnings("DuplicatedCode")
    public static FloatBuffer createFlippedBuffer(List<Vector3f> values) {
        FloatBuffer vectorBuffer = BufferUtils.createFloatBuffer(values.size() * 3);

        for (Vector3f value : values) {
            if (value == null)
                throw new IllegalStateException("Buffer element cannot be null!");

            vectorBuffer.put(value.getX());
            vectorBuffer.put(value.getY());
            vectorBuffer.put(value.getZ());
        }
        vectorBuffer.flip();

        return vectorBuffer;
    }

    @SuppressWarnings("DuplicatedCode")
    public static FloatBuffer createFlippedBuffer(Collection<Vector3f> values) {
        FloatBuffer vectorBuffer = BufferUtils.createFloatBuffer(values.size() * 3);

        for (Vector3f value : values) {
            if (value == null)
                throw new IllegalStateException("Buffer element cannot be null!");

            vectorBuffer.put(value.getX());
            vectorBuffer.put(value.getY());
            vectorBuffer.put(value.getZ());
        }
        vectorBuffer.flip();

        return vectorBuffer;
    }

    @SuppressWarnings("DuplicatedCode")
    public static FloatBuffer createFlippedBuffer(Vector3f... values) {
        FloatBuffer vectorBuffer = BufferUtils.createFloatBuffer(values.length * 3);

        for (Vector3f value : values) {
            if (value == null)
                throw new IllegalStateException("Buffer element cannot be null!");

            vectorBuffer.put(value.getX());
            vectorBuffer.put(value.getY());
            vectorBuffer.put(value.getZ());
        }
        vectorBuffer.flip();

        return vectorBuffer;
    }

    public static FloatBuffer createFlippedBuffer(Vector2f... values) {
        FloatBuffer vectorBuffer = BufferUtils.createFloatBuffer(values.length * 2);

        for (Vector2f value : values) {
            if (value == null)
                throw new IllegalStateException("Buffer element cannot be null!");

            vectorBuffer.put(value.getX());
            vectorBuffer.put(value.getY());
        }
        vectorBuffer.flip();

        return vectorBuffer;
    }

    public static FloatBuffer createVertexBuffer(Vertex[] vertices, boolean normals) {
        return createVertexBuffer(vertices, 0, vertices.length, normals);
    }

    public static FloatBuffer createVertexBuffer(Vertex[] vertices, int off, int len, boolean normals) {
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(len * (normals ? 8 : 5));

        Vertex ver;
        for (int i = off; i < len; i++) {
            ver = vertices[i];

            vertexBuffer.put(ver.getPosition().getX());
            vertexBuffer.put(ver.getPosition().getY());
            vertexBuffer.put(ver.getPosition().getZ());
            vertexBuffer.put(ver.getTexturePos().getX());
            vertexBuffer.put(ver.getTexturePos().getY());

            if (normals) {
                vertexBuffer.put(ver.getNormal().getX());
                vertexBuffer.put(ver.getNormal().getY());
                vertexBuffer.put(ver.getNormal().getZ());
            }
        }
        // has to be little endian
        vertexBuffer.flip();

        return vertexBuffer;
    }
}
