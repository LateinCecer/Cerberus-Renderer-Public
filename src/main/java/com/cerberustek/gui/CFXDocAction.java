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

/**
 * Stores the difference between two different versions of a
 * CFX document.
 *
 * This will in most cases be some kind of text edit, such as
 * the addition of one or multiple characters, the result of
 * a copy/paste operation, etc...
 */
public interface CFXDocAction {

    /**
     * Returns the index of the first line the action is performed
     * on before any kind of action is performed on the document.
     * @return first line
     */
    int getFirst();

    /**
     * Returns the index of the first letter (inclusive) in the first
     * line that the action is performed on.
     * @return first letter
     */
    int getStart();

    /**
     * Returns the index of the last line the action is performed
     * on before any kind of action is performed on the document.
     * @return last line
     */
    int getLast();

    /**
     * Returns the index of the last letter (exclusive) in the first
     * line that the action is performed on.
     * @return last letter
     */
    int getEnd();

    /**
     * Returns the type of action.
     * @return action type
     */
    CFXDocActionType getType();

    /**
     * Will apply the action to the appropriate CFXDocument.
     * @param doc document to apply the action to
     * @return number of affected lines
     */
    int doAction(CFXDocument doc);

    /**
     * Will undo the action on the appropriate CFXDocument.
     * @param doc document to undo the action on
     * @return number of affected lines
     */
    int undoAction(CFXDocument doc);
}
