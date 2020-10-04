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

uniform ivec2 cell;
uniform ivec2 cellSize;
uniform ivec2 srcSize;
uniform int rescale;

uniform sampler2D colorSamp;
uniform sampler2D normalSamp;
uniform sampler2D specularSamp;
uniform sampler2D emissionSamp;
uniform sampler2D metallicSamp;
uniform sampler2D depthSamp;

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
    // discard threads outside of the cell's bounds
    if (coord.x >= cellSize.x || coord.y >= cellSize.y) {
        return;
    }

    ivec2 destCoord = cell + coord;
    // destCoord.y = imageSize(colorTex).y - destCoord.y;
    if (rescale == 0) {
        imageStore(colorTex, destCoord, texelFetch(colorSamp, coord, 0));
        imageStore(normalTex, destCoord, texelFetch(normalSamp, coord, 0));
        imageStore(specularTex, destCoord, texelFetch(specularSamp, coord, 0));
        imageStore(emissionTex, destCoord, texelFetch(emissionSamp, coord, 0));
        imageStore(metallicTex, destCoord, texelFetch(metallicSamp, coord, 0));
        imageStore(depthTex, destCoord, texelFetch(depthSamp, coord, 0));
    } else {
        ivec2 srcCoord = ivec2(vec2(srcSize) * (vec2(coord) / vec2(cellSize)));
        imageStore(colorTex, destCoord, texelFetch(colorSamp, srcCoord, 0));
        imageStore(normalTex, destCoord, texelFetch(normalSamp, srcCoord, 0));
        imageStore(specularTex, destCoord, texelFetch(specularSamp, srcCoord, 0));
        imageStore(emissionTex, destCoord, texelFetch(emissionSamp, srcCoord, 0));
        imageStore(metallicTex, destCoord, texelFetch(metallicSamp, srcCoord, 0));
        imageStore(depthTex, destCoord, texelFetch(depthSamp, srcCoord, 0));
    }
}
