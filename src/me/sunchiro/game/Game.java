package me.sunchiro.game;

import me.sunchiro.game.engine.Engine;

public class Game {
	protected Engine eng;
	public Game(){
		eng = new Engine();
		eng.run();
	}
}
