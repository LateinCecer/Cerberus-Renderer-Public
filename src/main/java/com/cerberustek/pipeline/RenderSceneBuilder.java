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

package com.cerberustek.pipeline;

import com.cerberustek.Destroyable;
import com.cerberustek.logic.math.Matrix4f;
import com.cerberustek.shader.Shader;

/**
 * A RenderSceneBuilder contains a datastructure
 * used to render different meshes more
 * efficiently, by minimizing the amount of
 * glVertexAttribPointer calls.
 */
public interface RenderSceneBuilder extends Destroyable {

    /**
     * Will render all renderables added to the
     * scene builder
     * @param delta time passed since last draw
     */
    void render(double delta, Matrix4f mat, Shader shader);

    /**
     * Add a renderable to the scene builder.
     * @param renderable renderable to add
     */
    void append(Renderable renderable);

    /**
     * Will clear the render scene builder and thus
     * prepare it to render a new scene.
     *
     * This is typically done each time after the
     * scene builder was drawn (Except for static
     * scenes).
     */
    void clear();
}
