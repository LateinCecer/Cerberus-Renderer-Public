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
import com.cerberustek.pipeline.InputProvider;
import com.cerberustek.pipeline.impl.RenderNote;
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.ShaderBoard;
import com.cerberustek.shader.code.ShaderCodeLoader;
import com.cerberustek.shader.uniform.Uniform1f;
import com.cerberustek.shader.uniform.Uniform1i;
import com.cerberustek.window.Window;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class ShadowMergeNote extends RenderNote implements InputProvider {

    private CerberusRenderer renderer;
    private final ShaderResource shader;
    private final InputProvider frameBuffer;
    private final DisplayNote displayNote;

    public ShadowMergeNote(InputProvider shadowMap, InputProvider normalTexture,
                           InputProvider cameraSpacePosAndDepth, InputProvider screenTexture) {
        ShaderBoard shaderBoard = getRenderer().getShaderBoard();
        ShaderCodeLoader codeLoader = shaderBoard.getShaderCodeLoader();
        ShaderCodeResource vertexShader = codeLoader.resourceFromName("lighting/ShadowMergeVer.glsl");
        ShaderCodeResource fragmentShader = codeLoader.resourceFromName("lighting/ShadowMergeFrag.glsl");

        shader = new BaseShaderResource(vertexShader, fragmentShader, this::setupShader);
        shaderBoard.loadShader(shader);
        frameBuffer = screenTexture;
        displayNote = new DisplayNote(shader, shadowMap, screenTexture, normalTexture, screenTexture,
                cameraSpacePosAndDepth);
    }

    protected void setupShader(Shader shader) {
        try {
            shader.addUniform(new Uniform1i(shader, "ColorTexture", SceneNote.COLOR));
            shader.addUniform(new Uniform1i(shader, "ShadowTexture", ScreenSpaceShadowNote.SHADOW));
            shader.addUniform(new Uniform1i(shader, "NormalTexture", SceneNote.NORMAL));
            shader.addUniform(new Uniform1i(shader, "CameraSpacePosition", SceneNote.POSDEPTH));

            shader.addUniform(new Uniform1f(shader, "SoftShadowFactor", 0.0025f));
            shader.addUniform(new Uniform1f(shader, "SoftShaderDropoff", -0.005f));
            shader.addUniform(new Uniform1f(shader, "maxShadow", 0.85f));
        } catch (GLUnknownUniformException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(double v) {
        getRenderer().getTextureBoard().bindFrameBuffer((FrameBufferResource) frameBuffer.fetchOutput());
        glDrawBuffers(GL_COLOR_ATTACHMENT0 + SceneNote.COLOR);
        displayNote.update(v);
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }

    @Override
    public void destroy() {
        displayNote.destroy();
        getRenderer().getShaderBoard().deleteShader(shader);
    }

    @Override
    public FrameBufferResource fetchOutput() {
        return (FrameBufferResource) frameBuffer.fetchOutput();
    }

    @Override
    public void reinit(Window window) {
        ShaderBoard shaderBoard = getRenderer().getShaderBoard();
        Shader shader = shaderBoard.getShader(this.shader);

        if (shader != null) {
            shader.getUniform("SoftShadowFactor", Uniform1f.class).set(0.0025f);
            shader.getUniform("maxShadow", Uniform1f.class).set(0.85f);
        }
    }
}
