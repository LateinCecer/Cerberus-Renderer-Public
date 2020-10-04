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

import com.cerberustek.resource.material.MaterialResource;
import com.cerberustek.resource.model.ModelResource;
import com.cerberustek.Destroyable;
import com.cerberustek.shader.Shader;

public interface Renderable extends Destroyable, Transformable {

    String MAT_PROJECTION = "projection";
    String MAT_WORLD = "world_matrix";
    String MAT_WORLD_TRANSLATION = "world_translation_matrix";
    String MAT_WORLD_ROTATION = "world_rotation_matrix";
    String MAT_WORLD_SCALE = "world_scale_matrix";

    /**
     * Will update the shader uniforms for the object
     * rendering.
     *
     * This is especially useful for object rendering
     * in the SceneBuilder, where the render method
     * of the Renderable is not explicitly called.
     * @param shader shader object to update
     */
    void setupShader(Shader shader);

    /**
     * Returns rather or not this renderable will be rendered.
     *
     * This method will be called each time a render pull on this
     * renderable is requested.
     *
     * @return is rendered, or not
     */
    boolean getsRendered();

    /**
     * Returns rather or not this renderable is visible.
     *
     * This method will be called each time an update is performed
     * on this renderable. If this returns false, no updates and no
     * matrix updates will be performed on this object.
     *
     * @return is visible
     */
    boolean isVisible();

    /**
     * Will update the renderable with the passed time t and
     * the render scene.
     * @param t passed time since last update
     * @param scene updating render scene
     */
    void update(double t, RenderScene scene);

    /**
     * Returns the geometry information of this renderable.
     * @return object geometry
     */
    ModelResource getGeometry();

    /**
     * Returns the material resource for the renderable.
     * @return material resource
     */
    MaterialResource getMaterial();
}
