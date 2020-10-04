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
import com.cerberustek.resource.buffered.BufferedFileResource;
import com.cerberustek.resource.buffered.UnsignedByteBufferResource;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.resource.image.ImageResource;
import com.cerberustek.texture.ImageType;
import com.cerberustek.util.BufferUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

public class FileImageResource extends UnsignedByteBufferResource implements ImageResource<ByteBuffer>,
        BufferedFileResource<ByteBuffer> {

    private final File file;
    private final int unit;

    private ImageType type;
    private Vector2i size;

    public FileImageResource(File file, int unit) {
        this.file = file;
        this.unit = unit;
    }

    public FileImageResource(String path, int unit) {
        this.file = new File(path);
        this.unit = unit;
    }

    public File file() {
        return file;
    }

    @Override
    public ByteBuffer load() {
        CerberusRegistry registry = CerberusRegistry.getInstance();
        if (!file.exists()) {
            registry.warning("The texture file " + file.getPath() + " does not exist");
            registry.getService(CerberusEvent.class)
                    .executeFullEIF(new ExceptionEvent(CerberusRenderer.class, new FileNotFoundException()));
            return null;
        }

        if (!file.isFile()) {
            registry.warning("The texture file " + file.getPath() + " is not a file");
            registry.getService(CerberusEvent.class)
                    .executeFullEIF(new ExceptionEvent(CerberusRenderer.class, new FileNotFoundException()));
            return null;
        }

        try (FileInputStream inputStream = new FileInputStream(file)) {
            BufferedImage image = ImageIO.read(inputStream);

            size = new Vector2i(image.getWidth(), image.getHeight());
            type = ImageType.fromBufferedImage(image);
            return BufferUtil.createFlippedBuffer(image, type);

        } catch (FileNotFoundException e) {
            registry.warning("Texture file could not be found");
            registry.getService(CerberusEvent.class).executeFullEIF(new ExceptionEvent(CerberusRenderer.class, e));
        } catch (IOException e) {
            registry.warning("Texture file could not be loaded");
            registry.getService(CerberusEvent.class).executeFullEIF(new ExceptionEvent(CerberusRenderer.class, e));
        }
        return null;
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
    public void close() throws IOException {
        size = null;
        type = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileImageResource that = (FileImageResource) o;
        return unit == that.unit &&
                file.equals(that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, unit);
    }
}
