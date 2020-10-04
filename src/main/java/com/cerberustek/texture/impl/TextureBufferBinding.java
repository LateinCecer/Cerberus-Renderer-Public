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

package com.cerberustek.texture.impl;

import com.cerberustek.resource.image.TextureResource;

/**
 * A texture binding contains a texture and a buffer id.
 *
 * It can be used to bind texture buffers to units which
 * they usually do not belong to.
 */
public class TextureBufferBinding {

    /** The Texture */
    private final TextureResource texture;
    /** The buffer's id */
    private final int bufferId;

    /**
     * Init the texture binging with a texture and a buffer id.
     *
     * These variables are final and should never change during
     * the lifetime of the texture binding.
     *
     * @param texture texture resource
     * @param bufferId buffer id
     */
    public TextureBufferBinding(TextureResource texture, int bufferId) {
        this.texture = texture;
        this.bufferId = bufferId;
    }

    /**
     * The texture resource
     * @return texture resource
     */
    public TextureResource getTexture() {
        return texture;
    }

    /**
     * The buffer id
     * @return buffer id
     */
    public int getBufferId() {
        return bufferId;
    }
}
