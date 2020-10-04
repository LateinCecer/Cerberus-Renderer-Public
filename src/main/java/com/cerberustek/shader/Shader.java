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

import com.cerberustek.*;
import com.cerberustek.events.*;
import com.cerberustek.exceptions.GLUnknownBufferBlockException;
import com.cerberustek.exceptions.GLUnknownUniformBlockException;
import com.cerberustek.exceptions.GLUnknownUniformException;
import com.cerberustek.geometry.GeometryBoard;
import com.cerberustek.resource.shader.ShaderCodeResource;
import com.cerberustek.shader.code.ShaderCodeLoader;
import com.cerberustek.shader.uniform.Uniform;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL44.*;

@SuppressWarnings("rawtypes")
public abstract class Shader implements Updatable, Destroyable {

    /** The uniform map */
    private final Map<String, Uniform> uniformMap = new HashMap<>();
    /** The active shader attributes */
    private final GLSLType[] activeAttributes = new GLSLType[GeometryBoard.MAX_VERTEX_ATTRIBUTES];
    /** The shader types of the compiled programs of this shader */
    private final HashSet<ShaderType> attachedTypes = new HashSet<>();
    /** The Gl-Id of the shader program */
    private int programId;

    /**
     * Allocates the shader program on the vrm and sets the program id
     */
    public void genProgram() {
        programId = glCreateProgram();

        if (programId == -1)
            CerberusRegistry.getInstance().getService(CerberusEvent.class).executeFullEIF(new GLShaderError(this));
    }

    /**
     * Returns the Gl-Program id
     * @return program id
     */
    public int getProgramId() {
        return programId;
    }

    /**
     * Will bind the shader to the current Gl-RenderPipeline.
     */
    public void bind() {
        glUseProgram(programId);
    }

    /**
     * Will update the specific uniform.
     *
     * @param uniform uniform to update
     * @param <T> Data type of Uniform
     * @return The updated Uniform
     */
    public <T> Uniform<T> updateUniform(Uniform<T> uniform) {
        return uniform.update();
    }

    /**
     * Returns a uniform by it's name.
     *
     * If the Uniform is not registered to the shader program, this
     * method will return null.
     *
     * @param name Name of the uniform to retrieve
     * @return Uniform by that name
     */
    public Uniform getUniform(String name) {
        return uniformMap.get(name);
    }

    public <T extends Uniform> T getUniform(String name, Class<T> clazz) {
        Uniform u = getUniform(name);
        if (clazz.isInstance(u))
            return clazz.cast(u);
        return null;
    }

    /**
     * Returns if the shader contains a certain uniform.
     * @param name uniform name to check on
     * @return has it?
     */
    public boolean hasUniform(String name) {
        return uniformMap.containsKey(name);
    }

    /**
     * Returns the Gl-Uniform id of the uniform with the specified name.
     *
     * If there is no uniform with the before-mentioned name, this method
     * will throw a GLUnknownUniformException.
     *
     * @param name Name of the Uniform to look for
     * @return The id of the uniform
     * @throws GLUnknownUniformException Exception when uniform is not found.
     */
    public int genUniformId(String name) throws GLUnknownUniformException {
        int out = glGetUniformLocation(programId, name);

        if (out == 0xFFFFFFFF) {
            CerberusRegistry.getInstance().getService(CerberusEvent.class).executeFullEIF(
                    new GLShaderUniformError(this, name));
            throw new GLUnknownUniformException(name, this);
        }
        return out;
    }

    /**
     * Returns the Gl-Uniform Block id of the uniform block with the
     * specified name.
     *
     * If there is no uniform block with that name, this method will
     * throw a GLUnknownUniformBlockException.
     *
     * @param name uniform block name
     * @return uniform block index
     * @throws GLUnknownUniformBlockException get's thrown if the
     *          uniform block is not registered inside the
     *          shader program
     */
    public int genUniformBlockId(String name) throws GLUnknownUniformBlockException {
        int out = glGetUniformBlockIndex(programId, name);

        if (out == GL_INVALID_VALUE) {
            CerberusRegistry.getInstance().getService(CerberusEvent.class).executeFullEIF(
                    new ExceptionEvent(CerberusRenderer.class, new GLUnknownUniformBlockException(this, name)));
            throw new GLUnknownUniformBlockException(this, name);
        }
        return out;
    }

    /**
     * Returns the Gl-Buffer Block id of the uniform block with the
     * specified name.
     *
     * If there is no buffer block with that name, this method will
     * throw a GLUnknownBufferBlockException.
     *
     * @param name buffer block name
     * @return buffer block index
     * @throws GLUnknownBufferBlockException get's thrown if the
     *          buffer block is not registered inside the
     *          shader program
     */
    public int genStorageBlockId(String name) throws GLUnknownBufferBlockException {
        int out = glGetProgramResourceIndex(programId, GL_SHADER_STORAGE_BLOCK, name);

        if (out == GL_INVALID_VALUE) {
            CerberusRegistry.getInstance().getService(CerberusEvent.class).executeFullEIF(
                    new ExceptionEvent(CerberusRenderer.class, new GLUnknownBufferBlockException(this, name)));
            throw new GLUnknownBufferBlockException(this, name);
        }
        return out;
    }

    /**
     * Will bind the storage block with the name <code>name</code>
     * to the appropriate binding index.
     *
     * If there is no storage block with the specified name,
     * this method will throw an error.
     *
     * @param name storage block name
     * @param bindingIndex storage block binding id
     * @throws GLUnknownBufferBlockException get's thrown, if no
     *          storage block could be found for the specified name
     *          within the shader program
     */
    public void bindStorageBlock(String name, int bindingIndex) throws GLUnknownBufferBlockException {
        bindStorageBlock(genStorageBlockId(name), bindingIndex);
    }

    /**
     * Will bind the uniform block with the name <code>name</code>
     * to the appropriate binding index.
     *
     * If there is no storage block with the specified name,
     * this method will throw an error.
     *
     * @param name uniform block name
     * @param bindingIndex uniform block binding id
     * @throws GLUnknownUniformBlockException get's thrown, if no
     *          storage block could be found for the specified name
     *          within the shader program
     */
    public void bindUniformBlock(String name, int bindingIndex) throws GLUnknownUniformBlockException {
        bindUniformBlock(genUniformBlockId(name), bindingIndex);
    }

    /**
     * Will bind the uniform block with the index <code>blockIndex</code>
     * to the appropriate binding index.
     *
     * @param blockIndex uniform block index
     * @param bindingIndex uniform binding index
     */
    public void bindUniformBlock(int blockIndex, int bindingIndex) {
        glUniformBlockBinding(programId, blockIndex, bindingIndex);
    }

    /**
     * Will bind the buffer block with the index <code>blockIndex</code>
     * to the appropriate binding index.
     *
     * @param blockIndex buffer block index
     * @param bindingIndex buffer binding index
     */
    public void bindStorageBlock(int blockIndex, int bindingIndex) {
        glShaderStorageBlockBinding(programId, blockIndex, bindingIndex);
    }

    /**
     * This method will add a uniform to the shader.
     *
     * If the uniform is not known to the shader code or the uniform name does
     * not match the uniform id, this method will throw a
     * GLUnknownUniformException.
     *
     * @param uniform Uniform to add the the shader.
     * @throws GLUnknownUniformException Exception when a uniform with that
     *              id is not known to the shader
     */
    public void addUniform(Uniform uniform) throws GLUnknownUniformException {
        if (this.genUniformId(uniform.getName()) != uniform.getId())
            throw new GLUnknownUniformException(uniform.getName(), this);

        if (!uniformMap.containsKey(uniform.getName()))
            uniformMap.put(uniform.getName(), uniform);
    }

    /**
     * Removes a uniform from the shader.
     * @param uniform uniform to remove
     */
    public void removeUniform(Uniform uniform) {
        uniformMap.remove(uniform.getName());
    }

    /**
     * Inserts a uniform.
     *
     * If a uniform by that name is already known to the shader, this method
     * will replace the uniform. Otherwise it will add the uniform to the system.
     * If the uniform is not known to the shader code or the uniform id does not
     * match it's name, this method will throw a GLUnknownUniformException.
     *
     * @param uniform uniform to insert
     * @throws GLUnknownUniformException Exception thrown, when uniform unknown
     */
    public void insertUniform(Uniform uniform) throws GLUnknownUniformException {
        if (this.genUniformId(uniform.getName()) != uniform.getId())
            throw new GLUnknownUniformException(uniform.getName(), this);

        if (uniformMap.containsKey(uniform.getName()))
            uniformMap.replace(uniform.getName(), uniform);
        else
            uniformMap.put(uniform.getName(), uniform);
    }

    /**
     * This method will attempt to compile the shader code.
     * @return compilation successful
     */
    public boolean compile() {
        glLinkProgram(programId);

        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            CerberusRegistry.getInstance().getService(CerberusEvent.class).executeFullEIF(new GLShaderLinkError(
                    this, glGetProgramInfoLog(programId)));
            return false;
        }

        glValidateProgram(programId);

        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            CerberusRegistry.getInstance().getService(CerberusEvent.class).executeFullEIF(new GLShaderValidationError(
                    this, glGetProgramInfoLog(programId)));
            return false;
        }

        // get active attributes
        IntBuffer count = BufferUtils.createIntBuffer(1);
        IntBuffer length = BufferUtils.createIntBuffer(1);
        IntBuffer size = BufferUtils.createIntBuffer(1);
        IntBuffer type = BufferUtils.createIntBuffer(1);
        ByteBuffer name = BufferUtils.createByteBuffer(32);

        glGetProgramiv(programId, GL_ACTIVE_ATTRIBUTES, count);
        for (int i = 0; i < count.get(0); i++) {
            glGetActiveAttrib(programId, i, length, size, type, name);
            name.rewind();

            activeAttributes[glGetAttribLocation(programId, name)] = GLSLType.valueOf(type.get(0));

            length.clear();
            size.clear();
            type.clear();
            name.clear();
        }

        return true;
    }

    /**
     * Returns the GLSL Type of the attribute at a specified
     * attribute index.
     *
     * If the attribute is not active in the shader, this
     * method will return null.
     *
     * @param attributeIndex attribute index
     * @return attribute type
     */
    public GLSLType getAttribute(int attributeIndex) {
        return activeAttributes[attributeIndex];
    }

    /**
     * Returns if the attribute with the specified attribute
     * index is active.
     * @param attributeIndex attribute index
     * @return is the attribute active
     */
    public boolean isAttributeActive(int attributeIndex) {
        return activeAttributes[attributeIndex] != null;
    }

    /**
     * This method will add a shader program (Vertex-, Fragment-, Geometry-,
     * Tessellation-Shader) to the shader.
     *
     * If an attachment with that shader type is already present, this will
     * return false.
     *
     * @param resource Shader code resource to load the shader code from
     * @param type type of the shader to load
     * @return success
     */
    public boolean addProgram(ShaderCodeResource resource, ShaderType type) {
        if (attachedTypes.contains(type))
            return false; // program with that type is already attached
        int shaderId = glCreateShader(type.getGlType());

        if (shaderId == 0) {
            CerberusRegistry.getInstance().getService(CerberusEvent.class).executeFullEIF(new GLShaderError(this));
            return false;
        }

        ShaderCodeLoader codeLoader = CerberusRegistry.getInstance().getService(CerberusRenderer.class).
                getShaderBoard().getShaderCodeLoader();

        try {
            glShaderSource(shaderId, codeLoader.loadCode(resource));
            glCompileShader(shaderId);
        } catch (IOException e) {
            CerberusRegistry.getInstance().getService(CerberusEvent.class).executeFullEIF(new GLShaderCompileError(
                    this, glGetShaderInfoLog(shaderId)));
            return false;
        }

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            CerberusRegistry.getInstance().getService(CerberusEvent.class).executeFullEIF(new GLShaderCompileError(
                    this, glGetShaderInfoLog(shaderId)));
            return false;
        }

        glAttachShader(programId, shaderId);
        attachedTypes.add(type);
        return true;
    }

    /**
     * Returns true, if the shader program has an attachment
     * of the specified shader type.
     *
     * @param type shader type
     * @return has program attachment
     */
    public boolean hasAttachment(ShaderType type) {
        return attachedTypes.contains(type);
    }

    /**
     * Returns a linked copy of the attached shader types.
     * @return shader types
     */
    public Collection<ShaderType> getAttachedTypes() {
        return new LinkedHashSet<>(attachedTypes);
    }

    @Override
    public void destroy() {
        glDeleteProgram(programId);
    }

    @Override
    public void update(double delta) {
        uniformMap.values().forEach(Uniform::update);
    }
}
