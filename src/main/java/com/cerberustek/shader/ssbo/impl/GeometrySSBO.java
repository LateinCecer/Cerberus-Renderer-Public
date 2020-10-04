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

package com.cerberustek.shader.ssbo.impl;

import com.cerberustek.CerberusEvent;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.events.ExceptionEvent;
import com.cerberustek.geometry.IndexBuffer;
import com.cerberustek.geometry.IndexBufferArray;
import com.cerberustek.geometry.VertexAttribBinding;
import com.cerberustek.resource.model.IndexBufferArrayResource;
import com.cerberustek.resource.model.VertexAttribResource;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.buffer.GlBufferObject;
import com.cerberustek.buffer.GlBufferTarget;
import com.cerberustek.shader.ssbo.ShaderBlock;
import com.cerberustek.shader.ssbo.ShaderStorageBufferObject;

import java.util.ArrayList;

public class GeometrySSBO implements ShaderStorageBufferObject {

    private final ArrayList<ShaderBlock> blocks = new ArrayList<>();

    private CerberusRenderer renderer;

    public void addIndexBuffer(IndexBufferArrayResource bufferArray, int[] bindingIndex) {
        IndexBufferArray array = getRenderer().getGeometryBoard().getIndexBufferArray(bufferArray);

        for (int i = 0; i < array.size() && i < bindingIndex.length; i++) {
            IndexBuffer indexBuffer = array.getIndexBuffer(i);
            ShaderBlock block = new SharedShaderBlock(indexBuffer.bufferObject().clone(GlBufferTarget.SHADER_STORAGE),
                    bindingIndex[i], indexBuffer.off(), indexBuffer.byteSize());
            blocks.add(block);
        }
    }

    public void addIndexBufferWhole(IndexBufferArrayResource bufferArray, int bindingIndex) {
        IndexBufferArray array = getRenderer().getGeometryBoard().getIndexBufferArray(bufferArray);
        GlBufferObject buffer = array.bufferObject();

        if (buffer == null) {
            CerberusRegistry.getInstance().getService(CerberusEvent.class)
                    .executeFullEIF(new ExceptionEvent(CerberusRenderer.class,
                            new NullPointerException("Buffer object is null")));
            return;
        }

        ShaderBlock block = new WholeBufferShaderBlock(buffer, bindingIndex);
        blocks.add(block);
    }

    public void addVertexAttrib(VertexAttribResource resource, int bindingIndex, long offset, long size) {
        VertexAttribBinding binding = getRenderer().getGeometryBoard().loadVertexAttribute(resource);
        GlBufferObject bo = binding.bufferObject();
        if (bo == null) {
            CerberusRegistry.getInstance().getService(CerberusEvent.class)
                    .executeFullEIF(new ExceptionEvent(CerberusRenderer.class,
                            new NullPointerException("Buffer object is null!")));
            return;
        }

        ShaderBlock block = new SharedShaderBlock(bo.clone(GlBufferTarget.SHADER_STORAGE), bindingIndex, offset, size);
        blocks.add(block);
    }

    public void addVertexAttrib(VertexAttribResource resource, int bindingIndex) {
        VertexAttribBinding binding = getRenderer().getGeometryBoard().loadVertexAttribute(resource);
        GlBufferObject bo = binding.bufferObject();
        if (bo == null) {
            CerberusRegistry.getInstance().getService(CerberusEvent.class)
                    .executeFullEIF(new ExceptionEvent(CerberusRenderer.class,
                            new NullPointerException("Buffer object is null!")));
            return;
        }

        ShaderBlock block = new WholeBufferShaderBlock(bo, bindingIndex);
        blocks.add(block);
    }

    @Override
    public void bind(int index) {
        if (index >= blocks.size())
            throw new ArrayIndexOutOfBoundsException("Index " + index + " does not exist within the SSBO");

        ShaderBlock block = blocks.get(index);
        if (block != null)
            block.bind();
    }

    @Override
    public void bind(int index, int bindingIndex) {
        if (index >= blocks.size())
            throw new ArrayIndexOutOfBoundsException("Index: " + index + " does not exist within the SSBO");

        ShaderBlock block = blocks.get(index);
        if (block != null)
            block.bind(bindingIndex);
    }

    @Override
    public void bind() {
        for (ShaderBlock block : blocks)
            block.bind();
    }

    @Override
    public ShaderBlock getBlock(int index) {
        if (index >= blocks.size())
            return null;
        return blocks.get(index);
    }

    @Override
    public boolean contains(ShaderBlock block) {
        return blocks.contains(block);
    }

    @Override
    public void destroy() {
        blocks.clear();
        // the buffers themselfs are handled in the geometry board.
        // no need to delete them here
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }
}
