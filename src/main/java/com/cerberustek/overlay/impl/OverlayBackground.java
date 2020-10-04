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
import com.cerberustek.geometry.GeometryBoard;
import com.cerberustek.resource.image.TextureResource;
import com.cerberustek.resource.impl.FrameBufferResource;
import com.cerberustek.resource.model.ModelResource;
import com.cerberustek.resource.shader.ShaderResource;
import com.cerberustek.texture.AttachmentType;
import com.cerberustek.texture.ImageType;
import com.cerberustek.texture.TextureBoard;
import com.cerberustek.texture.impl.SimpleAttachment;
import com.cerberustek.texture.impl.TextureEmpty2D;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.logic.math.Vector2f;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.logic.math.Vector4f;
import com.cerberustek.overlay.OverlayFrame;
import com.cerberustek.overlay.OverlayInteraction;
import com.cerberustek.overlay.OverlayUtil;
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.ShaderBoard;
import com.cerberustek.shader.uniform.Uniform1i;
import com.cerberustek.shader.uniform.Uniform4f;
import com.cerberustek.util.RenderUtil;

import static org.lwjgl.opengl.GL11.*;

public class OverlayBackground extends OverlayFrameBase implements OverlayFrame {

    private FrameBufferResource frameBufferResource;
    private Vector4f color;
    private OverlayFrame forground;
    private CerberusRenderer renderer;
    private ShaderResource mergeShader;
    private ShaderResource backgroundShader;
    private ModelResource screen;

    public OverlayBackground(OverlayFrame parent, Vector2f size, Vector4f color, OverlayFrame forground) {
        super(parent, size);

        this.color = color;
        this.forground = forground;
        Vector2i screenSize = getRenderer().getWindow().getScreenSize().toVector2f().mul(absolutSize()).toVec2i();

        TextureEmpty2D empty = new TextureEmpty2D(1);
        empty.genTextures();
        empty.initTexture(0, 0, screenSize, ImageType.RGBA_8_INTEGER);

        this.frameBufferResource = new FrameBufferResource(screenSize, empty, false,
                new SimpleAttachment(0, AttachmentType.COLOR_00));

        this.mergeShader = OverlayUtil.getInstance().getMergeShader();
        this.backgroundShader = OverlayUtil.getInstance().getBackgroundShader();
        this.screen = OverlayUtil.getInstance().getScreenMesh();
    }

    @Override
    public void resize(Vector2i size) {
        if (frameBufferResource != null)
            getRenderer().getTextureBoard().deleteTexture(frameBufferResource);

        TextureEmpty2D empty = new TextureEmpty2D(1);
        empty.genTextures();
        empty.initTexture(0, 0, size, ImageType.RGBA_8_INTEGER);

        this.frameBufferResource = new FrameBufferResource(size, empty, false,
                new SimpleAttachment(0, AttachmentType.COLOR_00));
    }

    @Override
    public void interact(OverlayInteraction interaction) {

    }

    @Override
    public void update(double t) {
        ShaderBoard shaderBoard = getRenderer().getShaderBoard();
        TextureBoard textureBoard = getRenderer().getTextureBoard();
        GeometryBoard geometryBoard = getRenderer().getGeometryBoard();

        textureBoard.bindFrameBuffer(frameBufferResource);
        RenderUtil.clear();

        Shader shader = shaderBoard.bindShader(backgroundShader);
        shader.getUniform("color", Uniform4f.class).set(color);
        shader.update(t);
        geometryBoard.drawMesh(screen, DrawMode.TRIANGLES);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        shader = shaderBoard.bindShader(mergeShader);
        shader.getUniform("sampler", Uniform1i.class).set(0);
        shader.update(t);
        textureBoard.bindTexture(forground.getTexture());
        geometryBoard.drawMesh(screen, DrawMode.TRIANGLES);

        glDisable(GL_BLEND);
    }

    @Override
    public TextureResource getTexture() {
        return frameBufferResource;
    }

    @Override
    public Vector2i getResolution() {
        return frameBufferResource.getSize();
    }

    @Override
    public void destroy() {
        getRenderer().getTextureBoard().deleteTexture(frameBufferResource);
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }
}
