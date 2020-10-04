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
import com.cerberustek.pipeline.impl.notes.screenspacelighting.Light;
import com.cerberustek.resource.impl.FrameBufferResource;
import com.cerberustek.logic.math.Quaternionf;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.logic.math.Vector3f;
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.uniform.Uniform1i;
import com.cerberustek.shader.uniform.Uniform3f;
import com.cerberustek.shader.uniform.Uniform4f;
import com.cerberustek.texture.AttachmentType;
import com.cerberustek.texture.ImageType;
import com.cerberustek.texture.impl.SimpleAttachment;
import com.cerberustek.texture.impl.TextureEmpty2D;

public class ScreenSpaceLightingNote extends PostProcessingNote {

    private final static int MAX_LIGHTS = 20;

    private final Light[] lights = new Light[MAX_LIGHTS];

    public ScreenSpaceLightingNote() {
        super("lighting/ScreenSpaceLightingVer.glsl", "lighting/ScreenSpaceLightingFrag.glsl");
    }

    @Override
    protected void setupShader(Shader shader) {
        try {
            shader.addUniform(new Uniform1i(shader, "ColorTexture", SceneNote.COLOR));
            shader.addUniform(new Uniform1i(shader, "EmissionTexture", SceneNote.EMISSION));
            shader.addUniform(new Uniform1i(shader, "NormalTexture", SceneNote.NORMAL));
            shader.addUniform(new Uniform1i(shader, "SpecularTexture", SceneNote.SPECULAR));
            shader.addUniform(new Uniform1i(shader, "CameraSpacePositionTexture", SceneNote.POSDEPTH));
            shader.addUniform(new Uniform3f(shader, "ViewDirection", new Vector3f(1, 0, 0)));
            shader.addUniform(new Uniform1i(shader, "NumLightsUsed", 0));
            shader.addUniform(new Uniform4f(shader, "AmbientLightColor", new Quaternionf(0.1f, 0.1f, 0.1f, 1f)));

            for (int i = 0; i < 20; i++) {
                shader.addUniform(new Uniform4f(shader, "LightCameraSpacePosAndRange[" + i + "]", new Quaternionf()));
                shader.addUniform(new Uniform4f(shader, "LightColor[" + i + "]", new Quaternionf()));
                shader.addUniform(new Uniform3f(shader, "NegLightDir[" + i + "]", new Vector3f(-1, 0, 0)));
            }
        } catch (GLUnknownUniformException e) {
            e.printStackTrace();
        }
    }

    public void setAmbientLightColor(Vector3f color, float intensity) {
        set4f("AmbientLightColor", new Quaternionf(intensity, color));
    }

    public void setLight(int index, Light light) {
        lights[index] = light;
    }

    @Override
    public void destroy() {
        if (!updateLights())
            return;
        super.destroy();
    }

    private boolean updateLights() {
        int active = 0;
        Shader shader = getShader();

        for (Light light : lights) {
            if (light == null)
                continue;

            shader.getUniform("LightCameraSpacePosAndRange[" + active + "]", Uniform4f.class).set(
                    new Quaternionf(light.getPosition().getX(), light.getPosition().getY(), light.getPosition().getZ(),
                            light.getRange()));
            shader.getUniform("LightColor[" + active + "]", Uniform4f.class).set(
                    new Quaternionf(light.getColor().getX(), light.getColor().getY(), light.getColor().getZ(),
                            light.getIntensity()));
            shader.getUniform("NegLightDir[" + active + "]", Uniform3f.class).set(
                    light.getNegDirection());
            active++;
        }
        shader.getUniform("NumLightsUsed", Uniform1i.class).set(active);
        return true;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    protected FrameBufferResource setupFrameBuffer(FrameBufferResource frameBuffer, Vector2i screenSize) {
        if (frameBuffer != null)
            getRenderer().getTextureBoard().deleteTexture(frameBuffer);

        TextureEmpty2D texture = new TextureEmpty2D(1);
        texture.genTextures();
        texture.initTexture(0, 0, screenSize, ImageType.RGBA_32_FLOAT);

        return new FrameBufferResource(screenSize, texture, false,
                new SimpleAttachment(0, AttachmentType.COLOR_00));
    }
}
