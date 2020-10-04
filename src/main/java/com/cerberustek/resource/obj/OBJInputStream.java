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

import java.io.*;
import java.util.ArrayList;

public class OBJInputStream extends InputStream {

    private static final int MODE_NONE = 0;
    private static final int MODE_DMAT = 1;
    private static final int MODE_NAME = 2;
    private static final int MODE_VERTEX = 3;
    private static final int MODE_TEX = 4;
    private static final int MODE_NORMAL = 5;
    private static final int MODE_GROUP = 6;
    private static final int MODE_UMAT = 7;
    private static final int MODE_SMOOTH = 8;
    private static final int MODE_FACE = 9;

    private static final int READ_LIMIT = 256;

    private final BufferedInputStream in;

    private String name;
    private String mtllib;
    private String usemtl;
    private ArrayList<Vector3f> vertices;
    private ArrayList<Vector2f> texCoord;
    private ArrayList<Vector3f> normal;
    private String group;
    private int smooth;
    private ArrayList<Integer> indices;

    public OBJInputStream(InputStream in) {
        this.in = new BufferedInputStream(in);
    }

    public OBJObject readObject() throws IOException {
        int mode = 0;
        vertices = new ArrayList<>();
        texCoord = new ArrayList<>();
        normal = new ArrayList<>();
        indices = new ArrayList<>();

        in.mark(READ_LIMIT);
        String line;
        int count = -1;
        while ((line = readLine()) != null) {
            count++;
            // comment
            if (line.startsWith("#"))
                continue;

            // material define
            if (line.startsWith("mtllib")) {
                if (mode > MODE_DMAT)
                    throw new IllegalStateException();
                mode = MODE_DMAT;
                group = line.substring(8);
                continue;
            }

            // object name
            if (line.startsWith("o ")) {
                if (mode > MODE_NAME) {
                    // new object found
                    if (in.markSupported())
                        in.reset();
                    return make();
                }
                mode = MODE_NAME;
                name = line.substring(3);
                continue;
            }

            // vertex
            if (line.startsWith("v ")) {
                if (mode > MODE_VERTEX)
                    throw new IllegalStateException();
                mode = MODE_VERTEX;
                vertices.add(read3f(line.substring(2)));
                continue;
            }

            // texture coordinates
            if (line.startsWith("vt ")) {
                if (mode > MODE_TEX)
                    throw new IllegalStateException();
                mode = MODE_TEX;
                texCoord.add(read2f(line.substring(3)));
                continue;
            }

            // normal
            if (line.startsWith("vn ")) {
                if (mode > MODE_NORMAL)
                    throw new IllegalStateException();
                mode = MODE_NORMAL;
                normal.add(read3f(line.substring(3)));
                continue;
            }

            // group name
            if (line.startsWith("g ")) {
                if (mode > MODE_GROUP)
                    throw new IllegalStateException();
                mode = MODE_GROUP;
                group = line.substring(2);
                continue;
            }

            // material use
            if (line.startsWith("usemtl ")) {
                if (mode > MODE_UMAT)
                    throw new IllegalStateException();
                mode = MODE_UMAT;
                usemtl = line.substring(8);
                continue;
            }

            // smoothing
            if (line.startsWith("s ")) {
                if (mode > MODE_SMOOTH)
                    throw new IllegalStateException();
                mode = MODE_SMOOTH;

                try {
                    smooth = Integer.parseInt(line.substring(2));
                } catch (NumberFormatException e) {
                    smooth = -1;
                }
                continue;
            }

            // face
            if (line.startsWith("f ")) {
                mode = MODE_FACE;
                readFace(line.substring(2));
                continue;
            }

            throw new IllegalStateException("Invalid obj file format at line " + count);
        }
        return make();
    }

    private void readFace(String line) {
        StringBuilder builder = new StringBuilder();
        char[] chars = line.toCharArray();

        int index = 0;

        boolean flag = false;
        for (char c : chars) {
            if (flag) {
                if (c == '/') {
                    flag = false;

                    if (builder.length() > 0) {
                        try {
                            int i = Integer.parseInt(builder.toString());
                            if (index == 0)
                                indices.add(i - 1);
                            // TODO add indices for normal and texture coordinates
                        } catch (NumberFormatException e) {
                            throw new IllegalStateException("Invalid obj file format");
                        }

                        builder.setLength(0);
                    }
                    index++;
                } else if (c == ' ') {
                    return;
                } else if (isNumberChar(c))
                    builder.append(c);
                else
                    throw new IllegalStateException("Invalid number char " + c);

            } else if (c != ' ') {
                flag = true;

                if (isNumberChar(c))
                    builder.append(c);
                else
                    throw new IllegalStateException("Invalid obj file format");
            }
        }
    }

    private Vector3f read3f(String line) {
        StringBuilder builder = new StringBuilder();
        char[] chars = line.toCharArray();

        int index = 0;
        Vector3f output = new Vector3f(0);

        boolean flag = false;
        for (char c : chars) {
            if (flag) {
                if (c == ' ') {
                    flag = false;

                    Float f = readFloat(builder.toString());
                    if (f == null)
                        throw new IllegalStateException("Invalid obj file format");
                    builder.setLength(0);

                    if (index == 0)
                        output.setX(f);
                    else if (index == 1)
                        output.setY(f);
                    else if (index == 2) {
                        output.setZ(f);
                        return output;
                    }
                    index++;
                } else if (isNumberChar(c))
                    builder.append(c);
                else
                    throw new IllegalStateException("Invalid number char " + c);

            } else if (c != ' ') {
                flag = true;

                if (isNumberChar(c))
                    builder.append(c);
                else
                    throw new IllegalStateException("Invalid number char " + c);
            }
        }
        return output;
    }

    private Vector2f read2f(String line) {
        StringBuilder builder = new StringBuilder();
        char[] chars = line.toCharArray();

        int index = 0;
        Vector2f output = new Vector2f(0);

        boolean flag = false;
        for (char c : chars) {
            if (flag) {
                if (c == ' ') {
                    flag = false;

                    Float f = readFloat(builder.toString());
                    if (f == null)
                        throw new IllegalStateException("Invalid obj file format");
                    builder.setLength(0);

                    if (index == 0)
                        output.setX(f);
                    else if (index == 1) {
                        output.setY(f);
                        return output;
                    }
                    index++;
                } else if (isNumberChar(c))
                    builder.append(c);
                else
                    throw new IllegalStateException("Invalid number char " + c);

            } else if (c != ' ') {
                flag = true;

                if (isNumberChar(c))
                    builder.append(c);
                else
                    throw new IllegalStateException("Invalid number char " + c);
            }
        }
        return output;
    }

    private static final char[] numChars = new char[] {'-', '+', '.', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private boolean isNumberChar(char c) {
        for (char compare : numChars) {
            if (compare == c)
                return true;
        }
        return false;
    }

    private Float readFloat(String s) {
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private OBJObject make() {
        return null;
    }

    public String readLine() throws IOException {
        StringBuilder builder = new StringBuilder();

        for (int i = in.read(); i != -1; i++) {
            if (i == '\n')
                break;
            builder.append((char) i);
        }

        if (builder.length() == 0)
            return null;
        return builder.toString();
    }

    @Override
    public int read() throws IOException {
        return in.read();
    }

    @Override
    public int read(@NotNull byte[] b) throws IOException {
        return in.read(b);
    }

    @Override
    public int read(@NotNull byte[] b, int off, int len) throws IOException {
        return in.read(b, off, len);
    }

    @Override
    public byte[] readAllBytes() throws IOException {
        return in.readAllBytes();
    }

    @Override
    public byte[] readNBytes(int len) throws IOException {
        return in.readNBytes(len);
    }

    @Override
    public int readNBytes(byte[] b, int off, int len) throws IOException {
        return in.readNBytes(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return in.skip(n);
    }

    @Override
    public int available() throws IOException {
        return in.available();
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        in.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        in.reset();
    }

    @Override
    public boolean markSupported() {
        return in.markSupported();
    }

    @Override
    public long transferTo(OutputStream out) throws IOException {
        return in.transferTo(out);
    }
}
