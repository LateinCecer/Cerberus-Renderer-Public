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

package com.cerberustek.shader;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL43.*;

public enum ShaderType {

    GEOMETRY_SHADER(GL_GEOMETRY_SHADER),
    FRAGMENT_SHADER(GL_FRAGMENT_SHADER),
    VERTEX_SHADER(GL_VERTEX_SHADER),
    TESSALLATION_CONTROL_SHADER(GL_TESS_CONTROL_SHADER),
    TESSALLATION_EVALUATION_SHADER(GL_TESS_EVALUATION_SHADER),
    COMPUTE(GL_COMPUTE_SHADER);

    private final int glType;

    ShaderType(int glType) {
        this.glType = glType;
    }

    public int getGlType() {
        return glType;
    }
}
