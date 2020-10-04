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
import com.cerberustek.logic.math.Matrix4f;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.pipeline.Transformer;
import com.cerberustek.pipeline.impl.SimpleTransformer;
import com.cerberustek.window.Window;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.camera.Camera;

public class PlayerCamera implements Camera {

    private final Transformer transformer;

    private Matrix4f projectionMatrix;

    public PlayerCamera(float fov, float near, float far) {
        this.transformer = new SimpleTransformer();
        initProjection(fov, near, far);
    }

    public void initProjection(float fov, float near, float far) {
        Window window = CerberusRegistry.getInstance().getService(CerberusRenderer.class).getWindow();
        Vector2i windowSize = window.getSize();
        this.projectionMatrix = new Matrix4f().initProjection(-fov, (float) windowSize.getX() / (float)
                windowSize.getY(), near, far);
    }

    @Override
    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    @Override
    public Matrix4f getCameraMatrix() {
        return projectionMatrix.mul(transformer.getRotationMatrix4f().mul(transformer.getInverseScaleMatrix4f()).
                mul(transformer.getInverseTranslationMatrix4f()));
    }

    @Override
    public Transformer getTransformer() {
        return transformer;
    }
}
