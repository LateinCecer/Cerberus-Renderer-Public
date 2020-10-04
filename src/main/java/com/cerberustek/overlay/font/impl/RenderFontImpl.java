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
import com.cerberustek.overlay.font.CharResource;
import com.cerberustek.overlay.font.RenderFont;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class RenderFontImpl implements RenderFont {

    private final HashMap<Character, CharResource> charMap = new HashMap<>();
    private final Font font;

    public RenderFontImpl(Font font) {
        this.font = font;
    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public CharResource getCharacter(char c) {
        CharResource out = charMap.get(c);
        if (out != null)
            return out;

        out = fabriacateChar(c);
        charMap.put(c, out);
        return out;
    }

    private CharResource fabriacateChar(char c) {
        FontRenderContext context = new FontRenderContext(font.getTransform(), true, true);
        Rectangle2D bounds = font.getStringBounds("" + c, context);

        int width = (int) (bounds.getWidth());
        int height = (int) (bounds.getHeight());

        if (width == 0)
            return null;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = (Graphics2D) image.getGraphics();

        graphics2D.setColor(Color.WHITE);
        graphics2D.setFont(font);

        Map<?, ?> desktopHints = (Map<?, ?>) Toolkit.getDefaultToolkit()
                .getDesktopProperty("awt.font.desktophints");
        if (desktopHints == null) {
            graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            graphics2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                    RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        } else
            graphics2D.setRenderingHints(desktopHints);

        graphics2D.drawString("" + c, (float) bounds.getX(),
                (float) (-bounds.getY()));
        return new CharResourceImpl(c, this, new BufferedImageResource(image, 0), bounds);
    }
}
