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

public class CursorPositionEvent extends InputEvent implements Event {

    /** Current position */
    private final Vector2d currentPos;
    /** Delta position */
    private final Vector2d deltaPos;

    /**
     * An input event with the responsible input device and window.
     *
     * @param inputDevice the input device
     * @param windowId    window id
     */
    public CursorPositionEvent(InputDevice inputDevice, long windowId, Vector2d currentPos, Vector2d deltaPos) {
        super(inputDevice, windowId);
        this.currentPos = currentPos;
        this.deltaPos = deltaPos;
    }

    /**
     * Returns the current Position of the cursor.
     *
     * @return current position
     */
    public Vector2d getCurrentPos() {
        return currentPos;
    }

    /**
     * Returns the difference between the current and the last position
     * of the cursor.
     *
     * @return delta pos
     */
    public Vector2d getDeltaPos() {
        return deltaPos;
    }
}
