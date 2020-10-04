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

import com.cerberustek.Initable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface InputBoard extends Initable {

    /**
     * Adds an input device to the input board and assign
     * it to the specified specifications.
     *
     * If an other device is already listed under the
     * specified specifications, this method will throw
     * an illegal state exception.
     * If the device you are trying to register is already
     * registered, this method will do nothing.
     *
     * @param spec input device spec
     * @param device input device
     * @throws IllegalStateException is thrown, if an other
     *          device is already registered to the input
     *          board with the specified specs
     */
    void addInputDevice(@NotNull InputDeviceSpec spec, @NotNull InputDevice device) throws IllegalStateException;

    /**
     * Will remove an input device from the input board.
     * @param spec specifications to remove
     */
    void removeInputDevice(@NotNull InputDeviceSpec spec);

    /**
     * Adds an input device to the input board and assign it
     * to the specified specifications.
     *
     * If an other device is already listed under the specified
     * specifications, this method will replace the old
     * device with the new one.
     *
     * @param spec device specifications
     * @param device device to register
     */
    void replaceInputDevice(@NotNull InputDeviceSpec spec, @Nullable InputDevice device);

    /**
     * Will return true, if an input device is assigned to
     * the specified specifications.
     *
     * @param spec input device spec
     * @return contains input device
     */
    boolean hasInputDevice(@NotNull InputDeviceSpec spec);

    /**
     * Will return true, if an input device is assigned to
     * the specified specifications and the registered input
     * device matches the specified one.
     *
     * @param spec input device spec
     * @param device input device
     * @return contains input device
     */
    boolean hasInputDevice(@NotNull InputDeviceSpec spec, @NotNull InputDevice device);

    /**
     * Returns the input device that is assigned to the
     * specified specifications.
     *
     * If no device is currently assigned to the specs,
     * this method will return null.
     *
     * @param spec input device specifications
     * @return input device
     */
    @Nullable InputDevice getInputDevice(@NotNull InputDeviceSpec spec);

    /**
     * Returns the input device that is assigned to the
     * specified specifications.
     *
     * If no device is currently assigned to the specs,
     * this method will return null.
     *
     * @param spec input device specifications
     * @param clazz input device class
     * @param <T> input device type
     * @return input device
     */
    <T extends InputDevice> @Nullable T getInputDevice(@NotNull InputDeviceSpec spec, @NotNull Class<T> clazz);
}
