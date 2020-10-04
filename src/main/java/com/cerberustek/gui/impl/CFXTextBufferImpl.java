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

package com.cerberustek.gui.impl;

import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.gui.CFXAlphabet;
import com.cerberustek.gui.CFXTextBuffer;

import java.nio.ByteBuffer;

public class CFXTextBufferImpl implements CFXTextBuffer {

    private final ByteBuffer buffer;
    final CFXAlphabet[] alphabets;
    final int[] charCounts;
    private final long[] cuts;
    private final Vector2i bounds;

    public CFXTextBufferImpl(ByteBuffer buffer, CFXAlphabet[] alphabets, long[] cuts, int[] charCounts, Vector2i bounds) {
        this.buffer = buffer;
        this.alphabets = alphabets;
        this.cuts = cuts;
        this.charCounts = charCounts;
        this.bounds = bounds;
    }

    @Override
    public ByteBuffer getBuffer() {
        return buffer;
    }

    @Override
    public CFXAlphabet getAlphabets(int index) {
        return alphabets[index];
    }

    @Override
    public CFXAlphabet[] alphabets() {
        return alphabets;
    }

    @Override
    public int getCharCount(int index) {
        return charCounts[index];
    }

    @Override
    public int[] charCounts() {
        return charCounts;
    }

    @Override
    public long[] cuts() {
        return cuts;
    }

    @Override
    public int size() {
        return alphabets.length;
    }

    @Override
    public int charCount() {
        int sum = 0;
        for (int count : charCounts)
            sum += count;
        return sum;
    }

    @Override
    public Vector2i getBounds() {
        return bounds;
    }
}
