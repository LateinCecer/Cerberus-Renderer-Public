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
import com.cerberustek.data.impl.elements.IntElement;
import com.cerberustek.data.impl.tags.ArrayTag;
import com.cerberustek.exceptions.GITFFormatException;
import com.cerberustek.pipeline.RenderScene;

import java.util.ArrayList;

public class GITFScene implements GITFEntry {

    private ArrayList<GITFNode> nodes = new ArrayList<>();

    @Override
    public void read(GITFReader reader, MetaData data) throws GITFFormatException {
        if (!(data instanceof DocElement))
            throw new GITFFormatException();
        DocElement doc = (DocElement) data;

        @SuppressWarnings("unchecked") ArrayTag<IntElement> nodes = doc.extractArray("nodes");
        if (nodes != null) {
            for (IntElement node : nodes) {
                this.nodes.add(reader.getNote(node.get()));
            }
        }
    }

    /**
     * Returns the node at the specified index inside of the
     * scene.
     * @param index node index
     * @return node
     */
    public GITFNode getNode(int index) {
        if (index >= nodes.size())
            throw new IndexOutOfBoundsException();

        return nodes.get(index);
    }

    /**
     * Returns the amount of nodes stored inside the
     * scene.
     * @return amount of nodes
     */
    public int length() {
        return nodes.size();
    }

    /**
     * Will place all meshes from the gITF scene into of
     * the render scene.
     * @param scene render scene to populate
     */
    public void populate(RenderScene scene) {
        for (GITFNode node : nodes)
            node.populate(scene);
    }

    @Override
    public MetaData write() throws GITFFormatException {
        return null;
    }
}
