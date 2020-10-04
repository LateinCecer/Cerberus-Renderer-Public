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

package com.cerberustek.input.impl;

import com.cerberustek.window.Window;
import com.cerberustek.window.callback.CerberusCallback;

/**
 * A summery of all mouse inputs
 */
public class Mouse implements CerberusCallback {

    /** The mouse button input device */
    private final MouseButton mouseButton;
    /** The scroll wheel input device */
    private final ScrollWheel scrollWheel;
    /** The cursor enter input device */
    private final CursorEnter cursorEnter;
    /** The Cursor position input device */
    private final CursorPosition cursorPosition;

    public Mouse(MouseButton mouseButton, ScrollWheel scrollWheel, CursorEnter cursorEnter,
                 CursorPosition cursorPosition) {

        this.mouseButton = mouseButton;
        this.scrollWheel = scrollWheel;
        this.cursorEnter = cursorEnter;
        this.cursorPosition = cursorPosition;
    }

    public Mouse() {
        this.mouseButton = new MouseButton();
        this.scrollWheel = new ScrollWheel();
        this.cursorEnter = new CursorEnter();
        this.cursorPosition = new CursorPosition();
    }

    public MouseButton getMouseButton() {
        return mouseButton;
    }

    public ScrollWheel getScrollWheel() {
        return scrollWheel;
    }

    public CursorEnter getCursorEnter() {
        return cursorEnter;
    }

    public CursorPosition getCursorPosition() {
        return cursorPosition;
    }

    @Override
    public void init(Window window) {
        mouseButton.init(window);
        scrollWheel.init(window);
        cursorEnter.init(window);
        cursorPosition.init(window);
    }

    @Override
    public void free() {
        mouseButton.free();
        scrollWheel.free();
        cursorEnter.free();
        cursorPosition.free();
    }
}
