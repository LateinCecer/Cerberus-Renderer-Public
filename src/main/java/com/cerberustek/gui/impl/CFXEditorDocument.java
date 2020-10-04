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

import com.cerberustek.exceptions.EndOfDocumentException;
import com.cerberustek.gui.impl.action.CFXInsertAction;
import com.cerberustek.gui.impl.action.CFXRemoveAction;
import com.cerberustek.gui.impl.action.CFXSetAction;
import com.cerberustek.gui.CFXDocAction;
import com.cerberustek.gui.CFXDocument;
import com.cerberustek.gui.CFXDocumentEditable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CFXEditorDocument implements CFXDocumentEditable {

    private final ArrayList<CFXDocAction> actions = new ArrayList<>();
    private final CFXDocument doc;

    public CFXEditorDocument() {
        doc = new CFXDocumentImpl();
    }

    public CFXEditorDocument(CFXDocument doc) {
        this.doc = doc;
    }

    @Override
    public List<CFXDocAction> popActions() {
        List<CFXDocAction> out = new LinkedList<>(actions);
        actions.clear();
        return out;
    }

    @Override
    public void undoAction(CFXDocAction action) {
        action.undoAction(doc);
    }

    @Override
    public void doAction(CFXDocAction action) {
        action.doAction(doc);
    }

    @Override
    public CharSequence getLine(int first) throws EndOfDocumentException {
        return doc.getLine(first);
    }

    @Override
    public CharSequence get(int first, int start, int last, int end) throws EndOfDocumentException {
        return doc.get(first, start, last, end);
    }

    @Override
    public int insert(CharSequence input, int first) throws EndOfDocumentException {
        CFXInsertAction action = new CFXInsertAction(first, input.toString());
        actions.add(action);
        return action.doAction(doc);
    }

    @Override
    public int insert(CharSequence input, int first, int start) throws EndOfDocumentException {
        CFXInsertAction action = new CFXInsertAction(first, start, input.toString());
        actions.add(action);
        return action.doAction(doc);
    }

    @Override
    public int insert(CharSequence input, int first, int start, int last, int end) throws EndOfDocumentException, ArrayIndexOutOfBoundsException {
        CFXInsertAction action = new CFXInsertAction(first, start, last, end, input.toString());
        actions.add(action);
        return action.doAction(doc);
    }

    @Override
    public int set(CharSequence input, int first, int start) throws EndOfDocumentException {
        CFXSetAction action = new CFXSetAction(first, start, input.toString());
        actions.add(action);
        return action.doAction(doc);
    }

    @Override
    public int set(CharSequence input, int first, int start, int last, int end) throws EndOfDocumentException, ArrayIndexOutOfBoundsException {
        CFXSetAction action = new CFXSetAction(first, start, last, end, input.toString());
        actions.add(action);
        return action.doAction(doc);
    }

    @Override
    public int remove(int first, int start, int last, int end) throws EndOfDocumentException {
        CFXRemoveAction action = new CFXRemoveAction(first, start, last, end);
        actions.add(action);
        return action.doAction(doc);
    }

    @Override
    public int size() {
        return doc.size();
    }
}
