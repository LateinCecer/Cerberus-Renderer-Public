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

package com.cerberustek.gui;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public interface CFXCharacter {

    /**
     * Returns the cell id of the CFX Character inside the texture
     * atlas of the font renderer.
     * @return cell id
     */
    int getTex();

    /**
     * Returns the character width in pixels.
     * @return width in pixels
     */
    int getWidth();

    /**
     * Returns the character height in pixels.
     * @return height in pixels
     */
    int getHeight();

    /**
     * Returns the base char of this CFX-Character.
     * @return base char
     */
    char getCharacter();

    /**
     * Returns the font of the character.
     * @return font
     */
    @NotNull
    Font getFont();

    /**
     * Returns the CFX alphabet
     * @return alphabet
     */
    @NotNull
    CFXAlphabet getAlphabet();
}
