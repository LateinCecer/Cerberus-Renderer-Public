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

import com.cerberustek.CerberusRegistry;
import com.cerberustek.Destroyable;
import com.cerberustek.gui.*;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.CerberusRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;

public class CFXContentPane extends CFXPane implements CFXContainer {

    /** Container layout */
    private CFXLayout layout;
    /** Child components */
    private final HashSet<CFXComponent> children = new HashSet<>();
    /** component orientation */
    private CFXOrientation orientation;
    /** gui id */
    private int id = 0;

    public CFXContentPane(@NotNull CFXOrientation orientation, @NotNull Vector2i initialSize, @Nullable CFXLayout l) {
        super(new FlatCanvas(initialSize));
        CerberusRegistry.getInstance().getService(CerberusRenderer.class).tryGLTask(t -> getCanvas().init());
        this.orientation = orientation;

        if (l == null)
            layout = new RelativeLayout();
        else
            this.layout = l;
    }

    @Override
    public @NotNull CFXLayout getLayout() {
        return layout;
    }

    @Override
    public void setLayout(@NotNull CFXLayout layout) {
        this.layout = layout;
    }

    @Override
    public Collection<CFXComponent> getContents() {
        return new HashSet<>(children);
    }

    @Override
    public void add(CFXComponent child) {
        // refresh the child's resolution
        child.setResolution(layout.getSize(child, getCanvas()));
        child.register(id);
        children.add(child);
        requestRepaint();
    }

    @Override
    public void remove(CFXComponent child) {
        children.remove(child);
    }

    @Override
    public boolean contains(CFXComponent child) {
        return children.contains(child);
    }

    @Override
    public Collection<CFXComponent> query(Vector2i point) {
        return query(point, new HashSet<>());
    }

    @Override
    public Collection<CFXComponent> query(Vector2i point, Collection<CFXComponent> collection) {
        for (CFXComponent child : children) {
            Vector2i coord = layout.getOffset(child, getCanvas()).sub(point);

            if (coord.getX() >= 0 && coord.getY() >= 0) {

                Vector2i size = layout.getSize(child, getCanvas());
                if (coord.getX() < size.getX() && coord.getY() < size.getY()) {
                    collection.add(child);

                    if (child instanceof CFXContainer)
                        ((CFXContainer) child).query(point, collection);
                }
            }
        }
        return collection;
    }

    @Override
    public void paintToCanvas() {
        CFXCanvas canvas = getCanvas();

        for (CFXComponent child : children) {
            canvas.setSize(layout.getSize(child, canvas));
            canvas.setOffset(layout.getOffset(child, canvas));
            child.paintComponent(canvas);
        }
    }

    @Override
    public void repaint() {
        for (CFXComponent child : children) {
            if (child instanceof CFXRepaintable && ((CFXRepaintable) child).shouldRepaint())
                ((CFXRepaintable) child).repaint();
        }
        super.repaint();
    }

    @Override
    public @NotNull CFXOrientation getOrientation() {
        return orientation;
    }

    @Override
    public void setOrientation(@NotNull CFXOrientation orientation) {
        this.orientation = orientation;
    }

    @Override
    public void dispatchEvent(CFXEvent event) {
        for (CFXComponent child : children) {
            // check each loop, the event can be consumed
            // by the children
            if (event.isConsumed())
                break;

            Vector2i coord = layout.getOffset(child, getCanvas()).sub(event.getPoint());

            if (coord.getX() >= 0 && coord.getY() >= 0) {
                Vector2i size = layout.getSize(child, getCanvas());

                if (coord.getX() < size.getX() && coord.getY() < size.getY())
                    child.dispatchEvent(event);
            }
        }
    }

    @Override
    public void destroy() {
        children.forEach(Destroyable::destroy);
        super.destroy();
    }

    @Override
    public void register(int id) {
        this.id = id;
        for (CFXComponent child : children)
            child.register(id);
    }

    @Override
    public int id() {
        return id;
    }
}
