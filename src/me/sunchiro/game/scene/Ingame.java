package me.sunchiro.game.scene;

import java.util.PriorityQueue;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import me.sunchiro.game.Game;
import me.sunchiro.game.Player;
import me.sunchiro.game.engine.gl.Generator;
import me.sunchiro.game.engine.gl.Graphic;
import me.sunchiro.game.engine.gl.object.CharQuad;
import me.sunchiro.game.engine.gl.object.Quad;

public class Ingame implements Scene {
	private Player player;
	private CharQuad[] survive;
	private double marker = 0;
	PriorityQueue<timeEvent> events;
	@Override
	public void init(Graphic g) {
		g.clearScene();
		Quad bg = Generator.normalQuad();
		events = new PriorityQueue<timeEvent>();
		bg.put(0, 0, -1, 4000, 4000);
		bg.mapTexture(new Vector2f(0.75f, 0.75f), new Vector2f(1, 1));
		bg.setAllRGBA(new float[] { 0, 0, 0, 1 });
		g.addOrthoObject(bg);
		survive = Generator.StringQuadGenerator("|SURVIVE!", 100, 100, g.getHeight() / 2 - 100, 0, true);
		for (int i = 1; i <= 8; i++) {
			survive[i].setAllRGBA(new float[] { 1, 0, 0, 1 });
			survive[i].inverseAlpha = true;
		}
		g.addOrthoObjects(survive);
		player = Game.instance.player;
		marker = GLFW.glfwGetTime();
		events.add(new timeEvent() {
			{
				time = 3.0;
			}
			@Override
			public void run() {
				startGame();
			}
		});
	}

	@Override
	public boolean update() {
		// TODO Auto-generated method stub
		eventCheck();

		return false;
	}
	
	@Override
	public Scene next() {
		return null;
	}

	public double time() {
		return GLFW.glfwGetTime() - marker;
	}

	public void eventCheck() {
		if(events.size() == 0)return;
		if (events.peek().time <= time()) {
			events.poll().run();
		}
	}
	
	public void startGame(){
		
	}
}

abstract class timeEvent implements Comparable<timeEvent> {
	double time = 0;

	public abstract void run();

	public int compareTo(timeEvent event) {
		return time < event.time ? -1 : 1;
	}
}

abstract class musicTimeEvent {
	float time;
	String source;

	public abstract void run();
}
