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

package com.cerberustek.overlay.impl;

import com.cerberustek.geometry.DrawMode;
import com.cerberustek.overlay.OverlayFrame;
import com.cerberustek.overlay.OverlayInteraction;
import com.cerberustek.overlay.OverlayUtil;
import com.cerberustek.resource.image.TextureResource;
import com.cerberustek.resource.impl.FrameBufferResource;
import com.cerberustek.texture.AttachmentType;
import com.cerberustek.texture.ImageType;
import com.cerberustek.texture.TextureBoard;
import com.cerberustek.texture.impl.SimpleAttachment;
import com.cerberustek.texture.impl.TextureEmpty2D;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.logic.math.Vector2f;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.util.RenderUtil;

import static org.lwjgl.opengl.GL11.*;

public class OverlayMerge extends OverlayFrameBase {

    private FrameBufferResource frameBuffer;

    private OverlayFrame forground;
    private OverlayFrame background;

    private ImageType imageType;

    private CerberusRenderer renderer;

    OverlayMerge(OverlayFrame parent, Vector2f size, Vector2i resolution, OverlayFrame forground, OverlayFrame background) {
        this(parent, size, resolution, forground, background, ImageType.RGBA_8_INTEGER);
    }

    public OverlayMerge(OverlayFrame parent, Vector2f size, Vector2i resolution, OverlayFrame forground, OverlayFrame background, ImageType imageType) {
        super(parent, size);
        this.forground = forground;
        this.background = background;
        this.imageType = imageType;

        TextureEmpty2D texture = new TextureEmpty2D(1);
        texture.genTextures();
        texture.initTexture(0, 0, resolution, imageType);
        frameBuffer = new FrameBufferResource(resolution, texture, false,
                new SimpleAttachment(0, AttachmentType.COLOR_00));
        getRenderer().getTextureBoard().loadTexture(frameBuffer);
    }

    @Override
    public void resize(Vector2i size) {
        CerberusRenderer renderer = getRenderer();
        TextureBoard textureBoard = renderer.getTextureBoard();

        if (frameBuffer != null)
            textureBoard.deleteTexture(frameBuffer);

        TextureEmpty2D texture = new TextureEmpty2D(1);
        texture.genTextures();;
        texture.initTexture(0, 0, size, imageType);
        frameBuffer = new FrameBufferResource(size, texture, false,
                new SimpleAttachment(0, AttachmentType.COLOR_00));
        textureBoard.loadTexture(frameBuffer);
    }

    @Override
    public void interact(OverlayInteraction interaction) {

    }

    @Override
    public void update(double t) {
        CerberusRenderer renderer = getRenderer();
        renderer.getTextureBoard().bindFrameBuffer(frameBuffer);
        RenderUtil.clear();

        OverlayUtil util = OverlayUtil.getInstance();

        renderer.getShaderBoard().bindShader(util.getMergeShader());
        renderer.getTextureBoard().bindTexture(background.getTexture());
        renderer.getGeometryBoard().drawMesh(util.getScreenMesh(), DrawMode.TRIANGLES);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        renderer.getTextureBoard().bindTexture(forground.getTexture());
        renderer.getGeometryBoard().drawMesh(util.getScreenMesh(), DrawMode.TRIANGLES);

        glDisable(GL_BLEND);
    }

    @Override
    public TextureResource getTexture() {
        return frameBuffer;
    }

    @Override
    public Vector2i getResolution() {
        return frameBuffer.getSize();
    }

    @Override
    public void destroy() {
        getRenderer().getTextureBoard().deleteTexture(frameBuffer);
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }
}
