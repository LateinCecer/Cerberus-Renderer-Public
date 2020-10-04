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

public abstract class InputEvent implements Event {

    /** The input device responsible for the input event */
    private final InputDevice inputDevice;
    /** Window id */
    private final long windowId;

    /**
     * An input event with the responsible input device and window.
     *
     * @param inputDevice the input device
     * @param windowId window id
     */
    protected InputEvent(InputDevice inputDevice, long windowId) {
        this.inputDevice = inputDevice;
        this.windowId = windowId;
    }

    /**
     * Returns the input device which is responsible for the event call
     *
     * @return input device
     */
    public InputDevice getInputDevice() {
        return inputDevice;
    }

    /**
     * Returns the window in focus.
     *
     * @return window id
     */
    public long getWindowId() {
        return windowId;
    }
}
