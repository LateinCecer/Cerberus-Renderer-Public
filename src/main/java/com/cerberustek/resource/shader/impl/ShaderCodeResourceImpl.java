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

package com.cerberustek.resource.shader.impl;

import com.cerberustek.CerberusEvent;
import com.cerberustek.exceptions.ResourceException;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.events.ResourceErrorEvent;
import com.cerberustek.resource.shader.ShaderCodeResource;
import com.cerberustek.shader.code.ShaderCodeLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ShaderCodeResourceImpl implements ShaderCodeResource {

    private final InputStream inputStream;
    private final String name;

    public ShaderCodeResourceImpl(String name, InputStream inputStream) {
        this.name = name;
        this.inputStream = inputStream;
    }

    @Override
    public String load() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();
        String line;

        try {
            ShaderCodeLoader loader = CerberusRegistry.getInstance().getService(CerberusRenderer.class).
                    getShaderBoard().getShaderCodeLoader();
            while ((line = reader.readLine()) != null) {

                if (line.startsWith("#include<")) {
                    String path = line.substring(9, line.length() - 1);
                    ShaderCodeResource include = loader.resourceFromName(path);
                    if (include == null) {
                        CerberusRegistry.getInstance().critical("Could not find implemented shader code" +
                                " resource \"" + path + "\"!");
                        continue;
                    }

                    builder.append(loader.loadCode(include)).append('\n');
                    continue;
                }
                builder.append(line).append('\n');
            }
            reader.close();
        } catch (IOException e) {
            if (!CerberusRegistry.getInstance().getService(CerberusEvent.class).executeFullEIT(
                    new ResourceErrorEvent(this)))
                throw new ResourceException(this, "Could not load shader program!");
        }
        return builder.toString();
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    @Override
    public String getBinaryName() {
        return name;
    }
}
