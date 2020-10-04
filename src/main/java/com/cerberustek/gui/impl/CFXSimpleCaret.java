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

import com.cerberustek.gui.CFXCaret;
import com.cerberustek.gui.CFXCaretStyle;

public class CFXSimpleCaret implements CFXCaret {

    private int first;
    private int start;
    private int last;
    private int end;
    private CFXCaretStyle style;

    public CFXSimpleCaret(int first, int start) {
        this.first = first;
        this.start = start;
        this.last = -1;
        this.end = -1;
        this.style = CFXCaretStyle.DEFAULT;
    }

    @Override
    public int getFirst() {
        return first;
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int getLast() {
        return last;
    }

    @Override
    public int getEnd() {
        return end;
    }

    public CFXCaret setFirst(int first) {
        this.first = first;
        return this;
    }

    public CFXCaret setStart(int start) {
        this.start = start;
        return this;
    }

    public CFXCaret setLast(int last) {
        this.last = last;
        return this;
    }

    public CFXCaret setEnd(int end) {
        this.end = end;
        return this;
    }

    public void setStyle(CFXCaretStyle style) {
        this.style = style;
    }

    @Override
    public CFXCaretStyle getStyle() {
        return style;
    }
}
