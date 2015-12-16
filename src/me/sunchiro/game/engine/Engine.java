package me.sunchiro.game.engine;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;

import org.lwjgl.glfw.GLFW;

import me.sunchiro.game.engine.al.Audio;
import me.sunchiro.game.engine.gl.Camera;
import me.sunchiro.game.engine.gl.Graphic;
import me.sunchiro.game.engine.input.CursorCallback;
import me.sunchiro.game.engine.input.InputManager;
import me.sunchiro.game.engine.input.KeyCallback;

public class Engine implements Runnable {
	private Graphic g;
	public InputManager input;
	private boolean destroyed = false;
	public boolean isDestroyed(){
		return destroyed;
	}
	public void run() {
		g = new Graphic();
		g.createWindow(ConfigurationOption.screenWidth, ConfigurationOption.screenHeight,
				ConfigurationOption.titleName);
		g.loadTexture(new String[]{
			"textures/menuTexture.png",
			"textures/ingame.png"
		});
		input = new InputManager(g.getWindow());
		Audio.init();
		input.keyRegister.add(new KeyCallback() {
			{
				key = GLFW.GLFW_KEY_X;
				actionType = ActionType.KEYDOWN;
			}
			@Override
			public void run() {
				// TODO Auto-generated method stub
			}
		});
		input.cursorRegister.add(new CursorCallback() {
			{
				actionType = ActionType.MOVE;
			}
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
		input.cursorRegister.add(new CursorCallback() {
			{
				actionType = ActionType.CLICK;
			}
			@Override
			public void run(double x, double y) {
			}
		});
		glfwSetInputMode(g.getWindow(), GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
		while (!g.windowShouldClose()) {
			update();
			g.draw();
		}
		destroyed=true;
		update();
		g.destroy();
	}
	public synchronized void update() {
		notify();
	}
	public Camera getCamera(){
		return g.cam;
	}
	public Graphic getGraphic(){
		return g;
	}
}
