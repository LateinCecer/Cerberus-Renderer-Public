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

package com.cerberustek.util;

public interface PropertyMap<T> {

    /**
     * Creates a new property entry with the specified name
     * and value.
     *
     * If the name is already taken by another property, this
     * method will throw an IllegalStateException.
     * @param name property name
     * @param property property value
     * @throws IllegalStateException name taken
     */
    void createProperty(String name, T property);

    /**
     * Deletes a property with the specified name.
     *
     * If there is no property with that name to begin
     * with, nothing will happen.
     * @param name name of property to delete
     */
    void deleteProperty(String name);

    /**
     * Will set the value of a property with the specified
     * name.
     *
     * If there is no property registered to the given name
     * this method will throw an IllegalStateException.
     * @param name name of property to set the value of
     * @param property new value of the property
     * @throws IllegalStateException if property not found
     */
    void setProperty(String name, T property);

    /**
     * Similar to set property, but will create the property
     * with the specified name and value instead of throwing
     * and Exception if there is no property with that name.
     *
     * @param name property name
     * @param property property value
     */
    void insertProperty(String name, T property);

    /**
     * Returns true if this property map holds a property
     * with the specified name.
     *
     * @param name property name to look for
     * @return has property?
     */
    boolean hasProperty(String name);

    /**
     * Will return the value of the property with the
     * specified name.
     *
     * If there is no property with that name registered to
     * the map, this method will return null.
     * @param name name of property to look up
     * @return property value
     */
    T getProperty(String name);

    /**
     * Will return the value of the property with the
     * specified name and try to cast it to the specified
     * class type.
     *
     * If there is no property with that name registered to
     * the map, this method will return null.
     * If the property value retrieved does not implement the
     * specified class type, this method will throw an
     * IllegalStateException
     * @param name name of property to look up
     * @return property value
     * @throws IllegalStateException property value does not
     *          match class type
     */
    <D> D getProperty(String name, Class<D> clazz);

    /**
     * Will return the value of the property with the
     * specified name.
     *
     * If there is no property with that name registered to
     * the map, this method will create a new property with
     * the specified name and default value, and return the
     * default value.
     * @param name name of property to look up
     * @return property value
     */
    T getProperty(String name, T defaultValue);

    /**
     * Will return the value of the property with the
     * specified name and try to cast it to the specified
     * class type.
     *
     * If there is no property with that name registered to
     * the map, this method will create a new property with
     * the specified name and default value, and return the
     * default value.
     * If the property value retrieved or the default value
     * do not implement the specified class type, this method
     * will throw an IllegalStateException.
     * @param name name of property to look up
     * @return property value
     * @throws IllegalStateException property value does not
     *          match class type
     */
    <D> D getProperty(String name, Class<D> clazz, T defaultValue);
}
