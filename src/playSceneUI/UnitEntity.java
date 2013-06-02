/*Copyright 2012 Wong Cho Ching, all rights reserved.
 * This file is released under BSD 2-clause License.
 * For details, please read LICENSE.txt in this package.*/

package playSceneUI;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;


public class UnitEntity {
	String name;
	String iconPath;
	String GLIconPath;
	BufferedImage image;
	int[] texture = new int[2];
	int health;
	int attack;
	int speed;
	int costGold;
	int workerRequired;
	int size = 30;
	public UnitEntity(String name, String iconPath, String GLIconPath, int health, int attack, int speed, int costGold, int workerRequired){
		this.name = name;
		this.iconPath = iconPath;
		this.GLIconPath = GLIconPath;
		this.health = health;
		this.attack = attack;
		this.speed = speed;
		this.costGold = costGold;
		this.workerRequired = workerRequired;
	}
	public void loadTexture(GL2 gl){
		try {
			image = ImageIO.read(new File(GLIconPath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		WritableRaster raster = 
				Raster.createInterleavedRaster (DataBuffer.TYPE_BYTE,
						image.getWidth(),
						image.getHeight(),
						4,
						null);
		ComponentColorModel colorModel=
			new ComponentColorModel (ColorSpace.getInstance(ColorSpace.CS_sRGB),
					new int[] {8,8,8,8},
					true,
					false,
					ComponentColorModel.TRANSLUCENT,
					DataBuffer.TYPE_BYTE);
		BufferedImage dukeImg = 
				new BufferedImage (colorModel,
						raster,
						false,
						null);
		Graphics2D g = dukeImg.createGraphics();
		g.drawImage(image, null, null);
		DataBufferByte dukeBuf =
			(DataBufferByte)raster.getDataBuffer();
		byte[] dukeRGBA = dukeBuf.getData();
		ByteBuffer byteBuffer = ByteBuffer.wrap(dukeRGBA);
		byteBuffer.position(0);
		byteBuffer.mark();

		gl.glGenTextures(2, texture, 0);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, texture[0]);
		gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 1);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
		gl.glTexImage2D (GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, image.getWidth(), image.getHeight(), 0, GL2.GL_RGBA, 
				GL.GL_UNSIGNED_BYTE, byteBuffer);

		gl.glBindTexture(GL2.GL_TEXTURE_2D, texture[1]);
		gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 1);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
		gl.glTexImage2D (GL2.GL_TEXTURE_2D, 0, GL2.GL_LUMINANCE_ALPHA, image.getWidth(), image.getHeight(), 0, GL2.GL_RGBA, 
				GL.GL_UNSIGNED_BYTE, byteBuffer);
	}
}
