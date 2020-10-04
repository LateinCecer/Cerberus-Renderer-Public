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
import com.cerberustek.logic.math.Matrix4f;
import com.cerberustek.pipeline.RenderSceneBuilder;
import com.cerberustek.pipeline.Renderable;
import com.cerberustek.pipeline.SparseRenderScene;
import com.cerberustek.shader.Shader;

public class SimpleSparseScene extends SimpleScene implements SparseRenderScene {

    private final RenderSceneBuilder builder;

    private CerberusRenderer renderer;

    public SimpleSparseScene() {
        builder = new SparseSceneBuilder();
    }

    @Override
    public void render(Matrix4f projectionMatrix, Shader shader) {
        // super.render(projectionMatrix, shader);

        // uncomment this if there are some changes to the rendering pipeline that somehow cause the rendering
        // engine to unbind framebuffers
        /*FrameBufferResource frameBufferResource = (FrameBufferResource) textureBoard.getBoundFrameBuffer();
        if (frameBufferResource == null) {
            CerberusRegistry.getInstance().warning("Rendering scene without framebuffer!\n" +
                    Arrays.toString(Thread.currentThread().getStackTrace()));
            return;
        }

        FrameBuffer frameBuffer = (FrameBuffer) textureBoard.getTexture(frameBufferResource);
        frameBuffer.bindFrameBuffer();
        frameBuffer.drawToBuffers();*/

        builder.render(0, projectionMatrix, shader);
    }

    @Override
    public void destroy() {
        super.destroy();
        builder.destroy();
    }

    @Override
    public void update(double v) {
        builder.clear();
        super.update(v);
    }

    public RenderSceneBuilder builder() {
        return builder;
    }

    @Override
    public void appendToParse(Renderable renderable) {
        builder.append(renderable);

        /*
        Uncomment this, if child renderables are forbidden
        if (contains(renderable))
            builder.append(renderable);
        else
            throw new IllegalArgumentException("Renderable \"" + renderable + "\" is not part of the scene!");*/
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }
}
