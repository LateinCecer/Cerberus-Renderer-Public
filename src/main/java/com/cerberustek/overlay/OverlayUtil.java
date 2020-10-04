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

package com.cerberustek.overlay;

import com.cerberustek.exceptions.GLUnknownUniformException;
import com.cerberustek.geometry.ComponentType;
import com.cerberustek.geometry.GeometryBoard;
import com.cerberustek.geometry.Vertex;
import com.cerberustek.geometry.impl.VertexImpl;
import com.cerberustek.logic.math.Vector2f;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.logic.math.Vector3f;
import com.cerberustek.logic.math.Vector4f;
import com.cerberustek.resource.impl.*;
import com.cerberustek.resource.model.ModelResource;
import com.cerberustek.resource.shader.ShaderCodeResource;
import com.cerberustek.resource.shader.ShaderResource;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.shader.uniform.*;
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.ShaderBoard;
import com.cerberustek.shader.code.ShaderCodeLoader;
import com.cerberustek.util.BufferUtil;

import java.nio.FloatBuffer;

public class OverlayUtil {

    public final static Vector3f RESET_COLOR = new Vector3f(1, 1, 1);
    public final static Vector3f SELECTED_COLOR = new Vector3f(0.8f, 0.8f, 1);
    public final static Vector3f DISABLED_COLOR = new Vector3f(0.3f, 0.3f, 0.3f);

    private static OverlayUtil instance;

    private ModelResource postProcessingScreen;
    private ModelResource textureScreen;
    private ShaderResource displayShader;
    private ShaderResource mergeShader;
    private ShaderResource backgroundShader;
    private CerberusRenderer renderer;

    private OverlayUtil() {
        {
            final Vector3f[] vertices = new Vector3f[] {
                    new Vector3f(-1f, 1f, 0),
                    new Vector3f(1f, 1f, 0),
                    new Vector3f(1f, -1f, 0),
                    new Vector3f(-1f, -1f, 0)
            };
            final Vector2f[] uv = new Vector2f[] {
                    new Vector2f(0, 0),
                    new Vector2f(0, 1),
                    new Vector2f(1, 1),
                    new Vector2f(1, 0)
            };
            final int[] indices = new int[] {
                    0, 1, 2,
                    2, 3, 0
            };

            FloatBuffer vertexBuffer = BufferUtil.createFlippedBuffer(vertices);
            FloatBuffer uvBuffer = BufferUtil.createFlippedBuffer(uv);

            StaticVertexAttribResource<FloatBuffer> vertexAttrib = new StaticVertexAttribResource<>(
                    new StaticFloatBufferResource(vertexBuffer), 0, 12, 0
            );
            StaticVertexAttribResource<FloatBuffer> uvAttrib = new StaticVertexAttribResource<>(
                    new StaticFloatBufferResource(uvBuffer), 0, 8, 0
            );
            CompoundVertexBufferResource vbo = new CompoundVertexBufferResource();
            vbo.setAttribute(GeometryBoard.VERTEX, vertexAttrib);
            // vbo.setAttribute(GeometryBoard.TEXCOORD_0, uvAttrib);
            StaticIndexBufferArrayResource ibo = new StaticIndexBufferArrayResource(
                    indices, new long[] {}, ComponentType.UNSIGNED_INT
            );
            postProcessingScreen = new ContainerModelResource(vbo, ibo, 0);
        }
        {
            final Vertex[] vertices = new Vertex[]{
                    new VertexImpl(new Vector3f(1f, -1f, 0), new Vector2i(1, 1)),
                    new VertexImpl(new Vector3f(1f, 1f, 0), new Vector2i(1, 0)),
                    new VertexImpl(new Vector3f(-1f, 1f, 0), new Vector2i(0, 0)),
                    new VertexImpl(new Vector3f(-1f, -1f, 0), new Vector2i(0, 1))
            };
            final int[] indices = new int[] {
                    0, 3, 2,
                    2, 1, 0
            };
            textureScreen = new SimpleModelResource(vertices, indices);
        }

        renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        renderer.getGeometryBoard().loadMesh(postProcessingScreen);
        renderer.getGeometryBoard().loadMesh(textureScreen);

        ShaderBoard shaderBoard = renderer.getShaderBoard();
        ShaderCodeLoader codeLoader = shaderBoard.getShaderCodeLoader();
        ShaderCodeResource fragmentHUD = codeLoader.resourceFromName("util/HUDFrag.glsl");
        ShaderCodeResource vertexHUD = codeLoader.resourceFromName("util/HUDVer.glsl");

        ShaderCodeResource fragmentMerge = codeLoader.resourceFromName("cerberus/CopyFrag.glsl");
        ShaderCodeResource vertexMerge = codeLoader.resourceFromName("cerberus/CopyVer.glsl");

        ShaderCodeResource fragmentBackground = codeLoader.resourceFromName("cerberus/SingleColorFrag.glsl");
        ShaderCodeResource vertexBackground = codeLoader.resourceFromName("cerberus/SingleColorVer.glsl");

        displayShader = new BaseShaderResource(vertexHUD, fragmentHUD, shader -> {
            try {
                shader.addUniform(new Uniform2f(shader, "scale", new Vector2f(1, 1)));
                shader.addUniform(new Uniform2f(shader, "trans", new Vector2f(0, 0)));
                shader.addUniform(new Uniform2f(shader, "uv_offset", new Vector2f(0, 0)));
                shader.addUniform(new Uniform3f(shader, "color", new Vector3f(1, 1, 1)));
                shader.addUniform(new Uniform2f(shader, "size", new Vector2f(1, 1)));
                shader.addUniform(new Uniform1i(shader, "sampler", 0));
                shader.addUniform(new Uniform2f(shader, "uv_filtering", new Vector2f(0, 0)));
            } catch (GLUnknownUniformException e) {
                e.printStackTrace();
            }
        });

        mergeShader = new BaseShaderResource(vertexMerge, fragmentMerge, shader -> {
            try {
                shader.addUniform(new Uniform1i(shader, "sampler", 0));
            } catch (GLUnknownUniformException e) {
                e.printStackTrace();
            }
        });

        backgroundShader = new BaseShaderResource(vertexBackground, fragmentBackground, shader -> {
            try {
                shader.addUniform(new Uniform4f(shader, "color", new Vector4f(0, 0, 0, 0)));
            } catch (GLUnknownUniformException e) {
                e.printStackTrace();
            }
        });
    }

    public static OverlayUtil getInstance() {
        if (instance == null)
            instance = new OverlayUtil();
        return instance;
    }

    public ModelResource getScreenMesh() {
        return postProcessingScreen;
    }

    public ModelResource getTextureScreen() {
        return textureScreen;
    }

    public ShaderResource getScreenShader() {
        return displayShader;
    }

    public ShaderResource getMergeShader() {
        return mergeShader;
    }

    public ShaderResource getBackgroundShader() {
        return backgroundShader;
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }

    public void updateUniforms(double t) {
        getShader(displayShader).update(t);
        getShader(mergeShader).update(t);
    }

    private Shader getShader(ShaderResource resource) {
        CerberusRenderer renderer = getRenderer();
        Shader shader = renderer.getShaderBoard().getShader(resource);
        if (shader == null) {
            renderer.getShaderBoard().loadShader(resource);
            return getShader(resource);
        }
        return shader;
    }

    public void setScale(Vector2f value) {
        getUniform(displayShader, "scale", Uniform2f.class).set(value);
    }

    public void setTranslation(Vector2f value) {
        getUniform(displayShader, "trans", Uniform2f.class).set(value);
    }

    public void setColor(Vector3f color) {
        getUniform(displayShader, "color", Uniform3f.class).set(color);
    }

    public void setUVOffset(Vector2f offset) {
        getUniform(displayShader, "uv_offset", Uniform2f.class).set(offset);
    }

    public void setSize(Vector2f size) {
        getUniform(displayShader, "size", Uniform2f.class).set(size);
    }

    public void setSamplerUnit(int unit) {
        getUniform(displayShader, "sampler", Uniform1i.class).set(unit);
    }

    public void setMergeSampler(int unit) {
        getUniform(mergeShader, "sampler", Uniform1i.class).set(unit);
    }

    public void setUVFiltering(Vector2f filtering) {
        getUniform(displayShader, "uv_filtering", Uniform2f.class).set(filtering);
    }

    public void setBackgroundColor(Vector4f color) {
        getUniform(backgroundShader, "color", Uniform4f.class);
    }

    private <T extends Uniform> T getUniform(ShaderResource shaderResource, String name, Class<T> clazz) {
        Shader shader = getShader(shaderResource);
        if (shader == null)
            throw new IllegalStateException("The shader for the specified uniform does not exist!");
        return shader.getUniform(name, clazz);
    }
}
