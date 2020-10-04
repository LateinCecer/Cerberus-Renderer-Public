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

package com.cerberustek.shader.impl;

import com.cerberustek.CerberusRegistry;
import com.cerberustek.exceptions.GLComputeException;
import com.cerberustek.exceptions.GLComputeGroupSizeException;
import com.cerberustek.exceptions.GLShaderTypeException;
import com.cerberustek.exceptions.IllegalContextException;
import com.cerberustek.logic.math.Vector3i;
import com.cerberustek.resource.shader.SSBOResource;
import com.cerberustek.resource.shader.ShaderResource;
import com.cerberustek.resource.shader.UBOResource;
import com.cerberustek.shader.ssbo.ShaderBlock;
import com.cerberustek.shader.ssbo.ShaderStorageBufferObject;
import com.cerberustek.shader.ssbo.ShaderUniformBufferObject;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.buffer.GlBufferObject;
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.ShaderBoard;
import com.cerberustek.shader.ShaderType;
import com.cerberustek.shader.code.ShaderCodeLoader;
import com.cerberustek.shader.code.impl.SimpleShaderCodeLoader;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL43.*;

/**
 * The shader board is used to manage shaders
 */
public class ShaderBoardImpl implements ShaderBoard {

    /** Shader map */
    private final HashMap<ShaderResource, Shader> shaderMap = new HashMap<>();
    /** SSBO map */
    private final HashMap<SSBOResource, ShaderStorageBufferObject> SSBOMap = new HashMap<>();
    /** UBO map */
    private final HashMap<UBOResource, ShaderUniformBufferObject> UBOMap = new HashMap<>();
    /** bound ssbo blocks
     * Format: Binding Index, Bound block */
    private final HashMap<Integer, ShaderBlock> storageBlockMap = new HashMap<>();
    /** bound ubo blocks
     * Format: Binding Index, Bound block */
    private final HashMap<Integer, ShaderBlock> uniformBlockMap = new HashMap<>();
    /** Currently bound shader */
    private ShaderResource current;
    /** Shader code loader */
    private ShaderCodeLoader loader;
    /** Current renderer */
    private CerberusRenderer renderer;

    /* max values */
    private Vector3i maxCSGroupSize;
    private Vector3i maxCSLocalSize;
    private int maxCSLocalInvocations;
    private int maxCSSharedMem;

    @Override
    public void deleteShader(@NotNull ShaderResource resource) {
        Shader shader = getShader(resource);
        if (shader != null) {
            shader.destroy();
            shaderMap.remove(resource);
        }
    }

    @Override
    public Shader loadShader(@NotNull ShaderResource resource) {
        Shader shader = shaderMap.get(resource);
        if (shader != null)
            return shader;

        if (getRenderer().getWindow().isGlThread()) {
            shader = resource.load();
            if (shader == null)
                return null;
            shaderMap.put(resource, shader);
            return shader;
        } else {
            getRenderer().submitGLTask(d -> {
                loadShader(resource);
            });
            return null;
        }
    }

    @Override
    public Shader bindShader(@NotNull ShaderResource resource) {
        if (getRenderer().getWindow().isGlThread()) {
            Shader shader = loadShader(resource);
            if (shader != null) {
                shader.bind();
                shader.update(0);
                current = resource;
            } else {
                return null;
            }
            return shader;
        }
        return getCurrentlyBound();
    }

    @Override
    public Shader getCurrentlyBound() {
        return current != null ? shaderMap.get(current) : null;
    }

    @Override
    public ShaderResource getCurrentlyBoundResource() {
        return current;
    }

    @Override
    public ShaderCodeLoader getShaderCodeLoader() {
        return loader;
    }

    @Override
    public Vector3i maxComputeWorkGroupSize() {
        return maxCSGroupSize;
    }

    @Override
    public Vector3i maxComputeLocalGroupSize() {
        return maxCSLocalSize;
    }

    @Override
    public int maxComputeLocalInvocations() {
        return maxCSLocalInvocations;
    }

    @Override
    public int maxComputeSharedMemory() {
        return maxCSSharedMem;
    }

    @Override
    public void dispatchCompute(ShaderResource computeShader, Vector3i groupSize) throws GLComputeException,
            GLShaderTypeException {
        if (groupSize.getX() > maxCSGroupSize.getX() || groupSize.getY() > maxCSGroupSize.getY()
                || groupSize.getZ() > maxCSGroupSize.getZ())
            throw new GLComputeGroupSizeException(computeShader, groupSize);

        Shader shader = bindShader(computeShader);
        if (!shader.hasAttachment(ShaderType.COMPUTE))
            throw new GLShaderTypeException(computeShader, ShaderType.COMPUTE);

        glDispatchCompute(groupSize.getX(), groupSize.getY(), groupSize.getZ());
    }

    @Override
    public void dispatchCompute(Vector3i groupSize) throws GLComputeException, GLShaderTypeException {
        if (groupSize.getX() > maxCSGroupSize.getX() || groupSize.getY() > maxCSGroupSize.getY()
                || groupSize.getZ() > maxCSGroupSize.getZ())
            throw new GLComputeGroupSizeException(null, groupSize);

        Shader shader = getCurrentlyBound();
        if (shader == null || !shader.hasAttachment(ShaderType.COMPUTE))
            throw new GLShaderTypeException(null, ShaderType.COMPUTE);

        glDispatchCompute(groupSize.getX(), groupSize.getY(), groupSize.getZ());
    }

    @Override
    public void dispatchComputeIndirect(ShaderResource computeShader, GlBufferObject buffer, long offset) throws GLShaderTypeException {
        Shader shader = bindShader(computeShader);
        if (!shader.hasAttachment(ShaderType.COMPUTE))
            throw new GLShaderTypeException(computeShader, ShaderType.COMPUTE);

        buffer.bind();
        glDispatchComputeIndirect(offset);
    }

    @Override
    public void dispatchComputeIndirect(GlBufferObject buffer, long offset) throws GLShaderTypeException {
        Shader shader = getCurrentlyBound();
        if (shader == null || !shader.hasAttachment(ShaderType.COMPUTE))
            throw new GLShaderTypeException(null, ShaderType.COMPUTE);

        buffer.bind();
        glDispatchComputeIndirect(offset);
    }

    @Override
    public Vector3i getLocalGroupSize(ShaderResource shaderResource) throws GLShaderTypeException {
        Shader shader = bindShader(shaderResource);
        if (shader == null || !shader.hasAttachment(ShaderType.COMPUTE))
            throw new GLShaderTypeException(null, ShaderType.COMPUTE);

        IntBuffer buffer = BufferUtils.createIntBuffer(3);
        glGetProgramiv(shader.getProgramId(), GL_COMPUTE_WORK_GROUP_SIZE, buffer);
        return new Vector3i(buffer.get(0), buffer.get(1), buffer.get(2));
    }

    @Override
    public ShaderBlock getStorageBlock(int bindingIndex) {
        return storageBlockMap.get(bindingIndex);
    }

    @Override
    public ShaderBlock getUniformBlock(int bindingIndex) {
        return uniformBlockMap.get(bindingIndex);
    }

    @Override
    public ShaderStorageBufferObject loadSSBO(SSBOResource resource) {
        ShaderStorageBufferObject ssbo = SSBOMap.get(resource);
        if (ssbo != null)
            return ssbo;

        if (getRenderer().getWindow().isGlThread()) {
            ssbo = resource.load();
            SSBOMap.put(resource, ssbo);
            storageBlockMap.clear(); // clearing binding map to rebind buffers
            return ssbo;
        } else {
            getRenderer().tryGLTask(t -> {
                ShaderStorageBufferObject s = resource.load();
                SSBOMap.put(resource, s);
                storageBlockMap.clear(); // clearing binding map to rebind buffers
            });
        }
        return null;
    }

    @Override
    public ShaderStorageBufferObject getSSBO(SSBOResource resource) {
        return SSBOMap.get(resource);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public ShaderBlock bindSSBO(SSBOResource resource, int index) throws IllegalContextException {
        if (!getRenderer().getWindow().isGlThread())
            throw new IllegalContextException();

        ShaderStorageBufferObject ssbo = loadSSBO(resource);
        ShaderBlock block = ssbo.getBlock(index);
        if (block == null)
            throw new IllegalArgumentException("SSBO " + resource + " does not contain a block with index " + index);

        ShaderBlock prev = storageBlockMap.get(block.getBindingIndex());
        if (prev == null) {
            block.bind();
            storageBlockMap.put(block.getBindingIndex(), block);
        } else if (!block.equals(prev)) {
            block.bind();
            storageBlockMap.replace(block.getBindingIndex(), prev, block);
        }
        return block;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public ShaderBlock bindSSBO(SSBOResource resource, int index, int bindingIndex) throws IllegalContextException {
        if (!getRenderer().getWindow().isGlThread())
            throw new IllegalContextException();

        ShaderStorageBufferObject ssbo = loadSSBO(resource);
        ShaderBlock block = ssbo.getBlock(index);
        if (block == null)
            throw new IllegalArgumentException("SSBO " + resource + " does not contain a block with index " + index);

        ShaderBlock prev = storageBlockMap.get(bindingIndex);
        if (prev == null) {
            block.bind(bindingIndex);
            storageBlockMap.put(bindingIndex, block);
        } else if (!block.equals(prev)) {
            block.bind(bindingIndex);
            storageBlockMap.replace(bindingIndex, prev, block);
        }
        return block;
    }

    @Override
    public void deleteSSBO(SSBOResource resource) {
        ShaderStorageBufferObject ssbo = SSBOMap.get(resource);
        if (ssbo == null)
            return;

        getRenderer().tryGLTask(t -> {
            storageBlockMap.clear();
            ssbo.destroy();
            SSBOMap.remove(resource);
        });
    }

    @Override
    public ShaderUniformBufferObject loadUBO(UBOResource resource) {
        ShaderUniformBufferObject ubo = UBOMap.get(resource);
        if (ubo != null)
            return ubo;

        if (getRenderer().getWindow().isGlThread()) {
            ubo = resource.load();
            UBOMap.put(resource, ubo);
            uniformBlockMap.clear(); // clearing binding map, to rebind buffers
            return ubo;
        } else {
            getRenderer().tryGLTask(t -> {
                ShaderUniformBufferObject u = resource.load();
                UBOMap.put(resource, u);
                uniformBlockMap.clear(); // clearing binding map, to rebind buffers
            });
        }
        return null;
    }

    @Override
    public ShaderUniformBufferObject getUBO(UBOResource resource) {
        return UBOMap.get(resource);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public ShaderBlock bindUBO(UBOResource resource, int index) {
        if (!getRenderer().getWindow().isGlThread())
            throw new IllegalContextException();

        ShaderUniformBufferObject ubo = loadUBO(resource);
        ShaderBlock block = ubo.getBlock(index);
        if (block == null)
            throw new IllegalArgumentException("UBO " + resource + " does not contain block with index " + index);

        ShaderBlock prev = uniformBlockMap.get(block.getBindingIndex());
        if (prev == null) {
            block.bind();
            uniformBlockMap.put(block.getBindingIndex(), block);
        } else if (!block.equals(prev)) {
            block.bind();
            uniformBlockMap.replace(block.getBindingIndex(), prev, block);
        }
        return block;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public ShaderBlock bindUBO(UBOResource resource, int index, int bindingIndex) {
        if (!getRenderer().getWindow().isGlThread())
            throw new IllegalContextException();

        ShaderUniformBufferObject ubo = loadUBO(resource);
        ShaderBlock block = ubo.getBlock(index);
        if (block == null)
            throw new IllegalArgumentException("UBO " + resource + " does not contain block with index " + index);

        ShaderBlock prev = uniformBlockMap.get(bindingIndex);
        if (prev == null) {
            block.bind(bindingIndex);
            uniformBlockMap.put(bindingIndex, block);
        } else if (!block.equals(prev)) {
            block.bind(bindingIndex);
            uniformBlockMap.replace(bindingIndex, prev, block);
        }
        return block;
    }

    @Override
    public void deleteUBO(UBOResource resource) {
        ShaderUniformBufferObject ubo = UBOMap.get(resource);
        if (ubo == null)
            return;

        getRenderer().tryGLTask(t -> {
            uniformBlockMap.clear();
            ubo.destroy();
            UBOMap.remove(resource);
        });
    }

    @Override
    public void destroy() {
        loader.destroy();
        loader = null;
        shaderMap.values().forEach(Shader::destroy);
        shaderMap.clear();

        uniformBlockMap.clear();
        storageBlockMap.clear();

        SSBOMap.values().forEach(ShaderStorageBufferObject::destroy);
        SSBOMap.clear();
        UBOMap.values().forEach(ShaderUniformBufferObject::destroy);
        UBOMap.clear();
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void init() {
        loader = new SimpleShaderCodeLoader(new File(CerberusRegistry.getInstance().getService(CerberusRenderer.class).
                getSettings().getString("shader_folder", "shaders")));

        // load max values
        final int[] values = new int[1];

        maxCSGroupSize = new Vector3i(0, 0, 0);
        glGetIntegeri_v(GL_MAX_COMPUTE_WORK_GROUP_COUNT, 0, values);
        maxCSGroupSize.setX(values[0]);
        glGetIntegeri_v(GL_MAX_COMPUTE_WORK_GROUP_COUNT, 1, values);
        maxCSGroupSize.setY(values[0]);
        glGetIntegeri_v(GL_MAX_COMPUTE_WORK_GROUP_COUNT, 2, values);
        maxCSGroupSize.setZ(values[0]);

        maxCSLocalSize = new Vector3i(0, 0, 0);
        glGetIntegeri_v(GL_MAX_COMPUTE_WORK_GROUP_SIZE, 0, values);
        maxCSLocalSize.setX(values[0]);
        glGetIntegeri_v(GL_MAX_COMPUTE_WORK_GROUP_SIZE, 1, values);
        maxCSLocalSize.setY(values[0]);
        glGetIntegeri_v(GL_MAX_COMPUTE_WORK_GROUP_SIZE, 2, values);
        maxCSLocalSize.setZ(values[0]);

        maxCSLocalInvocations = glGetInteger(GL_MAX_COMPUTE_WORK_GROUP_INVOCATIONS);
        maxCSSharedMem = glGetInteger(GL_MAX_COMPUTE_SHARED_MEMORY_SIZE);

        CerberusRegistry.getInstance().debug("Init shader cache with compute properties:" +
                "\n\t# MAX COMPUTE WORK GROUP COUNT> " + maxCSGroupSize +
                "\n\t# MAX COMPUTE WORK GROUP SIZE> " + maxCSLocalSize +
                "\n\t# MAX COMPUTE WORK GROUP INVOCATIONS> " + maxCSLocalInvocations +
                "\n\t# MAX COMPUTE SHARED MEMORY> " + maxCSSharedMem + " Bytes");
    }

    @Override
    public Shader getShader(@NotNull ShaderResource resource) {
        try {
            Shader shader = shaderMap.get(resource);
            if (shader == null)
                loadShader(resource);
            return shader;
        } catch (ConcurrentModificationException e) {
            return getShader(resource);
        }
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }
}
