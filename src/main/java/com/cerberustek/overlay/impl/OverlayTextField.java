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
import com.cerberustek.logic.math.Vector3f;
import com.cerberustek.overlay.OverlayFrame;
import com.cerberustek.overlay.OverlayInteraction;
import com.cerberustek.overlay.OverlayUtil;
import com.cerberustek.util.RenderUtil;

import java.util.ArrayList;
import java.util.List;

public class OverlayTextField extends OverlayFrameBase {

    private boolean isMultiline = false;
    private boolean warp = true;
    private boolean useColorCodes = true;
    private Vector2f scrol = new Vector2f(0, 0);
    private Vector2i cursor = new Vector2i(0, 0);
    private Vector2i selection = null;

    private CerberusRenderer renderer;
    private FrameBufferResource frameBuffer;

    private ArrayList<String> text = new ArrayList<>();

    public OverlayTextField(OverlayFrame parent, Vector2f size, Vector2i resolution) {
        super(parent, size);

        TextureEmpty2D empty = new TextureEmpty2D(1);
        empty.genTextures();

        empty.initTexture(0, 0, resolution, ImageType.RGBA_8_INTEGER);
        frameBuffer = new FrameBufferResource(resolution, empty, false,
                new SimpleAttachment(0, AttachmentType.COLOR_00));
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void resize(Vector2i size) {
        if (frameBuffer != null)
            getRenderer().getTextureBoard().deleteTexture(frameBuffer);

        TextureEmpty2D empty = new TextureEmpty2D(1);
        empty.genTextures();

        empty.initTexture(0, 0, size, ImageType.RGBA_8_INTEGER);
        frameBuffer = new FrameBufferResource(size, empty, false,
                new SimpleAttachment(0, AttachmentType.COLOR_00));
    }

    @Override
    public void interact(OverlayInteraction interaction) {

    }

    @Override
    public void update(double t) {
        TextureBoard textureBoard = getRenderer().getTextureBoard();
        textureBoard.bindFrameBuffer(frameBuffer);
        RenderUtil.clear();

        OverlayUtil util = OverlayUtil.getInstance();
        Vector3f color = new Vector3f(1, 1, 1);

        float x = 0;
        float y = 0;
        float increaseY = 0;

        float displacementX = scrol.getX() % 1f;
        float displacementY = scrol.getY() % 1f;

        for (int i = (int) scrol.getY(); i < text.size(); i++) {
            String line = text.get(i);
            x = 0;

            for (int j = (int) scrol.getX(); j < line.length(); j++) {

            }
        }
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

    public void insertLine(int index, String line) {
        if (text.size() > index)
            text.add(index, line);
        else
            throw new IllegalStateException("Cannot insert line at index " + index + "! -> text is not big enough!");
    }

    public void removeLine(int index) {
        if (text.size() > index)
            text.remove(index);
        else
            throw new IllegalStateException("Cannot remove line at index " + index + "! -> line does not exist!");
    }

    public List<String> getText() {
        return text;
    }

    public String formatText() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : text)
            stringBuilder.append(s).append('\n');
        String out = stringBuilder.toString();
        return out.substring(0, out.length() - 1);
    }

    public void reset() {
        text.clear();
    }

    public void inputFormated(String text) {
        StringBuilder builder = new StringBuilder();
        char c;
        for (int i = 0; i < text.length(); i++) {
            c = text.charAt(i);

            if (c == '\n') {
                this.text.add(builder.toString());
                builder = new StringBuilder();
            } else
                builder.append(c);
        }
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }
}
