package me.sunchiro.game.engine;

import org.lwjgl.glfw.GLFW;

import me.sunchiro.game.engine.gl.Graphic;
import me.sunchiro.game.engine.gl.object.Vertex;
import me.sunchiro.game.engine.input.InputManager;
import me.sunchiro.game.engine.input.KeyCallback;
import me.sunchiro.game.engine.resource.ResourceManager;

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
		input.keyRegister.add(new KeyCallback() {
			{
				actionType = ActionType.keyDown;
				key = GLFW.GLFW_KEY_X;
			}
			@Override
			public void run() {
				Vertex v = g.quad.getVertex()[0];
				v.setX(v.getXYZW()[0]-0.1f);
				// TODO Auto-generated method stub
			}
		});
		while(!g.windowShouldClose()){
			update();
			g.draw();
		}
		g.destroy();
	}
	public void update(){
	}
	public void destroy(){
		
	}
}
