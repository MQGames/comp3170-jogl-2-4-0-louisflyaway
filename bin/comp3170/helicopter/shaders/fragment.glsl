#version 410

uniform vec3 u_colour;

layout(location = 0) out vec4 colour;

void main() {
    colour = vec4(u_colour,1);
}

