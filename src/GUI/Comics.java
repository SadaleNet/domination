/*Copyright 2012 Wong Cho Ching, all rights reserved.
 * This file is released under BSD 2-clause License.
 * For details, please read LICENSE.txt in this package.*/
package GUI;

import header.G;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.awt.Color;
import javax.swing.JButton;

import UIOverrides.JLabelBackground;

public class Comics extends JFrame {

	private JPanel pane;	
	boolean ending;
	
	public Comics(boolean ending) {
		this.ending = ending;
		Domination.frame.getContentPane().removeAll();
		init();
		Domination.frame.getContentPane().add(pane);
		Domination.frame.getContentPane().validate();
		Domination.frame.getContentPane().repaint();
	}
	private void init(){
		pane = new JPanel();
		pane.setBackground(Color.BLACK);
		pane.setBounds(0, 0, 698, 470);
		pane.setLayout(null);
		
		JButton btnNewButton = new JButton(ending?"Thanks for playing!":"Save the world!");
		//btnNewButton.setLocation(new Point(587,440));
		btnNewButton.setBounds(540, 440, 150, 24);
		btnNewButton.setMaximumSize(new Dimension(32555, 32555));
		btnNewButton.setMinimumSize(new Dimension(0, 0));
		btnNewButton.setPreferredSize(new Dimension(0, 0));

		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				G.comicsDone = 1;
				G.saveData();
				Domination.initialize();
			}
		});
		pane.add(btnNewButton);

		JLabelBackground image = new JLabelBackground(ending?"./images/ending.png":"./images/beginning.png");
		image.setBounds(0, 0, 685, 440);
		image.repaint();
		pane.add(image);
	}
}
