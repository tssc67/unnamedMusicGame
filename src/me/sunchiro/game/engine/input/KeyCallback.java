package me.sunchiro.game.engine.input;

public abstract class KeyCallback {
	public ActionType actionType;
	public int key;
	public int mod;
	public enum	ActionType{
		HOLD,KEYDOWN,KEYUP,SHIFT
	}
	public abstract void run();
}
