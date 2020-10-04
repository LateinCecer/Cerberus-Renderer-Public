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

package com.cerberustek.geometry.impl;

import com.cerberustek.exceptions.BufferFormatException;
import com.cerberustek.exceptions.IllegalContextException;
import com.cerberustek.exceptions.IllegalDrawStateException;
import com.cerberustek.geometry.*;
import com.cerberustek.geometry.impl.formats.VertexFormatF;
import com.cerberustek.geometry.impl.formats.VertexFormatI;
import com.cerberustek.resource.model.IndexBufferArrayResource;
import com.cerberustek.resource.model.ModelResource;
import com.cerberustek.resource.model.VertexAttribResource;
import com.cerberustek.resource.model.VertexBufferResource;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.Initable;
import org.jetbrains.annotations.NotNull;

import java.util.ConcurrentModificationException;
import java.util.HashMap;

import static org.lwjgl.opengl.GL43.*;

public class GeometryBoardImpl implements GeometryBoard, Initable {

    /** The mesh map
     * Where al' dm meshes are stor'd */
    private final HashMap<ModelResource, Mesh> meshMap = new HashMap<>();
    private final HashMap<VertexBufferResource, VertexBuffer> vertexMap = new HashMap<>();
    private final HashMap<IndexBufferArrayResource, IndexBufferArray> indexMap = new HashMap<>();
    /** Vertex attributes */
    private final HashMap<VertexAttribResource, VertexAttribBinding> attribMap = new HashMap<>();
    /** The current renderer */
    private CerberusRenderer renderer;
    /** The currently bound vertex buffer */
    private VertexBuffer vertexBuffer;
    /** The currently bound index buffer */
    private IndexBuffer indexBuffer;
    /** Loaded vertex formats */
    private final VertexFormat[] formats = new VertexFormat[MAX_VERTEX_ATTRIBUTES];
    /** contains the binding Id's for each attribute index */
    private final VertexAttribResource[] attribs = new VertexAttribResource[MAX_VERTEX_ATTRIBUTES];

    @Override
    public void init() {
        // init default formats
        formats[VERTEX] = new VertexFormatF(DataType.VEC3, ComponentType.FLOAT, 0);
        formats[TEXCOORD_0] = new VertexFormatF(DataType.VEC2, ComponentType.FLOAT, 0);
        formats[NORMAL_0] = new VertexFormatF(DataType.VEC3, ComponentType.FLOAT, 0);
        formats[TEXCOORD_1] = new VertexFormatF(DataType.VEC2, ComponentType.FLOAT, 0);
        formats[NORMAL_1] = new VertexFormatF(DataType.VEC3, ComponentType.FLOAT, 0);
        formats[TEXCOORD_2] = new VertexFormatF(DataType.VEC2, ComponentType.FLOAT, 0);
        formats[NORMAL_2] = new VertexFormatF(DataType.VEC3, ComponentType.FLOAT, 0);
        formats[TEXCOORD_3] = new VertexFormatF(DataType.VEC2, ComponentType.FLOAT, 0);
        formats[NORMAL_3] = new VertexFormatF(DataType.VEC3, ComponentType.FLOAT, 0);

        formats[TRANSLATION] = new VertexFormatF(DataType.VEC3, ComponentType.FLOAT, 0);
        formats[ROTATION] = new VertexFormatF(DataType.MAT3, ComponentType.FLOAT, 0);
        formats[SCALE] = new VertexFormatF(DataType.VEC3, ComponentType.FLOAT, 0);
        formats[SAMPLER_INDEX] = new VertexFormatI(DataType.SCALAR, ComponentType.UNSIGNED_INT, 0);
        formats[COLOR_MOD] = new VertexFormatF(DataType.VEC4, ComponentType.FLOAT, 0);
        formats[MAT_INDEX] = new VertexFormatI(DataType.SCALAR, ComponentType.UNSIGNED_INT, 0);

        formats[BONE_INDEX] = new VertexFormatI(DataType.SCALAR, ComponentType.UNSIGNED_INT, 0);
    }

    @Override
    public void deleteMesh(@NotNull ModelResource resource) {
        Mesh mesh = getMesh(resource);
        if (mesh != null) {
            if (mesh.getVertexBuffer().equals(vertexBuffer)) {
                vertexBuffer.unbind();
                vertexBuffer = null;
            }

            mesh.destroy();
            meshMap.remove(resource);
        }
    }

    @Override
    public void deleteIndexBufferArray(@NotNull IndexBufferArrayResource resource) {
        IndexBufferArray array = indexMap.get(resource);
        if (array != null) {
            if (array.contains(this.indexBuffer)) {
                indexBuffer.unbind();
                indexBuffer = null;
            }

            array.destroy();
            indexMap.remove(resource);
        }
    }

    @Override
    public void deleteVertexBuffer(@NotNull VertexBufferResource resource) {
        VertexBuffer vertexBuffer = vertexMap.get(resource);
        if (vertexBuffer != null) {
            if (vertexBuffer.equals(this.vertexBuffer)) {
                this.vertexBuffer.unbind();
                this.vertexBuffer = null;
            }

            vertexBuffer.destroy();
            vertexMap.remove(resource);
        }
    }

    @Override
    public void loadMesh(@NotNull ModelResource resource) {
        if (!meshMap.containsKey(resource))
            getRenderer().tryGLTask((t) ->
                    meshMap.put(resource, resource.load()));
    }

    @Override
    public void loadIndexBufferArray(@NotNull IndexBufferArrayResource resource) {
        if (!indexMap.containsKey(resource))
            getRenderer().tryGLTask((t) ->
                    indexMap.put(resource, resource.load()));
    }

    @Override
    public void loadVertexBufferArray(@NotNull VertexBufferResource resource) {
        if (!vertexMap.containsKey(resource))
            getRenderer().tryGLTask((t) ->
                    vertexMap.put(resource, resource.load()));
    }

    @Override
    public Mesh bindMesh(@NotNull ModelResource resource) {
        Mesh mesh = meshMap.get(resource);
        if (mesh == null) {
            getRenderer().tryGLTask((t) -> meshMap.put(resource, resource.load()));
            if ((mesh = meshMap.get(resource)) == null)
                throw new IllegalStateException("Failed to load async mesh. Wait until next frame!");
        }

        bindVertexBuffer(mesh.getVertexBuffer());
        bindIndexBuffer(mesh.getIndexBuffer());
        return mesh;
    }

    @Override
    public VertexBuffer bindVertexBuffer(@NotNull final VertexBuffer vertexBuffer) {
        if (this.vertexBuffer == null) {
            this.vertexBuffer = vertexBuffer;
            getRenderer().tryGLTask((t) -> this.vertexBuffer.bind());
            return vertexBuffer;
        } else if (!this.vertexBuffer.equals(vertexBuffer)){
            getRenderer().tryGLTask((t) -> {
                this.vertexBuffer.unbind();
                this.vertexBuffer = vertexBuffer;
                this.vertexBuffer.bind();
            });
            return vertexBuffer;
        }
        return this.vertexBuffer;
    }

    @Override
    public VertexBuffer bindVertexBuffer(@NotNull Mesh mesh) {
        return bindVertexBuffer(mesh.getVertexBuffer());
    }

    @Override
    public VertexBuffer bindVertexBuffer(@NotNull VertexBufferResource resource) {
        VertexBuffer vertexBuffer = vertexMap.get(resource);
        if (vertexBuffer == null) {
            if (getRenderer().getWindow().isGlThread()) {
                vertexBuffer = resource.load();
                vertexMap.put(resource, vertexBuffer);
            } else {
                getRenderer().tryGLTask((t) -> vertexMap.put(resource, resource.load()));
                return this.vertexBuffer;
            }
        } else if (this.vertexBuffer != null && this.vertexBuffer.equals(vertexBuffer))
            return this.vertexBuffer;

        this.vertexBuffer = vertexBuffer;
        vertexBuffer.bind();
        return this.vertexBuffer;
    }

    @Override
    public VertexBuffer bindVertexBuffer(@NotNull ModelResource resource) {
        Mesh mesh = getMesh(resource);
        if (mesh != null)
            return bindVertexBuffer(mesh);
        return null;
    }

    @Override
    public IndexBuffer bindIndexBuffer(@NotNull final IndexBuffer indexBuffer) {
        // The index buffer does not actually be bound, because
        // binding element array buffers is not expensive and
        // the buffer is bound before each draw call to avoid
        // confusion.
        if (this.indexBuffer == null) {
            this.indexBuffer = indexBuffer;
            getRenderer().tryGLTask((t) -> this.indexBuffer.bind());
            return this.indexBuffer;
        } else if (!this.indexBuffer.equals(indexBuffer)){
            getRenderer().tryGLTask((t) -> {
                this.indexBuffer = indexBuffer;
                this.indexBuffer.bind();
            });
            return this.indexBuffer;
        }
        return this.indexBuffer;
    }

    @Override
    public IndexBuffer bindIndexBuffer(@NotNull Mesh mesh) {
        return bindIndexBuffer(mesh.getIndexBuffer());
    }

    @Override
    public IndexBuffer bindIndexBuffer(@NotNull ModelResource resource) {
        Mesh mesh = getMesh(resource);
        if (mesh != null)
            return bindIndexBuffer(mesh.getIndexBuffer());
        return null;
    }

    @Override
    public VertexBuffer boundVertexBuffer() {
        return vertexBuffer;
    }

    @Override
    public IndexBuffer boundIndexBuffer() {
        return indexBuffer;
    }

    @Override
    public Mesh drawMesh(@NotNull ModelResource resource, @NotNull DrawMode mode) {
        Mesh mesh = bindMesh(resource);
        if (mesh != null) {
            if (getRenderer().getWindow().isGlThread())
                mesh.getIndexBuffer().draw(mode);
            else
                throw new IllegalContextException();
        }
        return mesh;
    }

    @Override
    public Mesh drawMeshInstanced(@NotNull ModelResource resource, @NotNull DrawMode mode, int primitives) {
        Mesh mesh = bindMesh(resource);
        if (mesh != null) {
            if (getRenderer().getWindow().isGlThread())
                mesh.getIndexBuffer().drawInstanced(mode, primitives);
            else
                throw new IllegalContextException();
        }
        return mesh;
    }

    @Override
    public VertexFormat getVertexAttributeFormat(int attribIndex) {
        if (attribIndex < 0 || attribIndex >= formats.length)
            throw new ArrayIndexOutOfBoundsException();
        return formats[attribIndex];
    }

    @Override
    public void setVertexAttributeFormat(int attribIndex, VertexFormat format) {
        if (formats[attribIndex] == null) {
            formats[attribIndex] = format;
        } else {
            if (format == null) {
                if (attribs[attribIndex] != null)
                    unbindVertexAttribute(attribIndex);
            } else {
                formats[attribIndex] = format;

                if (attribs[attribIndex] != null)
                    format.bind(attribIndex);
            }
        }
    }

    @Override
    public VertexAttribBinding loadVertexAttribute(VertexAttribResource resource) {
        VertexAttribBinding binding = attribMap.get(resource);
        if (binding == null) {

            CerberusRenderer renderer = getRenderer();
            if (renderer.getWindow().isGlThread()) {
                binding = resource.load();
                attribMap.put(resource, binding);
                return binding;
            } else {
                renderer.tryGLTask(t -> loadVertexAttribute(resource));
                return null;
            }

        } else
            return binding;
    }

    @Override
    public VertexAttribResource bindVertexAttribute(VertexAttribResource resource, int index) {
        if (getRenderer().getWindow().isGlThread()) {
            if (formats[index] == null)
                throw new BufferFormatException("There is currently no format loaded for the" +
                        " vertex attribute with index: " + index);

            if (resource != null) {
                VertexAttribBinding binding = attribMap.get(resource);
                if (binding == null) {
                    loadVertexAttribute(resource);
                    return attribs[index];
                }
                binding.bindBuffer(index);

                // load new vertex attrib resource
                if (attribs[index] == null) {
                    // enable the vertex attribute
                    glEnableVertexAttribArray(index);

                    formats[index].setRelativeOffset(binding.getRelativeOffset());
                    formats[index].bind(index);
                    glVertexAttribBinding(index, index);
                } else {
                    VertexAttribBinding prev = attribMap.get(attribs[index]);
                    if (prev == null)
                        throw new IllegalStateException("Unloaded buffer bound!");
                    prev.unbind();

                    // reset divisor if necessary
                    if (prev instanceof VertexDivisorBinding && !(binding instanceof VertexDivisorBinding))
                        glVertexBindingDivisor(index, 0);

                    // update relative offset if necessary
                    if (formats[index].getRelativeOffset() != binding.getRelativeOffset()) {
                        formats[index].setRelativeOffset(binding.getRelativeOffset());
                        formats[index].bind(index);
                    }
                }

                return attribs[index] = resource;
            } else {
                // disable the vertex attrib index
                unbindVertexAttribute(index);
                return null;
            }
        } else {
            return attribs[index];
        }
    }

    @Override
    public VertexAttribBinding getVertexAttribute(VertexAttribResource resource) {
        return attribMap.get(resource);
    }

    @Override
    public void unbindVertexAttribute(int index) {
        if (attribs[index] != null) {
            final VertexAttribBinding binding = attribMap.get(attribs[index]);
            if (binding == null)
                throw new IllegalStateException("Unloaded resource bound!");

            // some buffer is bound.
            // unbind and disable the vertex attribute
            getRenderer().tryGLTask(t -> {
                glDisableVertexAttribArray(index);
                attribs[index] = null;
                binding.unbind();
                // unbind buffer
                // glBindVertexBuffer(index, 0, 0, 0);
            });
        }
    }

    @Override
    public int getBindingIndex(VertexAttribResource resource) {
        for (int i = 0; i < attribs.length; i++) {
            if (resource.equals(attribs[i]))
                return i;
        }
        return -1;
    }

    @Override
    public void deleteVertexAttribute(VertexAttribResource resource) {
        if (getRenderer().getWindow().isGlThread()) {
            try {
                VertexAttribBinding binding = attribMap.get(resource);
                if (binding == null)
                    return;

                // unbind if the attribute buffer is currently bound
                for (int i = 0; i < attribs.length; i++) {
                    if (resource.equals(attribs[i]))
                        unbindVertexAttribute(i);
                }
                binding.destroy();
                attribMap.remove(resource);

            } catch (ConcurrentModificationException e) {
                deleteVertexAttribute(resource);
            }
        } else
            getRenderer().tryGLTask(t -> deleteVertexAttribute(resource));
    }

    @Override
    public Mesh getMesh(@NotNull ModelResource resource) {
        try {
            Mesh mesh = meshMap.get(resource);
            if (mesh == null) {
                if (getRenderer().getWindow().isGlThread()) {
                    mesh = resource.load();
                    meshMap.put(resource, mesh);
                } else
                    loadMesh(resource);
            }
            return mesh;
        } catch (ConcurrentModificationException e) {
            return getMesh(resource);
        }
    }

    @Override
    public VertexBuffer getVertexBuffer(@NotNull VertexBufferResource resource) {
        try {
            VertexBuffer vertexBuffer = vertexMap.get(resource);
            if (vertexBuffer == null) {
                if (getRenderer().getWindow().isGlThread()) {
                    vertexBuffer = resource.load();
                    vertexMap.put(resource, vertexBuffer);
                } else
                    loadVertexBufferArray(resource);
            }
            return vertexBuffer;
        } catch (ConcurrentModificationException e) {
            return getVertexBuffer(resource);
        }
    }

    @Override
    public IndexBufferArray getIndexBufferArray(@NotNull IndexBufferArrayResource resource) {
        try {
            IndexBufferArray indexBuffer = indexMap.get(resource);
            if (indexBuffer == null) {
                if (getRenderer().getWindow().isGlThread()) {
                    indexBuffer = resource.load();
                    indexMap.put(resource, indexBuffer);
                } else
                    loadIndexBufferArray(resource);
            }
            return indexBuffer;
        } catch (ConcurrentModificationException e) {
            return getIndexBufferArray(resource);
        }
    }

    @Override
    public void drawMesh(@NotNull DrawMode mode) {
        if (vertexBuffer == null || indexBuffer == null)
            throw new IllegalDrawStateException(vertexBuffer, indexBuffer);

        if (getRenderer().getWindow().isGlThread())
            indexBuffer.draw(mode);
        else
            throw new IllegalContextException();
    }

    @Override
    public void drawMeshInstanced(@NotNull DrawMode mode, int count) {
        if (indexBuffer == null)
            throw new IllegalDrawStateException(vertexBuffer, null);

        if (getRenderer().getWindow().isGlThread()) {
            indexBuffer.drawInstanced(mode, count);
        } else
            throw new IllegalContextException();
    }

    @Override
    public void destroy() {
        meshMap.values().forEach(Mesh::destroy);
        meshMap.clear();

        vertexMap.values().forEach(VertexBuffer::destroy);
        vertexMap.clear();

        indexMap.values().forEach(IndexBufferArray::destroy);
        indexMap.clear();

        attribMap.values().forEach(VertexAttribBinding::destroy);
        attribMap.clear();

        // disable vertex attributes
        for (int i = 0; i < GeometryBoard.MAX_VERTEX_ATTRIBUTES; i++)
            glDisableVertexAttribArray(i);
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }
}
