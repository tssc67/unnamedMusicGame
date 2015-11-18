package me.sunchiro.game.engine.resource;

import java.io.*;
import java.util.HashMap;

public class ResourceManager {
	private HashMap<String,Resource> resources;
	public ResourceManager(){
		resources  = new HashMap<String,Resource>(); 
	}
	public static InputStream readFile(String path){
		try {
			return new FileInputStream(new File(path));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
}