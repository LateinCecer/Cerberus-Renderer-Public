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

package com.cerberustek.gui.impl;

import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.logic.math.Vector3f;
import com.cerberustek.logic.math.Vector4f;
import com.cerberustek.gui.CFXCanvas;
import com.cerberustek.gui.CFXCaret;
import com.cerberustek.gui.CFXCaretRenderer;

public class CFXSimpleCaretRenderer implements CFXCaretRenderer {

    private final CFXCaret caret;
    private final CFXTextField textField;

    private Vector3f color;

    public CFXSimpleCaretRenderer(CFXCaret caret, CFXTextField textField) {
        this.caret = caret;
        this.textField = textField;
        color = new Vector3f(1f);
    }

    @Override
    public void render(Vector2i pixelPos, CFXCanvas canvas) {
        float opacity = 0.75f;
        canvas.drawRectangle(pixelPos.sub(1, 0), new Vector2i(2, textField.getFont().getSize()), new Vector4f(color, opacity));
    }

    @Override
    public CFXCaret getCaret() {
        return caret;
    }
}
