#version 450
precision highp float;

in vec2 texCoord;
in vec4 pos;
in vec4 polNormal;
in mat4 worldTrans;

layout (location = 0) out vec4 fragColor[1];
/*
# index   |   texture usage
# 0             color map
# 1             normal map
# 2             specular map
# 3             glow emissive map
# 4             metallic map
# 5             depth offset map
*/
#define COLOR 0

#define NODE_SIZE 7        // offset between nodes in 4 bytes
#define THREAD_SIZE 7      // offset between threads in 4 bytes

#include<util/util.glsl>

/*
#########################
#### Structs go here ####
#########################
*/

layout(std430, binding=1) buffer counterBuffer
{
    int nodeCounter; // starts with 1
    int threadCounter; // starts with 0
} Counter;

layout(std430, binding=2) buffer octreeBuffer
{
    // ### Layout ###
    // mutex-flag [1-bit] | child-link [31-bit]
    // unused-flag [1-bit] | top-link [31-bit]
    // unused-flag [1-bit] | bottom-link [31-bit]
    // unused-flag [1-bit] | left-link [31-bit]
    // unused-flag [1-bit] | right-link [31-bit]
    // unused-flag [1-bit] | front-link [31-bit]
    // unused-flag [1-bit] | back-link [31-bit]

    // size in total: 28 bytes per node
    // --> 7 data entries per nodes

    int[] data;
} Octree;

layout(std430, binding=3) buffer threadListBuffer
{
    // ### Layout ###
    // float x [32-bit]
    // float y [32-bit]
    // float z [32-bit]
    // diffuse color:       ARGB [32-bit]
    // counter, normal:     [8-bit] | XYZ[24-bit]
    // unused, specular:    [8-bit] | RGB[24-bit]
    // unsued, emission:    [8-bit] | RGB[24-bit]

    int[] data;
} ThreadList;

// Minimal node size
uniform float minNodeSize;


bool getMutex(int nodeId) {
    return bool((Octree.data[nodeId * NODE_SIZE] >> 31) & 0x1);
}

bool setMutex(int nodeId, bool mutex) {
    if (mutex) {
        return bool((atomicOr(Octree.data[nodeId * NODE_SIZE], 0x80000000) >> 31) & 0x1);
    } else {
        return bool((atomicAnd(Octree.data[nodeId * NODE_SIZE], 0x7FFFFFFF) >> 31) & 0x1);
    }
}

int getChildNode(int nodeId) {
    return Octree.data[nodeId * NODE_SIZE] & 0x7FFFFFFF;
}

void setChildNode(int nodeId, int childId) {
    atomicOr(Octree.data[nodeId * NODE_SIZE], childId);
}

void clearChild(int nodeId) {
    atomicAnd(Octree.data[nodeId * NODE_SIZE], 0x0);
}

void addDeferedThread(vec3 pos, vec4 diffuse, vec4 normal, vec4 specular, vec4 emission) {
    int id = atomicAdd(Counter.threadCounter, 1) * THREAD_SIZE;
    // position data
    ThreadList.data[id] = floatBitsToInt(pos.x);
    ThreadList.data[id + 1] = floatBitsToInt(pos.y);
    ThreadList.data[id + 2] = floatBitsToInt(pos.z);
    // diffuse
    ThreadList.data[id + 3] = colorToARGB(diffuse);
    // normal
    ThreadList.data[id + 4] = colorToARGB(normal);
    // specular
    ThreadList.data[id + 5] = colorToARGB(specular);
    // emission
    ThreadList.data[id + 6] = colorToARGB(emission);
}

int findNode(int parent, vec3 pos, vec3 nodePos, float nodeSize) {
    int childNode = getChildNode(parent);
    if (childNode == 0) { // child does not exist

        if (!setMutex(parent, true)) {
            // add child in this thread
            childNode = atomicAdd(Counter.nodeCounter, 8); // add 8 children
            clearChild(childNode);
            clearChild(childNode + 1);
            clearChild(childNode + 2);
            clearChild(childNode + 3);
            clearChild(childNode + 4);
            clearChild(childNode + 5);
            clearChild(childNode + 6);
            clearChild(childNode + 7);

            // set child reference to parent
            setChildNode(parent, childNode);
            // reset mutex
            setMutex(parent, false);

        } else {
            // child is already been added by an other thread.
            // add this thread to the defered thread list
            return -1;
        }
    }

    vec3 relPos = pos - nodePos;
    int add = 0;
    float nodeSizeHalf = nodeSize * 0.5f;

    if (nodeSize <= nodeSizeHalf) {

        if (relPos.x >= 0) {
            add += 4;
        }
        if (relPos.y >= 0) {
            add += 2;
        }
        if (relPos.z >= 0) {
            add += 1;
        }
        return childNode + add;
    } else {

        vec3 childPos = nodePos;
        float nodeSizeQuater = nodeSizeHalf * 0.5f;
        if (relPos.x >= 0) {
            add += 4;
            nodePos.x += nodeSizeQuater;
        } else {
            nodePos.x -= nodeSizeQuater;
        }
        if (relPos.y >= 0) {
            add += 2;
            nodePos.y += nodeSizeQuater;
        } else {
            nodePos.y -= nodeSizeQuater;
        }
        if (relPos.z >= 0) {
            add += 1;
            nodePos.z += nodeSizeQuater;
        } else {
            nodePos.z -= nodeSizeQuater;
        }

        return findNode(childNode + add, pos, nodePos, nodeSizeHalf);
    }
}


// Color Data (RGBA16)
uniform sampler2D colorMap;
// Color Data (RGBA16)
uniform sampler2D glowMap;
// Color Data (RGBA16)
uniform sampler2D specularMap;
// Non Color Data (vec3)
uniform sampler2D normalMap;
// Non Color Data (INT16)
uniform sampler2D metallicMap;
// Non Color Data (INT16)
uniform sampler2D roughnessMap;
// Non Color Data (INT16)
uniform sampler2D displacementMap;

uniform vec3 colorMod;
uniform vec3 glowMod;
uniform vec3 specularMod;

uniform float metallicMod;
uniform float roughnessMod;
uniform float displacementMod;

void main() {
    vec4 color = texture2D(colorMap, texCoord);
    vec4 glow = texture2D(glowMap, texCoord);
    vec4 specular = texture2D(specularMap, texCoord);
    vec4 normal = texture2D(normalMap, texCoord);
    vec4 metallic = texture2D(metallicMap, texCoord);
    vec4 roughness = texture2D(roughnessMap, texCoord);
    vec4 displacement = texture2D(displacementMap, texCoord);

    if (color.xyz == vec3(0, 0, 0))
        color = vec4(colorMod, 1);
    else
        color *= vec4(colorMod, 1);

    if (glow.xyz == vec3(0, 0, 0))
        glow = vec4(0, 0, 0, 0);
    else
        glow *= vec4(glowMod, 1);

    if (specular.xyz == vec3(0, 0, 0))
        specular = vec4(specularMod, 1);
    else
        specular *= vec4(specularMod, 1);

    if (normal.xyz == vec3(0, 0, 0))
        normal = polNormal;
    else
        normal = vec4(normalize(0.75 * polNormal.xyz + 0.25 * toCoord(normal).xyz), 1);

    float metallicValue = metallic.r;
    if (metallicValue == 0)
        metallicValue = metallicMod;
    else
        metallicValue *= metallicMod;

    float roughnessValue = roughness.r;
    if (roughnessValue == 0)
        roughnessValue = roughnessMod;
    else
        roughnessValue *= roughnessMod;

    float displacementValue = displacement.r;
    if (displacementValue == 0)
        displacementValue = displacementMod;
    else
        displacementValue *= displacementMod;


    // OctreeLeaf leaf = OctreeLeaf(Octree.nodes[0]);

    int rgba = colorToARGB(color);
    fragColor[COLOR] = ARGBToColor(rgba);
}
