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

package com.cerberustek.shader.ssbo.impl.struct;

import com.cerberustek.shader.ssbo.StructArray;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

public abstract class DynamicStructArray<T> implements StructArray<T> {

    protected final ArrayList<T> data = new ArrayList<>();
    protected long offset;

    public DynamicStructArray() {}

    public DynamicStructArray(T[] data) {
        this.data.addAll(Arrays.asList(data));
    }

    @Override
    public T get(int index) {
        return data.get(index);
    }

    @Override
    public void set(int index, T value) {
        data.set(index, value);
    }

    public void add(T value) {
        data.add(value);
    }

    public void remove(int index) {
        data.remove(index);
    }

    public void addAll(Collection<T> collection) {
        data.addAll(collection);
    }

    public void insert(int index, T value) {
        data.add(index, value);
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public long byteOffset() {
        return offset;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return data.iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        data.forEach(action);
    }
}
