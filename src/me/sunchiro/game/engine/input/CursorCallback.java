package me.sunchiro.game.engine.input;


public abstract class CursorCallback {
	public ActionType actionType;
	public enum	ActionType{
		MOVE
	}
	public abstract void run(double distanceX,double distanceY);
}
