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
import com.cerberustek.Updatable;
import com.cerberustek.logic.math.Matrix4f;
import com.cerberustek.shader.Shader;

import java.util.Collection;

public interface RenderScene extends Updatable, Destroyable {

    /**
     * Will update all render matrices of all registered
     * renderables in the scene.
     */
    void updateMatrices(double delta);

    /**
     * Will attempt to render the scene with the specified
     * shader.
     * @param projectionMatrix the projection matrix
     * @param shader shader to render the scene with
     */
    void render(Matrix4f projectionMatrix, Shader shader);

    /**
     * Returns rather or not the scene contains a certain renderable.
     * @param renderable renderable to check for
     * @return contains the renderable?
     */
    boolean contains(Renderable renderable);

    /**
     * Adds a renderable to the scene.
     * @param renderable renderable to add
     */
    void addRenderable(Renderable renderable);

    /**
     * Removes a renderable from the scene.
     * @param renderable renderable to remove
     */
    void removeRenderable(Renderable renderable);

    /**
     * Returns a collection of all top-level renderables in the
     * scene.
     *
     * Please note that not all renderables visible in a scene
     * are top-level and this method does thus not return all
     * objects visible to the camera.
     *
     * @return top-level renderables
     */
    Collection<Renderable> getRenderables();
}
