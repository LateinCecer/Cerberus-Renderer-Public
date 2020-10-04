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

import com.cerberustek.buffer.BufferAccess;
import com.cerberustek.logic.math.Vector3i;
import com.cerberustek.texture.ImageTexture;
import com.cerberustek.texture.ImageType;
import com.cerberustek.texture.Texture;

import java.util.Arrays;

import static org.lwjgl.opengl.GL42.*;

public class ImageTextureImpl implements ImageTexture {

    private final Texture buffer;
    private final int[] layers;

    public ImageTextureImpl(Texture texture) {
        this.buffer = texture;
        this.layers = new int[texture.length()];
        Arrays.fill(layers, 0);
    }

    @Override
    public void bindImage(int index, int unit, boolean layered, int layer, BufferAccess accessToken) {
        if (buffer.isOnline(index)) {
            glBindImageTexture(unit, buffer.getPointer(index), 0, layered, layer,
                    accessToken.glCode(), buffer.getType(index).toInternalFormat());
        }
    }

    @Override
    public void bindImage(int index, boolean layered, int layer, BufferAccess accessToken) {
        if (buffer.isOnline(index)) {
            glBindImageTexture(buffer.getUnit(index), buffer.getPointer(index), 0, layered, layer,
                    accessToken.glCode(), buffer.getType(index).toInternalFormat());
        }
    }

    @Override
    public void bindImage(int index, int unit, BufferAccess accessToken) {
        if (buffer.isOnline(index)) {
            int layer = layers[index];
            glBindImageTexture(unit, buffer.getPointer(index), 0, layer == -1, layer,
                    accessToken.glCode(), buffer.getType(index).toInternalFormat());
        }
    }

    @Override
    public void bindImage(int index, BufferAccess accessToken) {
        if (buffer.isOnline(index)) {
            int layer = layers[index];
            glBindImageTexture(buffer.getUnit(index), buffer.getPointer(index), 0, layer == -1, layer,
                    accessToken.glCode(), buffer.getType(index).toInternalFormat());
        }
    }

    @Override
    public void bindImage(BufferAccess accessToken) {
        for (int i = 0; i < buffer.length(); i++)
            bindImage(i, accessToken);
    }

    @Override
    public void unbindImage() {
        for (int i = 0; i < buffer.length(); i++)
            unbindImage(i);
    }

    @Override
    public void unbindImage(int index) {
        glBindImageTexture(buffer.getUnit(index), 0, 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
    }

    @Override
    public int getLayer(int index) {
        return layers[index];
    }

    @Override
    public void setLayer(int index, int layer) {
        layers[index] = layer;
    }

    @Override
    public boolean isOnline() {
        return buffer.isOnline();
    }

    @Override
    public boolean isOnline(int index) {
        return buffer.isOnline(index);
    }

    @Override
    public void genTextures() {
        buffer.genTextures();
    }

    @Override
    public void genTexture(int index) {
        buffer.genTexture(index);
    }

    @Override
    public void bind() {
        buffer.bind();
    }

    @Override
    public void bindToUnit(int index, int unit) {
        buffer.bindToUnit(index, unit);
    }

    @Override
    public void bind(int index) {
        buffer.bind(index);
    }

    @Override
    public void destroy(int index) {
        buffer.destroy(index);
    }

    @Override
    public int length() {
        return buffer.length();
    }

    @Override
    public int getUnit(int index) {
        return buffer.getUnit(index);
    }

    @Override
    public int getPointer(int index) {
        return buffer.getPointer(index);
    }

    @Override
    public ImageType getType(int index) {
        return buffer.getType(index);
    }

    @Override
    public Vector3i getSize(int index) {
        return buffer.getSize(index);
    }

    @Override
    public void destroy() {
        unbindImage();
        buffer.destroy();
    }
}
