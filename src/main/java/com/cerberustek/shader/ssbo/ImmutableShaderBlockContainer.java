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

import com.cerberustek.geometry.BufferFlag;
import com.cerberustek.geometry.ComponentType;

import java.nio.*;

/**
 * A static shader block container populates a buffer in a way that
 * is ideal for buffers that do not chance their size.
 *
 * Depending on the implementation and the specified buffer flag,
 * this can be further optimised for the individual use case.
 */
public interface ImmutableShaderBlockContainer extends ShaderBlockContainer, SingleBufferSSBO {

    /**
     * Will allocate <code>byteSize</code> bytes for the buffer.
     *
     * The default buffer flag for the implementation is used for
     * this.
     *
     * @param byteSize buffer size in bytes
     */
    void allocate(long byteSize);

    /**
     * Will allocate <code>byteSize</code> bytes for the buffer.
     *
     * The specified buffer flag is used for the allocation.
     *
     * @param byteSize buffer size in bytes
     * @param flags buffer flag
     */
    void allocate(long byteSize, BufferFlag... flags);

    /**
     * Will allocate enough space for <code>size</code> elements
     * of the specified buffer type <code>componentType</code>.
     *
     * The default buffer flag for this implementation is used.
     *
     * @param size buffer size in elements
     * @param componentType component type
     */
    void allocate(int size, ComponentType componentType);

    /**
     * Will allocate enough space for <code>size</code> elements
     * of the specified buffer type <code>componentType</code>.
     *
     * The specified buffer flag is used for the allocation.
     *
     * @param size buffer size in elements
     * @param componentType component type of the elements
     * @param flags buffer flag
     */
    void allocate(int size, ComponentType componentType, BufferFlag... flags);

    /**
     * Will allocate enough space for the <code>buffer</code> buffer
     * and upload it's contents.
     *
     * The specified buffer flag is used for the allocation.
     *
     * @param buffer buffer to upload
     * @param flags buffer flag
     */
    void allocate(ByteBuffer buffer, BufferFlag... flags);

    /**
     * Will allocate enough space for the <code>buffer</code> buffer
     * and upload it's contents.
     *
     * The default buffer flag for the specific implementation is
     * used for the allocation.
     *
     * @param buffer buffer to upload
     */
    void allocate(ByteBuffer buffer);

    /**
     * Will allocate enough space for the <code>buffer</code> buffer
     * and upload it's contents.
     *
     * The specified buffer flag is used for the allocation.
     *
     * @param buffer buffer to upload
     * @param flags buffer flag
     */
    void allocate(ShortBuffer buffer, BufferFlag... flags);

    /**
     * Will allocate enough space for the <code>buffer</code> buffer
     * and upload it's contents.
     *
     * The default buffer flag for the specific implementation is
     * used for the allocation.
     *
     * @param buffer buffer to upload
     */
    void allocate(ShortBuffer buffer);

    /**
     * Will allocate enough space for the <code>buffer</code> buffer
     * and upload it's contents.
     *
     * The specified buffer flag is used for the allocation.
     *
     * @param buffer buffer to upload
     * @param flags buffer flag
     */
    void allocate(IntBuffer buffer, BufferFlag... flags);

    /**
     * Will allocate enough space for the <code>buffer</code> buffer
     * and upload it's contents.
     *
     * The default buffer flag for the specific implementation is
     * used for the allocation.
     *
     * @param buffer buffer to upload
     */
    void allocate(IntBuffer buffer);

    /**
     * Will allocate enough space for the <code>buffer</code> buffer
     * and upload it's contents.
     *
     * The specified buffer flag is used for the allocation.
     *
     * @param buffer buffer to upload
     * @param flags buffer flag
     */
    void allocate(FloatBuffer buffer, BufferFlag... flags);

    /**
     * Will allocate enough space for the <code>buffer</code> buffer
     * and upload it's contents.
     *
     * The default buffer flag for the specific implementation is
     * used for the allocation.
     *
     * @param buffer buffer to upload
     */
    void allocate(FloatBuffer buffer);

    /**
     * Will allocate enough space for the <code>buffer</code> buffer
     * and upload it's contents.
     *
     * The specified buffer flag is used for the allocation.
     *
     * @param buffer buffer to upload
     * @param flags buffer flag
     */
    void allocate(DoubleBuffer buffer, BufferFlag... flags);

    /**
     * Will allocate enough space for the <code>buffer</code> buffer
     * and upload it's contents.
     *
     * The default buffer flag for the specific implementation is
     * used for the allocation.
     *
     * @param buffer buffer to upload
     */
    void allocate(DoubleBuffer buffer);

    /**
     * Will create partitions inside of the shader block container.
     *
     * The size and position in bytes of each partition is provided
     * by the <code>cuts</code> array. Thus, the amount of partitions
     * or shader blocks is given as cuts.length + 1.
     * This function works much like the partition function of the
     * index array buffer implementation. Look there for further
     * explanations.
     *
     * @param cuts partition cuts
     * @param bindingIndices the binding indices
     */
    void partition(long[] cuts, int[] bindingIndices);
}
