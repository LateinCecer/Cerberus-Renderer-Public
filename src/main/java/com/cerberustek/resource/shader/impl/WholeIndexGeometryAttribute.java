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

package com.cerberustek.resource.shader.impl;

import com.cerberustek.resource.model.IndexBufferArrayResource;
import com.cerberustek.resource.shader.GeometryAttribute;
import com.cerberustek.shader.ssbo.impl.GeometrySSBO;

public class WholeIndexGeometryAttribute implements GeometryAttribute {

    private final IndexBufferArrayResource resource;
    private final int bindingIndex;

    public WholeIndexGeometryAttribute(IndexBufferArrayResource resource, int bindingIndex) {
        this.resource = resource;
        this.bindingIndex = bindingIndex;
    }

    @Override
    public void load(GeometrySSBO ssbo) {
        ssbo.addIndexBufferWhole(resource, bindingIndex);
    }
}
