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

package com.cerberustek.pipeline.impl.notes;

import com.cerberustek.logic.math.*;
import com.cerberustek.resource.impl.BaseShaderResource;
import com.cerberustek.resource.impl.FrameBufferResource;
import com.cerberustek.resource.shader.ShaderCodeResource;
import com.cerberustek.resource.shader.ShaderResource;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.shader.uniform.*;
import com.cerberustek.pipeline.InputProvider;
import com.cerberustek.pipeline.impl.RenderNote;
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.ShaderBoard;
import com.cerberustek.shader.code.ShaderCodeLoader;
import com.cerberustek.texture.TextureBoard;
import com.cerberustek.util.RenderUtil;
import com.cerberustek.window.Window;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public abstract class PostProcessingNote extends RenderNote implements InputProvider {

    private FrameBufferResource frameBuffer;
    protected final ShaderResource shader;

    private CerberusRenderer renderer;
    private DisplayNote displayNote;

    public PostProcessingNote(ShaderResource shader, InputProvider... textures) {
        CerberusRenderer renderer = getRenderer();
        frameBuffer = setupFrameBuffer(null, renderer.getWindow().getScreenSize());
        if (frameBuffer != null)
            renderer.getTextureBoard().loadTexture(frameBuffer);
        renderer.getShaderBoard().loadShader(this.shader = shader);

        InputProvider[] displayTextures = new InputProvider[textures.length + 1];
        System.arraycopy(textures, 0, displayTextures, 0, textures.length);
        displayTextures[displayTextures.length - 1] = this;
        displayNote = new DisplayNote(this.shader, displayTextures);
    }

    public PostProcessingNote(String vertexShader, String fragmentShader, InputProvider... textures) {
        CerberusRenderer renderer = getRenderer();
        ShaderBoard shaderBoard = renderer.getShaderBoard();
        ShaderCodeLoader codeLoader = shaderBoard.getShaderCodeLoader();

        ShaderCodeResource vertexCode = codeLoader.resourceFromName(vertexShader);
        ShaderCodeResource fragmentCode = codeLoader.resourceFromName(fragmentShader);

        if (vertexCode == null)
            CerberusRegistry.getInstance().warning("Did not find vertex shader: " + vertexShader + "!");
        if (fragmentCode == null)
            CerberusRegistry.getInstance().warning("Did not find fragment shader: " + fragmentShader + "!");

        shader = new BaseShaderResource(vertexCode, fragmentCode, this::setupShader);
        frameBuffer = setupFrameBuffer(null, renderer.getWindow().getScreenSize());

        shaderBoard.loadShader(shader);
        if (frameBuffer != null)
            renderer.getTextureBoard().loadTexture(frameBuffer);

        displayNote = new DisplayNote(shader, textures);
    }

    @Override
    public void destroy() {
        CerberusRenderer renderer = getRenderer();
        renderer.getShaderBoard().deleteShader(shader);
        if (frameBuffer != null)
            renderer.getTextureBoard().deleteTexture(frameBuffer);

        displayNote.destroy();
    }

    /**
     * Will reinitialize the render note.
     *
     * This is especially important for all render notes that
     * use framebuffers to store screenspace textures in
     * deferred rendering.
     * This method should be overwritten if the postprocessing
     * note uses shader code with is dependent on the screensize,
     * such as downscaling shaders, blur shaders, bloom shaders
     * and alike.
     * @param window The current window. Carries e.g. framebuffer
     *               size information
     */
    public void reinit(Window window) {
        CerberusRenderer renderer = getRenderer();
        TextureBoard textureBoard = renderer.getTextureBoard();
        // textureBoard.deleteTexture(frameBuffer);

        // System.out.println("reinit processing note!");


        frameBuffer = setupFrameBuffer(frameBuffer, window.getScreenSize());
        textureBoard.loadTexture(frameBuffer);
    }

    @Override
    public void update(double v) {
        getRenderer().getTextureBoard().bindFrameBuffer(frameBuffer);
        RenderUtil.clear();
        displayNote.update(v);
    }

    protected CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }

    abstract protected void setupShader(Shader shader);
    abstract protected FrameBufferResource setupFrameBuffer(FrameBufferResource frameBuffer, Vector2i screenSize);

    @SuppressWarnings("Duplicates")
    protected Shader getShader() {
        CerberusRenderer renderer = getRenderer();
        Shader output = renderer.getShaderBoard().getShader(shader);
        if (output == null) {
            renderer.getShaderBoard().loadShader(shader);
            output = renderer.getShaderBoard().getShader(shader);
        }
        return output;
    }

    @Override
    public FrameBufferResource fetchOutput() {
        return frameBuffer;
    }

    protected boolean set1i(String name, int value) {
        Shader shader = getShader();
        if (shader == null)
            return false;

        shader.getUniform(name, Uniform1i.class).set(value);
        return true;
    }

    protected boolean set1f(String name, float value) {
        Shader shader = getShader();
        if (shader == null)
            return false;

        shader.getUniform(name, Uniform1f.class).set(value);
        return true;
    }

    protected boolean set2i(String name, Vector2i value) {
        Shader shader = getShader();
        if (shader == null)
            return false;

        shader.getUniform(name, Uniform2i.class).set(value);
        return true;
    }

    protected boolean set2f(String name, Vector2f value) {
        Shader shader = getShader();
        if (shader == null)
            return false;

        shader.getUniform(name, Uniform2f.class).set(value);
        return true;
    }

    protected boolean set3i(String name, Vector3i value) {
        Shader shader = getShader();
        if (shader == null)
            return false;

        shader.getUniform(name, Uniform3i.class).set(value);
        return true;
    }

    protected boolean set3f(String name, Vector3f value) {
        Shader shader = getShader();
        if (shader == null)
            return false;

        shader.getUniform(name, Uniform3f.class).set(value);
        return true;
    }

    protected boolean set4f(String name, Quaternionf value) {
        Shader shader = getShader();
        if (shader == null)
            return false;

        shader.getUniform(name, Uniform4f.class).set(value);
        return true;
    }

    protected boolean set4f(String name, Vector4f value) {
        Shader shader = getShader();
        if (shader == null)
            return false;

        shader.getUniform(name, Uniform4f.class).set(value);
        return true;
    }

    protected boolean set4i(String name, Vector4i value) {
        Shader shader = getShader();
        if (shader == null)
            return false;

        shader.getUniform(name, Uniform4i.class).set(value);
        return true;
    }

    protected boolean setArrayi(String name, int[] value) {
        Shader shader = getShader();
        if (shader == null)
            return false;

        shader.getUniform(name, UniformArrayi.class).set(value);
        return true;
    }

    protected boolean setArrayf(String name, float[] value) {
        Shader shader = getShader();
        if (shader == null)
            return false;

        shader.getUniform(name, UniformArrayf.class).set(value);
        return true;
    }

    protected boolean setBufferi(String name, IntBuffer value) {
        Shader shader = getShader();
        if (shader == null)
            return false;

        shader.getUniform(name, UniformBufferi.class).set(value);
        return true;
    }

    protected boolean setBufferf(String name, FloatBuffer value) {
        Shader shader = getShader();
        if (shader == null)
            return false;

        shader.getUniform(name, UniformBufferf.class).set(value);
        return true;
    }

    protected boolean setMatrix(String name, Matrix4f mat) {
        Shader shader = getShader();
        if (shader == null)
            return false;

        shader.getUniform(name, UniformMatrix4f.class).set(mat);
        return true;
    }

    protected Integer get1i(String name) {
        Shader shader = getShader();
        if (shader == null)
            return null;

        return shader.getUniform(name, Uniform1i.class).get();
    }

    protected Float get1f(String name) {
        Shader shader = getShader();
        if (shader == null)
            return null;

        return shader.getUniform(name, Uniform1f.class).get();
    }

    protected Vector2i get2i(String name) {
        Shader shader = getShader();
        if (shader == null)
            return null;

        return shader.getUniform(name, Uniform2i.class).get();
    }

    protected Vector2f get2f(String name) {
        Shader shader = getShader();
        if (shader == null)
            return null;

        return shader.getUniform(name, Uniform2f.class).get();
    }

    protected Vector3i get3i(String name) {
        Shader shader = getShader();
        if (shader == null)
            return null;

        return shader.getUniform(name, Uniform3i.class).get();
    }

    protected Vector3f get3f(String name) {
        Shader shader = getShader();
        if (shader == null)
            return null;

        return shader.getUniform(name, Uniform3f.class).get();
    }

    protected Vector4f get4f(String name) {
        Shader shader = getShader();
        if (shader == null)
            return null;

        return shader.getUniform(name, Uniform4f.class).get();
    }

    protected Vector4i get4i(String name) {
        Shader shader = getShader();
        if (shader == null)
            return null;

        return shader.getUniform(name, Uniform4i.class).get();
    }

    protected int[] getArrayi(String name) {
        Shader shader = getShader();
        if (shader == null)
            return null;

        return shader.getUniform(name, UniformArrayi.class).get();
    }

    protected float[] getArrayf(String name) {
        Shader shader = getShader();
        if (shader == null)
            return null;

        return shader.getUniform(name, UniformArrayf.class).get();
    }

    protected IntBuffer getBufferi(String name) {
        Shader shader = getShader();
        if (shader == null)
            return null;

        return shader.getUniform(name, UniformBufferi.class).get();
    }

    protected FloatBuffer getBufferf(String name) {
        Shader shader = getShader();
        if (shader == null)
            return null;

        return shader.getUniform(name, UniformBufferf.class).get();
    }

    protected Matrix4f getMatrix(String name) {
        Shader shader = getShader();
        if (shader == null)
            return null;

        return shader.getUniform(name, UniformMatrix4f.class).get();
    }
}
