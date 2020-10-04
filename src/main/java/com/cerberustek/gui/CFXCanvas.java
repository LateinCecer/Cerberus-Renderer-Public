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

import com.cerberustek.Destroyable;
import com.cerberustek.Initable;
import com.cerberustek.exceptions.GLShaderStateException;
import com.cerberustek.exceptions.IllegalContextException;
import com.cerberustek.logic.math.Vector2f;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.logic.math.Vector3f;
import com.cerberustek.logic.math.Vector4f;
import com.cerberustek.resource.image.TextureResource;
import com.cerberustek.resource.impl.ImageTextureResource;
import com.cerberustek.shader.impl.ComputeShader;
import org.jetbrains.annotations.NotNull;

public interface CFXCanvas extends Initable, Destroyable {

    /**
     * Will execute the compute shader so that one local
     * working thread is executed for each pixel of the
     * area of the canvas that is to be drawn to.
     *
     * In addition, this method may update a few uniforms
     * for the compute shader in regards to this canvas.
     * The image texture of this canvas will be bound
     * automatically to the binding locations 0-7.
     *
     * Please keep in mind that a shader in the cerberus
     * rendering environment should only be bound from
     * through the use of the appropriate binding methods
     * in the shader board. This of cause means, that the
     * shader should already be bound, when passed to
     * this method.
     *
     * This method also has to be run inside a gl thread,
     * since the compute shader dispatch should happen
     * in sync with the context of this method in most
     * scenarios.
     *
     * @param computeShader compute shader to run
     * @throws GLShaderStateException the exception that
     *          will be thrown if the shader passed to this
     *          method is currently not bound
     * @throws IllegalContextException the exception that
     *          will be thrown if this method is called
     *          outside of a valid gl thread
     */
    void dispatch(ComputeShader computeShader) throws GLShaderStateException, IllegalContextException;


    void dispatch(ComputeShader computeShader, Vector2i destCoord, Vector2i patchSize) throws GLShaderStateException,
                IllegalContextException;

    void dispatch(ComputeShader computeShader, Vector2i destCoord, Vector2i patchSize, Vector2i srcSize, int z)
                throws GLShaderStateException, IllegalContextException;

    /**
     * Offset of the area that should be written to
     * in pixels.
     * @return offset
     */
    @NotNull
    Vector2i getOffset();

    /**
     * Will set the current offset of the canvas patch to
     * paint in pixels.
     * @param offset current patch offset in pixels
     */
    void setOffset(@NotNull Vector2i offset);

    /**
     * Size of the area that should be written to
     * in pixels.
     * @return size in pixels
     */
    @NotNull
    Vector2i getSize();

    /**
     * Will set the current size of the canvas patch to
     * paint in pixels.
     * @param size current patch size in pixels
     */
    void setSize(@NotNull Vector2i size);

    /**
     * Image texture resource of the image texture to
     * write to.
     * @return image texture
     */
    @NotNull
    ImageTextureResource getImage();

    /**
     * Returns the current resolution of the canvas.
     * @return will return the resoltion of the canvas
     */
    @NotNull
    Vector2i getResolution();

    /**
     * Will reset the resolution of the canvas.
     *
     * This will also cause the canvas to be cleared.
     * @param resolution new canvas resolution
     */
    void setResolution(@NotNull Vector2i resolution);

    /**
     * Will clear the entire canvas.
     *
     * The canvas color, specular, emissive and
     * metallic roughness image will be reset to their
     * respective clear values.
     */
    void clear();

    /**
     * Returns the current clear color of the canvas.
     * @return clear color
     */
    @NotNull
    Vector4f getClearColor();

    /**
     * Sets the clear color of the canvas
     * @param clearColor clear color
     */
    void setClearColor(@NotNull Vector4f clearColor);

    /**
     * Returns the current clear specular value of the
     * canvas.
     * @return clear specular
     */
    @NotNull
    Vector4f getClearSpecular();

    /**
     * Sets the clear specular value of the canvas.
     * @param clearSpecular specular value
     */
    void setClearSpecular(@NotNull Vector4f clearSpecular);

    /**
     * Returns the current clear emissive value of the
     * canvas.
     * @return clear emissive value
     */
    @NotNull
    Vector4f getClearEmission();

    /**
     * Sets the clear emissive value of the canvas.
     * @param clearEmission clear emissive value
     */
    void setClearEmission(@NotNull Vector4f clearEmission);

    /**
     * Returns the current clear metallic roughness
     * @return clear metallic roughness
     */
    @NotNull
    Vector4f getClearMetallic();

    /**
     * Sets the clear metallic roughness
     * @param clearMetallic metallic roughnesse
     */
    void setClearMetallic(@NotNull Vector4f clearMetallic);

    /**
     * Returns the clear normal value.
     * @return clear normal value
     */
    @NotNull
    Vector3f getClearNormal();

    /**
     * Sets the clear normal value.
     * @param clearNormal clear normal value
     */
    void setClearNormal(@NotNull Vector3f clearNormal);

    /**
     * Returns the clear displacement value.
     * @return clear displacement
     */
    @NotNull
    Vector3f getClearDisplacement();

    /**
     * Sets the clear displacement value
     * @param clearDisplacement clear displacement
     */
    void setClearDisplacement(@NotNull Vector3f clearDisplacement);

    /**
     * Will draw a rectangle on the screen.
     *
     * Draw calls may only be issued within a valid gl rendering
     * thread.
     *
     * @param coord coordinate in pixels
     * @param size size in pixels
     * @throws IllegalContextException exception thrown, if the
     *          current thread is not a gl render thread
     */
    void drawRectangle(@NotNull Vector2i coord, @NotNull Vector2i size, Vector4f color, Vector4f specular,
                       Vector4f emission, Vector4f metallic, Vector3f normal, Vector3f displacement)
            throws IllegalContextException;

    void drawRectangle(@NotNull Vector2i coord, @NotNull Vector2i size,
                       Vector4f color) throws IllegalContextException;


    void drawTexture(@NotNull TextureResource texture, @NotNull Vector2i srcCoord, @NotNull Vector2i destCoord,
                     boolean blend);
    void drawTexture(@NotNull TextureResource texture, @NotNull Vector2i srcCoord, @NotNull Vector2i destCoord);

    void drawTexture(@NotNull TextureResource texture, @NotNull Vector2i srcCoord, @NotNull Vector2i destCoord,
                     @NotNull Vector2f scale, boolean blend);
    void drawTexture(@NotNull TextureResource texture, @NotNull Vector2i srcCoord, @NotNull Vector2i destCoord,
                     @NotNull Vector2f scale);

    void drawTexture(@NotNull TextureResource texture, @NotNull Vector2i srcCoord, @NotNull Vector2i destCoord,
                     @NotNull Vector2i size, boolean blend);
    void drawTexture(@NotNull TextureResource texture, @NotNull Vector2i srcCoord, @NotNull Vector2i destCoord,
                     @NotNull Vector2i size);

    void drawTexture(@NotNull TextureResource texture, @NotNull Vector2i srcCoord, @NotNull Vector2i destCoord,
                     @NotNull Vector2i size, Vector4f colorFactor, Vector4f colorAdd, boolean blend);
    void drawTexture(@NotNull TextureResource texture, @NotNull Vector2i srcCoord, @NotNull Vector2i destCoord,
                     @NotNull Vector2i size, Vector4f colorFactor, Vector4f colorAdd);

    void drawTexture(@NotNull TextureResource texture, @NotNull Vector2i srcCoord, @NotNull Vector2i destCoord,
                     @NotNull Vector2i size, Vector4f colorFactor, Vector4f colorAdd, Vector4f specularFactor,
                     Vector4f specularAdd, Vector4f emissionFactor, Vector4f emissionAdd, Vector4f metallicFactor,
                     Vector4f metallicAdd, boolean blend);
    void drawTexture(@NotNull TextureResource texture, @NotNull Vector2i srcCoord, @NotNull Vector2i destCoord,
                     @NotNull Vector2i size, Vector4f colorFactor, Vector4f colorAdd, Vector4f specularFactor,
                     Vector4f specularAdd, Vector4f emissionFactor, Vector4f emissionAdd, Vector4f metallicFactor,
                     Vector4f metallicAdd);

    /**
     * Will draw a string from a text render context.
     *
     * The text render context contains an SSBO that contains
     * the actual render information. The glyphs themselves are
     * rendered by the CFX font renderer and sampled from
     * texture atlae.
     * The font rendering itself is done inside a compute
     * shader.
     *
     * @param renderContext text render context
     * @param destCoord destination coord within the current
     *                  canvas patch
     */
    void drawString(@NotNull CFXTextRenderContext renderContext, @NotNull Vector2i destCoord);

    /**
     * Will draw a string from a text render context.
     *
     * The text render context contains an SSBO that contains
     * the actual render information. The glyphs themsleves are
     * rendered by the CFX font renderer and sampled from
     * texture altae.
     * The font rendering itself is done inside a compute
     * shader.
     *
     * @param renderContext text render context
     */
    void drawString(@NotNull CFXTextRenderContext renderContext);
}
