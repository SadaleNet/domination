/*Copyright 2012 Wong Cho Ching, all rights reserved.
 * This file is released under BSD 2-clause License.
 * For details, please read LICENSE.txt in this package.*/

package UIOverrides;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Dimension;

public class JLabelLineWrap extends JLabel {
	 
	JLabelLineWrap(String string) {
   	this.addPropertyChangeListener(new PropertyChangeListener() {
   		public void propertyChange(PropertyChangeEvent evt) {
   			if(getPreferredSize().width!=0){
				Font xx = getFont();
				int fontHeight = getFontMetrics(xx).getHeight();
				int stringWidth = getFontMetrics(xx).stringWidth(getText());
				int linesCount = (int) Math.floor(stringWidth / getPreferredSize().width);
				linesCount = Math.max(1, linesCount + 2);
				setPreferredSize(new Dimension(100, (fontHeight+2)*linesCount));
   			}
   		}
   	});
   }

   /*public void setWrappedSize() {
       Font xx = getFont();
       int fontHeight = getFontMetrics(xx).getHeight();
       int stringWidth = getFontMetrics(xx).stringWidth(getText());
       int linesCount = (int) Math.floor(stringWidth / getWidth());
       linesCount = Math.max(1, linesCount + 2);
       setPreferredSize(new Dimension(100, (fontHeight+2)*linesCount)); 
   }*/

}