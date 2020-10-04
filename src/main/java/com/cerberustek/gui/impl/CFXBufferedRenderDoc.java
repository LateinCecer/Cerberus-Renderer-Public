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
import com.cerberustek.events.ExceptionEvent;
import com.cerberustek.exceptions.EndOfDocumentException;
import com.cerberustek.gui.*;
import com.cerberustek.resource.impl.StaticByteBufferResource;
import com.cerberustek.resource.shader.SSBOResource;
import com.cerberustek.resource.shader.impl.MutableSSBOResource;
import com.cerberustek.shader.ShaderBoard;
import com.cerberustek.CerberusRenderer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class CFXBufferedRenderDoc implements CFXRenderDoc {

    private final ArrayList<CFXTextRenderContext> renderContexts = new ArrayList<>();

    private CFXDocument doc;
    private CerberusRenderer renderer;

    private Font font;
    private int vspace;
    private int hspace;
    private boolean highlight;
    private boolean tightPacking;

    public CFXBufferedRenderDoc(@NotNull Font font, int vspace, int hspace, boolean highlight, boolean tightPacking) {
        doc = new CFXEditorDocument();
        this.font = font;
        this.vspace = vspace;
        this.hspace = hspace;
        this.highlight = highlight;
        this.tightPacking = tightPacking;
    }

    @Override
    public CFXTextRenderContext getRenderContext(int lineId) {
        if (renderContexts.size() > lineId)
            return renderContexts.get(lineId);
        return null;
    }

    @Override
    public CFXDocument getDocument() {
        return doc;
    }

    @Override
    public void setDocument(CFXDocument doc) {
        if (doc instanceof CFXDocumentEditable) {
            this.doc = (CFXDocumentEditable) doc;
        } else
            throw new IllegalStateException("Document " + doc + " is not an editable document!");
    }

    /**
     * Will update the render context for the specified
     * line id.
     * @param lineId line id
     */
    private void updateLine(int lineId) {
        getRenderer().tryGLTask(t -> {
            CharSequence chars = doc.getLine(lineId);
            CFXManager guiManager = getRenderer().getGUIManager();
            CFXFontRenderer fontRenderer = guiManager.getFontRenderer();
            CFXTextRenderContext context = renderContexts.get(lineId);

            context = fontRenderer.formatTextBuffer(context.getBufferResource(), "" + chars, font, vspace, hspace, highlight, tightPacking);
            renderContexts.set(lineId, context);
        });
    }

    /**
     * Will update the render contexts for all lines.
     *
     * Note that this method will only update render
     * contexts which already exist. It will not add a
     * render context for a line that did not exist
     * previously.
     */
    private void updateAll() {
        getRenderer().tryGLTask(t -> {
            CFXManager guiManager = getRenderer().getGUIManager();
            CFXFontRenderer fontRenderer = guiManager.getFontRenderer();

            for (int i = 0; i < renderContexts.size(); i++) {
                CharSequence chars = doc.getLine(i);
                CFXTextRenderContext context = renderContexts.get(i);

                context = fontRenderer.formatTextBuffer(context.getBufferResource(), "" + chars, font,
                        vspace, hspace, highlight, tightPacking);
                renderContexts.set(i, context);
            }
        });
    }

    /**
     * Will delete all registered render contexts, clear the
     * buffers and refill the render context list with newly
     * populated buffers.
     *
     * This method will update all changes to the render doc,
     * but it is very costly, especially for large files.
     */
    private void rebuild() {
        getRenderer().tryGLTask(t -> {
            CFXManager guiManager = getRenderer().getGUIManager();
            CFXFontRenderer fontRenderer = guiManager.getFontRenderer();
            ShaderBoard shaderBoard = getRenderer().getShaderBoard();

            // clear all
            for (CFXTextRenderContext context : renderContexts)
                shaderBoard.deleteSSBO(context.getBufferResource());
            renderContexts.clear();

            // rebuild
            for (int i = 0; i < doc.size(); i++) {
                CharSequence chars = doc.getLine(i);
                CFXTextRenderContext context = formatInternalLineBuffer("" + chars, fontRenderer, i);
                if (context == null)
                    break;

                renderContexts.add(context);
            }
        });
    }

    /**
     * Will insert a render context similar to inserting
     * a line in a document.
     * @param lineId line id
     */
    private void insertLine(int lineId) {
        getRenderer().tryGLTask(t -> {
            CharSequence chars = doc.getLine(lineId);
            CFXManager guiManager = getRenderer().getGUIManager();
            CFXFontRenderer fontRenderer = guiManager.getFontRenderer();
            CFXTextRenderContext context = formatInternalLineBuffer("" + chars, fontRenderer, lineId);

            if (context != null)
                renderContexts.add(lineId, context);
        });
    }

    private CFXTextRenderContext formatInternalLineBuffer(String input, CFXFontRenderer fontRenderer, int lineId) {
        CFXTextBuffer textBuffer = fontRenderer.formatTextBuffer(input, font, vspace, hspace,
                highlight, tightPacking);
        if (textBuffer == null) {
            CerberusRegistry.getInstance().warning("Unable to format line " + lineId + "!");
            CerberusRegistry.getInstance().getService(CerberusEvent.class)
                    .executeFullEIF(new ExceptionEvent(CerberusRenderer.class, new NullPointerException()));
            return null;
        }

        ByteBuffer buf = textBuffer.getBuffer();
        int[] textBufferBindings = new int[textBuffer.size()];
        Arrays.fill(textBufferBindings, CFXFontRenderer.TEXT_BUFFER_BINDING);

        SSBOResource bufferResource = new MutableSSBOResource(new StaticByteBufferResource(buf),
                BufferUsage.STATIC_READ, textBuffer.cuts(), textBufferBindings);
        getRenderer().getShaderBoard().loadSSBO(bufferResource);
        return new CFXTextRenderContextImpl(textBuffer.alphabets(), bufferResource,
                textBuffer.charCounts(), textBuffer.getBounds());
    }

    /**
     * Will remove a line from the render contexts.
     * @param lineId line to remove
     */
    private void removeLine(int lineId) {
        CFXTextRenderContext context = renderContexts.get(lineId);
        if (context != null) {
            getRenderer().tryGLTask(t -> {
                getRenderer().getShaderBoard().deleteSSBO(context.getBufferResource());
            });
            renderContexts.remove(lineId);
        }
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
        int affected = doc.insert(input, first);
        updateLine(first);
        if (affected > 1) {
            for (int i = 1; i < affected; i++)
                insertLine(first + i);
        }
        return affected;
    }

    @Override
    public int insert(CharSequence input, int first, int start) throws EndOfDocumentException {
        int affected = doc.insert(input, first, start);
        updateLine(first);
        if (affected > 1) {
            for (int i = 1; i < affected; i++)
                insertLine(first + i);
        }
        return affected;
    }

    @Override
    public int insert(CharSequence input, int first, int start, int last, int end) throws EndOfDocumentException, ArrayIndexOutOfBoundsException {
        int affected = doc.insert(input, first, start, last, end);
        updateLine(first);
        if (first != last) {
            updateLine(last);
            if (affected > 2) {
                for (int i = 1; i < affected - 1; i++)
                    insertLine(first + i);
            }
        } else if (affected > 1) {
            for (int i = 1; i < affected; i++)
                insertLine(first + i);
        }
        return affected;
    }

    @Override
    public int set(CharSequence input, int first, int start) throws EndOfDocumentException {
        int affected = doc.set(input, first, start);
        return affected;
    }

    @Override
    public int set(CharSequence input, int first, int start, int last, int end) throws EndOfDocumentException, ArrayIndexOutOfBoundsException {
        int affected = doc.set(input, first, start, last, end);
        return affected;
    }

    @Override
    public int remove(int first, int start, int last, int end) throws EndOfDocumentException {
        int affected = doc.remove(first, start, last, end);
        return affected;
    }

    @Override
    public int size() {
        return doc.size();
    }

    /**
     * Returns the font used to render the text document.
     * @return font
     */
    public @NotNull Font getFont() {
        return font;
    }

    /**
     * Sets the render font and update all render contexts.
     *
     * This method will trigger the document render context
     * to rebuild.
     *
     * @param font render font
     */
    public void setFont(@NotNull Font font) {
        if (!this.font.equals(font)) {
            this.font = font;
            rebuild();
        }
    }

    /**
     * Returns the vertical spacing in pixels.
     * @return vertical spacing
     */
    public int getVerticalSpacing() {
        return vspace;
    }

    /**
     * Sets the vertical spacing in pixels.
     *
     * This will trigger the document render context to
     * rebuild.
     *
     * @param vspace vertical spacing in pixels
     */
    public void setVerticalSpacing(int vspace) {
        if (this.vspace != vspace) {
            this.vspace = vspace;
            rebuild();
        }
    }

    /**
     * Returns the horizontal spacing between letters
     * in pixels.
     * @return horizontal spacing
     */
    public int getHorizontalSpacing() {
        return hspace;
    }

    /**
     * Sets the horizontal spacing in pixels.
     *
     * This will trigger the document render context to
     * rebuild.
     *
     * @param hspace horizontal spacing in pixels
     */
    public void setHorizontalSpacing(int hspace) {
        if (this.hspace != hspace) {
            this.hspace = hspace;
            rebuild();
        }
    }

    /**
     * Returns true, if color highlighting is enabled for
     * this document.
     * @return color highlighting
     */
    public boolean getHighlighting() {
        return highlight;
    }

    /**
     * Enables/Disables color highlighting for the text buffer.
     *
     * This will trigger the document render context to
     * rebuild.
     *
     * @param highlight color highlighting
     */
    public void enableHighlighting(boolean highlight) {
        if (this.highlight != highlight) {
            this.highlight = highlight;
            rebuild();
        }
    }

    /**
     * Returns if the render doc should tightly pack letters.
     * @return tight packing
     */
    public boolean isTightPacking() {
        return tightPacking;
    }

    /**
     * Enables/Disables tight packing for letters.
     * @param value tight packing
     */
    public void enableTightPacking(boolean value) {
        if (this.tightPacking != value) {
            this.tightPacking = value;
            rebuild();
        }
    }

    /**
     * Returns the current cerberus render instance.
     * @return render instance
     */
    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }
}
