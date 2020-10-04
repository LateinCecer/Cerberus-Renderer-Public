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
import com.cerberustek.resource.impl.FrameBufferResource;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.logic.math.Matrix4f;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.logic.math.Vector3f;
import com.cerberustek.pipeline.InputProvider;
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.uniform.Uniform1f;
import com.cerberustek.shader.uniform.Uniform1i;
import com.cerberustek.shader.uniform.Uniform3f;
import com.cerberustek.shader.uniform.UniformMatrix4f;
import com.cerberustek.texture.AttachmentType;
import com.cerberustek.texture.ImageType;
import com.cerberustek.texture.impl.SimpleAttachment;
import com.cerberustek.texture.impl.TextureEmpty2D;

public class ScreenSpaceShadowNote extends PostProcessingNote {

    public final static int SHADOW = 12;

    private final ShadowMapNote[] lightViewMaps;

    public ScreenSpaceShadowNote(InputProvider normalTexture, ShadowMapNote... lightViewMap) {
        super("lighting/ScreenSpaceShadowVer.glsl", "lighting/ScreenSpaceShadowFrag.glsl",
                normalTexture);
        this.lightViewMaps = lightViewMap;
    }

    @Override
    protected void setupShader(Shader shader) {
        try {
            shader.addUniform(new Uniform1i(shader, "ShadowTexture", SHADOW));
            shader.addUniform(new Uniform1i(shader, "NormalTexture", SceneNote.NORMAL));
            shader.addUniform(new Uniform1i(shader, "SceneCameraSpacePosAndDepthTexture", SceneNote.POSDEPTH));
            shader.addUniform(new Uniform1i(shader, "SceneCameraSpacePosAndDepthTexture_LightView", ShadowMapNote.LIGHTPOSDEPTH));

            shader.addUniform(new UniformMatrix4f(shader, "matViewProjection_LightView", new Matrix4f().initIdentity()));
            shader.addUniform(new Uniform3f(shader, "LightDir", new Vector3f(-1, 0, 0)));

            shader.addUniform(new Uniform1f(shader, "ShadowOffset", 0.5f));

            float range = 300;
            shader.addUniform(new Uniform1f(shader, "ShadowRange", range));
            shader.addUniform(new Uniform1f(shader, "ShadowRangeSq", (float) Math.sqrt(range)));
            //shader.addUniform(new Uniform1f(shader, "InvShadowRange", 0.1f));
            shader.addUniform(new Uniform1f(shader, "ShadowIntensity", 1f));
        } catch (GLUnknownUniformException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(double v) {
        CerberusRenderer renderer = getRenderer();
        for (ShadowMapNote light : lightViewMaps) {
            renderer.getTextureBoard().bindTexture(light.fetchOutput());

            setMatrix("matViewProjection_LightView", light.getCamera().getCameraMatrix());
            set3f("LightDir", light.getCamera().getTransformer().getRotation().getForward().toVector3f());

            super.update(v);
        }
    }

    public void setShadowRange(float range) {
        set1f("ShadowRange", range);
        set1f("ShadowRangeSq", range * range);
        set1f("InvShadowRange", 1f / range);
    }

    public void setShadowOffset(float offset) {
        set1f("ShadowOffset", offset);
    }

    public void setShadowIntensity(float intensity) {
        set1f("ShadowIntensity", intensity);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    protected FrameBufferResource setupFrameBuffer(FrameBufferResource frameBuffer, Vector2i screenSize) {
        if (frameBuffer != null)
            getRenderer().getTextureBoard().deleteTexture(frameBuffer);

        TextureEmpty2D texture = new TextureEmpty2D(1);
        texture.genTextures();
        texture.initTexture(0, SHADOW, screenSize, ImageType.RGBA_32_FLOAT);

        return new FrameBufferResource(screenSize, texture, false,
                new SimpleAttachment(0, AttachmentType.COLOR_00));
    }
}
