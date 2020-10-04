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

package com.cerberustek.gui;

import com.cerberustek.Updatable;
import com.cerberustek.logic.math.Vector2i;
import org.jetbrains.annotations.NotNull;

/**
 * The C Layout contains information about the
 * position and size of the children components
 * of a parent component.
 */
public interface CFXLayout extends Updatable {

    /**
     * Returns the offset of the component relative
     * to the parents point of origin, in pixels.
     * @return offset in pixels
     */
    @NotNull
    Vector2i getOffset(@NotNull CFXComponent child, @NotNull CFXCanvas canvas);

    /**
     * Returns the size of the layout in pixels.
     * @return size
     */
    @NotNull
    Vector2i getSize(@NotNull CFXComponent child, @NotNull CFXCanvas canvas);
}
