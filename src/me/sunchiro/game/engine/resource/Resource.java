package me.sunchiro.game.engine.resource;

import java.io.IOException;
import java.io.InputStream;

public class Resource {
	private byte[] data;
	public String getString(){
		return data.toString();
	}
	void load(InputStream inp) throws IOException{
		data = new byte[inp.available()];
		inp.read(data, 0, inp.available());
	}
}
