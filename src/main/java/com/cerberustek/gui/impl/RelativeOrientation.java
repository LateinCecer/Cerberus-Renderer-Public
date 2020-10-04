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

import com.cerberustek.logic.math.Vector2f;
import com.cerberustek.gui.CFXOrientation;

public class RelativeOrientation implements CFXOrientation {

    private final Vector2f scale;
    private final Vector2f coord;

    public RelativeOrientation(Vector2f scale, Vector2f coord) {
        this.scale = scale;
        this.coord = coord;
    }

    /**
     * Returns the scale of the component relative to the
     * scale of the parent.
     * @return relative scale
     */
    public Vector2f getRelativeScale() {
        return scale;
    }

    /**
     * Returns the coordinations of the component relative
     * to the origin point of the parent
     * @return relative coord
     */
    public Vector2f getRelativeCoord() {
        return coord;
    }
}
