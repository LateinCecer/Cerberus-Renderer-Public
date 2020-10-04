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

package com.cerberustek.util.impl;

import com.cerberustek.util.PropertyMap;

import java.util.HashMap;

public class SimplePropertyMap<T> implements PropertyMap<T> {

    private final HashMap<String, T> map = new HashMap<>();

    @Override
    public void createProperty(String name, T property) {
        if (hasProperty(name))
            throw new IllegalStateException("Property with name: \"" + name +
                    "\" is already known to the map!");
        map.put(name, property);
    }

    @Override
    public void deleteProperty(String name) {
        map.remove(name);
    }

    @Override
    public void setProperty(String name, T property) {
        if (!hasProperty(name))
            throw new IllegalStateException("Property with name: \"" + name +
                    "\" is unknown to the map!");
        map.replace(name, property);
    }

    @Override
    public void insertProperty(String name, T property) {
        if (hasProperty(name))
            map.replace(name, property);
        else
            map.put(name, property);
    }

    @Override
    public boolean hasProperty(String name) {
        return map.containsKey(name);
    }

    @Override
    public T getProperty(String name) {
        return map.get(name);
    }

    @Override
    public <D> D getProperty(String name, Class<D> clazz) {
        T property = getProperty(name);
        if (property == null)
            return null;

        if (clazz.isInstance(property))
            return clazz.cast(property);
        return null;
        /*throw new IllegalStateException("Property with name: \"" + name +
                "\" is not an instance of class " + clazz + "!");*/
    }

    @Override
    public T getProperty(String name, T defaultValue) {
        T property = getProperty(name);
        if (property == null)
            map.put(name, property = defaultValue);
        return property;
    }

    @Override
    public <D> D getProperty(String name, Class<D> clazz, T defaultValue) {
        D property = getProperty(name, clazz);
        if (property == null) {
            if (!clazz.isInstance(defaultValue))
                return null;
                /*throw new IllegalArgumentException("The default value " + defaultValue +
                        " is not an instance of class " + clazz + "!");*/
            map.put(name, defaultValue);
            return clazz.cast(defaultValue);
        }
        return property;
    }
}
