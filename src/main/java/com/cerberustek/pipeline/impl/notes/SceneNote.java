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
import com.cerberustek.logic.math.Vector3f;
import com.cerberustek.pipeline.InputProvider;
import com.cerberustek.pipeline.impl.RenderNote;
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.ShaderBoard;
import com.cerberustek.shader.code.ShaderCodeLoader;
import com.cerberustek.shader.uniform.Uniform1f;
import com.cerberustek.shader.uniform.Uniform1i;
import com.cerberustek.shader.uniform.Uniform3f;
import com.cerberustek.shader.uniform.UniformMatrix4f;
import com.cerberustek.texture.AttachmentType;
import com.cerberustek.texture.ImageType;
import com.cerberustek.texture.TextureBoard;
import com.cerberustek.texture.impl.SimpleAttachment;
import com.cerberustek.texture.impl.TextureEmpty2D;
import com.cerberustek.util.LightUtil;
import com.cerberustek.util.RenderUtil;
import com.cerberustek.window.Window;

public class SceneNote extends RenderNote implements InputProvider {

    public final static int COLOR = 0;
    public final static int NORMAL = 1;
    public final static int SPECULAR = 2;
    public final static int EMISSION = 3;
    public final static int METALLIC = 4;
    public final static int DISPLACEMENT = 5;
    public final static int POSDEPTH = 6;

    private FrameBufferResource frameBuffer;
    private final ShaderResource sceneShader;

    private CerberusRenderer renderer;
    private Camera camera;

    public SceneNote(Camera camera) {
        this.camera = camera;

        CerberusRenderer renderer = getRenderer();
        Window window = renderer.getWindow();
        frameBuffer = setupFramebuffer(null, window.getScreenSize());

        ShaderBoard shaderBoard = renderer.getShaderBoard();
        ShaderCodeLoader codeLoader = shaderBoard.getShaderCodeLoader();

        ShaderCodeResource vertexScene = codeLoader.resourceFromName("cerberus/SceneVer.glsl");
        ShaderCodeResource fragmentScene = codeLoader.resourceFromName("cerberus/SceneFrag.glsl");

        sceneShader = new BaseShaderResource(
                vertexScene,
                fragmentScene, (shader -> {
            try {
                shader.addUniform(new UniformMatrix4f(shader, "world_matrix", new Matrix4f().initIdentity()));
                shader.addUniform(new UniformMatrix4f(shader, "world_rotation_matrix", new Matrix4f().initIdentity()));
                shader.addUniform(new UniformMatrix4f(shader, "projection", new Matrix4f().initIdentity()));

                shader.addUniform(new Uniform1i(shader, "colorMap", 0));
                shader.addUniform(new Uniform1i(shader, "normalMap", 1));
                shader.addUniform(new Uniform1i(shader, "specularMap", 2));
                shader.addUniform(new Uniform1i(shader, "glowMap", 3));
                shader.addUniform(new Uniform1i(shader, "metallicMap", 4));
                shader.addUniform(new Uniform1i(shader, "roughnessMap", 5));
                shader.addUniform(new Uniform1i(shader, "displacementMap", 6));

                shader.addUniform(new Uniform3f(shader, "colorMod", LightUtil.warmthToRGB(7000).mul(1.5f)));
                shader.addUniform(new Uniform3f(shader, "glowMod", new Vector3f(0.4f, 0.04f, 0.04f)));
                shader.addUniform(new Uniform3f(shader, "specularMod", new Vector3f(1, 1, 1)));
                shader.addUniform(new Uniform1f(shader, "metallicMod", 1f));
                shader.addUniform(new Uniform1f(shader, "roughnessMod", 1f));
                shader.addUniform(new Uniform1f(shader, "displacementMod", 0.01f));
            } catch (GLUnknownUniformException e) {
                e.printStackTrace();
            }
        }));

        shaderBoard.loadShader(sceneShader);
        renderer.getTextureBoard().loadTexture(frameBuffer);
    }

    public void setDisplacement(float displacement) {
        Shader shader = getShader();
        if (shader != null)
            shader.getUniform("displacementMod", Uniform1f.class).set(displacement);
    }

    public float getDisplacement() {
        Shader shader = getShader();
        if (shader != null)
            return shader.getUniform("displacementMod", Uniform1f.class).get();
        return 0;
    }

    public void setRoughness(float roughness) {
        Shader shader = getShader();
        if (shader != null)
            shader.getUniform("roughnessMod", Uniform1f.class).set(roughness);
    }

    public float getRoughness() {
        Shader shader = getShader();
        if (shader != null)
            return shader.getUniform("roughnessMod", Uniform1f.class).get();
        return 0;
    }

    public void setMetallic(float metallic) {
        Shader shader = getShader();
        if (shader != null)
            shader.getUniform("metallicMod", Uniform1f.class).set(metallic);
    }

    public float getMetallic() {
        Shader shader = getShader();
        if (shader != null)
            return shader.getUniform("metallicMod", Uniform1f.class).get();
        return 0;
    }

    public void setSpecular(Vector3f specular) {
        Shader shader = getShader();
        if (shader != null)
            shader.getUniform("specularMod", Uniform3f.class).set(specular);
    }

    public Vector3f getSpecular() {
        Shader shader = getShader();
        if (shader != null)
            return shader.getUniform("specularMod", Uniform3f.class).get();
        return null;
    }

    public void setGlow(Vector3f glow) {
        Shader shader = getShader();
        if (shader != null)
            shader.getUniform("glowMod", Uniform3f.class).set(glow);
    }

    public Vector3f getGlow() {
        Shader shader = getShader();
        if (shader != null)
            return shader.getUniform("glowMod", Uniform3f.class).get();
        return null;
    }

    public void setColor(Vector3f color) {
        Shader shader = getShader();
        if (shader != null)
            shader.getUniform("colorMod", Uniform3f.class).set(color);
    }

    public Vector3f getColor() {
        Shader shader = getShader();
        if (shader != null)
            return shader.getUniform("colorMod", Uniform3f.class).get();
        return null;
    }

    @SuppressWarnings("Duplicates")
    private Shader getShader() {
        CerberusRenderer renderer = getRenderer();
        Shader output = renderer.getShaderBoard().getShader(sceneShader);
        if (output == null) {
            renderer.getShaderBoard().loadShader(sceneShader);
            output = renderer.getShaderBoard().getShader(sceneShader);
        }
        return output;
    }

    @Override
    public void destroy() {
        CerberusRenderer renderer = getRenderer();

        renderer.getShaderBoard().deleteShader(sceneShader);
        renderer.getTextureBoard().deleteTexture(frameBuffer);
    }

    @Override
    public void update(double v) {
        CerberusRenderer renderer = getRenderer();
        renderer.getTextureBoard().bindFrameBuffer(frameBuffer);

        /*for (TextureResource texture : sceneTextures)
            renderer.getTextureBoard().bindTexture(texture);*/

        RenderUtil.clear();
        Shader s = renderer.getShaderBoard().bindShader(sceneShader);
        if (s != null)
            renderer.getPipeline().getScene().render(camera.getCameraMatrix(), s);
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }

    private FrameBufferResource setupFramebuffer(FrameBufferResource frameBuffer, Vector2i screenSize) {
        if (frameBuffer != null)
            getRenderer().getTextureBoard().deleteTexture(frameBuffer);

        TextureEmpty2D textures = new TextureEmpty2D(7);
        textures.genTextures();

        // ## Primary screen texture ##
        // Textured, though not lighten up scene pixels
        textures.initTexture(0, COLOR, screenSize, ImageType.RGBA_32_FLOAT);
        // ## camera view normal texture ##
        textures.initTexture(1, NORMAL, screenSize, ImageType.RGBA_16_INTEGER);
        // ## camera view specular texture ##
        textures.initTexture(2, SPECULAR, screenSize, ImageType.RGBA_16_INTEGER);
        // ## camera view emission texture ##
        textures.initTexture(3, EMISSION, screenSize, ImageType.RGBA_16_INTEGER);
        // ## camera view metallic texture ##
        textures.initTexture(4, METALLIC, screenSize, ImageType.RGBA_16_INTEGER);
        // ## camera view depth offset texture ##
        textures.initTexture(5, DISPLACEMENT, screenSize, ImageType.RGBA_32_FLOAT);
        // ## Scene camera space pos and depth texture ##
        textures.initTexture(6, POSDEPTH, screenSize, ImageType.RGBA_32_FLOAT);
        // textures.initTexture(6, 6, screenSize, ImageType.DEPTH_32_FLOAT);

        return new FrameBufferResource(screenSize, textures, true,
                new SimpleAttachment(0, AttachmentType.COLOR_00),
                new SimpleAttachment(1, AttachmentType.COLOR_01),
                new SimpleAttachment(2, AttachmentType.COLOR_02),
                new SimpleAttachment(3, AttachmentType.COLOR_03),
                new SimpleAttachment(4, AttachmentType.COLOR_04),
                new SimpleAttachment(5, AttachmentType.COLOR_05),
                new SimpleAttachment(6, AttachmentType.COLOR_06));
            // new SimpleAttachment(6, AttachmentType.DEPTH));
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    @Override
    public FrameBufferResource fetchOutput() {
        return frameBuffer;
    }

    @Deprecated
    public FrameBufferResource getFrameBuffer() {
        return frameBuffer;
    }

    @Override
    public void reinit(Window window) {
        TextureBoard textureBoard = getRenderer().getTextureBoard();
        // textureBoard.deleteTexture(frameBuffer);

        frameBuffer = setupFramebuffer(frameBuffer, window.getScreenSize());
        textureBoard.loadTexture(frameBuffer);
    }
}
