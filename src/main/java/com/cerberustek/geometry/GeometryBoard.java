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

import com.cerberustek.exceptions.IllegalContextException;
import com.cerberustek.exceptions.IllegalDrawStateException;
import com.cerberustek.resource.model.IndexBufferArrayResource;
import com.cerberustek.resource.model.ModelResource;
import com.cerberustek.resource.model.VertexAttribResource;
import com.cerberustek.resource.model.VertexBufferResource;
import com.cerberustek.Destroyable;
import com.cerberustek.Initable;
import org.jetbrains.annotations.NotNull;

public interface GeometryBoard extends Destroyable, Initable {

    /* standard attributes */
    int VERTEX = 0;                     // VEC3
    int TEXCOORD_0 = 1;                 // VEC2
    int NORMAL_0 = 2;                   // VEC3
    int TEXCOORD_1 = 3;                 // VEC2
    int NORMAL_1 = 4;                   // VEC3
    int TEXCOORD_2 = 5;                 // VEC2
    int NORMAL_2 = 6;                   // VEC3
    int TEXCOORD_3 = 7;                 // VEC2
    int NORMAL_3 = 8;                   // VEC3

    /* attributes for instanced draw calls */
    int TRANSLATION = 9;                // VEC3
    int ROTATION = 10;                  // MAT3
    int SCALE = 11;                     // VEC3
    int SAMPLER_INDEX = 12;             // SCALAR
    int COLOR_MOD = 13;                 // VEC4
    int MAT_INDEX = 14;                 // SCALAR

    /* animation indices for binding vertices to animation matrices/bones */
    int BONE_INDEX = 15;                // SCALAR

    int MAX_VERTEX_ATTRIBUTES = 16;

    /**
     * Deletes the model in question.
     *
     * If the model you are trying to delete is already unknown
     * to the system, nothing happens. If the current thread is not
     * a Gl-Render-thread, this method will attempt to delete the
     * mesh async.
     * @param resource resource of the model to delete
     */
    void deleteMesh(@NotNull ModelResource resource);

    /**
     * Deletes the IndexBufferArrayResource in question.
     *
     * If the index buffer you are trying to delete is already unknown
     * to the system, nothing will happen. If the current thread is not
     * a Gl-Render-thread, this method will attempt to delete the
     * index buffer array async.
     * @param resource resource of the index buffer array
     */
    void deleteIndexBufferArray(@NotNull IndexBufferArrayResource resource);

    /**
     * Deletes the VertexBufferResource in question.
     *
     * If the vertex buffer you are trying to delete is already unknown
     * to the system, nothing will happen. If the current thread is not
     * a Gl-Render-thread, this method will attempt to delete the
     * vertex buffer array async.
     * @param resource resource of the vertex buffer array
     */
    void deleteVertexBuffer(@NotNull VertexBufferResource resource);

    /**
     * Loads a model.
     *
     * If the model you are trying to load has already been load,
     * nothing happens. If the current thread is not a Gl-Render-
     * thread, this method will attempt to load the model async in
     * the main render thread.
     * @param resource resource of the model to load
     */
    void loadMesh(@NotNull ModelResource resource);

    /**
     * Loads an index buffer array.
     *
     * If the index buffer array you are trying to load has already
     * been load, nothing happens. If the current thread is not a Gl-
     * Render-thread, this method will attempt to load the model async
     * in the main render thread.
     * @param resource resource of the index array buffer to load
     */
    void loadIndexBufferArray(@NotNull IndexBufferArrayResource resource);

    /**
     * Loads a vertex buffer.
     *
     * If the vertex buffer array you are trying to load has already
     * been load, nothing happens. If the current thread is not a Gl-
     * Render-thread, this method will attempt to load the model async
     * in the main render thread.
     * @param resource resource of the vertex buffer to load
     */
    void loadVertexBufferArray(@NotNull VertexBufferResource resource);

    /**
     * Binds a model.
     *
     * If the model is not already bound, this method will
     * attempt to bind the model. If the current thread is not a
     * GL-Render-thread, this method will attempt to load the
     * model async in the main render thread.
     * Essentially, by this action, the render attribute pointers
     * are set. Because this action generates quite a big overhead
     * in the render query, try to load as few models as possible.
     * A loaded model can be drawn partially, so it may be
     * advisable to load multiple smaller models as one big one.
     * @param resource model resource
     */
    Mesh bindMesh(@NotNull ModelResource resource);

    /**
     * Will bind a vertex buffer.
     * @param vertexBuffer vertex buffer to bind
     * @return vertex buffer bound
     */
    VertexBuffer bindVertexBuffer(@NotNull VertexBuffer vertexBuffer);

    /**
     * Will bind the vertex buffer of the mesh.
     * @param mesh mesh
     * @return vertex buffer bound
     */
    VertexBuffer bindVertexBuffer(@NotNull Mesh mesh);

    /**
     * Will bind the vertex buffer associated with the
     * vertex buffer resource.
     * @param resource resource of the vertex buffer
     * @return vertex buffer bound
     */
    VertexBuffer bindVertexBuffer(@NotNull VertexBufferResource resource);

    /**
     * Will bind the vertex buffer associated with the
     * model resource.
     * @param resource model resource
     * @return vertex buffer bound
     */
    VertexBuffer bindVertexBuffer(@NotNull ModelResource resource);

    /**
     * Will bind an index buffer.
     * @param indexBuffer index buffer to bind
     * @return index buffer bound
     */
    IndexBuffer bindIndexBuffer(@NotNull IndexBuffer indexBuffer);

    /**
     * Will bind the index buffer of the mesh.
     * @param mesh mesh
     * @return index buffer bound
     */
    IndexBuffer bindIndexBuffer(@NotNull Mesh mesh);

    /**
     * Will bind the index buffer associated with the
     * model resource
     * @param resource model resource
     * @return index buffer bound
     */
    IndexBuffer bindIndexBuffer(@NotNull ModelResource resource);

    /**
     * Will return the bound vertex buffer
     * @return currently bound vertex buffer
     */
    VertexBuffer boundVertexBuffer();

    /**
     * Will return the bound index buffer
     * @return currently bound index buffer
     */
    IndexBuffer boundIndexBuffer();

    /**
     * Returns the model associated with the specified resource.
     *
     * If the model is currently not known to the system, this method
     * will return null, but try to load the model, possibly async.
     *
     * @param resource resource of the model to load
     * @return the model to load
     */
    Mesh getMesh(@NotNull ModelResource resource);

    /**
     * Returns the vertex buffer associated with the specified resource.
     *
     * If the vertex buffer is currently not known to the system, this method
     * will return null, but try to load the model, possibly async.
     *
     * @param resource resource of the vertex buffer to load
     * @return the vertex buffer loaded
     */
    VertexBuffer getVertexBuffer(@NotNull VertexBufferResource resource);

    /**
     * Returns the index buffer array associated with the specified
     * resource.
     *
     * If the index buffer array is currently not known to the system, this
     * method will return null, but try to load the model, possibly async.
     *
     * @param resource resource of the index buffer array to load
     * @return the index buffer array loaded
     */
    IndexBufferArray getIndexBufferArray(@NotNull IndexBufferArrayResource resource);

    /**
     * Draws the currently bound model.
     *
     * If there is a model currently bound, this method will draw set
     * model.
     * Draw calls can only be issued within a GL-thread.
     * If the thread used to call this method is not a GL-
     * thread, this method will throw an exception.
     *
     * @param mode draw mode
     * @throws IllegalDrawStateException Exception thrown,
     *          if either the vertex, or the index buffer
     *          is missing/not bound
     * @throws IllegalContextException Exception thrown,
     *          if the thread in which the method is
     *          called, is not a GL thread.
     */
    void drawMesh(@NotNull DrawMode mode);

    /**
     * Draws the currently bound model instanced.
     *
     * The <code>mode</code> specifies the draw mode. If you
     * want to render triangles, this would for example be
     * DrawMode.TRIANGLE.
     * The <code>count </code> parameter specifies the amount
     * of instanced that are supposed to be drawn.
     *
     * @param mode draw mode
     * @param primitives amount of instances
     */
    void drawMeshInstanced(@NotNull DrawMode mode, int primitives);

    /**
     * Draws a model.
     *
     * If the model does not exist, this method will not draw it,
     * but instead try to load it for the future.
     * Draw calls can only be issued within a GL-thread.
     * If the thread used to call this method is not a GL-
     * thread, this method will throw an exception.
     *
     * @param resource resource of the model to draw
     * @param mode draw mode
     * @return the model drawn.
     * @throws IllegalContextException Exception thrown,
     *          if the thread in which the method is
     *          called, is not a GL thread.
     */
    Mesh drawMesh(@NotNull ModelResource resource, @NotNull DrawMode mode);


    /**
     * Draws the specified bound model instanced.
     *
     * The <code>mode</code> specifies the draw mode. If you
     * want to render triangles, this would for example be
     * DrawMode.TRIANGLE.
     * The <code>count </code> parameter specifies the amount
     * of instanced that are supposed to be drawn.
     * If the model does not exist, this method will not draw it,
     * but instead try to load it for the future.
     * Draw calls can only be issued within a GL-thread.
     * If the thread used to call this method is not a GL-
     * thread, this method will throw an exception.
     *
     * @param resource the model to be drawn
     * @param mode draw mode
     * @param primitives amount of instances
     * @throws IllegalContextException Exception thrown,
     *          if the thread in which the method is
     *          called, is not a GL thread.
     */
    Mesh drawMeshInstanced(@NotNull ModelResource resource, @NotNull DrawMode mode, int primitives);

    /**
     * Returns the vertex attribute format, that is bound to the
     * attribute index.
     * @param attribIndex attribute index
     * @return the bound attribute format
     */
    VertexFormat getVertexAttributeFormat(int attribIndex);

    /**
     * Will set the attribute format.
     *
     * If the attribute format is set to null, the attribute index will
     * be disabled.
     *
     * @param attribIndex index of the attribute
     * @param format vertex format to set
     */
    void setVertexAttributeFormat(int attribIndex, VertexFormat format);

    /**
     * This method will attempt to load a vertex attribute based on the
     * specified resource.
     *
     * If the vertex attribute is already loaded, this method will return
     * the vertex attribute directly, without doing anything more.
     * If the vertex attribute is not loaded, and this is a GL-Thread,
     * this method will load the attribute directly and return it.
     * Otherwise, this method will attempt to load the vertex attribute
     * async and return null.
     *
     * @param resource resource of the attribute to load
     * @return vertex attribute buffer
     */
    VertexAttribBinding loadVertexAttribute(VertexAttribResource resource);

    /**
     * Will attempt to bind the vertex attribute to an attribute index.
     *
     * If the vertex attribute is already known to the system, this method
     * will load and return it. Otherwise this method will load the attribute,
     * possibly async.
     * If this is not a GL-Thread, this method will throw an exception.
     *
     * @param resource vertex attribute resource
     * @param index attribute index
     * @return vertex attribute
     */
    VertexAttribResource bindVertexAttribute(VertexAttribResource resource, int index);

    /**
     * Will attempt to get the vertex attribute attributed to the
     * resource.
     *
     * @param resource vertex attribute resource
     * @return Vertex Attribute
     */
    VertexAttribBinding getVertexAttribute(VertexAttribResource resource);

    /**
     * Will unbind the vertex attribute at the specified attribute index.
     *
     * If there is no attribute currently bound to set index, this
     * method will have no effect.
     *
     * @param index attribute index
     */
    void unbindVertexAttribute(int index);

    /**
     * Returns the vertex attribute binding of the vertex bending.
     *
     * If the Vertex attribute associated with the resource is
     * currently not bound, this method will return -1.
     * @param resource vertex attribute resource
     * @return resource
     */
    int getBindingIndex(VertexAttribResource resource);

    /**
     * Will attempt to delete the vertex attribute from the system.
     *
     * If the attribute is currently not known to the geometry board,
     * this method will have no effect.
     * If the attribute is known, and this a GL-Thread, this method
     * will delete the attribute directly. Otherwise, it will
     * attempt to delete the vertex attribute async.
     *
     * @param resource resource of the vertex attribute to delete
     */
    void deleteVertexAttribute(VertexAttribResource resource);
}
