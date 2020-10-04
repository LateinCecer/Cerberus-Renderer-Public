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

package com.cerberustek.overlay.font.impl;

import com.cerberustek.resource.impl.BufferedImageResource;
import com.cerberustek.resource.impl.BufferedTextureResource;
import com.cerberustek.overlay.font.CharResource;
import com.cerberustek.overlay.font.RenderFont;

import java.awt.geom.Rectangle2D;

public class CharResourceImpl extends BufferedTextureResource implements CharResource {

    private final RenderFont font;
    private final Rectangle2D bounds;
    private final char character;

    CharResourceImpl(char character, RenderFont font, BufferedImageResource resource, Rectangle2D bounds) {
        super(resource);

        this.character = character;
        this.font = font;
        this.bounds = bounds;
    }

    @Override
    public RenderFont getRenderFont() {
        return font;
    }

    @Override
    public Rectangle2D getBounds() {
        return bounds;
    }

    @Override
    public char getCharacter() {
        return character;
    }
}
