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

import com.cerberustek.CerberusRegistry;
import com.cerberustek.buffer.BufferUsage;
import com.cerberustek.exceptions.IllegalContextException;
import com.cerberustek.gui.*;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.resource.impl.StaticByteBufferResource;
import com.cerberustek.resource.shader.SSBOResource;
import com.cerberustek.resource.shader.impl.MutableSSBOResource;
import com.cerberustek.CerberusRenderer;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.nio.ByteBuffer;

/**
 * The CFX Text Pane is a simple text area.
 *
 * The contents of the text pane cannot be edited
 * by the user.
 * It inherits CFX Pane and draws the text information
 * directly to the internal CFX Canvas. A refresh of
 * the text buffer can be achieved through 'repaint()'.
 */
public class CFXTextPane extends CFXPane {

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

    /** The string to draw as text */
    private String text;
    /** Is true, if the text has been changed since the
     * last buffer update */
    boolean hasChanged = false;

    public CFXTextPane(@NotNull CFXOrientation orientation, @NotNull Vector2i initialSize, @NotNull Font font) {
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
        this.text = "";

        ByteBuffer buffer = BufferUtils.createByteBuffer(0);
        textBuffer = new MutableSSBOResource(new StaticByteBufferResource(buffer), BufferUsage.DYNAMIC_READ,
                new long[] {}, new int[] {CFXFontRenderer.TEXT_BUFFER_BINDING});
        getRenderer().getShaderBoard().loadSSBO(textBuffer);
    }

    @Override
    public void paintToCanvas() {
        if (!getRenderer().getWindow().isGlThread())
            throw new IllegalContextException();

        if (hasChanged) {
            if (!populateTextBuffer(text))
                CerberusRegistry.getInstance().warning("Failed to format text buffer" +
                        " for text pane " + toString());
        }

        if (renderContext == null)
            return; // nothing to draw

        getCanvas().clear();
        getCanvas().drawString(renderContext);
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

    }

    @Override
    public void register(int id) {
        this.id = id;
    }

    @Override
    public int id() {
        return id;
    }

    /**
     * Will populate the text rendering buffer.
     *
     * This method has to be called every time the text of
     * the text pane has been changed, because the render
     * buffer contains the actual information about the
     * symbols that get rendered as text.
     *
     * @param text string text to populate the render buffer
     *             with
     * @return CFX text render context
     */
    private boolean populateTextBuffer(@NotNull String text) {
        CerberusRenderer renderer = getRenderer();
        CFXFontRenderer fontRenderer = renderer.getGUIManager().getFontRenderer();

        CFXTextRenderContext renderContext;
        if (wrapping) {
            renderContext = fontRenderer.formatTextBuffer(textBuffer, fontRenderer.wrap(text, font, getResolution(),
                    hspace, vspace, tightPacking, wordWrapping), font, hspace, vspace, doHighlighting, tightPacking);
        } else {
            renderContext = fontRenderer.formatTextBuffer(textBuffer, text,
                    font, hspace, vspace, doHighlighting, tightPacking);
        }

        if (renderContext == null)
            return false;

        this.renderContext = renderContext;
        this.hasChanged = false;
        return true;
    }

    @Override
    public void setResolution(Vector2i res) {
        super.setResolution(res);
        if (wrapping)
            hasChanged = true;
    }

    /**
     * Returns the text that get's rendered as a string.
     *
     * This string may return escape characters as well
     * as color highlight information.
     *
     * @return text
     */
    public @NotNull String getText() {
        return text;
    }

    /**
     * Set's the text that should get rendered.
     *
     * This method will set the plain text of the CFX Text
     * Pane and request an update for the actual image
     * texture.
     *
     * @param text text
     */
    public void setText(@NotNull String text) {
        if (!this.text.equals(text)) {
            this.text = text;
            hasChanged = true;
            requestRepaint();
        }
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
     * Will return the current cerberus renderer instance.
     * @return cerberus renderer
     */
    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }
}
