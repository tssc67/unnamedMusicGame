package me.sunchiro.game.engine.gl;

import me.sunchiro.game.engine.gl.object.CharQuad;
import me.sunchiro.game.engine.gl.object.Cube;
import me.sunchiro.game.engine.gl.object.Drawable;
import me.sunchiro.game.engine.gl.object.Quad;
import me.sunchiro.game.engine.gl.object.Vertex;

public class Generator {
	public static CharQuad[] StringQuadGenerator(String word, float Size, float x, float y, float z, boolean isOrtho) {
		CharQuad[] output = new CharQuad[word.length()];
		float left = 0;
		float h = Size * 1.25f;
		for (int i = 0; i < word.length(); i++) {
			float w = Size * CharQuad.getCharWidth(word.charAt(i));
			Vertex[] v = new Vertex[4];
			for (int j = 0; j < 4; j++)
				v[j] = new Vertex();
			v[0].setXYZ(x + left, y, z);
			v[1].setXYZ(x + left, y + (isOrtho ? h : -h), z);
			v[2].setXYZ(x + left + w, y + (isOrtho ? h : -h), z);
			v[3].setXYZ(x + left + w, y, z);
			output[i] = new CharQuad(v);
			output[i].setCharacter(word.charAt(i));
			left += w;
		}
		return output;
	}

	public static Quad normalQuad() {
		return new Quad(new Vertex[] { new Vertex(), new Vertex(), new Vertex(), new Vertex() });
	}

	public static Cube normalCube() {
		return new Cube(new Vertex[] { new Vertex(), new Vertex(), new Vertex(), new Vertex(), new Vertex(),
				new Vertex(), new Vertex(), new Vertex() });
	}
	public static void inverseAlpha(Drawable[] objects){
		for(Drawable object:objects){
			object.inverseAlpha ^= true;
		}
	}
	public static void rotateAll(Drawable[] objects, float x,float y,float z){
		for(Drawable object:objects){
			object.rotation.add(x,y,z);
		}
	}
}
