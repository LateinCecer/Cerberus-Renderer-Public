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
import com.cerberustek.events.CharEvent;
import com.cerberustek.window.Window;
import com.cerberustek.input.InputDevice;
import org.lwjgl.glfw.GLFWCharCallback;

public class CharInput extends GLFWCharCallback implements InputDevice {

    private Window window;
    private final StringBuilder stringBuilder;
    private CerberusEvent eventService;
    private int threshold = 64;

    public CharInput() {
        stringBuilder = new StringBuilder();
    }

    @Override
    public void invoke(long window, int codepoint) {
        if (window == this.window.id()) {
            stringBuilder.appendCodePoint(codepoint);
            getEventService().executeShortEIT(new CharEvent(this, window, codepoint));

            if (stringBuilder.length() > threshold)
                stringBuilder.setLength(0);
        }
    }

    /**
     * Will return the text that has been recorded since the
     * last time the char input was cleared.
     *
     * This method will also clear the string buffer after the
     * text is retrieved.
     *
     * @return collected text
     */
    public String pull() {
        String outputString = stringBuilder.toString();
        stringBuilder.setLength(0);
        return outputString;
    }

    /**
     * Will return the text that has been recorded since the
     * last time the char input was cleared.
     *
     * Other than the pull() method, this method will not
     * clear the string buffer.
     *
     * @return collected text
     */
    public String peak() {
        return stringBuilder.toString();
    }

    /**
     * Will clear the string buffer.
     */
    public void clear() {
        stringBuilder.setLength(0);
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public void destroy() {
        free();
    }

    @Override
    public void init(Window window) {
        this.window = window;
        set(window.id());
    }

    private CerberusEvent getEventService() {
        if (eventService == null)
            eventService = CerberusRegistry.getInstance().getService(CerberusEvent.class);
        return eventService;
    }
}
