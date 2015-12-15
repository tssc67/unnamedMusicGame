package me.sunchiro.game.engine.gl.object;

import org.joml.Vector2f;

public class Quad extends Drawable {

	{
		size = 4;
	}

	public Quad(Vertex[] vertexs) {
		super(vertexs, new byte[] { 0, 1, 2, 2, 3, 0 });
	}

	public void put(float x, float y, float z, float width, float height) {
		vertexs[0].setXYZ(x, y, z);
		vertexs[1].setXYZ(x, y + height, z);
		vertexs[2].setXYZ(x + width, y + height, z);
		vertexs[3].setXYZ(x + width, y, z);
	}

	public void mapTexture(Vector2f ul, Vector2f br) {
		vertexs[0].setUV(ul.x, ul.y);
		vertexs[1].setUV(ul.x, br.y);
		vertexs[2].setUV(br.x, br.y);
		vertexs[3].setUV(br.x, ul.y);
	}
}
