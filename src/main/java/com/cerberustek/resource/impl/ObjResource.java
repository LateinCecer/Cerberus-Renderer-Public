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

package com.cerberustek.resource.impl;

import com.cerberustek.resource.model.ModelResource;
import com.cerberustek.geometry.Mesh;
import com.cerberustek.geometry.Vertex;
import com.cerberustek.geometry.impl.StaticMesh;
import com.cerberustek.geometry.impl.VertexImpl;
import com.cerberustek.logic.math.Vector3f;

import java.io.*;
import java.util.ArrayList;

public class ObjResource implements ModelResource {

    private final File file;
    private final boolean lines;

    public ObjResource(File file, boolean lines) {
        this.file = file;
        this.lines = lines;
    }

    public ObjResource(File file) {
        this(file, false);
    }

    @Override
    public Mesh load() {
        ArrayList<Vertex> vertices = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("v")) {
                    String[] s = line.split(" ");

                    float x = Float.parseFloat(s[1]);
                    float y = Float.parseFloat(s[2]);
                    float z = Float.parseFloat(s[3]);

                    vertices.add(new VertexImpl(new Vector3f(x, y, z)));

                } else if (line.startsWith("f")) {
                    String[] s = line.split(" ");

                    int i0 = Integer.parseInt(s[1]) - 1;
                    int i1 = Integer.parseInt(s[2]) - 1;
                    int i2 = Integer.parseInt(s[3]) - 1;

                    if (!lines) {
                        indices.add(i0);
                        indices.add(i1);
                        indices.add(i2);
                    } else {
                        indices.add(i0);
                        indices.add(i1);
                        indices.add(i1);
                        indices.add(i2);
                        indices.add(i2);
                        indices.add(i0);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Vertex[] vs = new Vertex[vertices.size()];
        vs = vertices.toArray(vs);

        int[] is = new int[indices.size()];
        for (int i = 0; i < is.length; i++)
            is[i] = indices.get(i);

        StaticMesh mesh = new StaticMesh();
        mesh.genBuffers();
        mesh.addVertices(vs, is, true);
        return mesh;
    }
}
