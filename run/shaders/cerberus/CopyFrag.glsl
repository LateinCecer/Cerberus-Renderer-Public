#version 450 core

in vec2 textcoord;

out vec4 fragColor;

uniform sampler2D sampler;

void main() {
    /*
    vec4 texColor = texture2D(sampler, textcoord.xy).rgba;

    if (texColor.a != 0)
        fragColor = vec4(texColor.rgb, 1);
    else
        fragColor = texColor;
        */

    fragColor = texture2D(sampler, textcoord.xy);
}
