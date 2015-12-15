package me.sunchiro.game;

import me.sunchiro.game.engine.Engine;
import me.sunchiro.game.engine.al.Audio;
import me.sunchiro.game.scene.Menu;
import me.sunchiro.game.scene.Scene;

public class Game {
	public Engine eng;
	private Thread engineThread;
	Scene current;
	public Player player;
	public static Game instance;
	public Game() {
		Game.instance = this;
		eng = new Engine();
		engineThread = new Thread(eng);
		synchronized (eng) {
			current = new Menu();
			mainLogic();
		}
	}

	public synchronized void mainLogic() {
		try {
			player = new Player();
			engineThread.start();
			eng.wait();
			current.init(eng.getGraphic());
			while (!eng.isDestroyed()) {
				eng.wait();
				Audio.soundSystem.checkFadeVolumes();
				if(current.update()){
					current = current.next();
					if(current==null)break;
					current.init(eng.getGraphic());
				}
				
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Audio.destroy();
		System.exit(0);
	}
}
