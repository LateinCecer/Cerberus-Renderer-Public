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

import com.cerberustek.event.Event;
import com.cerberustek.event.EventHandler;
import com.cerberustek.event.EventListener;
import com.cerberustek.resource.image.TextureResource;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.events.KeyEvent;
import com.cerberustek.gui.CFXCanvas;
import com.cerberustek.gui.CFXComponent;
import com.cerberustek.gui.impl.FlatCanvas;
import com.cerberustek.input.impl.MouseButton;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.logic.math.Vector4f;
import com.cerberustek.pipeline.InputProvider;
import com.cerberustek.pipeline.impl.RenderNote;
import com.cerberustek.window.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Self contained CFX rendering note.
 *
 * This rendering note will render a gui on top of a background
 * texture. The output of this note is an image texture.
 */
@EventHandler(events = {KeyEvent.class})
public class CFXNoteSelfContained extends RenderNote implements EventListener, InputProvider {

    /** The gui root component */
    protected final CFXComponent root;
    /** background */
    private InputProvider background;
    /** base canvas */
    private final CFXCanvas canvas;

    /** Returns is true, if this gui should update
     * every single frame */
    private boolean alwaysUpdate;
    /** Cerberus renderer instance */
    private CerberusRenderer renderer;

    public CFXNoteSelfContained(@NotNull CFXComponent root, @Nullable InputProvider background) {
        getRenderer().getGUIManager().register(root);
        root.setResolution(getRenderer().getWindow().getScreenSize());
        this.root = root;
        this.background = background;
        this.canvas = new FlatCanvas(root.getResolution());
        this.alwaysUpdate = false;
        canvas.init();
        canvas.setClearColor(new Vector4f(0, 0, 0, 0));
    }

    @Override
    public void reinit(Window window) {
        root.setResolution(window.getScreenSize());
        canvas.setResolution(window.getScreenSize());
    }

    @Override
    public void destroy() {
        root.destroy();
        canvas.destroy();
    }

    @Override
    public void update(double v) {
        if (getRenderer().getGUIManager().update(root.id()) || alwaysUpdate) {
            if (background != null)
                canvas.drawTexture(background.fetchOutput(), new Vector2i(0), new Vector2i(0),
                        canvas.getResolution(), false);
            else
                canvas.clear();

            root.paintComponent(canvas);
        }
    }

    /**
     * Returns true, if this gui is set to update once every
     * frame.
     * @return does always update
     */
    public boolean doesAlwaysUpdate() {
        return alwaysUpdate;
    }

    /**
     * Will enable/disable the gui to update every frame.
     * @param value update every frame
     */
    public void setAlwaysUpdate(boolean value) {
        this.alwaysUpdate = value;
    }

    /**
     * Returns the current cerberus rendering instance.
     *
     * Buffered locally for faster lookup times.
     *
     * @return current cerberus renderer
     */
    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }

    /**
     * Will set the render background.
     * @param background render background
     */
    public void setBackground(@Nullable InputProvider background) {
        getRenderer().tryGLTask(t -> {
            CFXNoteSelfContained.this.background = background;
        });
    }

    /**
     * Returns the background texture used by this method.
     * @return background texture.
     */
    public @Nullable InputProvider getBackground() {
        return background;
    }

    /**
     * Returns the target framebuffer
     * @return target framebuffer
     */
    @Override
    public @NotNull TextureResource fetchOutput() {
        return canvas.getImage();
    }

    @Override
    public boolean onEvent(Event event) {
        assert event instanceof KeyEvent;
        if (((KeyEvent) event).getInputDevice() instanceof MouseButton) {

        }
        return false;
    }
}
