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
import com.cerberustek.exceptions.GITFFormatException;
import com.cerberustek.logic.math.Vector3d;
import com.cerberustek.pipeline.RenderScene;

public class GITFNode implements GITFEntry {

    private GITFMesh mesh;
    private Vector3d scale;

    @Override
    public void read(GITFReader reader, MetaData data) throws GITFFormatException {
        if (!(data instanceof DocElement))
            throw new GITFFormatException();
        DocElement doc = (DocElement) data;

        Integer meshId = doc.valueInt("mesh");
        if (meshId == null)
            throw new GITFFormatException("A gITF node needs a mesh id");
        this.mesh = reader.getMesh(meshId);

        @SuppressWarnings("unchecked") ArrayTag<DoubleElement> array = doc.extractArray("scale");
        if (array != null) {
            scale = new Vector3d(
                    array.get(0).get(),
                    array.get(1).get(),
                    array.get(2).get()
            );
        } else
            scale = new Vector3d(1, 1, 1);
    }

    public GITFMesh getMesh() {
        return mesh;
    }

    public Vector3d getScale() {
        return scale;
    }

    public void populate(RenderScene scene) {
        for (int i = 0; i < mesh.length(); i++)
            scene.addRenderable(mesh.generateRenderable(i));
    }

    @Override
    public MetaData write() throws GITFFormatException {
        return null;
    }
}
