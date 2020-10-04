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
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.texture.ImageType;
import com.cerberustek.texture.Texture;
import com.cerberustek.texture.impl.TextureEmpty2D;

public class EmptyTexture2DResource implements TextureResource {

    private final Vector2i[] size;
    private final int[] units;
    private final ImageType[] types;

    public EmptyTexture2DResource(Vector2i[] sizes, int[] units, ImageType[] types) {
        if (sizes.length != units.length || sizes.length != types.length)
            throw new IllegalArgumentException("All parameters need to be of the same length!");

        this.size = sizes;
        this.units = units;
        this.types = types;
    }

    @Override
    public Texture load() {
        TextureEmpty2D texture = new TextureEmpty2D(size.length);
        texture.genTextures();
        texture.initTextures(units, size, types);
        return texture;
    }
}
