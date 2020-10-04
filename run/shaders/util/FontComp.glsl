#version 450 core
precision highp float;

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

layout(local_size_x = 8, local_size_y = 8, local_size_z = 1)   in;

struct Symbol {
    int glyph;
    int x;
    int y;
    int argb;
};

// font buffer containing the glyphs
layout (std430, binding = 1) buffer fontBuffer {
    int[] glyphs;
} Font;
// text buffer
layout (std140, binding = 2) buffer textBuffer {
    Symbol[] chars;
} Text;

#include<util/util.glsl>

uniform ivec2 atlasDim;
uniform ivec2 cellSize;

uniform ivec2 destCoord;
uniform ivec2 destSize;

uniform sampler2D colorSamp;
uniform sampler2D normalSamp;
uniform sampler2D specularSamp;
uniform sampler2D emissionSamp;
uniform sampler2D metallicSamp;
uniform sampler2D depthSamp;

ivec2 atlasCell(int cellId) {
    return ivec2(cellId % atlasDim.x, cellId / atlasDim.x) * cellSize;
}

ivec2 bounds(int glyph) {
    return ivec2(glyph & 0xFFFF, (glyph >> 16) & 0xFFFF);
}

/*
Pre-Defined inputs:

in uvec3 gl_NumWorkGroups;
in uvec3 gl_WorkGroupID;
in uvec3 gl_LocalInvocationID;
in uvec3 gl_GlobalInvocationID;
in uint  gl_LocalInvocationIndex;
*/
void main() {
    Symbol symbol = Text.chars[int(gl_GlobalInvocationID.z)];
    int glyph = Font.glyphs[symbol.glyph];
    ivec2 coord = ivec2(gl_GlobalInvocationID.xy);
    ivec2 glyphBounds = bounds(glyph);

    // clip to glyph size
    if (coord.x > glyphBounds.x || coord.y > glyphBounds.y) {
        return;
    }

    // base color
    vec4 baseColor = ARGBToColor(symbol.argb);
    // offset inside patch volume
    ivec2 destCoord_ = coord + ivec2(symbol.x, symbol.y);
    // clip to patch size
    if (destCoord_.x < 0 || destCoord_.y < 0 || destCoord_.x >= destSize.x || destCoord_.y >= destSize.y) {
        return;
    }
    // src coord
    ivec2 srcCoord_ = coord + atlasCell(symbol.glyph);
    // add uniform base offset
    destCoord_ += destCoord;

    imageStore(colorTex, destCoord_, texelFetch(colorSamp, srcCoord_, 0) * baseColor);
    imageStore(normalTex, destCoord_, texelFetch(normalSamp, srcCoord_, 0));
    imageStore(specularTex, destCoord_, texelFetch(specularSamp, srcCoord_, 0));
    imageStore(emissionTex, destCoord_, texelFetch(emissionSamp, srcCoord_, 0));
    imageStore(metallicTex, destCoord_, texelFetch(metallicSamp, srcCoord_, 0));
    imageStore(depthTex, destCoord_, texelFetch(depthSamp, srcCoord_, 0));
}
