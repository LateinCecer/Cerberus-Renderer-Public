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

import com.cerberustek.event.Event;
import com.cerberustek.event.EventHandler;
import com.cerberustek.event.EventListener;
import com.cerberustek.resource.image.TextureResource;
import com.cerberustek.events.GuiUpdateEvent;
import com.cerberustek.logic.math.Vector2f;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.overlay.OverlayContainer;
import com.cerberustek.overlay.OverlayFrame;
import com.cerberustek.overlay.impl.OverlayContainerImpl;
import com.cerberustek.pipeline.InputProvider;
import com.cerberustek.pipeline.impl.RenderNote;
import com.cerberustek.window.Window;

import java.util.UUID;

@EventHandler(events = GuiUpdateEvent.class)
public class GuiNote extends RenderNote implements EventListener, InputProvider {

    private final OverlayFrame root;
    private final UUID guiId;

    private boolean hasUpdated;

    public GuiNote(OverlayFrame root) {
        this.root = root;
        hasUpdated = false;
        guiId = UUID.randomUUID();
    }

    public GuiNote() {
        guiId = UUID.randomUUID();
        root = new OverlayContainerImpl(null, new Vector2i(1920, 1017), new Vector2f(1, 1), guiId);
    }

    @Override
    public void reinit(Window window) {
        /*CerberusRenderer renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        if (root != null) {
            root.resize(renderer.getWindow().getSize());
            requestUpdate();
        }*/
        requestUpdate();
    }

    @Override
    public void destroy() {
        root.destroy();
    }

    @Override
    public void update(double v) {
        if (!hasUpdated) {
            if (root instanceof OverlayContainer)
                ((OverlayContainer) root).updateChildren(v);

            root.update(v);
            hasUpdated = true;
        }
    }

    @Override
    public TextureResource fetchOutput() {
        return root.getTexture();
    }

    @Override
    public boolean onEvent(Event event) {
        assert event instanceof GuiUpdateEvent;

        if (((GuiUpdateEvent) event).getGuiId().equals(guiId))
            requestUpdate();
        return true;
    }

    public OverlayFrame getRoot() {
        return root;
    }

    public UUID getGuiId() {
        return guiId;
    }

    public void requestUpdate() {
        hasUpdated = false;
    }
}
