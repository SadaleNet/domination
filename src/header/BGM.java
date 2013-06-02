/*Copyright 2012 Wong Cho Ching, all rights reserved.
 * This file is released under BSD 2-clause License.
 * For details, please read LICENSE.txt in this package.*/

package header;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;


public class BGM{
	static Clip clip = null;
	static String oldPath = null;
	public static void play(String path, boolean loop){
		if(path!=oldPath){
			if(clip!=null){
				try { 
					if(AudioSystem.getLine(clip.getLineInfo()).isOpen())
						AudioSystem.getLine(clip.getLineInfo()).close();
				} catch (LineUnavailableException e) {e.printStackTrace();
				}
				if(clip.isOpen())
					clip.close();
			}
		    try {
		    	AudioInputStream sound = AudioSystem.getAudioInputStream(new File(path));
		    	DataLine.Info info = new DataLine.Info(Clip.class, sound.getFormat());
			    clip = (Clip) AudioSystem.getLine(info);
				clip.open(sound);
			} catch (LineUnavailableException e) {
			} catch (IOException e) {
			} catch (UnsupportedAudioFileException e) {
			}

			if(loop)
				clip.loop(-1);
			else
				clip.start();
			oldPath = path;
		}
	}
}