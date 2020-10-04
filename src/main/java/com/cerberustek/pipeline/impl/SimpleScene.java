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

import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.geometry.DrawMode;
import com.cerberustek.geometry.GeometryBoard;
import com.cerberustek.logic.math.Matrix4f;
import com.cerberustek.material.MaterialBoard;
import com.cerberustek.pipeline.RenderScene;
import com.cerberustek.pipeline.Renderable;
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.uniform.UniformMatrix4f;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;

public class SimpleScene implements RenderScene {

    protected final HashSet<Renderable> renderables = new HashSet<>();

    private CerberusRenderer renderer;

    @Override
    public void updateMatrices(double delta) {
        renderables.forEach(renderable -> renderable.updateMatrices(delta, null));
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void render(Matrix4f projectionMatrix, Shader shader) {

        CerberusRenderer renderer = getRenderer();
        GeometryBoard geometryBoard = renderer.getGeometryBoard();
        MaterialBoard materialBoard = renderer.getMaterialBoard();

        // uncomment this, if there framebuffer becomes unbound while rendering for
        // some reason
        /*
        FrameBufferResource frameBufferResource = (FrameBufferResource) getRenderer().getTextureBoard().getBoundFrameBuffer();
        if (frameBufferResource == null) {
            CerberusRegistry.getInstance().warning("Rendering scene without framebuffer!\n" +
                    Arrays.toString(Thread.currentThread().getStackTrace()));
            return;
        }

        FrameBuffer frameBuffer = (FrameBuffer) getRenderer().getTextureBoard().getTexture(frameBufferResource);
        frameBuffer.bindFrameBuffer();
        frameBuffer.drawToBuffers();*/


        for (Renderable renderable : renderables) {
            geometryBoard.bindMesh(renderable.getGeometry());

            if (shader.hasUniform(Renderable.MAT_PROJECTION))
                shader.getUniform(Renderable.MAT_PROJECTION, UniformMatrix4f.class)
                        .set(projectionMatrix.mul(renderable.getWorldMatrix()))
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

            materialBoard.bindMaterial(renderable.getMaterial(), shader);
            renderable.setupShader(shader);

            geometryBoard.drawMesh(DrawMode.TRIANGLES);
        }
    }

    @Override
    public boolean contains(Renderable renderable) {
        return renderables.contains(renderable);
    }

    @Override
    public void addRenderable(Renderable renderable) {
        renderables.add(renderable);
    }

    @Override
    public void removeRenderable(Renderable renderable) {
        renderables.remove(renderable);
    }

    @Override
    public Collection<Renderable> getRenderables() {
        return renderables;
    }

    @Override
    public void destroy() {
        try {
            renderables.forEach(Renderable::destroy);
        } catch (ConcurrentModificationException e) {
            destroy();
        } finally {
            renderables.clear();
        }
    }

    @Override
    public void update(double delta) {
        renderables.forEach(renderable -> renderable.update(delta, this));
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }
}
