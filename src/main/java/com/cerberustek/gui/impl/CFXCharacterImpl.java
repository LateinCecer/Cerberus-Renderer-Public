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

import com.cerberustek.gui.CFXAlphabet;
import com.cerberustek.gui.CFXCharacter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * CFX rendering character.
 *
 * Contains the base char and Font of the Character, along
 * side width the width and height of the characters bounding
 * in pixels and the CFX-alphabet.
 */
public class CFXCharacterImpl implements CFXCharacter {

    private final int tex;
    private final int width;
    private final int height;
    private final char base;
    private final Font font;
    private final CFXAlphabet alphabet;

    public CFXCharacterImpl(int tex, int width, int height, char base, @NotNull Font font, @NotNull CFXAlphabet alphabet) {
        this.tex = tex;
        this.width = width;
        this.height = height;
        this.base = base;
        this.font = font;
        this.alphabet = alphabet;
    }

    @Override
    public int getTex() {
        return tex;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public char getCharacter() {
        return base;
    }

    @Override
    @NotNull
    public Font getFont() {
        return font;
    }

    @Override
    @NotNull
    public CFXAlphabet getAlphabet() {
        return alphabet;
    }
}
