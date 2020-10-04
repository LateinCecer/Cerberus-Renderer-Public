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

import com.cerberustek.Destroyable;
import com.cerberustek.Initable;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.resource.image.TextureResource;
import com.cerberustek.resource.shader.SSBOResource;

public interface CFXAlphabet extends Destroyable, Initable {

    /**
     * Returns the cell size of the cfx alphabet.
     * @return cell size
     */
    Vector2i getCellSize();

    /**
     * Returns the dimensions of the underlying texture
     * atlas in cells.
     * @return atlas dimensions
     */
    Vector2i getDimensions();

    /**
     * Will bind the alphabet
     */
    void bind();

    /**
     * Will insert a character texture and return the cell id.
     * @param cellTexture cell texture to insert
     * @return cell id
     */
    int insertCharacter(TextureResource cellTexture);

    /**
     * Will remove the character in the specified atlas cell id.
     * @param cellId cell id
     */
    void removeCharacter(int cellId);

    /**
     * Texture atlas resource.
     *
     * The texture resource returned by this method should always
     * return a texture resource that loads a texture atlas.
     *
     * @return texture atlas
     */
    TextureResource getTextureAtlas();

    /**
     * Returns the glyph buffer.
     *
     * The glyph buffer contains information about the
     * @return glyph buffer
     */
    SSBOResource getGlyphBuffer();

    /**
     * Will return true, if all cells in the alphabet's texture atlas
     * are full.
     * @return all cells filled
     */
    boolean isFull();

    /**
     * Will returns the amount of free cells in the alphabet
     * @return free cells
     */
    int remaining();
}
