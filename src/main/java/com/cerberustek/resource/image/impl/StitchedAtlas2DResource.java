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

package com.cerberustek.resource.image.impl;

import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.logic.math.Vector3i;
import com.cerberustek.pipeline.impl.notes.SceneNote;
import com.cerberustek.resource.image.TextureResource;
import com.cerberustek.texture.Attachment;
import com.cerberustek.texture.AttachmentType;
import com.cerberustek.texture.ImageType;
import com.cerberustek.texture.Texture;
import com.cerberustek.texture.impl.SimpleAttachment;
import com.cerberustek.texture.impl.atlas.StitchedTextureAtlas2D;

import java.util.Arrays;

public class StitchedAtlas2DResource implements TextureResource {

    private final Vector2i dim;
    private final Vector2i cellSize;
    private final ImageType[] types;
    private final int[] units;
    private final Attachment[] attachments;

    public StitchedAtlas2DResource(Vector2i dimension, Vector2i cellSize, ImageType type) {
        this.dim = dimension;
        this.cellSize = cellSize;

        this.units = new int[] {
                SceneNote.COLOR,
                SceneNote.NORMAL,
                SceneNote.SPECULAR,
                SceneNote.EMISSION,
                SceneNote.METALLIC,
                SceneNote.DISPLACEMENT
        };

        this.types = new ImageType[units.length];
        this.attachments = new Attachment[] {
                new SimpleAttachment(0, AttachmentType.COLOR_00),
                new SimpleAttachment(1, AttachmentType.COLOR_01),
                new SimpleAttachment(2, AttachmentType.COLOR_02),
                new SimpleAttachment(3, AttachmentType.COLOR_03),
                new SimpleAttachment(4, AttachmentType.COLOR_04),
                new SimpleAttachment(5, AttachmentType.COLOR_05)
        };
        Arrays.fill(types, type);
    }

    @Override
    public Texture load() {
        return new StitchedTextureAtlas2D(new Vector3i(dim, 1), new Vector3i(cellSize, 1),
                units, types, attachments);
    }
}
