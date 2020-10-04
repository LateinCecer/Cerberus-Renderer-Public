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

package com.cerberustek.shader.property;

import com.cerberustek.data.MetaData;
import com.cerberustek.data.impl.tags.DocTag;
import com.cerberustek.data.impl.tags.StringTag;
import com.cerberustek.data.impl.tags.Vector4fTag;
import com.cerberustek.exception.LoadFormatException;
import com.cerberustek.logic.math.Vector4f;
import com.cerberustek.shader.uniform.Uniform4f;
import org.jetbrains.annotations.NotNull;

public class ShaderProperty4f extends SimpleShaderProperty<Vector4f, Uniform4f> {

    public ShaderProperty4f(@NotNull String name) {
        super(name);
    }

    public ShaderProperty4f(@NotNull String name, @NotNull Vector4f value) {
        super(name, value);
    }

    @Override
    public @NotNull Class<Uniform4f> getUniformType() {
        return Uniform4f.class;
    }

    @Override
    public MetaData convert() {
        DocTag tag = new DocTag(getName());
        tag.insert(new StringTag("class", getUniformType().getName()));
        tag.insert(new Vector4fTag("value", get()));

        return tag;
    }

    @Override
    public void load(MetaData metaData) throws LoadFormatException {
        if (!(metaData instanceof DocTag))
            throw new LoadFormatException("Invalid data format");
        Vector4fTag tag = ((DocTag) metaData).extract("value", Vector4fTag.class);
        set(tag == null ? new Vector4f(0, 0, 0, 0) : tag.get());
    }
}
