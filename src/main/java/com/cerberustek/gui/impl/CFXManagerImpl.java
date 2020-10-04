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

package com.cerberustek.gui.impl;

import com.cerberustek.CerberusEvent;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.events.ExceptionEvent;
import com.cerberustek.gui.*;
import com.cerberustek.input.KeyBinding;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.resource.shader.ShaderResource;
import com.cerberustek.settings.Settings;
import com.cerberustek.settings.impl.SettingsImpl;
import com.cerberustek.texture.ImageType;
import com.cerberustek.util.PropertyMap;
import com.cerberustek.util.impl.SimplePropertyMap;
import com.cerberustek.CerberusRenderer;

import java.io.File;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Supplier;

public class CFXManagerImpl implements CFXManager {

    private final PropertyMap<ShaderResource> shaderProperties;
    private final PropertyMap<KeyBinding> keyBindingProperties;
    private final HashMap<Integer, CFXComponent> registry;
    private final HashSet<Integer> requests;
    private final CFXFontRenderer fontRenderer;

    private Settings settings;
    private Vector2i atlasDim;

    public CFXManagerImpl() {
        shaderProperties = new SimplePropertyMap<>();
        keyBindingProperties = new SimplePropertyMap<>();
        registry = new HashMap<>();
        requests = new HashSet<>();
        fontRenderer = new CFXFontRendererImpl();
    }

    @Override
    public PropertyMap<ShaderResource> getShaders() {
        return shaderProperties;
    }

    @Override
    public PropertyMap<KeyBinding> getKeyBindings() {
        return keyBindingProperties;
    }

    @Override
    public ShaderResource getShader(String name) {
        return shaderProperties.getProperty(name);
    }

    @Override
    public KeyBinding getKeyBinding(String name) {
        return keyBindingProperties.getProperty(name);
    }

    @Override
    public ShaderResource getShader(String name, Supplier<ShaderResource> defaultValue) {
        ShaderResource resource = shaderProperties.getProperty(name);
        if (resource != null)
            return resource;

        // load shader
        resource = defaultValue.get();
        if (resource == null) {
            CerberusRegistry.getInstance().warning("Could not generate shader resource \""
                    + name + "\" for GUI manager");
            CerberusRegistry.getInstance().getService(CerberusEvent.class)
                    .executeFullEIF(new ExceptionEvent(CerberusRenderer.class, new NullPointerException()));
            return null;
        }
        return shaderProperties.getProperty(name, resource);
    }

    @Override
    public KeyBinding getKeyBinding(String name, Supplier<KeyBinding> doubleValue) {
        KeyBinding keyBinding = keyBindingProperties.getProperty(name);
        if (keyBinding != null)
            return keyBinding;

        // load shader
        keyBinding = doubleValue.get();
        if (keyBinding == null) {
            CerberusRegistry.getInstance().warning("Could not generate key binding \"" + name
                    + "\" for GUI manager");
            CerberusRegistry.getInstance().getService(CerberusEvent.class)
                    .executeFullEIF(new ExceptionEvent(CerberusRenderer.class, new NullPointerException()));
            return null;
        }
        return keyBindingProperties.getProperty(name, keyBinding);
    }

    @Override
    public ShaderResource getShader(CFXShader shader) {
        return getShader(shader.name(), shader.defaultResource());
    }

    @Override
    public KeyBinding getKeyBinding(CFXKeyBinding keyBinding) {
        return getKeyBinding(keyBinding.name(), keyBinding.defaultResource());
    }

    @Override
    public ImageType getImageType() {
        return ImageType.RGBA_16_FLOAT;
    }

    @Override
    public void requestUpdate(int id) {
        requests.add(id);
    }

    @Override
    public boolean update(int id) {
        if (requests.contains(id)) {
            CFXComponent root = registry.get(id);
            if (root instanceof CFXRepaintable)
                ((CFXRepaintable) root).repaint();

            requests.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public void update() {
        try {
            for (int id : requests) {
                CFXComponent root = registry.get(id);
                if (root instanceof CFXRepaintable)
                    ((CFXRepaintable) root).repaint();

                requests.clear();
            }
        } catch (ConcurrentModificationException e) {
            // bad timing, just try again
            update();
        }
    }

    @Override
    public int register(CFXComponent root) {
        int id = nextId();
        root.register(id);
        registry.put(id, root);
        return id;
    }

    @Override
    public void unregister(CFXComponent root) {
        if (root != null && root.id() > 0)
            registry.remove(root.id(), root);
    }

    @Override
    public CFXFontRenderer getFontRenderer() {
        return fontRenderer;
    }

    @Override
    public Vector2i getAtlasDimensions() {
        return atlasDim;
    }

    @Override
    public int tabInSpaces() {
        return settings.getInteger("spaces-per-tab", 4);
    }

    @Override
    public void setTabInSpaces(int spaces) {
        settings.setInteger("spaces-per-tab", spaces);
    }

    private int nextId() {
        int id = 1;
        while (registry.containsKey(id))
            id++;
        return id;
    }

    @Override
    public void destroy() {
        fontRenderer.destroy();
        settings.destroy();
    }

    @Override
    public void init() {
        CerberusRenderer renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        settings = new SettingsImpl(new File(renderer.getSettings().getString("CFX-Settings",
                "config/cfx-settings.properties")), false);
        settings.init();

        atlasDim = new Vector2i(
                settings.getInteger("atlas-width", 64),
                settings.getInteger("atlas-height", 64));
        CerberusRegistry.getInstance().debug("loaded CFX-GUI Manager");
    }
}
