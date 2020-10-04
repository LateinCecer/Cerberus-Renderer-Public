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
import com.cerberustek.buffer.BufferUsage;
import com.cerberustek.event.Event;
import com.cerberustek.event.EventHandler;
import com.cerberustek.event.EventListener;
import com.cerberustek.events.CharEvent;
import com.cerberustek.events.KeyEvent;
import com.cerberustek.exceptions.EndOfDocumentException;
import com.cerberustek.exceptions.IllegalContextException;
import com.cerberustek.gui.*;
import com.cerberustek.gui.impl.event.CFXClickEvent;
import com.cerberustek.input.KeyAction;
import com.cerberustek.input.KeyBinding;
import com.cerberustek.input.impl.CharInput;
import com.cerberustek.logic.math.Vector2f;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.resource.impl.StaticByteBufferResource;
import com.cerberustek.resource.shader.SSBOResource;
import com.cerberustek.resource.shader.impl.MutableSSBOResource;
import com.cerberustek.CerberusRenderer;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.nio.ByteBuffer;

public class CFXTextField extends CFXPane {

    /** SSBO buffer containing the charaters to render, plus
     * their individual position relative to the base canvas
     * and RGBA color information */
    private final SSBOResource textBuffer;
    /** Local reference to the current cerberus renderer
     * for faster lookup times during rendering */
    private CerberusRenderer renderer;
    /** The render context for text rendering */
    private CFXTextRenderContext renderContext;
    /** The orientation of the CFX Text pane */
    private CFXOrientation orientation;
    /** The Font for text rendering */
    private Font font;
    /** Register id */
    private int id;

    /** vertical spacing between lines */
    private int vspace;
    /** horizontal spacing between letters */
    private int hspace;
    /** should text highlights be formatted? */
    private boolean doHighlighting;
    /** should the text be tightly packed? */
    private boolean tightPacking;
    /** should wrap text to canvas bounds */
    private boolean wrapping;
    /** should wrap text aground words */
    private boolean wordWrapping;

    /** the document containing the text */
    private CFXDocument document;
    /** the main caret */
    private CFXSimpleCaret caret;
    /** the main caret renderer */
    private CFXCaretRenderer caretRenderer;
    /** scroll amount in rows/coulomb */
    private Vector2f scroll;

    private boolean hasChanged;
    private boolean isFocused;

    public CFXTextField(@NotNull CFXOrientation orientation, @NotNull Vector2i initialSize, @NotNull Font font) {
        super(new FlatCanvas(initialSize));
        getRenderer().tryGLTask(t -> getCanvas().init());

        this.orientation = orientation;
        this.vspace = 0;
        this.hspace = 0;
        this.doHighlighting = false;
        this.tightPacking = true;
        this.wrapping = false;
        this.wordWrapping = false;
        this.font = font;
        this.isFocused = true;
        this.document = new CFXEditorDocument();
        this.caret = new CFXSimpleCaret(0, 13);
        this.caretRenderer = new CFXSimpleCaretRenderer(caret, this);
        this.scroll = new Vector2f(0, 0);

        ByteBuffer buffer = BufferUtils.createByteBuffer(0);
        textBuffer = new MutableSSBOResource(new StaticByteBufferResource(buffer), BufferUsage.DYNAMIC_READ,
                new long[] {}, new int[] {CFXFontRenderer.TEXT_BUFFER_BINDING});
        getRenderer().getShaderBoard().loadSSBO(textBuffer);

        CerberusEvent eventService = CerberusRegistry.getInstance().getService(CerberusEvent.class);
        eventService.addListener(new KeyListener());
        eventService.addListener(new CharListener());
    }

    @Override
    public void paintToCanvas() {
        if (!getRenderer().getWindow().isGlThread())
            throw new IllegalContextException();

        if (hasChanged) {
            if (!populateTextBuffer())
                CerberusRegistry.getInstance().warning("Failed to populate" +
                        " text buffer for text field " + toString());
        }

        if (renderContext == null)
            return; // nothing to draw

        getCanvas().clear();
        getCanvas().drawString(renderContext);
    }

    @Override
    public void paintComponent(@NotNull CFXCanvas canvas) {
        super.paintComponent(canvas);
        renderCaret(canvas);
    }

    @Override
    public @NotNull CFXOrientation getOrientation() {
        return orientation;
    }

    @Override
    public void setOrientation(@NotNull CFXOrientation orientation) {
        this.orientation = orientation;
    }

    @Override
    public void dispatchEvent(CFXEvent event) {
        if (event instanceof CFXClickEvent) {
            isFocused = true;
        }
    }

    private void renderCaret(CFXCanvas canvas) {
        Vector2i pixelPos = pixelPosition(caret.getFirst(), caret.getStart());
        if (pixelPos == null)
            return; // don't render the caret

        caretRenderer.render(pixelPos, canvas);
    }

    /**
     * Will format the viewable text string from the field document
     * and populate the text buffer from set string.
     *
     * This method will have to be called, every time the text that
     * is displayed by this component changes.
     * @return will populate the text buffer
     */
    private boolean populateTextBuffer() {
        CerberusRenderer renderer = getRenderer();
        CFXFontRenderer fontRenderer = renderer.getGUIManager().getFontRenderer();

        if (document.size() == 0) {
            this.renderContext = null;
            this.hasChanged = false;
            return true;
        }

        StringBuilder builder = new StringBuilder();
        for (int lineId = (int) scroll.getX(); lineId < document.size(); lineId++) {
            CharSequence line = document.getLine(lineId);
            builder.append(line)
                    .append(CFXFontRenderer.ESCAPE_NEW_LINE);
        }

        CFXTextRenderContext renderContext;
        if (wrapping)
            renderContext = fontRenderer.formatTextBuffer(textBuffer, fontRenderer.wrap(builder.toString(),
                    font, getResolution(), hspace, vspace, tightPacking, wordWrapping), font, hspace,
                    vspace, doHighlighting, tightPacking);
        else
            renderContext = fontRenderer.formatTextBuffer(textBuffer, builder.toString(),
                    font, hspace, vspace, doHighlighting, tightPacking);

        if (renderContext == null)
            return false;

        this.renderContext = renderContext;
        this.hasChanged = true;
        return true;
    }

    private Vector2i pixelPosition(int line, int letter) {
        StringBuilder builder = new StringBuilder();
        for (int lineId = (int) scroll.getX(); lineId < document.size(); lineId++) {
            builder.append(document.getLine(lineId))
                    .append(CFXFontRenderer.ESCAPE_NEW_LINE);
        }

        CFXFontRenderer fontRenderer = getRenderer().getGUIManager().getFontRenderer();
        if (wrapping)
            return fontRenderer.getTextPixel(fontRenderer.wrap(builder.toString(), font, getResolution(),
                    hspace, vspace, tightPacking, wordWrapping, CFXFontRenderer.FAKE_NEW_LINE), font, hspace, vspace,
                    tightPacking, line, letter);
        else
            return fontRenderer.getTextPixel(builder.toString(), font, hspace, vspace, tightPacking, line, letter);
    }

    @Override
    public void setResolution(Vector2i res) {
        super.setResolution(res);
        if (wrapping)
            hasChanged = true;
    }

    @Override
    public void register(int id) {
        this.id = id;
    }

    @Override
    public int id() {
        return id;
    }

    public @NotNull CFXDocument getDocument() {
        return document;
    }

    public void setDocument(@NotNull CFXDocument doc) {
        if (!this.document.equals(doc)) {
            this.document = doc;
            hasChanged = true;
            requestRepaint();
        }
    }

    public void nextLetter() {
        caret.setStart(caret.getStart() + 1);
    }

    public void prevLetter() {
        int i = caret.getStart() - 1;
        if (i < 0)
            i = 0;
        caret.setStart(i);
    }

    public void nextLine() {
        int i = caret.getFirst();
        caret.setFirst(i + 1);
    }

    public void prevLine() {
        int i = caret.getFirst() - 1;
        if (i < 0)
            i = 0;
        caret.setFirst(i);
    }

    /**
     * Returns the font that is used for rendering the text.
     * @return render font
     */
    public @NotNull Font getFont() {
        return font;
    }

    /**
     * Set's the font that should be used for font rendering.
     *
     * This method will set the plain text of the CFX Text
     * Pane and request an update for the actual image
     * texture.
     *
     * @param font font
     */
    public void setFont(@NotNull Font font) {
        if (!this.font.equals(font)) {
            this.font = font;
            hasChanged = true;
            requestRepaint();
        }
    }

    /**
     * Returns vertical spacing between lines in
     * pixels.
     * @return vertical spacing
     */
    public int getVerticalSpacing() {
        return vspace;
    }

    /**
     * Set's the vertical spacing between lines in
     * pixels.
     * @param vspace vertical spacing
     */
    public void setVerticalSpacing(int vspace) {
        this.vspace = vspace;
    }

    /**
     * Returns the horizontal spacing between letters in
     * pixels.
     * @return horizontal spacing
     */
    public int getHorizontalSpacing() {
        return hspace;
    }

    /**
     * Set's the horizontal spacing between letters in
     * pixels.
     * @param hspace horizontal spacing
     */
    public void setHorizontalSpacing(int hspace) {
        this.hspace = hspace;
    }

    /**
     * Returns true, if the text pane has text highlights
     * enabled.
     *
     * If this setting is enabled, information about text
     * highlights will be pulled directly from appropriate
     * escape characters in the input text.
     *
     * @return do text highlighting
     */
    public boolean doHighlighting() {
        return doHighlighting;
    }

    /**
     * Enables/disables text highlighting for the text
     * pane.
     * @param value value
     */
    public void enableHighlighting(boolean value) {
        this.doHighlighting = value;
    }

    /**
     * Should the letters in the text be tightly packed?
     * @return tightly packed
     */
    public boolean isTightlyPacked() {
        return tightPacking;
    }

    /**
     * Enables/disables tight text packing.
     * @param value tight packing
     */
    public void enableTightPacking(boolean value) {
        this.tightPacking = value;
    }

    /**
     * Returns if wrapping is enabled in for this text pane.
     *
     * In this case, wrapping means, that additional lines are
     * added to the text, so that the entire text can be
     * displayed.
     *
     * @return wrapping enabled
     */
    public boolean doesWrap() {
        return wrapping;
    }

    /**
     * Will enable/disable wrapping.
     *
     * In this case, wrapping means, that additional lines are
     * added to the text, so that the entire text can be
     * displayed.
     *
     * @param value enable/disable wrapping
     */
    public void enableWrapping(boolean value) {
        this.wrapping = value;
    }

    /**
     * Returns if only whole words should be wrapped if possible.
     *
     * If whole word wrapping is enabled, words will not be
     * separated by wrapping.
     *
     * @return is whole word wrapping enabled/disabled
     */
    public boolean doesWordWrap() {
        return wrapping && wordWrapping;
    }

    /**
     * Will enable/disable whohle word wrapping.
     *
     * If whole word wrapping is enabled, words will not be
     * separated by wrapping.
     * If this value is set to true, wrapping will be
     * enabled automatically.
     *
     * @param value enable/disable whole word wrapping
     */
    public void enableWordWrapping(boolean value) {
        if (value)
            wrapping = wordWrapping = true;
        else
            wordWrapping = false;
    }

    /**
     * Returns the current instance of the cerberus renderer.
     *
     * The reference to the renderer is stored locally for
     * faster lookup times during rendering.
     *
     * @return cerberus renderer instance
     */
    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }

    @EventHandler(events = {KeyEvent.class})
    private class KeyListener implements EventListener {

        @Override
        public boolean onEvent(com.cerberustek.event.Event event) {
            KeyEvent keyEvent = (KeyEvent) event;
            if (CFXTextField.this.isFocused && ((KeyEvent) event).getAction() != KeyAction.RELEASED.getActionId()) {
                CFXManager manager = getRenderer().getGUIManager();
                KeyBinding binding = manager.getKeyBinding(CFXKeyBinding.NEXT_LETTER);
                if (binding.containsKeyCode(keyEvent.getKey())) {
                    nextLetter();
                }
                binding = manager.getKeyBinding(CFXKeyBinding.PREV_LETTER);
                if (binding.containsKeyCode(keyEvent.getKey())) {
                    prevLetter();
                }
                binding = manager.getKeyBinding(CFXKeyBinding.NEXT_LINE);
                if (binding.containsKeyCode(keyEvent.getKey())) {
                    nextLine();
                }
                binding = manager.getKeyBinding(CFXKeyBinding.PREV_LINE);
                if (binding.containsKeyCode(keyEvent.getKey())) {
                    prevLine();
                }
                binding = manager.getKeyBinding(CFXKeyBinding.BACKSPACE);
                if (binding.containsKeyCode(keyEvent.getKey())) {
                    prevLetter();
                    int index = caret.getStart();
                    if (index >= 0) {
                        document.remove(caret.getFirst(), index, caret.getFirst(), index + 1);
                        requestRepaint();
                    }
                }
                binding = manager.getKeyBinding(CFXKeyBinding.ENTER);
                if (binding.containsKeyCode(keyEvent.getKey())) {
                    document.insert("\n", caret.getFirst(), caret.getStart());
                    caret.setStart(0);
                    nextLine();
                    requestRepaint();
                }
                return true;
            }
            return false;
        }
    }

    @EventHandler(events = {CharEvent.class})
    private class CharListener implements EventListener {

        @Override
        public boolean onEvent(Event event) {
            CharEvent charEvent = (CharEvent) event;
            if (CFXTextField.this.isFocused) {
                CharInput device = (CharInput) charEvent.getInputDevice();
                String string = device.pull();
                if (string != null && !string.isEmpty()) {

                    try {
                        document.insert(string, caret.getFirst(), caret.getStart());
                        nextLetter();
                        requestRepaint();
                    } catch (EndOfDocumentException ignore) {}
                }
                return true;
            }

            return false;
        }
    }
}
