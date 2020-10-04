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
import com.cerberustek.gui.CFXFontRenderer;

public class CFXMoveAction implements CFXDocAction {

    private final int firstOne;
    private final int startOne;
    private final int lastOne;
    private final int endOne;

    private final int firstTwo;
    private final int startTwo;
    private int lastTwo;
    private int endTwo;

    public CFXMoveAction(int firstOne, int startOne, int lastOne, int endOne,
                        int firstTwo, int startTwo) {

        this.firstOne = firstOne;
        this.startOne = startOne;
        this.lastOne = lastOne;
        this.endOne = endOne;

        this.firstTwo = firstTwo;
        this.startTwo = startTwo;
    }

    @Override
    public int getFirst() {
        return firstOne;
    }

    @Override
    public int getStart() {
        return startOne;
    }

    @Override
    public int getLast() {
        return lastOne;
    }

    @Override
    public int getEnd() {
        return endOne;
    }

    public void expandForwards(char c) {
        if (c == CFXFontRenderer.ESCAPE_NEW_LINE) {
            lastTwo++;
            endTwo = 0;
        } else
            endTwo++;
    }

    @Override
    public CFXDocActionType getType() {
        return CFXDocActionType.SET;
    }

    @Override
    public int doAction(CFXDocument doc) {
        CharSequence original = doc.get(firstOne, startOne, lastOne, endOne);

        doc.remove(firstOne, startOne, lastOne, endOne);
        this.lastTwo = firstTwo;
        this.endTwo = startTwo;

        for (int i = 0; i < original.length(); i++)
            expandForwards(original.charAt(i));

        return doc.insert(original, firstTwo, startTwo);
    }

    @Override
    public int undoAction(CFXDocument doc) {
        CharSequence original = doc.get(firstTwo, startTwo, lastTwo, endTwo);

        doc.remove(firstTwo, startTwo, lastTwo, endTwo);
        return doc.insert(original, firstOne, startOne);
    }
}
