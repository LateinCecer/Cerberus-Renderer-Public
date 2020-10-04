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

package com.cerberustek.geometry.impl.verticies;

import com.cerberustek.geometry.GeometryBoard;
import com.cerberustek.geometry.Vertex;
import com.cerberustek.geometry.VertexBuffer;
import com.cerberustek.resource.impl.BoundVertexAttribResource;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.buffer.BufferUsage;
import com.cerberustek.buffer.GlBufferObject;
import com.cerberustek.buffer.GlBufferTarget;
import com.cerberustek.buffer.impl.SimpleGlBufferObject;
import com.cerberustek.util.BufferUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;

public class StaticVertexBuffer implements VertexBuffer {

    private GlBufferObject pointer;
    private int vertexSize;
    private boolean normals;

    private BoundVertexAttribResource vertexBinding;
    private BoundVertexAttribResource texBinding;
    private BoundVertexAttribResource normalBinding;

    private CerberusRenderer renderer;

    public StaticVertexBuffer() {
        vertexSize = 0;
    }

    public void genBuffers() {
        pointer = new SimpleGlBufferObject(GlBufferTarget.ARRAY, glGenBuffers());
    }

    public void addVertices(Vertex[] vertices) {
        normals = vertices[0].getNormal() != null;
        FloatBuffer vertexBuffer = BufferUtil.createVertexBuffer(vertices, normals);
        vertexSize = vertices[0].sizeof();

//        glBindBuffer(GL_ARRAY_BUFFER, vbo);
//        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        pointer.bind();
        pointer.bufferData(vertexBuffer, BufferUsage.STATIC_DRAW);

        vertexBinding = new BoundVertexAttribResource(pointer, 0, vertexSize, 0);
        texBinding = new BoundVertexAttribResource(pointer, 0, vertexSize, 12);
        if (normals)
            normalBinding = new BoundVertexAttribResource(pointer, 0, vertexSize, 20);
    }

    @Override
    public void destroy() {
        GeometryBoard board = getRenderer().getGeometryBoard();

        board.deleteVertexAttribute(vertexBinding);
        board.deleteVertexAttribute(texBinding);
        if (normals)
            board.deleteVertexAttribute(normalBinding);

        pointer.destroy();
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void bind() {
        GeometryBoard board = getRenderer().getGeometryBoard();

        board.bindVertexAttribute(vertexBinding, GeometryBoard.VERTEX);
        board.bindVertexAttribute(texBinding, GeometryBoard.TEXCOORD_0);
        if (normals)
            board.bindVertexAttribute(normalBinding, GeometryBoard.NORMAL_0);
    }

    @Override
    public void unbind() {

    }

    @Override
    public GlBufferObject bufferObject() {
        return pointer;
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }
}
