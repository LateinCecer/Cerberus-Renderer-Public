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

package com.cerberustek.geometry;

public enum DataType {

    SCALAR(1),
    VEC2(2),
    VEC3(3),
    VEC4(4),
    MAT2(4),
    MAT3(9),
    MAT4(16),
    MAT2x3(6),
    MAT2x4(8),
    MAT3x2(6),
    MAT3x4(12),
    MAT4x2(8),
    MAT4x3(12);

    private final int numComponents;

    DataType(int numComponents) {
        this.numComponents = numComponents;
    }

    public int getNumComponents() {
        return numComponents;
    }

    public static DataType fromName(String name) {
        return valueOf(name.toUpperCase());
    }
}
