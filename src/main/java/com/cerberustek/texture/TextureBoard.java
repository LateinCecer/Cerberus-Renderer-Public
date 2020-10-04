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

import com.cerberustek.resource.image.TextureResource;
import com.cerberustek.resource.impl.ImageTextureResource;
import com.cerberustek.Destroyable;
import com.cerberustek.buffer.BufferAccess;
import org.jetbrains.annotations.NotNull;

/**
 * The texture board is used to handle all textures, including
 * but not exclusively, FrameBufferObjects.
 */
public interface TextureBoard extends Destroyable {

    /**
     * Deletes a texture form the texture board and free the
     * texture buffer on the GPU.
     *
     * If the current thread is not a Gl-Renderthread, the
     * deletion of the texture will be done asynchronously
     * on the main render thread.
     *
     * @param resource resource by which the texture is
     *                 referenced
     */
    void deleteTexture(@NotNull TextureResource resource);

    /**
     * Loads a texture to the texture board and the GPU.
     *
     * If the current thread is not a Gl-Renderthread, the
     * loading of the texture will be executed asynchronously
     * in the main render thread.
     *
     * @param textureResource resource to load it from
     * @return the loaded texture object
     */
    Texture loadTexture(@NotNull TextureResource textureResource);

    /**
     * Returns a texture based on it's resource.
     *
     * If the texture in question is currently not known
     * to the system, this method will return null and attempt
     * to load the current system as soon as possible in the next
     * open time window on the gl-thread.
     *
     * @param textureResource texture resource to fetch the
     *                        texture from
     * @return texture
     */
    Texture getTexture(@NotNull TextureResource textureResource);

    /**
     * Binds a texture to the Gl-Renderpipeline, if the
     * current thread is a Gl-Renderthread.
     *
     * Regardless of the nature of the thread, this method
     * will load the texture from the specified resource, in
     * case it is not known to the texture board.
     * In that case, this method will return null for the
     * texture that it just bound. (aka. not bound ;P )
     * If the input resource is null, this method will
     * unbind all textures.
     *
     * @param resource resource of the texture
     * @return Texture that was bound
     */
    Texture bindTexture(TextureResource resource);

    /**
     * Binds a texture to the Gl-Renderpipeline, if the
     * current thread is a Gl-Renderthread.
     *
     * Regardless of the nature of the thread, this method
     * will load the texture from the specified resource, in
     * case it is not known to the texture board.
     * In that case, this method will return null for the
     * texture that it just bound. (aka. not bound ;P )
     * If the input resource is null, this method will
     * unbind all textures.
     *
     * @param resource resource of the texture
     * @param index texture buffer index
     * @param unit texture unit to bind to
     * @return Texture that was bound
     */
    Texture bindTexture(TextureResource resource, int index, int unit);

    /**
     * Will bind the image texture.
     *
     * If the image texture is not yet loaded, this method will
     * attempt to load the texture first.
     *
     * @param resource image texture resource
     * @param index texture buffer index
     * @param unit texture unit to bind to
     * @param layered layered
     * @param layer layer (only relevant if layered is false)
     * @param accessToken buffer access token
     * @return bound image texture
     */
    ImageTexture bindImageTexture(ImageTextureResource resource, int index, int unit, boolean layered,
                                  int layer, BufferAccess accessToken);

    /**
     * Will bind the image texture.
     *
     * If the image texture is not yet loaded, this method will
     * attempt to load the texture first.
     *
     * @param resource image texture resource
     * @param index texture buffer index
     * @param unit texture unit to bind to
     * @param accessToken buffer access token
     * @return bound image texture
     */
    ImageTexture bindImageTexture(ImageTextureResource resource, int index, int unit, BufferAccess accessToken);

    /**
     * Will bind the image texture.
     *
     * If the image texture is not yet loaded, this method will
     * attempt to load the texture first.
     *
     * @param resource image texture resource
     * @param index texture buffer index
     * @param layered layered
     * @param layer layer (only relevant if layered is false)
     * @param accessToken buffer access token
     * @return bound image texture
     */
    ImageTexture bindImageTexture(ImageTextureResource resource, int index, boolean layered, int layer,
                                  BufferAccess accessToken);

    /**
     * Will bind the image texture.
     *
     * If the image texture is not yet loaded, this method will
     * attempt to load the texture first.
     *
     * @param resource image texture resource
     * @param index texture buffer index
     * @param accessToken buffer access token
     * @return bound image texture
     */
    ImageTexture bindImageTexture(ImageTextureResource resource, int index, BufferAccess accessToken);

    /**
     * Will bind the image texture
     *
     * If the image texture is not yet loaded, this method will
     * attempt to load the texture first. This method will load
     * all texture buffers from the image texture.
     *
     * @param resource image texture resource
     * @param accessToken buffer access token
     * @return bound image texture
     */
    ImageTexture bindImageTexture(ImageTextureResource resource, BufferAccess accessToken);

    /**
     * Will unbind the image texture.
     * @param resource image texture to unbind
     * @param index texture buffer to unbind
     * @return unbound image texture
     */
    ImageTexture unbindImageTexture(ImageTextureResource resource, int index);

    /**
     * Will unbind the image texture.
     * @param resource image texture to unbind
     * @return unbound image texture
     */
    ImageTexture unbindImageTexture(ImageTextureResource resource);

    /**
     * Binds the framebuffer as a render target to the Gl-
     * Pipeline, if the current thread is a Gl-Renderthread.
     *
     * Regardless of the nature of this thread, this method
     * will load the framebuffer from the specific resource,
     * in case it is not known to the texture board.
     * In that case, this method will return null for the
     * framebuffer that it just bound. (aka. not bound ;P )
     * If the texture corresponding to the specified resource,
     * is not a valid FrameBuffer, this method will throw
     * an IllegalArgumentException.
     * If the input resource is null, this method will
     * unbind the current framebuffer.
     *
     * @param resource resource of the framebuffer
     * @return Framebuffer that was bound
     * @Exception IllegalArgumentException
     */
    FrameBuffer bindFrameBuffer(TextureResource resource);

    /**
     * Returns the texture object that is currently bound.
     *
     * The Texture Board only logs the texture bindings, that
     * are performed by the board itself and thus this method
     * will not return textures that were bound without using
     * the texture board.
     *
     * @return Resource of the currently bound texture
     */
    TextureResource getBoundTexture();

    /**
     * Returns the resource of the currently bound framebuffer.
     *
     * This method will only track the FrameBuffers that were
     * properly bound by the TextureBoard.
     *
     * @return Resource of FrameBuffer
     */
    TextureResource getBoundFrameBuffer();
}
