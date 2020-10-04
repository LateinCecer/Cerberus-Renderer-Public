#version 450 core

in vec2 textcoord;

out vec4 fragColor;

uniform vec3 color;
uniform sampler2D sampler;

void main() {
    vec4 textureColor = texture2D(sampler, textcoord.xy);

    if (textureColor.xyz != vec3(0, 0, 0))
        fragColor = textureColor;
    else
        fragColor = vec4(color, 1);
}
