#version 450

in vec2 texCoord;

layout (location = 0) out vec4 fragColor;

uniform sampler2D Own;
uniform sampler2D Other;

void main() {
    vec4 ownColor = texture(Own, texCoord);
    vec4 otherColor = texture(Other, texCoord);

    float scaling;
    if (ownColor.a == 0)
        scaling = (1 - otherColor.a) / ownColor.a;
    else
        scaling = 0;

    if (scaling > 1)
        scaling = 1;

    // alphaOwn * a + alphaOther = 1
    // <-> a = (1 - alphaOther) / alphaOwn

    fragColor = ownColor * scaling + otherColor;
}
