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

import com.cerberustek.input.ButtonInputDevice;
import com.cerberustek.input.KeyBinding;
import com.cerberustek.input.KeyMod;

import java.util.Collection;
import java.util.HashSet;

import static com.cerberustek.input.KeyMod.NONE;

public class SimpleKeyBinding implements KeyBinding {

    private final HashSet<Integer> keyCodes = new HashSet<>();
    private final ButtonInputDevice inputDevice;
    private final String name;

    public SimpleKeyBinding(String name, ButtonInputDevice inputDevice, Collection<Integer> keyCodes) {
        this.keyCodes.addAll(keyCodes);
        this.inputDevice = inputDevice;
        this.name = name;
    }

    public SimpleKeyBinding(String name, ButtonInputDevice inputDevice, int... keyCodes) {
        this(name, inputDevice, setFromArray(keyCodes));
    }

    private static Collection<Integer> setFromArray(int[] keyCodes) {
        HashSet<Integer> set = new HashSet<>();
        for (int i : keyCodes)
            set.add(i);
        return set;
    }

    public void addKeyCode(int keyCode) {
        if (!containsKeyCode(keyCode))
            keyCodes.add(keyCode);
    }

    public void removeKeyCode(int keyCode) {
        keyCodes.remove(keyCode);
    }

    public boolean containsKeyCode(int keyCode) {
        return keyCodes.contains(keyCode);
    }

    @Override
    public boolean isPressed() {
        for (int i : keyCodes) {
            if (inputDevice.isPressed(i))
                return true;
        }
        return false;
    }

    @Override
    public boolean isTyped() {
        for (int i : keyCodes) {
            if (inputDevice.isTyped(i))
                return true;
        }
        return false;
    }

    @Override
    public boolean isReleased() {
        for (int i : keyCodes) {
            if (inputDevice.isReleased(i))
                return true;
        }
        return false;
    }

    @Override
    public KeyMod getMod() {
        for (int i : keyCodes) {
            if (inputDevice.getMod(i) != NONE)
                return inputDevice.getMod(i);
        }
        return NONE;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ButtonInputDevice getInputDevice() {
        return inputDevice;
    }
}
