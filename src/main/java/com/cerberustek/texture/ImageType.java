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

package com.cerberustek.texture;

import com.cerberustek.geometry.ComponentType;

import java.awt.image.BufferedImage;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;

public enum ImageType {

    RGB_8_INTEGER(GL_RGB8, ImageFormat.RGB, 8, false, ComponentType.UNSIGNED_BYTE),
    RGB_16_INTEGER(GL_RGB16, ImageFormat.RGB, 16, false, ComponentType.UNSIGNED_BYTE),
    RGBA_8_INTEGER(GL_RGBA8, ImageFormat.RGBA, 8, true, ComponentType.UNSIGNED_BYTE),
    RGBA_16_INTEGER(GL_RGBA16, ImageFormat.RGBA, 16, true, ComponentType.UNSIGNED_BYTE),
    RGB_16_FLOAT(GL_RGB16F, ImageFormat.RGB, 16, false, ComponentType.HALF_FLOAT),
    RGB_32_FLOAT(GL_RGB32F, ImageFormat.RGB, 32, false, ComponentType.HALF_FLOAT),
    RGBA_16_FLOAT(GL_RGBA16F, ImageFormat.RGBA, 16, true, ComponentType.FLOAT),
    RGBA_32_FLOAT(GL_RGBA32F, ImageFormat.RGBA, 32, true, ComponentType.FLOAT),

    BGR_8_INTEGER(GL_RGB8, ImageFormat.BGR, 8, false, ComponentType.UNSIGNED_BYTE),
    BGR_16_INTEGER(GL_RGB16, ImageFormat.BGR, 16, false, ComponentType.UNSIGNED_BYTE),
    BGRA_8_INTEGER(GL_RGBA8, ImageFormat.BGRA, 8, true, ComponentType.UNSIGNED_BYTE),
    BGRA_16_INTEGER(GL_RGBA16, ImageFormat.BGRA, 16, true, ComponentType.UNSIGNED_BYTE),
    BGR_16_FLOAT(GL_RGB16F, ImageFormat.BGR, 16, false, ComponentType.HALF_FLOAT),
    BGR_32_FLOAT(GL_RGB32F, ImageFormat.BGR, 32, false, ComponentType.HALF_FLOAT),
    BGRA_16_FLOAT(GL_RGBA16F, ImageFormat.BGRA, 16, true, ComponentType.FLOAT),
    BGRA_32_FLOAT(GL_RGBA32F, ImageFormat.BGRA, 32, true, ComponentType.FLOAT),

    DEPTH_16_INTEGER(GL_DEPTH_COMPONENT, ImageFormat.DEPTH, 16, false, ComponentType.UNSIGNED_BYTE),
    DEPTH_24_INTEGER(GL_DEPTH_COMPONENT24, ImageFormat.DEPTH, 24, false, ComponentType.INT),
    DEPTH_32_INTEGER(GL_DEPTH_COMPONENT32, ImageFormat.DEPTH, 32, false, ComponentType.INT),
    DEPTH_32_FLOAT(GL_DEPTH_COMPONENT32F, ImageFormat.DEPTH, 32, false, ComponentType.FLOAT);

    private final int internalFormat;
    private final ImageFormat format;
    private final int pixelSize;
    private final boolean alpha;
    private final ComponentType bufferType;

    ImageType(int internalFormat, ImageFormat format, int pixelSize, boolean alpha, ComponentType bufferType) {
        this.internalFormat = internalFormat;
        this.format = format;
        this.pixelSize = pixelSize;
        this.alpha = alpha;
        this.bufferType = bufferType;
    }

    public int toInternalFormat() {
        return internalFormat;
    }

    @Deprecated
    public int toFormat() {
        return format.glCode();
    }

    public ImageFormat getFormat() {
        return format;
    }

    public boolean hasAlpha() {
        return alpha;
    }

    /**
     * Returns the <bold>bit</bold>> size of a single pixel
     * <bold>per channel!</bold>
     * @return bit size of a pixel per channel
     */
    public int sizeOfPixel() {
        return pixelSize;
    }

    /**
     * Returns the total byte size of a single pixel
     * @return pixel size
     */
    public int byteSize() {
        return (pixelSize / 8) * (alpha ? 4 : 3);
    }

    public ComponentType preferedBuffer() {
        return bufferType;
    }

    public static ImageType fromBufferedImage(BufferedImage image) {
        return RGBA_8_INTEGER;
    }
}
