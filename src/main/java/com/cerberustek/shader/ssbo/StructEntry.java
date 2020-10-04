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

import com.cerberustek.exceptions.IllegalStructSizeException;

import java.nio.ByteBuffer;

public interface StructEntry {

    /**
     * Will back offsets and length parameters into the entry according
     * to the specified memory layout.
     *
     * The offset parameter is passed down from the parent struct entry.
     *
     * @param layout memory layout to use
     * @param off passed down offset in bytes
     */
    void bake(MemoryLayout layout, long off) throws IllegalStructSizeException;

    /**
     * Will package the struct element's content into the specified
     * byte buffer.
     * @param byteBuffer byte buffer to package in
     */
    void packageData(ByteBuffer byteBuffer);

    /**
     * Returns the byte size of the struct for the specified
     * memory layout.
     * @return byte size of the struct
     */
    long byteSize();

    /**
     * Returns the byte offset of the struct entry.
     * @return byte offset
     */
    long byteOffset();
}
