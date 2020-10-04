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

package com.cerberustek.commands;

import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.service.terminal.TerminalProcessCommand;
import com.cerberustek.usr.PermissionHolder;
import com.cerberustek.worker.WorkerTask;

import java.util.Scanner;

public class FPSCommand implements TerminalProcessCommand {

    private int frameCounter = 0;
    private double averageFPS = 0;

    private WorkerTask task;

    @Override
    public void exit() {
        CerberusRegistry.getInstance().getService(CerberusRenderer.class)
                .getWorker().decomissionTask(task, CerberusRenderer.GROUP_OTHER);
    }

    @Override
    public boolean execute(PermissionHolder permissionHolder, Scanner scanner, String... args) {
        int delay = 100;
        if (args.length > 0) {
            try {
                delay = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        int repetitions = -1;
        if (args.length > 1) {
            try {
                repetitions = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        CerberusRegistry.getInstance().debug("Starting fps counter...");
        task = CerberusRegistry.getInstance().getService(CerberusRenderer.class)
                .getWorker().submitTask(this::update, CerberusRenderer.GROUP_OTHER, repetitions, delay);
        return true;
    }

    private void update(double time, int rep) {
        double delta = CerberusRegistry.getInstance().getService(CerberusRenderer.class).getPipeline().getDelta();
        if (frameCounter < 60)
            frameCounter++;

        if (averageFPS == 0)
            averageFPS = 1000d / delta;
        else {
            averageFPS -= averageFPS / frameCounter;
            averageFPS += 1000d / (delta * frameCounter);
        }
        CerberusRegistry.getInstance().debugInLine("Average FPS> " + averageFPS);
    }

    @Override
    public String executor() {
        return "fps";
    }

    @Override
    public String usage() {
        return "fps <delay> <repetitions (negative -> infinite)>";
    }

    @Override
    public String requiredPermission() {
        return CerberusRenderer.PERMISSION_COMMAND_FPS;
    }
}
