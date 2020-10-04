#version 450 core

in vec2 textcoord;

out vec4 fragColor;

uniform sampler2D sampler;

void main() {
    fragColor = texture2D(sampler, textcoord.xy);
}
