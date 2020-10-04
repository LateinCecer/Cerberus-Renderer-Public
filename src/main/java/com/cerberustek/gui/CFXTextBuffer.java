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

import com.cerberustek.logic.math.Vector2i;

import java.nio.ByteBuffer;

public interface CFXTextBuffer {

    /**
     * Returns the formatted byte buffer of the text buffer.
     * @return byte buffer
     */
    ByteBuffer getBuffer();

    /**
     * Returns the alphabet of the specified section of the
     * text buffer.
     * @param index section index
     * @return alphabet
     */
    CFXAlphabet getAlphabets(int index);

    /**
     * Returns all saved cfx alphabets.
     * @return all alphabets
     */
    CFXAlphabet[] alphabets();

    /**
     * Returns the char count of the buffer section with the
     * appropriate index.
     * @param index section index
     * @return char count
     */
    int getCharCount(int index);

    /**
     * Returns all char counts as an array
     * @return char counts
     */
    int[] charCounts();

    /**
     * Returns the cuts to the shader block needed for the
     * separation of each section.
     * @return cuts
     */
    long[] cuts();

    /**
     * Returns the amount of sections in this texture buffer.
     *
     * Related numbers:
     *      cuts.length + 1 = size()
     *      alphabets.length = size()
     *
     * @return amount of sections
     */
    int size();

    /**
     * Returns char count
     * @return char count
     */
    int charCount();

    /**
     * Will return the bounds of the formatted text in pixels.
     * @return bounds in pixels
     */
    Vector2i getBounds();
}
