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
import com.cerberustek.data.impl.tags.ArrayTag;
import com.cerberustek.exceptions.GITFFormatException;
import com.cerberustek.resource.model.ModelResource;
import com.cerberustek.pipeline.Renderable;
import com.cerberustek.pipeline.impl.renderables.StaticObjectRenderable;

import java.util.ArrayList;

public class GITFMesh implements GITFEntry {

    private final ArrayList<GITFPrimitive> primitives = new ArrayList<>();

    @Override
    public void read(GITFReader reader, MetaData data) throws GITFFormatException {
        if (!(data instanceof DocElement))
            throw new GITFFormatException();
        DocElement doc = (DocElement) data;

        // read primitives
        @SuppressWarnings("unchecked") ArrayTag<MetaData> primitives = doc.extractArray("primitives");
        if (primitives != null) {
            for (MetaData primitive : primitives) {
                GITFPrimitive p = new GITFPrimitive();
                p.read(reader, primitive);
                this.primitives.add(p);
            }
        }
    }

    public GITFPrimitive getPrimitive(int index) {
        if (index >= primitives.size())
            throw new IndexOutOfBoundsException();

        return primitives.get(index);
    }

    public ModelResource getModelResource(int index) {
        if (index >= primitives.size())
            throw new IndexOutOfBoundsException();

        return primitives.get(index).generateModelResource();
    }

    /**
     * Returns the amount of primitives in the mesh.
     * @return primitives
     */
    public int length() {
        return primitives.size();
    }

    /**
     * Will generate a renderable object from the primitive with the
     * specified <code>index</code>.
     * @param index primitive index
     * @return renderable
     */
    public Renderable generateRenderable(int index) {
        GITFPrimitive primitive = getPrimitive(index);
        return new StaticObjectRenderable(primitive.generateModelResource(), primitive.getMaterial());
    }

    @Override
    public MetaData write() throws GITFFormatException {
        return null;
    }
}
