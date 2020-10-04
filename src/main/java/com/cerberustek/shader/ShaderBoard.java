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

package com.cerberustek.shader;

import com.cerberustek.Destroyable;
import com.cerberustek.Initable;
import com.cerberustek.exceptions.GLComputeException;
import com.cerberustek.exceptions.GLShaderTypeException;
import com.cerberustek.exceptions.IllegalContextException;
import com.cerberustek.logic.math.Vector3i;
import com.cerberustek.resource.shader.SSBOResource;
import com.cerberustek.resource.shader.ShaderResource;
import com.cerberustek.resource.shader.UBOResource;
import com.cerberustek.shader.code.ShaderCodeLoader;
import com.cerberustek.shader.ssbo.ShaderBlock;
import com.cerberustek.shader.ssbo.ShaderStorageBufferObject;
import com.cerberustek.shader.ssbo.ShaderUniformBufferObject;
import com.cerberustek.buffer.GlBufferObject;
import org.jetbrains.annotations.NotNull;

public interface ShaderBoard extends Destroyable, Initable {

    /**
     * Will attempt to delete the shader associated by the shader
     * resource.
     *
     * If there is no shader linked to the shader resource, nothing
     * will happen. If the current thread is not the Gl-Render-thread,
     * this method will try to delete the shader async on the render
     * thread.
     * @param resource to delete
     */
    void deleteShader(@NotNull ShaderResource resource);

    /**
     * Will attempt to load a shader.
     *
     * If the shader associated by the shader resource has already
     * been loaded, nothing will happen. If the current thread is
     * not a Gl-Render-thread this method will try to load the shader
     * async on the main render thread.
     * @param shader shader to load
     */
    Shader loadShader(@NotNull ShaderResource shader);

    /**
     * Returns the shader associated to the {@code resource}.
     *
     * If the shader has not yet been properly load, this method
     * will return null, but try to load the shader.#
     *
     * @param resource resource the shader is associated with
     * @return shader
     */
    Shader getShader(@NotNull ShaderResource resource);

    /**
     * This method will bind a shader program the the current
     * Gl-RenderPipeline.
     *
     * If the shader you are trying to bind is currently not known
     * to the system, this method will dismiss to bind the shader,
     * but try to load it, possibly in an other thread.
     * If the current thread is not a Gl-Render-thread, nothing
     * will happen. This method will always return the shader
     * which is currently bound to the render pipeline.
     * @param shader shader to bind
     * @return shader bound.
     */
    Shader bindShader(@NotNull ShaderResource shader);

    /**
     * Returns the currently bound shader.
     * @return shader that is currently bound
     */
    Shader getCurrentlyBound();

    /**
     * Returns the currently bound shader resource.
     * @return shader resource of the shader that is
     *          currently bound.
     */
    ShaderResource getCurrentlyBoundResource();

    /**
     * Returns the current shader code loader.
     * @return shader code loader
     */
    ShaderCodeLoader getShaderCodeLoader();

    /**
     * Returns the max size in compute work groups.
     *
     * This size in given as a three dimensional array.
     *
     * @return max compute work groups
     */
    Vector3i maxComputeWorkGroupSize();

    /**
     * Returns the max size in invocations of the local
     * compute group.
     *
     * Like the maxComputeWorkGroupSize(), the max size of
     * the local group is although provided as a 3D-array.
     * Note, that there is although a limitation on how
     * many invocations (x * y * z) there can be in total.
     *
     * @return max compute local invocations per group
     */
    Vector3i maxComputeLocalGroupSize();

    /**
     * Returns the total maximum of invocations per local
     * group.
     *
     * @return max local invocations per group
     */
    int maxComputeLocalInvocations();

    /**
     * Maximum shared memory per compute shader.
     *
     * @return max shared memory in bytes
     */
    int maxComputeSharedMemory();

    /**
     * Will dispatch a compute shader with the specified work
     * group size.
     *
     * If the specified ShaderResource does not link to a
     * Compute Shader, this method will throw a
     * GLShaderTypeException.
     *
     * @param computeShader compute shader
     * @param groupSize work group size
     * @throws GLComputeException compute shader exception. Thrown
     *          when an invalid group size was chosen.
     * @throws GLShaderTypeException shader type exception. Thrown
     *          when an invalid shader resource was provided.
     */
    void dispatchCompute(ShaderResource computeShader, Vector3i groupSize) throws GLComputeException,
            GLShaderTypeException;

    /**
     * Will dispatch the currently bound compute shader with the
     * specified work group size.
     *
     * If the currently bound shader is not a compute shader,
     * this method will throw a GLShaderTypeException.
     *
     * @param groupSize work group size
     * @throws GLComputeException compute shader exception. Thrown
     *          when an invalid group size was chosen.
     * @throws GLShaderTypeException shader type exception. Thrown
     *          when an invalid shader resource is bound.
     */
    void dispatchCompute(Vector3i groupSize) throws GLComputeException, GLShaderTypeException;

    /**
     * Will dispatch a compute shader with the specified work
     * group size.
     *
     * If the specified ShaderResource does not link to a
     * Compute Shader, this method will return a
     * GLShaderTypeException.
     * Take good precautions that the group size stored inside
     * of the group size buffer is not bigger than the allowed
     * maximum. Wrong group sizes may cause the program to crash
     * or even GPU-hardlocks!
     *
     * @param computeShader compute shader
     * @param buffer group size buffer
     * @param offset group size buffer offset
     * @throws GLShaderTypeException shader type exception. Thrown
     *          when an invalid shader resource was provided.
     */
    void dispatchComputeIndirect(ShaderResource computeShader, GlBufferObject buffer, long offset) throws
            GLShaderTypeException;

    /**
     * Will dispatch the currently bound compute shader with
     * the specified work group size.
     *
     * If the currently bound shader is not a compute shader,
     * this method will throw a GLShaderTypeException.
     * Take good precautions that the group size stored inside
     * of the group size buffer is not bigger than the allowed
     * maximum. Wrong group sizes may cause the program to crash
     * or even GPU-hardlocks!
     *
     * @param buffer group size buffer
     * @param offset group size buffer offset
     * @throws GLShaderTypeException shader type exception. Thrown
     *          when an invalid shader resource is bound.
     */
    void dispatchComputeIndirect(GlBufferObject buffer, long offset) throws
            GLShaderTypeException;

    /**
     * Will return the local size of the compute work group from
     * a compute shader.
     *
     * @param shaderResource compute shader
     * @return local work group size
     * @throws GLShaderTypeException Exception that get's thrown,
     *          if the specified shader resource does not contain
     *          compute shader code
     */
    Vector3i getLocalGroupSize(ShaderResource shaderResource) throws GLShaderTypeException;

    /**
     * Returns the storage block that is currently bound
     * to the binding index.
     *
     * @param bindingIndex the binding index
     * @return the shader block bound to the binding index
     */
    ShaderBlock getStorageBlock(int bindingIndex);

    /**
     * Returns the uniform block that is currently bound
     * to the binding index.
     *
     * @param bindingIndex the binding index
     * @return the shader block bound to the binding index
     */
    ShaderBlock getUniformBlock(int bindingIndex);

    /**
     * Will load a shader storage buffer object from the
     * associated resource.
     *
     * If the SSBO resource is currently not registered,
     * this method will return null.
     *
     * @param resource SSBO resource
     * @return SSBO
     */
    ShaderStorageBufferObject loadSSBO(SSBOResource resource);

    /**
     * Will get a shader storage buffer object from the
     * associated resource.
     *
     * If the SSBO is currently not registered, this method will
     * attempt to load the SSBO asynchronously. In this case the
     * initial method call will return null.
     * If the SSBO resource is already registered, this method+
     * will just return the associated SSBO.
     *
     * @param resource SSBO resource
     * @return SSBO
     */
    ShaderStorageBufferObject getSSBO(SSBOResource resource);

    /**
     * Will attempt to bind one block from the SSBO.
     *
     * If the current thread is not a render thread, this method
     * will throw an IllegalContextException.
     * The specified <code>index</code> is NOT the binding- or
     * block-index, but rather the internal index of the block
     * inside of the SSBO resource.
     *
     * @param resource SSBO resource
     * @param index internal index
     * @return bound shader block
     * @throws IllegalContextException get's thrown, if the
     *          current thread is not a gl render thread
     */
    ShaderBlock bindSSBO(SSBOResource resource, int index) throws IllegalContextException;

    /**
     * Will attempt to bind one block from the SSBO block to a
     * specific binding index.
     *
     * If the current thread is not a render thread, this method
     * will throw an IllegalContextException.
     * The specified <code>index</code> is NOT the binding- or
     * block-index, but rather the internal index of the block
     * inside of the SSBO resource.
     *
     * @param resource SSBO resource
     * @param index internal index
     * @param bindingIndex binding index
     * @return bound shader block
     * @throws IllegalContextException get's thrown, if the
     *          current thread is not a gl render thread
     */
    ShaderBlock bindSSBO(SSBOResource resource, int index, int bindingIndex) throws IllegalContextException;

    /**
     * Will delete the SSBO.
     *
     * If the current thread is not a gl render thread, this method
     * will attempt to delete the SSBO asynchronously.
     *
     * @param resource SSBO to delete
     */
    void deleteSSBO(SSBOResource resource);

    /**
     * Will load a shader uniform buffer object from the
     * associated resource.
     *
     * If the UBO resource is currently not registered,
     * this method will return null.
     *
     * @param resource UBO resource
     * @return UBO
     */
    ShaderUniformBufferObject loadUBO(UBOResource resource);

    /**
     * Will get a shader uniform buffer object from the
     * associated resource.
     *
     * If the UBO is currently not registered, this method will
     * attempt to load the UBO asynchronously. In this case the
     * initial method call will return null.
     * If the UBO resource is already registered, this method+
     * will just return the associated UBO.
     *
     * @param resource UBO resource
     * @return UBO
     */
    ShaderUniformBufferObject getUBO(UBOResource resource);

    /**
     * Will attempt to bind one block from the UBO.
     *
     * If the current thread is not a render thread, this method
     * will throw an IllegalContextException.
     * The specified <code>index</code> is NOT the binding- or
     * block-index, but rather the internal index of the block
     * inside of the UBO resource.
     *
     * @param resource UBO resource
     * @param index internal index
     * @return bound shader block
     * @throws IllegalContextException get's thrown, if the
     *          current thread is not a gl render thread
     */
    ShaderBlock bindUBO(UBOResource resource, int index);

    /**
     * Will attempt to bind one block from the UBO block to a
     * specific binding index.
     *
     * If the current thread is not a render thread, this method
     * will throw an IllegalContextException.
     * The specified <code>index</code> is NOT the binding- or
     * block-index, but rather the internal index of the block
     * inside of the UBO resource.
     *
     * @param resource UBO resource
     * @param index internal index
     * @param bindingIndex binding index
     * @return bound shader block
     * @throws IllegalContextException get's thrown, if the
     *          current thread is not a gl render thread
     */
    ShaderBlock bindUBO(UBOResource resource, int index, int bindingIndex);

    /**
     * Will delete the UBO.
     *
     * If the current thread is not a gl render thread, this method
     * will attempt to delete the UBO asynchronously.
     *
     * @param resource UBO to delete
     */
    void deleteUBO(UBOResource resource);
}
