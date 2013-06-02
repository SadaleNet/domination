/*Copyright 2012 Wong Cho Ching, all rights reserved.
 * This file is released under BSD 2-clause License.
 * For details, please read LICENSE.txt in this package.*/

package GUI;

import UIOverrides.*;

import header.BGM;
import header.G;
import header.SFX;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;

import java.awt.Graphics;
import java.awt.Image;

import java.io.File;
import java.io.IOException;


import javax.imageio.ImageIO;
import javax.swing.JLayeredPane;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.AbstractButton;
import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import java.awt.GridLayout;
import java.awt.CardLayout;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Font;import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Insets;
import java.awt.Dimension;

import locale.L;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import java.awt.Color;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;


public class Domination {

	public static JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		new Domination();
	}

	/**
	 * Create the application.
	 */
	public Domination(){
		Domination.frame = new JFrame();
		Domination.frame.setResizable(false);
		Domination.frame.setBounds(new Rectangle(0, 0, 700, 500));
		Domination.frame.getContentPane().setBounds(new Rectangle(0, 0, 700, 500));
		Domination.frame.setTitle("Domination");
		Domination.frame.setBounds(50, 50, 700, 500);
		Domination.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Domination.frame.getContentPane().setLayout(null);
		Domination.frame.setVisible(true);


		G.loadData();

		init(); // My init
		initialize(); //created with Windws Builder

	}

	/**
	 * Initialize the contents of the frame.
	 */
	static boolean first = true;
	static void initialize(){
		BGM.play("./music/menu.wav", true);
		Domination.frame.getContentPane().removeAll();
		createStartScene();
		updateMap();
		Domination.frame.getContentPane().validate();
		Domination.frame.getContentPane().repaint();
		
		first = false;
	}

	public static PlayScene playscene;
	public static Comics comics;
	
	static JSpinner spinner;
	static JLabelBackground mapLabel;
	static JLabel lblFinish;
	static JButton start;
	static String[] stageList = new String[]{"0: Story", "1: Messico", "2: Cannonda", "3: You Like'd States", "4: Ending"};
	
	private static void createStartScene(){
		JPanel startScene = new JPanel();
		startScene.setBackground(Color.BLACK);
		startScene.setBounds(0, 0, 698, 470);
		Domination.frame.getContentPane().add(startScene);
		startScene.setLayout(null);
		
		JLabel lblAlphaReversion = new JLabel("Version 1");
		lblAlphaReversion.setForeground(Color.WHITE);
		lblAlphaReversion.setBounds(620, 449, 59, 15);
		startScene.add(lblAlphaReversion);
		
		start = new JButton("Start!");
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = -1;
				for(int i=0;i<stageList.length;i++){
					if(spinner.getValue().equals(stageList[i])){
						index = i;
						break;
					}
				}
				switch(index){
					case 0: comics = new Comics(false); break;
					case 4: comics = new Comics(true); break;
					default:
						PlayScene.stage = index-1;
						playscene = new PlayScene();
					break;
				}
			}
		});
		start.setFont(new Font("DejaVu Sans", Font.BOLD, 16));
		start.setAlignmentX(0.5f);
		start.setBounds(133, 217, 80, 31);
		startScene.add(start);
		
		JButton button_1 = new JButton("Quit");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		button_1.setAlignmentX(0.5f);
		button_1.setBounds(135, 295, 80, 27);
		startScene.add(button_1);
		
		JLabel lblStage = new JLabel("Stage");
		lblStage.setForeground(Color.WHITE);
		lblStage.setBounds(63, 261, 53, 18);
		startScene.add(lblStage);
		
		JLabelBackground lblDomination = new JLabelBackground("./images/title.png");
		lblDomination.setHorizontalAlignment(SwingConstants.CENTER);
		lblDomination.setFont(new Font("Ubuntu", Font.PLAIN, 90));
		lblDomination.setForeground(Color.WHITE);
		lblDomination.setBounds(6, 19, 686, 138);
		startScene.add(lblDomination);
		
		mapLabel = new JLabelBackground("");
		mapLabel.setOpaque(true);
		mapLabel.setBackground(Color.WHITE);
		mapLabel.setForeground(Color.WHITE);
		mapLabel.setBounds(351, 169, 325, 275);
		startScene.add(mapLabel);
		
		
		JToggleButton btnResetData = new JToggleButton("Reset Data");
		btnResetData.setLocation(28, 419);
		btnResetData.setSize(120, 25);

		startScene.add(btnResetData);
		
		JPopupMenu popupMenu_1 = new JPopupMenu();
		addPopup(btnResetData, popupMenu_1);
		
		
		JMenuItem mntmConfirmReset = new JMenuItem("Confirm reset");
		mntmConfirmReset.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				G.reset();
				initialize();
			}
		});
		mntmConfirmReset.setAlignmentX(Component.CENTER_ALIGNMENT);
		popupMenu_1.add(mntmConfirmReset);
		
		lblFinish = new JLabel();
		lblFinish.setFont(new Font("DejaVu Sans", Font.BOLD, 14));
		lblFinish.setForeground(Color.RED);
		lblFinish.setBounds(73, 188, 187, 18);
		lblFinish.setVisible(false);
		startScene.add(lblFinish);

		spinner = new JSpinner();
		spinner.setModel(new SpinnerListModel(stageList));
		spinner.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				updateMap();
				updateStart();
			}});
		spinner.setValue(stageList[G.comicsDone+G.levelCompleted]);
		spinner.setBounds(105, 257, 167, 26);
		startScene.add(spinner);

	}
	private void init() {
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		SFX.init();
	}
	private static void updateMap(){
		if(((String)spinner.getValue()).equals(stageList[0]))
			mapLabel.setImage("./images/map/0.png");
		else if(spinner.getValue().equals(stageList[1]))
			mapLabel.setImage("./images/map/1.png");
		else if(((String)spinner.getValue()).equals(stageList[2]))
			mapLabel.setImage("./images/map/2.png");
		else if(((String)spinner.getValue()).equals(stageList[3]))
			mapLabel.setImage("./images/map/3.png");
		else if(((String)spinner.getValue()).equals(stageList[4]))
			mapLabel.setImage("./images/map/0.png");
		mapLabel.repaint();
	}
	private static void updateStart(){
		int cap = G.comicsDone+G.levelCompleted;
		int failIndex = -1;
		for(int i=1;i<stageList.length;i++){
			if(spinner.getValue().equals(stageList[i])){
				if(cap<i){
					failIndex = i;
					break;
				}
			}
		}
		if(failIndex>-1){
			lblFinish.setText("Complete Stage "+(failIndex-1)+" First!");
			lblFinish.setVisible(true);
			start.setEnabled(false);
		}else{
			lblFinish.setVisible(false);
			start.setEnabled(true);
		}
		Domination.frame.repaint();
	}
	private static void addPopup(final Component component, final JPopupMenu popup) {
		((JToggleButton) component).addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(((JToggleButton) component).isSelected()){
						popup.show(((JToggleButton) component), (component.getPreferredSize().width-popup.getPreferredSize().width)/2, -popup.getPreferredSize().height);
				}
			}
		});
		popup.addPopupMenuListener(new PopupMenuListener() {
			@Override public void popupMenuCanceled(PopupMenuEvent e) {
			}
			@Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				((AbstractButton) component).setSelected(false);
			}
			@Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
		});
	}
}
