#version 450 core
precision highp float;

layout (points) in;
layout (triangle_strip, max_vertices = 6) out;

struct Glyph {
    int width;
    int height;
    int yOff;
    int tex;
};

layout (std430, binding = 1) buffer fontBuffer {
    Glyph[] glyphs;
} Font;

in VS_IN {
    int char;
    vec3 rgb;
} vs_in[];

out vec3 colorFactor;
out vec2 glyphCoord;

flat out int glyphIndex;

uniform vec2 screenSize;

void main() {
    ivec2 aa = ivec2(gl_in[0].gl_Position.xy);
    Glyph g = Font.glyphs[vs_in[0].char];
    colorFactor = vs_in[0].rgb;

    glyphIndex = g.tex;

    // set base offset
    aa.y += g.yOff;

    // other positions
    ivec2 ab = aa + ivec2(0, g.width);
    ivec2 ba = aa + ivec2(g.height, 0);
    ivec2 bb = ab + ivec2(g.height, 0);

    // vertices
    gl_Position = vec4(aa, 0, 1);
    glyphCoord = gl_Position.xy;
    EmitVertex();

    gl_Position = vec4(ab, 0, 1);
    glyphCoord = gl_Position.xy;
    EmitVertex();

    gl_Position = vec4(ba, 0, 1);
    glyphCoord = gl_Position.xy;
    EmitVertex();

    gl_Position = vec4(bb, 0, 1);
    glyphCoord = gl_Position.xy;
    EmitVertex();
}
