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

package com.cerberustek.resource.impl;

import com.cerberustek.resource.buffered.UnsignedByteBufferResource;
import com.cerberustek.resource.image.ImageResource;
import com.cerberustek.geometry.ComponentType;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.texture.ImageType;
import com.cerberustek.util.BufferUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

public class BufferedImageResource extends UnsignedByteBufferResource implements ImageResource<ByteBuffer> {

    private final BufferedImage image;
    private final int unit;

    // private MemoryStack stack;

    public BufferedImageResource(BufferedImage image, int unit) {
        this.image = image;
        this.unit = unit;
    }

    @Override
    public ComponentType getBufferType() {
        return getType().preferedBuffer();
    }

    @Override
    public Vector2i getSize() {
        return new Vector2i(image.getWidth(), image.getHeight());
    }

    @Override
    public ImageType getType() {
        return ImageType.fromBufferedImage(image);
    }

    @Override
    public int getTextureUnit() {
        return unit;
    }

    @Override
    public ByteBuffer load() {
        return BufferUtil.createFlippedBuffer(image, getType());
    }

    @Override
    public void close() throws IOException {
        // stack.pop();
    }
}
