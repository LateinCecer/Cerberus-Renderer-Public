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

import com.cerberustek.CerberusEvent;
import com.cerberustek.events.ExceptionEvent;
import com.cerberustek.resource.image.ImageResource;
import com.cerberustek.resource.image.TextureResource;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.texture.Texture;
import com.cerberustek.texture.impl.Texture2D;

import java.awt.image.BufferedImage;
import java.io.IOException;

@SuppressWarnings("rawtypes")
public class BufferedTextureResource implements TextureResource {

    private final ImageResource[] imageResources;

    public BufferedTextureResource(ImageResource... imageResources) {
        this.imageResources = imageResources;
    }

    public BufferedTextureResource(BufferedImage... images) {
        imageResources = new BufferedImageResource[images.length];
        for (int i = 0; i < imageResources.length; i++) {
            if (images[i] == null)
                throw new IllegalArgumentException("You may not upload an empty image to the VRAM!");
            imageResources[i] = new BufferedImageResource(images[i], i);
        }
    }

    @Override
    public Texture load() {
        Texture2D output = new Texture2D(imageResources.length);
        output.genTextures();
        int[] units = new int[imageResources.length];
        for (int i = 0; i < imageResources.length; i++)
            units[i] = imageResources[i].getTextureUnit();

        try {
            output.upload(imageResources, units);
        } catch (IOException e) {
            CerberusRegistry registry = CerberusRegistry.getInstance();
            registry.warning("Could not load Texture Resource");
            registry.getService(CerberusEvent.class).executeFullEIF(new ExceptionEvent(CerberusRenderer.class, e));
        }
        return output;
    }
}
