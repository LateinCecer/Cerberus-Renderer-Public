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

import com.cerberustek.Destroyable;
import com.cerberustek.logic.math.Vector3i;

public interface Texture extends Destroyable {

    /**
     * Returns true if the texture is currently stored in the VRM.
     * @return stored on VRM?
     */
    boolean isOnline();

    /**
     * Returns true if the texture buffer referenced by the index
     * is currently stored in the VRM.
     *
     * @param index buffer index
     * @return stored in VRM?
     */
    boolean isOnline(int index);

    /**
     * Allocates texture buffer pointers on the VRM.
     */
    void genTextures();

    /**
     * Allocated the texture buffer at the specific index.
     * @param index index to allocate the texture buffer at
     */
    void genTexture(int index);

    /**
     * Will bind the Texture to the Gl-Renderpipeline if it is
     * currently online.
     */
    void bind();

    /**
     * Will bind the Texture to the Gl-Renderpipeline if it is
     * currently online.
     *
     * This method will bind the texture the the specified
     * texture unit.
     *
     * @param index texture buffer index
     * @param unit texture unit
     */
    void bindToUnit(int index, int unit);

    /**
     * Will bind the referenced texture buffer to the Gl-Renderpipeline
     * if it is currently online.
     * @param index texture buffer index
     */
    void bind(int index);

    /**
     * Will delete the referenced texture buffer.
     * @param index texture buffer to delete
     */
    void destroy(int index);

    /**
     * Returns the amount of texture buffers this texture manages.
     * @return amount of texture buffers
     */
    int length();

    /**
     * Returns the texture unit of the texture buffer at the
     * specific texture buffer index.
     * @param index index of texture buffer
     * @return action level
     */
    int getUnit(int index);

    /**
     * Returns the texture buffer pointer at the index the texture buffer
     * is stored at.
     * @param index index of texture buffer to the pointer from
     * @return texture buffer pointer
     */
    int getPointer(int index);

    /**
     * Returns the image type of the specified texture buffer.
     * @param index index of the texture buffer
     * @return image type of the texture buffer
     */
    ImageType getType(int index);

    /**
     * Returns the buffer size of the texture buffer on the specified
     * buffer index.
     *
     * If the texture is 2D, only the first two parameters will be
     * occupied.
     *
     * @param index texture buffer to get the size from
     * @return Size of the texture buffer
     */
    Vector3i getSize(int index);
}
