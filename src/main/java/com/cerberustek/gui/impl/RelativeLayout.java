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
import com.cerberustek.gui.CFXCanvas;
import com.cerberustek.gui.CFXComponent;
import com.cerberustek.gui.CFXLayout;
import com.cerberustek.gui.CFXOrientation;
import org.jetbrains.annotations.NotNull;

public class RelativeLayout implements CFXLayout {

    @Override
    public @NotNull Vector2i getOffset(@NotNull CFXComponent child, @NotNull CFXCanvas canvas) {
        CFXOrientation orientation = child.getOrientation();
        if (!(orientation instanceof RelativeOrientation))
            return new Vector2i(0);

        return canvas.getResolution().toVector2f()
                .mul(((RelativeOrientation) orientation).getRelativeCoord())
                .toVector2i();
    }

    @Override
    public @NotNull Vector2i getSize(@NotNull CFXComponent child, @NotNull CFXCanvas canvas) {
        CFXOrientation orientation = child.getOrientation();
        if (!(orientation instanceof RelativeOrientation))
            return new Vector2i(0);

        return canvas.getResolution().toVector2f()
                .mul(((RelativeOrientation) orientation).getRelativeScale())
                .toVector2i();
    }

    @Override
    public void update(double v) {
        // nothing to do here
    }
}
