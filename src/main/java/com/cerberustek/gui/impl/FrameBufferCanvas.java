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

package com.cerberustek.gui.impl;

import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.resource.impl.FrameBufferResource;
import com.cerberustek.resource.impl.ImageTextureResource;
import com.cerberustek.texture.AttachmentType;
import com.cerberustek.texture.TextureBoard;
import com.cerberustek.texture.impl.SimpleAttachment;
import com.cerberustek.texture.impl.TextureEmpty2D;
import com.cerberustek.gui.CFXCanvas;

public class FrameBufferCanvas extends FlatCanvas implements CFXCanvas {

    private FrameBufferResource frameBuffer;

    public FrameBufferCanvas(Vector2i size) {
        super(size);
    }

    @Override
    protected void initImageTexture() {
        TextureBoard textureBoard = getRenderer().getTextureBoard();
        if (imageTexture != null) {
            // delete existing image texture and framebuffer
            textureBoard.deleteTexture(imageTexture);
            textureBoard.deleteTexture(frameBuffer);
        }

        TextureEmpty2D base = createBaseTexture();

        imageTexture = new ImageTextureResource(base);
        frameBuffer = new FrameBufferResource(size, base, false,
                new SimpleAttachment(0, AttachmentType.COLOR_00),
                new SimpleAttachment(1, AttachmentType.COLOR_01),
                new SimpleAttachment(2, AttachmentType.COLOR_02),
                new SimpleAttachment(3, AttachmentType.COLOR_03),
                new SimpleAttachment(4, AttachmentType.COLOR_04),
                new SimpleAttachment(5, AttachmentType.COLOR_05));

        textureBoard.loadTexture(imageTexture);
        textureBoard.loadTexture(frameBuffer);
    }

    /**
     * Will return the framebuffer resource for the canvas.
     *
     * If there canvas has not been initialized yet, this
     * will return null.
     *
     * @return canvas frame buffer texture
     */
    public FrameBufferResource getFrameBuffer() {
        return frameBuffer;
    }
}
