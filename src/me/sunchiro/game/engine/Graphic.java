package me.sunchiro.game.engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
public class Graphic {
	private GLFWErrorCallback errorCallback;
	private GLFWKeyCallback keyCallback;
	private int WIDTH;
	private int HEIGHT;
	private long window;
	public Graphic(){
	}
	public void createWindow(GLFWKeyCallback keyCallback,int width,int height,String name){
		glfwSetErrorCallback(errorCallback = Callbacks.errorCallbackPrint(System.err));
		if (glfwInit() != GL11.GL_TRUE)
			throw new IllegalStateException("Unable to initialize GLFW");
		glfwDefaultWindowHints(); // optional, the current window hints are
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
		window = glfwCreateWindow(WIDTH, HEIGHT, name, NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");
		glfwSetKeyCallback(window,this.keyCallback = keyCallback);
		glfwSetWindowSizeCallback(window,resizeCallback());
		ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(window, (vidmode.asIntBuffer().get(0) - WIDTH) / 2,
				(vidmode.asIntBuffer().get(1) - HEIGHT) / 2);
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		

		glfwShowWindow(window);
		
		GL.createCapabilities();
		glEnable(GL_DEPTH_TEST);
		glClearColor(0.1f,0.0f,0.f,1.0f);

	}
	private GLFWWindowSizeCallback resizeCallback(){
		return new GLFWWindowSizeCallback() {
			
			@Override
			public void invoke(long window, int width, int height) {
//				resizeWindow(width,height);
			}
		};
	}
	public void resizeWindow(int width,int height){
		glfwSetWindowSize(window, width, height);
	}
	public void setWindowTitle(byte[] titleName){
		glfwSetWindowTitle(window, ByteBuffer.wrap(titleName));
	}
	
	public void setWindowTitle(String titleName){
		try {
			setWindowTitle(titleName.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public int getWidth(){
		return WIDTH;
	}
	public void setWidth(int width){
		WIDTH = width;
		resizeWindow(WIDTH,HEIGHT);
	}
	public int getHeight(){
		return HEIGHT;
	}
	public void setHeight(int height){
		HEIGHT = height;
		resizeWindow(WIDTH,HEIGHT);
	}
}
