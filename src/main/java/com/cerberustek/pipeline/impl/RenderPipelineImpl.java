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

package com.cerberustek.pipeline.impl;

import com.cerberustek.event.Event;
import com.cerberustek.event.EventHandler;
import com.cerberustek.event.EventListener;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.events.FrameBufferSizeEvent;
import com.cerberustek.pipeline.RenderPipeline;
import com.cerberustek.pipeline.RenderScene;
import com.cerberustek.texture.RenderTarget;
import com.cerberustek.util.PriorityConsumer;
import com.cerberustek.window.Window;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.opengl.GL11.*;

/**
 * One implementation of the render pipeline :)
 */
@EventHandler(events={ FrameBufferSizeEvent.class })
public class RenderPipelineImpl implements RenderPipeline, EventListener {

    /** Very first render note */
    private RenderNote note;
    /** The currently active render target */
    private RenderTarget renderTarget;
    /** The renderer */
    private CerberusRenderer renderer;
    /** The current render scene */
    private RenderScene renderScene;
    /** The current time delta offset on the render pipeline */
    private double currentDelta;

    @Override
    public RenderScene setScene(RenderScene scene) {
        // getRenderer().getWorker().changeStatus(WorkerStatus.SLEEPING);
        if (renderScene != null)
            renderScene.destroy();

        renderScene = scene;
        // getRenderer().getWorker().changeStatus(WorkerStatus.STARTING);
        return renderScene;
    }

    @Override
    public RenderScene getScene() {
        if (renderScene == null)
            setScene(new SimpleScene());
        return renderScene;
    }

    @Override
    public RenderTarget clearRenderTarget() {
        RenderTarget target = getRenderTarget();
        getRenderer().tryGLTask((time) -> glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT));
        return target;
    }

    @Override
    public RenderTarget getRenderTarget() {
        if (renderTarget == null)
            setRenderTarget(getRenderer().getWindow());
        return renderTarget;
    }

    @Override
    public RenderTarget setRenderTarget(@NotNull RenderTarget renderTarget) {
        if (this.renderTarget != renderTarget) {
            getRenderer().tryGLTask((time) -> {
                renderTarget.set();
                this.renderTarget = renderTarget;
            });
        }
        return renderTarget;
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }

    @Override
    public RenderNote append(@NotNull RenderNote note) {
        if (this.note == null)
            return this.note = note;
        else
            return this.note.append(note);
    }

    @Override
    public RenderNote shorten() {
        if (note != null) {

            if (note.hasChild())
                return note.shorten();
            else {
                note.delete(note);
                RenderNote prev = note;
                note = null;
                return prev;
            }
        } else
            return null;
    }

    @Override
    public RenderNote insert(@NotNull RenderNote toInsert, @NotNull RenderNote parentNote) {
        if (note != null)
            return note.insert(toInsert, parentNote);
        else
            return null;
    }

    @Override
    public RenderNote insert(@NotNull RenderNote toInsert, int location) {
        if (note != null)
            return note.insert(toInsert, location);
        else {
            if (location == 0)
                return note = toInsert;
            else
                return null;
        }
    }

    @Override
    public RenderNote delete(@NotNull RenderNote renderNote) {
        if (note != null) {

            RenderNote oldNote = note;
            if (oldNote.equals(renderNote))
                note = oldNote.getChild();
            return oldNote.delete(renderNote);
        }
        return null;
    }

    @Override
    public RenderNote delete(int location) {
        if (note != null) {

            RenderNote oldNote = note;
            if (location == 0)
                note = oldNote.getChild();
            return oldNote.delete(location);
        }
        return null;
    }

    @Override
    public RenderNote get(int location) {
        if (note != null)
            return note.get(location);
        return null;
    }

    @Override
    public void resize() {
        Window window = getRenderer().getWindow();
        if (window != null)
            note.reinitAll(window);
        else
            throw new IllegalStateException("There is currently no active window!");
    }

    @Override
    public void clearNotes() {
        if (note != null) {
            note.clear();
            note = null;
        }
    }

    @Override
    public boolean empty() {
        return note == null || renderScene == null;
    }

    @Override
    public void destroy() {
        if (renderScene != null)
            renderScene.destroy();
        clearNotes();
    }

    @Override
    public void update(double delta) {
        currentDelta = delta;
        if (!empty()) {
            renderScene.updateMatrices(delta);
            note.render(delta);
        } else {
            CerberusRegistry.getInstance().fine("Cannot render anything: there is either no scene or the" +
                    " render pipeline is empty. This will probably fix itself");
        }
    }

    @Override
    public double getDelta() {
        return currentDelta;
    }

    @Override
    public boolean onEvent(Event event) {
        /* This event handler will automatically insure
        * that the entire render pipeline is scaled according
        * to the current framebuffer size.
        * If the size of the main framebuffer is changed in
        * such a way the a FrameBufferSizeEvent gets triggered,
        * all rendering notes are automatically rescaled from
        * here. */

        return getRenderer().nextGLTask(new PriorityConsumer(t -> this.resize()));
    }
}
