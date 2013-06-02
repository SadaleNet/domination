/*Copyright 2012 Wong Cho Ching, all rights reserved.
 * This file is released under BSD 2-clause License.
 * For details, please read LICENSE.txt in this package.*/

package header;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class G{ //G for global. All global variables goes here.
	
	public static int levelCompleted;
	public static int comicsDone;
	public static void loadData(){ //TODO untested.
		try {
			File file = new File("saveData");
			if(file.exists()){
				FileInputStream in = new FileInputStream(file);
				comicsDone = in.read();
				levelCompleted = in.read();
				in.close();
			}else{
				reset();
			}
		} catch (FileNotFoundException e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();}
	}
	public static void saveData(){ //TODO untested.
		try {
			FileOutputStream out = new FileOutputStream(new File("saveData"));
			out.write(comicsDone);
			out.write(levelCompleted);
			out.close();
		} catch (FileNotFoundException e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();}
	}
	public static void reset(){ //TODO untested.
		comicsDone = 0;
		levelCompleted = 0;
		saveData();
	}
}
