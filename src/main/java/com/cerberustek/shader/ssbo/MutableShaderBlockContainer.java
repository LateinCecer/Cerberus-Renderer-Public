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

import java.nio.*;

/**
 * The dynamic shader block container, not to be confused with a
 * dynamic buffer object, is used to store buffers of varying size.
 *
 * In a sense, the keyword dynamic, just as with the
 * StaticShaderBlockContainer, describes the size of the buffer,
 * rather then it's contents.
 */
public interface MutableShaderBlockContainer extends ShaderBlockContainer, SingleBufferSSBO {

    /**
     * Will allocate enough space for the <code>buffer</code> buffer
     * and upload it's contents.
     *
     * The partitions are made directly after uploading, using the
     * <code>cuts</code> array.
     *
     * @param buffer buffer to upload
     * @param cuts partitioning cuts
     * @param bindingIndices the binding indices
     */
    void bufferData(ByteBuffer buffer, long[] cuts, int[] bindingIndices);

    /**
     * Will allocate enough space for the <code>buffer</code> buffer
     * and upload it's contents.
     *
     * The partitions are made directly after uploading, using the
     * <code>cuts</code> array.
     *
     * @param buffer buffer to upload
     * @param cuts partitioning cuts
     * @param bindingIndices the binding indices
     */
    void bufferData(ShortBuffer buffer, long[] cuts, int[] bindingIndices);

    /**
     * Will allocate enough space for the <code>buffer</code> buffer
     * and upload it's contents.
     *
     * The partitions are made directly after uploading, using the
     * <code>cuts</code> array.
     *
     * @param buffer buffer to upload
     * @param cuts partitioning cuts
     * @param bindingIndices the binding indices
     */
    void bufferData(IntBuffer buffer, long[] cuts, int[] bindingIndices);

    /**
     * Will allocate enough space for the <code>buffer</code> buffer
     * and upload it's contents.
     *
     * The partitions are made directly after uploading, using the
     * <code>cuts</code> array.
     *
     * @param buffer buffer to upload
     * @param cuts partitioning cuts
     * @param bindingIndices the binding indices
     */
    void bufferData(LongBuffer buffer, long[] cuts, int[] bindingIndices);

    /**
     * Will allocate enough space for the <code>buffer</code> buffer
     * and upload it's contents.
     *
     * The partitions are made directly after uploading, using the
     * <code>cuts</code> array.
     *
     * @param buffer buffer to upload
     * @param cuts partitioning cuts
     * @param bindingIndices the binding indices
     */
    void bufferData(FloatBuffer buffer, long[] cuts, int[] bindingIndices);

    /**
     * Will allocate enough space for the <code>buffer</code> buffer
     * and upload it's contents.
     *
     * The partitions are made directly after uploading, using the
     * <code>cuts</code> array.
     *
     * @param buffer buffer to upload
     * @param cuts partitioning cuts
     * @param bindingIndices the binding indices
     */
    void bufferData(DoubleBuffer buffer, long[] cuts, int[] bindingIndices);

    /**
     * Will allocate enough <code>size</code> byte of data in the GPU's
     * memory as a buffer.
     *
     * The partitions are made directly after uploading, using the
     * <code>cuts</code> array.
     *
     * @param bufferSize buffer size in bytes
     * @param cuts partitioning cuts
     * @param bindingIndices the binding indices
     */
    void bufferData(long bufferSize, long[] cuts, int[] bindingIndices);
}
