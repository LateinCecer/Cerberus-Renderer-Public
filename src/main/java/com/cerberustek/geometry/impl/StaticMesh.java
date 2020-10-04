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

import com.cerberustek.geometry.*;
import com.cerberustek.geometry.impl.verticies.StaticVertexBuffer;
import com.cerberustek.logic.math.Vector3f;

import static org.lwjgl.opengl.GL15.*;

public class StaticMesh implements ModifiableMesh {

    private final StaticVertexBuffer vertexBuffer;
    private int ibo;
    private IndexBuffer indexBuffer;

    public StaticMesh() {
        vertexBuffer = new StaticVertexBuffer();
    }

    @Override
    public void genBuffers() {
        vertexBuffer.genBuffers();
        ibo = glGenBuffers();
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
    public void addVertices(Vertex[] vertices, int[] indices, boolean calcNormals) {
        if (calcNormals) {
            for (int i = 0; i < indices.length; i += 3) {
                int x = indices[i];
                int y = indices[i + 1];
                int z = indices[i + 2];

                Vector3f v1 = vertices[y].getPosition().sub(vertices[x].getPosition());
                Vector3f v2 = vertices[z].getPosition().sub(vertices[x].getPosition());
                Vector3f normal = v1.cross(v2).normalized();

                vertices[x].getNormal().set(normal.getX(), normal.getY(), normal.getZ());
                vertices[y].getNormal().set(normal.getX(), normal.getY(), normal.getZ());
                vertices[z].getNormal().set(normal.getX(), normal.getY(), normal.getZ());
            }
        }

        addVertices(vertices, indices);
    }

    @Override
    public void addVertices(Vertex[] vertices, int[] indices) {
        vertexBuffer.addVertices(vertices);
        indexBuffer = new StaticIndexBinding(ibo, 0, indices.length, ComponentType.UNSIGNED_INT);
        indexBuffer.addIndices(indices);
    }

    @Override
    public void destroy() {
        vertexBuffer.destroy();
        glDeleteBuffers(ibo);
        // Since a static mesh uses a static
        // index buffer and static index buffers
        // do not have to be destroyed, there is
        // no need in calling the destroy method
        // on the index buffer here
    }
}
