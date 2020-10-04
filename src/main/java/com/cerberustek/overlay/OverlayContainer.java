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

package com.cerberustek.overlay;

import com.cerberustek.geometry.DrawMode;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.logic.math.Vector2f;
import com.cerberustek.logic.math.Vector3f;

public interface OverlayContainer extends OverlayFrame {

    void updateChildren(double t);

    void addChild(OverlayFrame child, Vector2f pos);
    void removeChild(OverlayFrame child);

    Vector2f getChildPos(OverlayFrame child);

    static void drawChild(OverlayFrame child, Vector2f pos, CerberusRenderer renderer, double t) {
        if (!child.isVisible())
            return;

        float x = pos.getX();
        float y = pos.getY();

        Vector2f size = child.getSize();

        OverlayUtil util = OverlayUtil.getInstance();
        renderer.getShaderBoard().bindShader(util.getScreenShader());

        util.setColor(new Vector3f(1, 1, 1));
        util.setUVOffset(new Vector2f(0, 0));
        //util.setUVFiltering(new Vector2f(0.0125f * 0.5f * 0.2f, 0.0125f * 0.2f));
        util.setUVFiltering(new Vector2f(0, 0));
        util.setSize(new Vector2f(1, 1));
        util.setScale(size);
        util.setTranslation(new Vector2f(x * 2 - 1 + size.getX(), -y * 2 + 1 - size.getY()));
        util.updateUniforms(t);

        renderer.getTextureBoard().bindTexture(child.getTexture());
        renderer.getGeometryBoard().drawMesh(util.getScreenMesh(), DrawMode.TRIANGLES);
    }

    static void setColor(OverlayFrame child) {
        OverlayUtil util = OverlayUtil.getInstance();
        if (child.isEnabled()) {
            if (child instanceof Selectable && ((Selectable) child).isSelected())
                util.setColor(OverlayUtil.SELECTED_COLOR);
            else
                util.setColor(OverlayUtil.RESET_COLOR);
        } else
            util.setColor(OverlayUtil.DISABLED_COLOR);
    }
}
