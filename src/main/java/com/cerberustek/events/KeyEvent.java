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

public class KeyEvent extends InputEvent implements Event {

    /** Key code of the key event */
    private final int key;
    /** Scan code of the key event */
    private final int scanCode;
    /** The action performed */
    private final int action;
    /** Modifications to the key event */
    private final int mods;

    /**
     * Standard key event.
     *
     * @param inputDevice the input device responsible
     * @param window The Window that pulled the key callback
     * @param key The key code of the key that triggered the key event
     * @param scanCode The scan code of the key callback
     * @param action The action of the event
     * @param mods modification the the key event
     */
    public KeyEvent(InputDevice inputDevice, long window, int key, int scanCode, int action, int mods) {
        super(inputDevice, window);
        this.key = key;
        this.scanCode = scanCode;
        this.action = action;
        this.mods = mods;
    }

    public int getKey() {
        return key;
    }

    public int getScanCode() {
        return scanCode;
    }

    public int getAction() {
        return action;
    }

    public int getMods() {
        return mods;
    }
}
