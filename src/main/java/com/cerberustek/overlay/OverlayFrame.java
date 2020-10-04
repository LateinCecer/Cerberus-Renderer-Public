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

package com.cerberustek.overlay;

import com.cerberustek.resource.image.TextureResource;
import com.cerberustek.Destroyable;
import com.cerberustek.logic.math.Vector2f;
import com.cerberustek.logic.math.Vector2i;

import java.util.UUID;

public interface OverlayFrame extends Destroyable {

    void interact(OverlayInteraction interaction);

    void update(double t);
    void resize(Vector2i size);

    TextureResource getTexture();

    Vector2f getSize();
    void setSize(Vector2f size);

    Vector2i getResolution();

    boolean isEnabled();
    void setEnabled(boolean enabled);

    boolean isVisible();
    void setVisible(boolean visible);

    OverlayFrame parent();
    void updateParent(double t);

    Vector2f absolutSize();

    UUID getGuiId();
}
