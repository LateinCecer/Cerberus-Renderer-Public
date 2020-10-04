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

import com.cerberustek.buffer.BufferUsage;
import com.cerberustek.buffer.GlBufferObject;
import com.cerberustek.buffer.GlBufferTarget;
import com.cerberustek.buffer.impl.SimpleGlBufferObject;
import com.cerberustek.geometry.Vertex;
import com.cerberustek.geometry.VertexBuffer;
import com.cerberustek.util.BufferUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

public class OldStaticVertexBuffer implements VertexBuffer {

    private GlBufferObject pointer;
    private int vertexSize;
    private boolean normals;

    public OldStaticVertexBuffer() {
        vertexSize = 0;
        pointer = null;
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
    }

    @Override
    public void destroy() {
        pointer.destroy();
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void bind() {
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        if (normals)
            glEnableVertexAttribArray(2);

        pointer.bind();

        glVertexAttribPointer(0, 3, GL_FLOAT, false, vertexSize, 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, vertexSize, 12);
        if (normals)
            glVertexAttribPointer(2, 3, GL_FLOAT, false, vertexSize, 20);
    }

    @Override
    public void unbind() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
    }

    @Override
    public GlBufferObject bufferObject() {
        return pointer;
    }
}
