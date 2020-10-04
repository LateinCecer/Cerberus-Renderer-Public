#version 450
precision highp float;

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texture;
layout (location = 2) in vec3 normal;

layout (location = 9) in vec3 translation;
layout (location = 10) in mat3 rotation;

out vec2 texCoord;
out vec4 pos;
out vec4 polNormal;
out mat4 worldTrans;

uniform mat4 world_matrix;
uniform mat4 world_rotation_matrix;
uniform mat4 projection;

void main() {
    vec3 newPos = translation + rotation * position;

    pos = world_matrix * vec4(newPos, 1);
    gl_Position = projection * vec4(newPos, 1);
    texCoord = texture;
    polNormal = world_rotation_matrix * vec4(rotation * normal, 1);
    worldTrans = world_matrix;
}
