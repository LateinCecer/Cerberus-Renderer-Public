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

package com.cerberustek.resource.impl;

import com.cerberustek.resource.image.TextureResource;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.texture.Attachment;
import com.cerberustek.texture.Texture;
import com.cerberustek.texture.impl.FrameBufferTexture;

public class FrameBufferResource implements TextureResource {

    private Texture base;
    private Vector2i size;
    private Attachment[] attachments;
    private boolean renderBuffer;

    public FrameBufferResource(Vector2i size, Texture base, boolean renderBuffer, Attachment... attachments) {
        this.size = size;
        this.base = base;
        this.attachments = attachments;
        this.renderBuffer = renderBuffer;
    }

    @Override
    public Texture load() {
        FrameBufferTexture fbo = new FrameBufferTexture(size, base);
        fbo.createAttachments(attachments);
        if (renderBuffer)
            fbo.genRenderBuffer();
        fbo.genFrameBuffer();
        return fbo;
    }

    public Texture getBase() {
        return base;
    }

    public void setBase(Texture base) {
        this.base = base;
    }

    public Vector2i getSize() {
        return size;
    }

    public void setSize(Vector2i size) {
        this.size = size;
    }

    public Attachment[] getAttachments() {
        return attachments;
    }

    public void setAttachments(Attachment... attachments) {
        this.attachments = attachments;
    }

    public boolean isRenderBuffer() {
        return renderBuffer;
    }

    public void setRenderBuffer(boolean renderBuffer) {
        this.renderBuffer = renderBuffer;
    }
}
