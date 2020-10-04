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

package com.cerberustek.pipeline.impl.notes;

import com.cerberustek.exceptions.GLUnknownUniformException;
import com.cerberustek.resource.impl.BaseShaderResource;
import com.cerberustek.resource.impl.FrameBufferResource;
import com.cerberustek.resource.shader.ShaderCodeResource;
import com.cerberustek.resource.shader.ShaderResource;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.camera.Camera;
import com.cerberustek.logic.math.Matrix4f;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.logic.math.Vector3i;
import com.cerberustek.pipeline.InputProvider;
import com.cerberustek.pipeline.RenderScene;
import com.cerberustek.pipeline.impl.RenderNote;
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.ShaderBoard;
import com.cerberustek.shader.code.ShaderCodeLoader;
import com.cerberustek.shader.uniform.UniformMatrix4f;
import com.cerberustek.texture.AttachmentType;
import com.cerberustek.texture.ImageType;
import com.cerberustek.texture.TextureBoard;
import com.cerberustek.texture.impl.SimpleAttachment;
import com.cerberustek.texture.impl.TextureEmpty2D;
import com.cerberustek.util.RenderUtil;
import com.cerberustek.window.Window;

public class DepthNote extends RenderNote implements InputProvider {

    public final static int DEPTH = 6;

    private FrameBufferResource frameBuffer;
    private final ShaderResource shader;
    private final Camera camera;

    private CerberusRenderer renderer;

    public DepthNote(Camera camera) {
        this.camera = camera;
        CerberusRenderer renderer = getRenderer();
        ShaderBoard shaderBoard = renderer.getShaderBoard();
        ShaderCodeLoader codeLoader = shaderBoard.getShaderCodeLoader();

        ShaderCodeResource vertexDepth = codeLoader.resourceFromName("cerberus/DepthFrag.glsl");
        ShaderCodeResource fragmentDepth = codeLoader.resourceFromName("cerberus/DepthVer.glsl");

        shader = new BaseShaderResource(vertexDepth, fragmentDepth, shader -> {
            try {
                shader.addUniform(new UniformMatrix4f(shader, "projection", new Matrix4f().initIdentity()));
            } catch (GLUnknownUniformException e) {
                CerberusRegistry.getInstance().critical("Failed to load depth render shader!");
            }
        });

        frameBuffer = initFrameBuffer(null, getRenderer().getWindow().getSize());

        shaderBoard.loadShader(shader);
        renderer.getTextureBoard().loadTexture(frameBuffer);
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }

    private FrameBufferResource initFrameBuffer(FrameBufferResource frameBuffer, Vector2i size) {
        if (frameBuffer != null)
            getRenderer().getTextureBoard().deleteTexture(frameBuffer);

        TextureEmpty2D depthTexture = new TextureEmpty2D(1);
        depthTexture.genTextures();
        depthTexture.initTexture(0, DEPTH, size, ImageType.DEPTH_32_FLOAT);

        Vector3i textureSize = depthTexture.getSize(0);
        return new FrameBufferResource(new Vector2i(textureSize.getX(), textureSize.getY()), depthTexture, false,
                new SimpleAttachment(0, AttachmentType.DEPTH));
    }

    @Override
    public void destroy() {
        CerberusRenderer renderer = getRenderer();
        renderer.getShaderBoard().deleteShader(shader);
        renderer.getTextureBoard().deleteTexture(frameBuffer);
    }

    @Override
    public void update(double v) {
        getRenderer().getTextureBoard().bindFrameBuffer(frameBuffer);
        RenderUtil.clear();

        RenderScene scene = renderer.getPipeline().getScene();
        Shader s = renderer.getShaderBoard().bindShader(shader);
        if (s != null)
            scene.render(camera.getProjectionMatrix(), s);
    }

    @Override
    public void reinit(Window window) {
        TextureBoard textureBoard = getRenderer().getTextureBoard();
        // textureBoard.deleteTexture(frameBuffer);

        frameBuffer = initFrameBuffer(frameBuffer, window.getScreenSize());
        textureBoard.loadTexture(frameBuffer);
    }

    @Override
    public FrameBufferResource fetchOutput() {
        return frameBuffer;
    }
}
