#version 320 es

precision mediump float;

in vec2 texCoord;

uniform sampler2D texture1;
uniform sampler2D texture2;
uniform sampler2D displacementMap;
uniform float progress;
float strength = 0.5;

out vec4 fragColor;

vec4 getToColor(vec2 uv) {
    return texture(texture2, uv);
}

vec4 getFromColor(vec2 uv) {
    return texture(texture1, uv);
}

vec4 transition(vec2 uv) {
  float displacement = texture(displacementMap, uv).r * strength;

  vec2 uvFrom = vec2(uv.x + progress * displacement, uv.y);
  vec2 uvTo = vec2(uv.x - (1.0 - progress) * displacement, uv.y);

  return mix(
    getFromColor(uvFrom),
    getToColor(uvTo),
    progress
  );
}

void main() {
	fragColor = transition(texCoord);
}