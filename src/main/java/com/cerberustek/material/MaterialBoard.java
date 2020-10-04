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

package com.cerberustek.material;

import com.cerberustek.resource.material.MaterialResource;
import com.cerberustek.Destroyable;
import com.cerberustek.shader.Shader;

public interface MaterialBoard extends Destroyable {

    /**
     * Returns the material corresponding to the material
     * resource.
     *
     * If the material is currently not loaded, this method
     * will return null.
     *
     * @param resource material resource
     * @return material
     */
    Material getMaterial(MaterialResource resource);

    /**
     * Will attempt to load a material.
     *
     * If the material does already exist, this method
     * will return it without doing anything else.
     * If the material for the resource is not currently
     * loaded and can be loaded, this method will load
     * and return the resource.
     * If the resource cannot be loaded, this method
     * will return null.
     *
     * @param resource material resource
     * @return material
     */
    Material loadMaterial(MaterialResource resource);

    /**
     * Will attempt to delete a material.
     *
     * If the material corresponding to the material
     * resource is currently not known, this method
     * will have no effect.
     *
     * @param resource material resource
     */
    void deleteMaterial(MaterialResource resource);

    /**
     * Will attempt to bind the material.
     *
     * This will bind the materials textures and update the
     * shaders uniforms in accordance with the material
     * properties.
     * If the shader attribute is null, the shader uniforms
     * will not be updated and only the textures will be
     * bound.
     *
     * @param resource material resource
     * @param shader shader
     * @return bound material
     */
    Material bindMaterial(MaterialResource resource, Shader shader);

    /**
     * Will attempt to bind the material.
     *
     * This will bind the materials textures and update the
     * shaders uniforms in accordance with the material
     * properties.
     * This method will pull the currently bound shader from
     * the shader board. If there is currently no shader bound,
     * this method will only bind the textures.
     *
     * @param resource material resource
     * @return bound material
     */
    Material bindMaterial(MaterialResource resource);
}
