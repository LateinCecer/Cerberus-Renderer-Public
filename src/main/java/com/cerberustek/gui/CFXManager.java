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

package com.cerberustek.gui;

import com.cerberustek.Destroyable;
import com.cerberustek.Initable;
import com.cerberustek.input.KeyBinding;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.resource.shader.ShaderResource;
import com.cerberustek.texture.ImageType;
import com.cerberustek.util.PropertyMap;

import java.util.function.Supplier;

public interface CFXManager extends Destroyable, Initable {

    /**
     * Will return the gui shader from the shader property
     * name.
     * @return gui shader
     */
    PropertyMap<ShaderResource> getShaders();

    /**
     * Will return the gui key bindings.
     * @return key binding map
     */
    PropertyMap<KeyBinding> getKeyBindings();

    /**
     * Will return the gui shader form the shader property
     * map with the specified property name.
     * @param name shader property name
     * @return shader resource
     */
    ShaderResource getShader(String name);

    /**
     * Will return the key binding with the appropriate
     * name from the key bindings property map.
     * @param name name of the key binding
     * @return gui key binding
     */
    KeyBinding getKeyBinding(String name);

    /**
     * Will try to retrieve the shader resource.
     *
     * If the shader resource is not found within the property
     * map, the shader resource will be loaded and stored from
     * the appropriate supplier.
     *
     * @param name name of the shader property
     * @param defaultValue default value supplier
     * @return shader resource
     */
    ShaderResource getShader(String name, Supplier<ShaderResource> defaultValue);

    /**
     * Will try to retrieve the key binding with the specified
     * name.
     *
     * If the key binding is not found within the property
     * map, the keybinding will be loaded and stored from
     * the appropriate supplier.
     *
     * @param name name of the key binding property
     * @param doubleValue default value supplier
     * @return key binding
     */
    KeyBinding getKeyBinding(String name, Supplier<KeyBinding> doubleValue);

    /**
     * Will return the gui shader from the gui shader enum.
     *
     * If the shader resource is not found, it will be
     * generated from the supplier within the gui shader
     * enum constant.
     *
     * @param shader gui shader
     * @return shader enum
     */
    ShaderResource getShader(CFXShader shader);

    /**
     * Will return the gui key binding from the gui key binding
     * enum.
     *
     * If the key binding is not found, it will be generated
     * from the supplier within the gui key binding enum
     * constant.
     *
     * @param keyBinding gui key binding
     * @return key binding enum
     */
    KeyBinding getKeyBinding(CFXKeyBinding keyBinding);

    /**
     * Returns the image type to use for all gui components.
     * @return image type
     */
    ImageType getImageType();

    /**
     * Will request an update for the gui with the specified
     * gui id.
     * @param id gui id
     */
    void requestUpdate(int id);

    /**
     * Will update the gui with the specified gui id, if the
     * gui is registered.
     *
     * If the gui is not registered, this method will do nothing.
     *
     * @param id gui id
     * @return has updated
     */
    boolean update(int id);

    /**
     * Will update all registered gui's for which an update has
     * been requested.
     */
    void update();

    /**
     * Will register a gui with the specified root component
     * and the next free gui id.
     * @param root root component
     * @return gui id
     */
    int register(CFXComponent root);

    /**
     * Will unregister a gui with the specified root component
     * and free the appropriate gui id.
     * @param root component to unregister
     */
    void unregister(CFXComponent root);

    /**
     * Will return the cfx font renderer for the gui-system.
     * @return font renderer
     */
    CFXFontRenderer getFontRenderer();

    /**
     * Returns the dimensions of the texture atlas from the
     * font renderer.
     * @return texture atlas dimensions
     */
    Vector2i getAtlasDimensions();

    /**
     * Returns the amount of space bars representing a single
     * tab.
     *
     * This value will return 4 in most implementations.
     * @return tab in spaces
     */
    int tabInSpaces();

    /**
     * Sets the amount of space bars representing a single
     * tab.
     *
     * This value will return 4 in most implementations.
     * @param spaces spaces
     */
    void setTabInSpaces(int spaces);
}