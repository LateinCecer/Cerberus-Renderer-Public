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

package com.cerberustek.gui;

import com.cerberustek.CerberusEvent;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.events.ExceptionEvent;
import com.cerberustek.exceptions.GLUnknownUniformException;
import com.cerberustek.logic.math.Vector2f;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.logic.math.Vector3f;
import com.cerberustek.logic.math.Vector4f;
import com.cerberustek.pipeline.impl.notes.SceneNote;
import com.cerberustek.resource.impl.BaseComputeShaderResource;
import com.cerberustek.resource.impl.BaseShaderResource;
import com.cerberustek.resource.shader.ShaderCodeResource;
import com.cerberustek.resource.shader.ShaderResource;
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.ShaderBoard;
import com.cerberustek.shader.code.ShaderCodeLoader;
import com.cerberustek.shader.uniform.*;
import com.cerberustek.CerberusRenderer;

import java.util.function.Supplier;

public enum CFXShader {

    PATCH(new PatchSupplier()),
    RECTANGLE(new RectangleSupplier()),
    FONT(new FontSupplier()),
    DRAW(new DrawSupplier());

    private final Supplier<ShaderResource> defaultResource;

    CFXShader(Supplier<ShaderResource> supplier) {
        this.defaultResource = supplier;
    }

    public Supplier<ShaderResource> defaultResource() {
        return defaultResource;
    }

    private static class PatchSupplier implements Supplier<ShaderResource> {

        private void initShader(Shader shader) {
            try {
                shader.addUniform(new Uniform2i(shader, "srcCoord", new Vector2i(0, 0)));
                shader.addUniform(new Uniform2i(shader, "destCoord", new Vector2i(0, 0)));
                shader.addUniform(new Uniform2i(shader, "srcSize", new Vector2i(0, 0)));
                shader.addUniform(new Uniform2f(shader, "srcScale", new Vector2f(0, 0)));
                shader.addUniform(new Uniform1i(shader, "blend", 0));

                shader.addUniform(new Uniform4f(shader, "colorFactor", new Vector4f(1f, 1f, 1f, 1f)));
                shader.addUniform(new Uniform4f(shader, "colorAdd", new Vector4f(0f, 0f, 0f, 0f)));
                shader.addUniform(new Uniform4f(shader, "specularFactor", new Vector4f(1f, 1f, 1f, 1f)));
                shader.addUniform(new Uniform4f(shader, "specularAdd", new Vector4f(0, 0, 0, 0)));
                shader.addUniform(new Uniform4f(shader, "emissionFactor", new Vector4f(1f, 1f, 1f, 1f)));
                shader.addUniform(new Uniform4f(shader, "emissionAdd", new Vector4f(0, 0, 0, 0)));
                shader.addUniform(new Uniform4f(shader, "metallicFactor", new Vector4f(1f, 1f, 1f, 1f)));
                shader.addUniform(new Uniform4f(shader, "metallicAdd", new Vector4f(0, 0, 0, 0)));
            } catch (GLUnknownUniformException e) {
                CerberusRegistry.getInstance().warning("Failed to load patch shader for gui manager");
                CerberusRegistry.getInstance().getService(CerberusEvent.class)
                        .executeFullEIF(new ExceptionEvent(CerberusRenderer.class, e));
            }
        }

        @Override
        public ShaderResource get() {
            ShaderBoard shaderBoard = CerberusRegistry.getInstance().getService(CerberusRenderer.class).getShaderBoard();
            ShaderCodeLoader codeLoader = shaderBoard.getShaderCodeLoader();

            return new BaseComputeShaderResource(
                    codeLoader.resourceFromName("util/PatchComp.glsl"), this::initShader);
        }
    }

    private static class RectangleSupplier implements Supplier<ShaderResource> {

        private void initShader(Shader shader) {
            try {
                shader.addUniform(new Uniform2i(shader, "destCoord", new Vector2i(0)));
                shader.addUniform(new Uniform2i(shader, "destSize", new Vector2i(0)));

                shader.addUniform(new Uniform4f(shader, "colorFactor", new Vector4f(0, 0, 0, 0)));
                shader.addUniform(new Uniform4f(shader, "specularFactor", new Vector4f(0, 0, 0, 0)));
                shader.addUniform(new Uniform4f(shader, "emissionFactor", new Vector4f(0, 0, 0, 0)));
                shader.addUniform(new Uniform4f(shader, "metallicFactor", new Vector4f(0, 0, 0, 0)));

                shader.addUniform(new Uniform3f(shader, "normalFactor", new Vector3f(1, 1, 0)));
                shader.addUniform(new Uniform3f(shader, "displacementFactor", new Vector3f(0, 0, 0)));

            } catch (GLUnknownUniformException e) {
                CerberusRegistry.getInstance().warning("Failed to load rectangle shader for gui manager");
                CerberusRegistry.getInstance().getService(CerberusEvent.class)
                        .executeFullEIF(new ExceptionEvent(CerberusRenderer.class, e));
            }
        }

        @Override
        public ShaderResource get() {
            ShaderBoard shaderBoard = CerberusRegistry.getInstance().getService(CerberusRenderer.class).getShaderBoard();
            ShaderCodeLoader codeLoader = shaderBoard.getShaderCodeLoader();

            return new BaseComputeShaderResource(codeLoader.resourceFromName("util/RectangleComp.glsl"), this::initShader);
        }
    }

    @SuppressWarnings("DuplicatedCode")
    private static class FontSupplier implements Supplier<ShaderResource> {

        private void initShader(Shader shader) {
            try {
                shader.addUniform(new Uniform2i(shader, "atlasDim", new Vector2i(0)));
                shader.addUniform(new Uniform2i(shader, "cellSize", new Vector2i(0)));

                shader.addUniform(new Uniform2i(shader, "destCoord", new Vector2i(0)));
                shader.addUniform(new Uniform2i(shader, "destSize", new Vector2i(0)));

                shader.addUniform(new Uniform1i(shader, "colorSamp", SceneNote.COLOR));
                shader.addUniform(new Uniform1i(shader, "normalSamp", SceneNote.NORMAL));
                shader.addUniform(new Uniform1i(shader, "specularSamp", SceneNote.SPECULAR));
                shader.addUniform(new Uniform1i(shader, "emissionSamp", SceneNote.EMISSION));
                shader.addUniform(new Uniform1i(shader, "metallicSamp", SceneNote.METALLIC));
                shader.addUniform(new Uniform1i(shader, "depthSamp", SceneNote.DISPLACEMENT));
            } catch (GLUnknownUniformException e) {
                CerberusRegistry.getInstance().warning("Failed to load font shader for gui manager");
                CerberusRegistry.getInstance().getService(CerberusEvent.class)
                        .executeFullEIF(new ExceptionEvent(CerberusRenderer.class, e));
            }
        }

        @Override
        public ShaderResource get() {
            ShaderBoard shaderBoard = CerberusRegistry.getInstance().getService(CerberusRenderer.class).getShaderBoard();
            ShaderCodeLoader codeLoader = shaderBoard.getShaderCodeLoader();

            return new BaseComputeShaderResource(codeLoader.resourceFromName("util/FontComp.glsl"), this::initShader);
        }
    }

    private static class DrawSupplier implements Supplier<ShaderResource> {

        private void initShader(Shader shader) {
            try {
                shader.addUniform(new Uniform1i(shader, "sampler", 0));
            } catch (GLUnknownUniformException e) {
                CerberusRegistry.getInstance().warning("Failed to load draw shader for gui manager");
                CerberusRegistry.getInstance().getService(CerberusEvent.class)
                        .executeFullEIF(new ExceptionEvent(CerberusRenderer.class, e));
            }
        }

        @Override
        public ShaderResource get() {
            ShaderBoard shaderBoard = CerberusRegistry.getInstance().getService(CerberusRenderer.class).getShaderBoard();
            ShaderCodeLoader codeLoader = shaderBoard.getShaderCodeLoader();

            ShaderCodeResource vertexCode = codeLoader.resourceFromName("util/CFXDrawVer.glsl");
            ShaderCodeResource fragmentCode = codeLoader.resourceFromName("util/CFXDrawFrag.glsl");

            return new BaseShaderResource(vertexCode, fragmentCode, this::initShader);
        }
    }
}
