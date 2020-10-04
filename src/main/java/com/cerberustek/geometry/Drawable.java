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

package com.cerberustek.geometry;

public interface Drawable {

    /**
     * Will draw the mesh in the specified draw mode
     * @param renderMode draw mode
     */
    void draw(DrawMode renderMode);

    /**
     * Will draw the mesh in the specified draw mode in
     * an instanced fashion.
     *
     * @param renderMode draw mode
     * @param count amount of instances
     */
    void drawInstanced(DrawMode renderMode, int count);

    /**
     * Will bind the mesh (set the attribute pointers)
     */
    void bind();

    /**
     * Will ubind the mesh
     */
    void unbind();
}
