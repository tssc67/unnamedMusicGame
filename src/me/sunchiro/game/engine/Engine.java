package me.sunchiro.game.engine;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import org.lwjgl.glfw.GLFW;

import me.sunchiro.game.engine.gl.Camera;
import me.sunchiro.game.engine.gl.Graphic;
import me.sunchiro.game.engine.input.CursorCallback;
import me.sunchiro.game.engine.input.InputManager;
import me.sunchiro.game.engine.resource.ResourceManager;

public class Engine implements Runnable {
	private Graphic g;
	public InputManager input;
	private ResourceManager res;
	
	public void run() {
		res = new ResourceManager();
		g = new Graphic(res);
		g.createWindow(ConfigurationOption.screenWidth, ConfigurationOption.screenHeight,
				ConfigurationOption.titleName);
		input = new InputManager(g.getWindow());
		input.cursorRegister.add(new CursorCallback() {

			@Override
			public void run(double distanceX, double distanceY) {
				distanceX*=0.2;
				distanceY*=0.2;
				if (g.cam.pitch - distanceY < -89) {
					g.cam.pitch = -89;
				} else if (g.cam.pitch - distanceY > 89) {
					g.cam.pitch = 89;
				} else
					g.cam.pitch -= distanceY;
				g.cam.yaw -= distanceX;
			}
		});
		glfwSetInputMode(g.getWindow(), GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
		while (!g.windowShouldClose()) {
			update();
			g.draw();
		}
		g.destroy();
	}
	public void update() {
	}
	public Camera getCamera(){
		return g.cam;
	}
}
