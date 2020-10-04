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

package com.cerberustek.shader.code.impl;

import com.cerberustek.CerberusEvent;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.resource.shader.ShaderCodeResource;
import com.cerberustek.resource.shader.impl.ShaderCodeResourceImpl;
import com.cerberustek.events.FileNotFoundErrorEvent;
import com.cerberustek.events.MakeDirErrorEvent;
import com.cerberustek.shader.code.ShaderCodeLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class SimpleShaderCodeLoader implements ShaderCodeLoader {

    private final HashMap<ShaderCodeResource, String> codeMap = new HashMap<>();

    private File shaderFolder;

    public SimpleShaderCodeLoader(@NotNull File shaderFolder) {
        this.shaderFolder = shaderFolder;
        createFolder();
    }

    @Override
    public void removeCode(ShaderCodeResource resource) {
        codeMap.remove(resource);
    }

    @Override
    public boolean hasCode(ShaderCodeResource resource) {
        return codeMap.containsKey(resource);
    }

    @Override
    public String loadCode(ShaderCodeResource resource) throws IOException {
        String code = codeMap.get(resource);
        if (code == null) {
            code = resource.load();
            resource.close();
            codeMap.put(resource, code);
        }
        return code;
    }

    @Override
    public void setFolder(String path) {
        shaderFolder = new File(path);
        createFolder();
    }

    @Override
    public boolean exists(ShaderCodeResource resource) {
        return hasFile(shaderFolder, resource.getBinaryName().split("/"));
    }

    private boolean hasFile(File file, String[] name) {
        return getFile(file, name, 0) != null;
    }

    private File getFile(File file, String[] name, int index) {
        if (index >= name.length)
            return file;

        File[] files = file.listFiles();
        if (files == null)
            return null;

        for (File f : files) {
            if (f != null && f.getName().equals(name[index]))
                return getFile(f, name, index + 1);
        }
        return null;
    }

    @Override
    public File getFolder() {
        return shaderFolder;
    }

    @Override
    public ShaderCodeResource resourceFromName(String name) {
        File file = getFile(shaderFolder, name.split("/"), 0);
        if (file != null) {
            try {
                return new ShaderCodeResourceImpl(name, new FileInputStream(file));
            } catch (FileNotFoundException e) {
                CerberusRegistry.getInstance().getService(CerberusEvent.class).executeFullEIT(
                        new FileNotFoundErrorEvent(file));
            }
        }
        return null;
    }

    @Override
    public void destroy() {
        codeMap.keySet().forEach((resource) -> {
            try {
                resource.close();
            } catch (IOException e) {
                // Do nothing, this probably means that the resource is closed.
            }
        });
        codeMap.clear();
        shaderFolder = null;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        destroy();
    }

    private void createFolder() {
        if (!shaderFolder.exists() || !shaderFolder.isDirectory()) {
            CerberusRegistry.getInstance().critical("Could not find shader folder: " + shaderFolder.getPath());
            if (!shaderFolder.mkdirs()) {
                CerberusRegistry.getInstance().getService(CerberusEvent.class).executeFullEIF(new MakeDirErrorEvent(
                        "Could not create shader folder: " + shaderFolder.getPath()));
                CerberusRegistry.getInstance().critical("Could not create shader folder: " + shaderFolder.getPath());
            }
        }
    }
}
