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

package com.cerberustek.shader.code;

import com.cerberustek.Destroyable;
import com.cerberustek.resource.shader.ShaderCodeResource;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * The shader code resource loader loads shader code from the heap and
 * distributes it to the shaders using it.
 */
public interface ShaderCodeLoader extends Destroyable {

    /**
     * Will remove loaded shader code from the heap.
     * @param resource resource of the shader code
     */
    void removeCode(@NotNull ShaderCodeResource resource);

    /**
     * Returns rather or not the code loader has the shader code
     * with the specified resource loaded.
     * @param resource resource of the shader code
     * @return has code
     */
    boolean hasCode(@NotNull ShaderCodeResource resource);

    /**
     * Returns the shader code specified by the resource.
     *
     * If the shader code in question is currently not loaded, this method will
     * load the shader code from the resource.
     *
     * @param resource resource of the shader code
     * @return The shader code
     * @throws IOException Loading exception
     */
    String loadCode(@NotNull ShaderCodeResource resource) throws IOException;

    /**
     * Sets the main shader folder path.
     * @param path shader folder
     */
    void setFolder(@NotNull String path);

    /**
     * Returns if the shader code linked to the resource can be found
     * either in heap, or in the system folder.
     * @param resource resource to look for
     * @return does exist?
     */
    boolean exists(@NotNull ShaderCodeResource resource);

    /**
     * Returns the main shader folder.
     * @return shader folder path
     */
    File getFolder();

    /**
     * Creates a shader code resource based on the binary name of the
     * shader code.
     * @param name binary name of the shader code
     * @return Shader code resource of the shader code
     */
    ShaderCodeResource resourceFromName(@NotNull String name);
}
