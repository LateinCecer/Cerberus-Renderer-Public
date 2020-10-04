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
import com.cerberustek.logic.math.Vector2f;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.pipeline.InputProvider;
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.uniform.Uniform1f;
import com.cerberustek.shader.uniform.Uniform1i;
import com.cerberustek.texture.AttachmentType;
import com.cerberustek.texture.ImageType;
import com.cerberustek.texture.impl.SimpleAttachment;
import com.cerberustek.texture.impl.TextureEmpty2D;

public class GlowNote extends PostProcessingNote {

    public final static int GLOW = 8;

    public GlowNote(InputProvider downscaleTexture) {
        super("tonemap/GlowVer.glsl", "tonemap/GlowFrag.glsl", downscaleTexture);
    }

    @SuppressWarnings("Duplicates")
    @Override
    protected FrameBufferResource setupFrameBuffer(FrameBufferResource frameBuffer, Vector2i screenSize) {
        if (frameBuffer != null)
            getRenderer().getTextureBoard().deleteTexture(frameBuffer);

        screenSize = screenSize.div(4);
        TextureEmpty2D texture = new TextureEmpty2D(1);
        texture.genTextures();

        /* ## glow texture ## */
        texture.initTexture(0, GLOW, screenSize, ImageType.RGBA_32_FLOAT);

        return new FrameBufferResource(screenSize, texture, false,
                new SimpleAttachment(0, AttachmentType.COLOR_00));
    }

    @Override
    protected void setupShader(Shader shader) {
        try {
            shader.addUniform(new Uniform1f(shader, "minGlowingSamplingValue", 0.1f));
            shader.addUniform(new Uniform1f(shader, "dtu_per_sample", 0.00125f));
            shader.addUniform(new Uniform1f(shader, "dtv_per_sample", 0.00125f));
            shader.addUniform(new Uniform1i(shader, "screenTexture", DownScaleNote.DOWNSCALE));
        } catch (GLUnknownUniformException e) {
            e.printStackTrace();
        }
    }

    public void setUVOffset(Vector2f offset) {
        set1f("dtu_per_sample", offset.getX());
        set1f("dtv_per_sample", offset.getY());
    }

    public void setMinGlowingValue(float value) {
        set1f("minGlowingSamplingValue", value);
    }
}
