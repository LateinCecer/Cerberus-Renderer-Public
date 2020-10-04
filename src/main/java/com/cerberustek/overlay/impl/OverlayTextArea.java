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

package com.cerberustek.overlay.impl;

import com.cerberustek.CerberusEvent;
import com.cerberustek.events.ExceptionEvent;
import com.cerberustek.geometry.DrawMode;
import com.cerberustek.logic.math.Vector2f;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.logic.math.Vector3f;
import com.cerberustek.resource.image.TextureResource;
import com.cerberustek.resource.impl.FrameBufferResource;
import com.cerberustek.resource.model.ModelResource;
import com.cerberustek.texture.AttachmentType;
import com.cerberustek.texture.ImageType;
import com.cerberustek.texture.TextureBoard;
import com.cerberustek.texture.impl.SimpleAttachment;
import com.cerberustek.texture.impl.TextureEmpty2D;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.overlay.OverlayFrame;
import com.cerberustek.overlay.OverlayInteraction;
import com.cerberustek.overlay.OverlayUtil;
import com.cerberustek.overlay.font.CharResource;
import com.cerberustek.overlay.font.RenderFont;
import com.cerberustek.util.RenderUtil;

import static org.lwjgl.opengl.GL11.*;

public class OverlayTextArea extends OverlayFrameBase implements OverlayFrame {

    private RenderFont font;
    private String text = "";
    private Vector2i resolution;
    private boolean wrap;
    private boolean useColorCodes;
    private CerberusRenderer renderer;
    private float Xspaceing = 0f;
    private float Yspacing = 0f;

    private FrameBufferResource frameBuffer;

    public OverlayTextArea(OverlayFrame parent, Vector2f size) {
        super(parent, size);
        Vector2i frameSize = parent.getResolution();
        setResolution(frameSize.toVector2f().mul(absolutSize()).toVec2i());
    }

    public OverlayTextArea(OverlayFrame parent, Vector2f size, boolean enabled, boolean visible, Vector2i resolution) {
        super(parent, size, enabled, visible, parent.getGuiId());
        setResolution(resolution);
    }

    @Override
    public void interact(OverlayInteraction interaction) {

    }

    @Override
    public void update(double t) {
        CerberusRenderer renderer = getRenderer();
        // float x = resolution.getX() / 2;
        float x = 0;
        float y = 0;

        float increaseY = 0;

        Vector3f color = new Vector3f(1, 1, 1);
        renderer.getTextureBoard().bindFrameBuffer(frameBuffer);
        RenderUtil.clear();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            int remaining = text.length() - i - 1;

            if (c == '\n') {
                x = 0;
                y += Yspacing + increaseY;
                continue;
            } else if (c == '\t') {
                x += 4 * Xspaceing;
                continue;
            } else if (c == '\u001B' && useColorCodes) {
                if (remaining >= 3 && text.substring(i + 1, i + 4).equals("[0m")) { // Reset
                    color.set(1, 1, 1);
                    i += 3;
                } else if (remaining >= 4 && text.charAt(i + 1) == '['
                        && text.charAt(i + 4) == 'm') {

                    switch (text.substring(i + 2, i + 4)) {
                        case "30": // black
                            color.set(0, 0, 0);
                            break;
                        case "31": // red
                            color.set(1, 0.1f, 0.1f);
                            break;
                        case "32": // green
                            color.set(0.1f, 1, 0.1f);
                            break;
                        case "33": // yellow
                            color.set(0.8f, 1f, 0.1f);
                            break;
                        case "34": // blue
                            color.set(0.1f, 0.1f, 1f);
                            break;
                        case "35": // purple
                            color.set(1f, 0.1f, 0.8f);
                            break;
                        case "36": // cyan
                            color.set(0.1f, 0.8f, 1f);
                            break;
                        case "37": // white
                            color.set(1, 1, 1);
                            break;
                        default: // unknown
                            break;
                    }
                    i += 4;
                } else if (remaining >= 8 && text.substring(i + 1, i + 3).equals("[#")) {

                    try {
                        int rawBits = Integer.parseUnsignedInt(text.substring(i + 3, i + 9), 16);

                        float red = (float) ((rawBits >> 16) & 0xFF) / 255f;
                        float green = (float) ((rawBits >> 8) & 0xFF) / 255f;
                        float blue = (float) (rawBits & 0xFF) / 255f;

                        color.set(red, green, blue);
                    } catch (NumberFormatException e) {
                        CerberusRegistry.getInstance().warning("Failed to interpret color: "
                                + text.substring(i + 1, i + 9) + " - not in radix 16!");
                        CerberusRegistry.getInstance().getService(CerberusEvent.class)
                                .executeFullEIF(new ExceptionEvent(CerberusRenderer.class, e));
                    } finally {
                        i+= 8;
                    }
                }
                continue;
            }


            CharResource res = font.getCharacter(c);
            if (res == null)
                continue;

            if (x + Xspaceing + res.getBounds().getWidth() * 2f > resolution.getX() * 2f) {
                x = 0;
                y += Yspacing + increaseY;
            } else if (wrap && c != ' ') {
                float wordLength = Xspaceing + (float) res.getBounds().getWidth() * 2f;
                CharResource current;
                for (int j = i + 1; j < text.length(); j++) {
                    char d = text.charAt(j);
                    if (d == ' ' || d == '\n' || d == '\t')
                        break;

                    current = font.getCharacter(d);
                    if (current == null)
                        continue;

                    wordLength += Xspaceing + current.getBounds().getWidth() * 2f;
                }
                wordLength -= Xspaceing;

                if (x + wordLength > resolution.getX() * 2f && wordLength <= resolution.getX() * 2f) {
                    x = 0;
                    y += Yspacing + increaseY;
                }
            }

            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            OverlayUtil util = OverlayUtil.getInstance();
            ModelResource mesh = util.getTextureScreen();

            util.setUVFiltering(new Vector2f(0, 0));
            util.setUVOffset(new Vector2f(0, 0));
            util.setSize(new Vector2f(1, 1));
            util.setSamplerUnit(0);
            util.setColor(color);

            util.setScale(new Vector2f((float) (res.getBounds().getWidth() / resolution.getX()),
                    (float) (res.getBounds().getHeight() / resolution.getY())));
            util.setTranslation(new Vector2f((x + (float) res.getBounds().getWidth()) / resolution.getX() - 1f,
                    ((y - (float) res.getBounds().getHeight()) / resolution.getY()) + 1f));

            renderer.getShaderBoard().bindShader(util.getScreenShader());
            util.updateUniforms(t);
            renderer.getTextureBoard().bindTexture(res);
            renderer.getGeometryBoard().drawMesh(mesh, DrawMode.TRIANGLES);

            glDisable(GL_BLEND);

            x += Xspaceing + res.getBounds().getWidth() * 2;

            if (res.getBounds().getHeight() * 2f > -increaseY)
                increaseY = -(float) res.getBounds().getHeight() * 2f;

            if (x >= resolution.getX() * 2) {
                x = 0;
                y += Yspacing + increaseY;
            }
        }
    }

    @Override
    public TextureResource getTexture() {
        return frameBuffer;
    }

    @Override
    public void destroy() {
        getRenderer().getTextureBoard().deleteTexture(frameBuffer);
    }

    public RenderFont getFont() {
        return font;
    }

    public String getText() {
        return text;
    }

    public void setText(String text, RenderFont font) {
        this.font = font;
        this.text = text;

        getRenderer().tryGLTask((t) -> {
            // this.update(t);
            this.updateParent(t);
        });
    }

    public float getXspaceing() {
        return Xspaceing;
    }

    public void setXspaceing(float xspaceing) {
        Xspaceing = xspaceing;
    }

    public float getYspacing() {
        return Yspacing;
    }

    public void setYspacing(float yspacing) {
        Yspacing = yspacing;
    }

    public Vector2i getResolution() {
        return resolution;
    }

    public boolean isWrap() {
        return wrap;
    }

    public void setWrap(boolean wrap) {
        this.wrap = wrap;
    }

    public boolean isUseColorCodes() {
        return useColorCodes;
    }

    public void setUseColorCodes(boolean useColorCodes) {
        this.useColorCodes = useColorCodes;
    }

    public void setResolution(Vector2i resolution) {
        resize(resolution);

        getRenderer().tryGLTask((t) -> {
            // this.update(t);
            this.updateParent(t);
        });
    }

    @Override
    public void resize(Vector2i size) {
        if (frameBuffer != null) {
            TextureBoard textureBoard = getRenderer().getTextureBoard();
            textureBoard.deleteTexture(frameBuffer);
        }

        TextureEmpty2D empty2D = new TextureEmpty2D(1);
        empty2D.genTextures();
        empty2D.initTexture(0, 0, size, ImageType.RGBA_8_INTEGER);

        frameBuffer = new FrameBufferResource(size, empty2D, false,
                new SimpleAttachment(0, AttachmentType.COLOR_00));
        this.resolution = size;
    }

    public CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }
}
