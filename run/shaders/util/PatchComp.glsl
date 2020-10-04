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

uniform ivec2 srcCoord;
uniform ivec2 destCoord;
uniform ivec2 srcSize;
uniform vec2 srcScale;
uniform int blend;

uniform vec4 colorFactor;
uniform vec4 colorAdd;
uniform vec4 specularFactor;
uniform vec4 specularAdd;
uniform vec4 emissionFactor;
uniform vec4 emissionAdd;
uniform vec4 metallicFactor;
uniform vec4 metallicAdd;

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
    if (coord.x >= int(srcSize.x * srcScale.x) || coord.y >= int(srcSize.y * srcScale.y)) {
        return;
    }

    ivec2 src = srcCoord + ivec2(vec2(coord) / srcScale);
    ivec2 dest = destCoord + coord;


    if (blend > 0) {
        vec4 temp = imageLoad(colorTex, dest);
        vec4 temp2 = texelFetch(colorSamp, src, 0) * colorFactor + colorAdd;
        float mix = temp2.a;
        float mixInv = 1 - mix;

        imageStore(colorTex, dest, vec4(temp.rgb * mixInv + temp2.rgb * mix, temp.a + temp2.a));
        imageStore(normalTex, dest, imageLoad(normalTex, dest) * mixInv + texelFetch(normalSamp, src, 0) * mix);
        imageStore(specularTex, dest, imageLoad(specularTex, dest) * mixInv + (texelFetch(specularSamp, src, 0) * specularFactor + specularAdd) * mix);
        imageStore(emissionTex, dest, imageLoad(emissionTex, dest) * mixInv + (texelFetch(emissionSamp, src, 0) * emissionFactor + emissionAdd) * mix);
        imageStore(metallicTex, dest, imageLoad(metallicTex, dest) * mixInv + (texelFetch(metallicSamp, src, 0) * metallicFactor + metallicAdd) * mix);
        imageStore(depthTex, dest, imageLoad(depthTex, dest) * mixInv + texelFetch(depthSamp, src, 0) * mix);
    } else {
        imageStore(colorTex, dest, texelFetch(colorSamp, src, 0) * colorFactor + colorAdd);
        imageStore(normalTex, dest, texelFetch(normalSamp, src, 0));
        imageStore(specularTex, dest, texelFetch(specularSamp, src, 0) * specularFactor + specularAdd);
        imageStore(emissionTex, dest, texelFetch(emissionSamp, src, 0) * emissionFactor + emissionAdd);
        imageStore(metallicTex, dest, texelFetch(metallicSamp, src, 0) * metallicFactor + metallicAdd);
        imageStore(depthTex, dest, texelFetch(depthSamp, src, 0));
    }
}
