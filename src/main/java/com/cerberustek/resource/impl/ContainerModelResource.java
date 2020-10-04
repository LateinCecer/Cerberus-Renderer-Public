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

import com.cerberustek.resource.model.IndexBufferArrayResource;
import com.cerberustek.resource.model.ModelResource;
import com.cerberustek.resource.model.VertexBufferResource;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.geometry.GeometryBoard;
import com.cerberustek.geometry.IndexBufferArray;
import com.cerberustek.geometry.Mesh;
import com.cerberustek.geometry.VertexBuffer;
import com.cerberustek.geometry.impl.ContainerMesh;

public class ContainerModelResource implements ModelResource {

    private final IndexBufferArrayResource ibo;
    private final VertexBufferResource vbo;
    private final int index;

    public ContainerModelResource(VertexBufferResource vbo, IndexBufferArrayResource ibo, int index) {
        this.ibo = ibo;
        this.vbo = vbo;
        this.index = index;
    }

    @Override
    public Mesh load() {
        CerberusRenderer renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        GeometryBoard board = renderer.getGeometryBoard();

        VertexBuffer vertexBuffer = board.getVertexBuffer(vbo);
        IndexBufferArray indexBufferArray = board.getIndexBufferArray(ibo);

        if (vertexBuffer == null || indexBufferArray == null)
            throw new IllegalStateException("Vertex Buffer and Index Buffer could not be loaded! This should never happen!");
        return new ContainerMesh(vertexBuffer, indexBufferArray.getIndexBuffer(index));
    }
}
