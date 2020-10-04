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

package com.cerberustek.gui;

public interface CFXCaret {

    /**
     * Returns the line the caret is positioned in.
     *
     * If a block of text is selected, this will return the first
     * line that contains selected text.
     *
     * @return line of the caret
     */
    int getFirst();

    /**
     * Returns the letter the caret is positioned in within
     * the caret's line.
     *
     * If a block of text is selected, this will return the first
     * letter position of the text block (inclusive).
     *
     * @return line of the caret
     */
    int getStart();

    /**
     * Returns the second line of the caret's selection block.
     *
     * If the caret has currently not selected a block, this
     * method will return -1.
     *
     * @return last line of the selection block
     */
    int getLast();

    /**
     * Returns the second caret letter position within the last
     * line of the selection block.
     *
     * If the caret has currently not selected a block, this
     * method will return -1.
     *
     * @return end letter of the selection block (exclusive)
     */
    int getEnd();

    /**
     * Will set the first caret line.
     * @param first first line
     * @return this caret
     */
    CFXCaret setFirst(int first);

    /**
     * Will set the start caret letter.
     * @param start start letter
     * @return this caret
     */
    CFXCaret setStart(int start);

    /**
     * Will set the last caret line
     * @param last last line
     * @return this caret
     */
    CFXCaret setLast(int last);

    /**
     * Will set the end caret letter
     * @param end end letter
     * @return this caret
     */
    CFXCaret setEnd(int end);

    /**
     * Returns the caret style
     * @return caret style
     */
    CFXCaretStyle getStyle();
}
