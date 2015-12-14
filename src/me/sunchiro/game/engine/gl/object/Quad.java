package me.sunchiro.game.engine.gl.object;


public class Quad extends Drawable{
	
	{
		size = 4;
	}
	public Quad(Vertex[] vertexs) {
		super(vertexs, new byte[] { 0, 1, 2, 2, 3, 0 });
		// TODO Auto-generated constructor stub
	}
	
}
