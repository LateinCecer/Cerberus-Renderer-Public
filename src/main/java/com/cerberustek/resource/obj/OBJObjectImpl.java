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

package com.cerberustek.resource.obj;

import com.cerberustek.logic.math.Vector2f;
import com.cerberustek.logic.math.Vector3f;

public class OBJObjectImpl implements OBJObject {

    private final Vector3f[] vertices;
    private final Vector2f[] texture;
    private final Vector3f[] normal;
    private final int[] indices;

    private String name;

    public OBJObjectImpl(Vector3f[] vertices, Vector2f[] texture, Vector3f[] normal, int[] indices) {
        this.vertices = vertices;
        this.texture = texture;
        this.normal = normal;
        this.indices = indices;

        name = "obj";
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex(int i) {
        return indices[i];
    }

    @Override
    public Vector3f getVertex(int i) {
        return vertices[i];
    }

    @Override
    public Vector2f getUV(int i) {
        return texture[i];
    }

    @Override
    public Vector3f getNormal(int i) {
        return normal[i];
    }

    @Override
    public int indexSize() {
        return indices.length;
    }

    @Override
    public int vertexSize() {
        return vertices.length;
    }
}
