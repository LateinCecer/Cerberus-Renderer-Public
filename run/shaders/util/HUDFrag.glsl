#version 450
precision highp float;

in vec2 texCoord;

layout (location = 0) out vec4 fragColor;

uniform sampler2D sampler;
uniform vec3 color;
uniform vec2 uv_filtering;

void main() {
    vec4 sampleColor;

    sampleColor = texture(sampler, texCoord + vec2(0.5, 0.5) * uv_filtering);
    sampleColor += texture(sampler, texCoord + vec2(0.5, 0.25) * uv_filtering);
    sampleColor += texture(sampler, texCoord + vec2(0.5, -0.25) * uv_filtering);
    sampleColor += texture(sampler, texCoord + vec2(0.5, -0.5) * uv_filtering);

    sampleColor += texture(sampler, texCoord + vec2(0.25, 0.5) * uv_filtering);
    sampleColor += texture(sampler, texCoord + vec2(0.25, 0.25) * uv_filtering);
    sampleColor += texture(sampler, texCoord + vec2(0.25, -0.25) * uv_filtering);
    sampleColor += texture(sampler, texCoord + vec2(0.25, -0.5) * uv_filtering);

    sampleColor += texture(sampler, texCoord + vec2(-0.25, 0.5) * uv_filtering);
    sampleColor += texture(sampler, texCoord + vec2(-0.25, 0.25) * uv_filtering);
    sampleColor += texture(sampler, texCoord + vec2(-0.25, -0.25) * uv_filtering);
    sampleColor += texture(sampler, texCoord + vec2(-0.25, -0.5) * uv_filtering);

    sampleColor += texture(sampler, texCoord + vec2(-0.5, 0.5) * uv_filtering);
    sampleColor += texture(sampler, texCoord + vec2(-0.5, 0.25) * uv_filtering);
    sampleColor += texture(sampler, texCoord + vec2(-0.5, -0.25) * uv_filtering);
    sampleColor += texture(sampler, texCoord + vec2(-0.5, -0.5) * uv_filtering);

    sampleColor /= 16;
    fragColor = vec4(sampleColor.rgb * color, sampleColor.a);
}
