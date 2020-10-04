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

package com.cerberustek.texture;

import com.cerberustek.exceptions.AtlasCapacityException;
import com.cerberustek.exceptions.AtlasCellSizeException;
import com.cerberustek.resource.image.TextureResource;
import com.cerberustek.logic.math.Vector3i;

public interface TextureAtlas extends Texture {

    /**
     * Returns the dimensions of the texture atlas in atlas
     * cells.
     * @return atlas dimensions
     */
    Vector3i getAtlasDimensions();

    /**
     * Returns the size of a single cell in the texture atlas
     * in pixels.
     *
     * The size of the texture should be the atlas dimensions
     * times the cell size in pixels.
     *
     * @return size of a single cell in pixels
     */
    Vector3i getCellSize();

    /**
     * Converts a cell coordinate to a cell id.
     *
     * @param cellCoord input coordinates
     * @return cell id
     */
    int toCellId(Vector3i cellCoord);

    /**
     * Converts a cell id to cell coordinates.
     *
     * @param cellId input id
     * @return cell coordinates
     */
    Vector3i toCellCoord(int cellId);

    /**
     * Returns the atlas cell with the appropriate cell id.
     * @param cellId cell id of the atlas cell
     * @return cell
     */
    AtlasCell getCell(int cellId);

    /**
     * Will set a single cell of the texture atlas.
     *
     * This method will always write the provided cell
     * texture 'per pixel' to the texture atlas, meaning
     * that one pixel on the provided texture will
     * always be written to one pixel in the atlas
     * texture.
     *
     * If the size of the provided texture is smaller
     * than the cell size for the texture atlas, the
     * cell texture will be written fitted to the
     * upper right corner to the cell of the texture
     * atlas.
     * If the provided texture is lager than the
     * cell size of a single texture, this method will
     * throw an AtlasCellSizeException.
     *
     * This method, depending on the implementation,
     * may be executed asynchronously.
     *
     * @param cellId cell id
     * @param cellTexture texture of the cell
     * @throws AtlasCellSizeException exception thrown,
     *          if the provided cell texture is to large
     */
    AtlasCell setCell(TextureResource cellTexture, int cellId) throws AtlasCellSizeException;

    /**
     * Will set a single cell of the texture atlas.
     *
     * This method will always scale the provided texture
     * to fit the texture cell in the atlas texture.
     *
     * If the size of the provided cell texture is smaller than
     * the cell size of this texture atlas, the texture will
     * be upscaled to fit the cell in the texture atlas.
     * On the other hand, if the provided texture is larger than
     * the cell size of this texture atlas, than the texture
     * will be downscaled to fit the cell in the atlas texture.
     *
     * This method, depending on the implementation,
     * may be executed asynchronously.
     *
     * @param cellTexture texture of the cell
     * @param cellId cell id
     */
    AtlasCell setCellUnchecked(TextureResource cellTexture, int cellId);

    /**
     * Will add a cell texture to the texture atlas without
     * overwriting any existing textures.
     *
     * This method will always write the provided cell
     * texture 'per pixel' to the texture atlas, meaning
     * that one pixel on the provided texture will
     * always be written to one pixel in the atlas
     * texture.
     *
     * If the size of the provided texture is smaller
     * than the cell size for the texture atlas, the
     * cell texture will be written fitted to the
     * upper right corner to the cell of the texture
     * atlas.
     * If the provided texture is lager than the
     * cell size of a single texture, this method will
     * throw an AtlasCellSizeException.
     *
     * If all cells in the texture atlas are already
     * written to, this method will throw an
     * AtlasCapacityException.
     *
     * This method, depending on the implementation,
     * may be executed asynchronously.
     *
     * @param cellTexture cell texture to add
     * @return added atlas cell
     * @throws AtlasCapacityException thrown, if the texture
     *          atlas is already fully filled
     * @throws AtlasCellSizeException thrown, if the cell size
     *          of the provided texture does not match the
     *          cell size of the texture atlas
     */
    AtlasCell addCell(TextureResource cellTexture) throws AtlasCapacityException, AtlasCellSizeException;

    /**
     * Will add a cell texture to the texture atlas without
     * overwriting any existing textures.
     *
     * This method will always scale the provided texture
     * to fit the texture cell in the atlas texture.
     *
     * If the size of the provided cell texture is smaller than
     * the cell size of this texture atlas, the texture will
     * be upscaled to fit the cell in the texture atlas.
     * On the other hand, if the provided texture is larger than
     * the cell size of this texture atlas, than the texture
     * will be downscaled to fit the cell in the atlas texture.
     *
     * If all cells in the texture atlas are already
     * written to, this method will throw an
     * AtlasCapacityException.
     *
     * This method, depending on the implementation,
     * may be executed asynchronously.
     *
     * @param cellTexture cell texture to add
     * @return added atlas cell
     * @throws AtlasCapacityException thrown, if the texture
     *          atlas is already fully filled
     */
    AtlasCell addCellUnchecked(TextureResource cellTexture) throws AtlasCapacityException;

    /**
     * Will remove the cell with the specified cell id.
     * @param cellId id of the cell to remove
     */
    void removeCell(int cellId);

    /**
     * Will return the overall capacity of the texture atlas
     * in cells.
     * @return capacity in cells
     */
    int capacity();

    /**
     * Will return the amount of unoccupied cells.
     */
    int remaining();

    /**
     * Will return true, if all cells in the texture atlas are
     * occupied.
     * @return all cells occupied
     */
    boolean isFull();
}
