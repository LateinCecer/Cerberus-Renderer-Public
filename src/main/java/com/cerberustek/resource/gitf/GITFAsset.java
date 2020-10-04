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

import com.cerberustek.data.MetaData;
import com.cerberustek.data.impl.tags.DocTag;
import com.cerberustek.data.impl.tags.StringTag;
import com.cerberustek.exceptions.GITFFormatException;

public class GITFAsset implements GITFEntry {

    private String generator;
    private String version;
    private String copyright;

    @Override
    public void read(GITFReader reader, MetaData data) throws GITFFormatException {
        if (data instanceof DocTag) {
            DocTag doc = (DocTag) data;

            generator = doc.valueString("generator");
            version = doc.valueString("version");
            copyright = doc.valueString("copyright");

        } else
            throw new GITFFormatException();
    }

    @Override
    public MetaData write() {
        DocTag doc = new DocTag("asset");

        doc.insert(new StringTag("generator", generator));
        doc.insert(new StringTag("version", version));
        if (copyright != null)
            doc.insert(new StringTag("copyright", copyright));

        return doc;
    }

    public String getGenerator() {
        return generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }
}
