package me.sunchiro.game.scene;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import me.sunchiro.game.Game;
import me.sunchiro.game.engine.al.Audio;
import me.sunchiro.game.engine.gl.Generator;
import me.sunchiro.game.engine.gl.Graphic;
import me.sunchiro.game.engine.gl.MathUtil;
import me.sunchiro.game.engine.gl.object.CharQuad;
import me.sunchiro.game.engine.gl.object.Cube;
import me.sunchiro.game.engine.gl.object.Quad;
import me.sunchiro.game.engine.input.KeyCallback;

public class Menu implements Scene{
	protected CharQuad[] head;
	protected Quad logo;
	protected CharQuad[] pressEnter;
	private Graphic g;
	private boolean startGame = false;
	private int myTex = 0;
	private double startingTime = 0; 
	private boolean started = false;
	private void genCube(){//=[]= much cube
		Cube[] cube = new Cube[1500]; 
		for(int i=0;i<1500;i++){
			cube[i] = Generator.normalCube();
			cube[i].put(0, 0, 0, 1, 1, 1);
			cube[i].translation.add((float)Math.random()*22.0f-11.0f,(float)Math.random()*22.0f,(float)Math.random()*22.0f - 11.0f);
			cube[i].rotation.add((float)Math.random(),(float)Math.random(),(float)Math.random());
			cube[i].mapTexture(new Vector2f(0.5f,0.5f), new Vector2f(1,1));
			g.addObject(cube[i]);
			cube[i].setAllRGBA(new float[]{(float)Math.random(),(float)Math.random(),(float)Math.random(),1});
		}
//		Quad test = Generator.normalQuad();
//		test.put(0, 0, 1, 2, 2);
//		test.translation.z = -2;
//		test.mapTexture(new Vector2f(0.5f, 0.5f), new Vector2f(1,1));
//		g.addObject(test);
	}
	@Override
	public void init(Graphic g) {
		this.g=g;
		// TODO Auto-generated method stub
		Audio.load("Intro", "/audios/intro.wav");
		Audio.play("Intro");
		head = Generator.StringQuadGenerator("in_the_Box", 100f, 100, 100, 0,true);
		g.cam.yaw=180;
		pressEnter = Generator.StringQuadGenerator("Press enter to enjoy!!", 0.4f, -2.5f, -2, -2,false);
		logo = Generator.normalQuad();
		logo.put(-1.5f, -1.5f,0f,3 , 3,true);
		logo.translation.y = -3;
		logo.translation.z = 1;
		logo.rotation.x=MathUtil.toRadian(90);
		logo.mapTexture(new Vector2f(0, 0), new Vector2f(0.25f,0.25f));
		g.tid = 1;
//		g.cam.yaw+=180;
		g.addOrthoObjects(head);
		g.addObject(logo);
		g.addObjects(pressEnter);
		genCube();
		Game.instance.eng.input.keyRegister.add(new KeyCallback() {
			{
				key = GLFW.GLFW_KEY_ENTER;
				actionType = ActionType.KEYDOWN;
			}
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(started)return;
				started =true;
				startingTime = GLFW.glfwGetTime();
			}
		});
		Game.instance.eng.input.keyRegister.add(new KeyCallback() {
			{
				key = GLFW.GLFW_KEY_X;
				actionType = ActionType.KEYDOWN;
			}
			@Override
			public void run() {
				// TODO Auto-generated method stub
			}
		});
	}
	double startDelay = 1.000;
	@Override
	public boolean update() {
		
		float time = Audio.getTime("Intro");
		logo.scale = (time%480) / 480f *  1 + 1;
		if(started){
			logo.scale += (GLFW.glfwGetTime() - startingTime)/startDelay * 50;
		}
		startGame = (GLFW.glfwGetTime() - startingTime) > startDelay && started;
		if(startGame){
			Game.instance.eng.input.keyRegister.clear();
			Audio.soundSystem.fadeOut("Intro", null, 2000);
		}
		
		return startGame;
	}

	@Override
	public Scene next() {
		// TODO Auto-generated method stub
		return new Ingame();
	}

}
