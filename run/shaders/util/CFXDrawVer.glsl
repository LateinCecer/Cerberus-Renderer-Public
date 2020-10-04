#version 450 core

layout (location = 0) in vec3 position;

out vec2 textcoord;

void main() {
    gl_Position = vec4(position, 1.0);
    textcoord = vec2((position.x + 1.0) / 2.0, (1.0 + position.y) / 2.0);
}