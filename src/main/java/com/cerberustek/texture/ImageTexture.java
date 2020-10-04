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

import com.cerberustek.buffer.BufferAccess;

public interface ImageTexture extends Texture {

    /**
     * Will bind the image texture as an image.
     *
     * @param index index internal texture index
     * @param unit texture unit
     * @param layered layered
     * @param layer layer
     * @param accessToken access token
     */
    void bindImage(int index, int unit, boolean layered, int layer, BufferAccess accessToken);

    /**
     * Will bind the image texture as an image.
     *
     * The default texture unit will be used for the
     * specified texture buffer.
     *
     * @param index internal texture index
     * @param layered layered
     * @param layer layer index
     * @param accessToken buffer access token
     */
    void bindImage(int index, boolean layered, int layer, BufferAccess accessToken);

    /**
     * Will bind the image texture as an image.
     *
     * The image will be bound with layered = false and
     * the layer index set to 0.
     *
     * @param index internal texture index
     * @param unit texture unit to bind to
     * @param accessToken buffer access token
     */
    void bindImage(int index, int unit, BufferAccess accessToken);

    /**
     * Will bind the image texture as an image.
     *
     * The image will be bound with layered = false and
     * the layer index set to 0. The default texture unit
     * will be used for the specified texture buffer.
     *
     * @param index internal texture index
     * @param accessToken buffer access token
     */
    void bindImage(int index, BufferAccess accessToken);

    /**
     * Will bind all image textures as an image.
     *
     * The image will be bound with layered = false and
     * the layer index set to 0. The default texture unit
     * will be used for the specified texture buffer.
     *
     * @param accessToken buffer access token
     */
    void bindImage(BufferAccess accessToken);

    /**
     * Will unbind all image textures for all sub texture-
     * units contained in this image texture.
     */
    void unbindImage();

    /**
     * Will unbind the image texture for the sub image
     * with the specified internal index.
     * @param index internal texture index
     */
    void unbindImage(int index);

    /**
     * Returns the layer of the internal texture index.
     *
     * If the layer id is -1, the texture is set to be
     * layered.
     *
     * @param index internal texture index
     * @return layer id
     */
    int getLayer(int index);

    /**
     * Will set the layer of a image texture.
     *
     * @param index internal texture index
     * @param layer layer id
     */
    void setLayer(int index, int layer);
}
