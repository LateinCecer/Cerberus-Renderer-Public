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
import com.cerberustek.data.impl.tags.IntTag;
import com.cerberustek.data.impl.tags.StringTag;
import com.cerberustek.exception.LoadFormatException;
import com.cerberustek.shader.uniform.Uniform1i;
import org.jetbrains.annotations.NotNull;

public class ShaderProperty1i extends SimpleShaderProperty<Integer, Uniform1i> {

    public ShaderProperty1i(@NotNull String name) {
        super(name);
    }

    public ShaderProperty1i(@NotNull String name, @NotNull Integer value) {
        super(name, value);
    }

    @Override
    @NotNull
    public Class<Uniform1i> getUniformType() {
        return Uniform1i.class;
    }

    @Override
    public MetaData convert() {
        DocTag out = new DocTag(getName());
        out.insert(new StringTag("class", getUniformType().getName()));
        out.insert(new IntTag("value", get()));

        return out;
    }

    @Override
    public void load(MetaData metaData) throws LoadFormatException {
        if (!(metaData instanceof DocTag))
            throw new LoadFormatException("Invalid data format");
        Integer tag = ((DocTag) metaData).valueInt("value");
        set(tag == null ? 0 : tag);
    }
}
