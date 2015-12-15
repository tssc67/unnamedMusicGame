package me.sunchiro.game.scene;

import org.joml.Vector2f;

import me.sunchiro.game.engine.gl.Generator;
import me.sunchiro.game.engine.gl.Graphic;
import me.sunchiro.game.engine.gl.object.Quad;

public class Ingame implements Scene{

	@Override
	public void init(Graphic g) {
		// TODO Auto-generated method stub
		g.clearScene();
		Quad bg = Generator.normalQuad();
		bg.put(0, 0, -1, 4000, 4000);
		bg.mapTexture(new Vector2f(0,0), new Vector2f(1,1));
		g.addOrthoObject(bg);
	}

	@Override
	public boolean update() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Scene next() {
		return null;
	}

}
