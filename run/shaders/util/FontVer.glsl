#version 450 core
precision highp float;

layout (location = 1) in vec2 position;         // character position
layout (location = 2) in vec3 color;            // RGB color
layout (location = 15) in int character;        // character ID

out VS_OUT {
    int char;
    vec3 rgb;
} vs_out;

void main() {
    vs_out.char = character;
    vs_out.rgb = color;
    gl_Position = vec4(position.xy, 0, 1);
}
