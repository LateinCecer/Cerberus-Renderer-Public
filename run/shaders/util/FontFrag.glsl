#version 450 core
precision highp float;

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
#define NORMAL 1
#define SPECULAR 2
#define EMISSION 3
#define METALLIC 4
#define DEPTH 5
#define POS 6

#include<util/util.glsl>

in vec2 glyphCoord;

uniform sampler2D colorSamp;

void main() {

}
