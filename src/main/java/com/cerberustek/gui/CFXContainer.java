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

import com.cerberustek.logic.math.Vector2i;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * A C Container is a C Component that contains child components.
 *
 * The position and size of the these child components may be
 * dynamically dictated specified by the layout manager chosen
 * for this container.
 */
public interface CFXContainer extends CFXComponent, CFXRepaintable {

    /**
     * Will get the layout of the C component.
     *
     * The layout contains information about the position
     * and size of the children components of this
     * container.
     * This is similar to the Java Swing LayoutManager.
     * @return layout
     */
    @NotNull CFXLayout getLayout();

    /**
     * Will set the container's layout.
     *
     * The layout contains information about the position
     * and size of the children components of this
     * container.
     * This is similar to the Java Swing LayoutManager.
     * @param layout layout of the container
     */
    void setLayout(@NotNull CFXLayout layout);

    /**
     * Will trigger a repaint of (possibly) this component
     * and all child components.
     *
     * This is only relevant for C panes, but there has to
     * be some kind of tree structure support to actually
     * cause the sequential updating of panes throughout
     * the gui.
     */
    void repaint();

    /**
     * Returns all child components of the container
     * @return container contents
     */
    Collection<CFXComponent> getContents();

    /**
     * Adds a component to the container
     * @param child component to add
     */
    void add(CFXComponent child);

    /**
     * Removes a component from the container
     *
     * Components will only be removed from the set of direct
     * children to this container.
     *
     * @param child component to remove
     */
    void remove(CFXComponent child);

    /**
     * Returns true, if this container contains the specified
     * child component.
     *
     * This method will not check if child components to this
     * container contain the specified component.
     *
     * @param child component to look for
     * @return does contain component
     */
    boolean contains(CFXComponent child);

    /**
     * Will query through all children to this container and
     * find all components that contain the specified point.
     *
     * The point has to be provided in pixels counted from
     * the origin point of this component.
     *
     * @param point point to query on in pixels from origin
     * @return all children that intersect the point
     */
    Collection<CFXComponent> query(Vector2i point);

    /**
     * Will query through all children to this container and
     * find all components that contain the specified point.
     *
     * The point has to be provided in pixels counted from
     * the origin point of this component.
     * The queried components will be deposited inside the
     * specified collection. The very same collection will
     * be returned by this method.
     *
     * @param point point to query
     * @param collection collection to put the components in
     * @return original collection + all children that
     *          intersect the point
     */
    Collection<CFXComponent> query(Vector2i point, Collection<CFXComponent> collection);
}
