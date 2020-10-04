#version 450
precision highp float;

in vec2 texCoord;
in vec4 pos;
in vec4 polNormal;
in mat4 worldTrans;

layout (location = 0) out vec4 fragColor[7];
/*
# index   |   texture usage
# 0             color map
# 1             normal map
# 2             specular map
# 3             glow emissive map
# 4             metallic map
# 5             depth offset map
*/
#define COLOR 0
#define NORMAL 1
#define SPECULAR 2
#define EMISSION 3
#define METALLIC 4
#define DEPTH 5
#define POS 6

#include<util/util.glsl>

// Color Data (RGBA16)
uniform sampler2D colorMap;
// Color Data (RGBA16)
uniform sampler2D glowMap;
// Color Data (RGBA16)
uniform sampler2D specularMap;
// Non Color Data (vec3)
uniform sampler2D normalMap;
// Non Color Data (INT16)
uniform sampler2D metallicMap;
// Non Color Data (INT16)
uniform sampler2D roughnessMap;
// Non Color Data (INT16)
uniform sampler2D displacementMap;

uniform vec3 colorMod;
uniform vec3 glowMod;
uniform vec3 specularMod;

uniform float metallicMod;
uniform float roughnessMod;
uniform float displacementMod;

void main() {
    vec4 color = texture2D(colorMap, texCoord);
    vec4 glow = texture2D(glowMap, texCoord);
    vec4 specular = texture2D(specularMap, texCoord);
    vec4 normal = texture2D(normalMap, texCoord);
    vec4 metallic = texture2D(metallicMap, texCoord);
    vec4 roughness = texture2D(roughnessMap, texCoord);
    vec4 displacement = texture2D(displacementMap, texCoord);

    if (color.xyz == vec3(0, 0, 0))
        color = vec4(colorMod, 1);
    else
        color *= vec4(colorMod, 1);

    if (glow.xyz == vec3(0, 0, 0))
        glow = vec4(0, 0, 0, 0);
    else
        glow *= vec4(glowMod, 1);

    if (specular.xyz == vec3(0, 0, 0))
        specular = vec4(specularMod, 1);
    else
        specular *= vec4(specularMod, 1);

    if (normal.xyz == vec3(0, 0, 0))
        normal = polNormal;
    else
        normal = vec4(normalize(0.75 * polNormal.xyz + 0.25 * toCoord(normal).xyz), 1);

    float metallicValue = metallic.r;
    if (metallicValue == 0)
        metallicValue = metallicMod;
    else
        metallicValue *= metallicMod;

    float roughnessValue = roughness.r;
    if (roughnessValue == 0)
        roughnessValue = roughnessMod;
    else
        roughnessValue *= roughnessMod;

    float displacementValue = displacement.r;
    if (displacementValue == 0)
        displacementValue = displacementMod;
    else
        displacementValue *= displacementMod;

    fragColor[COLOR] = color;
    fragColor[NORMAL] = toColor(normal);
    fragColor[EMISSION] = glow;
    fragColor[SPECULAR] = specular * roughnessValue;
    fragColor[METALLIC] = vec4(metallicValue);
    fragColor[DEPTH] = vec4(displacementValue);
    fragColor[POS] = pos;
}
