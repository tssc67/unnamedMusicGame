package me.sunchiro.game.engine.gl;

public class MathUtil {
	public static float cot(float radian){
		return (float)(1/Math.tan(radian));
	}
	public static float toRadian(float degree){
		return (float)Math.toRadians(degree);
	}
}
