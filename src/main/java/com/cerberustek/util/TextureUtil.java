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

package com.cerberustek.util;

import com.cerberustek.CerberusEvent;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.events.GracefulShutdownEvent;
import com.cerberustek.logic.math.Vector3i;
import com.cerberustek.resource.image.TextureResource;
import com.cerberustek.texture.Texture;
import com.cerberustek.CerberusRenderer;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class TextureUtil {

    /**
     * Will unbind the texture 2D buffers for the
     * first 8 texture units
     */
    public static void unbindTextures() {
        for (int i = 0; i < 8; i++) {
            glActiveTexture(GL_TEXTURE0 + i);
            glBindTexture(GL_TEXTURE_2D, 0);
        }
    }

    public static void unbindTexture2D() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public static void activeUnit(int unit) {
        glActiveTexture(GL_TEXTURE0 + unit);
    }

    public static void unbindFramebuffer() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glDrawBuffers(new int[] {GL_LEFT, GL_RIGHT, GL_FRONT, GL_BACK, GL_FRONT_AND_BACK});
    }

    public static void unbindRenderbuffer() {
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
    }

    public static BufferedImage loadImage(String path) {
        try (FileInputStream inputStream = new FileInputStream(path)) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null)
                throw new IllegalArgumentException("Image with path " + path + " could not be loaded!");
            return image;
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read image data from path:" +
                    path + "!");
        }
    }

    public static BufferedImage downloadAsBufferedImage(TextureResource resource, int level) {
        Texture texture = CerberusRegistry.getInstance().getService(CerberusRenderer.class).getTextureBoard().getTexture(resource);
        Vector3i size = texture.getSize(level);

        ByteBuffer buffer = BufferUtils.createByteBuffer(size.getX() * size.getY() * 4);
        CerberusRegistry.getInstance().getService(CerberusRenderer.class).getTextureBoard().bindTexture(resource);
        glGetTexImage(GL_TEXTURE_2D, 0, GL_COLOR_ATTACHMENT0 + level, GL_UNSIGNED_BYTE, buffer);

        BufferedImage image = new BufferedImage(size.getX(), size.getY(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < size.getY(); y++) {
            for (int x = 0; x < size.getX(); x++) {
                int index = (x + (size.getX() * y)) * 4;
                try {
                    int r = (int) buffer.get(index) & 0xFF;
                    int g = (int) buffer.get(index + 1) & 0xFF;
                    int b = (int) buffer.get(index + 2) & 0xFF;
                    int a = (int) buffer.get(index + 3) & 0xFF;

                    /*
                    float i = buffer.get(x + (size.getX() * y));
                    if (i > 0 && i < 1) {
                        image.setRGB(x, y, new Color(i, i * i * 0.5f, 0, 1).getRGB());
                    } else
                        image.setRGB(x, y, 0);*/

                    image.setRGB(x, y, (r & 0xFF) << 24 + (g & 0xFF) << 16 + (b & 0xFF) << 8 + a & 0xFF);
                } catch (IndexOutOfBoundsException e) {
                    CerberusRegistry.getInstance().getService(CerberusEvent.class)
                            .executeFullEIF(new GracefulShutdownEvent(
                                    CerberusRegistry.getInstance().getService(CerberusRenderer.class),
                                    "Faild to write RGB data: " + buffer.capacity() + "\\" + index, Thread.currentThread().getStackTrace()));
                }
            }
        }
        return image;
    }
}
