/*Copyright 2012 Wong Cho Ching, all rights reserved.
 * This file is released under BSD 2-clause License.
 * For details, please read LICENSE.txt in this package.*/

package UIOverrides;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.nativewindow.util.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.jogamp.graph.font.Font;

public class JLabelBackground extends JLabel
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// The Image to store the background image in.
    Image img;
    public JLabelBackground(String path)
    {
    	setImage(path);
    }

    public void setImage(String path)
    {
    	if(path.equals("")){
    		img = null;
    	}else{
	        // Loads the background image and stores in img object.
	    	try {
				img = ImageIO.read(new File(path));
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }

    public void paint(Graphics g)
    {
    	if(img!=null){
	        // Draws the img to the BackgroundPanel.
	        g.drawImage(img, 0, 0, null);
    	}
    }
}