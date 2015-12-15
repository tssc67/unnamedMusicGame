package me.sunchiro.game.engine.input;


public abstract class CursorCallback {
	public ActionType actionType;
	public enum	ActionType{
		MOVE,CLICK
	}
	public abstract void run(double x,double y);
}
