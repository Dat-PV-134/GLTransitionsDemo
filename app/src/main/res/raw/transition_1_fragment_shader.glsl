#version 320 es

precision mediump float;

in vec2 texCoord;

uniform sampler2D texture1;
uniform sampler2D texture2;
uniform float progress;
vec2 direction = vec2(0.0, 1.0);

out vec4 fragColor;

vec4 getToColor(vec2 uv) {
    return texture(texture2, uv);
}

vec4 getFromColor(vec2 uv) {
    return texture(texture1, uv);
}

vec4 transition(vec2 uv) {
  vec2 p = uv + progress * sign(direction);
  vec2 f = fract(p);
  return mix(
    getToColor(f),
    getFromColor(f),
    step(0.0, p.y) * step(p.y, 1.0) * step(0.0, p.x) * step(p.x, 1.0)
  );
}

void main() {
	fragColor = transition(texCoord);
}