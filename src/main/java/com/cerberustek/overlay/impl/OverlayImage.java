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

import com.cerberustek.overlay.impl.interaction.SelectionInteraction;
import com.cerberustek.resource.image.TextureResource;
import com.cerberustek.texture.Texture;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.logic.math.Vector2f;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.overlay.OverlayFrame;
import com.cerberustek.overlay.OverlayInteraction;
import com.cerberustek.overlay.Selectable;

public class OverlayImage extends OverlayFrameBase implements OverlayFrame, Selectable {

    private final TextureResource texture;

    private boolean selected;

    public OverlayImage(OverlayFrame parent, TextureResource texture, Vector2f size) {
        super(parent, size);
        this.texture = texture;
        selected = false;
    }

    @Override
    public void interact(OverlayInteraction interaction) {
        if (interaction instanceof SelectionInteraction)
            selected = ((SelectionInteraction) interaction).isSelected();
    }

    @Override
    public void update(double t) {}

    @Override
    public void resize(Vector2i size) {}

    @Override
    public TextureResource getTexture() {
        return texture;
    }

    @Override
    public Vector2i getResolution() {
        Texture t = CerberusRegistry.getInstance().getService(CerberusRenderer.class).getTextureBoard().getTexture(texture);
        if (t != null)
            return new Vector2i(t.getSize(0).getX(), t.getSize(0).getY());
        return new Vector2i(0, 0);
    }

    @Override
    public void destroy() {}

    @Override
    public void select(boolean select) {
        this.selected = select;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }
}
