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

package com.cerberustek.resource.gitf;

import com.cerberustek.data.MetaData;
import com.cerberustek.data.impl.elements.DocElement;
import com.cerberustek.data.impl.tags.DocTag;
import com.cerberustek.exceptions.GITFFormatException;
import com.cerberustek.resource.impl.CompoundVertexBufferResource;
import com.cerberustek.resource.impl.ContainerModelResource;
import com.cerberustek.resource.material.MaterialResource;
import com.cerberustek.resource.model.IndexBufferArrayResource;
import com.cerberustek.resource.model.ModelResource;
import com.cerberustek.resource.model.VertexAttribResource;
import com.cerberustek.resource.model.VertexBufferResource;
import com.cerberustek.geometry.DrawMode;
import com.cerberustek.geometry.GeometryBoard;

public class GITFPrimitive implements GITFEntry {

    private GITFAccessor indexBuffer;
    private GITFMaterial material;
    private DrawMode drawMode;
    private GITFAccessor vertexAttribPosition;
    private GITFAccessor vertexAttribTexCoord;
    private GITFAccessor vertexAttribNormal;
    private GITFAccessor vertexAttribTangent;

    @Override
    public void read(GITFReader reader, MetaData data) throws GITFFormatException {
        if (!(data instanceof DocElement))
            throw new GITFFormatException();
        DocElement doc = (DocElement) data;

        Integer indices = doc.valueInt("indices");
        if (indices == null)
            throw new GITFFormatException("A primitive needs indices");
        this.indexBuffer = reader.getAccessor(indices);

        Integer material = doc.valueInt("material");
        if (material == null)
            throw new GITFFormatException("A primitive needs a material");
        this.material = reader.getMaterial(material);

        Integer drawMode = doc.valueInt("mode");
        if (drawMode == null)
            this.drawMode = DrawMode.TRIANGLES;
        else
            this.drawMode = DrawMode.valueOf(drawMode);

        DocTag attributes = doc.extractDoc("attributes");
        if (attributes == null)
            throw new GITFFormatException("A primitive needs vertex attributes");

        Integer positionAttrib = attributes.valueInt("POSITION");
        Integer texcoordAttrib = attributes.valueInt("TEXCOORD_0");
        Integer normalAttrib = attributes.valueInt("NORMAL");
        Integer tangentAttrib = attributes.valueInt("TANGENT");

        if (positionAttrib == null)
            throw new GITFFormatException("A primitive needs vertex position attributes");
        vertexAttribPosition = reader.getAccessor(positionAttrib);

        if (texcoordAttrib != null)
            vertexAttribTexCoord = reader.getAccessor(texcoordAttrib);

        if (normalAttrib != null)
            vertexAttribNormal = reader.getAccessor(normalAttrib);

        if (tangentAttrib != null)
            vertexAttribTangent = reader.getAccessor(tangentAttrib);
    }

    public VertexAttribResource generatePosAttrib() {
        return vertexAttribPosition == null ? null : vertexAttribPosition.asVertexAttrib();
    }

    public VertexAttribResource generateNormalAttrib() {
        return vertexAttribNormal == null ? null : vertexAttribNormal.asVertexAttrib();
    }

    public VertexAttribResource generateTexCoordAttrib() {
        return vertexAttribTexCoord == null ? null : vertexAttribTexCoord.asVertexAttrib();
    }

    public VertexAttribResource generateTangentAttrib() {
        return vertexAttribTangent == null ? null : vertexAttribTangent.asVertexAttrib();
    }

    public VertexBufferResource generateVertexBuffer() {
        CompoundVertexBufferResource resource = new CompoundVertexBufferResource();
        resource.setAttribute(GeometryBoard.VERTEX, generatePosAttrib());
        resource.setAttribute(GeometryBoard.NORMAL_0, generateNormalAttrib());
        resource.setAttribute(GeometryBoard.TEXCOORD_0, generateTexCoordAttrib());
        resource.setAttribute(GeometryBoard.NORMAL_1, generateTangentAttrib());
        return resource;
    }

    public IndexBufferArrayResource generateIndexBufferArray() {
        return indexBuffer.asIndexBufferArray();
    }

    public ModelResource generateModelResource() {
        return new ContainerModelResource(generateVertexBuffer(), generateIndexBufferArray(), 0);
    }

    public MaterialResource getMaterial() {
        return material.generateMaterial();
    }

    public DrawMode getDrawMode() {
        return drawMode;
    }

    @Override
    public MetaData write() throws GITFFormatException {
        return null;
    }
}
