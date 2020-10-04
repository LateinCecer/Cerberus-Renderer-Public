#version 450

#define texture2D texture
#define textureCube texture

precision highp float;

in vec2 texCoord;

out vec4 gs_FragColor;

uniform float minGlowingSamplingValue;
uniform sampler2D screenTexture;
uniform float dtu_per_sample;
uniform float dtv_per_sample;

vec2 Glow_1  = vec2(0.0, 20.0);
vec2 Glow_2  = vec2(13.0, 15.2);
vec2 Glow_3  = vec2(20.0, 0.0);

vec2 Glow_4  = vec2(13.0, -15.2);
vec2 Glow_5  = vec2(0.0, -20.0);
vec2 Glow_6  = vec2(-13.0, -15.5);

vec2 Glow_7  = vec2(-20.0, 0.0);
vec2 Glow_8  = vec2(-13.0, 15.2);
vec2 Glow_9  = vec2(-5.5, -5.5);

vec2 Glow_10 = vec2(5.5, -5.5);
vec2 Glow_11 = vec2(-5.5, 5.5);
vec2 Glow_12 = vec2(5.5, 5.5);

void main()
{

    vec4 SampleColor;

    SampleColor  = max(texture(screenTexture, texCoord.xy + vec2(dtu_per_sample*Glow_1.x, dtv_per_sample*Glow_1.y)) - minGlowingSamplingValue, 0.0);
    SampleColor += max(texture(screenTexture, texCoord.xy + vec2(dtu_per_sample*Glow_2.x, dtv_per_sample*Glow_2.y)) - minGlowingSamplingValue, 0.0);
    SampleColor += max(texture(screenTexture, texCoord.xy + vec2(dtu_per_sample*Glow_3.x, dtv_per_sample*Glow_3.y)) - minGlowingSamplingValue, 0.0);
    SampleColor += max(texture(screenTexture, texCoord.xy + vec2(dtu_per_sample*Glow_4.x, dtv_per_sample*Glow_4.y)) - minGlowingSamplingValue, 0.0);
    SampleColor += max(texture(screenTexture, texCoord.xy + vec2(dtu_per_sample*Glow_5.x, dtv_per_sample*Glow_5.y)) - minGlowingSamplingValue, 0.0);
    SampleColor += max(texture(screenTexture, texCoord.xy + vec2(dtu_per_sample*Glow_6.x, dtv_per_sample*Glow_6.y)) - minGlowingSamplingValue, 0.0);
    SampleColor += max(texture(screenTexture, texCoord.xy + vec2(dtu_per_sample*Glow_7.x, dtv_per_sample*Glow_7.y)) - minGlowingSamplingValue, 0.0);
    SampleColor += max(texture(screenTexture, texCoord.xy + vec2(dtu_per_sample*Glow_8.x, dtv_per_sample*Glow_8.y)) - minGlowingSamplingValue, 0.0);
    SampleColor += max(texture(screenTexture, texCoord.xy + vec2(dtu_per_sample*Glow_9.x, dtv_per_sample*Glow_9.y)) - minGlowingSamplingValue, 0.0);
    SampleColor += max(texture(screenTexture, texCoord.xy + vec2(dtu_per_sample*Glow_10.x, dtv_per_sample*Glow_10.y)) - minGlowingSamplingValue, 0.0);
    SampleColor += max(texture(screenTexture, texCoord.xy + vec2(dtu_per_sample*Glow_11.x, dtv_per_sample*Glow_11.y)) - minGlowingSamplingValue, 0.0);
    SampleColor += max(texture(screenTexture, texCoord.xy + vec2(dtu_per_sample*Glow_12.x, dtv_per_sample*Glow_12.y)) - minGlowingSamplingValue, 0.0);

    gs_FragColor = SampleColor;
}
