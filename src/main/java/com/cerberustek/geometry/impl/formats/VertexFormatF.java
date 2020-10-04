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

package com.cerberustek.geometry.impl.formats;

import com.cerberustek.geometry.ComponentType;
import com.cerberustek.geometry.DataType;
import com.cerberustek.geometry.VertexFormat;

import static org.lwjgl.opengl.GL43.*;

public class VertexFormatF implements VertexFormat {

    private final DataType type;
    private final ComponentType componentType;

    private int offset;

    public VertexFormatF(DataType type, ComponentType componentType, int offset) {
        this.type = type;
        this.componentType = componentType;
        this.offset = offset;
    }

    @Override
    public void bind(int index) {
        glVertexAttribFormat(index, type.getNumComponents(), componentType.getGlId(), false, offset);
    }

    @Override
    public int getRelativeOffset() {
        return offset;
    }

    @Override
    public void setRelativeOffset(int relativeOffset) {
        this.offset = relativeOffset;
    }

    @Override
    public DataType getDataType() {
        return type;
    }

    @Override
    public ComponentType getComponentType() {
        return componentType;
    }
}
