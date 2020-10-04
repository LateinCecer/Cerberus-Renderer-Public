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

import com.cerberustek.CerberusEvent;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.events.ExceptionEvent;
import com.cerberustek.exceptions.FontRenderException;
import com.cerberustek.exceptions.TextFormatException;
import com.cerberustek.gui.*;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.logic.math.Vector3f;
import com.cerberustek.resource.image.TextureResource;
import com.cerberustek.resource.impl.BufferedImageResource;
import com.cerberustek.resource.impl.BufferedTextureResource;
import com.cerberustek.resource.shader.SSBOResource;
import com.cerberustek.shader.ssbo.ShaderStorageBufferObject;
import com.cerberustek.shader.ssbo.impl.MutableSSBO;
import com.cerberustek.texture.Texture;
import com.cerberustek.texture.TextureBoard;
import com.cerberustek.util.CFXColor;
import com.cerberustek.CerberusRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * The main font rendering system of the new CFX-GUI renderer.
 *
 * Characters are initially rendered using the glyph renderer
 * and font system from the standard java awt library. These
 * individual glyph renderings are then stored in temporary
 * textures, which are then stitched together in a texture
 * atlas.
 *
 * The font rendering itself is done by a compute shader. This
 * greatly improves rendering performance compared to a more
 * traditional, polygonal based font rendering system, because
 * the entire clipping and rasterization part during font
 * rendering is ditched.
 * Using the compute shader setup in place also ensures, that
 * all characters are indeed rendered undistorted to the target
 * texture, which is not necessarily the case for the old
 * font rendering system the Cerberus Renderer employed.
 */
public class CFXFontRendererImpl implements CFXFontRenderer {

    /** Default character set used to figure out the maximum size
     * of a character with a given font */
    private static final char[] DEFAULT_CHARACTER_SET = new char[] {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
            'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
            'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', '0', 'ö', 'ä', 'ü', 'Ö', 'Ä', 'Ü', 'ß', '?', '!', '.',
            '-', '_', '*', '+', '~', '\'', '#', '=', '}', '{', '[', ']',
            '(', ')', '&', '%', '$', '§', '"', '@', '€', '|', '<', '>'
    };

    /** Character map */
    private final HashMap<CharInfo, CFXCharacter> chars = new HashMap<>();
    /** Alphabets containing the characters
     * This should be sorted by cellsize in ascending other,
     * such that the cellsize of the right neighbour of an
     * alphabet is always greater than the cellsize of set
     * alphabet in both, the x and y coordinate.*/
    private final ArrayList<CFXAlphabet> alphabets = new ArrayList<>();
    /** local reference to the current cerberus renderer
     * instance for faster lookup during rendering. */
    private CerberusRenderer renderer;

    @Override
    public @Nullable CFXCharacter loadCharacter(char c, @NotNull Font font) {
        CharInfo info = new CharInfo(c, font);
        CFXCharacter character = chars.get(info);
        if (character != null)
            return character;

        CerberusRenderer renderer = getRenderer();
        if (renderer.getWindow().isGlThread()) {
            // this is a gl render thread. Load the char directly
            BufferedImage image = generateImage(c, font);
            if (image == null) {
                CerberusRegistry.getInstance().getService(CerberusEvent.class)
                        .executeFullEIF(new ExceptionEvent(CerberusRenderer.class,
                                new FontRenderException("Cannot render character " + c + " with font " + font)));
                CerberusRegistry.getInstance().warning("Failed to render character glyph \"" + c + "\" in font "
                        + font + "!");
                return null;
            }

            TextureResource charResource = new BufferedTextureResource(new BufferedImageResource(image, 0));
            TextureBoard textureBoard = renderer.getTextureBoard();
            Texture texture = textureBoard.loadTexture(charResource);
            if (texture == null) {
                CerberusRegistry.getInstance().getService(CerberusEvent.class)
                        .executeFullEIF(new ExceptionEvent(CerberusRenderer.class,
                                new FontRenderException("Failed to load image texture for char " + c
                                        + " with font " + font + "!")));
                CerberusRegistry.getInstance().warning("Failed to render character glyph \"" + c + "\" in font "
                        + font + "!");
                return null;
            }

            CFXAlphabet alphabet = findAppropriateAlphabet(new Vector2i(image.getWidth(), image.getHeight()), font);
            textureBoard.bindTexture(charResource);
            // alphabet.bind();

            CFXCharacter cfxChar;
            try {
                int id = alphabet.insertCharacter(charResource);
                if (id == -1) {
                    CerberusRegistry.getInstance().warning("Failed to save character glyph \"" + c + "\" in font "
                            + font + "!");
                    return null;
                }

                cfxChar = new CFXCharacterImpl(id, image.getWidth(), image.getHeight(), c, font, alphabet);
                chars.put(info, cfxChar);
            } finally {
                // delete temporary char texture
                getRenderer().getTextureBoard().deleteTexture(charResource);
            }
            return cfxChar;
        } else {
            // this is not a gl render thread. Load asynchronously
            getRenderer().submitGLTask(t -> loadCharacter(c, font));
            return null;
        }
    }

    @Override
    public @Nullable CFXCharacter getCharacter(char c, @NotNull Font font) {
        return chars.get(new CharInfo(c, font));
    }

    @Override
    public void deleteCharacter(@NotNull CFXCharacter c) {
        chars.remove(new CharInfo(c.getCharacter(), c.getFont()));
    }

    @Override
    public void deleteCharacter(char c, @NotNull Font font) {
        chars.remove(new CharInfo(c, font));
    }

    @Override
    public @Nullable Collection<CFXCharacter> loadString(@NotNull String s, @NotNull Font font) {
        char[] chars = s.toCharArray();
        if (chars.length == 0)
            return null;

        HashSet<CFXCharacter> output = new HashSet<>();
        for (char c : chars) {
            CFXCharacter cfxChar = loadCharacter(c, font);
            if (cfxChar != null)
                output.add(cfxChar);
            else
                CerberusRegistry.getInstance().warning("Failed to render character " + c + " from string" +
                        " input. The character will not show up during rendering.");
        }
        return output;
    }

    @Override
    public @Nullable Collection<CFXCharacter> loadString(@NotNull CharSequence s, @NotNull Font font) {
        HashSet<CFXCharacter> output = new HashSet<>();

        for (int i = 0; i < s.length(); i++) {
            CFXCharacter cfxChar = loadCharacter(s.charAt(i), font);
            if (cfxChar != null)
                output.add(cfxChar);
            else
                CerberusRegistry.getInstance().warning("Failed to render character " + s.charAt(i) + " from char" +
                        " sequence input. The character will not show up during rendering.");
        }
        return output;
    }

    @Override
    public @Nullable Collection<CFXCharacter> loadString(@NotNull char[] s, @NotNull Font font) {
        HashSet<CFXCharacter> output = new HashSet<>();

        for (char c : s) {
            CFXCharacter cfxChar = loadCharacter(c, font);
            if (cfxChar != null)
                output.add(cfxChar);
            else
                CerberusRegistry.getInstance().warning("Failed to render character " + c + " from" +
                        " char array input. The character will not show up during rendering.");
        }
        return output;
    }

    @Override
    public @Nullable Collection<CFXCharacter> loadString(@NotNull Collection<Character> s, @NotNull Font font) {
        HashSet<CFXCharacter> output = new HashSet<>();

        for (char c : s) {
            CFXCharacter cfxChar = loadCharacter(c, font);
            if (cfxChar != null)
                output.add(cfxChar);
            else
                CerberusRegistry.getInstance().warning("Failed to render character " + c + " from"
                        + " char collection input. The character will not show up during rendering.");
        }
        return output;
    }

    @Override
    public void addAlphabet(@NotNull CFXAlphabet alphabet) {
        this.alphabets.add(alphabet);
    }

    @Override
    public void removeAlphabet(@NotNull CFXAlphabet alphabet) {
        this.alphabets.remove(alphabet);
    }

    @Override
    public @Nullable CFXTextBuffer formatTextBuffer(@NotNull String input, @NotNull Font font, int hspace, int vspace) {
        return formatTextBuffer(input, font, hspace, vspace, false, false);
    }

    @Override
    public @Nullable CFXTextBuffer formatTextBuffer(@NotNull String input, @NotNull Font font, int hspace, int vspace, boolean colors,
                                          boolean packing) {

        CFXManager manager = getRenderer().getGUIManager();
        final HashMap<CFXAlphabet, HashSet<Symbol>> mapOfChars = new HashMap<>();

        int bufferCap = 0;

        Vector2i pos = new Vector2i(0);
        int currentLineHeight = font.getSize();
        int currentARGB = 0xFFFFFFFF; // default color: white

        Rectangle2D spaceBounds = getSpaceBounds(font);
        int spaceWidth = (int) Math.ceil(spaceBounds.getWidth());
        int spaceHeight = (int) Math.ceil(spaceBounds.getHeight());

        Loop : for (int i = 0; i < input.length(); i++) {
            switch (input.charAt(i)) {
                case ESCAPE_RETURN:
                    CerberusRegistry.getInstance().warning("return is not allowed in direct text processing");
                    CerberusRegistry.getInstance().warning("return escape char will be ignored!");
                    break;
                case ESCAPE_NEW_LINE:
                    pos.setX(0);
                    pos.addSelf(0, currentLineHeight + vspace);
                    currentLineHeight = font.getSize();
                    break;
                case ESCAPE_BACKSPACE:
                    CerberusRegistry.getInstance().warning("backspace not allowed in direct text processing");
                    CerberusRegistry.getInstance().warning("backspace escape char will be ignored!");
                    break;
                case ESCAPE_HIGHLIGHT:
                    try {
                        FormatColor color = findColor(input, i);
                        currentARGB = CFXColor.toARGB(1f, color.color.getX(),
                                color.color.getY(), color.color.getZ());
                        i = color.pos;
                    } catch (TextFormatException e) {
                        CerberusRegistry.getInstance().fine("Illegal text formatting: " + e);
                    }
                    break;
                case ESCAPE_FORM_FEED:
                    break Loop;
                case ESCAPE_TAB:
                    pos.addSelf(spaceWidth * manager.tabInSpaces() + hspace, 0);
                    break;
                case SPACE:
                    pos.addSelf(spaceWidth + hspace, 0);
                    if (currentLineHeight < spaceHeight)
                        currentLineHeight = spaceHeight;
                    break;
                default:
                    CFXCharacter cfxChar = loadCharacter(input.charAt(i), font);
                    if (cfxChar == null)
                        continue; // could not load character

                    // adjust height for the current line
                    if (currentLineHeight < cfxChar.getHeight())
                        currentLineHeight = cfxChar.getHeight();

                    mapOfChars.computeIfAbsent(cfxChar.getAlphabet(),
                            k -> new HashSet<>()).add(new Symbol(pos.copy(), cfxChar, currentARGB));

                    if (packing || cfxChar.getWidth() > spaceWidth)
                        pos.addSelf(cfxChar.getWidth() + hspace, 0);
                    else
                        pos.addSelf(spaceWidth + hspace, 0);

                    bufferCap++;
            }
        }

        if (mapOfChars.isEmpty())
            return null;

        final ByteBuffer buffer = BufferUtils.createByteBuffer(bufferCap * TEXT_BUFFER_SYMBOL_SIZE);
        final long[] cuts = new long[mapOfChars.size() - 1];
        final CFXAlphabet[] alphabets = new CFXAlphabet[mapOfChars.size()];
        final int[] charCounts = new int[mapOfChars.size()];
        int i = 0;

        for (CFXAlphabet alphabet : mapOfChars.keySet()) {
            HashSet<Symbol> chars = mapOfChars.get(alphabet);
            if (i < cuts.length)
                cuts[i] = chars.size() * TEXT_BUFFER_SYMBOL_SIZE;

            charCounts[i] = chars.size();
            alphabets[i++] = alphabet;

            chars.forEach(c -> {
                buffer.putInt(c.character.getTex());
                buffer.putInt(c.coord.getX());
                buffer.putInt(c.coord.getY());
                buffer.putInt(c.argb);
            });
        }

        buffer.flip();
        return new CFXTextBufferImpl(buffer, alphabets, cuts, charCounts, pos);
    }

    @Override
    public @Nullable CFXTextRenderContext formatTextBuffer(@NotNull SSBOResource ssbo, @NotNull String input, @NotNull Font font, int hspace, int vspace,
                                                           boolean colors, boolean packing) {
        CFXTextBuffer textBuffer = formatTextBuffer(input, font, hspace, vspace, colors, packing);
        if (textBuffer != null)
            return formatTextBuffer(textBuffer, ssbo);
        return null;
    }

    @Override
    public @Nullable CFXTextRenderContext formatTextBuffer(@NotNull CFXTextBuffer buffer, @NotNull SSBOResource ssbo) {
        ShaderStorageBufferObject bufferObj = getRenderer().getShaderBoard().loadSSBO(ssbo);
        if (!(bufferObj instanceof MutableSSBO))
            throw new IllegalStateException("Buffer has to be mutalbe!");

        getRenderer().tryGLTask(t -> {
            // format binding id's
            int[] bindings = new int[buffer.size()];
            Arrays.fill(bindings, TEXT_BUFFER_BINDING);

            ((MutableSSBO) bufferObj).getPointer().bind();
            ((MutableSSBO) bufferObj).bufferData(buffer.getBuffer(), buffer.cuts(), bindings);
            getRenderer().getShaderBoard().bindSSBO(ssbo, 0); // mark the ssbo as bound for security reasons
        });
        return new CFXTextRenderContextImpl(buffer.alphabets(), ssbo, buffer.charCounts(), buffer.getBounds());
    }

    @Override
    public @Nullable Vector2i getTextBounds(@NotNull String input, @NotNull Font font, int hspace, int vspace,
                                            boolean packing) {
        CFXManager manager = getRenderer().getGUIManager();

        Vector2i pos = new Vector2i(0);
        int currentLineHeight = font.getSize();

        Rectangle2D spaceBounds = getSpaceBounds(font);
        int spaceWidth = (int) Math.ceil(spaceBounds.getWidth());
        int spaceHeight = (int) Math.ceil(spaceBounds.getHeight());

        Loop : for (int i = 0; i < input.length(); i++) {
            switch (input.charAt(i)) {
                case ESCAPE_RETURN:
                    CerberusRegistry.getInstance().warning("return is not allowed in direct text processing");
                    CerberusRegistry.getInstance().warning("return escape char will be ignored!");
                    break;
                case ESCAPE_NEW_LINE:
                    pos.setX(0);
                    pos.addSelf(0, currentLineHeight + vspace);
                    currentLineHeight = font.getSize();
                    break;
                case ESCAPE_BACKSPACE:
                    CerberusRegistry.getInstance().warning("backspace not allowed in direct text processing");
                    CerberusRegistry.getInstance().warning("backspace escape char will be ignored!");
                    break;
                case ESCAPE_HIGHLIGHT:
                    try {
                        FormatColor color = findColor(input, i);
                        i = color.pos;
                    } catch (TextFormatException e) {
                        CerberusRegistry.getInstance().fine("Illegal text formatting: " + e);
                    }
                    break;
                case ESCAPE_FORM_FEED:
                    break Loop;
                case ESCAPE_TAB:
                    pos.addSelf(spaceWidth * manager.tabInSpaces() + hspace, 0);
                    break;
                case SPACE:
                    pos.addSelf(spaceWidth + hspace, 0);
                    if (currentLineHeight < spaceHeight)
                        currentLineHeight = spaceHeight;
                    break;
                default:
                    CFXCharacter cfxChar = loadCharacter(input.charAt(i), font);
                    if (cfxChar == null)
                        continue; // could not load character

                    // adjust height for the current line
                    if (currentLineHeight < cfxChar.getHeight())
                        currentLineHeight = cfxChar.getHeight();

                    if (packing || cfxChar.getWidth() > spaceWidth)
                        pos.addSelf(cfxChar.getWidth() + hspace, 0);
                    else
                        pos.addSelf(spaceWidth + hspace, 0);
            }
        }
        return pos;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public @Nullable Vector2i getTextPixel(@NotNull String input, @NotNull Font font, int hspace, int vspace,
                                           boolean packing, int line, int letter) {
        CFXManager manager = getRenderer().getGUIManager();

        Vector2i pos = new Vector2i(0);
        int currentLineHeight = font.getSize();

        Rectangle2D spaceBounds = getSpaceBounds(font);
        int spaceWidth = (int) Math.ceil(spaceBounds.getWidth());
        int spaceHeight = (int) Math.ceil(spaceBounds.getHeight());

        int currentLine = 0;
        int currentLetter = 0;

        if (line == currentLine && letter <= currentLetter)
            return pos;

        Loop : for (int i = 0; i < input.length(); i++) {
            switch (input.charAt(i)) {
                case ESCAPE_RETURN:
                    CerberusRegistry.getInstance().warning("return is not allowed in direct text processing");
                    CerberusRegistry.getInstance().warning("return escape char will be ignored!");
                    break;
                case ESCAPE_NEW_LINE:
                    if (currentLine == line)
                        return pos;

                    pos.setX(0);
                    pos.addSelf(0, currentLineHeight + vspace);
                    currentLineHeight = font.getSize();
                    currentLine++;
                    currentLetter = 0;
                    break;
                case FAKE_NEW_LINE:
                    pos.setX(0);
                    pos.addSelf(0, currentLineHeight + vspace);
                    currentLineHeight = font.getSize();
                    break;
                case ESCAPE_BACKSPACE:
                    CerberusRegistry.getInstance().warning("backspace not allowed in direct text processing");
                    CerberusRegistry.getInstance().warning("backspace escape char will be ignored!");
                    break;
                case ESCAPE_HIGHLIGHT:
                    try {
                        FormatColor color = findColor(input, i);
                        i = color.pos;
                    } catch (TextFormatException e) {
                        CerberusRegistry.getInstance().fine("Illegal text formatting: " + e);
                    }
                    break;
                case ESCAPE_FORM_FEED:
                    break Loop;
                case ESCAPE_TAB:
                    pos.addSelf(spaceWidth * manager.tabInSpaces() + hspace, 0);
                    currentLetter++;
                    break;
                case SPACE:
                    pos.addSelf(spaceWidth + hspace, 0);
                    if (currentLineHeight < spaceHeight)
                        currentLineHeight = spaceHeight;
                    currentLetter++;
                    break;
                default:
                    CFXCharacter cfxChar = loadCharacter(input.charAt(i), font);
                    if (cfxChar == null)
                        continue; // could not load charater

                    // adjust height for the current line
                    if (currentLineHeight < cfxChar.getHeight())
                        currentLineHeight = cfxChar.getHeight();

                    if (packing || cfxChar.getWidth() > spaceWidth)
                        pos.addSelf(cfxChar.getWidth() + hspace, 0);
                    else
                        pos.addSelf(spaceWidth + hspace, 0);
                    currentLetter++;
            }

            if (line == currentLine && letter == currentLetter)
                return pos;
            if (currentLine > line)
                return null;
        }
        return null;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public @Nullable Vector2i getTextPosition(@NotNull String input, @NotNull Font font, int hspace, int vspace,
                                              boolean packing, @NotNull Vector2i pixelPos) {

        CFXManager manager = getRenderer().getGUIManager();

        Vector2i pos = new Vector2i(0);
        Vector2i prevPos = new Vector2i(0);
        int currentLineHeight = font.getSize();

        Rectangle2D spaceBounds = getSpaceBounds(font);
        int spaceWidth = (int) Math.ceil(spaceBounds.getWidth());
        int spaceHeight = (int) Math.ceil(spaceBounds.getHeight());

        int currentLine = 0;
        int currentLetter = 0;

        int prevLine = 0;
        int prevLetter = 0;

        if (prevPos.getY() < pixelPos.getY() && pos.getY() >= pixelPos.getY())
            return new Vector2i(prevLine, prevLetter);
        if (prevPos.getX() < pixelPos.getX() && pos.getX() >= pixelPos.getX() &&
                pos.getY() < pixelPos.getY() && pos.getY() + currentLineHeight >= pixelPos.getY())
            return new Vector2i(currentLine, prevLetter);

        Loop : for (int i = 0; i < input.length(); i++) {
            prevPos = pos.copy();

            prevLine = currentLine;
            prevLetter = currentLetter;

            switch (input.charAt(i)) {
                case ESCAPE_RETURN:
                    CerberusRegistry.getInstance().warning("return is not allowed in direct text processing");
                    CerberusRegistry.getInstance().warning("return escape char will be ignored!");
                    break;
                case ESCAPE_NEW_LINE:
                    pos.setX(0);
                    pos.addSelf(0, currentLineHeight + vspace);
                    currentLineHeight = font.getSize();
                    currentLine++;
                    currentLetter = 0;
                    break;
                case ESCAPE_BACKSPACE:
                    CerberusRegistry.getInstance().warning("backspace not allowed in direct text processing");
                    CerberusRegistry.getInstance().warning("backspace escape char will be ignored!");
                    break;
                case ESCAPE_HIGHLIGHT:
                    try {
                        FormatColor color = findColor(input, i);
                        i = color.pos;
                    } catch (TextFormatException e) {
                        CerberusRegistry.getInstance().fine("Illegal text formatting: " + e);
                    }
                    break;
                case ESCAPE_FORM_FEED:
                    break Loop;
                case ESCAPE_TAB:
                    pos.addSelf(spaceWidth * manager.tabInSpaces() + hspace, 0);
                    currentLetter++;
                    break;
                case SPACE:
                    pos.addSelf(spaceWidth + hspace, 0);
                    if (currentLineHeight < spaceHeight)
                        currentLineHeight = spaceHeight;
                    currentLetter++;
                    break;
                default:
                    CFXCharacter cfxChar = loadCharacter(input.charAt(i), font);
                    if (cfxChar == null)
                        continue; // could not load charater

                    // adjust height for the current line
                    if (currentLineHeight < cfxChar.getHeight())
                        currentLineHeight = cfxChar.getHeight();

                    if (packing || cfxChar.getWidth() > spaceWidth)
                        pos.addSelf(cfxChar.getWidth() + hspace, 0);
                    else
                        pos.addSelf(spaceWidth + hspace, 0);
                    currentLetter++;
            }

            if (prevPos.getY() < pixelPos.getY() && pos.getY() >= pixelPos.getY())
                return new Vector2i(prevLine, prevLetter);
            if (prevPos.getX() < pixelPos.getX() && pos.getX() >= pixelPos.getX() &&
                    pos.getY() < pixelPos.getY() && pos.getY() + currentLineHeight >= pixelPos.getY())
                return new Vector2i(currentLine, prevLetter);
        }
        return new Vector2i(currentLine, currentLetter);
    }

    @Override
    public void moveCaret(@NotNull CFXCaret caret, @NotNull CFXDocument doc, int hspace, int vspace, boolean packing,
                          boolean direction) {

        // TODO implement backwards caret movement

        if (caret.getLast() == -1) {
            // move only the main caret position
            if (caret.getFirst() >= doc.size())
                return;

            CharSequence line = doc.getLine(caret.getFirst());
            if (line.length() >= caret.getStart())
                return;

            switch (line.charAt(caret.getStart())) {
                case ESCAPE_RETURN:
                    CerberusRegistry.getInstance().warning("return is not allowed in direct text processing");
                    CerberusRegistry.getInstance().warning("return escape char will be ignored!");
                    break;
                case ESCAPE_NEW_LINE:
                    if (caret.getFirst() + 1 >= doc.size())
                        return;

                    caret.setFirst(caret.getFirst() + 1);
                    caret.setStart(0);
                    break;
                case ESCAPE_BACKSPACE:
                    CerberusRegistry.getInstance().warning("backspace not allowed in direct text processing");
                    CerberusRegistry.getInstance().warning("backspace escape char will be ignored!");
                    break;
                case ESCAPE_HIGHLIGHT:
                    try {
                        FormatColor color = findColor("" + line, caret.getStart());
                        caret.setStart(color.pos + 1);
                    } catch (TextFormatException e) {
                        CerberusRegistry.getInstance().fine("Illegal text formatting: " + e);
                    }
                    break;
                case ESCAPE_FORM_FEED:
                    break;
                case ESCAPE_TAB:
                case SPACE:
                default:
                    if (caret.getStart() + 1 >= line.length())
                        return;
                    caret.setStart(caret.getStart() + 1);
                    break;
            }
        } else {
            // move the back end of the caret block
            if (caret.getLast() >= doc.size())
                return;

            CharSequence line = doc.getLine(caret.getLast());
            if (line.length() >= caret.getEnd())
                return;

            switch (line.charAt(caret.getEnd())) {
                case ESCAPE_RETURN:
                    CerberusRegistry.getInstance().warning("return is not allowed in direct text processing");
                    CerberusRegistry.getInstance().warning("return escape char will be ignored!");
                    break;
                case ESCAPE_NEW_LINE:
                    if (caret.getLast() + 1 >= doc.size())
                        return;

                    caret.setLast(caret.getLast() + 1);
                    caret.setEnd(0);
                    break;
                case ESCAPE_BACKSPACE:
                    CerberusRegistry.getInstance().warning("backspace not allowed in direct text processing");
                    CerberusRegistry.getInstance().warning("backspace escape char will be ignored!");
                    break;
                case ESCAPE_HIGHLIGHT:
                    try {
                        FormatColor color = findColor("" + line, caret.getEnd());
                        caret.setEnd(color.pos + 1);
                    } catch (TextFormatException e) {
                        CerberusRegistry.getInstance().fine("Illegal text formatting: " + e);
                    }
                    break;
                case ESCAPE_FORM_FEED:
                    break;
                case ESCAPE_TAB:
                case SPACE:
                default:
                    if (caret.getEnd() + 1 >= line.length())
                        return;
                    caret.setEnd(caret.getEnd() + 1);
                    break;
            }
        }
    }

    @Override
    public @NotNull String wrap(@NotNull String input, @NotNull Font font, @NotNull Vector2i bounds, int hspace,
                                int vspace, boolean packing, boolean wordWrap) {
        return wrap(input, font, bounds, hspace, vspace, packing, wordWrap, ESCAPE_NEW_LINE);
    }

    @Override
    public @NotNull String wrap(@NotNull String input, @NotNull Font font, @NotNull Vector2i bounds, int hspace,
                                int vspace, boolean packing, boolean wordWrap, char newLineEscape) {

        CFXManager manager = getRenderer().getGUIManager();
        StringBuilder builder = new StringBuilder();
        StringBuilder line = new StringBuilder();

        Vector2i pos = new Vector2i(0);
        boolean inWord = false;
        int lastWordStart = 0;
        int currentLineHeight = font.getSize();

        Rectangle2D spaceBounds = getSpaceBounds(font);
        int spaceWidth = (int) Math.ceil(spaceBounds.getWidth());
        int spaceHeight = (int) Math.ceil(spaceBounds.getHeight());

        Loop : for (int i = 0; i < input.length(); i++) {
            switch (input.charAt(i)) {
                case ESCAPE_RETURN:
                    CerberusRegistry.getInstance().warning("return is not allowed in direct text processing");
                    CerberusRegistry.getInstance().warning("return escape char will be ignored!");
                    break;
                case ESCAPE_NEW_LINE:
                    builder.append(line.toString());
                    builder.append(ESCAPE_NEW_LINE);
                    line = new StringBuilder();
                    inWord = false;

                    pos.setX(0);
                    pos.addSelf(0, currentLineHeight + vspace);
                    currentLineHeight = font.getSize();
                    break;
                case ESCAPE_BACKSPACE:
                    CerberusRegistry.getInstance().warning("backspace not allowed in direct text processing");
                    CerberusRegistry.getInstance().warning("backspace escape char will be ignored!");
                    break;
                case ESCAPE_HIGHLIGHT:
                    try {
                        FormatColor color = findColor(input, i);
                        i = color.pos;
                        builder.append("\u001b[#").append(CFXColor.toHexColor(color.color, null));
                    } catch (TextFormatException e) {
                        CerberusRegistry.getInstance().fine("Illegal text formatting: " + e);
                    }
                    break;
                case ESCAPE_FORM_FEED:
                    break Loop;
                case ESCAPE_TAB:
                    if (inWord) {
                        builder.append(line.toString());
                        line = new StringBuilder();
                        inWord = false;
                    }

                    pos.addSelf(spaceWidth * manager.tabInSpaces() + hspace, 0);
                    if (pos.getX() >= bounds.getX()) {
                        // insert new line
                        builder.append(line.toString());
                        builder.append(newLineEscape);
                        line = new StringBuilder();

                        pos.setX(spaceWidth * manager.tabInSpaces() + hspace);
                        pos.addSelf(0, currentLineHeight + vspace);
                        currentLineHeight = font.getSize();
                    }
                    builder.append(ESCAPE_TAB);

                    break;
                case SPACE:
                    if (inWord) {
                        builder.append(line.toString());
                        line = new StringBuilder();
                        inWord = false;
                    }

                    pos.addSelf(spaceWidth + hspace, 0);
                    if (pos.getX() < bounds.getX()) {
                        if (currentLineHeight < spaceHeight)
                            currentLineHeight = spaceHeight;
                    } else {
                        // insert new line
                        builder.append(line.toString());
                        builder.append(newLineEscape);
                        line = new StringBuilder();

                        pos.setX(spaceWidth + hspace);
                        pos.addSelf(0, currentLineHeight + vspace);
                        currentLineHeight = Math.max(spaceHeight, font.getSize());
                    }
                    builder.append(SPACE);
                    break;
                default:
                    if (!inWord) {
                        inWord = true;
                        lastWordStart = pos.getX();
                    }

                    CFXCharacter cfxChar = loadCharacter(input.charAt(i), font);
                    if (cfxChar == null)
                        continue; // could not load character

                    // adjust height for the current line
                    if (currentLineHeight < cfxChar.getHeight())
                        currentLineHeight = cfxChar.getHeight();

                    int add;
                    if (packing || cfxChar.getWidth() > spaceWidth)
                        add = cfxChar.getWidth() + hspace;
                    else
                        add = spaceWidth + hspace;

                    pos.addSelf(add, 0);
                    if (pos.getX() >= bounds.getX()) {
                        if (lastWordStart == 0 || !wordWrap) {
                            // word is to long. separate word
                            builder.append(line.toString());
                            line = new StringBuilder();
                            pos.setX(add);
                            lastWordStart = 0;
                        }
                        builder.append(newLineEscape);
                        // next line
                        pos.addSelf(-lastWordStart, currentLineHeight + vspace);
                        lastWordStart = 0;
                    }
                    line.append(cfxChar.getCharacter());
            }
        }
        builder.append(line.toString());
        return builder.toString();
    }

    @Override
    public @Nullable CFXTextRenderContext formatTextBuffer(@NotNull SSBOResource ssbo, @NotNull String input,
                                                           @NotNull Font font, int hspace, int vspace) {
        CFXTextBuffer textBuffer = formatTextBuffer(input, font, hspace, vspace);
        if (textBuffer != null)
            return formatTextBuffer(textBuffer, ssbo);
        return null;
    }

    @Override
    public long calcByteSize(@NotNull String input) {
        int count = 0;
        int lineCount = 0;

        for (int i = 0; i < input.length(); i++)  {
            switch (input.charAt(i)) {
                case ESCAPE_RETURN:
                    lineCount = 0;
                    break;
                case ESCAPE_NEW_LINE:
                    count += lineCount;
                    break;
                case ESCAPE_BACKSPACE:
                    lineCount--;
                    break;
                case ESCAPE_HIGHLIGHT:
                    try {
                        FormatColor color = findColor(input, i);
                        i = color.pos;
                    } catch (TextFormatException e) {
                        CerberusRegistry.getInstance().fine("Illegal text formatting: " + e);
                    }
                case ESCAPE_FORM_FEED: // fall through
                case ESCAPE_TAB:
                    break;
                default:
                    lineCount++;
            }
        }
        return (count + lineCount) * TEXT_BUFFER_SYMBOL_SIZE;
    }

    @Override
    public void destroy() {
        alphabets.forEach(CFXAlphabet::destroy);
        alphabets.clear();
        chars.clear();
    }

    private FormatColor findColor(String input, int pos) throws TextFormatException {
        if (input.charAt(pos) == '\u001B') {
            int remaining = input.length() - pos;
            if (remaining > 2 && input.charAt(pos + 1) == '[') {

                final String msg = "Illegal color format: " + input.substring(pos, pos + 3);
                if (input.charAt(pos + 2) == '0') {
                    if (input.charAt(pos + 3) == 'm') {
                        return new FormatColor(CFXColor.WHITE.getDiffuse(), pos + 3);
                    }
                    throw new TextFormatException(msg);
                }

                if (remaining == 3)
                    throw new TextFormatException(msg);

                if (input.charAt(pos + 2) == '3' && input.charAt(pos + 4) == 'm') {
                    if (input.charAt(pos + 3) == '0')
                        return new FormatColor(CFXColor.BLACK.getDiffuse(), pos + 4);
                    if (input.charAt(pos + 3) == '1')
                        return new FormatColor(CFXColor.RED.getDiffuse(), pos + 4);
                    if (input.charAt(pos + 3) == '2')
                        return new FormatColor(CFXColor.GREEN.getDiffuse(), pos + 4);
                    if (input.charAt(pos + 3) == '3')
                        return new FormatColor(CFXColor.YELLOW.getDiffuse(), pos + 4);
                    if (input.charAt(pos + 3) == '4')
                        return new FormatColor(CFXColor.BLUE.getDiffuse(), pos + 4);
                    if (input.charAt(pos + 3) == '5')
                        return new FormatColor(CFXColor.PURPLE.getDiffuse(), pos + 4);
                    if (input.charAt(pos + 3) == '6')
                        return new FormatColor(CFXColor.CYAN.getDiffuse(), pos + 4);
                    if (input.charAt(pos + 3) == '7')
                        return new FormatColor(CFXColor.WHITE.getDiffuse(), pos + 4);

                    throw new TextFormatException("Illegal color format: " + input.substring(pos, pos + 5));
                }

                if (remaining > 7 && input.charAt(pos + 2) == '#') {

                    try {
                        int rawBits = Integer.parseUnsignedInt(input.substring(pos + 3, pos + 9), 16);

                        float red = (float) ((rawBits >> 16) & 0xFF) / 255f;
                        float green = (float) ((rawBits >> 8) & 0xFF) / 255f;
                        float blue = (float) (rawBits & 0xFF) / 255f;

                        return new FormatColor(new Vector3f(red, green, blue), pos + 8);
                    } catch (NumberFormatException e) {
                        CerberusRegistry.getInstance().warning("Failed to interpret color: "
                                + input.substring(pos + 1, pos + 9) + " - not in radix 16!");
                        CerberusRegistry.getInstance().getService(CerberusEvent.class)
                                .executeFullEIF(new ExceptionEvent(CerberusRenderer.class, e));
                    }
                }
            }
        }
        return new FormatColor(CFXColor.WHITE.getDiffuse(), pos);
    }

    /**
     * Will return the bounds of the space bar for the specified font.
     * @param font font
     * @return bounding rectangle for the space bar
     */
    private @NotNull Rectangle2D getSpaceBounds(@NotNull Font font) {
        FontRenderContext renderContext = new FontRenderContext(font.getTransform(), true, true);
        return font.getStringBounds(" ", renderContext);
    }

    /**
     * Will find the alphabet with the smallest cell size that is still greater
     * than the size of the texture which should be inserted into the alphabet.
     *
     * If there is not appropriate CFX-Alphabet currently registered, this method
     * will create a new one.
     * @param textureSize size of the texture to insert in pixels
     * @param font font of the char to insert
     * @return alphabet
     */
    private @NotNull CFXAlphabet findAppropriateAlphabet(Vector2i textureSize, Font font) {
        for (CFXAlphabet alphabet : alphabets) {
            if (alphabet.getCellSize().getX() < textureSize.getX()
                    || alphabet.getCellSize().getY() < textureSize.getY())
                continue;

            if (!alphabet.isFull())
                return alphabet;
        }

        // create new texture atlas
        Vector2i maxSize = maxBounds(font);
        CerberusRegistry.getInstance().fine("Could not find CFX alphabet with free space" +
                " for a character of size " + textureSize + ". Creating new one with cell" +
                " size " + maxSize);

        return createAlphabet(maxSize);
    }

    /**
     * Will create a new alphabet with the specified texture size.
     *
     * This method should only be called from within a valid gl
     * render context. (no-check)
     */
    private @NotNull CFXAlphabet createAlphabet(Vector2i textureSize) {
        CFXAlphabet alphabet = new CFXAlphabetImpl(textureSize);
        alphabet.init();

        // insert alphabet
        for (int i = 0; i < alphabets.size(); i++) {
            CFXAlphabet other = alphabets.get(i);
            if (other.getCellSize().getX() >= textureSize.getX()
                    && other.getCellSize().getY() >= textureSize.getY()) {
                alphabets.add(i, alphabet);
                return alphabet;
            }
        }
        alphabets.add(alphabet);
        return alphabet;
    }

    /**
     * Will generate a buffered image from a character an a font.
     *
     * The image will be scaled according to the rendered characters
     * glyph size.
     *
     * @param c character to render
     * @param font font to render the character with
     * @return character rendered to a buffered image
     */
    private static BufferedImage generateImage(char c, Font font) {
        FontRenderContext renderContext = new FontRenderContext(font.getTransform(), true, true);
        Rectangle2D charBounds = font.getStringBounds(Character.toString(c), renderContext);

        int width = (int) Math.ceil(charBounds.getWidth());
        int height = (int) Math.ceil(charBounds.getHeight());

        if (width == 0)
            return null;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.setFont(font);

        Map<?, ?> desktopHints = (Map<?, ?>) Toolkit.getDefaultToolkit()
                .getDesktopProperty("awt.font.desktophints");
        if (desktopHints == null) {
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                    RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        } else
            graphics.setRenderingHints(desktopHints);

        graphics.drawString(Character.toString(c), (float) charBounds.getX(), -(float) charBounds.getY());
        return image;
    }

    /**
     * Will check the font size against a specified standard char set
     * of the roman alphabet (width a few extras) and compute the maximum
     * character size in pixels for the font.
     * @param font font
     * @return max character size in pixels
     */
    private static Vector2i maxBounds(@NotNull Font font) {
        int width = 0;
        int height = 0;

        FontRenderContext renderContext = new FontRenderContext(font.getTransform(), true, true);
        for (char c : DEFAULT_CHARACTER_SET) {
            Rectangle2D charBounds = font.getStringBounds(Character.toString(c), renderContext);
            // CerberusRegistry.getInstance().debug("Char " + c + " size: x = " + charBounds.getWidth() + ", y = " + charBounds.getHeight());

            if (charBounds.getWidth() > width)
                width = (int) Math.ceil(charBounds.getWidth());
            if (charBounds.getHeight() > height)
                height = (int) Math.ceil(charBounds.getHeight());
        }

        return new Vector2i(width, height);
    }

    /**
     * Returns the current renderer instance
     * @return Cerberus renderer
     */
    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }

    /**
     * Format color struct class
     */
    private static class FormatColor {

        private final Vector3f color;
        private final int pos;

        public FormatColor(Vector3f color, int pos) {
            this.color = color;
            this.pos = pos;
        }
    }

    /**
     * Symbol struct class
     */
    private static class Symbol {

        private final Vector2i coord;
        private final CFXCharacter character;
        private final int argb;

        public Symbol(Vector2i coord, CFXCharacter character, int argb) {
            this.coord = coord;
            this.character = character;
            this.argb = argb;
        }
    }
}
