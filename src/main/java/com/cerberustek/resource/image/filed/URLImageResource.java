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

package com.cerberustek.resource.image.filed;

import com.cerberustek.CerberusEvent;
import com.cerberustek.events.ExceptionEvent;
import com.cerberustek.resource.buffered.BufferedURLResource;
import com.cerberustek.resource.buffered.UnsignedByteBufferResource;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.resource.image.ImageResource;
import com.cerberustek.texture.ImageType;
import com.cerberustek.util.BufferUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Objects;

public class URLImageResource extends UnsignedByteBufferResource implements ImageResource<ByteBuffer>, BufferedURLResource<ByteBuffer> {

    private final URL url;
    private final int unit;

    private ImageType type;
    private Vector2i size;

    public URLImageResource(URL url, int unit) {
        this.url = url;
        this.unit = unit;
    }

    public URLImageResource(String path, int unit) throws MalformedURLException {
        this.url = new URL(path);
        this.unit = unit;
    }

    @Override
    public URL url() {
        return url;
    }

    @Override
    public Vector2i getSize() {
        return size;
    }

    @Override
    public ImageType getType() {
        return type;
    }

    @Override
    public int getTextureUnit() {
        return unit;
    }

    @Override
    public ByteBuffer load() {
        CerberusRegistry registry = CerberusRegistry.getInstance();

        try (InputStream inputStream = url.openStream()) {
            BufferedImage image = ImageIO.read(inputStream);

            size = new Vector2i(image.getWidth(), image.getHeight());
            type = ImageType.fromBufferedImage(image);
            return BufferUtil.createFlippedBuffer(image, type);

        } catch (IOException e) {
            registry.warning("Texture file could not be loaded");
            registry.getService(CerberusEvent.class).executeFullEIF(new ExceptionEvent(CerberusRenderer.class, e));
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        size = null;
        type = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        URLImageResource that = (URLImageResource) o;
        return unit == that.unit &&
                url.equals(that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, unit);
    }
}
