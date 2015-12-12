/*
 * (C) Copyright 2015 Teerapath Sujitto

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.

 */
package me.sunchiro.game.engine.gl;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import me.sunchiro.game.engine.resource.ResourceManager;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class Graphic {
	private GLFWErrorCallback errorCallback;
	private GLFWWindowSizeCallback windowSizeCalback;
	private boolean FBOEnabled;
	private int width;
	private int height;
	private long window;
	private ResourceManager res;
	private Shader shader;

	public Graphic(ResourceManager res) {
		this.res = res;
		shader = new Shader();
	}

	public void createWindow(int width, int height, String name) {
		this.width = width;
		this.height = height;
		glfwSetErrorCallback(errorCallback = Callbacks.errorCallbackPrint(System.err));
		if (glfwInit() != GL11.GL_TRUE)
			throw new IllegalStateException("Unable to initialize GLFW");
		glfwDefaultWindowHints(); // optional, the current window hints are
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
		glfwWindowHint(GLFW_VISIBLE, GL11.GL_FALSE); // the window will stay
														// hidden
		glfwWindowHint(GLFW_RESIZABLE, GL11.GL_TRUE); // the window will be
														// resizable
		window = glfwCreateWindow(width, height, name, NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");
		// glfwSetKeyCallback(window,this.keyCallback = keyCallback);
		glfwSetWindowSizeCallback(window, resizeCallback());
		ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(window, (vidmode.asIntBuffer().get(0) - width) / 2,
				(vidmode.asIntBuffer().get(1) - height) / 2);
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		GL.createCapabilities();
		setCapabilities();
		glfwShowWindow(window);

		// Setup an XNA like background color
		GL11.glClearColor(0.4f, 0.6f, 0.9f, 0f);

		// Map the internal OpenGL coordinate system to the entire screen
		GL11.glViewport(0, 0, width, height);

		shader.setupShader();

	}

	public void setCapabilities() {
		FBOEnabled = GL.getCapabilities().GL_EXT_framebuffer_object;
	}

	public void destroy() {
		shader.destroy();

	}

	public void draw() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		//

		//
		glfwSwapBuffers(window);
		glfwPollEvents();
	}

	private GLFWWindowSizeCallback resizeCallback() {
		return windowSizeCalback = new GLFWWindowSizeCallback() {

			@Override
			public void invoke(long window, int width, int height) {
				GL11.glViewport(0, 0, width, height);
			}
		};
	}

	public long getWindow() {
		return window;
	}

	public boolean windowShouldClose() {
		return glfwWindowShouldClose(window) == GL11.GL_TRUE;
	}

	public void resizeWindow(int width, int height) {
		glfwSetWindowSize(window, width, height);
	}

	public void setWindowTitle(byte[] titleName) {
		glfwSetWindowTitle(window, ByteBuffer.wrap(titleName));
	}

	public void setWindowTitle(String titleName) {
		try {
			setWindowTitle(titleName.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		resizeWindow(width, height);
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
		resizeWindow(width, height);
	}
}
