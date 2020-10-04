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

package com.cerberustek.gui.impl;

import java.awt.*;
import java.util.Objects;

class CharInfo {

    private final char c;
    private final Font font;

    CharInfo(char c, Font font) {
        this.c = c;
        this.font = font;
    }

    public char getC() {
        return c;
    }

    public Font getFont() {
        return font;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharInfo charInfo = (CharInfo) o;
        return c == charInfo.c &&
                Objects.equals(font, charInfo.font);
    }

    @Override
    public int hashCode() {
        return Objects.hash(c, font);
    }
}
