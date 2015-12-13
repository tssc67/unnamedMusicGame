package me.sunchiro.game.engine.gl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class TextureManager {
	private List<Integer> textures = new ArrayList<Integer>();
	public int getTexture(int id) {
		return textures.get(id);
	}
	public int loadTexture(String filename, int textureUnit) {
		ByteBuffer buff = null;
		int tWidth = 0;
		int tHeight = 0;
		try {
			InputStream in = getClass().getResourceAsStream(filename);
			PNGDecoder decoder = new PNGDecoder(in);
			tWidth = decoder.getWidth();
			tHeight = decoder.getHeight();
			System.out.println(decoder.hasAlpha());
			buff = ByteBuffer.allocateDirect(4 * tWidth * tHeight);
			decoder.decode(buff, tWidth*4, Format.RGBA);
			buff.flip();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		int texId = GL11.glGenTextures();
		GL13.glActiveTexture(textureUnit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, tWidth, tHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
				buff);
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

		// Setup the ST coordinate system
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

		// Setup what to do when the texture has to be scaled
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);

		int errorValue = GL11.glGetError();
		 if (errorValue != GL11.GL_NO_ERROR) {
			 System.out.println("Texture loading error");
			 System.exit(-1);
		 }
		textures.add(texId);
		return textures.size() - 1;
	}
}
