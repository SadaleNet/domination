/*Copyright 2012 Wong Cho Ching, all rights reserved.
 * This file is released under BSD 2-clause License.
 * For details, please read LICENSE.txt in this package.*/

package UIOverrides;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import GUI.PlayScene;

public class BuildListCellRender extends JLabel implements ListCellRenderer {
    public BuildListCellRender() {
        setOpaque(true);
    }

    public Component getListCellRendererComponent(final JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
    	if(((String) value).substring(0,1).equals("."))
    		setIcon(new ImageIcon(((String) value)));
    	else
    		setText((String) value);
    	
        Color background;
        Color foreground;

        // check if this cell represents the current DnD drop location
        JList.DropLocation dropLocation = list.getDropLocation();
        if (dropLocation != null
                && !dropLocation.isInsert()
                && dropLocation.getIndex() == index) {

            background = Color.BLUE;
            foreground = Color.WHITE;

        // check if this cell is selected
        } else if (isSelected) {
            background = Color.BLUE;
            foreground = Color.WHITE;

        // unselected, and not the DnD drop location
        } else {
            background = Color.WHITE;
            foreground = Color.BLACK;
        };

        setBackground(background);
        setForeground(foreground);

        return this;
    }
}