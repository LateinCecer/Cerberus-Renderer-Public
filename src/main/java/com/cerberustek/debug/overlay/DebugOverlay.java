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

package com.cerberustek.debug.overlay;

import com.cerberustek.CerberusRegistry;
import com.cerberustek.logic.math.Vector2f;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.overlay.OverlayFrame;
import com.cerberustek.overlay.font.RenderFont;
import com.cerberustek.overlay.font.impl.RenderFontImpl;
import com.cerberustek.overlay.impl.OverlayContainerImpl;
import com.cerberustek.overlay.impl.OverlayTextArea;
import com.cerberustek.pipeline.RenderPipeline;
import com.cerberustek.pipeline.impl.RenderNote;
import com.cerberustek.service.TerminalUtil;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.util.CFXColor;
import com.cerberustek.worker.WorkerPriority;
import com.cerberustek.worker.WorkerTask;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.UUID;

public class DebugOverlay extends OverlayContainerImpl {

    private CerberusRenderer renderer;

    private RenderFont plain;
    private RenderFont bold;

    private OverlayTextArea fpsDisplay;
    private OverlayTextArea renderPerformance;

    private WorkerTask task;

    public DebugOverlay(@NotNull OverlayFrame parent, Vector2i resolution, Vector2f size) {
        super(parent, resolution, size);
        setup();
    }

    public DebugOverlay(OverlayFrame parent, Vector2i resolution, Vector2f size, UUID guiId) {
        super(parent, resolution, size, guiId);
        setup();
    }

    private void setup() {
        String fontName = getRenderer().getSettings().getString("system_font", "Consolas");
        plain = new RenderFontImpl(new Font(fontName, Font.PLAIN, 24));
        bold = new RenderFontImpl(new Font(fontName, Font.BOLD, 24));

        fpsDisplay = new OverlayTextArea(this, new Vector2f(0.25f, 0.05f));
        fpsDisplay.setWrap(false);
        fpsDisplay.setUseColorCodes(true);
        renderPerformance = new OverlayTextArea(this, new Vector2f(0.5f, 0.9f));
        renderPerformance.setWrap(true);
        renderPerformance.setUseColorCodes(true);

        addChild(fpsDisplay, new Vector2f(0, 0));
        addChild(renderPerformance, new Vector2f(0, 0.05f));

        task = getRenderer().getWorker().submitTask(this::infoTrigger, WorkerPriority.MEDIUM,
                CerberusRenderer.GROUP_OTHER, -1, 250);
    }

    private void infoTrigger(double t, int i) {
        updateInfo();
    }

    public void updateInfo() {
        CerberusRenderer renderer = getRenderer();
        RenderPipeline pipeline = renderer.getPipeline();
        float fps = Math.round(100000d / pipeline.getDelta()) / 100f;
        fpsDisplay.setText("FPS: " + CFXColor.AQUA_BLUE + fps + TerminalUtil.ANSI_RESET + " / "
                + CFXColor.AQUA_BLUE + renderer.getSettings().getInteger("framecap", 144), bold);

        StringBuilder builder = new StringBuilder();
        builder.append("RenderPipeline:\n");
        RenderNote note = pipeline.get(0);
        while (note != null) {
            builder.append(CFXColor.GOLDEN)
                    .append(" + ")
                    .append(note.getClass().getSimpleName())
                    .append(": ")
                    .append(CFXColor.AQUA_BLUE)
                    .append(note.averageRenderTime())
                    .append(CFXColor.GOLDEN)
                    .append(" ms\n")
                    .append(TerminalUtil.ANSI_RESET);

            if (note.hasChild())
                note = note.getChild();
            else
                break;
        }
        builder.append("Buffer swap and event poll: ").append(CFXColor.AQUA_BLUE)
                .append(renderer.getWindow().timeBufferSwap()).append(TerminalUtil.ANSI_RESET).append(" ms\n");
        builder.append("Pipeline delta T: ").append(CFXColor.AQUA_BLUE)
                .append(pipeline.getDelta()).append(TerminalUtil.ANSI_RESET).append(" ms\n");
        builder.append("V-Sync: ");
        if (renderer.getWindow().vsync())
            builder.append(CFXColor.GREEN_APPLE).append("ENABLED");
        else
            builder.append(CFXColor.RED_WINE).append("DISABLED");
        builder.append(TerminalUtil.ANSI_RESET).append('\n');
        builder.append("Renderer: ").append(CFXColor.GOLDEN)
                .append(renderer.getWindow().getRenderer()).append(TerminalUtil.ANSI_RESET).append('\n');
        builder.append("OpenGL-version: ").append(CFXColor.GOLDEN)
                .append(renderer.getWindow().getGLVersion()).append(TerminalUtil.ANSI_RESET).append('\n');

        renderPerformance.setText(builder.toString(), bold);
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }

    @Override
    public void destroy() {
        super.destroy();
        getRenderer().getWorker().decomissionTask(task, CerberusRenderer.GROUP_OTHER);
    }
}
