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

package com.cerberustek.resource.obj;

import com.cerberustek.logic.math.Vector2f;
import com.cerberustek.logic.math.Vector3f;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class OBJOutputStream extends OutputStream {

    private final BufferedOutputStream out;

    public OBJOutputStream(OutputStream out) {
        this.out = new BufferedOutputStream(out);
    }

    public void writeObject(OBJObject obj) throws IOException {
        writeName(obj.getName());

        for (int i = 0; i < obj.vertexSize(); i++)
            writeVertex(obj.getVertex(i));

        writeOffSmoothing();

        for (int i = 0; i < obj.indexSize(); i += 3)
            writeFace(obj.getIndex(i), obj.getIndex(i + 1), obj.getIndex(i + 2));
    }

    public void writeString(String value) throws IOException {
        out.write(value.getBytes());
    }

    public void writeLine(String value) throws IOException {
        out.write(value.getBytes());
        out.write('\n');
    }

    public void writeName(String objectName) throws IOException {
        writeLine("o " + objectName);
    }

    public void writeVertex(Vector3f v) throws IOException {
        writeLine("v " + v.getX() + " " + v.getY() + " " + v.getZ());
    }

    public void writeUV(Vector2f uv) throws IOException {
        writeLine("vt " + uv.getX() + " " + uv.getY());
    }

    public void writeNormal(Vector3f n) throws IOException {
        writeLine("vn " + n.getX() + " " + n.getY() + " " + n.getZ());
    }

    public void writeGroup(String groupName) throws IOException {
        writeLine("g " + groupName);
    }

    public void writeDefMaterial(String matFile) throws IOException {
        writeLine("mtllib " + matFile);
    }

    public void writeUseMaterial(String material) throws IOException {
        writeLine("usemtl " + material);
    }

    public void writeSmoothing(int factor) throws IOException {
        writeLine("s " + factor);
    }

    public void writeOffSmoothing() throws IOException {
        writeLine("s off");
    }

    public void writeFace(int... indices) throws IOException {
        writeString("f");
        for (int i : indices)
            writeString(" " + (i + 1));
        write('\n');
    }

    public void writeFaceTex(int[] v, int[] vt) throws IOException {
        if (v.length != vt.length)
            throw new IllegalArgumentException("arrays have to be of the same dimension");

        writeString("f");
        for (int i = 0; i < v.length; i++)
            writeString(" " + (v[i] + 1) + "/" + (vt[i] + 1));
        write('\n');
    }

    public void writeFaceNor(int[] v, int[] n) throws IOException {
        if (v.length != n.length)
            throw new IllegalStateException("arrays have to be of the same dimension");

        writeString("f");
        for (int i = 0; i < v.length; i++)
            writeString(" " + (v[i] + 1) + "//" + (n[i] + 1));
        write('\n');
    }

    public void writeFace(int[] v, int[] vt, int[] n) throws IOException {
        if (v.length != vt.length || v.length != n.length)
            throw new IllegalStateException("arrays have to be of the same dimension");

        writeString("f");
        for (int i = 0; i < v.length; i++)
            writeString(" " + (v[i] + 1) + "/" + (vt[i] + 1) + "/" + (n[i] + 1));
        write('\n');
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    @Override
    public void write(@NotNull byte[] b) throws IOException {
        out.write(b);
    }

    @Override
    public void write(@NotNull byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
