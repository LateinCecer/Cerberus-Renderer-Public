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
import com.cerberustek.pipeline.InputProvider;
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.uniform.Uniform1i;
import com.cerberustek.texture.AttachmentType;
import com.cerberustek.texture.ImageType;
import com.cerberustek.texture.impl.SimpleAttachment;
import com.cerberustek.texture.impl.TextureEmpty2D;

public class LuminanceNote extends PostProcessingNote {

    public final static int LUMINANCE = 9;

    public LuminanceNote(InputProvider glowTexture) {
        super("tonemap/LuminanceVer.glsl", "tonemap/LuminanceFrag.glsl", glowTexture);
    }

    @Override
    protected void setupShader(Shader shader) {
        try {
            shader.addUniform(new Uniform1i(shader, "ScreenTexture", GlowNote.GLOW));
        } catch (GLUnknownUniformException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("Duplicates")
    @Override
    protected FrameBufferResource setupFrameBuffer(FrameBufferResource frameBuffer, Vector2i screenSize) {
        if (frameBuffer != null)
            getRenderer().getTextureBoard().deleteTexture(frameBuffer);

        screenSize = screenSize.div(4);
        TextureEmpty2D texture = new TextureEmpty2D(1);
        texture.genTextures();

        /* luminance texture */
        texture.initTexture(0, LUMINANCE, screenSize, ImageType.RGBA_32_FLOAT);

        return new FrameBufferResource(screenSize, texture, false,
                new SimpleAttachment(0, AttachmentType.COLOR_00));
    }
}
