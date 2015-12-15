#version 150 core

uniform sampler2D texture_diffuse;
uniform int inverseAlpha;
in vec4 pass_Color;
in vec2 pass_TextureCoord;

out vec4 out_Color;


void main(void) {
	out_Color = pass_Color;
	// Override out_Color with our texture pixel
	out_Color = texture2D(texture_diffuse, pass_TextureCoord);
	if(inverseAlpha>0){
		out_Color.a = 1 - out_Color.a;
	}
	out_Color = vec4(out_Color.rgba * pass_Color.rgba);
}
