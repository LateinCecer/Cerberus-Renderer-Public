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
import com.cerberustek.resource.shader.ShaderCodeResource;
import com.cerberustek.resource.shader.ShaderResource;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.logic.math.Matrix4f;
import com.cerberustek.logic.math.Vector3f;
import com.cerberustek.pipeline.InstancedRenderScene;
import com.cerberustek.pipeline.RenderScene;
import com.cerberustek.pipeline.impl.RenderNote;
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.ShaderBoard;
import com.cerberustek.shader.code.ShaderCodeLoader;
import com.cerberustek.shader.uniform.Uniform1f;
import com.cerberustek.shader.uniform.Uniform1i;
import com.cerberustek.shader.uniform.Uniform3f;
import com.cerberustek.shader.uniform.UniformMatrix4f;
import com.cerberustek.texture.TextureBoard;
import com.cerberustek.util.LightUtil;
import com.cerberustek.window.Window;

public class InstancedSceneNote extends RenderNote {

    // reference scene note
    private final SceneNote sceneNote;

    private final ShaderResource shaderT;
    private final ShaderResource shaderR;
    private final ShaderResource shaderS;
    private final ShaderResource shaderTR;
    private final ShaderResource shaderTS;
    private final ShaderResource shaderRS;
    private final ShaderResource shaderTRS;

    private CerberusRenderer renderer;

    public InstancedSceneNote(SceneNote sceneNote) {
        this.sceneNote = sceneNote;

        CerberusRenderer renderer = getRenderer();

        ShaderBoard shaderBoard = renderer.getShaderBoard();
        ShaderCodeLoader codeLoader = shaderBoard.getShaderCodeLoader();

        ShaderCodeResource vertexSceneT = codeLoader.resourceFromName("cerberus/SceneTVer.glsl");
        ShaderCodeResource vertexSceneR = codeLoader.resourceFromName("cerberus/SceneRVer.glsl");
        ShaderCodeResource vertexSceneS = codeLoader.resourceFromName("cerberus/SceneSVer.glsl");
        ShaderCodeResource vertexSceneTR = codeLoader.resourceFromName("cerberus/SceneTRVer.glsl");
        ShaderCodeResource vertexSceneTS = codeLoader.resourceFromName("cerberus/SceneTSVer.glsl");
        ShaderCodeResource vertexSceneRS = codeLoader.resourceFromName("cerberus/SceneRSVer.glsl");
        ShaderCodeResource vertexSceneTRS = codeLoader.resourceFromName("cerberus/SceneTRSVer.glsl");

        ShaderCodeResource fragmentScene = codeLoader.resourceFromName("cerberus/SceneFrag.glsl");


        /*
        Init shaders
         */
        shaderT = new BaseShaderResource(vertexSceneT, fragmentScene, this::initShader);
        shaderR = new BaseShaderResource(vertexSceneR, fragmentScene, this::initShader);
        shaderS = new BaseShaderResource(vertexSceneS, fragmentScene, this::initShader);
        shaderTR = new BaseShaderResource(vertexSceneTR, fragmentScene, this::initShader);
        shaderTS = new BaseShaderResource(vertexSceneTS, fragmentScene, this::initShader);
        shaderRS = new BaseShaderResource(vertexSceneRS, fragmentScene, this::initShader);
        shaderTRS = new BaseShaderResource(vertexSceneTRS, fragmentScene, this::initShader);

        shaderBoard.loadShader(shaderT);
        shaderBoard.loadShader(shaderR);
        shaderBoard.loadShader(shaderS);
        shaderBoard.loadShader(shaderTR);
        shaderBoard.loadShader(shaderTS);
        shaderBoard.loadShader(shaderRS);
        shaderBoard.loadShader(shaderTRS);
    }

    private void initShader(Shader shader) {
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
    }

    @Override
    public void reinit(Window window) {
        // handled mostly by the parent scene note
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void destroy() {
        CerberusRenderer renderer = getRenderer();
        ShaderBoard shaderBoard = renderer.getShaderBoard();

        shaderBoard.deleteShader(shaderT);
        shaderBoard.deleteShader(shaderR);
        shaderBoard.deleteShader(shaderS);
        shaderBoard.deleteShader(shaderTR);
        shaderBoard.deleteShader(shaderTS);
        shaderBoard.deleteShader(shaderRS);
        shaderBoard.deleteShader(shaderTRS);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void update(double v) {
        CerberusRenderer renderer = getRenderer();
        RenderScene scene = renderer.getPipeline().getScene();
        if (!(scene instanceof InstancedRenderScene))
            return;

        TextureBoard textureBoard = renderer.getTextureBoard();
        textureBoard.bindFrameBuffer(sceneNote.getFrameBuffer());

        Matrix4f projection = sceneNote.getCamera().getCameraMatrix();
        InstancedRenderScene instancedScene = (InstancedRenderScene) scene;

        instancedScene.renderInstanced(projection, shaderT);
        instancedScene.renderInstanced(projection, shaderR);
        instancedScene.renderInstanced(projection, shaderS);
        instancedScene.renderInstanced(projection, shaderTR);
        instancedScene.renderInstanced(projection, shaderTS);
        instancedScene.renderInstanced(projection, shaderRS);
        instancedScene.renderInstanced(projection, shaderTRS);
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }
}
