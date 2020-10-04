#version 450
precision highp float;

#define MAXLIGHTS 20;

in vec2 texCoord;
in mat4 projection;

layout (location = 0) out vec4 fragColor;

uniform sampler2D ColorTexture;
uniform sampler2D EmissionTexture;
uniform sampler2D NormalTexture;
uniform sampler2D SpecularTexture;
uniform sampler2D CameraSpacePositionTexture;

uniform vec3 ViewDirection;

uniform vec4 LightCameraSpacePosAndRange[20];
uniform vec4 LightColor[20];
uniform vec3 NegLightDir[20];

uniform int NumLightsUsed;
uniform vec4 AmbientLightColor;

void main() {
    // No light sources enabled:
    if (NumLightsUsed == 0) {
        fragColor = texture(ColorTexture, texCoord) + texture(EmissionTexture, texCoord);
    } // At least one light source enabled:
    else {
        vec3 Normal = texture(NormalTexture, texCoord).rgb;

        // No light calculation possible, due to missing normals
        if (Normal.r < 0.01 && Normal.g < 0.01 && Normal.b < 0.01) {
            fragColor = texture(ColorTexture, texCoord) + texture(EmissionTexture, texCoord);
        } // Light can be calculated:
        else {
            // Reinterpret color to non color data:
            Normal = 2.0 * Normal - vec3(1, 1, 1);
            vec3 CameraSpacePos = texture(CameraSpacePositionTexture, fragColor).xyz;

            vec4 LightPos;
            vec4 SpecularColor = texture(SpecularTexture, fragColor);
            vec4 DiffuseLightColor = vec4(0.0, 0.0, 0.0, 1.0);
            vec4 SpecularLightColor = vec4(0.0, 0.0, 0.0, 1.0);

            float tempDot;
            float SpecularIntensity;
            float Distance;
            float InvDistance;
            float DistanceBasedIntensity;
            float tempValue;
            vec3 NegPointLightDir;

            // Calc light color for all light sources
            for (int i = 0; i < NumLightsUsed; i++) {
                // directional light
                if (LightCameraSpacePosAndRange[i].w < 0.0) {
                    // calc diffused reflection
                    tempDot = dot(Normal, NegLightDir[i]);
                    DiffuseLightColor += max(tempDot, 0.0) * LightColor[i];

                    // calc refraction
                    if (tempDot > -0.7) {
                        SpecularIntensity = max(-dot(2.0 * tempDot * Normal - NegLightDir[i], ViewDirection), 0.0);

                        // calc size of reflective surface
                        SpecularIntensity = pow(SpecularIntensity, 20.0 * SpecularColor.w);
                        SpecularLightColor += SpecularIntensity * SpecularColor;

                    }
                }
                // calc point light
                else {
                    NegPointLightDir = (projection * LightCameraSpacePosAndRange[i]).xyz - CameraSpacePos;

                    // pixel outside of the sphere of influense
                    if (abs(NegPointLightDir.x) > LightCameraSpacePosAndRange[i].w)
                        continue;
                    if (abs(NegPointLightDir.y) > LightCameraSpacePosAndRange[i].w)
                        continue;
                    if (abs(NegPointLightDir.z) > LightCameraSpacePosAndRange[i].w)
                        continue;

                    Distance = length(NegPointLightDir);
                    if (Distance > LightCameraSpacePosAndRange[i].w)
                        continue;

                    // calc light intensity based on distance
                    InvDistance = 1.0/Distance;
                    NegPointLightDir *= InvDistance;

                    tempValue = LightCameraSpacePosAndRange[i].w * LightCameraSpacePosAndRange[i].w;
                    DistanceBasedIntensity = max(0.0, (tempValue - Distance * Distance) / tempValue);

                    // calc diffused reflection
                    tempDot = dot(Normal, NegPointLightDir);
                    DiffuseLightColor += DistanceBasedIntensity * max(tempDot, 0.0) * LightColor[i];

                    // calc refraction
                    if (tempDot > -0.7) {
                        SpecularIntensity = max(-dot(2.0 * tempDot * Normal - NegPointLightDir, ViewDirection), 0.0);

                        // calc size of reflective area
                        SpecularIntensity = pow(SpecularIntensity, 20.0 * SpecularColor.w);
                        SpecularLightColor += DistanceBasedIntensity * SpecularIntensity * SpecularColor;
                    }
                }
            }

            fragColor = SpecularLightColor + texture(EmissionTexture, texCoord) +
                    texture(ColorTexture, texCoord) * (AmbientLightColor + DiffuseLightColor);
        }
    }
}
