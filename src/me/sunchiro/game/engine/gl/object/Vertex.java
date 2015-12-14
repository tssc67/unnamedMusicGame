package me.sunchiro.game.engine.gl.object;

public class Vertex {
	private float[] xyzw = new float[] { 0f, 0f, 0f, 1f };
	private float[] rgba = new float[] { 1f, 1f, 1f, 1f };
	private float[] st = new float[] { 0f, 0f };
	public static final int stride = 40;

	public void setXYZ(float x, float y, float z) {
		setX(x);
		setY(y);
		setZ(z);
	}

	public void setXYZ(float[] xyz) {// NOT NULL SAFE
		setX(xyz[0]);
		setX(xyz[1]);
		setX(xyz[2]);
	}

	public void setR(float r) {
		rgba[0] = r;
	}

	public void setG(float g) {
		rgba[1] = g;
	}

	public void setB(float b) {
		rgba[2] = b;
	}

	public void setA(float a) {
		rgba[3] = a;
	}

	public void setRGBA(float r, float g, float b, float a) {
		rgba[0] = r;
		rgba[1] = g;
		rgba[2] = b;
		rgba[3] = a;
	}

	public void setRGBA(float[] rgba) {
		this.rgba[0] = rgba[0];
		this.rgba[1] = rgba[1];
		this.rgba[2] = rgba[2];
		this.rgba[3] = rgba[3];
	}

	public void setX(float x) {
		xyzw[0] = x;
	}

	public void setY(float y) {
		xyzw[1] = y;
	}

	public void setZ(float z) {
		xyzw[2] = z;
	}

	public void setUV(float s, float t) {
		st[0] = s;
		st[1] = t;
	}

	public float[] getElements() {
		float[] out = new float[10];
		out[0] = xyzw[0];
		out[1] = xyzw[1];
		out[2] = xyzw[2];
		out[3] = xyzw[3];
		out[4] = rgba[0];
		out[5] = rgba[1];
		out[6] = rgba[2];
		out[7] = rgba[3];
		out[8] = st[0];
		out[9] = st[1];
		return out;
	}

	public void setElements(float[] in) {
		xyzw[0] = in[0];
		xyzw[1] = in[1];
		xyzw[2] = in[2];
		xyzw[3] = in[3];
		rgba[0] = in[4];
		rgba[1] = in[5];
		rgba[2] = in[6];
		rgba[3] = in[7];
		st[0] = in[8];
		st[1] = in[9];
	}
	public Vertex copyTo(Vertex v){
		v.setElements(getElements());
		return v;
	}
	public Vertex clone(){
		return copyTo(new Vertex());
	}
	public float[] getXYZW() {
		return xyzw;
	}

	public float[] getRGBA() {
		return rgba;
	}

	public float[] getST() {
		return st;
	}
}
