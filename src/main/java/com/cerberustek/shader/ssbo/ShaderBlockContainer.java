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

package com.cerberustek.shader.ssbo;

import com.cerberustek.Destroyable;

public interface ShaderBlockContainer extends Destroyable {

    /**
     * Will bind the shader block with the internal index
     * <code>index</code>.
     * @param index internal index
     */
    void bind(int index);

    /**
     * Will bind the shader block with the internal index
     * <code>index</code> to the specified binding index.
     * @param index internal index
     * @param bindingIndex binding index
     */
    void bind(int index, int bindingIndex);

    /**
     * Will bind all installed shader blocks to their respective
     * binding index.
     */
    void bind();

    /**
     * Will return the shader block with the internal index
     * <code>index</code>.
     * @param index internal index
     * @return shader block
     */
    ShaderBlock getBlock(int index);

    /**
     * Returns true, if the specified shader block is part of
     * this shader block container.
     * @param block shader block to test
     * @return does contain shader block
     */
    boolean contains(ShaderBlock block);
}
