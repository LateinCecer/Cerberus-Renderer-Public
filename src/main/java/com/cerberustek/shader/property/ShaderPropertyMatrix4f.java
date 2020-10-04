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
import com.cerberustek.data.impl.tags.Matrix4fTag;
import com.cerberustek.data.impl.tags.StringTag;
import com.cerberustek.exception.LoadFormatException;
import com.cerberustek.logic.math.Matrix4f;
import com.cerberustek.shader.uniform.UniformMatrix4f;
import org.jetbrains.annotations.NotNull;

public class ShaderPropertyMatrix4f extends SimpleShaderProperty<Matrix4f, UniformMatrix4f> {

    public ShaderPropertyMatrix4f(@NotNull String name) {
        super(name);
    }

    public ShaderPropertyMatrix4f(@NotNull String name, @NotNull Matrix4f value) {
        super(name, value);
    }

    @Override
    public @NotNull Class<UniformMatrix4f> getUniformType() {
        return UniformMatrix4f.class;
    }

    @Override
    public MetaData convert() {
        DocTag tag = new DocTag(getName());
        tag.insert(new StringTag("class", getUniformType().getName()));
        tag.insert(new Matrix4fTag("value", get()));

        return tag;
    }

    @Override
    public void load(MetaData metaData) throws LoadFormatException {
        if (!(metaData instanceof DocTag))
            throw new LoadFormatException("Invalid data format");
        Matrix4fTag tag = ((DocTag) metaData).extract("value", Matrix4fTag.class);
        set(tag == null ? new Matrix4f().initIdentity() : tag.get());
    }
}
