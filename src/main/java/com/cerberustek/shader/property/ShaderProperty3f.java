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
import com.cerberustek.data.impl.tags.Vector3fTag;
import com.cerberustek.exception.LoadFormatException;
import com.cerberustek.logic.math.Vector3f;
import com.cerberustek.shader.uniform.Uniform3f;
import org.jetbrains.annotations.NotNull;

public class ShaderProperty3f extends SimpleShaderProperty<Vector3f, Uniform3f> {

    public ShaderProperty3f(@NotNull String name) {
        super(name);
    }

    public ShaderProperty3f(@NotNull String name, @NotNull Vector3f value) {
        super(name, value);
    }

    @Override
    @NotNull
    public Class<Uniform3f> getUniformType() {
        return Uniform3f.class;
    }

    @Override
    public MetaData convert() {
        DocTag out = new DocTag(getName());
        out.insert(new StringTag("class", getUniformType().getName()));
        out.insert(new Vector3fTag("value", get()));

        return out;
    }

    @Override
    public void load(MetaData metaData) throws LoadFormatException {
        if (!(metaData instanceof DocTag))
            throw new LoadFormatException("Invalid data format");
        Vector3fTag tag = ((DocTag) metaData).extract("value", Vector3fTag.class);
        set(tag == null ? new Vector3f(0, 0, 0) : tag.get());
    }
}
