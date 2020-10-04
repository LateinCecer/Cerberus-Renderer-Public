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

import com.cerberustek.CerberusEvent;
import com.cerberustek.overlay.impl.interaction.MouseReleaseInteraction;
import com.cerberustek.resource.image.TextureResource;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.events.GuiInteractionEvent;
import com.cerberustek.logic.math.Vector2f;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.overlay.OverlayFrame;
import com.cerberustek.overlay.OverlayInteraction;
import com.cerberustek.overlay.OverlayLocalInteraction;
import com.cerberustek.overlay.Selectable;
import com.cerberustek.overlay.font.RenderFont;
import com.cerberustek.overlay.impl.interaction.MouseClickInteraction;

import java.util.function.Consumer;

public class OverlayButton extends OverlayFrameBase implements Selectable {

    private final Consumer<OverlayButton> consumer;

    private OverlayTextArea text;
    private boolean selected;

    public OverlayButton(OverlayFrame parent, Vector2f size, Consumer<OverlayButton> consumer) {
        super(parent, size);
        this.text = new OverlayTextArea(this, size);
        this.consumer = consumer;
        this.selected = false;
    }

    @Override
    public void interact(OverlayInteraction interaction) {
        if (interaction instanceof OverlayLocalInteraction) {
            Vector2f pos = ((OverlayLocalInteraction) interaction).getPosition();
            interaction = ((OverlayLocalInteraction) interaction).baseInteraction();

            if (!CerberusRegistry.getInstance().getService(CerberusEvent.class)
                    .executeShortEIF(new GuiInteractionEvent(this, interaction)))
                return;

            if (interaction instanceof MouseClickInteraction && isEnabled()) {
                if (((MouseClickInteraction) interaction).isLeftClick())
                    select(true);
            } else if (interaction instanceof MouseReleaseInteraction && isEnabled()) {
                if (((MouseReleaseInteraction) interaction).isLeftClick() && isSelected()) {
                    select(false);
                    if (pos != null)
                        consumer.accept(this);
                }
            }
        }
    }

    @Override
    public void update(double t) {

    }

    @Override
    public void resize(Vector2i size) {

    }

    @Override
    public TextureResource getTexture() {
        return text.getTexture();
    }

    @Override
    public Vector2i getResolution() {
        return text.getResolution();
    }

    @Override
    public void destroy() {
        text.destroy();
    }

    @Override
    public void select(boolean select) {
        if (text instanceof Selectable)
            ((Selectable) text).select(select);

        boolean old = this.selected;
        this.selected = select;

        if (old != select)
            CerberusRegistry.getInstance().getService(CerberusRenderer.class).tryGLTask(this::updateParent);
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    public void setText(String s, RenderFont font) {
        this.text.setText(s, font);
    }
}
