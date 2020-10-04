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

package com.cerberustek.pipeline.impl;

import com.cerberustek.resource.model.ModelResource;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.geometry.DrawMode;
import com.cerberustek.geometry.GeometryBoard;
import com.cerberustek.geometry.Mesh;
import com.cerberustek.geometry.VertexBuffer;
import com.cerberustek.logic.math.Matrix4f;
import com.cerberustek.material.MaterialBoard;
import com.cerberustek.pipeline.RenderSceneBuilder;
import com.cerberustek.pipeline.Renderable;
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.uniform.UniformMatrix4f;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple scene renderer implementation
 */
public class SparseSceneBuilder implements RenderSceneBuilder {

    protected final ArrayList<VertexBuffer> vertexBuffers = new ArrayList<>();
    protected final ArrayList<Renderable> renderables = new ArrayList<>();
    /** partition length of each partition */
    protected final ArrayList<Integer> partitions = new ArrayList<>();

    private CerberusRenderer renderer;

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void render(double delta, Matrix4f mat, Shader shader) {
        if (partitions.isEmpty())
            return;

        CerberusRenderer renderer = getRenderer();
        GeometryBoard geometryBoard = renderer.getGeometryBoard();
        MaterialBoard materialBoard = renderer.getMaterialBoard();

        Iterator<Integer> itrPartition = partitions.iterator();
        Iterator<VertexBuffer> itrBuffer = vertexBuffers.iterator();

        int partitionCounter = 0;
        int partitionSize = itrPartition.next();

        geometryBoard.bindVertexBuffer(itrBuffer.next());
        for (Renderable renderable : renderables) {

            if (partitionCounter == partitionSize) {

                if (!itrPartition.hasNext())
                    return;

                partitionSize = itrPartition.next();
                geometryBoard.bindVertexBuffer(itrBuffer.next());
                partitionCounter = 0;
            }

            geometryBoard.bindIndexBuffer(renderable.getGeometry());

            if (shader.hasUniform(Renderable.MAT_PROJECTION)) {
                shader.getUniform(Renderable.MAT_PROJECTION, UniformMatrix4f.class)
                        .set(mat.mul(renderable.getWorldMatrix()))
                        .update();
            } if (shader.hasUniform(Renderable.MAT_WORLD)) {
                shader.getUniform(Renderable.MAT_WORLD, UniformMatrix4f.class)
                        .set(renderable.getWorldMatrix())
                        .update();
            } if (shader.hasUniform(Renderable.MAT_WORLD_ROTATION)) {
                shader.getUniform(Renderable.MAT_WORLD_ROTATION, UniformMatrix4f.class)
                        .set(renderable.getWorldRotationMatrix())
                        .update();
            } if (shader.hasUniform(Renderable.MAT_WORLD_SCALE)) {
                shader.getUniform(Renderable.MAT_WORLD_SCALE, UniformMatrix4f.class)
                        .set(renderable.getWorldScaleMatrix())
                        .update();
            } if (shader.hasUniform(Renderable.MAT_WORLD_TRANSLATION)) {
                shader.getUniform(Renderable.MAT_WORLD_TRANSLATION, UniformMatrix4f.class)
                        .set(renderable.getWorldTranslationMatrix())
                        .update();
            }

            materialBoard.bindMaterial(renderable.getMaterial(), shader);
            renderable.setupShader(shader);

            geometryBoard.drawMesh(DrawMode.TRIANGLES);
            partitionCounter++;
        }
    }

    @Override
    public void append(Renderable renderable) {
        ModelResource model = renderable.getGeometry();
        CerberusRenderer renderer = getRenderer();
        GeometryBoard board = renderer.getGeometryBoard();

        Mesh mesh = board.getMesh(model);
        if (mesh == null)
            return;

        VertexBuffer vertices = mesh.getVertexBuffer();
        if (!vertexBuffers.contains(vertices)) {
            // Add new vertex buffer
            vertexBuffers.add(vertices);
            renderables.add(renderable);
            partitions.add(1);
        } else {
            // Find vertex buffer index
            int index = vertexBuffers.indexOf(vertices);
            // update partition size
            int size = partitions.get(index);
            partitions.set(index, size + 1);

            for (int i = 0; i < index; i++)
                size += partitions.get(i);

            renderables.add(size, renderable);
        }
    }

    @Override
    public void clear() {
        renderables.clear();
        vertexBuffers.clear();
        partitions.clear();
    }

    @Override
    public void destroy() {
        clear();
    }

    protected CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }
}
