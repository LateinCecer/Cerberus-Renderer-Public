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

import com.cerberustek.logic.math.Matrix4f;
import com.cerberustek.pipeline.Transformer;
import com.cerberustek.camera.Camera;

public class DummyCamera implements Camera {

    private Matrix4f projection;
    private Matrix4f cameraMatrix;

    public DummyCamera(Matrix4f projection, Matrix4f camera) {
        this.projection = projection;
        this.cameraMatrix = camera;
    }

    @Override
    public Matrix4f getProjectionMatrix() {
        return projection;
    }

    @Override
    public Matrix4f getCameraMatrix() {
        return cameraMatrix;
    }

    @Override
    public Transformer getTransformer() {
        return null;
    }
}
