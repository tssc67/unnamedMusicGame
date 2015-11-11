package me.sunchiro.game.engine;

public abstract class KeyCallback {
	public ActionType actionType;
	public int key;
	public int mod;
	public enum	ActionType{
		HOLD,keyDown,keyUp
	}
	public KeyCallback(ActionType actionType,int key){
		this.actionType = actionType;
		this.key = key;
	}
	public abstract void run();
}
