#version 450
precision highp float;

in vec4 pos;

layout (location = 0) out vec4 fragColor;

#include<util/util.glsl>

void main() {
    fragColor = pos;
}
