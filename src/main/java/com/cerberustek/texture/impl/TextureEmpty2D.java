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

package com.cerberustek.texture.impl;

import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.logic.math.Vector3i;
import com.cerberustek.texture.ImageType;
import com.cerberustek.texture.Texture;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class TextureEmpty2D implements Texture {

    private final Vector2i[] sizes;
    private final int[] textureBuffers;
    private final int[] levels;
    private final boolean[] online;
    private final ImageType types[];

    public TextureEmpty2D(int size) {
        this.sizes = new Vector2i[size];
        this.textureBuffers = new int[size];
        this.levels = new int[size];
        this.online = new boolean[size];
        this.types = new ImageType[size];
    }

    /**
     * Initializes the a specific texture buffer.
     *
     * @param index Index of the texture buffer which to initialize
     * @param unit Active unit of the texture buffer
     * @param size Buffer size of the texture buffer
     * @param type The type of image that is to be drawn
     *             to the texture buffer
     */
    public void initTexture(int index, int unit, Vector2i size, ImageType type) {
        glActiveTexture(GL_TEXTURE0 + unit);
        glBindTexture(GL_TEXTURE_2D, textureBuffers[index]);

        this.sizes[index] = size;
        this.levels[index] = unit;
        this.online[index] = true;
        this.types[index] = type;
        glTexImage2D(GL_TEXTURE_2D, 0, type.toInternalFormat(), size.getX(), size.getY(), 0, type.toFormat(),
                type.preferedBuffer().getGlId(), (ByteBuffer) null);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    }

    /**
     * Initializes all textures with the specified units, sizes and
     * ImageTypes.
     *
     * Be careful, as the amount of units, sizes and image types passed
     * in this method each have to be equal to or greater as the amount
     * of texture buffers in the FrameBuffer.
     *
     * @param units Active units of the texture buffers
     * @param sizes Buffer sizes of the texture buffers
     * @param types Image types of the texture buffers
     */
    public void initTextures(@NotNull int[] units, @NotNull Vector2i[] sizes, @NotNull ImageType[] types) {
        if (units.length < length())
            throw new IllegalArgumentException("The amount of active units has to be greater or equal to the amount" +
                    " of texture buffers in the FrameBufferObject.");
        if (sizes.length < length())
            throw new IllegalArgumentException("The amount of texture sizes has to be greater or equal to the amount" +
                    " of texture buffers in the FrameBufferObject");
        if (types.length < length())
            throw new IllegalArgumentException("The amount of image types has to be greater or equal to the amount" +
                    " of textures buffers in the FrameBufferObject");

        for (int i = 0; i < length(); i++)
            initTexture(i, units[i], sizes[i], types[i]);
    }

    @Override
    public boolean isOnline() {
        for (int i = 0; i < length(); i++) {
            if (!isOnline(i))
                return false;
        }
        return true;
    }

    @Override
    public boolean isOnline(int index) {
        return online[index];
    }

    public void setTexture(int index, int pointer) {
        if (textureBuffers[index] != 0)
            throw new IllegalStateException("Texture buffer at index " + index + " is already allocated.");
        textureBuffers[index] = pointer;
    }

    /**
     * Allocated the texture buffer at the specific index.
     * @param index index to allocate the texture buffer at
     */
    public void genTexture(int index) {
        if (textureBuffers[index] == 0)
            textureBuffers[index] = glGenTextures();
        else
            throw new IllegalStateException("Texture buffer at index " + index + " is already allocated.");
    }

    @Override
    public void genTextures() {
        glGenTextures(textureBuffers);
    }

    @Override
    public void bind() {
        for (int i = 0; i < length(); i++)
            bind(i);
    }

    @Override
    public void bindToUnit(int index, int unit) {
        if (isOnline(index)) {
            glActiveTexture(GL_TEXTURE0 + unit);
            glBindTexture(GL_TEXTURE_2D, textureBuffers[index]);
        }
    }

    @Override
    public void bind(int index) {
        if (isOnline(index)) {
            glActiveTexture(GL_TEXTURE0 + levels[index]);
            glBindTexture(GL_TEXTURE_2D, textureBuffers[index]);
        }
    }

    @Override
    public void destroy(int index) {
        glDeleteTextures(textureBuffers[index]);
    }

    @Override
    public int length() {
        return textureBuffers.length;
    }

    @Override
    public int getUnit(int index) {
        return levels[index];
    }

    @Override
    public int getPointer(int index) {
        return textureBuffers[index];
    }

    @Override
    public ImageType getType(int index) {
        return types[index];
    }

    @Override
    public Vector3i getSize(int index) {
        return new Vector3i(sizes[index].getX(), sizes[index].getY(), 0);
    }

    @Override
    public void destroy() {
        glDeleteTextures(textureBuffers);
    }
}
