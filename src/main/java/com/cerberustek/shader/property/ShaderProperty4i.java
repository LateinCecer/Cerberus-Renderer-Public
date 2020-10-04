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
import com.cerberustek.data.impl.tags.Vector4iTag;
import com.cerberustek.exception.LoadFormatException;
import com.cerberustek.logic.math.Vector4i;
import com.cerberustek.shader.uniform.Uniform4i;
import org.jetbrains.annotations.NotNull;

public class ShaderProperty4i extends SimpleShaderProperty<Vector4i, Uniform4i> {

    public ShaderProperty4i(@NotNull String name) {
        super(name);
    }

    public ShaderProperty4i(@NotNull String name, @NotNull Vector4i value) {
        super(name, value);
    }

    @Override
    public @NotNull Class<Uniform4i> getUniformType() {
        return Uniform4i.class;
    }

    @Override
    public MetaData convert() {
        DocTag tag = new DocTag(getName());
        tag.insert(new StringTag("class", getUniformType().getName()));
        tag.insert(new Vector4iTag("value", get()));

        return tag;
    }

    @Override
    public void load(MetaData metaData) throws LoadFormatException {
        if (!(metaData instanceof DocTag))
            throw new LoadFormatException("Invalid data format");
        Vector4iTag tag = ((DocTag) metaData).extract("value", Vector4iTag.class);
        set(tag == null ? new Vector4i(0, 0, 0, 0) : tag.get());
    }
}
