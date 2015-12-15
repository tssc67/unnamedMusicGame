package me.sunchiro.game.engine.gl.object;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class CharQuad extends Quad {
	private static Font awt;
	private static float[][] charMap = new float[256][4];
	private static float comparedWidth;

	private static FontMetrics createFontMetrics(Font font) {
		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics g = bi.getGraphics();
		FontMetrics fm = g.getFontMetrics(font);
		return fm;
	}

	static {
		try {
			InputStream inputStream = CharQuad.class.getResourceAsStream("/futurabk.ttf");
			awt = Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(256f);
		} catch (FontFormatException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		FontMetrics fmt = createFontMetrics(awt);
		float left = 0;
		float top = 0;
		float height = 0.125f;
		float width = 1 / 16f;
		for (int i = 32; i < 160; i++) {
			float cw = fmt.charWidth(i) / 4096f;
			if (i % 16 == 0 && i > 32) {
				left = 0;
				top += height;
			}
			charMap[i][0] = left;
			charMap[i][1] = top;
			charMap[i][2] = left + cw;
			charMap[i][3] = top + height;
			left += width;

		}
		comparedWidth = charMap['A'][2] - charMap['A'][0];
	}

	public CharQuad(Vertex[] vertexs) {
		super(vertexs);
		this.isChar = true;
	}

	public void setCharacter(Character c) {
		vertexs[0].setUV(charMap[c][0], charMap[c][1]);
		vertexs[1].setUV(charMap[c][0], charMap[c][3]);
		vertexs[2].setUV(charMap[c][2], charMap[c][3]);
		vertexs[3].setUV(charMap[c][2], charMap[c][1]);
	}

	public static float getCharWidth(Character ch) {
		return (charMap[ch][2] - charMap[ch][0]) / comparedWidth;
	}
}
