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
import com.cerberustek.logic.math.Quaternionf;
import com.cerberustek.logic.math.Vector2f;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.pipeline.InputProvider;
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.uniform.Uniform1f;
import com.cerberustek.shader.uniform.Uniform1i;
import com.cerberustek.shader.uniform.Uniform4f;
import com.cerberustek.texture.AttachmentType;
import com.cerberustek.texture.ImageType;
import com.cerberustek.texture.impl.SimpleAttachment;
import com.cerberustek.texture.impl.TextureEmpty2D;

public class ToneMappingNote extends PostProcessingNote {

    public ToneMappingNote(InputProvider colorScreen, InputProvider glowScreen, InputProvider adaptationScreen) {
        super("tonemap/BloomVer.glsl", "tonemap/BloomFrag.glsl", colorScreen, glowScreen, adaptationScreen);
    }

    @Override
    protected void setupShader(Shader shader) {
        try {
            shader.addUniform(new Uniform1i(shader, "ScreenTexture", SceneNote.COLOR));
            shader.addUniform(new Uniform1i(shader, "GlowTexture", GlowNote.GLOW));
            shader.addUniform(new Uniform1i(shader, "SceneTextureAdaptedLuminance", AdaptedLuminanceNote.ADAPTED_LUMINANCE));
            shader.addUniform(new Uniform1f(shader, "Dtu_PerPixel", 0.000125f));
            shader.addUniform(new Uniform1f(shader, "Dtv_PerPixel", 0.000125f));

            float bloomFactor = 0.0025f;
            float maxBrightness = 3.0f;
            float exposure = 0.85f;
            float HDRinvIntensity = 0.5f;
            float minLuminanceAdaptation = 1f;
            float overblendingIntensity = 1.0f;
            float overblendingColor = 1f;
            float overblendingOffset = 0.025f;
            float gamma = 1.8f;

            shader.addUniform(new Uniform4f(shader, "HDRValues1", new Quaternionf(bloomFactor, maxBrightness, exposure, HDRinvIntensity)));
            shader.addUniform(new Uniform4f(shader, "HDRValues2", new Quaternionf(minLuminanceAdaptation, overblendingIntensity, overblendingColor, overblendingOffset)));
            shader.addUniform(new Uniform1f(shader, "Dtu_Filter", 0.000125f));
            shader.addUniform(new Uniform1f(shader, "Dtv_Filter", 0.000125f));

            shader.addUniform(new Uniform1f(shader, "gamma", gamma));
        } catch (GLUnknownUniformException e) {
            e.printStackTrace();
        }
    }

    public void setHDRValues(float bloomFactor, float maxBrightness, float exposure, float HDRIntensity,
                             float minLuminanceAdaptation, float overblendingIntensity, float overblendingColor,
                             float overblendingOffset) {
        set4f("HDRValues1", new Quaternionf(bloomFactor, maxBrightness, exposure, HDRIntensity));
        set4f("HDRValues2", new Quaternionf(minLuminanceAdaptation, overblendingIntensity, overblendingColor,
                            overblendingOffset));
    }

    public void setUVOffset(Vector2f offset) {
        set1f("Dtu_PerPixel", offset.getX());
        set1f("Dtv_PerPixel", offset.getY());
    }

    public void setUVFilter(Vector2f filter) {
        set1f("Dtu_Filter", filter.getX());
        set1f("Dtv_Filter", filter.getY());
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    protected FrameBufferResource setupFrameBuffer(FrameBufferResource frameBuffer, Vector2i screenSize) {
        if (frameBuffer != null)
            getRenderer().getTextureBoard().deleteTexture(frameBuffer);

        TextureEmpty2D texture = new TextureEmpty2D(1);
        texture.genTextures();
        texture.initTexture(0, SceneNote.COLOR, screenSize, ImageType.RGBA_8_INTEGER);

        return new FrameBufferResource(screenSize, texture, false,
                new SimpleAttachment(0, AttachmentType.COLOR_00));
    }
}
