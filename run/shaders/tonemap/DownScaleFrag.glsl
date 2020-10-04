#version 450

#define texture2D texture
#define textureCube texture

precision highp float;

in vec2 texCoord;
out vec4 gs_FragColor;

uniform sampler2D screenTexture;
uniform float dtu_per_sample;
uniform float dtv_per_sample;

void main()
{
    vec4 sampleColor;

    sampleColor = texture(screenTexture, texCoord.xy +
                vec2(dtu_per_sample * 0.5, dtv_per_sample * -0.5));
    sampleColor += texture(screenTexture, texCoord.xy +
                vec2(dtu_per_sample * 0.5, dtv_per_sample * -0.25));
    sampleColor += texture(screenTexture, texCoord.xy +
                vec2(dtu_per_sample * 0.5, dtv_per_sample * 0.25));
    sampleColor += texture(screenTexture, texCoord.xy +
                vec2(dtu_per_sample * 0.5, dtv_per_sample * 0.5));
    sampleColor += texture(screenTexture, texCoord.xy +
                vec2(dtu_per_sample * 0.25, dtv_per_sample * -0.5));
    sampleColor += texture(screenTexture, texCoord.xy +
                vec2(dtu_per_sample * 0.25, dtv_per_sample * -0.25));
    sampleColor += texture(screenTexture, texCoord.xy +
                vec2(dtu_per_sample * 0.25, dtv_per_sample * 0.25));
    sampleColor += texture(screenTexture, texCoord.xy +
                vec2(dtu_per_sample * 0.25, dtv_per_sample * 0.5));
    sampleColor += texture(screenTexture, texCoord.xy +
                vec2(dtu_per_sample * -0.25, dtv_per_sample * -0.5));
    sampleColor += texture(screenTexture, texCoord.xy +
                vec2(dtu_per_sample * -0.25, dtv_per_sample * -0.25));
    sampleColor += texture(screenTexture, texCoord.xy +
                vec2(dtu_per_sample * -0.25, dtv_per_sample * 0.25));
    sampleColor += texture(screenTexture, texCoord.xy +
                vec2(dtu_per_sample * -0.25, dtv_per_sample * 0.5));
    sampleColor += texture(screenTexture, texCoord.xy +
                vec2(dtu_per_sample * -0.5, dtv_per_sample * -0.5));
    sampleColor += texture(screenTexture, texCoord.xy +
                vec2(dtu_per_sample * -0.5, dtv_per_sample * -0.25));
    sampleColor += texture(screenTexture, texCoord.xy +
                vec2(dtu_per_sample * -0.5, dtv_per_sample * 0.25));
    sampleColor += texture(screenTexture, texCoord.xy +
                vec2(dtu_per_sample * -0.5, dtv_per_sample * 0.5));

    gs_FragColor = 0.0625f * sampleColor;
}