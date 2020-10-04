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

package com.cerberustek.pipeline.impl.notes;

import com.cerberustek.pipeline.impl.RenderNote;
import com.cerberustek.window.Window;

public class PartialActivationNote extends RenderNote {

    private RenderNote other;
    private double timer;
    private double delay;

    public PartialActivationNote(RenderNote other, double delay) {
        this.delay = delay;
        this.timer = 0;
        this.other = other;
    }

    @Override
    public void destroy() {
        other.destroy();
    }

    @Override
    public void update(double v) {
        timer += v;
        if (timer >= delay) {
            other.update(timer);
            timer = timer - delay;
        }
    }

    public RenderNote getOther() {
        return other;
    }

    public void setOther(RenderNote other) {
        this.other = other;
    }

    public double getTimer() {
        return timer;
    }

    public void setTimer(double timer) {
        this.timer = timer;
    }

    public double getDelay() {
        return delay;
    }

    public void setDelay(double delay) {
        this.delay = delay;
    }

    @Override
    public void reinit(Window window) {
        other.reinit(window);
    }
}
