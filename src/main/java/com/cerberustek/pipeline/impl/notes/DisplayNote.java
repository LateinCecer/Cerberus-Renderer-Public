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

package com.cerberustek.pipeline.impl.notes;

import com.cerberustek.resource.model.ModelResource;
import com.cerberustek.resource.shader.ShaderResource;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.CerberusRenderer;
import com.cerberustek.geometry.DrawMode;
import com.cerberustek.logic.math.Matrix4f;
import com.cerberustek.logic.math.Quaternionf;
import com.cerberustek.logic.math.Vector3f;
import com.cerberustek.overlay.OverlayUtil;
import com.cerberustek.pipeline.InputProvider;
import com.cerberustek.pipeline.impl.RenderNote;
import com.cerberustek.shader.Shader;
import com.cerberustek.shader.uniform.UniformMatrix4f;
import com.cerberustek.window.Window;

public class DisplayNote extends RenderNote {

    private final ModelResource screen;
    private InputProvider[] textures;
    private final ShaderResource shader;
    private final CerberusRenderer renderer;

    public DisplayNote(ShaderResource shader, InputProvider... texture) {
        renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);

        this.textures = texture;
        this.shader = shader;
        this.screen = OverlayUtil.getInstance().getScreenMesh();
    }

    @Override
    public void destroy() {}

    @Override
    public void update(double v) {
        /*try {
            BufferedImage image = TextureUtil.downloadAsBufferedImage(texture, 0);
            FileOutputStream stream = new FileOutputStream("test-image.png");
            ImageIO.write(image, "png", stream);
            System.out.println("Hello World!");
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        /*renderer.getTextureBoard().bindFrameBuffer(null);
        renderer.getWindow().clear();*/

        Shader shader = renderer.getShaderBoard().bindShader(this.shader);
        if (shader.hasUniform("world_matrix"))
            shader.getUniform("world_matrix", UniformMatrix4f.class).set(new Matrix4f().initIdentity());
        if (shader.hasUniform("world_translation_matrix"))
            shader.getUniform("world_translation_matrix", UniformMatrix4f.class)
                    .set(new Matrix4f().initTranslation(new Vector3f(0, 0, 0)));
        if (shader.hasUniform("world_scale_matrix"))
            shader.getUniform("world_scale_matrix", UniformMatrix4f.class)
                    .set(new Matrix4f().initScale(new Vector3f(1, 1,1)));
        if (shader.hasUniform("world_rotation_matrix"))
            shader.getUniform("world_rotation_matrix", UniformMatrix4f.class)
                    .set(new Matrix4f().initRotation(new Quaternionf(0, 0, 0, 1)));

        shader.update(v);
        for (InputProvider resource : textures)
            renderer.getTextureBoard().bindTexture(resource.fetchOutput());
        renderer.getGeometryBoard().drawMesh(screen, DrawMode.TRIANGLES);
    }

    public InputProvider[] getTexture() {
        return textures;
    }

    /**
     * Sets the texture buffers for the display shader.
     *
     * This method has to be executed synchronized to
     * the render thread, so that the texture buffer
     * references are not updated while the renderer tries
     * to render the display with the texture buffer.
     * @param resources texture buffers
     */
    public void setTexture(final InputProvider... resources) {
        CerberusRenderer renderer = CerberusRegistry.getInstance().getService(CerberusRenderer.class);
        renderer.tryGLTask((t) -> {
            textures = resources;
        });
    }

    @Override
    public void reinit(Window window) {}
}
