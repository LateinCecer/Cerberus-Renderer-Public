#version 450
precision highp float;

layout (location = 0) in vec3 position;
layout (location = 10) in mat3 rotation;
layout (location = 11) in vec3 scale;

out vec4 pos;

uniform mat4 projection;
uniform mat4 world_matrix;

void main() {
    vec3 newPos = rotation * vec3(scale.x * position.x, scale.y * position.y, scale.z * position.z);

    pos = world_matrix * vec4(newPos, 1);
    gl_Position = projection * vec4(newPos, 1);
}
