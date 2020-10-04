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

package com.cerberustek.geometry.impl;

import com.cerberustek.geometry.IndexBuffer;
import com.cerberustek.geometry.Mesh;
import com.cerberustek.geometry.VertexBuffer;

public class ContainerMesh implements Mesh {

    private final VertexBuffer vertexBuffer;
    private final IndexBuffer indexBuffer;

    public ContainerMesh(VertexBuffer vertexBuffer, IndexBuffer indexBuffer) {
        this.vertexBuffer = vertexBuffer;
        this.indexBuffer = indexBuffer;
    }

    @Override
    public VertexBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    @Override
    public IndexBuffer getIndexBuffer() {
        return indexBuffer;
    }

    @Override
    public void destroy() {
        vertexBuffer.destroy();
        // The index buffer should not be
        // destroyed here, because index
        // buffers usually are held by
        // index buffer arrays and the
        // deletion for one partition
        // would lead to the deletion of
        // all partitions
    }
}
