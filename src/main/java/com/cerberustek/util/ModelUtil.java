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

import com.cerberustek.geometry.Vertex;
import com.cerberustek.logic.math.Vector3f;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class ModelUtil {

    public static void addNormals(Vertex[] vertices, int[] indices) {
        for (int i = 0; i < indices.length; i += 3) {
            int x = indices[i];
            int y = indices[i + 1];
            int z = indices[i + 2];

            Vector3f v1 = vertices[y].getPosition().sub(vertices[x].getPosition());
            Vector3f v2 = vertices[z].getPosition().sub(vertices[x].getPosition());
            Vector3f normal = v1.cross(v2).normalized();

            vertices[x].getNormal().set(normal.getX(), normal.getY(), normal.getZ());
            vertices[y].getNormal().set(normal.getX(), normal.getY(), normal.getZ());
            vertices[z].getNormal().set(normal.getX(), normal.getY(), normal.getZ());
        }
    }

    public static void addNormals(Vector3f[] vertices, int[] indices, Vector3f[] normals) {
        for (int i = 0; i < indices.length; i += 3) {
            int x = indices[i];
            int y = indices[i + 1];
            int z = indices[i + 2];

            Vector3f v1 = vertices[y].sub(vertices[x]);
            Vector3f v2 = vertices[z].sub(vertices[x]);
            normals[x] = normals[y] = normals[z] = v1.cross(v2).normalizeSelf();
        }
    }

    public static void writeOFF(Vector3f[] vertices, int[] indices, OutputStream outputStream) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        writer.write("OFF\n");
        writer.write(vertices.length + " " + (indices.length / 3) + " " + indices.length + "\n");

        // write vertices
        for (Vector3f v : vertices)
            writer.write(" " + v.getX() + " " + v.getY() + " " + v.getZ() + "\n");

        // write indices ordered into triangle strips for the faces
        for (int i = 0; i < indices.length; i += 3)
            writer.write(" 3  " + indices[i] + " " + indices[i + 1] + " " + indices[i + 2] + "\n");

        writer.flush();
    }

    public static void writeOFF(Vertex[] vertices, int[] indices, OutputStream outputStream) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        writer.write("OFF\n");
        writer.write(vertices.length + " " + (indices.length / 3) + " " + indices.length + "\n");

        // write vertices
        for (Vertex v : vertices)
            writer.write(" " + v.getPosition().getX() + " " + v.getPosition().getY() + " " + v.getPosition().getZ() + "\n");

        // write indices ordered into triangle strips for the faces
        for (int i = 0; i < indices.length; i += 3)
            writer.write(" 3  " + indices[i] + " " + indices[i + 1] + " " + indices[i + 2] + "\n");

        writer.flush();
    }
}
