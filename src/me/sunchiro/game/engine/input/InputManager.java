package me.sunchiro.game.engine.input;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFWKeyCallback;

public class InputManager {
	private GLFWKeyCallback keyCallback;
	public ArrayList<KeyCallback> keyRegister = new ArrayList<KeyCallback>();
	private boolean keyState[] = new boolean[1024];
	private long window;

	public InputManager(long window) {
		this.window = window;
		keyCallback = keyboardCallbackHandler();
		glfwSetKeyCallback(window, keyCallback);
	}
	private GLFWKeyCallback keyboardCallbackHandler() {
		return new GLFWKeyCallback() {

			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				// TODO Auto-generated method stub
				KeyCallback.ActionType generatedAction;
				if ((action == GLFW_PRESS) && !keyState[key]) {
					generatedAction = KeyCallback.ActionType.keyDown;
					keyState[key] = true;
				} else if ((action == GLFW_RELEASE)) {
					generatedAction = KeyCallback.ActionType.keyUp;
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
}
