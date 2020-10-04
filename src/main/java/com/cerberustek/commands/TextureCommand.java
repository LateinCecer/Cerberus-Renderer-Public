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
import com.cerberustek.service.terminal.TerminalCommand;
import com.cerberustek.texture.TextureBoard;
import com.cerberustek.usr.PermissionHolder;

import java.util.Scanner;

public class TextureCommand implements TerminalCommand {
    @Override
    public boolean execute(PermissionHolder permissionHolder, Scanner scanner, String... args) {
        if (args.length < 1)
            return false;

        String subcommnad = args[0].toLowerCase();
        switch (subcommnad) {
            case "current":
                TextureBoard board = CerberusRegistry.getInstance().getService(CerberusRenderer.class).getTextureBoard();
                CerberusRegistry.getInstance().debug("Currently bound shader: " + board.getBoundTexture());
                return true;
        }
        return false;
    }

    @Override
    public String executor() {
        return "texture";
    }

    @Override
    public String usage() {
        return "texture <list>";
    }

    @Override
    public String requiredPermission() {
        return CerberusRenderer.PERMISSION_COMMAND_TEXTURE;
    }
}
