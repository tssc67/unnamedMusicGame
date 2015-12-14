package me.sunchiro.game.engine.gl.object;

import java.nio.FloatBuffer;

public abstract class Drawable{
	protected Vertex[] vertexs;
	protected byte[] indices;
	private boolean visible = true;
	public int size;
	public boolean markAsRemove = false;
	protected Drawable(){
		
	}
	public Drawable(Vertex[] vertexs,byte[] indices){
		this.vertexs = vertexs;
		this.indices = indices;
	}
	public Vertex[] getVertex(){
		return vertexs;
	}
	public byte[] getIndices(){
		return indices;
	}
	public void setVisible(boolean visible){
		this.visible = visible;
	}
	public boolean isVisible(){
		return visible;
	}
	public void store(FloatBuffer buff){
		for(int i=0;i<size;i++){
			buff.put(vertexs[i].getElements());
		}
	}
}
