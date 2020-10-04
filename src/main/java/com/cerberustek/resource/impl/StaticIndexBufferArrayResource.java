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

import com.cerberustek.resource.model.IndexBufferArrayResource;
import com.cerberustek.geometry.ComponentType;
import com.cerberustek.geometry.IndexBuffer;
import com.cerberustek.geometry.IndexBufferArray;
import com.cerberustek.geometry.impl.StaticIndexBufferArray;

public class StaticIndexBufferArrayResource implements IndexBufferArrayResource {

    private final int[] indices;
    private final long[] cuts;
    private final ComponentType[] dataTypes;
    private final Class<? extends IndexBuffer>[] classes;

    public StaticIndexBufferArrayResource(int[] indices, long[] cuts, ComponentType[] dataTypes) {
        this.indices = indices;
        this.cuts = cuts;
        this.dataTypes = dataTypes;
        this.classes = null;
    }

    public StaticIndexBufferArrayResource(int[] indices, long[] cuts, ComponentType dataType) {
        this.indices = indices;
        this.cuts = cuts;
        this.dataTypes = new ComponentType[] {dataType};
        this.classes = null;
    }

    public StaticIndexBufferArrayResource(int[] indices, long[] cuts, ComponentType[] dataTypes,
                                          Class<? extends IndexBuffer>[] classes) {
        this.indices = indices;
        this.cuts = cuts;
        this.dataTypes = dataTypes;
        this.classes = classes;
    }

    @Override
    public IndexBufferArray load() {
        IndexBufferArray buffer = new StaticIndexBufferArray();
        buffer.genBuffers();
        buffer.allocate(indices);

        if (classes == null) {
            if (dataTypes.length == 1)
                buffer.partition(cuts, dataTypes[0]);
            else
                buffer.partition(cuts, dataTypes);
        } else
            buffer.partition(cuts, dataTypes, classes);

        return buffer;
    }
}
