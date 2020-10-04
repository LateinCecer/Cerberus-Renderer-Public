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
import com.cerberustek.events.CursorPositionEvent;
import com.cerberustek.logic.math.Vector2d;
import com.cerberustek.resource.image.ImageResource;
import com.cerberustek.window.Window;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.input.InputDevice;
import org.lwjgl.glfw.GLFWCursorPosCallback;

import static org.lwjgl.glfw.GLFW.*;

public class CursorPosition extends GLFWCursorPosCallback implements InputDevice {

    private boolean lock;

    private Window window;

    private Vector2d currentPos;
    private Vector2d lastPos;

    private CerberusEvent eventService;

    public CursorPosition() {
        this.currentPos = new Vector2d(0, 0);
        this.lastPos = new Vector2d(0, 0);
        this.lock = false;
    }

    public void hide(final boolean hidden) {
        CerberusRegistry.getInstance().getService(CerberusRenderer.class).tryGLTask((deltaT) ->
                glfwSetInputMode(window.id(), GLFW_CURSOR, hidden ? GLFW_CURSOR_HIDDEN : GLFW_CURSOR_NORMAL));
    }

    public void custom(final ImageResource resource) {
        // TODO
    }

    public void lock(boolean value) {
        this.lock = value;
        CerberusRegistry.getInstance().getService(CerberusRenderer.class).tryGLTask(deltaT ->
                glfwSetInputMode(window.id(), GLFW_CURSOR, value ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL));
    }

    public boolean isLocked() {
        return lock;
    }

    public void setPosition(final Vector2d position) {
        CerberusRegistry.getInstance().getService(CerberusRenderer.class).tryGLTask((deltaT) -> {
            glfwSetCursorPos(window.id(), position.getX(), position.getY());
            invoke(window.id(), position.getX(), position.getY());
        });
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

    @Override
    public void invoke(long window, double x, double y) {
        lastPos.set(currentPos.getX(), currentPos.getY());
        currentPos.set(x, y);

        if (eventService == null)
            eventService = CerberusRegistry.getInstance().getService(CerberusEvent.class);
        eventService.executeFullEIF(new CursorPositionEvent(this, window, currentPos, getDeltaPos()));
    }

    public void reset() {
        if (lock) {
            currentPos.set(0, 0);
            glfwSetCursorPos(window.id(), 0, 0);
        } else {
            Vector2d center = this.window.getSize().toVector2d().div(2d);
            currentPos.set(center.getX(), center.getY());
            lastPos.set(center.getX(), center.getY());
        }
    }

    public Vector2d getCurrentPos() {
        return currentPos;
    }

    public Vector2d getLastPos() {
        return lastPos;
    }

    public Vector2d getDeltaPos() {
        if (lock)
            return currentPos;
        else
            return lastPos.sub(currentPos).div(window.getScreenSize().toVector2d());
    }
}
