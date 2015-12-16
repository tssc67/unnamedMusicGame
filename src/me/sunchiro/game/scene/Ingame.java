package me.sunchiro.game.scene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import de.matthiasmann.twl.renderer.Texture;
import de.matthiasmann.twl.textarea.TextAreaModel.Clear;
import me.sunchiro.game.Game;
import me.sunchiro.game.Player;
import me.sunchiro.game.engine.ConfigurationOption;
import me.sunchiro.game.engine.al.Audio;
import me.sunchiro.game.engine.gl.Camera;
import me.sunchiro.game.engine.gl.Generator;
import me.sunchiro.game.engine.gl.Graphic;
import me.sunchiro.game.engine.gl.MathUtil;
import me.sunchiro.game.engine.gl.object.CharQuad;
import me.sunchiro.game.engine.gl.object.Cube;
import me.sunchiro.game.engine.gl.object.Quad;
import me.sunchiro.game.engine.gl.object.Vertex;
import me.sunchiro.game.engine.input.InputManager;
import me.sunchiro.game.engine.input.KeyCallback;
import paulscode.sound.SoundSystemConfig;

public class Ingame implements Scene {
	private Player player;
	private CharQuad[] survive;
	private CharQuad[] keepMovin;
	private CharQuad[] andShoot;
	private double marker = 0;
	private Graphic g;
	PriorityQueue<timeEvent> events;
	private Quad bg;
	private InputManager input;
	public Vector3f accelStep = new Vector3f(0.03f, 0.1f, 0.03f);
	public Vector3f deceleration = new Vector3f(0.01f, 0.0051f, 0.01f);
	public float maxSpeed = 0.1f;
	public Vector3f accel;
	public Enemy[] enemy;
	public Queue<Bullet> bullets;
	public Queue<Bullet> bulletsInstance;
	public Queue<Effect> effQ;
	public float room = 30.0f;
	public Quad playerFace;
	public float stamina = 2.0f;
	public boolean isRunning = false;
	public boolean shaking = false;
	public float bulletWidth = 0.3f;
	public float life = 1.0f;

	public boolean lose = false;
	public boolean started = false;

	@Override
	public Scene next() {
		g.clearScene();
		Audio.soundSystem.stop("ratherbe");
		g.cam.eye.set(0,0,1);
		return new Menu();
	}

	@Override
	public void init(Graphic g) {
		Audio.load("hit", "/audios/HitSound.wav");
		Audio.load("ratherbe", "/audios/music.wav");
		bullets = new LinkedList<Bullet>();
		input = Game.instance.eng.input;
		bg = Generator.normalQuad();
		this.g = g;
		g.clearScene();
		events = new PriorityQueue<timeEvent>();
		bg.put(0, 0, -50, 4000, 4000, true);
		bg.mapTexture(new Vector2f(0.75f, 0.75f), new Vector2f(1, 1));
		bg.setAllRGBA(new float[] { 0, 0, 0, 1 });
		g.addOrthoObject(bg);
		survive = Generator.StringQuadGenerator("SURVIVE!", 80, 100, 100, 0, true);
		keepMovin = Generator.StringQuadGenerator("KEEP MOVIN!", 80, 100, 200, 0, true);
		andShoot = Generator.StringQuadGenerator(":)", 80, 100, 300, 0, true);
		for (int i = 0; i < survive.length; i++) {
			survive[i].setAllRGBA(new float[] { 1, 0, 0, 1 });
			survive[i].inverseAlpha = true;
		}
		g.addOrthoObjects(survive);
		for (int i = 0; i < keepMovin.length; i++) {
			keepMovin[i].setAllRGBA(new float[] { 0.2f, 0.8f, 0.2f, 1 });
			keepMovin[i].inverseAlpha = true;
		}
		g.addOrthoObjects(keepMovin);
		for (int i = 0; i < andShoot.length; i++) {
			andShoot[i].setAllRGBA(new float[] { 1, 1, 1, 1 });
			// andShoot[i].inverseAlpha = true;
		}
		g.addOrthoObjects(andShoot);
		player = Game.instance.player;

		marker = GLFW.glfwGetTime();
		events.add(new timeEvent() {
			{
				time = 3.0; 
			}

			@Override
			public void run() {
				startGame();
				started = true;
			}
		});
		accel = new Vector3f();
	}

	public void startGame() {
		g.tid = 2;
		g.clearScene();
		effQ = new LinkedList<Effect>();
		try {
			loadTempo("/tempo/ratherbe.txt");
		} catch (TempoLoadingException e) {//My exception
			System.out.println("Fail loading Tempo");
			e.printStackTrace();
		}
		bulletsInstance = new LinkedList<Bullet>();
		events.add(new timeEvent() {
			{
				time = time() + 1;
			}

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Audio.play("ratherbe");
			}
		});
		for (int i = 0; i < 5000; i++) {
			Quad q = Generator.normalQuad();
			q.put(0, 0, 0, bulletWidth, bulletWidth, false);
			q.mapTexture(new Vector2f(3 / 16f, 1 / 16f), new Vector2f(4 / 16f, 2 / 16f));
			Bullet bull = new Bullet(q, new Vector3f());
			bulletsInstance.add(bull);
			q.setVisible(false);
			g.addObject(bull.quad);
		}
		float faceWidth = 150;
		playerFace = Generator.normalQuad();
		playerFace.put(g.getWidth() / 2 - faceWidth / 2, g.getHeight() - faceWidth, 0, faceWidth, faceWidth, true);
		playerFace.mapTexture(new Vector2f(0, 2 / 16f), new Vector2f(1 / 16f, 3 / 16f));
		g.addOrthoObject(playerFace);
		Cube[] enemies = new Cube[ConfigurationOption.objectCount];
		enemy = new Enemy[ConfigurationOption.objectCount];
		for (int i = 0; i < ConfigurationOption.objectCount; i++) {
			float size = 1 + (float) Math.random();
			enemies[i] = Generator.normalCube();
			enemies[i].put(-size / 2, -size / 2, -size / 2, size, size, size);
			enemies[i].mapTexture(new Vector2f(0, 0.0626f), new Vector2f(0.1874f, 0.09374f));
			enemies[i].translation.add((float) Math.random() * 100 - 50, -10 + (float) (Math.random() * 20),
					(float) Math.random() * 100 - 50);
			enemy[i] = new Enemy(enemies[i]);
		}
		g.addObjects(enemies);
		registerMovement();
	}

	public void registerMovement() {
		// Forward movement
		input.keyRegister.add(new KeyCallback() {
			{
				key = GLFW.GLFW_KEY_W;
				actionType = ActionType.KEYDOWN;
			}

			@Override
			public void run() {
				accel.z += accelStep.z;
			}
		});
		input.keyRegister.add(new KeyCallback() {
			{
				key = GLFW.GLFW_KEY_W;
				actionType = ActionType.KEYUP;
			}

			@Override
			public void run() {
				accel.z -= accelStep.z;
			}
		});
		// Left Movement
		input.keyRegister.add(new KeyCallback() {
			{
				key = GLFW.GLFW_KEY_A;
				actionType = ActionType.KEYDOWN;
			}

			@Override
			public void run() {
				accel.x += accelStep.x;
			}
		});
		input.keyRegister.add(new KeyCallback() {
			{
				key = GLFW.GLFW_KEY_A;
				actionType = ActionType.KEYUP;
			}

			@Override
			public void run() {
				accel.x -= accelStep.x;
			}
		});
		// Right Movement
		input.keyRegister.add(new KeyCallback() {
			{
				key = GLFW.GLFW_KEY_D;
				actionType = ActionType.KEYDOWN;
			}

			@Override
			public void run() {
				accel.x -= accelStep.x;
			}
		});
		input.keyRegister.add(new KeyCallback() {
			{
				key = GLFW.GLFW_KEY_D;
				actionType = ActionType.KEYUP;
			}

			@Override
			public void run() {
				accel.x += accelStep.x;
			}
		});
		// Backward strife movement
		input.keyRegister.add(new KeyCallback() {
			{
				key = GLFW.GLFW_KEY_S;
				actionType = ActionType.KEYDOWN;
			}

			@Override
			public void run() {
				accel.z -= accelStep.z;
			}
		});
		input.keyRegister.add(new KeyCallback() {
			{
				key = GLFW.GLFW_KEY_S;
				actionType = ActionType.KEYUP;
			}

			@Override
			public void run() {
				accel.z += accelStep.z;
			}
		});
		// Jump
		input.keyRegister.add(new KeyCallback() {
			{
				key = GLFW.GLFW_KEY_SPACE;
				actionType = ActionType.KEYDOWN;
			}

			@Override
			public void run() {
				if (player.loc.y < 0.01f) {
					player.velocity.y = accelStep.y;
				}

			}
		});
		// Sprint
		input.keyRegister.add(new KeyCallback() {
			{
				key = GLFW.GLFW_KEY_LEFT_SHIFT;
				actionType = ActionType.KEYDOWN;
			}

			@Override
			public void run() {
				isRunning = true;
			}
		});
		input.keyRegister.add(new KeyCallback() {
			{
				key = GLFW.GLFW_KEY_LEFT_SHIFT;
				actionType = ActionType.KEYUP;
			}

			@Override
			public void run() {
				isRunning = false;
			}
		});
		// SHAKKKKEEE
		input.keyRegister.add(new KeyCallback() {
			{
				key = GLFW.GLFW_KEY_Z;
				actionType = ActionType.KEYDOWN;
			}

			@Override
			public void run() {
				// TODO Auto-generated method stub
				shaking = true;
				record(0);
			}
		});
		input.keyRegister.add(new KeyCallback() {
			{
				key = GLFW.GLFW_KEY_Z;
				actionType = ActionType.KEYUP;
			}

			@Override
			public void run() {
				// TODO Auto-generated method stub
				shaking = false;
				record(0);
			}
		});
		// OUCH! MY EYE
		input.keyRegister.add(new KeyCallback() {
			{
				key = GLFW.GLFW_KEY_X;
				actionType = ActionType.KEYDOWN;
			}

			@Override
			public void run() {
				// TODO Auto-generated method stub
				record(1);
				flashAll();
			}
		});
		input.keyRegister.add(new KeyCallback() {
			{
				key = GLFW.GLFW_KEY_C;
				actionType = ActionType.KEYDOWN;
			}

			@Override
			public void run() {
				// TODO Auto-generated method stub
				record(1);
				flashAll();
			}
		});
	}

	@Override
	public boolean update() {
		eventCheck();

		if (!started)
			return false;
		System.out.println(!Audio.soundSystem.playing("ratherbe"));
		if (lose)
			return true;
		moveBullets();
		updateEnemy();
		movePlayer();
		g.cam.eye.set(player.loc);
		g.cam.eye.y += 0.75f;
		playerFace.setAllRGBA(new float[] { life, life, life, 1 });
		effect();
		return false;
	}

	public double time() {
		return GLFW.glfwGetTime() - marker;
	}

	public void eventCheck() {
		if (events.size() == 0)
			return;
		if (events.peek().time <= time()) {
			events.poll().run();
		}
	}

	public void record(int v) {
		System.out.println(Audio.getTime("ratherbe") + ":" + v + ",");
	}

	public void effect() {
		if (effQ.size() > 0) {
			if (effQ.peek().time <= Audio.getTime("ratherbe")) {
				int action = effQ.poll().action;
				// 1 flash //0 shake
				if (action == 0) {
					shaking ^= true;
				}
				if (action == 1) {
//					flashAll();
					shootPossibility(0.01f);
				}
				if(action == 2){
					System.out.println("fuck");
					win();
				}
			}
		}
		if (shaking) {
			g.cam.eye.add((float) Math.random() * 0.1f - 0.05f, (float) Math.random() * 0.1f - 0.05f,
					(float) Math.random() * 0.1f - 0.05f);
		}
	}

	public void updateEnemy() {
		for (Enemy en : enemy) {
			if (en.flashing) {
				en.cube.setAllRGBA(new float[] { en.flashv, en.flashv, en.flashv, en.flashv });
				en.flashv -= 0.03f;
				if (en.flashv <= 0.4) {
					en.flashing = false;
					en.cube.inverseAlpha = false;
					en.cube.setAllRGBA(new float[] { 1, 1, 1, 1 });
				}
			}
		}
	}

	public void moveBullets() {
		int bulletCount = bullets.size();
		if (bulletCount == 0)
			return;
		Bullet cur;
		for (int i = 0; i < bulletCount; i++) {
			cur = bullets.poll();
			if (new Vector3f(cur.quad.translation).sub(player.loc).length() < bulletWidth) {
				Audio.soundSystem.quickPlay(true, Audio.class.getResource("/audios/HitSound.wav"), "HitSound.wav",
						false, 0, 0, 0, SoundSystemConfig.ATTENUATION_NONE, 0);
				if(!ConfigurationOption.godMode)
				life -= .1f;
				if (life < 0.1f) {
					lose();
				}
			}
			cur.quad.translation.add(cur.v);
			Vector3f bulloc = cur.quad.translation;
			if (bulloc.y < -room || bulloc.y > room ) {
				bulletsInstance.add(cur);
				cur.quad.setVisible(false);
			} else {
				bullets.add(cur);
			}
		}
	}

	public void movePlayer() {
		Vector3f diff = new Vector3f(player.velocity);
		diff.add(accel);
		if (deceleration.x > Math.abs(diff.x)) {
			diff.x = 0;
		} else {
			diff.x -= diff.x < 0 ? -deceleration.x : deceleration.x;
		}
		if (deceleration.z > Math.abs(diff.z)) {
			diff.z = 0;
		} else {
			diff.z -= diff.z < 0 ? -deceleration.z : deceleration.z;
		}
		stamina += 0.002f;
		float finalMaxspeed = maxSpeed;
		float maxStamina = 0.4f;
		if (stamina > maxStamina)
			stamina = maxStamina;
		if (isRunning) {
			finalMaxspeed = maxSpeed + stamina;
			stamina -= 0.004;
			if (stamina < 0)
				stamina = 0;
		}
		float stamRate = stamina / maxStamina;
		g.bgColor.set(stamRate * 0.8f, stamRate * 0.8f, stamRate * 0.8f);
		diff.y = 0;
		if (diff.length() > finalMaxspeed) {
			diff.normalize().mul(finalMaxspeed);
		}
		diff.y = player.velocity.y;
		diff.y -= deceleration.y;

		if (-diff.y > player.loc.y) {
			diff.y = 0;
			player.loc.y = 0;
		} else {
			player.loc.y += diff.y;
		}

		player.velocity.set(diff);
		new Matrix4f().rotateY(MathUtil.toRadian(g.cam.yaw)).transformPoint(diff);
		player.loc.add(diff);
		if (player.loc.x < -room)
			player.loc.x = -room;
		if (player.loc.x > room)
			player.loc.x = room;
		if (player.loc.z < -room)
			player.loc.z = -room;
		if (player.loc.z > room)
			player.loc.z = room;
	}

	public void fire(Enemy en, Vector3f velocity) {
		Bullet bull = bulletsInstance.poll();
		Quad q = bull.quad;
		q.translation.set(en.cube.translation);
		bull.v = velocity;
		q.setVisible(true);
		bullets.add(bull);
	}

	public void fire(int boxId, Vector3f velocity) {
		fire(enemy[boxId], velocity);
	}

	public void fire(int boxId) {
		fire(boxId, new Vector3f().add(player.loc).sub(enemy[boxId].cube.translation).normalize().mul(0.3f));
	}

	public void fire(Enemy en) {
		fire(en, new Vector3f().add(player.loc).sub(en.cube.translation).normalize().mul(0.3f));
	}

	public void flashAll() {
		for (Enemy en : enemy) {
			en.flash();
		}
	}

	public void shootPossibility(float p) {
		for (Enemy en : enemy) {
			if (Math.random() < p) {
				en.flash();
				fire(en);
			}
		}
	}

	public void loadTempo(String filename) throws TempoLoadingException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filename)));
		StringBuilder out = new StringBuilder();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				out.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new TempoLoadingException(e);
		}
		String[] tempos = out.toString().split(",");
		int i = 0;
		for (String tempo : tempos) {
			String[] duet = tempo.split(":");
			try{
				float time = Float.parseFloat(duet[0]);
				int action = Integer.parseInt(duet[1]);
				effQ.add(new Effect(time, action));
			}catch(Exception e){
				e.printStackTrace();
				throw new TempoLoadingException("Wrong file format!");
			}
			i++;
		}
	}

	public void lose() {
		CharQuad[] youLose;
		g.clearScene();
		youLose = Generator.StringQuadGenerator("You lose :|", 120, 10, 200, 0, true);
		g.addOrthoObject(bg);
		g.addOrthoObjects(youLose);
		events.add(new timeEvent() {
			{
				time = time() + 1.5;
			}

			@Override
			public void run() {
				lose = true;
			}
		});
	}
	public void win() {
		CharQuad[] youWin;
		g.clearScene();
		youWin = Generator.StringQuadGenerator("Nailed it :D", 120, 10, 200, 0, true);
		g.addOrthoObject(bg);
		g.addOrthoObjects(youWin);
		events.add(new timeEvent() {
			{
				time = time() + 1.5;
			}

			@Override
			public void run() {
				lose = true;
			}
		});
	}
}
class TempoLoadingException extends Exception{
		public TempoLoadingException(){
		}
		public TempoLoadingException(String message){
			super(message);
		}
		public TempoLoadingException(Throwable cause){
			super(cause);
		}
}
class Effect {
	public float time;
	public int action;

	public Effect(float time, int action) {
		this.time = time;
		this.action = action;
	}
}

class Enemy {
	public Cube cube;
	public boolean flashing = false;
	public float flashv = 0.0f;

	public void flash() {
		flashing = true;
		flashv = 1;
		cube.inverseAlpha = true;
	}

	public Enemy(Cube cube) {
		this.cube = cube;
	}
}

class Bullet {
	public Quad quad;
	public Vector3f v;

	public Bullet(Quad quad, Vector3f velocity) {
		this.quad = quad;
		this.v = velocity;
	}
}

abstract class timeEvent implements Comparable<timeEvent> {
	double time = 0;

	public abstract void run();

	public int compareTo(timeEvent event) {
		return time < event.time ? -1 : 1;
	}
}
