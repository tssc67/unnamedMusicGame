package me.sunchiro.game.engine;

import me.sunchiro.game.engine.gl.Graphic;
import me.sunchiro.game.engine.input.InputManager;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.opengl.GL11;
public class Engine {
	private Graphic g;
	private InputManager input;
	public void run() {
		g = new Graphic();
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
