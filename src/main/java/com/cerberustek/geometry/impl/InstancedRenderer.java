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

import com.cerberustek.geometry.impl.verticies.OrphanVertexDivisorBuffer;
import com.cerberustek.material.MaterialBoard;
import com.cerberustek.pipeline.Renderable;
import com.cerberustek.resource.impl.OrphanVertexDivisorResource;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.geometry.DrawMode;
import com.cerberustek.geometry.GeometryBoard;
import com.cerberustek.logic.math.Matrix4f;
import com.cerberustek.logic.math.Vector3f;
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.uniform.UniformMatrix4f;
import com.cerberustek.util.BufferUtil;

import java.nio.FloatBuffer;
import java.util.Collection;

public class InstancedRenderer {

    private final OrphanVertexDivisorResource translation = new OrphanVertexDivisorResource();

    private final Renderable renderable;

    private FloatBuffer positionBuffer;

    private int primitives = 0;
    private CerberusRenderer renderer;

    public InstancedRenderer(Renderable renderable) {
        this.renderable = renderable;
    }

    public void setTranslation(Collection<Vector3f> pos) {
        positionBuffer = BufferUtil.createFlippedBuffer(pos);
    }

    public void setTranslation(FloatBuffer data) {
        this.positionBuffer = data;
    }

    public void draw(DrawMode mode, Matrix4f mat, Shader shader) {
        CerberusRenderer renderer = getRenderer();

        renderable.updateMatrices(0, null);

        if (shader.hasUniform(Renderable.MAT_PROJECTION))
            shader.getUniform(Renderable.MAT_PROJECTION, UniformMatrix4f.class)
                    .set(mat.mul(renderable.getWorldMatrix()))
                    .update();
        if (shader.hasUniform(Renderable.MAT_WORLD))
            shader.getUniform(Renderable.MAT_WORLD, UniformMatrix4f.class)
                    .set(renderable.getWorldMatrix())
                    .update();
        if (shader.hasUniform(Renderable.MAT_WORLD_ROTATION))
            shader.getUniform(Renderable.MAT_WORLD_ROTATION, UniformMatrix4f.class)
                    .set(renderable.getWorldRotationMatrix())
                    .update();
        if (shader.hasUniform(Renderable.MAT_WORLD_SCALE))
            shader.getUniform(Renderable.MAT_WORLD_SCALE, UniformMatrix4f.class)
                    .set(renderable.getWorldScaleMatrix())
                    .update();
        if (shader.hasUniform(Renderable.MAT_WORLD_TRANSLATION))
            shader.getUniform(Renderable.MAT_WORLD_TRANSLATION, UniformMatrix4f.class)
                    .set(renderable.getWorldTranslationMatrix())
                    .update();

        GeometryBoard board = renderer.getGeometryBoard();
        MaterialBoard materialBoard = renderer.getMaterialBoard();

        materialBoard.bindMaterial(renderable.getMaterial(), shader);
        renderable.setupShader(shader);

        board.bindMesh(renderable.getGeometry());
        board.bindVertexAttribute(translation, GeometryBoard.TRANSLATION);
        // board.drawMesh(DrawMode.TRIANGLES);
        board.drawMeshInstanced(mode, primitives);

        // update translations
        if (positionBuffer != null) {
            OrphanVertexDivisorBuffer switchBuffer = (OrphanVertexDivisorBuffer) board.getVertexAttribute(translation);
            if (switchBuffer == null)
                return;

            switchBuffer.load(positionBuffer, 0, 12, 0, 1);
            primitives = positionBuffer.capacity() / 3;
            positionBuffer.clear();
            positionBuffer = null;
        }
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }
}
