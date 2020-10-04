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

package com.cerberustek.geometry;

/**
 * Vertex Attribute Format
 */
public interface VertexFormat { ;

    /**
     * Bind the vertex format to the attribute index.
     * @param index attribute index
     */
    void bind(int index);

    /**
     * Returns the relative off the format in the buffer object.
     *
     * This allows bind multiple formats on different attribute
     * indices, to use the same binding index.
     *
     * @return relative offset
     */
    int getRelativeOffset();

    /**
     * Sets the relative offset.
     *
     * This method does not update the relative offset on the
     * GPU. The format has to be rebound for the offset change
     * to take effect.
     *
     * @param relativeOffset new relative offset
     */
    void setRelativeOffset(int relativeOffset);

    /**
     * Returns the data type of the vertex format
     * @return data type
     */
    DataType getDataType();

    /**
     * Returns the component type of the data format
     * @return component type
     */
    ComponentType getComponentType();
}
