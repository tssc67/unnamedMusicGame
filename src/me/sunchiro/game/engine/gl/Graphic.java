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

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import me.sunchiro.game.engine.gl.object.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.LinkedList;

public class Graphic {
	private GLFWErrorCallback errorCallback;
	private GLFWWindowSizeCallback windowSizeCalback;
	private boolean FBOEnabled;
	private boolean invalidated = false;
	private int width;
	private int height;
	private long window;
	public Shader shader;
	private TextureManager texManager;
	public Camera cam;
	private Matrix4f mvpMat;
	private FloatBuffer matBuff = null;
	private LinkedList<Drawable> objects = new LinkedList<Drawable>();
	private LinkedList<Drawable> orthoObjects = new LinkedList<Drawable>();
	public static Graphic instance;
	public Vector3f eye = new Vector3f(1, 1, 1);
	public Vector3f look = new Vector3f(0, 0, 0);
	private int vertexCount = 0;
	private int indicesCount = 0;
	public int tid;
	public int tid_charmap;
	ByteBuffer vertByteBuff;
	int vaoId = 0;
	int vboId = 0;
	int vboiId = 0;

	public Graphic() {
		shader = new Shader();
		texManager = new TextureManager();
		cam = new Camera();
	}

	public void invalidate() {
		invalidated = true;
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
		Graphic.instance = this;
		glfwSwapInterval(1);
		GL.createCapabilities();
		setCapabilities();

		glfwShowWindow(window);

		// Setup an XNA like background color
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glDepthFunc(GL11.GL_LESS);

		GL11.glClearColor(0.22f, 0.23f, 0.25f, 0f);
		// Map the internal OpenGL coordinate system to the entire screen
		GL11.glViewport(0, 0, width, height);
		matBuff = BufferUtils.createFloatBuffer(16);
		vaoId = GL30.glGenVertexArrays();
		vboiId = GL15.glGenBuffers();
		vboId = GL15.glGenBuffers();
		tid_charmap = texManager.loadTexture("/textures/charmap.png", GL13.GL_TEXTURE0);
		shader.setupShader();
		// testQuad();

	}

	public void loadTexture(String[] filename) {
		for (String file : filename) {
			texManager.loadTexture(file, GL13.GL_TEXTURE0);
		}
	}

	public void addObject(Drawable object) {
		objects.add(object);
		vertexCount += object.getVertex().length;
		indicesCount += object.getIndices().length;
		invalidate();
	}

	public void addOrthoObject(Drawable object) {
		orthoObjects.add(object);
		vertexCount += object.getVertex().length;
		indicesCount += object.getIndices().length;
		invalidate();
	}

	public void addObjects(Drawable[] objectArray) {
		for (Drawable object : objectArray) {
			vertexCount += object.getVertex().length;
			indicesCount += object.getIndices().length;
			objects.add(object);
		}
		invalidate();
	}

	public void addOrthoObjects(Drawable[] objectArray) {
		for (Drawable object : objectArray) {
			vertexCount += object.getVertex().length;
			indicesCount += object.getIndices().length;
			orthoObjects.add(object);
		}
		invalidate();
	}

	public void removeObject(Drawable object) {
		vertexCount -= object.getVertex().length;
		indicesCount -= object.getIndices().length;
		objects.remove(object);
		invalidate();
	}

	public void clearScene() {
		objects = new LinkedList<Drawable>();
		orthoObjects = new LinkedList<Drawable>();
		vertexCount = 0;
		indicesCount = 0;
	}

	public void removeOrthoObject(Drawable object) {
		vertexCount -= object.getVertex().length;
		indicesCount -= object.getIndices().length;
		orthoObjects.remove(object);
		invalidate();
	}

	public void resizeBuffer() {
		vertByteBuff = BufferUtils.createByteBuffer(vertexCount * Vertex.stride);
		ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indicesCount);
		FloatBuffer vertFloatBuff = vertByteBuff.asFloatBuffer();
		for (Drawable object : objects) {
			object.store(vertFloatBuff);
			indicesBuffer.put(object.getIndices());
		}
		for (Drawable object : orthoObjects) {
			object.store(vertFloatBuff);
			indicesBuffer.put(object.getIndices());
		}
		indicesBuffer.flip();
		vertFloatBuff.flip();
		GL30.glBindVertexArray(vaoId);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertFloatBuff, GL15.GL_STREAM_DRAW);
		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex.stride, 0);
		GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, Vertex.stride, 16);
		GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, Vertex.stride, 32);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);

		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	public Cube cube;

	public void testQuad() {
		String l = "YEAHHH SUCH STRING,MUCH AWESOME";
		CharQuad quads[] = Generator.StringQuadGenerator(l, 20, 10, 10, 0,true);
		addOrthoObjects(quads);
		Vertex v0 = new Vertex();
		Vertex v1 = new Vertex();
		Vertex v2 = new Vertex();
		Vertex v3 = new Vertex();
		Vertex v4 = new Vertex();
		Vertex v5 = new Vertex();
		Vertex v6 = new Vertex();
		Vertex v7 = new Vertex();

		v0.setXYZ(-1.5f, 1.5f, 0);
		v1.setXYZ(-1.5f, 0.5f, 0);
		v2.setXYZ(-0.5f, 0.5f, 0);
		v3.setXYZ(-0.5f, 1.5f, 0);
		v4.setXYZ(-1.5f, 1.5f, -1f);
		v5.setXYZ(-1.5f, 0.5f, -1f);
		v6.setXYZ(-0.5f, 0.5f, -1f);
		v7.setXYZ(-0.5f, 1.5f, -1f);
		cube = new Cube(new Vertex[] { v0, v1, v2, v3, v4, v5, v6, v7 });
		cube.mapTexture(new Vector2f(0, 0f), new Vector2f(1, 1 / 8f));
		addObject(cube);
	}

	public void setCapabilities() {
		FBOEnabled = GL.getCapabilities().GL_EXT_framebuffer_object;
	}

	public void destroy() {
		shader.destroy();
	}

	public void draw() {
		logic();
		render();
	}

	private void logic() {
		if (invalidated) {
			invalidated = false;
			resizeBuffer();
		}
		mvpMat = cam.getMVP();
		if (vertexCount == 0)
			return;
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		int offset = 0;
		FloatBuffer vertFloatBuff = vertByteBuff.asFloatBuffer();
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, vertByteBuff);
		vertFloatBuff.rewind();
		for (Drawable object : objects) {
			object.store(vertFloatBuff);
		}
		for (Drawable object : orthoObjects) {
			object.store(vertFloatBuff);
		}
		vertFloatBuff.flip();
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, vertByteBuff);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		// GL20.glUseProgram(shader.getPID());
		// mvpMat.get(0, matBuff);
		// // matBuff.flip();
		// // matBuff.put(0,5.0f);
		// GL20.glUniformMatrix4fv(shader.getMVPLocation(), false, matBuff);
		// GL20.glUseProgram(0);
	}

	private void render() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL20.glUseProgram(shader.getPID());
		//

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL30.glBindVertexArray(vaoId);

		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texManager.getTexture(tid));
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
		long offset = 0;
		int shift = 0;
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.01f);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		for (Drawable object : objects) {
			Matrix4f finalMVP = new Matrix4f(mvpMat);
			Matrix4f modelMat = new Matrix4f();
			Matrix4f scaler = new Matrix4f();
			scaler.scale(object.scale);
			modelMat.translate(object.translation);
			 modelMat.rotateXYZ(object.rotation.x,object.rotation.y,object.rotation.z);
			finalMVP.mul(scaler);
			finalMVP.mul(modelMat);
			finalMVP.get(0, matBuff);
			if(object.isChar){
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, texManager.getTexture(tid_charmap));
			}
			GL20.glUniformMatrix4fv(shader.getMVPLocation(), false, matBuff);
			if (object.isVisible())
				GL32.glDrawElementsBaseVertex(GL11.GL_TRIANGLES, object.getIndices().length, GL11.GL_UNSIGNED_BYTE,
						offset, shift);

			offset += object.getIndices().length;
			shift += object.getVertex().length;
			if(object.isChar){
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, texManager.getTexture(tid));
			}
		}
		render2d(offset, shift);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		//
		GL20.glUseProgram(0);
		glfwSwapBuffers(window);
		glfwPollEvents();

	}

	private void render2d(long offset, int shift) {
		cam.getOrthoMVP().get(0, matBuff);
		GL20.glUniformMatrix4fv(shader.getMVPLocation(), false, matBuff);
		
		for (Drawable object : orthoObjects) {
			if(object.isChar){
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, texManager.getTexture(tid_charmap));
			}
			if (object.isVisible())
				GL32.glDrawElementsBaseVertex(GL11.GL_TRIANGLES, object.getIndices().length, GL11.GL_UNSIGNED_BYTE,
						offset, shift);
			offset += object.getIndices().length;
			shift += object.getVertex().length;
		}
		// static utility function
	}

	private GLFWWindowSizeCallback resizeCallback() {
		return windowSizeCalback = new GLFWWindowSizeCallback() {

			@Override
			public void invoke(long window, int newwidth, int newheight) {
				width = newwidth;
				height = newheight;
				cam.resize(newwidth, newheight);
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

	public float getAspectRatio() {
		return width / (float) height;
	}

	public void setHeight(int height) {
		this.height = height;
		resizeWindow(width, height);
	}
}
