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

package com.cerberustek.material.impl;

import com.cerberustek.material.Material;
import com.cerberustek.resource.material.MaterialResource;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.material.MaterialBoard;
import com.cerberustek.shader.Shader;

import java.util.HashMap;

public class MaterialBoardImpl implements MaterialBoard {

    private final HashMap<MaterialResource, Material> materials = new HashMap<>();

    private CerberusRenderer renderer;

    @Override
    public Material getMaterial(MaterialResource resource) {
        return materials.get(resource);
    }

    @Override
    public Material loadMaterial(MaterialResource resource) {
        Material material = materials.get(resource);
        if (material != null)
            return material;

        material = resource.load();
        materials.put(resource, material);
        return material;
    }

    @Override
    public void deleteMaterial(MaterialResource resource) {
        materials.remove(resource);
    }

    @Override
    public Material bindMaterial(MaterialResource resource, Shader shader) {
        Material material = loadMaterial(resource);
        if (material == null)
            return null;

        material.bindTextures();
        material.bindProperties(shader);
        return material;
    }

    @Override
    public Material bindMaterial(MaterialResource resource) {
        Material material = loadMaterial(resource);
        if (material == null)
            return null;

        material.bindTextures();

        Shader shader = getRenderer().getShaderBoard().getCurrentlyBound();
        if (shader != null)
            material.bindProperties(shader);

        return material;
    }

    @Override
    public void destroy() {
        materials.clear();
        // save materials
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }
}
