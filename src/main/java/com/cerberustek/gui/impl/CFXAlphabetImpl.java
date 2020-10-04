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
import com.cerberustek.events.ExceptionEvent;
import com.cerberustek.exceptions.AtlasCapacityException;
import com.cerberustek.exceptions.IllegalContextException;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.logic.math.Vector3i;
import com.cerberustek.resource.image.TextureResource;
import com.cerberustek.resource.image.impl.StitchedAtlas2DResource;
import com.cerberustek.resource.shader.SSBOResource;
import com.cerberustek.resource.shader.impl.ImmutableSSBOResource;
import com.cerberustek.shader.ssbo.ShaderBlock;
import com.cerberustek.texture.AtlasCell;
import com.cerberustek.texture.Texture;
import com.cerberustek.texture.TextureAtlas;
import com.cerberustek.texture.impl.atlas.StitchedTextureAtlas2D;
import com.cerberustek.util.BufferUtil;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.gui.CFXAlphabet;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

public class CFXAlphabetImpl implements CFXAlphabet {

    private static final int BINDING_GLYPH_BUFFER = 1;

    private final Vector2i cellSize;
    private final Vector2i dimensions;
    private final StitchedAtlas2DResource atlas;
    private final SSBOResource glyphBuffer;

    private CerberusRenderer renderer;

    public CFXAlphabetImpl(Vector2i cellSize) {
        this.cellSize = cellSize;

        this.dimensions = getRenderer().getGUIManager().getAtlasDimensions();
        this.atlas = new StitchedAtlas2DResource(dimensions, cellSize, getRenderer().getGUIManager().getImageType());
        this.glyphBuffer = new ImmutableSSBOResource(dimensions.getX() * dimensions.getY() * 4,
                new long[] {}, new int[] {BINDING_GLYPH_BUFFER}, BufferUtil.FLAGS_GPGPU_DATA_INPUT);
    }

    @Override
    public void init() {
        getRenderer().getTextureBoard().loadTexture(atlas);
        getRenderer().getShaderBoard().loadSSBO(glyphBuffer);
    }

    @Override
    public Vector2i getCellSize() {
        return cellSize;
    }

    @Override
    public Vector2i getDimensions() {
        return dimensions;
    }

    @Override
    public void bind() {
        // System.out.println("Binding atlas texture");
        getRenderer().getTextureBoard().bindTexture(atlas);
        getRenderer().getShaderBoard().bindSSBO(glyphBuffer, 0);
    }

    @Override
    public int insertCharacter(TextureResource cellTexture) {
        StitchedTextureAtlas2D atlas = (StitchedTextureAtlas2D) getRenderer().getTextureBoard().loadTexture(this.atlas);
        try {
            Texture texture = getRenderer().getTextureBoard().bindTexture(cellTexture);
            if (texture == null)
                return -1;

            AtlasCell cell = atlas.addCell(cellTexture);
            if (cell == null)
                return -1;

            Vector3i textureSize = texture.getSize(0);
            if (insertGlyph(cell.getCellId(), textureSize.getX(), textureSize.getY()))
                return cell.getCellId();
        } catch (AtlasCapacityException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Will insert the width and height information for the character
     * into the glyph buffer of the alphabet.
     *
     * This method will return true only if the information could be
     * inserted successfully.
     *
     * @param index character index inside the alphabet
     * @param width character width
     * @param height character height
     * @return operation success
     */
    private boolean insertGlyph(int index, int width, int height) {
        ShaderBlock block = getRenderer().getShaderBoard().bindSSBO(glyphBuffer, 0);
        if (block == null) {
            CerberusRegistry.getInstance().getService(CerberusEvent.class)
                    .executeFullEIF(new ExceptionEvent(CerberusRenderer.class,
                            new NullPointerException("Glyph buffer has not been initialized for alphabet: "
                                    + toString())));
            return false;
        }
        ByteBuffer buffer = BufferUtils.createByteBuffer(4);
        buffer.putInt((width & 0xFFFF) + ((height & 0xFFFF) << 16));
        buffer.flip();
        block.upload(buffer, index * 4);

        // CerberusRegistry.getInstance().debug("Inserting glyph into alphabet: tex=" + index + ", width=" + width + ", height=" + height);
        return true;
    }

    @Override
    public void removeCharacter(int cellId) {
        StitchedTextureAtlas2D atlas = (StitchedTextureAtlas2D) getRenderer().getTextureBoard().loadTexture(this.atlas);
        atlas.removeCell(cellId);
    }

    @Override
    public TextureResource getTextureAtlas() {
        return atlas;
    }

    @Override
    public SSBOResource getGlyphBuffer() {
        return glyphBuffer;
    }

    @Override
    public boolean isFull() {
        if (!getRenderer().getWindow().isGlThread())
            throw new IllegalContextException();

        TextureAtlas atlas = (TextureAtlas) getRenderer().getTextureBoard().bindTexture(this.atlas);
        if (atlas == null) {
            CerberusRegistry.getInstance().warning("CFX-Alphabet has not been initialized yet");
            return false;
        }
        return atlas.isFull();
    }

    @Override
    public int remaining() {
        return 0;
    }

    @Override
    public void destroy() {
        getRenderer().getTextureBoard().deleteTexture(atlas);
        getRenderer().getShaderBoard().deleteSSBO(glyphBuffer);
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }
}
