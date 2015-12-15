package me.sunchiro.game.engine.gl;

import java.io.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

class Shader {
	private int vsId = 0;
	private int fsId = 0;
	private int pId = 0;
	private int mvpMatLocation = 0;
	private int orthoMatLocation = 0;
	private int inverseLocation = 0;
	public void setupShader() {
		int errorCheckValue = GL11.glGetError();
		vsId = this.loadShader("/shaders/quad/vertex.glsl", GL20.GL_VERTEX_SHADER);
		// Load the fragment shader
		fsId = this.loadShader("/shaders/quad/fragment.glsl", GL20.GL_FRAGMENT_SHADER);
		pId = GL20.glCreateProgram();
		GL20.glAttachShader(pId, vsId);
		GL20.glAttachShader(pId, fsId);

		// Position information will be attribute 0
		GL20.glBindAttribLocation(pId, 0, "in_Position");
		// Color information will be attribute 1
		GL20.glBindAttribLocation(pId, 1, "in_Color");
		GL20.glBindAttribLocation(pId, 2, "in_TextureCoord");
		
		GL20.glLinkProgram(pId);
		GL20.glValidateProgram(pId);

		mvpMatLocation = GL20.glGetUniformLocation(pId, "mvpMatrix");
		inverseLocation = GL20.glGetUniformLocation(pId, "inverseAlpha");
		errorCheckValue = GL11.glGetError();
		if (errorCheckValue != GL11.GL_NO_ERROR) {
			System.out.println("ERROR - Could not create the shaders");
			System.exit(-1);
		}
	}

	public int loadShader(String filename, int type) {
		StringBuilder shaderSource = new StringBuilder();
		int shaderID = 0;

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filename)));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Could not read file.");
			e.printStackTrace();
			System.exit(-1);
		}

		shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);

		return shaderID;
	}

	public void destroy() {
		GL20.glUseProgram(0);
		GL20.glDetachShader(pId, vsId);
		GL20.glDetachShader(pId, fsId);

		GL20.glDeleteShader(vsId);
		GL20.glDeleteShader(fsId);
		GL20.glDeleteProgram(pId);
	}
	
	public int getPID(){
		return pId;
	}
	public int getMVPLocation(){
		return mvpMatLocation;
	}
	public int getOrthoMatLocation(){
		return orthoMatLocation;
	}
	public int getInverseLocation(){
		return inverseLocation;
	}
}
