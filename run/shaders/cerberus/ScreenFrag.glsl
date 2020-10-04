#version 450

in vec2 textcoord;

out vec4 fragColor[2];

uniform sampler2D colorMap;
uniform sampler2D depthMap;

void main() {
    vec4 textureColor = texture2D(colorMap, textcoord.xy);
    vec4 textureNormal = texture2D(depthMap, textcoord.xy);

    fragColor[0] = textureColor;
    fragColor[1] = textureNormal;
}
