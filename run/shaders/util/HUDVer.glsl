#version 450
precision highp float;

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texture;

out vec2 texCoord;

uniform vec2 scale;
uniform vec2 trans;

uniform vec2 uv_offset;
uniform vec2 size;

void main() {
    gl_Position = vec4(position.xy * scale + trans, 0, 1);
    texCoord = size * texture + uv_offset;
}
