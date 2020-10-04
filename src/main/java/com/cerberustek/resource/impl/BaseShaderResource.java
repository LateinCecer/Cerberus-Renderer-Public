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
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.ShaderType;
import com.cerberustek.shader.impl.BaseShader;

import java.util.function.Consumer;

public class BaseShaderResource implements ShaderResource {

    private final ShaderCodeResource[] shaderCode;
    private final ShaderType[] types;

    private final Consumer<Shader> shaderInit;

    public BaseShaderResource(ShaderCodeResource vertex, ShaderCodeResource fragment, Consumer<Shader> shaderInit) {
        this.shaderCode = new ShaderCodeResource[] {vertex, fragment};
        this.types = new ShaderType[] {ShaderType.VERTEX_SHADER, ShaderType.FRAGMENT_SHADER};
        this.shaderInit = shaderInit;
    }

    public BaseShaderResource(ShaderCodeResource[] codeSnippets, ShaderType[] types, Consumer<Shader> shaderInit) {
        if (codeSnippets.length != types.length)
            throw new IllegalArgumentException("A shader type has to be provided for each shader code snippet!");

        this.shaderCode = codeSnippets;
        this.types = types;
        this.shaderInit = shaderInit;
    }

    @Override
    public Shader load() {
        BaseShader shader = new BaseShader();
        shader.genProgram();
        for (int i = 0; i < shaderCode.length; i++)
            shader.addProgram(shaderCode[i], types[i]);
        shader.compile();
        shaderInit.accept(shader);
        return shader;
    }
}
