package me.sunchiro.game.engine.gl;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
	public Vector3f eye = new Vector3f(0, 0, 1);
	// public Vector3f look = new Vector3f(0,0,0);
	public float pitch = 0, yaw = 0;
	public float fov = 60f;
	private float aspectRatio = 800f / 600f;
	private float near_plane = 0.1f;
	private float far_plane = 100f;
	private int screenWidth = 800, screenHeight = 600;

	public void resize(int width, int height) {
		aspectRatio = width / (float) height;
		screenWidth = width;
		screenHeight = height;
	}

	private Vector3f getLookVector() {
		Vector3f look = new Vector3f(0, 0, -1);
		new Matrix4f().rotateX(MathUtil.toRadian(pitch)).transformPoint(look);
		new Matrix4f().rotateY(MathUtil.toRadian(yaw)).transformPoint(look);
		new Matrix4f().translate(eye).transformPoint(look);
		return look;
	}

	public Matrix4f getMVP() {
		Vector3f look = getLookVector();
		// return new Matrix4f().ortho(-1*aspectRatio, 1*aspectRatio, -1, 1,
		// 0.1f, 100f).lookAt(eye.x,eye.y, eye.z, look.x, look.y, look.z, 0.0f,
		// 1.0f, 0.0f);
		return new Matrix4f().perspective(MathUtil.toRadian(60), aspectRatio, near_plane, far_plane).lookAt(eye.x,
				eye.y, eye.z, look.x, look.y, look.z, 0.0f, 1.0f, 0.0f);
	}

	public Matrix4f getOrthoMVP() {
//		return new Matrix4f().ortho(-screenWidth/2.0f, screenWidth/2.0f, -screenHeight, screenHeight, near_plane, far_plane)
		return new Matrix4f().ortho(0, screenWidth, screenHeight, 0, near_plane, far_plane)
				.lookAt(0.0f,0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
	}
	// public void rotateCameraXY(float x,float y){//radian
	// rotateCameraX(x);
	// rotateCameraY(y);
	// }
	// public void rotateCameraX(float x){
	// pitch
	// }
	// public void rotateCameraY(float y){
	//
	// }
}
