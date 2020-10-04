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

package com.cerberustek.shader.ssbo;

import java.nio.ByteBuffer;

/**
 * Struct java wrapper for GLSL blocks
 */
public interface Struct extends StructEntry {

    /**
     * Will query the struct for some entry inside of the struct.
     *
     * The query result will contain the queried entry, along with it's
     * total offset in bytes to the start of the struct.
     * This information can be used to update only certain parts of the
     * struct.
     *
     * @param request query request
     * @return query result
     */
    StructEntry query(StructQuery request);

    /**
     * appends a new struct entry to the struct.
     *
     * The new entry will be inserted at the bottom of the struct.
     *
     * @param entry new entry
     */
    void append(StructEntry entry);

    /**
     * Inserts a new struct entry into the struct.
     *
     * The new entry will be inserted at the specified index.
     * If the index is outside the bounds of this struct, this
     * method will throw a IndexOutOfBoundsException.
     * @param entry entry
     * @param index index to insert at
     * @throws IndexOutOfBoundsException exception that get's thrown,
     *          when the insertion index is outside of the bounds of
     *          this struct.
     */
    void insert(StructEntry entry, int index) throws IndexOutOfBoundsException;

    /**
     * Will remove the bottom most entry of the struct.
     */
    void remove();

    /**
     * Will remove the entry at the specified <code>index</code>
     * location.
     *
     * If the index is outside of the bounds of this struct, this method
     * will throw an IndexOutOfBoundsException.
     * @param index index of the entry to remove
     * @throws IndexOutOfBoundsException get's thrown, whenever the index
     *          is outside of the struct's bounds.
     */
    void remove(int index) throws IndexOutOfBoundsException;

    /**
     * Returns the Struct element at the specified index.
     * @param index element index
     * @return struct element at the specified index
     */
    StructEntry getElement(int index);

    /**
     * Returns the Struct element at the specified index.
     *
     * This method will attempt to cast the struct element at the
     * index to the type of the specified struct element class. If
     * the casting fails, this method will return null.
     *
     * @param index element index
     * @param clazz type class to cast to
     * @param <T> element type
     * @return retrieved and casted struct element
     */
    <T extends StructEntry> T getElement(int index, Class<T> clazz);

    /**
     * Will package the struct's data into a byte buffer.
     * @return byte buffer
     */
    ByteBuffer packageData();

    /**
     * Returns the size of the struct in elements.
     *
     * This method only counts direct children, not indirect
     * children.
     *
     * @return size in elements
     */
    int size();
}
