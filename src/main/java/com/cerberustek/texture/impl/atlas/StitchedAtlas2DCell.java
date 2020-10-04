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

package com.cerberustek.texture.impl.atlas;

import com.cerberustek.resource.image.TextureResource;
import com.cerberustek.texture.StitchedAtlasCell;
import com.cerberustek.texture.TextureAtlas;

public class StitchedAtlas2DCell implements StitchedAtlasCell {

    private final TextureResource resource;
    private final int cellId;
    private final TextureAtlas atlas;

    public StitchedAtlas2DCell(TextureResource resource, int cellId, TextureAtlas atlas) {
        this.resource = resource;
        this.cellId = cellId;
        this.atlas = atlas;
    }

    @Override
    public TextureResource getTextureResource() {
        return resource;
    }

    @Override
    public int getCellId() {
        return cellId;
    }

    @Override
    public TextureAtlas atlas() {
        return atlas;
    }
}
