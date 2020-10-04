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

import com.cerberustek.exceptions.EndOfDocumentException;

/**
 * A document class used to store and process text data for
 * such things as text fields and text editors.
 */
public interface CFXDocument {

    /**
     * Will get the specified line of the document as a plain
     * char sequence.
     *
     * @param first line
     * @return line as plain text
     * @throws EndOfDocumentException thrown, if the specified index
     *          of the line to retrieve is not inside of the document
     */
    CharSequence getLine(int first) throws EndOfDocumentException;

    /**
     * Will get the specified block of the document as a plain
     * char sequence.
     *
     * @param first first line
     * @param start first letter
     * @param last last line
     * @param end last letter
     * @return block as plain text
     * @throws EndOfDocumentException thrown, if the specified block
     *          exceeds the bounds of the document
     */
    CharSequence get(int first, int start, int last, int end) throws EndOfDocumentException;

    /**
     * Will insert the specified input string into the document at the
     * end of the specified line.
     *
     * @param input input string
     * @param first line to append to
     * @return number of affected lines
     * @throws EndOfDocumentException thrown, if the specified line index
     *          does not exist within the document
     */
    int insert(CharSequence input, int first) throws EndOfDocumentException;

    /**
     * Will insert the specified input string into the document at the
     * appropriate position.
     *
     * @param input input string
     * @param first first line
     * @param start first letter
     * @return number of affected lines
     * @throws EndOfDocumentException thrown, if the specified position
     *          attributes are outside of the bounds of the document
     */
    int insert(CharSequence input, int first, int start) throws EndOfDocumentException;

    /**
     * Will insert the specified input string into the document at the
     * appropriate position.
     *
     * The end location of the block will be queried from the input
     * string.
     *
     * @param input input string
     * @param first first line
     * @param start first letter
     * @param last last line
     * @param end last letter
     * @return number of affected lines
     * @throws EndOfDocumentException thrown, if the specified block
     *          exceeds the bounds of the document
     * @throws ArrayIndexOutOfBoundsException thrown, if the specified
     *          block size exceeds the size of the input string
     */
    int insert(CharSequence input, int first, int start, int last, int end) throws EndOfDocumentException,
            ArrayIndexOutOfBoundsException;

    /**
     * Will set the specified block of the document to the
     * appropriate input text.
     *
     * The end location of the block will be queried from the
     * input string.
     *
     * @param input input string
     * @param first first line
     * @param start first letter
     * @return number of affected lines
     * @throws EndOfDocumentException thrown, when
     *          the specified block is outside of the scope of the document
     */
    int set(CharSequence input, int first, int start) throws EndOfDocumentException;

    /**
     * Will set the specified block of the document to the
     * appropriate input text.
     *
     * @param input input text
     * @param first first line
     * @param start first letter (inclusive)
     * @param last last line
     * @param end last letter (exclusive)
     * @return number of affected lines
     * @throws EndOfDocumentException thrown, when
     *          the specified block is outside of the scope of the document
     * @throws ArrayIndexOutOfBoundsException thrown, when the specified
     *          block size is greater than the input string
     */
    int set(CharSequence input, int first, int start, int last, int end) throws EndOfDocumentException,
            ArrayIndexOutOfBoundsException;

    /**
     * Will remove the specified block from the document.
     *
     * @param first first line
     * @param start first letter (inclusive)
     * @param last last line
     * @param end last letter (exclusive)
     * @return number of affected lines
     * @throws EndOfDocumentException thrown, when
     *          the specified block is outside of the scope of the document
     */
    int remove(int first, int start, int last, int end) throws EndOfDocumentException;

    /**
     * Returns the amount of lines in the document
     * @return amount of lines
     */
    int size();
}
