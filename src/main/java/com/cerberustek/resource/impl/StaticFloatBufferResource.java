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

package com.cerberustek.resource.impl;

import com.cerberustek.resource.buffered.FloatBufferResource;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class StaticFloatBufferResource extends FloatBufferResource {

    private final FloatBuffer buffer;

    public StaticFloatBufferResource(FloatBuffer data) {
        this.buffer = data;
    }

    public StaticFloatBufferResource(ByteBuffer data) {
        this.buffer = data.asFloatBuffer();
    }

    @Override
    public FloatBuffer load() {
        return buffer;
    }
}
