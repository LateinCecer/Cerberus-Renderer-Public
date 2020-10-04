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

package com.cerberustek.buffer;

import com.cerberustek.Destroyable;
import com.cerberustek.geometry.BufferFlag;

import java.nio.*;

/**
 * Gl buffer object for management purposes
 */
public interface GlBufferObject extends Destroyable {

    /**
     * Returns the buffer target of the buffer object.
     * @return buffer target
     */
    GlBufferTarget getTarget();

    /**
     * Will bind the entire buffer using glBindBuffer(...)
     */
    void bind();

    /**
     * Will bind the buffer object to a specified buffer
     * target.
     * @param target buffer target to bind to
     */
    void bind(GlBufferTarget target);

    /**
     * Will bind part of the buffer using glBindBufferRange(...) with
     * the binding index 0.
     * @param offset offset in bytes
     * @param size length in bytes
     */
    void bind(long offset, long size);

    /**
     * Will bind part of the buffer using glBindBufferRange(...) with
     * the binding index 0 to the specified buffer target.
     * @param target buffer target
     * @param offset offset in bytes
     * @param size length in bytes
     */
    void bind(GlBufferTarget target, long offset, long size);

    /**
     * Will bind part of the buffer using glBindBufferRange(...) with
     * the specified binding index.
     * @param bindingIndex binding index
     * @param offset offset in bytes
     * @param size length in bytes
     */
    void bind(int bindingIndex, long offset, long size);

    /**
     * Will bind part of the buffer using glBindBufferRange(...) with
     * the specified binding index to the specified buffer target.
     * @param bufferTarget buffer target
     * @param bindingIndex binding index
     * @param offset offset in bytes
     * @param size length in bytes
     */
    void bind(GlBufferTarget bufferTarget, int bindingIndex, long offset, long size);

    /**
     * Will bind the entire buffer using glBindBufferBase(...) with
     * the specified binding index.
     * @param bindingIndex binding index
     */
    void bind(int bindingIndex);

    /**
     * Will bind the entire buffer using glBindBufferBase(...) with
     * the specified binding index to the specified buffer target.
     * @param bufferTarget buffer target to bind to
     * @param bindingIndex binding index to bind to
     */
    void bind(GlBufferTarget bufferTarget, int bindingIndex);

    /**
     * Will reallocate the buffer object and update the new data
     * set to it.
     *
     * If the provided data buffer is null, the buffer object will
     * be invalidated. The BufferUsage helps the driver to decide
     * how to allocate the buffer in terms of optimization.
     * If the buffer is invalidated and reused with the same size
     * (orphaning) there is a good change, although no guarantee,
     * that the driver will reallocate the same space in the VRAM
     * for the buffer object. This is a very common practice for
     * buffer streaming for it's simplicity and relatively good
     * runtime performance.
     * Remember to bind the buffer, before calling this method.
     * The buffer object will be allocated mutably by this
     * method, meaning that it can be reallocated.
     *
     * @param buffer byte buffer
     * @param usage buffer usage
     */
    void bufferData(ByteBuffer buffer, BufferUsage usage);

    /**
     * Will reallocate the buffer object and update the new data
     * set to it.
     *
     * If the provided data buffer is null, the buffer object will
     * be invalidated. The BufferUsage helps the driver to decide
     * how to allocate the buffer in terms of optimization.
     * If the buffer is invalidated and reused with the same size
     * (orphaning) there is a good change, although no guarantee,
     * that the driver will reallocate the same space in the VRAM
     * for the buffer object. This is a very common practice for
     * buffer streaming for it's simplicity and relatively good
     * runtime performance.
     * Remember to bind the buffer, before calling this method.
     * The buffer object will be allocated mutably by this
     * method, meaning that it can be reallocated.
     *
     * @param buffer byte buffer
     * @param usage buffer usage
     */
    void bufferData(ShortBuffer buffer, BufferUsage usage);

    /**
     * Will reallocate the buffer object and update the new data
     * set to it.
     *
     * If the provided data buffer is null, the buffer object will
     * be invalidated. The BufferUsage helps the driver to decide
     * how to allocate the buffer in terms of optimization.
     * If the buffer is invalidated and reused with the same size
     * (orphaning) there is a good change, although no guarantee,
     * that the driver will reallocate the same space in the VRAM
     * for the buffer object. This is a very common practice for
     * buffer streaming for it's simplicity and relatively good
     * runtime performance.
     * Remember to bind the buffer, before calling this method.
     * The buffer object will be allocated mutably by this
     * method, meaning that it can be reallocated.
     *
     * @param buffer byte buffer
     * @param usage buffer usage
     */
    void bufferData(IntBuffer buffer, BufferUsage usage);

    /**
     * Will reallocate the buffer object and update the new data
     * set to it.
     *
     * If the provided data buffer is null, the buffer object will
     * be invalidated. The BufferUsage helps the driver to decide
     * how to allocate the buffer in terms of optimization.
     * If the buffer is invalidated and reused with the same size
     * (orphaning) there is a good change, although no guarantee,
     * that the driver will reallocate the same space in the VRAM
     * for the buffer object. This is a very common practice for
     * buffer streaming for it's simplicity and relatively good
     * runtime performance.
     * Remember to bind the buffer, before calling this method.
     * The buffer object will be allocated mutably by this
     * method, meaning that it can be reallocated.
     *
     * @param buffer byte buffer
     * @param usage buffer usage
     */
    void bufferData(LongBuffer buffer, BufferUsage usage);

    /**
     * Will reallocate the buffer object and update the new data
     * set to it.
     *
     * If the provided data buffer is null, the buffer object will
     * be invalidated. The BufferUsage helps the driver to decide
     * how to allocate the buffer in terms of optimization.
     * If the buffer is invalidated and reused with the same size
     * (orphaning) there is a good change, although no guarantee,
     * that the driver will reallocate the same space in the VRAM
     * for the buffer object. This is a very common practice for
     * buffer streaming for it's simplicity and relatively good
     * runtime performance.
     * Remember to bind the buffer, before calling this method.
     * The buffer object will be allocated mutably by this
     * method, meaning that it can be reallocated.
     *
     * @param buffer byte buffer
     * @param usage buffer usage
     */
    void bufferData(FloatBuffer buffer, BufferUsage usage);

    /**
     * Will reallocate the buffer object and update the new data
     * set to it.
     *
     * If the provided data buffer is null, the buffer object will
     * be invalidated. The BufferUsage helps the driver to decide
     * how to allocate the buffer in terms of optimization.
     * If the buffer is invalidated and reused with the same size
     * (orphaning) there is a good change, although no guarantee,
     * that the driver will reallocate the same space in the VRAM
     * for the buffer object. This is a very common practice for
     * buffer streaming for it's simplicity and relatively good
     * runtime performance.
     * Remember to bind the buffer, before calling this method.
     * The buffer object will be allocated mutably by this
     * method, meaning that it can be reallocated.
     *
     * @param buffer byte buffer
     * @param usage buffer usage
     */
    void bufferData(DoubleBuffer buffer, BufferUsage usage);

    /**
     * Will reallocate the buffer object and allocate the specified
     * buffer size in bytes of empty storage.
     *
     * The BufferUsage helps the driver to decide how to allocate
     * the buffer in terms of optimization.
     * If the buffer is invalidated and reused with the same size
     * (orphaning) there is a good change, although no guarantee,
     * that the driver will reallocate the same space in the VRAM
     * for the buffer object. This is a very common practice for
     * buffer streaming for it's simplicity and relatively good
     * runtime performance.
     * Remember to bind the buffer, before calling this method.
     * The buffer object will be allocated mutably by this
     * method, meaning that it can be reallocated.
     *
     * @param size buffer size in bytes
     * @param usage buffer usage
     */
    void bufferData(long size, BufferUsage usage);

    void bufferStorage(ByteBuffer buffer, BufferFlag... flag);

    void bufferStorage(ShortBuffer buffer, BufferFlag... flag);

    void bufferStorage(IntBuffer buffer, BufferFlag... flag);

    void bufferStorage(FloatBuffer buffer, BufferFlag... flag);

    void bufferStorage(DoubleBuffer buffer, BufferFlag... flag);

    void bufferStorage(long size, BufferFlag... flag);

    void bufferSubData(ByteBuffer buffer, long offset);

    void bufferSubData(ShortBuffer buffer, long offset);

    void bufferSubData(IntBuffer buffer, long offset);

    void bufferSubData(LongBuffer buffer, long offset);

    void bufferSubData(FloatBuffer buffer, long offset);

    void bufferSubData(DoubleBuffer buffer, long offset);

    void getBufferSubData(ByteBuffer buffer, long offset);

    void getBufferSubData(ShortBuffer buffer, long offset);

    void getBufferSubData(IntBuffer buffer, long offset);

    void getBufferSubData(LongBuffer buffer, long offset);

    void getBufferSubData(FloatBuffer buffer, long offset);

    void getBufferSubData(DoubleBuffer buffer, long offset);

    /**
     * Will map the buffer using the specified buffer access.
     *
     * The buffer flags will be added to the access bit according
     * to the OpenGL specifications.
     * Remember to unmap the buffer after you finished using
     * it.
     *
     * @param access buffer access
     * @param flags access flags
     * @return mapped buffer
     */
    ByteBuffer mapBuffer(BufferAccess access, BufferFlag... flags);

    /**
     * Will map the buffer using the specified buffer access.
     *
     * The buffer flags will be added to the access bit according
     * to the OpenGL specifications.
     * Remember to unmap the buffer after you finished using
     * it. If the old buffer parameter is not null, this will
     * reuse the old buffer object for mapping.
     *
     * @param access buffer access
     * @param old_buffer old buffer
     * @param flags access flags
     * @return mapped buffer
     */
    ByteBuffer mapBuffer(BufferAccess access, ByteBuffer old_buffer, BufferFlag... flags);

    /**
     * Will map the buffer using the specified buffer access.
     *
     * The buffer flags will be added to the access bit according
     * to the OpenGL specifications.
     * Remember to unmap the buffer after you finished using
     * it. If the old buffer parameter is not null, this will
     * reuse the old buffer object for mapping.
     * The <code>byteSize</code> parameter should equal the
     * size of the buffer object in bytes. This method does exactly
     * the same thing as the other mapping methods, it just
     * avoids wasting performance on querying the buffer object's
     * size.
     *
     * @param access buffer access
     * @param byteSize size of the buffer object
     * @param old_buffer old buffer
     * @param flags access flags
     * @return mapped buffer
     */
    ByteBuffer mapBuffer(BufferAccess access, long byteSize, ByteBuffer old_buffer, BufferFlag... flags);

    /**
     * Will map part of the buffer using the specified buffer access.
     *
     * The buffer flags will be added to the access bit according
     * to the OpenGL specifications.
     * Remember to unmap the buffer after you finished using
     * it.
     *
     * @param access buffer access
     * @param off offset in bytes
     * @param len length in bytes
     * @param flags access flags
     * @return mapped buffer
     */
    ByteBuffer mapBufferRange(BufferAccess access, long off, long len, BufferFlag... flags);

    /**
     * Will map part of the buffer using the specified buffer access.
     *
     * The buffer flags will be added to the access bit according
     * to the OpenGL specifications.
     * Remember to unmap the buffer after you finished using
     * it. If the old buffer parameter is not null, this will
     * reuse the old buffer object for mapping.
     *
     * @param access buffer access
     * @param old_buffer old buffer
     * @param flags access flags
     * @return mapped buffer
     */
    ByteBuffer mapBufferRange(BufferAccess access, long off, long len, ByteBuffer old_buffer, BufferFlag... flags);

    /**
     * Will flush mapped data with the write_bit to the GPU.
     *
     * If you use this, be buffer should be mapped with the
     * MAP_FLUSH_EXPLICIT buffer access flag.
     *
     * @param off offset in bytes
     * @param len length in bytes
     */
    void flushMappedBufferRange(long off, long len);

    /**
     * Will unmap the buffer.
     *
     * After unmapping, the java Buffer Object returned during
     * the mapping process should no longer be used.
     * Remember the unmap all mapped buffers when they are no
     * longer needed.
     * If a buffer is not equipped with the PERSISTENT buffer
     * flag, the buffer HAS to be unmapped by the client, before
     * any GL commands can be issued that access the buffer.
     */
    void unmapBuffer();

    /**
     * Will unbind the buffer target of this buffer.
     *
     * This is achieved by binding the zero buffer to the
     * buffer target of this buffer object.
     */
    void unbind();

    /**
     * Returns the buffer pointer.
     * @return buffer pointer
     */
    int getPointer();

    /**
     * Will clone the buffer object, but with a different
     * buffer target.
     *
     * The pointer to the actual gl buffer will be the same,
     * thus changes to the cloned buffer will effect the
     * original buffer and vise versa.
     *
     * @param coneTarget new buffer target
     * @return cloned buffer target
     */
    GlBufferObject clone(GlBufferTarget coneTarget);
}
