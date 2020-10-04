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
import com.cerberustek.logic.math.Vector2f;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.pipeline.InputProvider;
import com.cerberustek.pipeline.Renderable;
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

public class ShadowMapNote extends RenderNote implements InputProvider {

    public final static int LIGHTPOSDEPTH = 11;

    private FrameBufferResource frameBuffer;
    private final ShaderResource shader;

    private Vector2f size;
    private Camera camera;
    private CerberusRenderer renderer;

    public ShadowMapNote(Camera lightCamera, Vector2f size) {
        this.camera = lightCamera;

        CerberusRenderer renderer = getRenderer();
        ShaderBoard shaderBoard = renderer.getShaderBoard();
        ShaderCodeLoader codeLoader = shaderBoard.getShaderCodeLoader();

        ShaderCodeResource vertex = codeLoader.resourceFromName("lighting/ShadowMapVer.glsl");
        ShaderCodeResource fragment = codeLoader.resourceFromName("lighting/ShadowMapFrag.glsl");

        this.shader = new BaseShaderResource(
                vertex, fragment, shader -> {
                    try {
                        shader.addUniform(new UniformMatrix4f(shader, Renderable.MAT_PROJECTION, new Matrix4f().initIdentity()));
                        shader.addUniform(new UniformMatrix4f(shader, Renderable.MAT_WORLD, new Matrix4f().initIdentity()));
                    } catch (GLUnknownUniformException e) {
                        e.printStackTrace();
                    }
        });

        this.size = size;
        frameBuffer = initFrameBuffer(null,
                getRenderer().getWindow().getScreenSize().toVector2f().mul(size).toVec2i());

        shaderBoard.loadShader(shader);
        renderer.getTextureBoard().loadTexture(frameBuffer);
    }

    private FrameBufferResource initFrameBuffer(FrameBufferResource frameBuffer, Vector2i size) {
        if (frameBuffer != null)
            getRenderer().getTextureBoard().deleteTexture(frameBuffer);

        TextureEmpty2D texture = new TextureEmpty2D(1);
        texture.genTextures();
        texture.initTexture(0, LIGHTPOSDEPTH, size, ImageType.RGBA_32_FLOAT);

        return new FrameBufferResource(size, texture, true,
                new SimpleAttachment(0, AttachmentType.COLOR_00));
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Vector2f getSize() {
        return size;
    }

    public void setSize(Vector2f size) {
        this.size = size;
    }

    @Override
    public void destroy() {
        CerberusRenderer renderer = getRenderer();
        renderer.getTextureBoard().deleteTexture(frameBuffer);
        renderer.getShaderBoard().deleteShader(shader);
    }

    @Override
    public void update(double v) {
        if (camera == null)
            return;

        CerberusRenderer renderer = getRenderer();
        renderer.getTextureBoard().bindFrameBuffer(frameBuffer);
        RenderUtil.clear();
        Shader s = renderer.getShaderBoard().bindShader(shader);

        if (s != null)
            renderer.getPipeline().getScene().render(camera.getCameraMatrix(), s);
    }

    @Deprecated
    public FrameBufferResource getFrameBuffer() {
        return frameBuffer;
    }

    @Override
    public void reinit(Window window) {
        TextureBoard textureBoard = getRenderer().getTextureBoard();
        // textureBoard.deleteTexture(frameBuffer);

        frameBuffer = initFrameBuffer(frameBuffer, window.getScreenSize().toVector2f().mul(size).toVec2i());
        textureBoard.loadTexture(frameBuffer);
    }

    @Override
    public FrameBufferResource fetchOutput() {
        return frameBuffer;
    }
}
