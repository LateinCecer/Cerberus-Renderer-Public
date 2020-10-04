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

package com.cerberustek.material.impl;

import com.cerberustek.exceptions.IllegalContextException;
import com.cerberustek.resource.image.TextureResource;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.property.ShaderProperty;
import com.cerberustek.shader.property.ShaderPropertyMap;
import com.cerberustek.shader.property.SimpleShaderPropertyMap;
import com.cerberustek.shader.uniform.Uniform;
import com.cerberustek.material.Material;
import com.cerberustek.texture.Texture;
import com.cerberustek.texture.TextureBoard;
import com.cerberustek.texture.impl.TextureBufferBinding;

import java.util.Collection;
import java.util.HashMap;

public class BaseMaterial implements Material {

    private final HashMap<Integer, TextureBufferBinding> textures = new HashMap<>();

    private ShaderPropertyMap properties;
    private CerberusRenderer renderer;

    public BaseMaterial() {
        properties = new SimpleShaderPropertyMap();
    }

    @Override
    public int textureSize() {
        return textures.size();
    }

    @Override
    public TextureResource getTexture(int unit) {
        TextureBufferBinding binding = textures.get(unit);
        return binding == null ? null : binding.getTexture();
    }

    @Override
    public void setTexture(int unit, TextureResource texture, int bufferIndex) {
        TextureBufferBinding binding = textures.get(unit);
        if (binding != null) {
            if (!binding.getTexture().equals(texture) || binding.getBufferId() != bufferIndex)
                textures.replace(unit, binding, new TextureBufferBinding(texture, bufferIndex));
        }
        textures.put(unit, new TextureBufferBinding(texture, bufferIndex));
    }

    @Override
    public void addTexture(TextureResource resource) {
        TextureBoard textureBoard = getRenderer().getTextureBoard();

        Texture texture = textureBoard.getTexture(resource);
        if (texture == null)
            getRenderer().tryGLTask((t) -> {
                textureBoard.loadTexture(resource);
                addTexture(resource);
            });
        else {
            for (int i = 0; i < texture.length(); i++)
                setTexture(texture.getUnit(i), resource, i);
        }
    }

    @Override
    public void bindTextures() {
        TextureBoard textureBoard = getRenderer().getTextureBoard();
        textures.forEach((unit, binding) -> textureBoard.bindTexture(binding.getTexture(), binding.getBufferId(), unit));
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void bindProperties(Shader shader) {
        if (properties == null)
            return;

        if (!getRenderer().getWindow().isGlThread())
            throw new IllegalContextException();

        Collection<ShaderProperty> properties = this.properties.properties();
        for (ShaderProperty property : properties) {
            Uniform uniform = property.inforce(shader);
            if (uniform != null)
                uniform.update();
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void inforceProperties(Shader shader) {
        if (properties == null)
            return;

        Collection<ShaderProperty> properties = this.properties.properties();
        for (ShaderProperty property : properties)
            property.inforce(shader);
    }

    @Override
    public ShaderPropertyMap getProperties() {
        return properties;
    }

    @Override
    public void setProperties(ShaderPropertyMap doc) {
        this.properties = doc;
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }
}
