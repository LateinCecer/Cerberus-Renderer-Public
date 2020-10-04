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

import com.cerberustek.CerberusRegistry;
import com.cerberustek.input.ButtonInputDevice;
import com.cerberustek.input.InputDeviceSpec;
import com.cerberustek.input.KeyBinding;
import com.cerberustek.input.impl.Keyboard;
import com.cerberustek.input.impl.SimpleKeyBinding;
import com.cerberustek.CerberusRenderer;

import java.util.function.Supplier;

public enum CFXKeyBinding {

    NEXT_LETTER(createKeyboardKey("Next Letter", Keyboard.KEY_RIGHT)),
    PREV_LETTER(createKeyboardKey("Previous Letter", Keyboard.KEY_LEFT)),
    NEXT_LINE(createKeyboardKey("Next Line", Keyboard.KEY_DOWN)),
    PREV_LINE(createKeyboardKey("Previous Line", Keyboard.KEY_UP)),
    BACKSPACE(createKeyboardKey("Backspace", Keyboard.KEY_BACKSPACE)),
    ENTER(createKeyboardKey("Enter", Keyboard.KEY_ENTER, Keyboard.KEY_KP_ENTER)),
    DELETE(createKeyboardKey("Delete", Keyboard.KEY_DELETE)),
    INSERT(createKeyboardKey("Insert", Keyboard.KEY_INSERT)),
    SHIFT(createKeyboardKey("Shift", Keyboard.KEY_LEFT_SHIFT, Keyboard.KEY_RIGHT_SHIFT)),
    CONTROL(createKeyboardKey("Control", Keyboard.KEY_LEFT_CONTROL, Keyboard.KEY_RIGHT_CONTROL));

    private final Supplier<KeyBinding> supplier;

    CFXKeyBinding(Supplier<KeyBinding> supplier) {
        this.supplier = supplier;
    }

    public Supplier<KeyBinding> defaultResource() {
        return supplier;
    }

    private static BindingSupplier createKeyboardKey(String name, int... keyCode) {
        return new BindingSupplier(InputDeviceSpec.KEYBOARD, Keyboard.class, name, keyCode);
    }

    private static BindingSupplier createMouseKey(String name, int... keyCode) {
        return new BindingSupplier(InputDeviceSpec.MOUSE_BUTTON, Keyboard.class, name, keyCode);
    }

    private static class BindingSupplier implements Supplier<KeyBinding> {

        private final String name;
        private final int[] keyCode;
        private final InputDeviceSpec deviceSpec;
        private final Class<? extends ButtonInputDevice> deviceClass;

        public BindingSupplier(InputDeviceSpec deviceSpec, Class<? extends ButtonInputDevice> deviceClass,
                               String name, int... keyCode) {
            this.name = name;
            this.keyCode = keyCode;
            this.deviceSpec = deviceSpec;
            this.deviceClass = deviceClass;
        }

        @Override
        public KeyBinding get() {
            CerberusRenderer renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
            ButtonInputDevice buttonDevice = renderer.getInputBoard().getInputDevice(deviceSpec, deviceClass);
            if (buttonDevice == null)
                return null;

            return new SimpleKeyBinding(name, buttonDevice, keyCode);
        }
    }
}
