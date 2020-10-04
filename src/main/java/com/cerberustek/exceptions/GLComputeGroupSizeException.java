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

package com.cerberustek.exceptions;

import com.cerberustek.logic.math.Vector3i;
import com.cerberustek.resource.shader.ShaderResource;

public class GLComputeGroupSizeException extends GLComputeException {

    private final Vector3i groupSize;

    public GLComputeGroupSizeException(ShaderResource shaderResource, Vector3i groupSize) {
        super(shaderResource, " [invalid group size] " + groupSize);
        this.groupSize = groupSize;
    }

    public Vector3i getGroupSize() {
        return groupSize;
    }
}
