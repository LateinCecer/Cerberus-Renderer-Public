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

package com.cerberustek.resource.impl;

import com.cerberustek.resource.shader.ShaderCodeResource;
import com.cerberustek.resource.shader.ShaderResource;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.ShaderBoard;
import com.cerberustek.shader.ShaderType;
import com.cerberustek.shader.code.ShaderCodeLoader;
import com.cerberustek.shader.impl.ComputeShader;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class BaseComputeShaderResource implements ShaderResource {

    private final ShaderCodeResource resource;
    private final Consumer<Shader> initConsumer;

    public BaseComputeShaderResource(ShaderCodeResource code, @Nullable Consumer<Shader> initConsumer) {
        this.resource = code;
        this.initConsumer = initConsumer;
    }

    public BaseComputeShaderResource(ShaderCodeResource code) {
        this.resource = code;
        this.initConsumer = null;
    }

    @Override
    public Shader load() {
        CerberusRegistry registry = CerberusRegistry.getInstance();
        CerberusRenderer renderer = registry.getService(CerberusRenderer.class);

        ShaderBoard shaderBoard = renderer.getShaderBoard();
        ShaderCodeLoader codeLoader = shaderBoard.getShaderCodeLoader();

        ComputeShader shader = new ComputeShader();
        shader.genProgram();
        if (codeLoader.exists(resource))
            shader.addProgram(resource, ShaderType.COMPUTE);

        if (!shader.compile())
            registry.warning("Compute shader " + resource + " could not be compiled correctly");
        else {
            registry.debug("Compute shader " + resource + " compiled successfully");

            if (initConsumer != null)
                initConsumer.accept(shader);
        }
        return shader;
    }
}
