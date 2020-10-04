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

import com.cerberustek.exceptions.IllegalContextException;
import com.cerberustek.resource.image.TextureResource;
import com.cerberustek.resource.impl.FrameBufferResource;
import com.cerberustek.resource.model.ModelResource;
import com.cerberustek.resource.shader.ShaderResource;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.geometry.DrawMode;
import com.cerberustek.geometry.GeometryBoard;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.overlay.OverlayUtil;
import com.cerberustek.pipeline.InputProvider;
import com.cerberustek.pipeline.impl.RenderNote;
import com.cerberustek.texture.AttachmentType;
import com.cerberustek.texture.ImageType;
import com.cerberustek.texture.TextureBoard;
import com.cerberustek.texture.impl.SimpleAttachment;
import com.cerberustek.texture.impl.TextureEmpty2D;
import com.cerberustek.util.RenderUtil;
import com.cerberustek.window.Window;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.opengl.GL11.*;

public class MergeNote extends RenderNote implements InputProvider {

    private InputProvider background;
    private InputProvider[] textures;
    private FrameBufferResource frameBuffer;
    private final ShaderResource displayShader;
    private final ModelResource screen;

    private CerberusRenderer renderer;

    public MergeNote(@NotNull InputProvider background, @NotNull InputProvider... textures) {
        this.background = background;
        this.textures = textures;
        this.screen = OverlayUtil.getInstance().getScreenMesh();

        displayShader = OverlayUtil.getInstance().getMergeShader();
        frameBuffer = initFrameBuffer(null, getRenderer().getWindow().getScreenSize());

        getRenderer().getTextureBoard().loadTexture(frameBuffer);
    }

    @SuppressWarnings("DuplicatedCode")
    private FrameBufferResource initFrameBuffer(FrameBufferResource frameBuffer, Vector2i size) {
        if (frameBuffer != null)
            getRenderer().getTextureBoard().deleteTexture(frameBuffer);

        TextureEmpty2D empty = new TextureEmpty2D(1);
        empty.genTextures();
        empty.initTexture(0, 0, size, ImageType.RGBA_8_INTEGER);

        return new FrameBufferResource(size, empty, false,
                new SimpleAttachment(0, AttachmentType.COLOR_00));
    }

    @Override
    public void destroy() {
        getRenderer().getTextureBoard().deleteTexture(frameBuffer);
    }

    @Override
    public void update(double v) {
        TextureBoard textureBoard = getRenderer().getTextureBoard();
        GeometryBoard geometryBoard = getRenderer().getGeometryBoard();
        getRenderer().getShaderBoard().bindShader(displayShader);

        textureBoard.bindFrameBuffer(frameBuffer);
        RenderUtil.clear();

        textureBoard.bindTexture(background.fetchOutput());
        geometryBoard.drawMesh(screen, DrawMode.TRIANGLES);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        for (InputProvider resource : textures) {
            textureBoard.bindTexture(resource.fetchOutput());
            geometryBoard.drawMesh(screen, DrawMode.TRIANGLES);
        }

        glDisable(GL_BLEND);
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }

    @Override
    public TextureResource fetchOutput() {
        return frameBuffer;
    }

    public void setBackground(@NotNull InputProvider background) {
        if (!getRenderer().getWindow().isGlThread())
            throw new IllegalContextException();

        this.background = background;
    }

    public void setTextures(@NotNull InputProvider... textures) {
        if (!getRenderer().getWindow().isGlThread())
            throw new IllegalContextException();

        this.textures = textures;
    }

    @Override
    public void reinit(Window window) {
        TextureBoard textureBoard = getRenderer().getTextureBoard();
        // textureBoard.deleteTexture(frameBuffer);

        frameBuffer = initFrameBuffer(frameBuffer, window.getScreenSize());
        // System.out.println("Merge framebuffer size: " + window.getScreenSize());
        textureBoard.loadTexture(frameBuffer);
    }
}
