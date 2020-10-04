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

package com.cerberustek.gui;

import com.cerberustek.CerberusRegistry;
import com.cerberustek.Destroyable;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.logic.math.Vector4f;
import com.cerberustek.CerberusRenderer;
import org.jetbrains.annotations.NotNull;

/**
 * A C component, that contains it's own canvas.
 *
 * The component can be repainted, which will trigger
 * an (possibly asynchronous) call to the paintTo
 * Canvas method.
 *
 * All calls to the paintComponent() method will simply
 * paint the contents of the internal canvas to the
 * target canvas.
 */
public abstract class CFXPane implements CFXComponent, CFXRepaintable, Destroyable {

    /** Internal cerberus canvas */
    private final CFXCanvas canvas;
    /** Renderer service instance */
    private CerberusRenderer renderer;
    /** repaint flag */
    private boolean shouldRepaint;

    public CFXPane(CFXCanvas canvas) {
        this.canvas = canvas;
        shouldRepaint = true;
    }

    /**
     * Will paint the component to the internal canvas.
     */
    public abstract void paintToCanvas();

    @Override
    public void paintComponent(@NotNull CFXCanvas canvas) {
        canvas.drawTexture(getCanvas().getImage(), new Vector2i(0), new Vector2i(0), canvas.getSize(),
                null, new Vector4f(0, 0, 0, 0),
                null, new Vector4f(0, 0, 0, 0),
                null, new Vector4f(0, 0, 0, 0),
                null, new Vector4f(0, 0, 0, 0), true);
    }

    /**
     * Will repaint the pane, possibly asynchronously.
     *
     * If the current thread is a valid gl render thread,
     * the repaint call will be in sync with the method
     * call. Otherwise the repainting will be scheduled
     * for later execution in the gl render thread.
     */
    public void repaint() {
        getRenderer().tryGLTask(t -> paintToCanvas());
        shouldRepaint = false;
    }

    /**
     * Returns the canvas the contents of the pane are
     * drawn to.
     * @return canvas
     */
    @NotNull
    public CFXCanvas getCanvas() {
        return canvas;
    }

    /**
     * Will return the current instance of the cerberus
     * renderer service
     * @return cerberus renderer
     */
    @NotNull
    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }

    @Override
    public void destroy() {
        canvas.destroy();
    }

    @Override
    public void setResolution(Vector2i res) {
        canvas.setResolution(res);
        requestRepaint();

        CerberusRegistry.getInstance().debug("Settings resolution: " + res);
    }

    @Override
    public Vector2i getResolution() {
        return canvas.getResolution();
    }

    @Override
    public boolean shouldRepaint() {
        return shouldRepaint;
    }

    @Override
    public void requestRepaint() {
        shouldRepaint = true;
        if (id() != 0)
            CerberusRegistry.getInstance().getService(CerberusRenderer.class).getGUIManager().requestUpdate(id());
    }
}
