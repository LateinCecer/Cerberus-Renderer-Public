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

import com.cerberustek.overlay.OverlayContainer;
import com.cerberustek.overlay.OverlayFrame;
import com.cerberustek.overlay.OverlayInteraction;
import com.cerberustek.overlay.OverlayLocalInteraction;
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
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class OverlayContainerImpl extends OverlayFrameBase implements OverlayContainer {

    private final HashMap<OverlayFrame, Vector2f> children = new HashMap<>();
    private FrameBufferResource frameBuffer;

    private CerberusRenderer renderer;

    public OverlayContainerImpl(@NotNull OverlayFrame parent, Vector2i resolution, Vector2f size) {
        this(parent, resolution, size, parent.getGuiId());
    }

    public OverlayContainerImpl(OverlayFrame parent, Vector2i resolution, Vector2f size, UUID guiId) {
        super(parent, size, true, true, guiId);
        TextureEmpty2D texture = new TextureEmpty2D(1);
        texture.genTextures();
        texture.initTexture(0, 0, resolution, ImageType.RGBA_8_INTEGER);
        frameBuffer = new FrameBufferResource(resolution, texture, false,
                new SimpleAttachment(0, AttachmentType.COLOR_00));
        getRenderer().getTextureBoard().loadTexture(frameBuffer);
    }

    @Override
    public void interact(OverlayInteraction interaction) {
        relayInteraction(interaction);
    }

    @Override
    public void update(double t) {
        getRenderer().getTextureBoard().bindFrameBuffer(frameBuffer);
        RenderUtil.clear();
        children.forEach((child, pos) -> OverlayContainer.drawChild(child, pos, renderer, t));
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void resize(Vector2i size) {
        if (frameBuffer != null) {
            TextureBoard textureBoard = getRenderer().getTextureBoard();
            textureBoard.deleteTexture(frameBuffer);
        }

        TextureEmpty2D empty2D = new TextureEmpty2D(1);
        empty2D.genTextures();
        empty2D.initTexture(0, 0, size, ImageType.RGBA_8_INTEGER);

        frameBuffer = new FrameBufferResource(size, empty2D, false,
                new SimpleAttachment(0, AttachmentType.COLOR_00));
        update(0);


        children.keySet().forEach(child -> {
            child.resize(child.absolutSize().mul(size.toVector2f()).toVec2i());
        });
        updateChildren(0);
        update(0);


        // updateParent(0);
    }

    @Override
    public void updateChildren(double t) {
        children.keySet().forEach(child -> {
            if (child instanceof OverlayContainer)
                ((OverlayContainer) child).updateChildren(t);
            child.update(t);
        });
    }

    @Override
    public TextureResource getTexture() {
        return frameBuffer;
    }

    @Override
    public Vector2i getResolution() {
        return frameBuffer.getSize();
    }

    private void relayInteraction(OverlayInteraction interaction) {
        if (interaction instanceof OverlayLocalInteraction) {
            final Vector2f coord = ((OverlayLocalInteraction) interaction).getPosition();
            children.forEach((child, pos) -> {
                if (coord.getX() > pos.getX() &&
                        coord.getY() > pos.getY() &&
                        coord.getX() - child.getSize().getX() <= pos.getX() &&
                        coord.getY() - child.getSize().getY() <= pos.getY()) {

                    Vector2f displacement = coord.sub(pos);
                    Vector2f newCoord = new Vector2f(
                            displacement.getX() * getSize().getX() / child.getSize().getX(),
                            displacement.getY() * getSize().getY() / child.getSize().getY()
                    );

                    OverlayLocalInteraction local = new OverlayLocalInteraction(
                            ((OverlayLocalInteraction) interaction).baseInteraction(), newCoord);
                    child.interact(local);
                }
            });
        } else
            children.keySet().forEach(child -> child.interact(interaction));
    }

    @Override
    public void addChild(OverlayFrame child, Vector2f position) {
        if (!children.containsKey(child))
            children.put(child, position);
    }

    @Override
    public void removeChild(OverlayFrame child) {
        children.remove(child);
    }

    @Override
    public Vector2f getChildPos(OverlayFrame child) {
        return children.get(child);
    }

    @Override
    public void destroy() {
        children.keySet().forEach(OverlayFrame::destroy);
        getRenderer().getTextureBoard().deleteTexture(frameBuffer);
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }
}
