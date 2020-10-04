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

package com.cerberustek.texture;

import com.cerberustek.logic.math.Vector2i;

import java.util.Collection;

public interface FrameBuffer extends Texture, RenderTarget {

    /**
     * Generates the RenderBuffer.
     */
    void genRenderBuffer();

    /**
     * Binds the RenderBuffer to the Gl-RenderPipeline.
     */
    void bindRenderBuffer();

    /**
     * Generates and initializes the frame buffer object.
     *
     * Please make sure to initialize both the textures and the
     * RenderBuffer before you initialize the FrameBuffer.
     */
    void genFrameBuffer();

    /**
     * Binds the FrameBuffer to the Gl-RenderPipeline.
     */
    void bindFrameBuffer();

    /**
     * Binds the draw buffers to the shader output
     */
    void drawToBuffers();

    /**
     * Returns the FrameBuffer size.
     * @return Screen size of FrameBuffer
     */
    Vector2i getScreenSize();

    /**
     * Creates attachments based on texture buffer indices.
     *
     * Returns the attachments it created.
     *
     * @param indices texture buffer indices to create attachments
     *                from
     * @return attachments
     */
    Attachment[] createAttachments(Attachment[] indices);

    /**
     * Returns the type of attachment for the texture buffer at the
     * specific index of this texture.
     * @param index buffer index
     * @return attachment
     */
    Attachment getAttachment(int index);

    /**
     * Returns all attachment types
     * @return attachments
     */
    Collection<Attachment> getAttachments();
}
