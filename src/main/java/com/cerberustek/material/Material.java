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

package com.cerberustek.material;

import com.cerberustek.exceptions.IllegalContextException;
import com.cerberustek.resource.image.TextureResource;
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.property.ShaderPropertyMap;

public interface Material {

    /**
     * Returns the amount of textures stored in the material.
     * @return amount of textures
     */
    int textureSize();

    /**
     * Returns the texture for the texture unit.
     *
     * If one textures manages multiple texture
     * buffers, the same texture may be returned
     * for different texture units.
     *
     * @param unit texture unit
     * @return texture
     */
    TextureResource getTexture(int unit);

    /**
     * Sets the texture buffer a texture unit.
     * @param unit texture unit
     * @param texture texture
     * @param bufferIndex texture buffer index
     */
    void setTexture(int unit, TextureResource texture, int bufferIndex);

    /**
     * Adds the texture to the material.
     *
     * This method will use the default unit-texture-buffer
     * matchup in the texture to figure out which texture
     * buffer inside the texture to add should go with
     * which texture unit.
     * @param texture texture unit
     */
    void addTexture(TextureResource texture);

    /**
     * Will bind the textures to the according texture units
     */
    void bindTextures();

    /**
     * Will update the uniforms of the shader.
     *
     * The uniform values will be updated on the CPU level
     * and on the GPU level. If the current thread is a render
     * thread, this method will throw a GL context exception.
     *
     * @param shader shader to update
     * @throws IllegalContextException Exception that gets
     *          thrown, if the thread is not a gl render
     *          thread
     */
    void bindProperties(Shader shader) throws IllegalContextException;

    /**
     * Will update the uniforms of the shader only on
     * the cpu level.
     *
     * Just like bind(Shader shader), but this method will
     * not update the GPU buffers.
     *
     * @param shader shader to update
     */
    void inforceProperties(Shader shader);

    /**
     * Returns the materials properties.
     * @return properties
     */
    ShaderPropertyMap getProperties();

    /**
     * Sets the materials property map.
     * @param doc properties
     */
    void setProperties(ShaderPropertyMap doc);
}
