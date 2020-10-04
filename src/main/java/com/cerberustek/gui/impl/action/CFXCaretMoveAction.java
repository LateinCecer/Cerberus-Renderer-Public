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

public class CFXCaretMoveAction implements CFXDocAction {

    private final int first;
    private final int start;

    public CFXCaretMoveAction(int first, int start) {
        this.first = first;
        this.start = start;
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
        return -1;
    }

    @Override
    public int getEnd() {
        return -1;
    }

    @Override
    public CFXDocActionType getType() {
        return CFXDocActionType.MOVE;
    }

    @Override
    public int doAction(CFXDocument doc) {
        return 0;
    }

    @Override
    public int undoAction(CFXDocument doc) {
        return 0;
    }
}
