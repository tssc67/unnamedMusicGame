package me.sunchiro.game.engine;

import static org.lwjgl.glfw.GLFW.*;

import java.awt.List;
import java.util.ArrayList;

import org.lwjgl.glfw.*;
import org.lwjgl.glfw.GLFWKeyCallback;

import me.sunchiro.game.engine.KeyCallback.ActionType;

public class Engine {
	private Graphic gl;
	public ArrayList<KeyCallback> keyRegister = new ArrayList<KeyCallback>();
	private boolean keyState[] = new boolean[1024];
	public Engine() {
	}
	public void run() {
		gl.createWindow(keyPress(), 
				ConfigurationOption.screenWidth, 
				ConfigurationOption.screenHeight,
				ConfigurationOption.titleName);
	}

	public GLFWKeyCallback keyPress() {
		return new GLFWKeyCallback() {

			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				// TODO Auto-generated method stub
				ActionType generatedAction;
				if((action==GLFW_PRESS)^keyState[key]){
					generatedAction = ActionType.keyDown;
					keyState[key]=true;
				}
				else if((action==GLFW_RELEASE)^keyState[key]){
					generatedAction = ActionType.keyUp;
					keyState[key]=false;
				}
				else{
					generatedAction = ActionType.HOLD;
				}
				for(KeyCallback keyCallback:keyRegister){
					if(keyCallback.key == key && keyCallback.actionType == generatedAction){
						keyCallback.run();
					}
				}
			}
		};
	}
}
