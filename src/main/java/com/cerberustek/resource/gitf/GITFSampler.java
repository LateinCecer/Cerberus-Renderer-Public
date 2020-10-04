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
import com.cerberustek.data.impl.elements.DocElement;
import com.cerberustek.exceptions.GITFFormatException;

public class GITFSampler implements GITFEntry {

    private int magFilter;
    private int minFilter;
    private int warpS;
    private int warpT;

    public GITFSampler() {
        magFilter = 0;
        minFilter = 0;
        warpS = 0;
        warpT = 0;
    }

    @Override
    public void read(GITFReader reader, MetaData data) throws GITFFormatException {
        if (!(data instanceof DocElement))
            throw new GITFFormatException();
        DocElement doc = (DocElement) data;

        Integer magFilter = doc.valueInt("magFilter");
        Integer minFilter = doc.valueInt("minFilter");
        Integer warpS = doc.valueInt("warpS");
        Integer warpT = doc.valueInt("warpT");

        if (magFilter != null)
            this.magFilter = magFilter;
        if (minFilter != null)
            this.minFilter = minFilter;
        if (warpS != null)
            this.warpS = warpS;
        if (warpT != null)
            this.warpT = warpT;
    }

    @Override
    public MetaData write() throws GITFFormatException {
        return null;
    }

    public int getMagFilter() {
        return magFilter;
    }

    public int getMinFilter() {
        return minFilter;
    }

    public int getWarpS() {
        return warpS;
    }

    public int getWarpT() {
        return warpT;
    }
}
