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
import com.cerberustek.pipeline.InstancedRenderScene;
import com.cerberustek.pipeline.RenderScene;
import com.cerberustek.pipeline.Renderable;
import com.cerberustek.pipeline.impl.RenderNote;
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.ShaderBoard;
import com.cerberustek.shader.code.ShaderCodeLoader;
import com.cerberustek.shader.uniform.UniformMatrix4f;
import com.cerberustek.window.Window;

public class InstancedShadowMapNote extends RenderNote {

    private final ShadowMapNote note;

    private final ShaderResource shaderT;
    private final ShaderResource shaderR;
    private final ShaderResource shaderS;
    private final ShaderResource shaderTR;
    private final ShaderResource shaderTS;
    private final ShaderResource shaderRS;
    private final ShaderResource shaderTRS;

    private CerberusRenderer renderer;

    public InstancedShadowMapNote(ShadowMapNote note) {
        this.note = note;

        CerberusRenderer renderer = getRenderer();
        ShaderBoard shaderBoard = renderer.getShaderBoard();
        ShaderCodeLoader codeLoader = shaderBoard.getShaderCodeLoader();

        ShaderCodeResource vertexT = codeLoader.resourceFromName("lighting/ShadowMapTVer.glsl");
        ShaderCodeResource vertexR = codeLoader.resourceFromName("lighting/ShadowMapRVer.glsl");
        ShaderCodeResource vertexS = codeLoader.resourceFromName("lighting/ShadowMapSVer.glsl");
        ShaderCodeResource vertexTR = codeLoader.resourceFromName("lighting/ShadowMapTRVer.glsl");
        ShaderCodeResource vertexTS = codeLoader.resourceFromName("lighting/ShadowMapTSVer.glsl");
        ShaderCodeResource vertexRS = codeLoader.resourceFromName("lighting/ShadowMapRSVer.glsl");
        ShaderCodeResource vertexTRS = codeLoader.resourceFromName("lighting/ShadowMapTRSVer.glsl");

        ShaderCodeResource fragment = codeLoader.resourceFromName("lighting/ShadowMapFrag.glsl");


        shaderT = new BaseShaderResource(vertexT, fragment, this::initShader);
        shaderR = new BaseShaderResource(vertexR, fragment, this::initShader);
        shaderS = new BaseShaderResource(vertexS, fragment, this::initShader);
        shaderTR = new BaseShaderResource(vertexTR, fragment, this::initShader);
        shaderTS = new BaseShaderResource(vertexTS, fragment, this::initShader);
        shaderRS = new BaseShaderResource(vertexRS, fragment, this::initShader);
        shaderTRS = new BaseShaderResource(vertexTRS, fragment, this::initShader);

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
            shader.addUniform(new UniformMatrix4f(shader, Renderable.MAT_PROJECTION, new Matrix4f().initIdentity()));
            shader.addUniform(new UniformMatrix4f(shader, Renderable.MAT_WORLD, new Matrix4f().initIdentity()));
        } catch (GLUnknownUniformException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reinit(Window window) {
        // handled by parent shadow map note
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
        if (note == null || note.getCamera() == null)
            return;

        CerberusRenderer renderer = getRenderer();
        RenderScene scene = renderer.getPipeline().getScene();
        if (!(scene instanceof InstancedRenderScene))
            return;

        renderer.getTextureBoard().bindFrameBuffer(note.getFrameBuffer());
        InstancedRenderScene instancedScene = (InstancedRenderScene) scene;
        Matrix4f cameraMatrix = note.getCamera().getCameraMatrix();

        instancedScene.renderInstanced(cameraMatrix, shaderT);
        instancedScene.renderInstanced(cameraMatrix, shaderR);
        instancedScene.renderInstanced(cameraMatrix, shaderS);
        instancedScene.renderInstanced(cameraMatrix, shaderTR);
        instancedScene.renderInstanced(cameraMatrix, shaderTS);
        instancedScene.renderInstanced(cameraMatrix, shaderRS);
        instancedScene.renderInstanced(cameraMatrix, shaderTRS);
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }
}
