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

import com.cerberustek.resource.buffered.UnsignedByteBufferResource;
import com.cerberustek.resource.image.ImageResource;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.logic.math.Vector3i;

import com.cerberustek.texture.ImageType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

@SuppressWarnings("rawtypes")
public class Texture2D extends LoadableTexture {

    private final Vector2i[] sizes;
    private final int[] levels;
    private final boolean[] online;
    private final ImageType types[];

    public Texture2D(int size) {
        super(size);
        this.sizes = new Vector2i[size];
        this.online = new boolean[size];
        this.levels = new int[size];
        this.types = new ImageType[size];
    }

    @Override
    public void destroy(int index) {
        online[index] = false;
        super.destroy(index);
    }

    @Override
    public int getUnit(int index) {
        return levels[index];
    }

    @Override
    public ImageType getType(int index) {
        return types[index];
    }

    @Override
    public void upload(@NotNull ImageResource imageResource, int activeLevel, int index) throws IOException {
        if (!(imageResource instanceof UnsignedByteBufferResource))
            throw new IllegalArgumentException("A 2D-LoadableTexture has to be initialized with a unsigned byte" +
                    " buffer resource!");

        UnsignedByteBufferResource byteResource = (UnsignedByteBufferResource) imageResource;
        /* Load the byte buffer. For some image resources, the image
        * size and type is only available while the image resource is
        * loaded. */
        ByteBuffer buffer = byteResource.load();

        sizes[index] = imageResource.getSize();
        levels[index] = activeLevel;
        types[index] = imageResource.getType();

        // CerberusRegistry registry = CerberusRegistry.getInstance();
        if (isOnline(index)) {
            // registry.warning("Deleting texture " + index + "!");
            glDeleteTextures(idBuffer[index]);
        }

        glActiveTexture(GL_TEXTURE0 + activeLevel);
        glBindTexture(GL_TEXTURE_2D, idBuffer[index]);

        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);


        /*
        registry.debug(TerminalUtil.ANSI_YELLOW + "Texture registered to texture unit: " + activeLevel +
                " of " + idBuffer.length + TerminalUtil.ANSI_RESET);
        registry.debug("Texture type: " + imageResource.getType());
        registry.debug("Texture size: width = " + imageResource.getSize().getX() +
                " height = " + imageResource.getSize().getY());
        registry.debug("Texture buffer type: " + byteResource.getBufferType());
        registry.debug("Texture data loaded via byte buffer!");*/



        glTexImage2D(GL_TEXTURE_2D, 0, imageResource.getType().toInternalFormat(),
                imageResource.getSize().getX(), imageResource.getSize().getY(),
                0, imageResource.getType().toFormat(), byteResource.getBufferType().getGlId(),
                buffer);
        imageResource.close();

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        online[index] = true;
    }

    @Override
    public boolean isOnline(int index) {
        return online[index];
    }

    @Override
    public Vector3i getSize(int index) {
        return new Vector3i(sizes[index].getX(), sizes[index].getY(), 0);
    }
}
