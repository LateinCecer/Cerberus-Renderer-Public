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

package com.cerberustek.pipeline.impl.renderables;

import com.cerberustek.resource.material.MaterialResource;
import com.cerberustek.resource.model.ModelResource;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.pipeline.RenderScene;
import com.cerberustek.pipeline.Renderable;
import com.cerberustek.pipeline.SparseRenderScene;
import com.cerberustek.pipeline.impl.TransformableBase;
import com.cerberustek.shader.Shader;

public class StaticObjectRenderable extends TransformableBase implements Renderable {

    private final ModelResource resource;

    private boolean shouldRender = true;
    private boolean isVisible = true;

    private CerberusRenderer renderer;
    private MaterialResource material;

    public StaticObjectRenderable(ModelResource resource, MaterialResource material) {
        this.resource = resource;
        this.material = material;
    }

    @Override
    public void setupShader(Shader shader) {
        // Nothing special to do here.
        // This method may has to be overwritten thought
    }

    @Override
    public boolean getsRendered() {
        return shouldRender;
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public ModelResource getGeometry() {
        return resource;
    }

    @Override
    public MaterialResource getMaterial() {
        return material;
    }

    @Override
    public void update(double delta, RenderScene scene) {
        if (getsRendered()) {
            if (scene instanceof SparseRenderScene)
                ((SparseRenderScene) scene).appendToParse(this);
        }
    }

    private CerberusRenderer getRenderer() {
        if (renderer == null)
            renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        return renderer;
    }

    @Override
    public void destroy() {

    }

    public void setRendering(boolean shouldRender) {
        this.shouldRender = shouldRender;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
