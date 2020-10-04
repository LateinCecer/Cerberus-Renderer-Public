vec4 toCoord(vec4 color) {
    return vec4(2.0 * color.xyz - vec3(1, 1, 1), color.w);
}

vec4 toColor(vec4 coord) {
    return vec4(0.5 * coord.xyz + vec3(0.5, 0.5, 0.5), coord.w);
}

int colorChanelToRaw(float chanel) {
    return int(chanel * 255);
}

int colorToARGB(vec4 color) {
    return ((int(color.r * 255f) & 0xFF) << 16) + ((int(color.g * 255f) & 0xFF) << 8) + (int(color.b * 255f) & 0xFF) + ((int(color.a * 255f) & 0xFF) << 24);
}

vec4 ARGBToColor(int argb) {
    return vec4(float((argb >> 16) & 0xFF) / 255f, float((argb >> 8) & 0xFF) / 255f, float(argb & 0xFF) / 255f, float((argb >> 24) & 0xFF) / 255f);
}
