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

package com.cerberustek.pipeline.impl;

import com.cerberustek.Destroyable;
import com.cerberustek.Updatable;
import com.cerberustek.window.Window;
import org.jetbrains.annotations.NotNull;

/**
 * Render notes used in the render pipeline to render stuff.
 */
public abstract class RenderNote implements Updatable, Destroyable {

    /** Parent note (upwards link) */
    private RenderNote parent;
    /** Child note (downwards link) */
    private RenderNote child;
    /** average render time in ms */
    private int renderTime = 0;

    /**
     * RenderNote with parent and child
     * @param parent Parent note
     * @param child Child note
     */
    public RenderNote(RenderNote parent, RenderNote child) {
        this.parent = parent;
        this.child = child;
    }

    /**
     * RenderNote with just a parent.
     *
     * In this case the child note gets set to null.
     * @param parent Parent note
     */
    public RenderNote(RenderNote parent) {
        this(parent, null);
    }

    /**
     * RenderNote without parent or child note.
     *
     * Both, parent and child note, are set to null.
     */
    public RenderNote() {
        this(null, null);
    }

    /**
     * Renders a frame and passes the render command down to
     * the child note.
     *
     * @param delta time passed since last call in seconds
     */
    void render(double delta) {
        long time = System.nanoTime();
        update(delta);
        renderTime = (int) (System.nanoTime() - time);

        if (hasChild())
            child.render(delta);
    }

    /**
     * Will reinitialize the render note.
     *
     * This is especially important for all render notes that
     * use framebuffers to store screenspace textures in
     * deferred rendering.
     * @param window The current window. Carries e.g. framebuffer
     *               size information
     */
    public abstract void reinit(Window window);

    /**
     * Will reinitialize this render note and all
     * if it's children.
     *
     * Take a look at the doc description of the
     * <code>reinit</code> Method for more information.
     * @param window window to reinitialize the
     *               notes with
     */
    void reinitAll(Window window) {
        reinit(window);

        if (hasChild())
            child.reinitAll(window);
    }

    /**
     * Returns rather or not this RenderNote has a Parent note.
     * @return Parent note existing?
     */
    public boolean hasParent() {
        return parent != null;
    }

    /**
     * Returns rather or not this RenderNote has a Child note.
     * @return Child note existing?
     */
    public boolean hasChild() {
        return child != null;
    }

    /**
     * Returns the Parent note (uplink)
     * @return Parent note
     */
    public RenderNote getParent() {
        return parent;
    }

    /**
     * Returns the child note (downlink)
     * @return Child note
     */
    public RenderNote getChild() {
        return child;
    }

    /**
     * Sets the Child note.
     *
     * Keep in mind that this method does not change the child's parent
     * note to this. You should do that yourself.
     *
     * @param note Child note
     * @return This render Note
     */
    public RenderNote setChild(RenderNote note) {
        child = note;
        return this;
    }

    /**
     * Sets the Parent note.
     *
     * Keep in mind that this method does not change the parent's
     * child note to this, you should do that yourself.
     *
     * @param note Parent note
     * @return This render Note
     */
    public RenderNote setParent(@NotNull RenderNote note) {
        parent = note;
        return this;
    }

    /**
     * Inserts a Render note after a specified parent note.
     *
     * The {@code toInsert} Note will be inserted right after the
     * specified {@code parent} Note. This method will also adjust
     * all child and parent notes.
     * In case the parent note cannot be found, this method will
     * return null, otherwise it will return the inserted RenderNote.
     *
     * @param toInsert Note to insert
     * @param parent Parent note of the note to insert
     * @return Inserted note
     */
    public RenderNote insert(@NotNull RenderNote toInsert, @NotNull RenderNote parent) {
        if (this.equals(parent)) {
            toInsert.setParent(this);
            if (hasChild())
                toInsert.setChild(child.setParent(toInsert));
            return child = toInsert;
        } else {
            if (hasChild())
                return child.insert(toInsert, parent);
            else
                return null;
        }
    }

    /**
     * Inserts a Render note at a specific position.
     *
     * The {@code toInsert} Note will be inserted at the specified
     * {@code location} index. This method will also adjust all
     * child and parent notes.
     * In case the location cannot be reached, this method will
     * return null. Otherwise it will return the Note with it has
     * inserted.
     *
     * @param toInsert Render note to insert
     * @param location Location index to insert it at
     * @return Render note inserted
     */
    public RenderNote insert(@NotNull RenderNote toInsert, int location) {
        if (location < 0)
            return null;
        else if (location == 0) {
            toInsert.setParent(this);
            if (hasChild())
                toInsert.setChild(child.setParent(toInsert));
            return child = toInsert;
        } else {
            if (hasChild())
                return child.insert(toInsert, location - 1);
            else
                return null;
        }
    }

    /**
     * This method will append a render note at the very end of
     * the render pipeline.
     *
     * This method also sets the parent note of the {@code toAppend}
     * note to this and the child note of this note to {@code toAppend}.
     * It will return the new parent note if {@code toAppend} (aka.
     * the formerly last note in the chain).
     *
     * @param toAppend A note to append
     * @return The note it was appended at
     */
    public RenderNote append(@NotNull RenderNote toAppend) {
        if (hasChild())
            return child.append(toAppend);
        else {
            toAppend.setParent(this);
            child = toAppend;
            return this;
        }
    }

    /**
     * This method will delete the very last note of the chain.
     *
     * This method also adjusts child and parent notes. It will
     * return the note it just deleted.
     *
     * @return deleted note
     */
    public RenderNote shorten() {
        if (hasChild())
            return child.shorten();
        else
            return delete(this);
    }

    /**
     * Deletes a render note from the pipeline.
     *
     * This method will adjust all child and parent notes after the
     * {@code toDelete} Note has successfully been deleted. It will
     * return the RenderNote it deleted, and null, if it could not
     * be located in the first place.
     *
     * @param toDelete Render note to delete
     * @return Render note deleted
     */
    public RenderNote delete(@NotNull RenderNote toDelete) {
        if (this.equals(toDelete)) {
            delete();
            return this;
        } else {

            if (hasChild())
                return child.delete(toDelete);
            else
                return null;
        }
    }

    /**
     * Deletes a render note at a specific location index.
     *
     * This method will adjust all child and parent notes after the
     * not at index {@code location} has successfully been deleted.
     * It will return the RenderNote it deleted, and null, if it could
     * not be found in the first place.
     *
     * @param location Index at which to delete a Render note
     * @return Render not deleted
     */
    public RenderNote delete(int location) {
        if (location < 0)
            return null;
        else if (location == 0) {
            delete();
            return this;
        } else {

            if (hasChild())
                return child.delete(location - 1);
            else
                return null;
        }
    }

    /**
     * Returns the render note at the specified {@code location} index.
     *
     * This method will return null if the location index cannot be
     * reached.
     *
     * @param location index at which to return the Render note
     * @return Render note at index {@code location}
     */
    public RenderNote get(int location) {
        if (location < 0)
            return null;
        else if (location == 0)
            return this;
        else {

            if (hasChild())
                return child.get(location - 1);
            else
                return null;
        }
    }

    /**
     * Internal method to delete this note and destroy it's contents.
     *
     * Before deletion, this method sets it's parents child note to
     * it's current child, and vise versa.
     */
    private void delete() {
        if (hasParent()) {
            parent.setChild(child);
            if (hasChild())
                child.setParent(parent);
        }
        parent = null;
        child = null;
        destroy();
    }

    /**
     * This method will delete this note and all notes after it.
     */
    public void clear() {
        if (hasChild())
            child.clear();
        parent = null;
        child = null;
        destroy();
    }

    /**
     * Returns the average time it takes to render the
     * Renderable, in milli seconds
     * @return average render time
     */
    public double averageRenderTime() {
        return renderTime * 1e-6;
    }
}
