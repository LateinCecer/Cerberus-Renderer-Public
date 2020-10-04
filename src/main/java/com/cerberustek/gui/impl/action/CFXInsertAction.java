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

public class CFXInsertAction implements CFXDocAction {

    private final int first;
    private int start;
    private int last;
    private int end;

    private String data;

    public CFXInsertAction(int first, String data) {
        this(first, -1, -1, -1, data);
    }

    public CFXInsertAction(int first, int start, String data) {
        this(first, start, -1, -1, data);
    }

    public CFXInsertAction(int first, int start, int lastLimit, int endLimit, String data) {
        this.first = first;
        this.start = start;
        this.last = first;
        this.end = start;
        this.data = "";

        char[] chars = data.toCharArray();
        if (lastLimit != -1) {
            if (endLimit != -1) {
                for (char c : chars) {
                    if (last > lastLimit)
                        break;
                    if (end > endLimit)
                        break;
                    expandForwards(c);
                }
            } else {
                for (char c : chars) {
                    if (last > lastLimit)
                        break;
                    expandForwards(c);
                }
            }
        } else {
            for (char c : chars)
                expandForwards(c);
        }
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

    public void expandForwards(char c) {
        if (c == CFXFontRenderer.ESCAPE_NEW_LINE) {
            last++;
            end = 0;
        } else
            end++;

        data = data + c;
    }

    @Override
    public CFXDocActionType getType() {
        return CFXDocActionType.INSERT;
    }

    @Override
    public int doAction(CFXDocument doc) {
        if (start != -1) {
            return doc.insert(data, first, start);
        } else {
            if (first >= doc.size())
                start = 0;
            else
                start = doc.getLine(first).length();
            return doc.insert(data, first);
        }
    }

    @Override
    public int undoAction(CFXDocument doc) {
        return doc.remove(first, start, last, end);
    }
}
