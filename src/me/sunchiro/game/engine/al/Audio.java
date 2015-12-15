package me.sunchiro.game.engine.al;


import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryJavaSound;

public class Audio {
	// public HashMap<String, WaveData> waves;
	static SoundSystem soundSystem;

	public static void init() {
		try {
			SoundSystemConfig.setCodec("wav", CodecWav.class);
		} catch (SoundSystemException e) {
			e.printStackTrace();
		}
		try {
			soundSystem = new SoundSystem(LibraryJavaSound.class);
		} catch (SoundSystemException e) {
			System.out.println("JavaSound library is not compatible on " + "this computer");
			e.printStackTrace();
			return;
		}
	}
	public static void load(String name,String filename){
		soundSystem.newStreamingSource
		(true, name, Audio.class.getResource(filename),filename, true, 0, 0, 0, SoundSystemConfig.ATTENUATION_NONE, 0);
	}
	public static void destroy(){
		soundSystem.cleanup();
	}
	public static void play(String source){
		soundSystem.play(source);
	}
	public static float getTime(String source){
		return soundSystem.millisecondsPlayed(source);
	}
}
