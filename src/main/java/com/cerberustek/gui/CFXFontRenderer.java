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

import com.cerberustek.Destroyable;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.resource.shader.SSBOResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Collection;

public interface CFXFontRenderer extends Destroyable {

    /** Binding port for the text buffer in the font shader */
    int TEXT_BUFFER_BINDING = 2;
    /** Symbol size in the text buffer in bytes */
    int TEXT_BUFFER_SYMBOL_SIZE = 16;

    char SPACE = ' ';
    char ESCAPE_TAB = '\t';
    char ESCAPE_RETURN = '\r';
    char ESCAPE_FORM_FEED = '\f';
    char ESCAPE_NEW_LINE = '\n';
    char ESCAPE_BACKSPACE = '\b';
    char ESCAPE_HIGHLIGHT = '\u001B';

    char FAKE_NEW_LINE = '\u001A';

    /**
     * Will load a character to the font renderer.
     *
     * If the character is already loaded at the time of
     * calling this method, this will just return the already
     * loaded character.
     * If the character is not loaded and the current thread
     * is a valid gl render thread, this method will directly
     * load the character to the appropriate texture atlas
     * and return the cfx character. Otherwise the character
     * will be loaded asynchronously and this method will
     * return null.
     *
     * @param c character char
     * @param font character font
     * @return CFX-Character
     */
    @Nullable CFXCharacter loadCharacter(char c, @NotNull Font font);

    /**
     * Will return the CFX-Character for the char and font
     * credentials.
     *
     * Different from the loadCharacter method, this method
     * will not load the character if absent.
     *
     * @param c char
     * @param font font
     * @return CFX-Character
     */
    @Nullable CFXCharacter getCharacter(char c, @NotNull Font font);

    /**
     * Will delete a cfx character from the font renderer.
     * @param c character to delete
     */
    void deleteCharacter(@NotNull CFXCharacter c);

    /**
     * Will delete the appropriate cfx character for the
     * specified character and font.
     *
     * If the character is not loaded, this method will
     * do nothing.
     *
     * @param c character
     * @param font font
     */
    void deleteCharacter(char c, @NotNull Font font);

    /**
     * Will load all characters in the string.
     * @param s string
     * @param font font
     * @return cfx characters
     */
    @Nullable Collection<CFXCharacter> loadString(@NotNull String s, @NotNull Font font);

    /**
     * Will load all characters in the char sequence.
     * @param s char sequence
     * @param font font
     * @return cfx characters
     */
    @Nullable Collection<CFXCharacter> loadString(@NotNull CharSequence s, @NotNull Font font);

    /**
     * Will load all characters in the char array.
     * @param s char array
     * @param font font
     * @return cfx characters
     */
    @Nullable Collection<CFXCharacter> loadString(@NotNull char[] s, @NotNull Font font);

    /**
     * Will load all characters in the char collection.
     * @param s char collection
     * @param font font
     * @return cfx characters
     */
    @Nullable Collection<CFXCharacter> loadString(@NotNull Collection<Character> s, @NotNull Font font);

    /**
     * Will add the specified cfx alphabet.
     * @param alphabet alphabet to add
     */
    void addAlphabet(@NotNull CFXAlphabet alphabet);

    /**
     * Will remote the specified cfx alphabet.
     * @param alphabet alphabet to remove
     */
    void removeAlphabet(@NotNull CFXAlphabet alphabet);

    /**
     * Will create and return a text buffer for the input
     * string and font.
     *
     * The default implementation of this method does not
     * support all types of escape characters for performance
     * reasons.
     * If you wish to format a text that contains escape
     * characters such as backspace and return, run the input
     * string through a per-formatter before submitting it to
     * this method.
     *
     * @param input input string to format
     * @param font input font
     * @param hspace horizontal spacing between letters
     * @param vspace vertical spacing between letters
     * @return formatted text buffer
     */
    @Nullable
    CFXTextBuffer formatTextBuffer(@NotNull String input, @NotNull Font font, int hspace, int vspace);

     /**
     * Will create and return a text buffer for the input
     * string and font.
     *
     * The default implementation of this method does not
     * support all types of escape characters for performance
     * reasons.
     * If you wish to format a text that contains escape
     * characters such as backspace and return, run the input
     * string through a per-formatter before submitting it to
     * this method.
     *
     * @param input input string to format
     * @param font input font
     * @param hspace horizontal spacing between letters
     * @param vspace vertical spacing between letters
     * @param colors enable color highlighting
     * @param packing if this is true, tight packing for of
     *                the letters will be enabled
     * @return formatted text buffer
     */
     @Nullable
    CFXTextBuffer formatTextBuffer(@NotNull String input, @NotNull Font font, int hspace, int vspace,
                                   boolean colors, boolean packing);

    /**
     * Will populate the specified buffer with the contents
     * of a formatted text buffer using the specified string
     * and font.
     *
     * The specified ssbo has to be a Mutable ssbo, otherwise
     * this method will throw an illegal argument exception.
     *
     * @param ssbo ssbo to write to
     * @param input input text
     * @param font input font
     * @param hspace horizontal spacing between letters
     * @param vspace vertical spacing between letters
     * @param colors allow highlight text colors
     * @param packing if this is true, tight packing for of
     *                the letters will be enabled
     */
    @Nullable
    CFXTextRenderContext formatTextBuffer(@NotNull SSBOResource ssbo, @NotNull String input, @NotNull Font font,
                                          int hspace, int vspace, boolean colors, boolean packing);

    /**
     * Will populate the specified buffer with the contents
     * of a formatted text buffer using the specified string
     * and font.
     *
     * The specified ssbo has to be a Mutable ssbo, otherwise
     * this method will throw an illegal argument exception.
     *
     * @param ssbo ssbo to write to
     * @param input input text
     * @param font input font
     * @param hspace horizontal spacing between letters
     * @param vspace vertical spacing between letters
     */
    @Nullable
    CFXTextRenderContext formatTextBuffer(@NotNull SSBOResource ssbo, @NotNull String input, @NotNull Font font, int hspace, int vspace);

    /**
     * Will populate the specified buffer with the contents
     * of a formatted text buffer using the specified string
     * and font.
     *
     * The specified ssbo has to be a Mutable ssbo, otherwise
     * this method will throw an illegal argument exception.
     *
     * @param ssbo ssbo to write to
     * @param buffer formatted text buffer
     */
    @Nullable
    CFXTextRenderContext formatTextBuffer(@NotNull CFXTextBuffer buffer, @NotNull SSBOResource ssbo);

    /**
     * Returns the width and height of the formatted text in
     * pixels.
     *
     * @param input input string to format
     * @param font input font
     * @param hspace horizontal spacing between letters
     * @param vspace vertical spacing between letters
     * @param packing if this is true, tight packing for of
     *                the letters will be enabled
     * @return text bounds in pixels
     */
    @Nullable
    Vector2i getTextBounds(@NotNull String input, @NotNull Font font, int hspace, int vspace, boolean packing);

    /**
     * Returns the position of the specified line in the
     * specified line in pixels.
     *
     * @param input input text
     * @param font font
     * @param hspace horizontal spacing in pixels
     * @param vspace vertical spacing in pixels
     * @param packing tight packing
     * @param line line index
     * @param letter letter index
     * @return pixel position
     */
    @Nullable
    Vector2i getTextPixel(@NotNull String input, Font font, int hspace, int vspace, boolean packing, int line, int letter);

    /**
     * Returns the line and letter index of the specified pixel
     * position inside of the input text.
     *
     * The x-coordinate in the returned vector contains the line
     * index and the y-coordinate represents the letter index.
     *
     * @param input input text
     * @param font font
     * @param hspace horizontal spacing in pixels
     * @param vspace vertical spacing in pixels
     * @param packing tight packing
     * @param pixelPos pixel position
     * @return line and letter index
     */
    @Nullable
    Vector2i getTextPosition(@NotNull String input, @NotNull Font font, int hspace, int vspace, boolean packing, @NotNull Vector2i pixelPos);

    /**
     * Will move the caret by one character either forwards, or
     * backwards depending in the specified direction.
     *
     * If the end of the file is reached, the caret will simply
     * not be moved further. If the specified direction parameter
     * is true, the caret will be moved forward. Consequently,
     * if the direction parameter reports false, the caret will
     * be moved backwards.
     *
     * @param caret caret to move
     * @param doc document to move the caret in
     * @param hspace horizontal spacing in pixels
     * @param vspace vertical spacing in pixels
     * @param packing tight packing
     * @param direction movement direction of the caret
     */
    void moveCaret(@NotNull CFXCaret caret, @NotNull CFXDocument doc, int hspace, int vspace, boolean packing,
                   boolean direction);

    /**
     * Will insert new lines and limit the amount of lines,
     * so that the input string fits into the appropriate
     * pixel bounds.
     *
     * If the wordWarp option is true, lines will only be
     * separated between words (as long as a single word is
     * not longer than a line).
     *
     * @param input input string to format
     * @param bounds bounds in pixels to abide by
     * @param wordWrap word wrapping
     * @return wrapped string
     */
    @NotNull
    String wrap(@NotNull String input, @NotNull Font font, @NotNull Vector2i bounds, int hspace, int vspace,
                boolean packing, boolean wordWrap);

    /**
     * Will insert new lines and limit the amount of lines,
     * so that the input string fits into the appropriate
     * pixel bounds.
     *
     * If the worldWrap option is true, lines will only be
     * separated between words (as long as a single word is
     * not longer than a line).
     *
     * @param input input string to format
     * @param font input font
     * @param bounds bounds in pixels to abide by
     * @param hspace word wrapping
     * @param vspace vertical spacing
     * @param packing horizontal spacing
     * @param wordWrap word wrapping
     * @param newLineEscape line escape char
     * @return wrapped string
     */
    @NotNull String wrap(@NotNull String input, @NotNull Font font, @NotNull Vector2i bounds, int hspace, int vspace,
                         boolean packing, boolean wordWrap, char newLineEscape);

    /**
     * Will calculate and return the buffer capacity required
     * to store the specified input text with the input font
     * in bytes.
     * @param input input text
     * @return byte size
     */
    long calcByteSize(@NotNull String input);
}
