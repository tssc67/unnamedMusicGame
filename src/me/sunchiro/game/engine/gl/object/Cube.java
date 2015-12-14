package me.sunchiro.game.engine.gl.object;

import org.joml.Vector2f;

public class Cube extends Drawable{
	static final byte[] seq = new byte[]{
			0,1,2,3,
			0,3,7,4,
			3,2,6,7,
			2,1,5,6,
			1,0,4,5,
			7,6,5,4
	};
	static final byte[] base = new byte[]{0,1,2,2,3,0};
	{
		size = 24;
	}
	static final float[][] defaultUV = new float[][]{
		{0,0},
		{0,1},
		{1,1},
		{1,0}
	};
	private Quad[] quads = new Quad[6];
	public Cube(Vertex[] vertexs) {
		
		Vertex[] cubeVert = new Vertex[size];
		byte[] indices = new byte[36];
		for(int i=0;i<size;i++){
			cubeVert[i]=vertexs[seq[i]].clone();
		}
		
		for(int i=0;i<6;i++){
			for(int j=0;j<6;j++){
				indices[i*6+j]= (byte)(base[j]+i*4);
			}
		}
		this.vertexs = cubeVert;
		this.indices = indices;
		// TODO Auto-generated constructor stub
	}
	@Override
	public Vertex[] getVertex(){
		return vertexs;
	}
	@Override
	public byte[] getIndices(){
		return indices;
	}
	public void mapTexture(Vector2f ul,Vector2f br){
		float width = br.x-ul.x;
		float height = br.y-ul.y;
		float bWidth = width/6;
		for(int i=0;i<6;i++){
			for(int j=0;j<4;j++){
				this.vertexs[i*4+j].setUV(ul.x + bWidth * i + defaultUV[j][0]*bWidth, ul.y  +  defaultUV[j][1]*height);
			}
		}
	}
}
