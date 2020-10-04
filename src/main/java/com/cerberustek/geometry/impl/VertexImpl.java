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

package com.cerberustek.geometry.impl;

import com.cerberustek.geometry.Vertex;
import com.cerberustek.logic.math.Vector2f;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.logic.math.Vector3f;

public class VertexImpl implements Vertex {

    private final Vector3f position;
    private final Vector2f texturePos;
    private final Vector3f normal;

    public VertexImpl(Vector3f position, Vector2f texturePos, Vector3f normal) {
        this.position = position;
        this.texturePos = texturePos;
        this.normal = normal;
    }

    @Deprecated
    public VertexImpl(Vector3f position, Vector2i texturePos, Vector3f normal) {
        this(position, texturePos.toVector2f(), normal);
    }

    @Deprecated
    public VertexImpl(Vector3f position, Vector2i texturePos) {
        this(position, texturePos, new Vector3f(0, 0, 0));
    }

    public VertexImpl(Vector3f position, Vector2f texturePos) {
        this(position, texturePos, new Vector3f(0, 0, 0));
    }

    public VertexImpl(Vector3f position) {
        this(position, new Vector2f(0, 0));
    }

    @Override
    public Vector3f getPosition() {
        return position;
    }

    @Override
    public Vector2f getTexturePos() {
        return texturePos;
    }

    @Override
    public Vector3f getNormal() {
        return normal;
    }

    @Override
    public int sizeof() {
        return normal.length() == 0 ? 20 : 32;
    }
}
