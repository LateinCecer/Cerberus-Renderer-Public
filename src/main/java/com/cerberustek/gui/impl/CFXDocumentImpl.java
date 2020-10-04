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
import com.cerberustek.gui.CFXDocument;

import java.util.ArrayList;

import static com.cerberustek.gui.CFXFontRenderer.*;

@SuppressWarnings("DuplicatedCode")
public class CFXDocumentImpl implements CFXDocument {

    /** list of lines */
    private final ArrayList<CharSequence> lines = new ArrayList<>();

    public CFXDocumentImpl() {
        lines.add("");
    }

    @Override
    public CharSequence getLine(int first) throws EndOfDocumentException {
        if (first >= lines.size())
            throw new EndOfDocumentException(first, 0, -1, -1, this);
        return lines.get(first);
    }

    @Override
    public CharSequence get(int first, int start, int last, int end) throws EndOfDocumentException {
        if (first >= lines.size() || last >= lines.size())
            throw new EndOfDocumentException(first, start, last, end, this);

        if (first == last) {
            return lines.get(first).subSequence(start, end);
        } else {
            StringBuilder builder = new StringBuilder();
            CharSequence currentLine = lines.get(first);
            if (start >= currentLine.length())
                throw new EndOfDocumentException(first, start, last, end, this);

            builder.append(currentLine.subSequence(start, currentLine.length()));

            for (int lineIndex = first + 1; lineIndex < last; lineIndex++)
                builder.append(lines.get(lineIndex));

            currentLine = lines.get(last);
            if (end > currentLine.length())
                throw new EndOfDocumentException(first, start, last, end, this);

            builder.append(currentLine.subSequence(0, end));
            return builder.toString();
        }
    }

    @Override
    public int insert(CharSequence input, int first) throws EndOfDocumentException {
        if (first >= lines.size())
            throw new EndOfDocumentException(first, 0, -1, -1, this);

        int line = first;

        StringBuilder lineBuilder = new StringBuilder();
        lineBuilder.append(lines.get(line));

        for (int index = 0; index < input.length(); index++) {
            char c = input.charAt(index);

            if (c == ESCAPE_NEW_LINE) {
                if (line == first)
                    lines.set(line, lineBuilder.toString());
                else
                    lines.add(line, lineBuilder.toString());
                lineBuilder = new StringBuilder();
                line++;
            } else
                lineBuilder.append(c);
        }

        if (line == first)
            lines.set(line, lineBuilder.toString());
        else
            lines.add(line, lineBuilder.toString());

        return 1 + line - first;
    }

    @Override
    public int insert(CharSequence input, int first, int start) throws EndOfDocumentException {
        if (first >= lines.size())
            throw new EndOfDocumentException(first, start, -1, -1, this);

        CharSequence currentLine = lines.get(first);
        if (start > currentLine.length())
            throw new EndOfDocumentException(first, start, -1, -1, this);

        int line = first;

        CharSequence tail = currentLine.subSequence(start, currentLine.length());
        StringBuilder lineBuilder = new StringBuilder();
        lineBuilder.append(currentLine.subSequence(0, start));

        for (int index = 0; index < input.length(); index++) {
            char c = input.charAt(index);

            if (c == ESCAPE_NEW_LINE) {
                if (line == first)
                    lines.set(line, lineBuilder.toString());
                else
                    lines.add(line, lineBuilder.toString());
                lineBuilder = new StringBuilder();
                line++;
            } else
                lineBuilder.append(c);
        }

        lineBuilder.append(tail);
        if (line == first)
            lines.set(line, lineBuilder.toString());
        else
            lines.add(line, lineBuilder.toString());

        return 1 + line - first;
    }

    @Override
    public int insert(CharSequence input, int first, int start, int last, int end) throws EndOfDocumentException, ArrayIndexOutOfBoundsException {
        if (first >= lines.size())
            throw new EndOfDocumentException(first, start, last, end, this);

        CharSequence currentLine = lines.get(first);
        if (start > currentLine.length())
            throw new EndOfDocumentException(first, start, last, end, this);

        if (last >= lines.size())
            throw new EndOfDocumentException(first, start, last, end, this);
        if (end > lines.get(end).length())
            throw new EndOfDocumentException(first, start, last, end, this);

        int line = first;
        int inputLine = 0;
        int inputLetter = 0;

        CharSequence tail = currentLine.subSequence(start, currentLine.length());
        StringBuilder lineBuilder = new StringBuilder();
        lineBuilder.append(currentLine.subSequence(0, start));

        for (int index = 0; index < input.length() && (inputLine < last || inputLetter < end); index++) {
            char c = input.charAt(index);

            if (c == ESCAPE_NEW_LINE) {
                if (line == first)
                    lines.set(line, lineBuilder.toString());
                else
                    lines.add(line, lineBuilder.toString());
                lineBuilder = new StringBuilder();
                line++;
                inputLine++;
                inputLetter = 0;
            } else {
                lineBuilder.append(c);
                inputLetter++;
            }
        }

        lineBuilder.append(tail);
        if (line == first)
            lines.set(line, lineBuilder.toString());
        else
            lines.add(line, lineBuilder.toString());

        return 1 + line - first;
    }

    @Override
    public int set(CharSequence input, int first, int start) throws EndOfDocumentException {
        if (first >= lines.size())
            throw new EndOfDocumentException(first, start, -1, -1, this);

        CharSequence firstLine = lines.get(first);
        if (start > firstLine.length())
            throw new EndOfDocumentException(first, start, -1, -1, this);

        int line = first;

        CharSequence tail = firstLine.subSequence(start, firstLine.length());
        StringBuilder lineBuilder = new StringBuilder();
        lineBuilder.append(firstLine.subSequence(0, start));

        for (int index = 0; index < input.length(); index++) {
            char c = input.charAt(index);

            if (c == ESCAPE_NEW_LINE) {
                if (line < lines.size())
                    lines.set(line, lineBuilder.toString());
                else
                    lines.add(lineBuilder.toString());
                lineBuilder = new StringBuilder();
                line++;
            } else
                lineBuilder.append(c);
        }

        lineBuilder.append(tail);
        if (line < lines.size())
            lines.set(line, lineBuilder.toString());
        else
            lines.add(lineBuilder.toString());

        return 1 + line - first;
    }

    @Override
    public int set(CharSequence input, int first, int start, int last, int end) throws EndOfDocumentException, ArrayIndexOutOfBoundsException {
        if (first >= lines.size())
            throw new EndOfDocumentException(first, start, last, end, this);

        CharSequence firstLine = lines.get(first);
        if (start > firstLine.length())
            throw new EndOfDocumentException(first, start, last, end, this);

        if (last >= lines.size())
            throw new EndOfDocumentException(first, start, last, end, this);

        CharSequence lastLine = lines.get(last);
        if (end > lastLine.length())
            throw new EndOfDocumentException(first, start, last, end, this);

        int line = first;

        CharSequence tail = lastLine.subSequence(end, lastLine.length());
        StringBuilder lineBuilder = new StringBuilder();
        lineBuilder.append(firstLine.subSequence(0, start));

        // remove all partially or totally overwritten lines
        for (int index = first; index <= last; index++)
            //noinspection SuspiciousListRemoveInLoop
            lines.remove(index);

        for (int index = 0; index < input.length(); index++) {
            char c = input.charAt(index);

            if (c == ESCAPE_NEW_LINE) {
                if (line == first)
                    lines.set(line, lineBuilder.toString());
                else
                    lines.add(line, lineBuilder.toString());
                lineBuilder = new StringBuilder();
                line++;
            } else
                lineBuilder.append(c);
        }

        lineBuilder.append(tail);
        if (line == first)
            lines.set(line, lineBuilder.toString());
        else
            lines.add(line, lineBuilder.toString());

        return 1 + line - first;
    }

    @Override
    public int remove(int first, int start, int last, int end) throws EndOfDocumentException {
        if (first >= lines.size())
            throw new EndOfDocumentException(first, start, last, end, this);

        CharSequence firstLine = lines.get(first);
        if (start > firstLine.length())
            throw new EndOfDocumentException(first, start, last, end, this);

        if (last >= lines.size())
            throw new EndOfDocumentException(first, start, last, end, this);

        CharSequence lastLine = lines.get(last);
        if (end > lastLine.length())
            throw new EndOfDocumentException(first, start, last, end, this);

        // remove all partially or completely overlapping lines except one
        for (int index = first + 1; index <= last; index++)
            //noinspection SuspiciousListRemoveInLoop
            lines.remove(index);
        // overwrite the last remaining line of the original
        lines.set(first, "" + firstLine.subSequence(0, start) + lastLine.subSequence(end, lastLine.length()));

        return 1 + first - last;
    }

    @Override
    public int size() {
        return lines.size();
    }
}
