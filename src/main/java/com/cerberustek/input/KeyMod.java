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

package com.cerberustek.input;

import com.cerberustek.input.impl.Keyboard;

public enum KeyMod {

    NONE(0),
    SHIFT(Keyboard.MOD_SHIFT),
    CONTROL(Keyboard.MOD_CONTROL),
    ALT(Keyboard.MOD_ALT),
    SUPER(Keyboard.MOD_SUPER);

    private final int keyCode;

    KeyMod(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public static KeyMod fromModCode(int keyCode) {
        switch (keyCode) {
            case Keyboard.MOD_SHIFT:
                return SHIFT;
            case Keyboard.MOD_CONTROL:
                return CONTROL;
            case Keyboard.MOD_ALT:
                return ALT;
            case Keyboard.MOD_SUPER:
                return SUPER;
            default:
                return NONE;
        }
    }
}
