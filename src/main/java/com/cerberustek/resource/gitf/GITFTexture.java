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
import com.cerberustek.resource.image.TextureResource;
import com.cerberustek.resource.impl.BufferedTextureResource;

public class GITFTexture implements GITFEntry {

    private GITFSampler sampler;
    private GITFImage image;

    private TextureResource texture;

    @Override
    public void read(GITFReader reader, MetaData data) throws GITFFormatException {
        if (!(data instanceof DocElement))
            throw new GITFFormatException();
        DocElement doc = (DocElement) data;

        Integer samplerID = doc.valueInt("sampler");
        Integer imageId = doc.valueInt("source");

        if (samplerID == null || imageId == null)
            throw new GITFFormatException();

        sampler = reader.getSampler(samplerID);
        image = reader.getImage(imageId);

        texture = new BufferedTextureResource(image.loadBuffered());
    }

    @Override
    public MetaData write() throws GITFFormatException {
        return null;
    }

    public TextureResource getTextureResource() {
        return texture;
    }
}
