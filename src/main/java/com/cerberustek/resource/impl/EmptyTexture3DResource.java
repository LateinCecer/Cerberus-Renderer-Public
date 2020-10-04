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

package com.cerberustek.resource.impl;

import com.cerberustek.resource.image.TextureResource;
import com.cerberustek.logic.math.Vector3i;
import com.cerberustek.texture.ImageType;
import com.cerberustek.texture.Texture;
import com.cerberustek.texture.impl.TextureEmpty3D;

public class EmptyTexture3DResource implements TextureResource {

    private final Vector3i[] sizes;
    private final int[] units;
    private final ImageType[] types;

    public EmptyTexture3DResource(Vector3i[] sizes, int[] units, ImageType[] types) {
        if (sizes.length != units.length || sizes.length != types.length)
            throw new IllegalArgumentException("All parameters need to be of the same length!");

        this.sizes = sizes;
        this.units = units;
        this.types = types;
    }

    @Override
    public Texture load() {
        TextureEmpty3D texture = new TextureEmpty3D(sizes.length);
        texture.genTextures();
        texture.initTextures(units, sizes, types);
        return texture;
    }
}
