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

package com.cerberustek.pipeline;

import com.cerberustek.logic.math.Matrix4f;

public interface Transformable {

    /**
     * Updates the matrices of the transformable and all it's children.
     *
     * If the parent transformable is set to null, this method will
     * assume the current transformable as the root transformable.
     *
     * @param delta time since last update
     * @param parent the parent transformable of this
     */
    void updateMatrices(double delta, Transformable parent);

    /**
     * Will update the matrices in an inverse fashion.
     * @param delta time since last update
     * @param child the child transformable of this
     */
    void updateMatricesInverse(double delta, Transformable child);

    /**
     * Returns the world translation matrix of this renderable.
     * @return world translation matrix
     */
    Matrix4f getWorldTranslationMatrix();

    /**
     * Returns the world scale matrix of this renderable
     * @return world scale matrix
     */
    Matrix4f getWorldScaleMatrix();

    /**
     * Returns the world rotation matrix of this renderable.
     * @return world rotation matrix
     */
    Matrix4f getWorldRotationMatrix();

    /**
     * Returns the world render matrix of this renderable.
     * @return world render matrix
     */
    Matrix4f getWorldMatrix();

    /**
     * Returns the transformer of this renderable.
     * @return transformer
     */
    Transformer getTransformer();

    /**
     * creates a clone from the transformable.
     * @return clone
     */
    Transformable copy();
}
