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

import java.util.Iterator;
import java.util.function.Consumer;

public abstract class StaticStructArray<T> implements StructArray<T> {

    protected final T[] data;
    protected long offset;

    public StaticStructArray(T[] data) {
        this.data = data;
    }

    @Override
    public T get(int index) {
        if (index >= data.length)
            throw new ArrayIndexOutOfBoundsException();
        return data[index];
    }

    @Override
    public void set(int index, T value) {
        if (index >= data.length)
            throw new ArrayIndexOutOfBoundsException();
        data[index] = value;
    }

    @Override
    public int size() {
        return data.length;
    }

    @Override
    public long byteOffset() {
        return offset;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new StaticStructIterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        for (T datum : data) action.accept(datum);
    }

    private class StaticStructIterator implements Iterator<T> {

        private int index;

        private StaticStructIterator() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return StaticStructArray.this.size() > index;
        }

        @Override
        public T next() {
            return StaticStructArray.this.data[index++];
        }
    }
}
