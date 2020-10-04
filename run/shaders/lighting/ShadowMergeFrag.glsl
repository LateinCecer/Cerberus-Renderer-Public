#version 450
precision highp float;

in vec2 texCoord;

layout (location = 0) out vec4 fragColor;

uniform sampler2D ColorTexture;
uniform sampler2D ShadowTexture;
uniform sampler2D NormalTexture;
uniform sampler2D CameraSpacePosition;

uniform float SoftShadowFactor;
uniform float SoftShaderDropoff;
uniform float maxShadow;

const float MinDot = 0.95;

vec4 calcShadow(vec4 ShadowColor, vec4 SurfaceColor) {
    return SurfaceColor * (vec4(1) - ShadowColor * maxShadow);
}

float vecAbs(vec3 value) {
    return value.x * value.x + value.y * value.y + value.z * value.z;
}

void main() {
    vec3 Normal = texture(NormalTexture, texCoord).xyz;

    // No normal information; cannot calc shadow
    if (Normal.r < 0.01 && Normal.g < 0.01 && Normal.b < 0.01) {
        discard;
    } else {
        vec4 ShadowColor = texture(ShadowTexture, texCoord);
        vec4 SurfaceColor = texture(ColorTexture, texCoord);

        if (SoftShadowFactor == 0.0) {
            fragColor = calcShadow(ShadowColor, SurfaceColor);
            return;
        }
        // calc soft shadow
        else {
            Normal = 2.0 * Normal - vec3(1.0, 1.0, 1.0);

            ShadowColor *= 2.0;
            int counter = 2;
            vec3 TestNormal;
            vec2 tex;

            float blend = vecAbs(texture(CameraSpacePosition, texCoord).xyz);
            blend = SoftShadowFactor + SoftShaderDropoff / blend;

            // loop 1
            tex.x = texCoord.x + blend;
            tex.y = texCoord.y + blend;

            TestNormal = texture(NormalTexture, tex).xyz;
            TestNormal = 2.0 * TestNormal - vec3(1.0, 1.0, 1.0);

            if (dot(Normal, TestNormal) > MinDot) {
                counter++;
                ShadowColor += texture(ShadowTexture, tex);
            }

            // loop 2
            tex.x = texCoord.x + blend;
            tex.y = texCoord.y - blend;

            TestNormal = texture(NormalTexture, tex).xyz;
            TestNormal = 2.0 * TestNormal - vec3(1.0, 1.0, 1.0);

            if (dot(Normal, TestNormal) > MinDot) {
                counter++;
                ShadowColor += texture(ShadowTexture, tex);
            }

            // loop 3
            tex.x = texCoord.x - blend;
            tex.y = texCoord.y + blend;

            TestNormal = texture(NormalTexture, tex).xyz;
            TestNormal = 2.0 * TestNormal - vec3(1.0, 1.0, 1.0);

            if (dot(Normal, TestNormal) > MinDot) {
                counter++;
                ShadowColor += texture(ShadowTexture, tex);
            }

            // loop 4
            tex.x = texCoord.x - blend;
            tex.y = texCoord.y - blend;

            TestNormal = texture(NormalTexture, tex).xyz;
            TestNormal = 2.0 * TestNormal - vec3(1.0, 1.0, 1.0);

            if (dot(Normal, TestNormal) > MinDot) {
                counter++;
                ShadowColor += texture(ShadowTexture, tex);
            }

            // loop 5
            tex.x = texCoord.x + blend;
            tex.y = texCoord.y;

            TestNormal = texture(NormalTexture, tex).xyz;
            TestNormal = 2.0 * TestNormal - vec3(1.0, 1.0, 1.0);

            if (dot(Normal, TestNormal) > MinDot) {
                counter++;
                ShadowColor += texture(ShadowTexture, tex);
            }

            // loop 6
            tex.x = texCoord.x - blend;
            tex.y = texCoord.y;

            TestNormal = texture(NormalTexture, tex).xyz;
            TestNormal = 2.0 * TestNormal - vec3(1.0, 1.0, 1.0);

            if (dot(Normal, TestNormal) > MinDot) {
                counter++;
                ShadowColor += texture(ShadowTexture, tex);
            }

            // loop 7
            tex.x = texCoord.x;
            tex.y = texCoord.y + blend;

            TestNormal = texture(NormalTexture, tex).xyz;
            TestNormal = 2.0 * TestNormal - vec3(1.0, 1.0, 1.0);

            if (dot(Normal, TestNormal) > MinDot) {
                counter++;
                ShadowColor += texture(ShadowTexture, tex);
            }

            // loop 8
            tex.x = texCoord.x;
            tex.y = texCoord.y - blend;

            TestNormal = texture(NormalTexture, tex).xyz;
            TestNormal = 2.0 * TestNormal - vec3(1.0, 1.0, 1.0);

            if (dot(Normal, TestNormal) > MinDot) {
                counter++;
                ShadowColor += texture(ShadowTexture, tex);
            }

            // End loop

            float weight = 1.0 / float(counter);
            ShadowColor *= weight;

            fragColor = calcShadow(ShadowColor, SurfaceColor);
        }
    }
}
