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

import com.cerberustek.resource.shader.ShaderResource;

public class GLComputeException extends Exception {

    private final ShaderResource computeShader;

    public GLComputeException(ShaderResource shaderResource) {
        super("Error in compute shader \"" + shaderResource + "\"");
        this.computeShader = shaderResource;
    }

    public GLComputeException(ShaderResource shaderResource, String message) {
        super("Error in compute shader \"" + shaderResource + "\":> " + message);
        this.computeShader = shaderResource;
    }

    public ShaderResource getComputeShader() {
        return computeShader;
    }
}
