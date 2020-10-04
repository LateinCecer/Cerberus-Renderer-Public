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

import java.util.List;

/**
 * Is a special kind of CFX Document that is mend for user
 * driven edits.
 *
 * An Editable document will produce CFXDocActions based on
 * the operations that are performed on it. These actions
 * can be used to undo edits, save changes or synchronize
 * changes with some kind of network service and are thus
 * crucial for any kind of user-driven text editor.
 */
public interface CFXDocumentEditable extends CFXDocument {

    /**
     * Will return and clear the list of actions that have
     * taken place on this document since the last time this
     * method was called.
     * @return list of actions
     */
    List<CFXDocAction> popActions();

    /**
     * Will undo the action without any effect on the action
     * list of this editable document.
     * @param action action to undo
     */
    void undoAction(CFXDocAction action);

    /**
     * Will do the action on the document without any effect
     * on the action list of this editable document.
     * @param action action to do
     */
    void doAction(CFXDocAction action);
}
