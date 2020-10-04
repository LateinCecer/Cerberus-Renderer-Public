#version 450 core

layout (location = 0) out vec4 fragColor;

in vec2 texCoord;

uniform sampler2D screenTexture;
uniform sampler2D normalTexture;
uniform sampler2D sceneCameraSpacePosTexture;

uniform mat4 projection;

uniform float surfaceHeightScale;
uniform float occlusionSampleStepDistance;
uniform float occlusionInvDistanceFactor;
uniform float occlusionIntensity;
uniform float occlusionAmbientIntensity;
uniform float occlusionBias;
uniform float invSurfaceHeightOcclusionFactor;

float maxCalcRange = 0.1;

vec4 normalColor;
vec4 cameraSpacePos;

int NUM_STEPS_PER_DIR = 4;


void main() {

}
