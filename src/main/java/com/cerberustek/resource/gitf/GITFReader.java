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

package com.cerberustek.resource.gitf;

import com.cerberustek.data.MetaData;
import com.cerberustek.data.impl.elements.DocElement;
import com.cerberustek.data.impl.tags.ArrayTag;
import com.cerberustek.exceptions.GITFFormatException;
import com.cerberustek.json.JSONUtil;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.exception.JSONFormatException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GITFReader {

    private String path;

    private final ArrayList<GITFAccessor> accessors = new ArrayList<>();
    private final ArrayList<GITFBuffer> buffers = new ArrayList<>();
    private final ArrayList<GITFBufferView> bufferViews = new ArrayList<>();
    private final ArrayList<GITFMaterial> materials = new ArrayList<>();
    private final ArrayList<GITFMesh> meshes = new ArrayList<>();
    private final ArrayList<GITFNode> nodes = new ArrayList<>();
    private final ArrayList<GITFSampler> samplers = new ArrayList<>();
    private final ArrayList<GITFImage> images = new ArrayList<>();
    private final ArrayList<GITFTexture> textures = new ArrayList<>();

    public GITFReader() {}

    public GITFResource load(File file) throws GITFFormatException, IOException, JSONFormatException {
        DocElement doc = (DocElement) JSONUtil.fromFile(file).toMeta();
        return load(doc, file.getParent());
    }

    public GITFResource load(DocElement doc, String path) throws GITFFormatException {
        if (doc == null)
            throw new IllegalArgumentException("The gITF Document cannot be null!");
        this.path = path;

        CerberusRegistry.getInstance().debug("Loading gITF from path: " + path + "!");

        /*
        ##################################
        ###### Asset
        ##################################
         */
        GITFAsset asset = new GITFAsset();
        asset.read(this, doc.extract("asset"));

        /*
        ##################################
        ###### Buffers
        ##################################
         */
        @SuppressWarnings("unchecked") ArrayTag<MetaData> buffers = doc.extractArray("buffers");
        if (buffers == null)
            throw new GITFFormatException();
        for (MetaData buffer : buffers) {
            GITFBuffer b = new GITFBuffer();
            b.read(this, buffer);
            this.buffers.add(b);
        }

        /*
        ##################################
        ###### Buffer Viewer
        ##################################
         */
        @SuppressWarnings("unchecked") ArrayTag<MetaData> bufferViews = doc.extractArray("bufferViews");
        if (bufferViews == null)
            throw new GITFFormatException();
        for (MetaData view : bufferViews) {
            GITFBufferView v = new GITFBufferView();
            v.read(this, view);
            this.bufferViews.add(v);
        }

        /*
        ###################################
        ###### Accessor
        ###################################
         */
        @SuppressWarnings("unchecked") ArrayTag<MetaData> accessors = doc.extractArray("accessors");
        if (accessors == null)
            throw new GITFFormatException();
        for (MetaData acc : accessors) {
            GITFAccessor a = new GITFAccessor();
            a.read(this, acc);
            this.accessors.add(a);
        }

        /*
        ###################################
        ###### Samplers
        ###################################
         */
        @SuppressWarnings("unchecked") ArrayTag<MetaData> samplers = doc.extractArray("samplers");
        if (samplers != null) {
            for (MetaData sampler : samplers) {
                GITFSampler s = new GITFSampler();
                s.read(this, sampler);
                this.samplers.add(s);
            }
        }

        /*
        ###################################
        ###### images
        ###################################
         */
        @SuppressWarnings("unchecked") ArrayTag<MetaData> images = doc.extractArray("images");
        if (images != null) {
            for (MetaData image : images) {
                GITFImage i = new GITFImage();
                i.read(this, image);
                this.images.add(i);
            }
        }

        /*
        ###################################
        ###### textures
        ###################################
         */
        @SuppressWarnings("unchecked") ArrayTag<MetaData> textures = doc.extractArray("textures");
        if (textures != null) {
            for (MetaData texture : textures) {
                GITFTexture t = new GITFTexture();
                t.read(this, texture);
                this.textures.add(t);
            }
        }

        /*
        ###################################
        ###### materials
        ###################################
         */
        @SuppressWarnings("unchecked") ArrayTag<MetaData> materials = doc.extractArray("materials");
        if (materials != null) {
            for (MetaData material : materials) {
                GITFMaterial m = new GITFMaterial();
                m.read(this, material);
                this.materials.add(m);
            }
        }

        /*
        ###################################
        ###### meshes
        ###################################
         */
        @SuppressWarnings("unchecked") ArrayTag<MetaData> meshes = doc.extractArray("meshes");
        if (meshes != null) {
            for (MetaData mesh : meshes) {
                GITFMesh m = new GITFMesh();
                m.read(this, mesh);
                this.meshes.add(m);
            }
        }

        /*
        ###################################
        ###### nodes
        ###################################
         */
        @SuppressWarnings("unchecked") ArrayTag<MetaData> nodes = doc.extractArray("nodes");
        if (nodes == null)
            throw new GITFFormatException("No nodes present");
        for (MetaData node : nodes) {
            GITFNode n = new GITFNode();
            n.read(this, node);
            this.nodes.add(n);
        }

        /*
        ###################################
        ###### scenes
        ###################################
         */
        final ArrayList<GITFScene> finalScenes = new ArrayList<>();

        @SuppressWarnings("unchecked") ArrayTag<MetaData> scenes = doc.extractArray("scenes");
        if (scenes == null)
            throw new GITFFormatException("No scenes present");
        for (MetaData scene : scenes) {
            GITFScene s = new GITFScene();
            s.read(this, scene);
            finalScenes.add(s);
        }

        Integer startScene = doc.valueInt("scene");
        if (startScene == null)
            startScene = 0;

        GITFResource resource = new GITFResource(asset, finalScenes, startScene);

        // clear buffers
        this.accessors.clear();
        this.buffers.clear();
        this.bufferViews.clear();
        this.images.clear();
        this.materials.clear();
        this.meshes.clear();
        this.nodes.clear();
        this.samplers.clear();
        this.textures.clear();

        return resource;
    }

    File loadUri(String uri) {
        return new File(path + "/" + uri);
    }

    GITFAccessor getAccessor(int index) {
        if (accessors.size() <= index)
            return null;

        return accessors.get(index);
    }

    GITFBuffer getBuffer(int index) {
        if (buffers.size() <= index)
            return null;

        return buffers.get(index);
    }

    GITFBufferView getBufferView(int index) {
        if (bufferViews.size() <= index)
            return null;

        return bufferViews.get(index);
    }

    GITFMaterial getMaterial(int index) {
        if (materials.size() <= index)
            return null;

        return materials.get(index);
    }

    GITFMesh getMesh(int index) {
        if (meshes.size() <= index)
            return null;

        return meshes.get(index);
    }

    GITFNode getNote(int index) {
        if (nodes.size() <= index)
            return null;

        return nodes.get(index);
    }

    GITFSampler getSampler(int index) {
        if (samplers.size() <= index)
            return null;

        return samplers.get(index);
    }

    GITFImage getImage(int index) {
        if (images.size() <= index)
            return null;

        return images.get(index);
    }

    GITFTexture getTexture(int index) {
        if (textures.size() <= index)
            return null;
        return textures.get(index);
    }
}
