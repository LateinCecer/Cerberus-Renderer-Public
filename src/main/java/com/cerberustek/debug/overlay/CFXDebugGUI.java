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
import com.cerberustek.Destroyable;
import com.cerberustek.logic.math.Vector2f;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.pipeline.InputProvider;
import com.cerberustek.pipeline.RenderPipeline;
import com.cerberustek.pipeline.impl.RenderNote;
import com.cerberustek.pipeline.impl.notes.CFXNoteSelfContained;
import com.cerberustek.window.Window;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.gui.impl.CFXTextPane;
import com.cerberustek.gui.impl.RelativeOrientation;
import com.cerberustek.util.CFXColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;

public class CFXDebugGUI extends CFXNoteSelfContained implements Destroyable {

    private double updateInterval;
    private double timer;

    private int bufferPos;
    private final double[] deltaBuffer;

    private CerberusRenderer renderer;

    public CFXDebugGUI(@NotNull InputProvider target) {
        super(new CFXTextPane(new RelativeOrientation(new Vector2f(1), new Vector2f(0)),
                new Vector2i(1), new Font("Fire Code", Font.BOLD, 14)), target);
        updateInterval = getRenderer().getSettings().getDouble("debug-information-cycle", 500.0) * 1e-3;
        timer = 0;

        bufferPos = 0;
        deltaBuffer = new double[getRenderer().getSettings().getInteger("delta-buffer-cap", 60)];
        Arrays.fill(deltaBuffer, 0.0);
    }

    @Override
    public void update(double v) {
        putDelta(v);

        timer += v;
        if (timer >= updateInterval) {
            fillText(v);
            timer = 0;
        }
        super.update(v);
    }

    private void fillText(double v) {
        CFXTextPane textPane = (CFXTextPane) root;
        CerberusRenderer renderer = getRenderer();
        RenderPipeline pipeline = renderer.getPipeline();

        StringBuilder builder = new StringBuilder();
        builder.append(CFXColor.GREY_BLUE)
                .append("[FPS]\tcurrent> ")
                .append(formatFPS(v))
                .append(CFXColor.GREY_BLUE).append("\t|\taverage> ")
                .append(formatFPS(getAverageDelta()));

        builder.append('\n').append('\n')
                .append(CFXColor.GREY_BLUE)
                .append("Pipeline timings: [Total> ")
                .append(formatNumber(pipeline.getDelta()))
                .append(CFXColor.GREY_BLUE).append(" ms]");
        RenderNote note = pipeline.get(0);
        while (note != null) {
            builder.append('\n')
                    .append(CFXColor.GREY_BLUE)
                    .append("\t# ")
                    .append(note.getClass().getSimpleName())
                    .append("> ")
                    .append(formatNumber(note.averageRenderTime()))
                    .append(CFXColor.GREY_BLUE)
                    .append(" ms");

            if (note.hasChild())
                note = note.getChild();
            else
                break;
        }
        Window window = renderer.getWindow();
        builder.append('\n')
                .append(CFXColor.GREY_BLUE)
                .append("Buffer swap> ")
                .append(formatNumber(window.timeBufferSwap()))
                .append(CFXColor.GREY_BLUE)
                .append(" ms");
        builder.append('\n')
                .append(CFXColor.GREY_BLUE)
                .append("Parallel wait> ")
                .append(formatNumber(renderer.getParallelDelta()))
                .append(CFXColor.GREY_BLUE)
                .append(" ms");
        builder.append('\n')
                .append(CFXColor.GREY_BLUE)
                .append("Dropped frames> ")
                .append(CFXColor.LIME_GREEN)
                .append(renderer.countDroppedFrames());

        builder.append('\n').append('\n');
        builder.append(CFXColor.GREY_BLUE)
                .append("Fullscreen> ")
                .append(formatBoolean(window.isFullScreen()))
                .append(CFXColor.GREY_BLUE)
                .append("\t|\tScreen size> ")
                .append(formatNumber(window.getSize().getX()))
                .append(CFXColor.GREY_BLUE)
                .append(" x ")
                .append(formatNumber(window.getSize().getY()))
                .append('\n');
        builder.append(CFXColor.GREY_BLUE)
                .append("VSync> ")
                .append(formatBoolean(window.vsync()))
                .append('\n');
        builder.append(CFXColor.GREY_BLUE)
                .append("Renderer> ")
                .append(CFXColor.LIME_GREEN)
                .append(window.getRenderer())
                .append('\n');
        builder.append(CFXColor.GREY_BLUE)
                .append("OpenGL> ")
                .append(CFXColor.LIME_GREEN)
                .append(window.getGLVersion())
                .append('\n');

        textPane.enableHighlighting(true);
        textPane.enableTightPacking(true);
        textPane.enableWrapping(false);
        textPane.enableWordWrapping(false);
        textPane.setText(builder.toString());
        textPane.repaint();
    }

    private String formatBoolean(boolean value) {
        if (value)
            return CFXColor.LIME_GREEN + "true";
        else
            return CFXColor.BLOOD_ORANGE + "false";
    }

    private String formatFPS(double delta) {
        float fps = Math.round(100 / delta) / 100f;
        return CFXColor.LIME_GREEN + Float.toString(fps);
    }

    private String formatNumber(double delta) {
        float fps = Math.round(100d * delta) / 100f;
        return CFXColor.LIME_GREEN + Float.toString(fps);
    }

    private void putDelta(double value) {
        deltaBuffer[bufferPos++] = value;

        if (bufferPos == deltaBuffer.length)
            bufferPos = 0;
    }

    private double getAverageDelta() {
        double sum = 0;
        for (double d : deltaBuffer)
            sum += d;
        return sum / deltaBuffer.length;
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }

    public double getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(double updateInterval) {
        this.updateInterval = updateInterval;
    }
}
