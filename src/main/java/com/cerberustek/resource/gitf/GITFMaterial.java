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
import com.cerberustek.data.impl.elements.DoubleElement;
import com.cerberustek.data.impl.tags.ArrayTag;
import com.cerberustek.data.impl.tags.DocTag;
import com.cerberustek.exceptions.GITFFormatException;
import com.cerberustek.resource.impl.StaticMaterialResource;
import com.cerberustek.resource.material.MaterialResource;
import com.cerberustek.logic.math.Vector4d;
import com.cerberustek.material.Material;
import com.cerberustek.material.impl.BaseMaterial;
import com.cerberustek.pipeline.impl.notes.SceneNote;
import com.cerberustek.shader.property.ShaderProperty3f;

public class GITFMaterial implements GITFEntry {

    private GITFTexture baseTexture;
    private GITFTexture normalTexture;
    private GITFTexture metallicRoughnessTexture;

    private Vector4d colorMod;

    @Override
    public void read(GITFReader reader, MetaData data) throws GITFFormatException {
        if (!(data instanceof DocElement))
            throw new GITFFormatException();
        DocElement doc = (DocElement) data;

        DocTag metallic = doc.extractDoc("pbrMetallicRoughness");
        if (metallic == null)
            throw new GITFFormatException();

        @SuppressWarnings("unchecked") ArrayTag<DoubleElement> baseColorFactor = metallic.extractArray("baseColorFactor");
        if (baseColorFactor == null)
            colorMod = new Vector4d(1, 1, 1, 1);
        else {
            if (baseColorFactor.length() != 4)
                throw new GITFFormatException("baseColorFactor vector length is "
                        + baseColorFactor.length() + ", but should be 4.");

            colorMod = new Vector4d(
                    baseColorFactor.get(0).get(),
                    baseColorFactor.get(1).get(),
                    baseColorFactor.get(2).get(),
                    baseColorFactor.get(3).get()
            );
        }

        baseTexture = extractTexture("baseColorTexture", metallic, reader);
        normalTexture = extractTexture("normalTexture", doc, reader);
        metallicRoughnessTexture = extractTexture("metallicRoughnessTexture", metallic, reader);

        if (baseTexture == null)
            throw new GITFFormatException("Missing base color texture!");
    }

    private GITFTexture extractTexture(String tag, DocElement doc, GITFReader reader) {
        DocTag texture = doc.extractDoc(tag);
        if (texture == null)
            return null;

        Integer index = texture.valueInt("index");
        if (index == null)
            return null;
        return reader.getTexture(index);
    }

    public MaterialResource generateMaterial() {
        Material material = new BaseMaterial();

        material.setTexture(SceneNote.COLOR, baseTexture.getTextureResource(), 0);
        if (normalTexture != null)
            material.setTexture(SceneNote.NORMAL, normalTexture.getTextureResource(), 0);
        if (metallicRoughnessTexture != null)
            material.setTexture(SceneNote.METALLIC, metallicRoughnessTexture.getTextureResource(), 0);

        material.getProperties().setProperty(new ShaderProperty3f("colorMod", colorMod.xyz().toVector3f()));
        return new StaticMaterialResource(material);
    }

    @Override
    public MetaData write() throws GITFFormatException {
        return null;
    }
}
