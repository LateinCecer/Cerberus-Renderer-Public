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

package com.cerberustek.gui.impl.action;

import com.cerberustek.gui.CFXDocAction;
import com.cerberustek.gui.CFXDocActionType;
import com.cerberustek.gui.CFXDocument;

public class CFXRemoveAction implements CFXDocAction {

    private final int first;
    private final int start;
    private final int last;
    private final int end;

    private CharSequence data;

    public CFXRemoveAction(int first, int start, int last, int end) {
        this.first = first;
        this.start = start;
        this.last = last;
        this.end = end;

        data = "";
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

    @Override
    public CFXDocActionType getType() {
        return CFXDocActionType.REMOVE;
    }

    @Override
    public int doAction(CFXDocument doc) {
        data = doc.get(first, start, last, end);
        return doc.remove(first, start, last, end);
    }

    @Override
    public int undoAction(CFXDocument doc) {
        return doc.insert(data, first, start);
    }
}
