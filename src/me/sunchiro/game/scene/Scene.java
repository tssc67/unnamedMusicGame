package me.sunchiro.game.scene;

import me.sunchiro.game.engine.gl.Graphic;

public interface Scene {
	public void init(Graphic g);
	public boolean update();
	public Scene next();
}
