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

package com.cerberustek.texture.impl.atlas;

import com.cerberustek.CerberusEvent;
import com.cerberustek.events.ExceptionEvent;
import com.cerberustek.exceptions.*;
import com.cerberustek.pipeline.impl.notes.SceneNote;
import com.cerberustek.resource.image.TextureResource;
import com.cerberustek.resource.impl.BaseShaderResource;
import com.cerberustek.resource.shader.ShaderCodeResource;
import com.cerberustek.resource.shader.ShaderResource;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.buffer.BufferAccess;
import com.cerberustek.texture.*;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.logic.math.Vector3i;
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.ShaderBoard;
import com.cerberustek.shader.ShaderType;
import com.cerberustek.shader.code.ShaderCodeLoader;
import com.cerberustek.shader.uniform.Uniform1i;
import com.cerberustek.shader.uniform.Uniform2i;
import com.cerberustek.texture.impl.ImageTextureImpl;
import com.cerberustek.texture.impl.TextureEmpty2D;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.opengl.GL42.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT;
import static org.lwjgl.opengl.GL42.glMemoryBarrier;

public class StitchedTextureAtlas2D implements TextureAtlas {

    public static final String ATLAS_SHADER = "shader.atlas";

    private final Vector3i dimensions;
    private final Vector3i cellSize;
    private final TextureEmpty2D base;
    private final ImageTexture imageTexture;
    private final AtlasCell[] cells;

    private Vector2i localComputeSize;

    private CerberusRenderer renderer;

    public StitchedTextureAtlas2D(Vector3i dimensions, Vector3i cellSize, int[] units,
                                  ImageType[] imageTypes, Attachment[] attachments) {

        if (imageTypes.length < units.length)
            throw new IllegalArgumentException("There have the be at least as many image types as" +
                    "texture units provided to the texture atlas base texture");
        if (attachments.length != units.length)
            throw new IllegalArgumentException("There have to be just as many attachments as" +
                    " texture units to the texture atlas base framebuffer");
        if (units.length == 0)
            throw new IllegalArgumentException("There has to be at least one texture unit");

        this.dimensions = dimensions;
        cells = new AtlasCell[dimensions.getX() * dimensions.getY()];

        this.cellSize = cellSize;
        Vector2i textureSize = dimensions.xy().mul(cellSize.xy());

        this.base = new TextureEmpty2D(units.length);
        base.genTextures();

        for (int i = 0; i < units.length; i++)
            base.initTexture(i, units[i], textureSize, imageTypes[i]);

        imageTexture = new ImageTextureImpl(base);
    }

    @Override
    public Vector3i getAtlasDimensions() {
        return dimensions;
    }

    @Override
    public Vector3i getCellSize() {
        return cellSize;
    }

    @Override
    public int toCellId(Vector3i cellCoord) {
        if (cellCoord.getX() >= dimensions.getX() || cellCoord.getY() >= dimensions.getY() ||
                cellCoord.getX() < 0 || cellCoord.getY() < 0)
            throw new IllegalArgumentException("cell coords outside of texture atlas");

        return cellCoord.getY() * dimensions.getX() + cellCoord.getX();
    }

    @Override
    public Vector3i toCellCoord(int cellId) {
        return new Vector3i(cellId % dimensions.getX(), cellId / dimensions.getX(), 0);
    }

    @Override
    public AtlasCell getCell(int cellId) {
        if (cellId >= cells.length || cellId < 0)
            throw new IllegalArgumentException("Invalid cell id " + cellId);
        return cells[cellId];
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public AtlasCell setCell(TextureResource cellTexture, int cellId) throws AtlasCellSizeException {
        StitchedAtlas2DCell cell = new StitchedAtlas2DCell(cellTexture, cellId, this);
        cells[cellId] = cell;

        // render the cell texture to the texture atlas
        final CerberusRenderer renderer = getRenderer();
        renderer.tryGLTask(t -> {
            ShaderBoard shaderBoard = renderer.getShaderBoard();
            TextureBoard textureBoard = renderer.getTextureBoard();
            Shader shader = shaderBoard.bindShader(getAtlasShader());
            Texture srcSamp = textureBoard.bindTexture(cellTexture);

            if (shader == null) {
                CerberusRegistry.getInstance().warning("[ATLAS]> Failed to bind atlas shader");
                CerberusRegistry.getInstance().getService(CerberusEvent.class)
                        .executeFullEIF(new ExceptionEvent(CerberusRenderer.class,
                                new NullPointerException("Failed to set atlas cell")));
                return;
            }
            if (srcSamp == null) {
                CerberusRegistry.getInstance().warning("[ATLAS]> Failed to bind cell texture");
                CerberusRegistry.getInstance().getService(CerberusEvent.class)
                        .executeFullEIF(new ExceptionEvent(CerberusRenderer.class,
                                new NullPointerException("Failed to set atlas cell")));
                return;
            }

            Vector2i srcSize = srcSamp.getSize(0).xy();
            if (srcSize.getX() > cellSize.getX() || srcSize.getY() > cellSize.getY())
                throw new AtlasCellSizeException(this, srcSamp.getSize(0));

            setupShader(shader, cellId, srcSize, false);
            imageTexture.bindImage(BufferAccess.WRITE_ONLY);

            try {
                Vector2i local = getLocalComputeSize();
                shaderBoard.dispatchCompute(new Vector3i(
                        (int) Math.ceil((float) srcSize.getX() / (float) local.getX()),
                        (int) Math.ceil((float) srcSize.getY() / (float) local.getY()), 1));
                glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
            } catch (GLComputeException | GLShaderTypeException e) {
                CerberusRegistry.getInstance().getService(CerberusEvent.class)
                        .executeFullEIF(new ExceptionEvent(CerberusRenderer.class, e));
            }

            imageTexture.unbindImage();
        });
        return cell;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public AtlasCell setCellUnchecked(TextureResource cellTexture, int cellId) {
        StitchedAtlas2DCell cell = new StitchedAtlas2DCell(cellTexture, cellId, this);
        cells[cellId] = cell;

        // render the cell texture to the texture atlas
        final CerberusRenderer renderer = getRenderer();
        renderer.tryGLTask(t -> {
            ShaderBoard shaderBoard = renderer.getShaderBoard();
            TextureBoard textureBoard = renderer.getTextureBoard();
            Shader shader = shaderBoard.bindShader(getAtlasShader());
            Texture srcSamp = textureBoard.bindTexture(cellTexture);

            if (shader == null) {
                CerberusRegistry.getInstance().warning("[ATLAS]> Failed to bind atlas shader");
                CerberusRegistry.getInstance().getService(CerberusEvent.class)
                        .executeFullEIF(new ExceptionEvent(CerberusRenderer.class,
                                new NullPointerException("Failed to set atlas cell")));
                return;
            }
            if (srcSamp == null) {
                CerberusRegistry.getInstance().warning("[ATLAS]> Failed to bind cell texture");
                CerberusRegistry.getInstance().getService(CerberusEvent.class)
                        .executeFullEIF(new ExceptionEvent(CerberusRenderer.class,
                                new NullPointerException("Failed to set atlas cell")));
                return;
            }

            Vector2i srcSize = srcSamp.getSize(0).xy();

            setupShader(shader, cellId, srcSize, true);
            imageTexture.bindImage(BufferAccess.WRITE_ONLY);

            try {
                Vector2i local = getLocalComputeSize();
                shaderBoard.dispatchCompute(new Vector3i(
                        (int) Math.ceil((float) cellSize.getX() / (float) local.getX()),
                        (int) Math.ceil((float) cellSize.getY() / (float) local.getY()), 1));
                glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
            } catch (GLComputeException | GLShaderTypeException e) {
                CerberusRegistry.getInstance().getService(CerberusEvent.class)
                        .executeFullEIF(new ExceptionEvent(CerberusRenderer.class, e));
            }

            imageTexture.unbindImage();
        });
        return cell;
    }

    @Override
    public AtlasCell addCell(TextureResource cellTexture) throws AtlasCapacityException, AtlasCellSizeException {
        int cellId = nextFreeId();
        if (cellId == -1)
            throw new AtlasCapacityException(this);

        return setCell(cellTexture, cellId);
    }

    @Override
    public AtlasCell addCellUnchecked(TextureResource cellTexture) throws AtlasCapacityException {
        int cellId = nextFreeId();
        if (cellId == -1)
            throw new AtlasCapacityException(this);

        return setCellUnchecked(cellTexture, cellId);
    }

    @Override
    public void removeCell(int cellId) {
        if (cellId >= 0 && cellId < cells.length)
            cells[cellId] = null;
    }

    @Override
    public int capacity() {
        return cells.length;
    }

    @Override
    public int remaining() {
        int count = 0;
        for (AtlasCell cell : cells) {
            if (cell == null)
                count++;
        }
        return count;
    }

    @Override
    public boolean isFull() {
        // go through the cell array backward; usually this will
        // speed up the lookup process for large atlae
        for (int i = cells.length - 1; i >= 0; i--) {
            if (cells[i] == null)
                return false;
        }
        return true;
    }

    @Override
    public boolean isOnline() {
        return base.isOnline();
    }

    @Override
    public boolean isOnline(int index) {
        return base.isOnline(index);
    }

    @Override
    public void genTextures() {
        base.genTextures();
    }

    @Override
    public void genTexture(int index) {
        base.genTexture(index);
    }

    @Override
    public void bind() {
        base.bind();
    }

    @Override
    public void bindToUnit(int index, int unit) {
        base.bindToUnit(index, unit);
    }

    @Override
    public void bind(int index) {
        base.bind(index);
    }

    @Override
    public void destroy(int index) {
        base.destroy(index);
    }

    @Override
    public int length() {
        return base.length();
    }

    @Override
    public int getUnit(int index) {
        return base.getUnit(index);
    }

    @Override
    public int getPointer(int index) {
        return base.getPointer(index);
    }

    @Override
    public ImageType getType(int index) {
        return base.getType(index);
    }

    @Override
    public Vector3i getSize(int index) {
        return base.getSize(index);
    }

    @Override
    public void destroy() {
        imageTexture.destroy();
    }

    /**
     * Will return the atlas shader consistently throughout
     * all texture atlas instances.
     * @return texture atlas shader
     */
    private ShaderResource getAtlasShader() {
        ShaderResource resource = getRenderer().getPropertyMap().getProperty(ATLAS_SHADER, ShaderResource.class);
        if (resource == null) {
            ShaderBoard shaderBoard = getRenderer().getShaderBoard();
            ShaderCodeLoader codeLoader = shaderBoard.getShaderCodeLoader();

            ShaderCodeResource fragmentCode = codeLoader.resourceFromName("util/AtlasComp.glsl");

            resource = new BaseShaderResource(new ShaderCodeResource[] {fragmentCode},
                    new ShaderType[] {ShaderType.COMPUTE}, this::initAtlasShader);
            return getRenderer().getPropertyMap().getProperty(ATLAS_SHADER, ShaderResource.class, resource);
        }
        return resource;
    }

    /**
     * Will initiate the atlas shader.
     * @param shader atlas shader
     */
    private void initAtlasShader(Shader shader) {
        try {
            // atlas parameters
            shader.addUniform(new Uniform2i(shader, "cell", new Vector2i(0, 0)));
            shader.addUniform(new Uniform2i(shader, "cellSize", cellSize.xy()));
            shader.addUniform(new Uniform2i(shader, "srcSize", cellSize.xy()));
            shader.addUniform(new Uniform1i(shader, "rescale", 0));

            // samplers
            shader.addUniform(new Uniform1i(shader, "colorSamp", SceneNote.COLOR));
            shader.addUniform(new Uniform1i(shader, "normalSamp", SceneNote.NORMAL));
            shader.addUniform(new Uniform1i(shader, "specularSamp", SceneNote.SPECULAR));
            shader.addUniform(new Uniform1i(shader, "emissionSamp", SceneNote.EMISSION));
            shader.addUniform(new Uniform1i(shader, "metallicSamp", SceneNote.METALLIC));
            shader.addUniform(new Uniform1i(shader, "depthSamp", SceneNote.DISPLACEMENT));
        } catch (GLUnknownUniformException e) {
            e.printStackTrace();
        }
    }

    /**
     * Will setup the shaders uniforms for rendering a cell texture
     * to the atlas.
     * @param shader atlas shader
     * @param cell cell coordinate
     * @param srcSize src texture size
     * @param rescale rescale source texture
     */
    private void setupShader(Shader shader, int cell, Vector2i srcSize, boolean rescale) {
        setupShader(shader, toCellCoord(cell).xy(), srcSize, rescale);
    }

    /**
     * Will setup the shaders uniforms for rendering a cell texture
     * to the atlas.
     * @param shader atlas shader
     * @param cellCoord cell coordinate
     * @param srcSize src texture size
     * @param rescale rescale source texture
     */
    private void setupShader(Shader shader, Vector2i cellCoord, Vector2i srcSize, boolean rescale) {
        shader.getUniform("cell", Uniform2i.class).set(cellCoord.mul(cellSize.xy()));
        shader.getUniform("cellSize", Uniform2i.class).set(cellSize.xy());
        shader.getUniform("srcSize", Uniform2i.class).set(srcSize);
        shader.getUniform("rescale", Uniform1i.class).set(rescale ? 1 : 0);

        shader.update(0);
    }

    /**
     * Returns the id of the next free cell.
     *
     * If this method returns -1, the entire texture atlas
     * is already fully filled.
     *
     * @return next free cell id
     */
    private int nextFreeId() {
        for (int i = 0; i < cells.length; i++) {
            if (cells[i] == null)
                return i;
        }
        return -1;
    }

    @NotNull
    private Vector2i getLocalComputeSize() {
        if (localComputeSize == null) {
            try {
                return localComputeSize = getRenderer().getShaderBoard().getLocalGroupSize(getAtlasShader()).xy();
            } catch (GLShaderTypeException e) {
                CerberusRegistry.getInstance().getService(CerberusEvent.class)
                        .executeFullEIF(new ExceptionEvent(CerberusRenderer.class, e));
                return new Vector2i(8, 8);
            }
        }
        return localComputeSize;
    }

    /**
     * Will return the renderer instance.
     * @return cerberus renderer
     */
    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }
}
