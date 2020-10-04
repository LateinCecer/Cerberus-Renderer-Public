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

import com.cerberustek.logic.math.Vector2f;
import com.cerberustek.overlay.OverlayFrame;

import java.util.UUID;

public abstract class OverlayFrameBase implements OverlayFrame {

    private final OverlayFrame parent;
    private final UUID guiId;

    private Vector2f size;
    private boolean enabled;
    private boolean visible;

    public OverlayFrameBase(OverlayFrame parent, Vector2f size, boolean enabled, boolean visible, UUID guiId) {
        this.parent = parent;
        this.size = size;
        this.enabled = enabled;
        this.visible = visible;
        this.guiId = guiId;
    }

    public OverlayFrameBase(OverlayFrame parent, Vector2f size) {
        this(parent, size, true, true, parent.getGuiId());
    }

    @Override
    public Vector2f getSize() {
        return size;
    }

    @Override
    public void setSize(Vector2f size) {
        this.size = size;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public OverlayFrame parent() {
        return parent;
    }

    @Override
    public void updateParent(double t) {
        update(t);
        if (parent() != null)
            parent.updateParent(t);
    }

    @Override
    public Vector2f absolutSize() {
        if (parent != null)
            return parent.absolutSize().mul(size);
        return size;
    }

    @Override
    public UUID getGuiId() {
        return guiId;
    }
}
