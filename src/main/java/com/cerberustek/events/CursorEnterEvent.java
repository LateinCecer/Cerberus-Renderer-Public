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
import com.cerberustek.input.InputDevice;

public class CursorEnterEvent extends InputEvent implements Event {

    /** enter value */
    private final boolean value;

    /**
     * An input event with the responsible input device and window.
     * @param inputDevice the input device
     * @param windowId    window id
     * @param value enter?
     */
    public CursorEnterEvent(InputDevice inputDevice, long windowId, boolean value) {
        super(inputDevice, windowId);
        this.value = value;
    }

    /** Has entered */
    public boolean hasEntered() {
        return value;
    }
}
