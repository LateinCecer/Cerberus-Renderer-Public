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

package com.cerberustek.gui.impl;

import com.cerberustek.CerberusEvent;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.buffer.BufferAccess;
import com.cerberustek.events.ExceptionEvent;
import com.cerberustek.events.ModificationFraudEvent;
import com.cerberustek.exceptions.GLComputeException;
import com.cerberustek.exceptions.GLShaderStateException;
import com.cerberustek.exceptions.GLShaderTypeException;
import com.cerberustek.exceptions.IllegalContextException;
import com.cerberustek.gui.*;
import com.cerberustek.logic.math.*;
import com.cerberustek.pipeline.impl.notes.SceneNote;
import com.cerberustek.resource.image.TextureResource;
import com.cerberustek.resource.impl.ImageTextureResource;
import com.cerberustek.resource.shader.ShaderResource;
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.ShaderBoard;
import com.cerberustek.shader.ShaderType;
import com.cerberustek.shader.impl.ComputeShader;
import com.cerberustek.shader.uniform.*;
import com.cerberustek.texture.Texture;
import com.cerberustek.texture.TextureBoard;
import com.cerberustek.texture.impl.TextureEmpty2D;
import com.cerberustek.CerberusRenderer;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.opengl.GL42.*;

@SuppressWarnings("DuplicatedCode")
public class FlatCanvas implements CFXCanvas {

    /** image texture to use for rendering */
    protected ImageTextureResource imageTexture;
    /** Cerberus renderer instance */
    private CerberusRenderer renderer;
    /** resolution of the image texture/canvas */
    protected Vector2i size;
    /** clear color */
    private Vector4f clearColor;
    /** clear specular */
    private Vector4f clearSpecular;
    /** clear emissive */
    private Vector4f clearEmissive;
    /** clear metallic roughness */
    private Vector4f clearMetallic;
    /** clear normal value */
    private Vector3f clearNormal;
    /** clear displacement value */
    private Vector3f clearDisplacement;

    /** current patch offset in pixels */
    protected Vector2i currentOffset;
    /** current patch size in pixels */
    protected Vector2i currentSize;

    public FlatCanvas(Vector2i size) {
        this.size = size;
        this.clearColor = new Vector4f(0, 0, 0, 0);
        this.clearSpecular = new Vector4f(0, 0, 0, 0);
        this.clearEmissive = new Vector4f(0, 0, 0, 0);
        this.clearMetallic = new Vector4f(0, 0, 0, 0);
        this.clearNormal = new Vector3f(1, 1, 0);
        this.clearDisplacement = new Vector3f(0, 0, 0);

        currentOffset = new Vector2i(0, 0);
        currentSize = size;
    }

    @Override
    public void init() {
        initImageTexture();
    }

    protected void initImageTexture() {
        TextureBoard textureBoard = getRenderer().getTextureBoard();
        if (imageTexture != null) {
            // delete existing instance
            textureBoard.deleteTexture(imageTexture);
        }

        imageTexture = new ImageTextureResource(createBaseTexture());
        textureBoard.loadTexture(imageTexture);
    }

    protected TextureEmpty2D createBaseTexture() {
        CFXManager guiManager = getRenderer().getGUIManager();
        TextureEmpty2D base = new TextureEmpty2D(6);
        base.genTextures();

        base.initTexture(0, SceneNote.COLOR, size, guiManager.getImageType());
        base.initTexture(1, SceneNote.NORMAL, size, guiManager.getImageType());
        base.initTexture(2, SceneNote.SPECULAR, size, guiManager.getImageType());
        base.initTexture(3, SceneNote.EMISSION, size, guiManager.getImageType());
        base.initTexture(4, SceneNote.METALLIC, size, guiManager.getImageType());
        base.initTexture(5, SceneNote.DISPLACEMENT, size, guiManager.getImageType());
        return base;
    }

    @Override
    public void dispatch(ComputeShader computeShader) throws GLShaderStateException, IllegalContextException {
        dispatch(computeShader, null, null);
    }

    @Override
    public void dispatch(ComputeShader computeShader, Vector2i destCoord, Vector2i patchSize)
            throws GLShaderStateException, IllegalContextException {

        dispatch(computeShader, destCoord, patchSize, null, 1);
    }

    @Override
    public void dispatch(ComputeShader computeShader, Vector2i destCoord, Vector2i patchSize, Vector2i srcSize, int z)
            throws GLShaderStateException, IllegalContextException {
        if (z < 1)
            throw new IllegalArgumentException("Illegal compute work group size");

        ShaderBoard shaderBoard = getRenderer().getShaderBoard();
        if (!computeShader.equals(shaderBoard.getCurrentlyBound()))
            throw new GLShaderStateException(shaderBoard.getCurrentlyBound(), computeShader);
        if (!getRenderer().getWindow().isGlThread())
            throw new IllegalContextException();

        if (computeShader.hasUniform("destSize"))
            computeShader.getUniform("destSize", Uniform2i.class).set(patchSize != null ? patchSize : currentSize);
        if (computeShader.hasUniform("destCoord"))
            computeShader.getUniform("destCoord", Uniform2i.class).set(destCoord != null ? destCoord : currentOffset);
        computeShader.update(0);

        // bind image
        getRenderer().getTextureBoard().bindImageTexture(imageTexture, BufferAccess.WRITE_ONLY);
        try {
            int width;
            int height;
            if (srcSize == null) {

                if (patchSize == null) {
                    // groupSize ~ currentSize
                    width = (int) Math.ceil((double) currentSize.getX() / (double) computeShader.getLocalGroupSize().getX());
                    height = (int) Math.ceil((double) currentSize.getY() / (double) computeShader.getLocalGroupSize().getY());
                } else {
                    // groupSize ~ patchSize
                    width = (int) Math.ceil((double) patchSize.getX() / (double) computeShader.getLocalGroupSize().getX());
                    height = (int) Math.ceil((double) patchSize.getY() / (double) computeShader.getLocalGroupSize().getY());
                }

            } else {
                // groupSize ~ srcSize
                width = (int) Math.ceil((double) srcSize.getX() / (double) computeShader.getLocalGroupSize().getX());
                height = (int) Math.ceil((double) srcSize.getY() / (double) computeShader.getLocalGroupSize().getY());
            }

            shaderBoard.dispatchCompute(new Vector3i(width, height, z));
            glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
        } catch (GLComputeException | GLShaderTypeException e) {
            CerberusRegistry.getInstance().warning("Failed to render to canvas");
            CerberusRegistry.getInstance().getService(CerberusEvent.class)
                    .executeFullEIF(new ExceptionEvent(CerberusRenderer.class, e));
        }
        getRenderer().getTextureBoard().unbindImageTexture(imageTexture);
    }

    @Override
    public @NotNull Vector2i getOffset() {
        return currentOffset;
    }

    @Override
    public @NotNull Vector2i getSize() {
        return currentSize;
    }

    @Override
    public void setOffset(@NotNull Vector2i offset) {
        if (offset.getX() < 0)
            offset.setX(0);
        if (offset.getY() < 0)
            offset.setY(0);

        this.currentOffset = offset;
    }

    @Override
    public void setSize(@NotNull Vector2i size) {
        if (size.getX() < 1 || size.getY() < 1)
            throw new IllegalArgumentException("patch size has to be positive");

        if (size.getX() + currentOffset.getX() > this.size.getX())
            size.setX(this.size.getX() - currentOffset.getX());
        if (size.getY() + currentOffset.getY() > this.size.getY())
            size.setY(this.size.getY() - currentOffset.getY());

        this.currentSize = size;
    }

    @Override
    public @NotNull ImageTextureResource getImage() {
        return imageTexture;
    }

    @Override
    public @NotNull Vector2i getResolution() {
        return size;
    }

    @Override
    public void setResolution(@NotNull Vector2i resolution) {
        this.size = resolution;
        initImageTexture();

        setOffset(new Vector2i(0));
        setSize(size);
    }

    @Override
    public void clear() {
        getRenderer().tryGLTask(t -> {
            setOffset(new Vector2i(0));
            setSize(size);

            drawRectangle(new Vector2i(0), size, null, null, null,
                    null, null, null);
        });
    }

    @Override
    public @NotNull Vector4f getClearColor() {
        return clearColor;
    }

    @Override
    public void setClearColor(@NotNull Vector4f clearColor) {
        this.clearColor = clearColor;
    }

    @Override
    public @NotNull Vector4f getClearSpecular() {
        return clearSpecular;
    }

    @Override
    public void setClearSpecular(@NotNull Vector4f clearSpecular) {
        this.clearSpecular = clearSpecular;
    }

    @Override
    public @NotNull Vector4f getClearEmission() {
        return clearEmissive;
    }

    @Override
    public void setClearEmission(@NotNull Vector4f clearEmission) {
        this.clearEmissive = clearEmission;
    }

    @Override
    public @NotNull Vector4f getClearMetallic() {
        return clearMetallic;
    }

    @Override
    public void setClearMetallic(@NotNull Vector4f clearMetallic) {
        this.clearMetallic = clearMetallic;
    }

    @Override
    public @NotNull Vector3f getClearNormal() {
        return clearNormal;
    }

    @Override
    public void setClearNormal(@NotNull Vector3f clearNormal) {
        this.clearNormal = clearNormal;
    }

    @Override
    public @NotNull Vector3f getClearDisplacement() {
        return clearDisplacement;
    }

    @Override
    public void setClearDisplacement(@NotNull Vector3f clearDisplacement) {
        this.clearDisplacement = clearDisplacement;
    }

    @Override
    public void drawRectangle(@NotNull Vector2i coord, @NotNull Vector2i size, Vector4f color, Vector4f specular,
                              Vector4f emission, Vector4f metallic, Vector3f normal, Vector3f displacement)
            throws IllegalContextException {

        if (!getRenderer().getWindow().isGlThread())
            throw new IllegalContextException();

        // do some clipping
        coord.addSelf(currentOffset);
        if (doClipping(coord, size))
            return;

        // load the shader
        ComputeShader shader = prepareRectangleShader();

        shader.getUniform("colorFactor", Uniform4f.class).set(color != null ? color : clearColor);
        shader.getUniform("normalFactor", Uniform3f.class).set(normal != null ? normal : clearNormal);
        shader.getUniform("specularFactor", Uniform4f.class).set(specular != null ? specular : clearSpecular);
        shader.getUniform("emissionFactor", Uniform4f.class).set(emission != null ? emission : clearEmissive);
        shader.getUniform("metallicFactor", Uniform4f.class).set(metallic != null ? metallic : clearMetallic);
        shader.getUniform("displacementFactor", Uniform3f.class).set(displacement != null ? displacement : clearDisplacement);

        dispatch(shader, coord, size);
    }

    @Override
    public void drawRectangle(@NotNull Vector2i coord, @NotNull Vector2i size, Vector4f color)
            throws IllegalContextException {
        drawRectangle(coord, size, color, null, null,
                null, null, null);
    }

    @Override
    public void drawTexture(@NotNull TextureResource texture, @NotNull Vector2i srcCoord, @NotNull Vector2i destCoord,
                            boolean blend) {
        drawTexture(texture, srcCoord, srcCoord, destCoord, false);
    }

    @Override
    public void drawTexture(@NotNull TextureResource texture, @NotNull Vector2i srcCoord, @NotNull Vector2i destCoord) {
        drawTexture(texture, srcCoord, destCoord, new Vector2f(1));
    }

    @Override
    public void drawTexture(@NotNull TextureResource texture, @NotNull Vector2i srcCoord, @NotNull Vector2i destCoord,
                            @NotNull Vector2f scale, boolean blend) {
        ComputeShader shader = preparePatchShader();
        if (shader == null) {
            // cannot draw patch
            return;
        }

        Texture tex = getRenderer().getTextureBoard().bindTexture(texture);
        if (tex == null) {
            CerberusRegistry.getInstance().warning("Could not bind Texture");
            return;
        }

        Vector2i srcSize = tex.getSize(0).xy();
        Vector2i size = srcSize.toVector2f().mul(scale).toVector2i();
        // clipping
        destCoord.addSelf(currentOffset);
        if (doClipping(destCoord, size))
            return;

        shader.getUniform("srcCoord", Uniform2i.class).set(srcCoord);
        shader.getUniform("srcSize", Uniform2i.class).set(srcSize);
        shader.getUniform("srcScale", Uniform2f.class).set(scale);
        shader.getUniform("blend", Uniform1i.class).set(blend ? 1 : 0);

        shader.getUniform("colorFactor", Uniform4f.class).set(new Vector4f(1, 1, 1, 1));
        shader.getUniform("colorAdd", Uniform4f.class).set(clearColor);
        shader.getUniform("specularFactor", Uniform4f.class).set(new Vector4f(1, 1, 1, 1));
        shader.getUniform("specularAdd", Uniform4f.class).set(clearSpecular);
        shader.getUniform("emissionFactor", Uniform4f.class).set(new Vector4f(1, 1, 1, 1));
        shader.getUniform("emissionAdd", Uniform4f.class).set(clearEmissive);
        shader.getUniform("metallicFactor", Uniform4f.class).set(new Vector4f(1, 1, 1, 1));
        shader.getUniform("metallicAdd", Uniform4f.class).set(clearMetallic);

        dispatch(shader, destCoord, size);
    }

    @Override
    public void drawTexture(@NotNull TextureResource texture, @NotNull Vector2i srcCoord, @NotNull Vector2i destCoord,
                            @NotNull Vector2f scale) {
        drawTexture(texture, srcCoord, destCoord, scale, false);
    }

    @Override
    public void drawTexture(@NotNull TextureResource texture, @NotNull Vector2i srcCoord, @NotNull Vector2i destCoord,
                            @NotNull Vector2i size, boolean blend) {
        drawTexture(texture, srcCoord, destCoord, size, null, null, null, null,
                null, null, null, null, blend);
    }

    @Override
    public void drawTexture(@NotNull TextureResource texture, @NotNull Vector2i srcCoord, @NotNull Vector2i destCoord,
                            @NotNull Vector2i size) {
        drawTexture(texture, srcCoord, destCoord, size, null, null, null, null,
                null, null, null, null);
    }

    @Override
    public void drawTexture(@NotNull TextureResource texture, @NotNull Vector2i srcCoord, @NotNull Vector2i destCoord,
                            @NotNull Vector2i size, Vector4f colorFactor, Vector4f colorAdd, boolean blend) {

        drawTexture(texture, srcCoord, destCoord, size, colorFactor, colorAdd, null, null,
                null, null, null, null, blend);
    }

    @Override
    public void drawTexture(@NotNull TextureResource texture, @NotNull Vector2i srcCoord, @NotNull Vector2i destCoord,
                            @NotNull Vector2i size, Vector4f colorFactor, Vector4f colorAdd) {

        drawTexture(texture, srcCoord, destCoord, size, colorFactor, colorAdd, null, null,
                null, null, null, null);
    }

    @Override
    public void drawTexture(@NotNull TextureResource texture, @NotNull Vector2i srcCoord, @NotNull Vector2i destCoord,
                            @NotNull Vector2i size, Vector4f colorFactor, Vector4f colorAdd, Vector4f specularFactor,
                            Vector4f specularAdd, Vector4f emissionFactor, Vector4f emissionAdd,
                            Vector4f metallicFactor, Vector4f metallicAdd, boolean blend) {

        destCoord.addSelf(currentOffset);
        if (doClipping(destCoord, size))
            return;

        ComputeShader shader = preparePatchShader();
        if (shader == null) {
            // cannot draw texture
            return;
        }

        Texture tex = getRenderer().getTextureBoard().bindTexture(texture);
        if (tex == null) {
            CerberusRegistry.getInstance().warning("Could not bind Texture");
            return;
        }

        Vector2i srcSize = tex.getSize(0).xy();
        Vector2f scale = size.toVector2f().div(srcSize.toVector2f());
        shader.getUniform("srcCoord", Uniform2i.class).set(srcCoord);
        shader.getUniform("srcSize", Uniform2i.class).set(srcSize);
        shader.getUniform("srcScale", Uniform2f.class).set(scale);
        shader.getUniform("blend", Uniform1i.class).set(blend ? 1 : 0);

        shader.getUniform("colorFactor", Uniform4f.class).set(colorFactor != null ? colorFactor : new Vector4f(1, 1, 1, 1));
        shader.getUniform("colorAdd", Uniform4f.class).set(colorAdd != null ? colorAdd : clearColor);
        shader.getUniform("specularFactor", Uniform4f.class).set(specularFactor != null ? specularFactor : new Vector4f(1, 1, 1, 1));
        shader.getUniform("specularAdd", Uniform4f.class).set(specularAdd != null ? specularAdd : clearSpecular);
        shader.getUniform("emissionFactor", Uniform4f.class).set(emissionFactor != null ? emissionFactor : new Vector4f(1, 1, 1, 1));
        shader.getUniform("emissionAdd", Uniform4f.class).set(emissionAdd != null ? emissionAdd : clearEmissive);
        shader.getUniform("metallicFactor", Uniform4f.class).set(metallicFactor != null ? metallicFactor : new Vector4f(1, 1, 1, 1));
        shader.getUniform("metallicAdd", Uniform4f.class).set(metallicAdd != null ? metallicAdd : clearMetallic);

        dispatch(shader, destCoord, size);
    }

    @Override
    public void drawTexture(@NotNull TextureResource texture, @NotNull Vector2i srcCoord, @NotNull Vector2i destCoord,
                            @NotNull Vector2i size, Vector4f colorFactor, Vector4f colorAdd, Vector4f specularFactor,
                            Vector4f specularAdd, Vector4f emissionFactor, Vector4f emissionAdd,
                            Vector4f metallicFactor, Vector4f metallicAdd) {
        drawTexture(texture, srcCoord, destCoord, size, colorFactor, colorAdd, specularFactor, specularAdd,
                emissionFactor, emissionAdd, metallicFactor, metallicAdd, false);
    }

    @Override
    public void drawString(@NotNull CFXTextRenderContext renderContext, @NotNull Vector2i destCoord) {
        // do clipping
        destCoord.addSelf(currentOffset);
        Vector2i size = currentSize.copy();
        if (doClipping(destCoord, size))
            return;

        ComputeShader shader = prepareFontShader();
        if (shader == null) {
            // cannot draw string
            return;
        }

        ShaderBoard shaderBoard = getRenderer().getShaderBoard();
        int aCount = renderContext.size();
        for (int i = 0; i < aCount; i++) {
            CFXAlphabet alphabet = renderContext.getAlphabet(i);
            alphabet.bind();

            shaderBoard.bindSSBO(renderContext.getBufferResource(), i);

            shader.getUniform("atlasDim", Uniform2i.class).set(alphabet.getDimensions());
            dispatch(shader, destCoord, size, alphabet.getCellSize(), renderContext.getCharCount(i));
        }
    }

    @Override
    public void drawString(@NotNull CFXTextRenderContext renderContext) {
        ComputeShader shader = prepareFontShader();
        if (shader == null) {
            // cannot draw string
            return;
        }

        ShaderBoard shaderBoard = getRenderer().getShaderBoard();
        int aCount = renderContext.size();
        for (int i = 0; i < aCount; i++) {
            CFXAlphabet alphabet = renderContext.getAlphabet(i);
            alphabet.bind();

            // CFXFontRenderer fontRenderer = getRenderer().getGUIManager().getFontRenderer();
            // Font font = new Font("Fira Code Medium", Font.PLAIN, 24);
            // CFXCharacter testChar = fontRenderer.getCharacter('H', font);

            // TextureResource cobbleTexture = new BufferedTextureResource(new FileImageResource("textures/coord_test.png", 0));
            // getRenderer().getTextureBoard().bindTexture(cobbleTexture);
            // shaderBoard.bindSSBO(renderContext.getBufferResource(), i);

            // getRenderer().getTextureBoard().bindTexture(testChar.getAlphabet().getTextureAtlas());

            shader.getUniform("atlasDim", Uniform2i.class).set(alphabet.getDimensions());
            shader.getUniform("cellSize", Uniform2i.class).set(alphabet.getCellSize());
            // getRenderer().getTextureBoard().bindTexture(alphabet.getTextureAtlas());

            dispatch(shader, null, null, alphabet.getCellSize(), renderContext.getCharCount(i));
            // CerberusRegistry.getInstance().info("Successfully rendered text to canvas");
        }
    }

    private ComputeShader preparePatchShader() {
        return prepareShader(CFXShader.PATCH);
    }

    private ComputeShader prepareFontShader() {
        return prepareShader(CFXShader.FONT);
    }

    private ComputeShader prepareRectangleShader() {
        return prepareShader(CFXShader.RECTANGLE);
    }

    private ComputeShader prepareShader(CFXShader source) {
        CFXManager guiManager = getRenderer().getGUIManager();
        ShaderResource patchShader = guiManager.getShader(source);
        if (patchShader == null) {
            // cannot draw shader
            return null;
        }

        Shader shader = getRenderer().getShaderBoard().bindShader(patchShader);
        // check if the shader is a valid compute shader
        if (!(shader instanceof ComputeShader)) {
            CerberusRegistry.getInstance().warning(source.name() + " shader is not a compute shader");
            CerberusEvent eventService = CerberusRegistry.getInstance().getService(CerberusEvent.class);
            eventService.executeFullEIF(new ExceptionEvent(CerberusRenderer.class,
                    new GLShaderTypeException(patchShader, ShaderType.COMPUTE)));
            eventService.executeFullEIF(new ModificationFraudEvent(CerberusRenderer.class));
            return null;
        }
        return (ComputeShader) shader;
    }

    private boolean doClipping(Vector2i destCoord, Vector2i size) {
        if (size.getX() + destCoord.getX() < currentOffset.getX())
            return true;
        if (size.getY() + destCoord.getY() < currentOffset.getX())
            return true;

        if (size.getX() + destCoord.getX() > currentSize.getX() + currentOffset.getX())
            size.setX(currentSize.getX() + currentOffset.getX() - destCoord.getX());
        if (size.getY() + destCoord.getY() > currentSize.getY() + currentOffset.getY())
            size.setY(currentSize.getY() + currentOffset.getY() - destCoord.getY());

        if (destCoord.getX() < currentOffset.getX()) {
            size.addSelf(destCoord.getX() - currentOffset.getX(), 0);
            destCoord.setX(currentOffset.getX());
        }
        if (destCoord.getY() < currentOffset.getY()) {
            size.addSelf(0, destCoord.getY() - currentOffset.getY());
            destCoord.setY(currentOffset.getY());
        }
        return false;
    }

    @Override
    public void destroy() {
        getRenderer().getTextureBoard().deleteTexture(imageTexture);
    }

    protected CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }
}
