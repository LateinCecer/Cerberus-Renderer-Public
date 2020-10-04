#version 450

#define texture2D texture
#define textureCube texture

precision highp float;

in vec2 texCoord;
out vec4 gs_FragColor;

uniform sampler2D ScreenTexture;
uniform sampler2D GlowTexture;
uniform sampler2D SceneTextureAdaptedLuminance;

// Texturekoordinatenaenderung pro sample:
uniform float Dtu_PerPixel;
uniform float Dtv_PerPixel;

uniform vec4 HDRValues1;
uniform vec4 HDRValues2;

uniform float Dtu_Filter;
uniform float Dtv_Filter;

uniform float gamma;

vec2 Glow_1 = vec2(0.0, 20.0);
vec2 Glow_2 = vec2(13.0, 15.2);
vec2 Glow_3 = vec2(20.0, 0.0);
vec2 Glow_4 = vec2(13.0, -15.2);
vec2 Glow_5 = vec2(0.0, -20.0);
vec2 Glow_6 = vec2(-13.0, -15.2);
vec2 Glow_7 = vec2(-20.0, 20.0);
vec2 Glow_8 = vec2(-13.0, 15.2);
vec2 Glow_9 = vec2(-5.5, -5.5);
vec2 Glow_10 = vec2(5.5, -5.5);
vec2 Glow_11 = vec2(-5.5, 5.5);
vec2 Glow_12 = vec2(5.5, 5.5);

void main()
{
    float Luminance = clamp(texture(SceneTextureAdaptedLuminance, vec2(0.5, 0.5)).r, 0.0, 1.0);
    Luminance *= Luminance;

    vec4 Color = texture(ScreenTexture, texCoord);

    if (Dtu_Filter > 0.0 || Dtv_Filter > 0.0)
    {
        vec4 ColorT1 = min(Color, texture(ScreenTexture, texCoord + vec2(Dtu_Filter, Dtv_Filter)));
        vec4 ColorT2 = min(Color, texture(ScreenTexture, texCoord - vec2(Dtu_Filter, Dtv_Filter)));
        Color = max(ColorT1, ColorT2);
    }

    // Ueberblendungsbereich vergroessern
    vec4 SampleColor = texture(GlowTexture, texCoord);
    SampleColor += texture(GlowTexture, texCoord + vec2(Dtu_PerPixel * Glow_1.x, Dtv_PerPixel * Glow_1.y));
    SampleColor += texture(GlowTexture, texCoord + vec2(Dtu_PerPixel * Glow_2.x, Dtv_PerPixel * Glow_2.y));
    SampleColor += texture(GlowTexture, texCoord + vec2(Dtu_PerPixel * Glow_3.x, Dtv_PerPixel * Glow_3.y));
    SampleColor += texture(GlowTexture, texCoord + vec2(Dtu_PerPixel * Glow_4.x, Dtv_PerPixel * Glow_4.y));
    SampleColor += texture(GlowTexture, texCoord + vec2(Dtu_PerPixel * Glow_5.x, Dtv_PerPixel * Glow_5.y));
    SampleColor += texture(GlowTexture, texCoord + vec2(Dtu_PerPixel * Glow_6.x, Dtv_PerPixel * Glow_6.y));
    SampleColor += texture(GlowTexture, texCoord + vec2(Dtu_PerPixel * Glow_7.x, Dtv_PerPixel * Glow_7.y));
    SampleColor += texture(GlowTexture, texCoord + vec2(Dtu_PerPixel * Glow_8.x, Dtv_PerPixel * Glow_8.y));
    SampleColor += texture(GlowTexture, texCoord + vec2(Dtu_PerPixel * Glow_9.x, Dtv_PerPixel * Glow_9.y));
    SampleColor += texture(GlowTexture, texCoord + vec2(Dtu_PerPixel * Glow_10.x, Dtv_PerPixel * Glow_10.y));
    SampleColor += texture(GlowTexture, texCoord + vec2(Dtu_PerPixel * Glow_11.x, Dtv_PerPixel * Glow_11.y));
    SampleColor += texture(GlowTexture, texCoord + vec2(Dtu_PerPixel * Glow_12.x, Dtv_PerPixel * Glow_12.y));

    // HDRValues1.x := BloomFactor
    // HDRValues1.y := BrightnessMax
    // HDRValues1.z := Exposure
    // HDRValues1.w := HDRInvIntensityFactor

    // HDRValues2.x := MinLuminanceAdaptation
    // HDRValues2.y := OverBlendingIntensity
    // HDRValues2.z := OverBlendingColor
    // HDRValues2.w := OverBlendingOffset

    // Szenenpixel in der Naehe einer Lichtquelle ("eines Lichtquellenpixels")
    // ueberblenden (Bloom-Effekt):
    vec4 SceneColor = Color + HDRValues1.x * SampleColor;
    float OverBlending = max(HDRValues2.y * (HDRValues2.w + HDRValues2.z *
                (SceneColor.r + SceneColor.g + SceneColor.b)), 0.0);

    float LuminanceAdaptation = 1.0f + HDRValues1.w * Luminance;
    if (LuminanceAdaptation < HDRValues2.x)
        LuminanceAdaptation = HDRValues2.x;

    float ToneMappingValue = 0.5 + 0.5 * OverBlending * HDRValues1.z * (HDRValues1.z * HDRValues1.y + 1.0f) /
                (HDRValues1.z + LuminanceAdaptation);
    SceneColor *= ToneMappingValue;

    // do gamma filtering
    SceneColor.rgb = pow(SceneColor.rgb, 1 / vec3(gamma));
    gs_FragColor = SceneColor;
}
