#version 320 es

precision mediump float;

in vec2 texCoord;

uniform sampler2D texture1;
uniform sampler2D texture2;
uniform float progress;
float count = 10.0;
float smoothness = 0.5;

out vec4 fragColor;

vec4 getToColor(vec2 uv) {
    return texture(texture2, uv);
}

vec4 getFromColor(vec2 uv) {
    return texture(texture1, uv);
}

vec4 transition (vec2 p) {
  float pr = smoothstep(-smoothness, 0.0, p.x - progress * (1.0 + smoothness));
  float s = step(pr, fract(count * p.x));
  return mix(getFromColor(p), getToColor(p), s);
}

void main() {
	fragColor = transition(texCoord);
}