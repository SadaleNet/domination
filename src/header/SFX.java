/*Copyright 2012 Wong Cho Ching, all rights reserved.
 * This file is released under BSD 2-clause License.
 * For details, please read LICENSE.txt in this package.*/

package header;

import java.applet.Applet;

import sun.audio.*;
import java.applet.AudioClip;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SFX extends Thread{
	static Clip clip;
	public static void init(){
		try {
			clip = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void play(String path){
		try {
			//final Clip clip = AudioSystem.getClip();
	    	AudioInputStream sound = AudioSystem.getAudioInputStream(new File(path));
			/*if(clip.isOpen()){
				clip.close();
			}*/
	    	clip = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));
			clip.open(sound);
			clip.start();
			/*clip.addLineListener(new LineListener(){
				@Override
				public void update(LineEvent event) {
					if(!clip.isRunning()){
					}
				}
			});*/
		} catch (LineUnavailableException e) {e.printStackTrace();
		} catch (MalformedURLException e) {e.printStackTrace();
		} catch (IOException e) {e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {e.printStackTrace();
		} catch(java.lang.IllegalStateException e){/*System.out.println("Sorry. Could not play sound!");*/ //JVM cannot connect with pulse audio in linux. :(
		} catch(java.lang.IllegalArgumentException e){/*System.out.println("Sorry. Could not play sound!");*/ //JVM cannot connect with pulse audio in linux. :(
		}
	}
}


/*

public class SFX extends Thread{
	
	public static void play(String path){
		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(new File(path).toURI().toURL()));
			clip.start();
		} catch (LineUnavailableException e) {e.printStackTrace();
		} catch (MalformedURLException e) {e.printStackTrace();
		} catch (IOException e) {e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {e.printStackTrace();
		} catch(java.lang.IllegalStateException e){System.out.println("Sorry. Could not play sound!"); //JVM cannot connect with pulse audio in linux. :(
		} catch(java.lang.IllegalArgumentException e){System.out.println("Sorry. Could not play sound!"); //JVM cannot connect with pulse audio in linux. :(
		}
	}
}
*/