#version 450

precision highp float;

in vec2 texCoord;
out vec4 gs_FragColor;

uniform sampler2D SceneAdaptedLuminanceTexture;
uniform sampler2D SceneLuminanceTexture;

uniform vec3 color;

void main()
{
    vec4 tempColor = texture2D(SceneAdaptedLuminanceTexture, vec2(0.5, 0.5));
    float LuminanceAdaptated01d = color.r * tempColor.r + color.g * tempColor.g + color.b * tempColor.b;

    tempColor = texture2D(SceneLuminanceTexture, vec2(0.5, 0.5));
    float Luminance = color.r * tempColor.r + color.g * tempColor.g + color.b * tempColor.b;

    float LuminanceApapted = 0.9 * LuminanceAdaptated01d + 0.1 * Luminance;
    gs_FragColor = vec4(LuminanceApapted);
}
