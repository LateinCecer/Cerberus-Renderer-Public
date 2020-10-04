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

package com.cerberustek.texture.impl;

import com.cerberustek.CerberusEvent;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.events.GLFrameBufferInitializationError;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.logic.math.Vector3i;
import com.cerberustek.texture.Attachment;
import com.cerberustek.texture.FrameBuffer;
import com.cerberustek.texture.ImageType;
import com.cerberustek.texture.Texture;
import com.cerberustek.util.TextureUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL45.*;

public class FrameBufferTexture implements FrameBuffer {

    /** Texture */
    private final Texture texture;
    /** Frame buffer size */
    private Vector2i screenSize;
    /** Frame buffer object id.
     * Used for storing geometry and color information */
    private int frameBuffer;
    /** Depth render buffer object.
     * Used for storing depth information */
    private int renderBuffer;
    /** Attachments */
    private HashMap<Integer, Attachment> attachments = new HashMap<>();

    private int[] attachmentIndecies;

    /**
     * Frame buffer object from screen size and a texture.
     *
     * @param screenSize Frame buffer size
     * @param texture Texture to draw to
     */
    public FrameBufferTexture(@NotNull Vector2i screenSize, Texture texture) {
        this.screenSize = screenSize;
        this.texture = texture;
    }

    @Override
    public void genTextures() {
        texture.genTextures();
    }

    @Override
    public void genTexture(int index) {
        texture.genTexture(index);
    }

    @Override
    public void bindToUnit(int index, int unit) {
        texture.bindToUnit(index, unit);
    }

    @Override
    public void bind(int index) {
        texture.bind(index);
    }

    @Override
    public void bind() {
        texture.bind();
    }

    @Override
    public void genFrameBuffer() {
        /*if (renderBuffer == 0)
            throw new IllegalStateException("The DepthRenderBuffer has to be initialized prior to the FrameBuffer" +
                    " initialization!");*/

        for (int i = 0; i < length(); i++) {
            if (!isOnline(i))
                throw new IllegalStateException("All texture buffers have to be initialized prior to the FrameBuffer" +
                        " initialization!");
        }

        frameBuffer = glGenFramebuffers();
        TextureUtil.unbindTexture2D();
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
        glNamedFramebufferDrawBuffers(frameBuffer, attachmentIndecies);

        for (int i = 0; i < length(); i++) {
            glActiveTexture(GL_TEXTURE0 + texture.getUnit(i));
            glBindTexture(GL_TEXTURE_2D, texture.getPointer(i));
            glFramebufferTexture2D(GL_FRAMEBUFFER, attachments.get(i).getType().getId(), GL_TEXTURE_2D,
                    texture.getPointer(i), 0);
            // CerberusRegistry.getInstance().debug("Bound texture buffer " + i + " to framebuffer attachment " + attachments.get(i).getType() + " >> " + attachmentIndecies[i]);
        }

        if (renderBuffer != 0) {
            glBindRenderbuffer(GL_RENDERBUFFER, renderBuffer);
            glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, renderBuffer);
            TextureUtil.unbindRenderbuffer();
        }

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            CerberusRegistry.getInstance().getService(CerberusEvent.class).executeFullEIF(
                    new GLFrameBufferInitializationError("Framebuffer initialization could not be completed!",
                            this));
        TextureUtil.unbindFramebuffer();
    }

    @Override
    public void bindFrameBuffer() {
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
    }

    @Override
    public void genRenderBuffer() {
        renderBuffer = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, renderBuffer);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, screenSize.getX(), screenSize.getY());

        TextureUtil.unbindRenderbuffer();
    }

    @Override
    public void drawToBuffers() {
        glDrawBuffers(attachmentIndecies);
        // glDrawBuffers(new int[] {GL_DEPTH_ATTACHMENT, GL_COLOR_ATTACHMENT1});
        // glNamedFramebufferDrawBuffers(frameBuffer, attachmentIndecies);
    }

    @Override
    public void bindRenderBuffer() {
        glBindRenderbuffer(GL_RENDERBUFFER, renderBuffer);
    }

    @Override
    public boolean isOnline() {
        return frameBuffer != 0 && texture.isOnline();
    }

    @Override
    public boolean isOnline(int index) {
        return texture.isOnline(index);
    }

    @Override
    public int getPointer(int index) {
        return texture.getPointer(index);
    }

    @Override
    public ImageType getType(int index) {
        return texture.getType(index);
    }

    @Override
    public Vector3i getSize(int index) {
        return texture.getSize(index);
    }

    @Override
    public Attachment getAttachment(int index) {
        return attachments.get(index);
    }

    @Override
    public void destroy(int index) {
        texture.destroy(index);
    }

    @Override
    public void destroy() {
        texture.destroy();

        if (renderBuffer != 0)
            glDeleteRenderbuffers(renderBuffer);
        glDeleteFramebuffers(frameBuffer);
    }

    @Override
    public int length() {
        return texture.length();
    }

    @Override
    public void set() {
        glViewport(0, 0, getScreenSize().getX(), getScreenSize().getY());
        bindFrameBuffer();
        if (renderBuffer != 0)
            bindRenderBuffer();
        drawToBuffers();
    }

    @Override
    public Vector2i getScreenSize() {
        return screenSize;
    }

    @Override
    public Collection<Attachment> getAttachments() {
        return attachments.values();
    }

    @Override
    public Attachment[] createAttachments(Attachment[] indices) {
        attachments.clear();
        attachmentIndecies = new int[indices.length];
        for (int i = 0; i < indices.length; i++) {
            attachments.put(indices[i].getTextureBufferIndex(), indices[i]);
            attachmentIndecies[i] = indices[i].getType().getId();
        }
        return indices;
    }

    @Override
    public int getUnit(int index) {
        return texture.getUnit(index);
    }
}
