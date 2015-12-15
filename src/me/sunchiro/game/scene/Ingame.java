package me.sunchiro.game.scene;

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
	public Cube[] enemy;
	public Queue<Bullet> bullets;
	public float room = 30.0f;
	public Quad playerFace;
	public float stamina=2.0f;
	public boolean isRunning = false;
	public float bulletWidth = 0.3f;
	@Override
	public void init(Graphic g) {
		bullets = new LinkedList<Bullet>();
		input = Game.instance.eng.input;
		bg = Generator.normalQuad();
		this.g = g;
		g.clearScene();
		events = new PriorityQueue<timeEvent>();
		bg.put(0, 0, -50, 4000, 4000);
		bg.mapTexture(new Vector2f(0.75f, 0.75f), new Vector2f(1, 1));
		bg.setAllRGBA(new float[] { 0, 0, 0, 1 });
		g.addOrthoObject(bg);
		survive = Generator.StringQuadGenerator("SURVIVE!", 80, 100, 100, 0, true);
		keepMovin = Generator.StringQuadGenerator("KEEP MOVIN!", 80, 100, 200, 0, true);
		andShoot = Generator.StringQuadGenerator("AND SHOOT!!", 80, 100, 300, 0, true);
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
			}
		});
		accel = new Vector3f();
	}

	@Override
	public boolean update() {
		// TODO Auto-generated method stub
		moveBullets();
		eventCheck();
		movePlayer();
		g.cam.eye = player.loc;
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
		if (events.size() == 0)
			return;
		if (events.peek().time <= time()) {
			events.poll().run();
		}
	}

	public void startGame() {
		g.tid = 2;
		g.clearScene();
		
		// simpleCube = Generator.normalCube();
		// simpleCube.put(-0.5f, -0.5f, -0.5f, 1, 1, 1);
		// simpleCube.mapTexture(new Vector2f(0, 0), new Vector2f(6 / 16f, 1 /
		// 16f));
		// g.addObject(simpleCube);
		float faceWidth = 150;
		playerFace = Generator.normalQuad();
		// playerFace.put(0, 0, 0, 100, 100);
		playerFace.put(g.getWidth() / 2 - faceWidth / 2, g.getHeight() - faceWidth, 0, faceWidth, faceWidth);
		playerFace.mapTexture(new Vector2f(0, 2 / 16f), new Vector2f(1 / 16f, 3 / 16f));
		g.addOrthoObject(playerFace);
		enemy = new Cube[1900];
		for (int i = 0; i < 1900; i++) {
			float size = 1 + (float) Math.random();
			enemy[i] = Generator.normalCube();
			enemy[i].put(-size / 2, -size / 2, -size / 2, size, size, size);
			enemy[i].mapTexture(new Vector2f(0, 0.0626f), new Vector2f(0.1874f, 0.09374f));
			enemy[i].translation.add((float) Math.random() * 100 - 50, 5 + (float) (Math.random() * 20),
					(float) Math.random() * 100 - 50);
		}
		g.addObjects(enemy);
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

	}
	public void moveBullets(){
		
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
		float finalMaxspeed =maxSpeed;
		float maxStamina = 0.4f;
		if(stamina>maxStamina)stamina = maxStamina;
		if(isRunning){
			finalMaxspeed = maxSpeed + stamina;
			stamina -= 0.004;
			if(stamina<0) stamina = 0;
		}
		float stamRate = stamina/maxStamina;
		g.bgColor.set(stamRate*0.8f,stamRate*0.8f,stamRate*0.8f);
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
	public void fire(int boxId,Vector3f velocity){
		Quad q = Generator.normalQuad();
		q.put(0, 0, 0, bulletWidth, bulletWidth);
		q.mapTexture(new Vector2f(0.5f,0f), new Vector2f(0.75f,0.25f));
		bullets.add(new Bullet(q, velocity));
	}
	public void fire(int boxId){
		fire(boxId,new Vector3f().add(player.loc).sub(enemy[boxId].translation).normalize().mul(1));
	}
}
class Bullet {
	Quad quad;
	public Vector3f v;
	public Bullet(Quad quad,Vector3f velocity){
		this.quad = quad;
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
