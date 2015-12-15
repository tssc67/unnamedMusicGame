package me.sunchiro.game.engine.input;

import static org.lwjgl.glfw.GLFW.*;

import java.nio.DoubleBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.opengl.GL11;

public class InputManager {
	private GLFWKeyCallback keyCallback;
	private GLFWCursorPosCallback cursorCallback;
	private GLFWMouseButtonCallback mouseCallback;
	public ArrayList<KeyCallback> keyRegister = new ArrayList<KeyCallback>();
	public ArrayList<CursorCallback> cursorRegister = new ArrayList<CursorCallback>();
	private boolean keyState[] = new boolean[1024];
//	private long window;
	private double lastX=0,lastY=0	;
	private boolean calCursor = false;
	public InputManager(long window) {
		keyCallback = keyboardCallbackHandler();
		cursorCallback = cursorCallbackHandler();
		mouseCallback = mouseCallbackHandler();
		glfwSetKeyCallback(window, keyCallback);
		glfwSetCursorPosCallback(window,cursorCallback);
		glfwSetMouseButtonCallback(window, mouseCallback);
	}
	private GLFWKeyCallback keyboardCallbackHandler() {
		return new GLFWKeyCallback() {

			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				// TODO Auto-generated method stub
				KeyCallback.ActionType generatedAction;
				if ((action == GLFW_PRESS) && !keyState[key]) {
					generatedAction = KeyCallback.ActionType.KEYDOWN;
					keyState[key] = true;
				} else if ((action == GLFW_RELEASE)) {
					generatedAction = KeyCallback.ActionType.KEYUP;
					keyState[key] = false;
				} else {
					generatedAction = KeyCallback.ActionType.HOLD;
				}
				for (KeyCallback keyCallback : keyRegister) {
					if (keyCallback.key == key && keyCallback.actionType == generatedAction) {
						keyCallback.run();
					}
				}
			}
		};
	}
	private GLFWCursorPosCallback cursorCallbackHandler(){
		return new GLFWCursorPosCallback() {
			
			@Override
			public void invoke(long window, double xpos, double ypos) {
				if(!calCursor){
					lastX=xpos;
					lastY=ypos;
					calCursor=true;
					return;
				}
				double diffX = xpos-lastX;
				double diffY = ypos-lastY;
				for(CursorCallback cursorCb:cursorRegister){
					if(cursorCb.actionType == CursorCallback.ActionType.MOVE)
					cursorCb.run(diffX, diffY);
				}
				lastX=xpos;
				lastY=ypos;
			}
		};
	}
	private GLFWMouseButtonCallback mouseCallbackHandler(){
		return new GLFWMouseButtonCallback() {
			
			@Override
			public void invoke(long window, int button, int action, int mods) {
				for(CursorCallback cursorCb:cursorRegister){
					DoubleBuffer px = BufferUtils.createDoubleBuffer(1);
					DoubleBuffer py = BufferUtils.createDoubleBuffer(1);
					if(cursorCb.actionType == CursorCallback.ActionType.CLICK && action == 1){
						cursorCb.run(lastX, lastY);
					}
						
				}
			}
		};
	}
}
