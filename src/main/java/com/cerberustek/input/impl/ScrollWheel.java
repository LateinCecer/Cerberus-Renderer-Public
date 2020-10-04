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
import com.cerberustek.events.ScrollEvent;
import com.cerberustek.logic.math.Vector2d;
import com.cerberustek.window.Window;
import com.cerberustek.input.InputDevice;
import org.lwjgl.glfw.GLFWScrollCallback;

public class ScrollWheel extends GLFWScrollCallback implements InputDevice {

    private Vector2d lastScroll;
    private Vector2d currentScroll;

    private CerberusEvent eventService;

    public ScrollWheel() {
        lastScroll = new Vector2d(0, 0);
        currentScroll = new Vector2d(0, 0);
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
    public void invoke(long window, double x, double y) {
        lastScroll.set(currentScroll.getX(), currentScroll.getY());
        currentScroll.set(x, y);

        if (eventService == null)
            eventService = CerberusRegistry.getInstance().getService(CerberusEvent.class);
        eventService.executeFullEIF(new ScrollEvent(this, window, currentScroll, getDeltaScroll()));
    }

    public Vector2d getLastScroll() {
        return lastScroll;
    }

    public Vector2d getCurrentScroll() {
        return currentScroll;
    }

    public Vector2d getDeltaScroll() {
        return currentScroll.sub(lastScroll);
    }
}
