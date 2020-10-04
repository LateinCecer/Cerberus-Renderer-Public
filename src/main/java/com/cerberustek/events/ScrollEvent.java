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

package com.cerberustek.events;

import com.cerberustek.event.Event;
import com.cerberustek.logic.math.Vector2d;
import com.cerberustek.input.InputDevice;

public class ScrollEvent extends InputEvent implements Event {

    /** The amount scrolled in x and y direction */
    private final Vector2d scroll;
    /** The amount the scroll factor was changed by this event */
    private final Vector2d deltaScroll;

    /**
     * Mouse scroll event.
     *
     * @param inputDevice the input device responsible for the event call
     * @param scroll scroll
     * @param deltaScroll change in scroll
     * @param windowId window in focus
     */
    public ScrollEvent(InputDevice inputDevice, long windowId, Vector2d scroll, Vector2d deltaScroll) {
        super(inputDevice, windowId);
        this.scroll = scroll;
        this.deltaScroll = deltaScroll;
    }

    /** Returns the amount scrolled in x and y direction */
    public Vector2d getScroll() {
        return scroll;
    }

    /** Returns the difference between the current and the last scrolls */
    public Vector2d getDeltaScroll() {
        return deltaScroll;
    }
}
