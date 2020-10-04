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

package com.cerberustek.pipeline.impl;

import com.cerberustek.logic.math.Matrix4f;
import com.cerberustek.pipeline.Transformable;
import com.cerberustek.pipeline.Transformer;

@SuppressWarnings("DuplicatedCode")
public class TransformableBase implements Transformable {

    protected final Transformer transformer;

    protected Matrix4f translationMatrix;
    protected Matrix4f scaleMatrix;
    protected Matrix4f rotationMatrix;
    protected Matrix4f renderMatrix;

    public TransformableBase(Transformer transformer) {
        this.transformer = transformer;
    }

    public TransformableBase() {
        transformer = new SimpleTransformer();
    }

    public TransformableBase(Transformable head) {
        transformer = head.getTransformer();
        translationMatrix = head.getWorldTranslationMatrix();
        scaleMatrix = head.getWorldScaleMatrix();
        rotationMatrix = head.getWorldRotationMatrix();
        renderMatrix = head.getWorldMatrix();
    }

    @Override
    public void updateMatrices(double delta, Transformable parent) {
        Matrix4f translation = transformer.getTranslationMatrix4f();
        Matrix4f scale = transformer.getScaleMatrix4f();
        Matrix4f rotation = transformer.getRotationMatrix4f();

        if (parent != null) {
            translationMatrix = parent.getWorldMatrix().mul(translation);
            scaleMatrix = parent.getWorldScaleMatrix().mul(scale);
            rotationMatrix = parent.getWorldRotationMatrix().mul(rotation);
        } else {
            translationMatrix = translation;
            scaleMatrix = scale;
            rotationMatrix = rotation;
        }

        renderMatrix = translationMatrix.mul(rotation.mul(scale));
        // If the renderable has any children, those should be updated here
    }

    @Override
    public void updateMatricesInverse(double delta, Transformable parent) {
        Matrix4f translation = transformer.getTranslationMatrix4f();
        Matrix4f scale = transformer.getScaleMatrix4f();
        Matrix4f rotation = transformer.getRotationMatrix4f();

        if (parent != null) {
            translationMatrix = translation.mul(parent.getWorldMatrix());
            scaleMatrix = scale.mul(parent.getWorldScaleMatrix());
            rotationMatrix = rotation.mul(parent.getWorldRotationMatrix());
        } else {
            translationMatrix = translation;
            scaleMatrix = scale;
            rotationMatrix = rotation;
        }

        renderMatrix = scale.mul(rotation).mul(translationMatrix);
        // If the renderable has any children, those should be updated here
    }

    @Override
    public Matrix4f getWorldTranslationMatrix() {
        return translationMatrix;
    }

    @Override
    public Matrix4f getWorldScaleMatrix() {
        return scaleMatrix;
    }

    @Override
    public Matrix4f getWorldRotationMatrix() {
        return rotationMatrix;
    }

    @Override
    public Matrix4f getWorldMatrix() {
        return renderMatrix;
    }

    @Override
    public Transformer getTransformer() {
        return transformer;
    }

    @Override
    public Transformable copy() {
        return new TransformableBase(this);
    }
}
