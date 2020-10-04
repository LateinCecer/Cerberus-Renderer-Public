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

import com.cerberustek.input.InputDevice;

public class CharEvent extends InputEvent {

    private final int codePoint;

    /**
     * An input event with the responsible input device and window.
     *
     * @param inputDevice the input device
     * @param windowId    window id
     */
    public CharEvent(InputDevice inputDevice, long windowId, int codePoint) {
        super(inputDevice, windowId);
        this.codePoint = codePoint;
    }

    public int getCodePoint() {
        return codePoint;
    }
}
