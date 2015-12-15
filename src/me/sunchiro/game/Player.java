package me.sunchiro.game;

import org.joml.Vector3f;

public class Player {
	public Vector3f loc;
	public Vector3f velocity; 
	public float pitch=0,yaw=0;
	public Player(){
		loc = new Vector3f();
		velocity = new Vector3f();
	}
}
