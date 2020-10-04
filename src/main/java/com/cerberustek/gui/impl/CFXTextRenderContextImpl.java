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
import com.cerberustek.resource.shader.SSBOResource;
import com.cerberustek.gui.CFXAlphabet;
import com.cerberustek.gui.CFXTextRenderContext;

public class CFXTextRenderContextImpl implements CFXTextRenderContext {

    private final CFXAlphabet[] alphabets;
    private final int[] charCounts;
    private final SSBOResource ssbo;
    private final Vector2i bounds;

    public CFXTextRenderContextImpl(CFXAlphabet[] alphabets, SSBOResource ssbo, int[] charCounts, Vector2i bounds) {
        this.alphabets = alphabets;
        this.ssbo = ssbo;
        this.charCounts = charCounts;
        this.bounds = bounds;
    }

    @Override
    public CFXAlphabet getAlphabet(int index) {
        return alphabets[index];
    }

    @Override
    public int getCharCount(int index) {
        return charCounts[index];
    }

    @Override
    public SSBOResource getBufferResource() {
        return ssbo;
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
