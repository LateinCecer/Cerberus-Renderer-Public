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
import com.cerberustek.resource.impl.ImageTextureResource;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.buffer.BufferAccess;
import com.cerberustek.texture.FrameBuffer;
import com.cerberustek.texture.ImageTexture;
import com.cerberustek.texture.Texture;
import com.cerberustek.texture.TextureBoard;
import com.cerberustek.util.TextureUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ConcurrentModificationException;
import java.util.HashMap;

public class TextureBoardImpl implements TextureBoard {

    private final HashMap<TextureResource, Texture> textureMap = new HashMap<>();

    private CerberusRenderer renderer;
    private TextureResource currentTexture;
    private TextureResource currentFrameBuffer;

    @Override
    public void deleteTexture(@NotNull TextureResource resource) {
        Texture texture = getTexture(resource);
        if (texture != null) {
            getRenderer().tryGLTask((time) -> texture.destroy());
            textureMap.remove(resource);
        }
    }

    @Override
    public Texture loadTexture(@NotNull TextureResource textureResource) {
        Texture texture = textureMap.get(textureResource);
        if (texture == null) {
            // load the texture
            if (getRenderer().getWindow().isGlThread()) {
                texture = textureResource.load();
                if (texture == null)
                    return null;
                textureMap.put(textureResource, texture);
            } else {
                getRenderer().tryGLTask(t -> {
                    loadTexture(textureResource);
                });
                return null;
            }
        }
        return texture;
    }

    @Override
    public Texture bindTexture(TextureResource resource) {
        if (resource == null) {
            TextureUtil.unbindTexture2D();
            currentTexture = null;
            return null;
        }

        Texture texture = loadTexture(resource);
        if (texture == null)
            return null;

        if (getRenderer().getWindow().isGlThread()) {
            texture.bind();
            currentTexture = resource;
        }
        return texture;
    }

    @Override
    public Texture bindTexture(TextureResource resource, int index, int unit) {
        if (resource == null) {
            TextureUtil.unbindTexture2D();
            currentTexture = null;
            return null;
        }

        Texture texture = loadTexture(resource);
        if (texture == null)
            return null;

        if (getRenderer().getWindow().isGlThread()) {
            texture.bindToUnit(index, unit);
            currentTexture = resource;
        }
        return texture;
    }

    @Override
    public ImageTexture bindImageTexture(ImageTextureResource resource, int index, int unit, boolean layered,
                                         int layer, BufferAccess accessToken) {
        Texture obj = loadTexture(resource);
        if (!(obj instanceof ImageTexture))
            throw new IllegalArgumentException("The texture specified by the image texture resource is not" +
                    " an image texture!");

        ((ImageTexture) obj).bindImage(index, unit, layered, layer, accessToken);
        return (ImageTexture) obj;
    }

    @Override
    public ImageTexture bindImageTexture(ImageTextureResource resource, int index, int unit, BufferAccess accessToken) {
        Texture obj = loadTexture(resource);
        if (!(obj instanceof ImageTexture))
            throw new IllegalArgumentException("The texture specified by the image texture resource is not" +
                    " an image texture!");

        ((ImageTexture) obj).bindImage(index, unit, accessToken);
        return (ImageTexture) obj;
    }

    @Override
    public ImageTexture bindImageTexture(ImageTextureResource resource, int index, boolean layered, int layer, BufferAccess accessToken) {
        Texture obj = loadTexture(resource);
        if (!(obj instanceof ImageTexture))
            throw new IllegalArgumentException("The texture specified by the image texture resource is not" +
                    " an image texture!");

        ((ImageTexture) obj).bindImage(index, layered, layer, accessToken);
        return (ImageTexture) obj;
    }

    @Override
    public ImageTexture bindImageTexture(ImageTextureResource resource, int index, BufferAccess accessToken) {
        Texture obj = loadTexture(resource);
        if (!(obj instanceof ImageTexture))
            throw new IllegalArgumentException("The texture specified by the image texture resource is not" +
                    " an image texture!");

        ((ImageTexture) obj).bindImage(index, accessToken);
        return (ImageTexture) obj;
    }

    @Override
    public ImageTexture bindImageTexture(ImageTextureResource resource, BufferAccess accessToken) {
        Texture obj = loadTexture(resource);
        if (!(obj instanceof ImageTexture))
            throw new IllegalArgumentException("The texture specified by the image texture resource is not" +
                    " an image texture!");

        ((ImageTexture) obj).bindImage(accessToken);
        return (ImageTexture) obj;
    }

    @Override
    public ImageTexture unbindImageTexture(ImageTextureResource resource, int index) {
        Texture obj = loadTexture(resource);
        if (!(obj instanceof ImageTexture))
            throw new IllegalArgumentException("The texture specified by the image texture resource is not" +
                    " an image texture!");

        ((ImageTexture) obj).unbindImage(index);
        return (ImageTexture) obj;
    }

    @Override
    public ImageTexture unbindImageTexture(ImageTextureResource resource) {
        Texture obj = loadTexture(resource);
        if (!(obj instanceof ImageTexture))
            throw new IllegalArgumentException("The texture specified by the image texture resource is not" +
                    " an image texture!");

        ((ImageTexture) obj).unbindImage();
        return (ImageTexture) obj;
    }

    @Override
    public FrameBuffer bindFrameBuffer(TextureResource resource) {
        if (resource == null) {
            currentFrameBuffer = null;
            getRenderer().getPipeline().setRenderTarget(getRenderer().getWindow());
            return null;
        }

        Texture texture = bindTexture(resource);
        if (texture == null)
            return null;

        if (!(texture instanceof FrameBuffer))
            throw new IllegalArgumentException("The texture resource " + resource + " was used to bind a framebuffer" +
                    " but does reference one.");

        FrameBuffer frameBuffer = (FrameBuffer) texture;
        getRenderer().getPipeline().setRenderTarget(frameBuffer);
        currentFrameBuffer = resource;
        return frameBuffer;
    }

    @Override
    public TextureResource getBoundTexture() {
        return currentTexture;
    }

    @Override
    public TextureResource getBoundFrameBuffer() {
        return currentFrameBuffer;
    }

    @Override
    public void destroy() {
        getRenderer().tryGLTask((time) -> textureMap.values().forEach(Texture::destroy));
        textureMap.clear();
    }

    @Override
    public Texture getTexture(@NotNull TextureResource resource) {
        try {
            return textureMap.get(resource);
        } catch (ConcurrentModificationException e) {
            return getTexture(resource);
        }
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }
}
