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

package com.cerberustek.texture;

import static org.lwjgl.opengl.GL30.*;

public enum AttachmentType {

    COLOR_00(GL_COLOR_ATTACHMENT0),
    COLOR_01(GL_COLOR_ATTACHMENT1),
    COLOR_02(GL_COLOR_ATTACHMENT2),
    COLOR_03(GL_COLOR_ATTACHMENT3),
    COLOR_04(GL_COLOR_ATTACHMENT4),
    COLOR_05(GL_COLOR_ATTACHMENT5),
    COLOR_06(GL_COLOR_ATTACHMENT6),
    COLOR_07(GL_COLOR_ATTACHMENT7),
    COLOR_08(GL_COLOR_ATTACHMENT8),
    COLOR_09(GL_COLOR_ATTACHMENT9),
    COLOR_10(GL_COLOR_ATTACHMENT10),
    COLOR_11(GL_COLOR_ATTACHMENT11),
    COLOR_12(GL_COLOR_ATTACHMENT12),
    COLOR_13(GL_COLOR_ATTACHMENT13),
    COLOR_14(GL_COLOR_ATTACHMENT14),
    COLOR_15(GL_COLOR_ATTACHMENT15),
    COLOR_16(GL_COLOR_ATTACHMENT16),
    COLOR_17(GL_COLOR_ATTACHMENT17),
    COLOR_18(GL_COLOR_ATTACHMENT18),
    COLOR_19(GL_COLOR_ATTACHMENT19),
    COLOR_20(GL_COLOR_ATTACHMENT20),
    COLOR_21(GL_COLOR_ATTACHMENT21),
    COLOR_22(GL_COLOR_ATTACHMENT22),
    COLOR_23(GL_COLOR_ATTACHMENT23),
    COLOR_24(GL_COLOR_ATTACHMENT24),
    COLOR_25(GL_COLOR_ATTACHMENT25),
    COLOR_26(GL_COLOR_ATTACHMENT26),
    COLOR_27(GL_COLOR_ATTACHMENT27),
    COLOR_28(GL_COLOR_ATTACHMENT28),
    COLOR_29(GL_COLOR_ATTACHMENT29),
    COLOR_30(GL_COLOR_ATTACHMENT30),
    COLOR_31(GL_COLOR_ATTACHMENT31),

    DEPTH(GL_DEPTH_ATTACHMENT);

    private final int id;

    AttachmentType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
