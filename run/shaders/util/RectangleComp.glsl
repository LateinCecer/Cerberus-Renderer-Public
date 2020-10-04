#version 450 core

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
#define NORMAL 1
#define SPECULAR 2
#define EMISSION 3
#define METALLIC 4
#define DEPTH 5

layout(binding = COLOR, rgba16f) uniform image2D colorTex;
layout(binding = NORMAL, rgba16f) uniform image2D normalTex;
layout(binding = SPECULAR, rgba16f) uniform image2D specularTex;
layout(binding = EMISSION, rgba16f) uniform image2D emissionTex;
layout(binding = METALLIC, rgba16f) uniform image2D metallicTex;
layout(binding = DEPTH, rgba16f) uniform image2D depthTex;

layout(local_size_x = 8, local_size_y = 8)   in;

uniform ivec2 destCoord;
uniform ivec2 destSize;

uniform vec4 colorFactor;
uniform vec4 specularFactor;
uniform vec4 emissionFactor;
uniform vec4 metallicFactor;
uniform vec3 normalFactor;
uniform vec3 displacementFactor;

/*
Pre-Defined inputs:

in uvec3 gl_NumWorkGroups;
in uvec3 gl_WorkGroupID;
in uvec3 gl_LocalInvocationID;
in uvec3 gl_GlobalInvocationID;
in uint  gl_LocalInvocationIndex;
*/
void main() {
    ivec2 coord = ivec2(gl_GlobalInvocationID.xy);
    if (coord.x >= destSize.x || coord.y >= destSize.y) {
        return;
    }

    ivec2 dest = destCoord + coord;
    imageStore(colorTex, dest, colorFactor);
    imageStore(specularTex, dest, specularFactor);
    imageStore(emissionTex, dest, emissionFactor);
    imageStore(metallicTex, dest, metallicFactor);
    imageStore(normalTex, dest, vec4(normalFactor, 1));
    imageStore(depthTex, dest, vec4(displacementFactor, 1));
}
