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
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.logic.math.Vector3f;
import com.cerberustek.pipeline.InputProvider;
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.uniform.Uniform1i;
import com.cerberustek.shader.uniform.Uniform3f;
import com.cerberustek.texture.AttachmentType;
import com.cerberustek.texture.ImageType;
import com.cerberustek.texture.impl.SimpleAttachment;
import com.cerberustek.texture.impl.TextureEmpty2D;

public class AdaptedLuminanceNote extends PostProcessingNote {

    public final static int ADAPTED_LUMINANCE = 10;

    public AdaptedLuminanceNote(InputProvider sceneLuminance) {
        super("tonemap/LuminanceAdaptationVer.glsl", "tonemap/LuminanceAdaptationFrag.glsl",
                sceneLuminance);
    }

    @Override
    protected void setupShader(Shader shader) {
        try {
            shader.addUniform(new Uniform1i(shader, "SceneAdaptedLuminanceTexture", ADAPTED_LUMINANCE));
            shader.addUniform(new Uniform1i(shader, "SceneLuminanceTexture", LuminanceNote.LUMINANCE));

            // ######################## new Vector3f(0.27f, 0.67f, 0.06f)
            shader.addUniform(new Uniform3f(shader, "color", new Vector3f(0.212671f, 0.71516f, 0.072169f)));
        } catch (GLUnknownUniformException e) {
            e.printStackTrace();
        }
    }

    public void setColor(Vector3f color) {
        set3f("color", color);
    }

    @SuppressWarnings("Duplicates")
    @Override
    protected FrameBufferResource setupFrameBuffer(FrameBufferResource frameBuffer, Vector2i screenSize) {
        if (frameBuffer != null)
            getRenderer().getTextureBoard().deleteTexture(frameBuffer);

        screenSize = screenSize.div(4);
        TextureEmpty2D texture = new TextureEmpty2D(1);
        texture.genTextures();

        /* ## adapted luminance texture ## */
        texture.initTexture(0, ADAPTED_LUMINANCE, screenSize, ImageType.RGBA_32_FLOAT);

        return new FrameBufferResource(screenSize, texture, false,
                new SimpleAttachment(0, AttachmentType.COLOR_00));
    }
}
