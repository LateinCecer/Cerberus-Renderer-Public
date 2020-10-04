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
import com.cerberustek.data.impl.tags.LongTag;
import com.cerberustek.data.impl.tags.StringTag;
import com.cerberustek.exception.LoadFormatException;
import com.cerberustek.shader.uniform.Uniformi64NV;
import org.jetbrains.annotations.NotNull;

public class ShaderPropertyi64NV extends SimpleShaderProperty<Long, Uniformi64NV> {

    protected ShaderPropertyi64NV(@NotNull String name) {
        super(name);
    }

    protected ShaderPropertyi64NV(@NotNull String name, @NotNull Long value) {
        super(name, value);
    }

    @Override
    public @NotNull Class<Uniformi64NV> getUniformType() {
        return Uniformi64NV.class;
    }

    @Override
    public MetaData convert() {
        DocTag tag = new DocTag(getName());
        tag.insert(new StringTag("class", getUniformType().getName()));
        tag.insert(new LongTag("value", get()));

        return tag;
    }

    @Override
    public void load(MetaData metaData) throws LoadFormatException {
        if (!(metaData instanceof DocTag))
            throw new LoadFormatException("Invalid data format");
        LongTag tag = ((DocTag) metaData).extract("value", LongTag.class);
        set(tag == null ? 0L : tag.get());
    }
}
