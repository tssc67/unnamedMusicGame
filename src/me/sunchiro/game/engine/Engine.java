package me.sunchiro.game.engine;

import me.sunchiro.game.engine.gl.Graphic;
import me.sunchiro.game.engine.input.InputManager;
import me.sunchiro.game.engine.resource.ResourceManager;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.opengl.GL11;
public class Engine {
	private Graphic g;
	private InputManager input;
	private ResourceManager res;
	public void run() {
		res = new ResourceManager();
		g = new Graphic(res);
		g.createWindow(
				ConfigurationOption.screenWidth, 
				ConfigurationOption.screenHeight,
				ConfigurationOption.titleName);
		input = new InputManager(g.getWindow());
		while(!g.windowShouldClose()){
			
			update();
			g.draw();
		}
	}
	public void update(){
		
	}
}
