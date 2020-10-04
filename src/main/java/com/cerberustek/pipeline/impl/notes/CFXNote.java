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

import com.cerberustek.CerberusEvent;
import com.cerberustek.event.Event;
import com.cerberustek.event.EventHandler;
import com.cerberustek.event.EventListener;
import com.cerberustek.events.ExceptionEvent;
import com.cerberustek.events.ModificationFraudEvent;
import com.cerberustek.resource.impl.FrameBufferResource;
import com.cerberustek.resource.model.ModelResource;
import com.cerberustek.resource.shader.ShaderResource;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.events.KeyEvent;
import com.cerberustek.geometry.DrawMode;
import com.cerberustek.gui.CFXCanvas;
import com.cerberustek.gui.CFXComponent;
import com.cerberustek.gui.CFXShader;
import com.cerberustek.gui.impl.FlatCanvas;
import com.cerberustek.input.impl.MouseButton;
import com.cerberustek.logic.math.Vector3f;
import com.cerberustek.logic.math.Vector4f;
import com.cerberustek.overlay.OverlayUtil;
import com.cerberustek.pipeline.InputProvider;
import com.cerberustek.pipeline.impl.RenderNote;
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.ShaderBoard;
import com.cerberustek.window.Window;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.opengl.GL11.*;

/**
 * CFX rendering note
 */
@EventHandler(events = {KeyEvent.class})
public class CFXNote extends RenderNote implements EventListener, InputProvider {

    /** The gui root component */
    protected final CFXComponent root;
    /** framebuffer to draw to */
    private InputProvider target;
    /** screen mesh */
    private final ModelResource screen;
    /** base canvas */
    private final CFXCanvas canvas;

    /** Returns is true, if this gui should update
     * every single frame */
    private boolean alwaysUpdate;
    /** Cerberus renderer instance */
    private CerberusRenderer renderer;

    public CFXNote(@NotNull CFXComponent root, @NotNull InputProvider target) {
        getRenderer().getGUIManager().register(root);
        root.setResolution(getRenderer().getWindow().getScreenSize());
        this.root = root;
        this.target = target;
        this.screen = OverlayUtil.getInstance().getScreenMesh();
        this.canvas = new FlatCanvas(root.getResolution());
        this.alwaysUpdate = false;
        canvas.init();
        canvas.setClearColor(new Vector4f(0, 0, 0, 0));
        canvas.setClearNormal(new Vector3f(0));
        canvas.setClearSpecular(new Vector4f(0, 0, 0, 0));
        canvas.setClearEmission(new Vector4f(0, 0, 0, 0));
        canvas.setClearMetallic(new Vector4f(0, 0, 0, 0));
        canvas.setClearDisplacement(new Vector3f(0));
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
            canvas.clear();
            root.paintComponent(canvas);
        }

        ShaderBoard shaderBoard = getRenderer().getShaderBoard();
        ShaderResource shaderResource = getRenderer().getGUIManager().getShader(CFXShader.DRAW);
        if (shaderResource == null) {
            CerberusRegistry.getInstance().warning("[CFX-NOTE]> Failed to get draw shader");
            CerberusEvent cerberusEvent = CerberusRegistry.getInstance().getService(CerberusEvent.class);
            cerberusEvent.executeFullEIF(new ExceptionEvent(CerberusRenderer.class,
                    new NullPointerException()));
            cerberusEvent.executeFullEIF(new ModificationFraudEvent(CerberusRenderer.class));
            return;
        }

        Shader shader = shaderBoard.bindShader(shaderResource);
        if (shader == null) {
            CerberusRegistry.getInstance().warning("[CFX-NOTE]> Failed to bind draw shader");
            CerberusEvent cerberusEvent = CerberusRegistry.getInstance().getService(CerberusEvent.class);
            cerberusEvent.executeFullEIF(new ExceptionEvent(CerberusRenderer.class,
                    new NullPointerException()));
            cerberusEvent.executeFullEIF(new ModificationFraudEvent(CerberusRenderer.class));
            return;
        }

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        shader.update(v);
        getRenderer().getTextureBoard().bindFrameBuffer((FrameBufferResource) target.fetchOutput());
        getRenderer().getTextureBoard().bindTexture(canvas.getImage());
        getRenderer().getGeometryBoard().drawMesh(screen, DrawMode.TRIANGLES);

        glDisable(GL_BLEND);
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

    public void setTarget(@NotNull InputProvider resource) {
        if (getRenderer().getWindow().isGlThread())
            this.target = resource;
    }

    /**
     * Returns the target framebuffer
     * @return target framebuffer
     */
    @Override
    public FrameBufferResource fetchOutput() {
        return (FrameBufferResource) target.fetchOutput();
    }

    @Override
    public boolean onEvent(Event event) {
        assert event instanceof KeyEvent;
        if (((KeyEvent) event).getInputDevice() instanceof MouseButton) {

        }
        return false;
    }
}
