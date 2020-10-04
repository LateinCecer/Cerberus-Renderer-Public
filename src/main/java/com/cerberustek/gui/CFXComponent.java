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

import com.cerberustek.Destroyable;
import com.cerberustek.logic.math.Vector2i;
import org.jetbrains.annotations.NotNull;

/**
 * The C Component is the basic component of the Cerberus
 * GUI system.
 */
public interface CFXComponent extends Destroyable {

    /**
     * Returns the orientation of a C component.
     * @return orientation
     */
    @NotNull CFXOrientation getOrientation();

    /**
     * Will set the components orientation.
     * @param orientation the components orientation
     */
    void setOrientation(@NotNull CFXOrientation orientation);

    /**
     * Will paint the component to the specified
     * canvas.
     *
     * Information about the current pixel size and
     * the offset of this component dictated by the
     * parents layout, are contained within the
     * canvas.
     * @param canvas canvas to paint to
     */
    void paintComponent(@NotNull CFXCanvas canvas);

    /**
     * Will set the resolution of the C component.
     *
     * This might trigger child components to update
     * their resolutions, dependent on the specified
     * layout options, as well.
     * Setting the resolution will in most cases require
     * the gui to repaint, however the repaint will not
     * be triggered directly by this method and should be
     * handled from an other source, if necassary.
     *
     * @param res new component resolution
     */
    void setResolution(Vector2i res);

    /**
     * Returns the current component resolution.
     * @return component resolution
     */
    Vector2i getResolution();

    /**
     * Will dispatch a C event.
     * @param event event
     */
    void dispatchEvent(CFXEvent event);

    /**
     * Will set the gui id of the component
     * @param id gui id
     */
    void register(int id);

    /**
     * Returns the gui id of the component
     * @return gui id
     */
    int id();
}
