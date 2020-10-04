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

import com.cerberustek.CerberusRegistry;
import com.cerberustek.settings.Settings;
import com.cerberustek.window.Window;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.input.InputBoard;
import com.cerberustek.input.InputDevice;
import com.cerberustek.input.InputDeviceSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InputBoardImpl implements InputBoard {

    private final InputDevice[] devices;

    public InputBoardImpl() {
        devices = new InputDevice[InputDeviceSpec.values().length];
    }

    @Override
    public void addInputDevice(@NotNull InputDeviceSpec spec, @NotNull InputDevice device) throws IllegalStateException {
        if (devices[spec.ordinal()] != null)
            devices[spec.ordinal()] = device;
        else if (!devices[spec.ordinal()].equals(device))
            throw new IllegalStateException("Specification is already assigned");
    }

    @Override
    public void removeInputDevice(@NotNull InputDeviceSpec spec) {
        InputDevice device = devices[spec.ordinal()];
        if (device == null)
            return;

        device.destroy();
        devices[spec.ordinal()] = null;
    }

    @Override
    public void replaceInputDevice(@NotNull InputDeviceSpec spec, @Nullable InputDevice device) {
        devices[spec.ordinal()] = device;
    }

    @Override
    public boolean hasInputDevice(@NotNull InputDeviceSpec spec) {
        return devices[spec.ordinal()] != null;
    }

    @Override
    public boolean hasInputDevice(@NotNull InputDeviceSpec spec, @NotNull InputDevice device) {
        return device.equals(devices[spec.ordinal()]);
    }

    @Override
    public @Nullable InputDevice getInputDevice(@NotNull InputDeviceSpec spec) {
        return devices[spec.ordinal()];
    }

    @Override
    public <T extends InputDevice> @Nullable T getInputDevice(@NotNull InputDeviceSpec spec, @NotNull Class<T> clazz) {
        InputDevice inputDevice = devices[spec.ordinal()];
        if (inputDevice == null)
            return null;
        try {
            return clazz.cast(inputDevice);
        } catch (ClassCastException e) {
            return null;
        }
    }

    @Override
    public void init() {
        CerberusRenderer renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        Window window = renderer.getWindow();
        Settings settings = renderer.getSettings();

        if (settings.getBoolean("add-keyboard", true)) {
            devices[InputDeviceSpec.KEYBOARD.ordinal()] = new Keyboard();
            window.addCallback(devices[InputDeviceSpec.KEYBOARD.ordinal()]);
        }
        if (settings.getBoolean("add-mouse", true)) {
            Mouse mouse = new Mouse();
            devices[InputDeviceSpec.MOUSE_BUTTON.ordinal()] = mouse.getMouseButton();
            devices[InputDeviceSpec.MOUSE_WHEEL.ordinal()] = mouse.getScrollWheel();
            devices[InputDeviceSpec.CURSOR_ENTER.ordinal()] = mouse.getCursorEnter();
            devices[InputDeviceSpec.CURSOR_POSITION.ordinal()] = mouse.getCursorPosition();
            window.addCallback(mouse);
        }
        if (settings.getBoolean("add-text-input", true)) {
            devices[InputDeviceSpec.CHAR_INPUT.ordinal()] = new CharInput();
            window.addCallback(devices[InputDeviceSpec.CHAR_INPUT.ordinal()]);
        }
        if (settings.getBoolean("add-filedrop", true)) {
            devices[InputDeviceSpec.FILE_DROP.ordinal()] = new DropCallback();
        }
    }
}
