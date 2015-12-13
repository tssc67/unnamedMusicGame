package me.sunchiro.game.engine.input;

public abstract class KeyCallback {
	public ActionType actionType;
	public int key;
	public int mod;
	public enum	ActionType{
		HOLD,keyDown,keyUp
	}
	public abstract void run();
}
