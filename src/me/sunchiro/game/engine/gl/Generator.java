package me.sunchiro.game.engine.gl;

import me.sunchiro.game.engine.gl.object.CharQuad;
import me.sunchiro.game.engine.gl.object.Vertex;

public class Generator {
	public static CharQuad[] StringQuadGenerator(String word, float Size, float x, float y,float z) {
		CharQuad[] output = new CharQuad[word.length()];
		for (int i = 0; i < word.length(); i++) {
			Vertex[] v = new Vertex[4];
			v[0].setXYZ(x,y,0);
			v[1].setXYZ(x,y,0);
			v[2].setXYZ(x,y,0);
			v[3].setXYZ(x,y,0);
			output[i] = new CharQuad(v);
		}
		return output;
	}
}
