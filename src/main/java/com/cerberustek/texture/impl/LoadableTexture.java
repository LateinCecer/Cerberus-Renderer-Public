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

import com.cerberustek.resource.image.ImageResource;
import com.cerberustek.texture.Texture;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

public abstract class LoadableTexture implements Texture {

    /** GL texture pointer buffer */
    protected final int[] idBuffer;

    /**
     * Will create a texture object with {@code size} texture buffers.
     *
     * @param size amount of texture buffers
     */
    public LoadableTexture(int size) {
        idBuffer = new int[size];
    }

    @Override
    public void genTextures() {
        glGenTextures(idBuffer);
    }

    @Override
    public void genTexture(int index) {
        if (idBuffer[index] != 0)
            throw new IllegalStateException("The texture buffer at index: " + index + " is already allocated.");
        idBuffer[index] = glGenTextures();
    }

    /**
     * This method will update an array of images as textures to the GPU.
     *
     * The first entry of the image resource array will be uploaded to the
     * first texture buffer of the texture, on the corresponding texture
     * level. This, of course, means that you will need at least as may
     * resources as there are texture buffers in this texture and at least
     * as may active level as there are resources. (Otherwise this method
     * will throw IllegalArgumentExceptions)
     *
     * @param imageResources resources
     * @param activeLevels The active level each texture buffer will be
     *                    loaded to
     * @throws IOException Exception from closing the resources
     */
    public void upload(@NotNull ImageResource[] imageResources, int[] activeLevels) throws IOException {
        upload(imageResources, activeLevels, 0);
    }

    /**
     * This method will update an array of images as textures to the GPU.
     *
     * The first entry of the image resource array will be uploaded to the
     * first texture buffer of the texture, on the corresponding texture
     * level. This, of course, means that you will need at least as may
     * resources as there are texture buffers in this texture and at least
     * as may active level as there are resources. (Otherwise this method
     * will throw IllegalArgumentExceptions).
     * The offset on this method indicates the starting index on the
     * {@code imageResources} and {@code activeLevels} arrays, however
     * it will have no effect on the starting index of the texture buffer
     * on which the images will be loaded.
     *
     * @param imageResources resources
     * @param activeLevels The active level each texture will be
     *                     loaded to
     * @param offset The offset of the resource and active level arrays
     * @throws IOException Exception from closing the resources
     */
    public void upload(@NotNull ImageResource[] imageResources, int[] activeLevels, int offset) throws IOException {
        if (imageResources.length - offset < idBuffer.length)
            throw new IllegalArgumentException("Resource buffer may not be smaller than the size of the texture!");
        if (activeLevels.length - offset < imageResources.length)
            throw new IllegalArgumentException("There have to be at least as many active level as image resources!");

        for (int i = 0; i < idBuffer.length; i++)
            upload(imageResources[i + offset], activeLevels[i + offset], i + offset);
    }

    /**
     * This method will update a single image to a single texture buffer on
     * a specified active level.
     *
     * @param imageResource resource
     * @param activeLevel The active level the texture will be loaded on
     * @param index The index of the texture buffer on which to load the
     *              image to
     * @throws IOException Exception from closing the resources
     */
    public abstract void upload(@NotNull ImageResource imageResource, int activeLevel, int index) throws IOException;

    @Override
    public boolean isOnline() {
        for (int i = 0; i < idBuffer.length; i++) {
            if (!isOnline(i))
                return false;
        }
        return true;
    }

    @Override
    public int getPointer(int index) {
        return idBuffer[index];
    }

    @Override
    public void bind() {
        for (int i = 0; i < idBuffer.length; i++)
            bind(i);
    }

    @Override
    public void bind(int index) {
        if (isOnline(index)) {
            glActiveTexture(GL_TEXTURE0 + getUnit(index));
            glBindTexture(GL_TEXTURE_2D, idBuffer[index]);
        }
    }

    @Override
    public void bindToUnit(int index, int unit) {
        if (isOnline(index)) {
            glActiveTexture(GL_TEXTURE0 + unit);
            glBindTexture(GL_TEXTURE_2D, idBuffer[index]);
        }
    }

    @Override
    public int length() {
        return idBuffer.length;
    }

    @Override
    public void destroy() {
        glDeleteTextures(idBuffer);
    }

    @Override
    public void destroy(int index) {
        glDeleteTextures(idBuffer[index]);
    }
}
