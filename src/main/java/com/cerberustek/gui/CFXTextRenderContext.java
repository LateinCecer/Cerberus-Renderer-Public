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
import com.cerberustek.resource.shader.SSBOResource;

public interface CFXTextRenderContext {

    /**
     * Returns the alphabet for the specified section of the
     * text render buffer.
     * @param index section index
     * @return alphabet
     */
    CFXAlphabet getAlphabet(int index);

    /**
     * Returns the char count of the buffer section with the
     * appropriate index.
     * @param index section index
     * @return char count
     */
    int getCharCount(int index);

    /**
     * Returns the resource to the associated shader storage
     * buffer object.
     * @return buffer resource
     */
    SSBOResource getBufferResource();

    /**
     * Returns the amount of section in the text render context
     * @return amount of sections
     */
    int size();

    /**
     * Returns the amount of characters
     * @return char count
     */
    int charCount();

    /**
     * Returns the bounding size of the formatted text in
     * pixels.
     * @return bounding size in pixels
     */
    Vector2i getBounds();
}
