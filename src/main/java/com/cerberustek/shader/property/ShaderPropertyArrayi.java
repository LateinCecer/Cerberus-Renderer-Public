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
import com.cerberustek.data.impl.elements.IntElement;
import com.cerberustek.data.impl.tags.DocTag;
import com.cerberustek.data.impl.tags.SpecificArrayTag;
import com.cerberustek.data.impl.tags.StringTag;
import com.cerberustek.exception.LoadFormatException;
import com.cerberustek.shader.uniform.UniformArrayi;
import org.jetbrains.annotations.NotNull;

public class ShaderPropertyArrayi extends SimpleShaderProperty<int[], UniformArrayi> {

    public ShaderPropertyArrayi(@NotNull String name) {
        super(name);
    }

    public ShaderPropertyArrayi(@NotNull String name, int @NotNull [] value) {
        super(name, value);
    }

    @Override
    public @NotNull Class<UniformArrayi> getUniformType() {
        return UniformArrayi.class;
    }

    @Override
    public MetaData convert() {
        DocTag tag = new DocTag(getName());
        tag.insert(new StringTag("class", getUniformType().getName()));

        IntElement[] data = new IntElement[get().length];
        for (int i = 0; i < data.length; i++)
            data[i] = new IntElement(get()[i]);
        tag.insert(new SpecificArrayTag<>("value", IntElement.class, data));

        return tag;
    }

    @Override
    public void load(MetaData metaData) throws LoadFormatException {
        if (!(metaData instanceof DocTag))
            throw new LoadFormatException("Invalid data format");
        @SuppressWarnings("unchecked") SpecificArrayTag<IntElement> array
                = ((DocTag) metaData).extract("value", SpecificArrayTag.class);
        if (array == null)
            set(new int[]{});
        else {
            int[] data = new int[array.length()];
            for (int i = 0; i < data.length; i++)
                data[0] = array.get(i).get();
            set(data);
        }
    }
}
