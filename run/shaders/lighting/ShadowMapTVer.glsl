#version 450
precision highp float;

layout (location = 0) in vec3 position;
layout (location = 9) in vec3 translation;

out vec4 pos;

uniform mat4 projection;
uniform mat4 world_matrix;

void main() {
    vec3 newPos = translation + position;

    pos = world_matrix * vec4(newPos, 1);
    gl_Position = projection * vec4(newPos, 1);
}
