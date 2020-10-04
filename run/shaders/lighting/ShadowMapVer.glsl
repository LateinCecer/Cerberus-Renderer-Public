#version 450
precision highp float;

layout (location = 0) in vec3 position;

out vec4 pos;

uniform mat4 projection;
uniform mat4 world_matrix;

void main() {
    pos = world_matrix * vec4(position, 1);
    gl_Position = projection * vec4(position, 1);
}
