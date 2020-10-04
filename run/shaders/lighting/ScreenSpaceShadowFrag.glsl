#version 450
precision highp float;

in vec2 texCoord;

layout (location = 0) out vec4 fragColor[];

#include<util/util.glsl>

uniform sampler2D ShadowTexture;
uniform sampler2D NormalTexture;
uniform sampler2D SceneCameraSpacePosAndDepthTexture;
uniform sampler2D SceneCameraSpacePosAndDepthTexture_LightView;

uniform mat4 matViewProjection_LightView;
uniform vec3 LightDir;

uniform float ShadowOffset;
uniform float ShadowRange;
uniform float ShadowRangeSq;
uniform float ShadowIntensity;

void main() {
    vec3 Normal = texture(NormalTexture, texCoord).xyz;

    // no normal data; cannot calc shadows
    if (Normal.r < 0.01 && Normal.g < 0.01 && Normal.b < 0.01) {
        // fragColor = vec4(1, 0, 0.5, 1);
        discard; // fragColor = texture(ShadowTexture, texCoord);
    // calc shadows
    } else {
        vec4 SceneCameraSpacePosAndDepth = texture(SceneCameraSpacePosAndDepthTexture, texCoord);
        // Transform scene pixel position into light source view space
        vec4 Projection = matViewProjection_LightView * vec4(SceneCameraSpacePosAndDepth.xyz, 1);

        // Calc light source texture coords
        float invW = 1.0 / Projection.w;
        float TexY = 0.5 * Projection.y * invW + 0.5;
        float TexX = 0.5 * Projection.x * invW + 0.5;

        if (TexX > 1 || TexY > 1 || TexX < 0 || TexY < 0)
            discard;

        vec4 SceneCameraSpacePosAndDepth_LightView = texture(SceneCameraSpacePosAndDepthTexture_LightView, vec2(TexX, TexY));
        //vec4 cam = matViewProjection_LightView * vec4(SceneCameraSpacePosAndDepth_LightView.xyz, 1);

        //float invW = 1.0 / cam.w;
        //float TexY = 0.5 * cam.y * invW + 0.5;
        //float TexX = 0.5 * cam.x * invW + 0.5;

        // No occluding pixel
        if (SceneCameraSpacePosAndDepth_LightView.a <= 0.0)
            // fragColor = vec4(0, 0, 1, 1);
            discard; // fragColor = texture(ShadowTexture, texCoord);
        else
        {
            vec3 Difference = SceneCameraSpacePosAndDepth.xyz - SceneCameraSpacePosAndDepth_LightView.xyz;

            float tempDot = dot(Difference, LightDir);

            if (tempDot < 0.0)
                discard;
            else {
                float Distance = length(Difference);

                // scene pixel outside of shadow area
                if (Distance < ShadowOffset)
                    discard;
                else if (Distance > ShadowRange) {
                    // fragColor = vec4(Distance, 0, 0, 1);
                    // return;
                    discard;
                // calc shadow
                } else {
                    float InvDistance = 1.0 / Distance;
                    // Difference *= InvDistance;

                    // fragColor = vec4(InvDistance, 0, 0, SceneCameraSpacePosAndDepth_LightView.a);
                    // return;

                    float DistanceBasedIntensity = max(0.0, 1 - (Distance * Distance / ShadowRangeSq));
                    tempDot *= InvDistance;

                    Normal = 2.0 * Normal - vec3(1.0, 1.0, 1.0);
                    float tempDot2 = pow(max(-dot(Normal, LightDir), 0.0), 2);

                    // calc shadow intensity
                    float Intensity = 1.0 - tempDot2 * ShadowIntensity * DistanceBasedIntensity * tempDot;
                    float Color = texture(ShadowTexture, texCoord).x;

                    // fragColor = vec4(Intensity, 1, 0, SceneCameraSpacePosAndDepth_LightView.a);
                    // return;

                    // skip, if shadow is already darker
                    if (Intensity < Color)
                        discard;
                    else // fragColor = vec4(Intensity, Intensity, Intensity, SceneCameraSpacePosAndDepth_LightView.a);
                        fragColor[0] = vec4(Intensity, Intensity, Intensity, SceneCameraSpacePosAndDepth_LightView.a);
                }
            }
        }
    }
}
