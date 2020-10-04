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

package com.cerberustek.camera.impl;

import com.cerberustek.CerberusRegistry;
import com.cerberustek.Updatable;
import com.cerberustek.logic.math.Quaterniond;
import com.cerberustek.logic.math.Vector2d;
import com.cerberustek.logic.math.Vector3d;
import com.cerberustek.logic.math.Vector3f;
import com.cerberustek.input.KeyBinding;
import com.cerberustek.input.impl.CursorPosition;
import com.cerberustek.input.impl.Keyboard;
import com.cerberustek.input.impl.Mouse;
import com.cerberustek.input.impl.SimpleKeyBinding;

public class MoveableCamera extends PlayerCamera implements Updatable {

    private final Keyboard keyboard;
    private final Mouse mouse;

    private final KeyBinding forward;
    private final KeyBinding backward;
    private final KeyBinding right;
    private final KeyBinding left;
    private final KeyBinding up;
    private final KeyBinding down;
    private final KeyBinding rollRight;
    private final KeyBinding rollLeft;
    private final KeyBinding lock;

    private float movementSpeed = 10f;
    private float mouseSenitivity = 0.01f;

    public MoveableCamera(float fov, float near, float far, Keyboard keyboard, Mouse mouse) {
        super(fov, near, far);
        this.keyboard = keyboard;
        this.mouse = mouse;

        forward = new SimpleKeyBinding("forward", keyboard, Keyboard.KEY_W);
        backward = new SimpleKeyBinding("backward", keyboard, Keyboard.KEY_S);
        right = new SimpleKeyBinding("right", keyboard, Keyboard.KEY_D);
        left = new SimpleKeyBinding("left", keyboard, Keyboard.KEY_A);
        up = new SimpleKeyBinding("up", keyboard, Keyboard.KEY_SPACE);
        down = new SimpleKeyBinding("down", keyboard, Keyboard.KEY_LEFT_SHIFT);
        rollLeft = new SimpleKeyBinding("roll_left", keyboard, Keyboard.KEY_Q);
        rollRight = new SimpleKeyBinding("roll_right", keyboard, Keyboard.KEY_E);
        lock = new SimpleKeyBinding("bind_cursor", keyboard, Keyboard.KEY_R);
    }

    @Override
    public void update(double v) {
        /* ###########
        Calc movement
         ########### */
        double mod = movementSpeed * v;
        Quaterniond rotation = getTransformer().getRotation();
        Vector3d add = new Vector3d(0, 0, 0);
        if (forward.isPressed())
            add.addSelf(rotation.getForward());
        if (backward.isPressed())
            add.addSelf(rotation.getBack());
        if (left.isPressed())
            add.addSelf(rotation.getRight());
        if (right.isPressed())
            add.addSelf(rotation.getLeft());
        if (up.isPressed())
            add.addSelf(rotation.getUp());
        if (down.isPressed())
            add.addSelf(rotation.getDown());
        getTransformer().setTranslation(getTransformer().getTranslation().add(add.normalized().mul(mod)));

        /* ###########
        Calc rotation
         ########### */
        CursorPosition cursor = mouse.getCursorPosition();
        if (cursor.isLocked()) {
            Vector2d deltaPos = cursor.getDeltaPos();

            mod = mouseSenitivity * Math.sqrt(v);
            Quaterniond pitch = new Quaterniond(0, 0, 0, 1).initRotation(mod * deltaPos.getY(), rotation.getLeft());
            Quaterniond yaw = new Quaterniond(0, 0, 0, 1).initRotation(mod * deltaPos.getX(), new Vector3f(0, 1, 0));
            Quaterniond roll = new Quaterniond(0, 0, 0, 1).initRotation(0, rotation.getForward());

            cursor.reset();

            getTransformer().rotate(pitch);
            getTransformer().rotate(yaw);
            getTransformer().rotate(roll);
        }

        if (lock.isTyped()) {
            cursor.lock(!cursor.isLocked());
            // cursor.hide(cursor.isLocked());
            cursor.reset();
            CerberusRegistry.getInstance().debug("Cursor is now " + (cursor.isLocked() ? "locked" : "unlocked") + "!");
        }
    }

    public float getMovementSpeed() {
        return movementSpeed;
    }

    public void setMovementSpeed(float movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    public float getMouseSenitivity() {
        return mouseSenitivity;
    }

    public void setMouseSenitivity(float mouseSenitivity) {
        this.mouseSenitivity = mouseSenitivity;
    }
}
