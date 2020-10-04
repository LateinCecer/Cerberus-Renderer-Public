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

package com.cerberustek.geometry.impl.verticies;

import com.cerberustek.resource.model.VertexAttribResource;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.buffer.GlBufferObject;
import com.cerberustek.geometry.GeometryBoard;
import com.cerberustek.geometry.VertexBuffer;

public class CompoundVertexBuffer implements VertexBuffer {

    private final VertexAttribResource[] attribs = new VertexAttribResource[GeometryBoard.MAX_VERTEX_ATTRIBUTES];

    private CerberusRenderer renderer;

    public void setAttributes(VertexAttribResource[] attribs) {
        setAttributes(0, attribs, 0, attribs.length);
    }

    public void setAttributes(int srcOff, VertexAttribResource[] attribs, int destOff, int len) {
        if (srcOff + len > attribs.length)
            throw new ArrayIndexOutOfBoundsException();
        if (destOff + len > this.attribs.length)
            throw new ArrayIndexOutOfBoundsException();

        if (len >= 0) System.arraycopy(attribs, srcOff, this.attribs, destOff, len);
    }

    public void setAttribute(int index, VertexAttribResource resource) {
        attribs[index] = resource;
    }

    public void removeAttribute(int index) {
        attribs[index] = null;
    }

    public boolean hasAttribute(int index) {
        return attribs[index] != null;
    }

    @Override
    public void bind() {
        GeometryBoard board = getRenderer().getGeometryBoard();
        for (int i = 0; i < attribs.length; i++) {
            if (attribs[i] != null)
                board.bindVertexAttribute(attribs[i], i);
        }
    }

    @Override
    public void unbind() {
        GeometryBoard board = getRenderer().getGeometryBoard();
        for (int i = 0; i < attribs.length; i++) {
            if (attribs[i] != null)
                board.unbindVertexAttribute(i);
        }
    }

    @Override
    public GlBufferObject bufferObject() {
        return null;
    }

    @Override
    public void destroy() {
        GeometryBoard board = getRenderer().getGeometryBoard();
        for (int i = 0; i < attribs.length; i++) {
            if (attribs[i] != null)
                board.deleteVertexAttribute(attribs[i]);
        }
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }
}
