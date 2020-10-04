#version 450

precision highp float;

// in vec2 texCoord;

layout (location = 0) out vec4 fragColor;

uniform sampler2D ScreenTexture;

vec2 LuminanceSamplePos_1  = vec2(0.5, 0.5);

vec2 LuminanceSamplePos_2  = vec2(0.45, 0.45);
vec2 LuminanceSamplePos_3  = vec2(0.55, 0.45);
vec2 LuminanceSamplePos_4  = vec2(0.45, 0.55);
vec2 LuminanceSamplePos_5  = vec2(0.55, 0.55);
vec2 LuminanceSamplePos_6  = vec2(0.45, 0.5);
vec2 LuminanceSamplePos_7  = vec2(0.55, 0.5);
vec2 LuminanceSamplePos_8  = vec2(0.5, 0.45);
vec2 LuminanceSamplePos_9  = vec2(0.5, 0.55);

vec2 LuminanceSamplePos_10  = vec2(0.4, 0.4);
vec2 LuminanceSamplePos_11  = vec2(0.45, 0.4);
vec2 LuminanceSamplePos_12 = vec2(0.5, 0.4);
vec2 LuminanceSamplePos_13  = vec2(0.55, 0.4);
vec2 LuminanceSamplePos_14 = vec2(0.6, 0.4);

vec2 LuminanceSamplePos_15  = vec2(0.4, 0.6);
vec2 LuminanceSamplePos_16  = vec2(0.45, 0.6);
vec2 LuminanceSamplePos_17  = vec2(0.5, 0.6);
vec2 LuminanceSamplePos_18  = vec2(0.55, 0.6);
vec2 LuminanceSamplePos_19  = vec2(0.6, 0.6);

vec2 LuminanceSamplePos_20  = vec2(0.4, 0.45);
vec2 LuminanceSamplePos_21  = vec2(0.4, 0.5);
vec2 LuminanceSamplePos_22  = vec2(0.4, 0.55);

vec2 LuminanceSamplePos_23  = vec2(0.6, 0.45);
vec2 LuminanceSamplePos_24  = vec2(0.6, 0.5);
vec2 LuminanceSamplePos_25  = vec2(0.6, 0.55);

void main()
{
    vec4 SampleColor  = texture(ScreenTexture, LuminanceSamplePos_1);

    SampleColor += 0.8*texture(ScreenTexture, LuminanceSamplePos_2);
    SampleColor += 0.8*texture(ScreenTexture, LuminanceSamplePos_3);
    SampleColor += 0.8*texture(ScreenTexture, LuminanceSamplePos_4);
    SampleColor += 0.8*texture(ScreenTexture, LuminanceSamplePos_5);
    SampleColor += 0.8*texture(ScreenTexture, LuminanceSamplePos_6);
    SampleColor += 0.8*texture(ScreenTexture, LuminanceSamplePos_7);
    SampleColor += 0.8*texture(ScreenTexture, LuminanceSamplePos_8);
    SampleColor += 0.8*texture(ScreenTexture, LuminanceSamplePos_9);


    SampleColor += 0.6*texture(ScreenTexture, LuminanceSamplePos_10);
    SampleColor += 0.6*texture(ScreenTexture, LuminanceSamplePos_11);
    SampleColor += 0.6*texture(ScreenTexture, LuminanceSamplePos_12);
    SampleColor += 0.6*texture(ScreenTexture, LuminanceSamplePos_13);
    SampleColor += 0.6*texture(ScreenTexture, LuminanceSamplePos_14);
    SampleColor += 0.6*texture(ScreenTexture, LuminanceSamplePos_15);
    SampleColor += 0.6*texture(ScreenTexture, LuminanceSamplePos_16);
    SampleColor += 0.6*texture(ScreenTexture, LuminanceSamplePos_17);
    SampleColor += 0.6*texture(ScreenTexture, LuminanceSamplePos_18);
    SampleColor += 0.6*texture(ScreenTexture, LuminanceSamplePos_19);
    SampleColor += 0.6*texture(ScreenTexture, LuminanceSamplePos_20);
    SampleColor += 0.6*texture(ScreenTexture, LuminanceSamplePos_21);
    SampleColor += 0.6*texture(ScreenTexture, LuminanceSamplePos_22);
    SampleColor += 0.6*texture(ScreenTexture, LuminanceSamplePos_23);
    SampleColor += 0.6*texture(ScreenTexture, LuminanceSamplePos_24);
    SampleColor += 0.6*texture(ScreenTexture, LuminanceSamplePos_25);

    fragColor = SampleColor;
}