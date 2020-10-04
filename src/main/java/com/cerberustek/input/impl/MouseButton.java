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

import com.cerberustek.CerberusEvent;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.events.KeyEvent;
import com.cerberustek.window.Window;
import com.cerberustek.input.ButtonInputDevice;
import com.cerberustek.input.KeyAction;
import com.cerberustek.input.KeyMod;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

public class MouseButton extends GLFWMouseButtonCallback implements ButtonInputDevice {

    public static final int MOUSE_BUTTON_1 = 0;
    public static final int MOUSE_BUTTON_2 = 1;
    public static final int MOUSE_BUTTON_3 = 2;
    public static final int MOUSE_BUTTON_4 = 3;
    public static final int MOUSE_BUTTON_5 = 4;
    public static final int MOUSE_BUTTON_6 = 5;
    public static final int MOUSE_BUTTON_7 = 6;
    public static final int MOUSE_BUTTON_8 = 7;
    public static final int MOUSE_BUTTON_LAST = 7;
    public static final int MOUSE_BUTTON_LEFT = 0;
    public static final int MOUSE_BUTTON_RIGHT = 1;
    public static final int MOUSE_BUTTON_MIDDLE = 2;

    private boolean[] buttonsPressed;
    private boolean[] buttonsTyped;
    private KeyMod[] keyMods;

    private CerberusEvent eventService;

    public MouseButton() {
        this.buttonsPressed = new boolean[MOUSE_BUTTON_LAST];
        this.buttonsTyped = new boolean[MOUSE_BUTTON_LAST];
        this.keyMods = new KeyMod[MOUSE_BUTTON_LAST];

        for (int i = 0; i < MOUSE_BUTTON_LAST; i++) {
            buttonsPressed[i] = false;
            buttonsTyped[i] = false;
            keyMods[i] = KeyMod.NONE;
        }
    }

    @Override
    public KeyMod getMod(int keyCode) {
        if (buttonsPressed[keyCode])
            return keyMods[keyCode];
        return KeyMod.NONE;
    }

    @Override
    public boolean isPressed(int keyCode) {
        return buttonsPressed[keyCode];
    }

    @Override
    public boolean isTyped(int keyCode) {
        boolean out = buttonsPressed[keyCode] && buttonsTyped[keyCode];
        buttonsTyped[keyCode] = false;
        return out;
    }

    @Override
    public boolean isReleased(int keyCode) {
        return !buttonsPressed[keyCode];
    }

    @Override
    public void destroy() {
        free();
    }

    @Override
    public void init(Window window) {
        set(window.id());
    }

    @Override
    public void invoke(long window, int button, int action, int mods) {
        if (eventService == null)
            eventService = CerberusRegistry.getInstance().getService(CerberusEvent.class);


        if (button <= MOUSE_BUTTON_LAST && button >= 0) {
            buttonsTyped[button] = !buttonsPressed[button] && action != KeyAction.PRESSED.getActionId();
            buttonsPressed[button] = action != KeyAction.RELEASED.getActionId();
            keyMods[button] = KeyMod.fromModCode(action);

            eventService.executeFullEIF(new KeyEvent(this, window, button, 0, action, mods));
        }
    }
}
