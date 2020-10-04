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

package com.cerberustek.resource.gitf;

import com.cerberustek.CerberusEvent;
import com.cerberustek.data.MetaData;
import com.cerberustek.data.impl.elements.DocElement;
import com.cerberustek.data.impl.tags.StringTag;
import com.cerberustek.events.ExceptionEvent;
import com.cerberustek.events.ModificationFraudEvent;
import com.cerberustek.exceptions.GITFFormatException;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GITFImage implements GITFEntry {

    private String uri;
    private String type;
    private File file;

    @Override
    public void read(GITFReader reader, MetaData data) throws GITFFormatException {
        if (!(data instanceof DocElement))
            throw new GITFFormatException();
        DocElement doc = (DocElement) data;

        uri = doc.valueString("uri");
        type = doc.valueString("mimeType");

        if (uri == null || type == null)
            throw new GITFFormatException();

        file = reader.loadUri(uri);
    }

    @Override
    public MetaData write() throws GITFFormatException {
        DocElement element = new DocElement();
        element.insert(new StringTag("uri", uri));
        element.insert(new StringTag("mimeType", type));
        return element;
    }

    public BufferedImage loadBuffered() {
        if (!file.exists())
            return null;

        try {
            BufferedImage image = ImageIO.read(file);
            if (image != null)
                return image;
        } catch (IOException e) {
            CerberusRegistry registry = CerberusRegistry.getInstance();
            registry.warning("Unable to load buffered image resource " + file);
            registry.getService(CerberusEvent.class).executeFullEIF(new ExceptionEvent(CerberusRenderer.class, e));
            registry.getService(CerberusEvent.class).executeFullEIF(new ModificationFraudEvent(CerberusRenderer.class));
        }
        return null;
    }
}
