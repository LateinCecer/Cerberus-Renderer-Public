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

import com.cerberustek.exceptions.PermutationException;
import com.cerberustek.Destroyable;
import com.cerberustek.buffer.GlBufferObject;

/**
 * Array of Index Buffers. All index buffer partitions
 * in the array store their data within the same index
 * buffer object.
 */
public interface IndexBufferArray extends Destroyable {

    /**
     * Will generate the index buffer object
     */
    void genBuffers();

    /**
     * Will bind the partition of the IBO at the index
     * location.
     * @param index partition index
     * @throws ArrayIndexOutOfBoundsException Exception that
     *          is thrown, when the partition index does not
     *          exist.
     */
    void bind(int index);

    /**
     * Will bind the entire IBO to the buffer
     * target.
     */
    void bind();

    /**
     * Returns the gl buffer object with contains
     * the buffer pointer of the array's ibo.
     * @return gl buffer object
     */
    GlBufferObject bufferObject();

    /**
     * Returns the index buffer with the array index
     * <code>index</code>.
     *
     * If the partition index <code>index</code> does
     * not exist, this method will return null.
     * @param index index of the index buffer
     * @return index buffer
     */
    IndexBuffer getIndexBuffer(int index);

    /**
     * Returns rather or not the <code>buffer</code>
     * is a partition of this index buffer array.
     *
     * @param buffer index buffer object to check
     * @return is part of buffer array
     */
    boolean contains(IndexBuffer buffer);

    /**
     * Will allocate <code>byteSize</code> bytes for
     * the IBO
     * @param byteSize size of the IBO in bytes
     */
    void allocate(long byteSize);

    /**
     * Will allocate <code>byteSize</code> bytes for
     * the IBO. The flag <code>flag</code> will be
     * used for allocation.
     * @param byteSize size of the IBO in bytes
     * @param flag buffer flag
     */
    void allocate(long byteSize, BufferFlag flag);

    /**
     * Will allocate <code>size</code> elements of the
     * DataType <code>type</code> for the IBO
     * @param size element size of the IBO
     * @param type DataType of the IBO
     */
    void allocate(int size, ComponentType type);

    /**
     * Will allocate the buffer size needed to store
     * the indices and upload them to the VRM directly.
     *
     * Please take node, that this action will allocate
     * the buffer dynamically. Future buffer
     * modifications may fail and this should only be
     * done for static buffers.
     * @param indices indices to store
     */
    void allocate(int[] indices);

    /**
     * Will allocate the buffer size needed to store
     * the indices and upload them to the VRM directly.
     *
     * Please take node, that this action will allocate
     * the buffer dynamically. Future buffer
     * modifications may fail and this should only be
     * done for static buffers.
     * @param indices indices to store
     */
    void allocate(short[] indices);

    /**
     * Will create multiple partitions.
     *
     * The IBO buffer storage will be assigned to
     * multiple index buffers. The size and offset
     * of the partitions will be determined by the
     * <code>cuts</code> made. The cut indices represent
     * the byte locations.
     *
     * The i-th partition is therefor allocated from
     * - the (i - 1)-th cut index inclusive
     * to
     * - the i-th cut index exclusive.
     *
     * The first cut index always being 0 and the last cut
     * index being n (with n being the total byte size of
     * the IBO).
     * Also there have to be exactly one more data type
     * in the parameters than cuts.
     * @param cuts partition cuts in byte locations
     * @param dataTypes data Types of the partition
     * @throws IndexOutOfBoundsException Exception that is
     *          thrown if one of the cut indices exceeds
     *          the maximum byte index within the IBO.
     * @throws PermutationException Exception that is
     *          thrown if the cut indices are not in a
     *          ascending order.
     */
    void partition(long[] cuts, ComponentType[] dataTypes);

    /**
     * Will create multiple partitions.
     *
     * The IBO buffer storage will be assigned to
     * multiple index buffers. The size and offset
     * of the partitions will be determined by the
     * <code>cuts</code> made. The cut indices represent
     * the byte locations.
     *
     * The i-th partition is therefor allocated from
     * - the (i - 1)-th cut index inclusive
     * to
     * - the i-th cut index exclusive.
     *
     * The first cut index always being 0 and the last cut
     * index being n (with n being the total byte size of
     * the IBO).
     * This version will initialize all partitions with the
     * same data type.
     * @param cuts partition cuts in byte locations
     * @param dataType data Types of the partition
     * @throws IndexOutOfBoundsException Exception that is
     *          thrown if one of the cut indices exceeds
     *          the maximum byte index within the IBO.
     * @throws PermutationException Exception that is
     *          thrown if the cut indices are not in a
     *          ascending order.
     */
    void partition(long[] cuts, ComponentType dataType);

    /**
     * Will create multiple partitions.
     *
     * The IBO buffer storage will be assigned to
     * multiple index buffers. The size and offset
     * of the partitions will be determined by the
     * <code>cuts</code> made. The cut indices represent
     * the byte locations.
     *
     * The i-th partition is therefor allocated from
     * - the (i - 1)-th cut index inclusive
     * to
     * - the i-th cut index exclusive.
     *
     * The first cut index always being 0 and the last cut
     * index being n (with n being the total byte size of
     * the IBO).
     * Also there have to be exactly one more data type
     * in the parameters than cuts.
     * @param cuts partition cuts in byte locations
     * @param dataTypes data Types of the partition
     * @param bufferClass the classes of the index buffer
     *                    partition implementation
     * @throws IndexOutOfBoundsException Exception that is
     *          thrown if one of the cut indices exceeds
     *          the maximum byte index within the IBO.
     * @throws PermutationException Exception that is
     *          thrown if the cut indices are not in a
     *          ascending order.
     */
    void partition(long[] cuts, ComponentType[] dataTypes, Class<? extends IndexBuffer>[] bufferClass);

    /**
     * Size of the array in elements
     * @return array size
     */
    int size();

    /**
     * Returns the size of the array in bytes.
     * @return array size in bytes
     */
    long byteSize();
}
