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

package com.cerberustek.pipeline;

import com.cerberustek.pipeline.impl.RenderNote;
import com.cerberustek.Destroyable;
import com.cerberustek.Updatable;
import com.cerberustek.texture.RenderTarget;
import org.jetbrains.annotations.NotNull;

public interface RenderPipeline extends Updatable, Destroyable {

    /**
     * Sets the current RenderScene.
     *
     * While setting the render scene the rendering thread will
     * sleep.
     * @param scene new render scene
     * @return render scene
     */
    RenderScene setScene(RenderScene scene);

    /**
     * Returns the current render scene.
     * @return render scene
     */
    RenderScene getScene();

    /**
     * Clears the depth and color buffer of the currently active
     * Render target.
     *
     * @return Render target cleared.
     */
    RenderTarget clearRenderTarget();

    /**
     * Returns the currently active render target.
     *
     * @return Render target
     */
    RenderTarget getRenderTarget();

    /**
     * Sets the currently active render target.
     *
     * This method will also adjust all relevant Gl and Rendering
     * functions to the new RenderTarget.
     *
     * @param renderTarget new render target
     * @return The new current render target
     */
    RenderTarget setRenderTarget(@NotNull RenderTarget renderTarget);

    /**
     * This method will append a render note at the very end of
     * the render pipeline.
     *
     * This method also sets the parent note of the {@code toAppend}
     * note to this and the child note of this note to {@code toAppend}.
     * It will return the new parent note if {@code toAppend} (aka.
     * the formerly last note in the chain).
     *
     * @param note A note to append
     * @return The note it was appended at
     */
    RenderNote append(@NotNull RenderNote note);

    /**
     * This method will delete the very last note of the chain.
     *
     * This method also adjusts child and parent notes. It will
     * return the note it just deleted.
     *
     * @return deleted note
     */
    RenderNote shorten();

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
     * @param parentNote Parent note of the note to insert
     * @return Inserted note
     */
    RenderNote insert(@NotNull RenderNote toInsert, @NotNull RenderNote parentNote);

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
    RenderNote insert(@NotNull RenderNote toInsert, int location);

    /**
     * Deletes a render note from the pipeline.
     *
     * This method will adjust all child and parent notes after the
     * {@code toDelete} Note has successfully been deleted. It will
     * return the RenderNote it deleted, and null, if it could not
     * be located in the first place.
     *
     * @param renderNote Render note to delete
     * @return Render note deleted
     */
    RenderNote delete(@NotNull RenderNote renderNote);

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
    RenderNote delete(int location);

    /**
     * Returns the render note at the specified {@code location} index.
     *
     * This method will return null if the location index cannot be
     * reached.
     *
     * @param location index at which to return the Render note
     * @return Render note at index {@code location}
     */
    RenderNote get(int location);

    /**
     * This method will reinit all appended RenderNotes with the
     * currently open GLFW window.
     */
    void resize();

    /**
     * This method will remove and delete all Render note that are
     * currently on the pipeline.
     */
    void clearNotes();

    /**
     * Returns if the render pipeline is empty or not.
     *
     * @return empty?
     */
    boolean empty();

    /**
     * Returns the time passed between render loops in a fraction of
     * one second.
     * @return delta time
     */
    double getDelta();
}
