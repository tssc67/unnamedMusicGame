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
	public Resource get(String name){
		return resources.get(name);
	}
	public Resource loadFile(String name,String fileName) throws FileNotFoundException, IOException{
		Resource toAdd = new Resource();
		toAdd.load(new DataInputStream(new FileInputStream(fileName)));
		return resources.put(name, toAdd);
	}
	public void clear(){
		resources.clear();
	}
	
}