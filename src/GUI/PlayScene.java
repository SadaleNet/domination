/*Copyright 2012 Wong Cho Ching, all rights reserved.
 * This file is released under BSD 2-clause License.
 * For details, please read LICENSE.txt in this package.*/

package GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;

import enums.E;

import locale.L;
import playSceneUI.PlaySceneBoard;
import playSceneUI.PlaySceneDataInit;
import playSceneUI.StructureBuilt;
import playSceneUI.StructureEntity;
import UIOverrides.BuildListCellRender;
import UIOverrides.JLabelBackground;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;


public class PlayScene {
	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PlayScene window = new PlayScene();
					PlayScene.mainFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/
	/*private void init() {
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
	}*/
	PlaySceneDataInit playSceneDataInit;
	public static PlaySceneBoard playSceneBoard;
	public static int stage;
	public PlayScene(){
		if(keyListener!=null)
			KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keyListener);
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyListener);
		Domination.frame.getContentPane().removeAll();
		//init();
		initialize();
		playSceneDataInit = new PlaySceneDataInit();
		playSceneBoard = new PlaySceneBoard();
		Domination.frame.getContentPane().add(playScene);
		Domination.frame.getContentPane().validate();
		Domination.frame.getContentPane().repaint();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private final JPanel topPanel = new JPanel();
	public static JList structureList;
	public static JLabel lblStructureName;
	public static JLabel lblStructureDescription;
	public static JTable structurePropTable;
	public static JButton btnStructureUpgrade;
	public static JToggleButton tglbtnStrucutreSell;
	public static JPanel result;
		public static JLabelBackground victoryLabel;
		public static JLabel lblUnlocked;
		public static JLabel lblCongrats;


	public static Box StructureUI;
		public static JProgressBar buildingBar;
		public static JPanel structureDetails;

	public static Box upgrade_sell;
		
	public static JToggleButton tglbtnPause;
	
	public static JPanel playScene;
	public static JPanel resourcePanel;
	public static JPanel rightPanel2;
	

	private void initialize(){
		
		playScene = new JPanel();
		playScene.setBounds(0, 0, 698, 470);
		playScene.setLayout(null);
		
		JPanel rightPanel = new JPanel();
		rightPanel.setBackground(new Color(250,245,240));
		rightPanel.setBounds(420, 35, 279, 435);
		playScene.add(rightPanel);
		rightPanel.setLayout(null);
		
		Box horizontalBox = Box.createHorizontalBox();
		horizontalBox.setBounds(183, 394, 89, 35);
		rightPanel.add(horizontalBox);
		
		tglbtnPause = new JToggleButton();
		tglbtnPause.setMaximumSize(new Dimension(28, 28));
		tglbtnPause.setMinimumSize(new Dimension(28, 28));
		try {tglbtnPause.setIcon(new ImageIcon(new File("./images/buttons/pause.png").toURI().toURL())); } catch (MalformedURLException e1) {e1.printStackTrace();}
		horizontalBox.add(tglbtnPause);
		
		JToggleButton tglbtnFastForward = new JToggleButton();
		tglbtnFastForward.setVisible(false);
		tglbtnFastForward.setIcon(null);
		tglbtnFastForward.setMinimumSize(new Dimension(28, 28));
		tglbtnFastForward.setMaximumSize(new Dimension(28, 28));
		horizontalBox.add(tglbtnFastForward);
		
		JToggleButton tglbtnMusicVolume = new JToggleButton();
		tglbtnMusicVolume.setVisible(false);
		tglbtnMusicVolume.setIcon(null);
		tglbtnMusicVolume.setMinimumSize(new Dimension(28, 28));
		tglbtnMusicVolume.setMaximumSize(new Dimension(28, 28));
		horizontalBox.add(tglbtnMusicVolume);
		
		JPopupMenu popupMenu_1 = new JPopupMenu();
		addPopup(tglbtnMusicVolume, popupMenu_1);
		
		JLabel labelPlus = new JLabel("+");
		labelPlus.setAlignmentX(Component.CENTER_ALIGNMENT);
		popupMenu_1.add(labelPlus);
		
		JSlider sliderMusicVolume = new JSlider();
		sliderMusicVolume.setOrientation(SwingConstants.VERTICAL);
		sliderMusicVolume.setPreferredSize(new Dimension(21, 150));
		popupMenu_1.add(sliderMusicVolume);
		
		JLabel labelMinus = new JLabel("-");
		labelMinus.setAlignmentX(Component.CENTER_ALIGNMENT);
		popupMenu_1.add(labelMinus);
		
		JToggleButton tglbtnSfxvolume = new JToggleButton();
		tglbtnSfxvolume.setVisible(false);
		tglbtnSfxvolume.setMinimumSize(new Dimension(28, 28));
		tglbtnSfxvolume.setMaximumSize(new Dimension(28, 28));
		horizontalBox.add(tglbtnSfxvolume);
		
		JPopupMenu popupMenu_3 = new JPopupMenu();
		addPopup(tglbtnSfxvolume, popupMenu_3);
		
		JLabel label = new JLabel("+");
		label.setAlignmentX(0.5f);
		popupMenu_3.add(label);
		
		JSlider sliderSfxVolume = new JSlider();
		sliderSfxVolume.setPreferredSize(new Dimension(21, 150));
		sliderSfxVolume.setOrientation(SwingConstants.VERTICAL);
		popupMenu_3.add(sliderSfxVolume);
		
		JLabel label_1 = new JLabel("-");
		label_1.setAlignmentX(0.5f);
		popupMenu_3.add(label_1);
		
		JToggleButton tglbtnRestart = new JToggleButton();
		try {tglbtnRestart.setIcon(new ImageIcon(new File("./images/buttons/restart.png").toURI().toURL())); } catch (MalformedURLException e1) {e1.printStackTrace();}
		tglbtnRestart.setMinimumSize(new Dimension(28, 28));
		tglbtnRestart.setMaximumSize(new Dimension(28, 28));
		horizontalBox.add(tglbtnRestart);

		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(tglbtnRestart, popupMenu);
		
		JMenuItem mntmConfirmRestart = new JMenuItem(L.s("Confirm restart"));
		mntmConfirmRestart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Domination.playscene = new PlayScene();
			}
		});
		popupMenu.add(mntmConfirmRestart);
		
		JToggleButton tglbtnQuit = new JToggleButton();
		try {tglbtnQuit.setIcon(new ImageIcon(new File("./images/buttons/quit.png").toURI().toURL())); } catch (MalformedURLException e1) {e1.printStackTrace();}
		tglbtnQuit.setMinimumSize(new Dimension(28, 28));
		tglbtnQuit.setMaximumSize(new Dimension(28, 28));
		horizontalBox.add(tglbtnQuit);
		
		JPopupMenu popupMenu_4 = new JPopupMenu();
		addPopup(tglbtnQuit, popupMenu_4);
		
		JMenuItem mntmSaveAndQuit = new JMenuItem(L.s("Confirm quit"));
		mntmSaveAndQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Domination.initialize();
			}
		});
		popupMenu_4.add(mntmSaveAndQuit);
		
		Box verticalBox = Box.createVerticalBox();
		verticalBox.setBounds(0, 6, 279, 390);
		rightPanel.add(verticalBox);
		
		JScrollPane scrollPane = new JScrollPane();
		
		scrollPane.setMaximumSize(new Dimension(279, 390));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		verticalBox.add(scrollPane);
		
		scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener(){
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e){
				structureDetails.validate();
				structureDetails.repaint();
			}
		});

		rightPanel2 = new JPanel();
		rightPanel2.setSize(new Dimension(252, 440));
		rightPanel2.setMaximumSize(new Dimension(252, 440));
		rightPanel2.setBounds(new Rectangle(0, 6, 252, 410));
		scrollPane.setViewportView(rightPanel2);
		rightPanel2.setLayout(new BoxLayout(rightPanel2, BoxLayout.Y_AXIS));
			
			StructureUI = Box.createVerticalBox();
			rightPanel2.add(StructureUI);
			
			JPanel panel = new JPanel();
			rightPanel2.add(panel);
			panel.setMinimumSize(new Dimension(235, 100));
			panel.setMaximumSize(new Dimension(235, 100));
			panel.setBackground(Color.BLACK);
			panel.setPreferredSize(new Dimension(235, 100));
			structureList = new JList();
			MouseAdapter ma = new MouseAdapter() {
				int oldIndex = 0;
				@Override
				public void mousePressed(MouseEvent e){
					if(PlaySceneBoard.states==PlaySceneBoard.NORMAL){
						int index = structureList.getSelectedIndex();
						oldIndex = index;
						if(index==0){
							PlaySceneBoard.clearUI();
							structureList.clearSelection();
						}else if(index!=-1){
							PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer] = -1;
							PlaySceneBoard.structureEntity[PlaySceneBoard.structureEntityMap[index-1]].updateUI(0, StructureEntity.PURCHASING, PlaySceneBoard.currentPlayer);
						}
					}
				}
				@Override
				public void mouseDragged(MouseEvent e){
					if(oldIndex>0)
						structureList.setSelectedIndex(oldIndex);
					else
						structureList.clearSelection();
				}
			};
			structureList.addMouseListener(ma);
			structureList.addMouseMotionListener(ma);
			panel.add(structureList);
			structureList.setPreferredSize(new Dimension(225, 90));
			structureList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			structureList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			structureList.setCellRenderer(new BuildListCellRender());
			structureList.setVisibleRowCount(-1);
			
			JLabel lblHoldToBuild = new JLabel("Hold <Shift> to build multiple");
			lblHoldToBuild.setFont(new Font("DejaVu Sans", Font.BOLD, 12));
			lblHoldToBuild.setForeground(Color.BLUE);
			lblHoldToBuild.setAlignmentX(Component.CENTER_ALIGNMENT);
			rightPanel2.add(lblHoldToBuild);
	
			Component verticalStrut = Box.createVerticalStrut(20);
			rightPanel2.add(verticalStrut);
	
			lblStructureName = new JLabel(L.s("StructureName"));
			rightPanel2.add(lblStructureName);
			lblStructureName.setHorizontalAlignment(SwingConstants.CENTER);
			lblStructureName.setAlignmentX(Component.CENTER_ALIGNMENT);
			lblStructureName.setFont(new Font("DejaVu Sans", Font.BOLD, 16));
	
			lblStructureDescription = new JLabel("<html>"+L.s("StructureDescription")+"</html>");
			rightPanel2.add(lblStructureDescription);
			lblStructureDescription.setAlignmentX(Component.CENTER_ALIGNMENT);
	
		structurePropTable = new JTable();
		rightPanel2.add(structurePropTable.getTableHeader());
		rightPanel2.add(structurePropTable);
		structurePropTable.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null, null},
			},
			new String[] {
					L.s("Properties"), L.s("Cur."), L.s("Next")
			}
		));
		structurePropTable.setFont(new Font("Arial", Font.PLAIN, 11));
		structurePropTable.setEnabled(false);
		structurePropTable.setEnabled(false);
				
			upgrade_sell = Box.createHorizontalBox();
			rightPanel2.add(upgrade_sell);
			
			btnStructureUpgrade = new JButton(L.s("Upgrade(U)"));
			btnStructureUpgrade.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if(btnStructureUpgrade.isEnabled())
						PlaySceneBoard.processUpgrade(PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer]);
				}
			});
			upgrade_sell.add(btnStructureUpgrade);
			btnStructureUpgrade.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			tglbtnStrucutreSell = new JToggleButton(L.s("Sell(S)"));
			tglbtnStrucutreSell.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent e) {
					int index = PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer];
					if(index>-1){
						if(tglbtnStrucutreSell.isSelected())
							PlaySceneBoard.structureEntity[PlaySceneBoard.structureEntityMap[PlaySceneBoard.structureBuilt[index].type]].updateUI(
									PlaySceneBoard.structureBuilt[index].currentLevel, StructureEntity.SELLING, PlaySceneBoard.currentPlayer);
						else
							PlaySceneBoard.structureEntity[PlaySceneBoard.structureEntityMap[PlaySceneBoard.structureBuilt[index].type]].updateUI(
									PlaySceneBoard.structureBuilt[index].currentLevel, StructureEntity.NORMAL, PlaySceneBoard.currentPlayer);
					}
				}
			});
			upgrade_sell.add(tglbtnStrucutreSell);
			tglbtnStrucutreSell.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			JPopupMenu popupMenu_2 = new JPopupMenu();
			addPopup(tglbtnStrucutreSell, popupMenu_2);
			
			JMenuItem mntmConfirmSell = new JMenuItem("Confirm sell");
			mntmConfirmSell.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					PlaySceneBoard.processSell(PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer]);
				}
			});
			popupMenu_2.add(mntmConfirmSell);
	
			structureDetails = new JPanel();
			rightPanel2.add(structureDetails);
			structureDetails.setLayout(new BoxLayout(structureDetails, BoxLayout.Y_AXIS));
			

		buildingBar = new JProgressBar();
		topPanel.setBounds(0, 0, 699, 36);
		playScene.add(topPanel);
		topPanel.setLayout(null);
		
		resourcePanel = new JPanel();
		FlowLayout fl_resourcePanel = (FlowLayout) resourcePanel.getLayout();
		fl_resourcePanel.setVgap(2);
		fl_resourcePanel.setAlignment(FlowLayout.LEFT);
		resourcePanel.setBackground(new Color(245, 240, 250));
		resourcePanel.setBounds(0, 0, 699, 36);
		topPanel.add(resourcePanel);
		
		
		
		PlaySceneBoard.clearUI();
		
		result = new JPanel();
		result.setVisible(false);
		result.setBackground(new Color(255, 255, 255));
		result.setBounds(14, 95, 398, 305);
		playScene.add(result);
		result.setLayout(null);

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.BLACK);
		panel_1.setBounds(5, 5, 388, 295);
		result.add(panel_1);
		panel_1.setLayout(null);
		
		victoryLabel = new JLabelBackground("");
		victoryLabel.setForeground(Color.WHITE);
		victoryLabel.setBounds(14, 20, 360, 120);
		panel_1.add(victoryLabel);
		
		
		lblUnlocked = new JLabel("Unlocked");
		lblUnlocked.setForeground(Color.ORANGE);
		lblUnlocked.setFont(new Font("DejaVu Sans", Font.PLAIN, 24));
		lblUnlocked.setBounds(10, 140, 371, 93);
		panel_1.add(lblUnlocked);
		
		lblCongrats = new JLabel("Congrats!");
		lblCongrats.setForeground(Color.YELLOW);
		lblCongrats.setBounds(20, 260, 194, 29);
		panel_1.add(lblCongrats);

		JButton btnNewButton = new JButton("Proceed");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Domination.initialize();
			}
		});
		btnNewButton.setFont(new Font("Ubuntu", Font.BOLD, 32));
		btnNewButton.setBounds(226, 240, 154, 49);
		panel_1.add(btnNewButton);
		
	}
	private static void addPopup(final Component component, final JPopupMenu popup) {
		/*component.addMouseListener(new MouseAdapter() {
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton()==MouseEvent.BUTTON1) {
					showMenu(e);
				}
			}
		});*/
		((JToggleButton) component).addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(((JToggleButton) component).isSelected()){
					if(component==tglbtnStrucutreSell)
						popup.show(((JToggleButton) component), (component.getPreferredSize().width-popup.getPreferredSize().width)/2, popup.getPreferredSize().height);
					else
						popup.show(((JToggleButton) component), (component.getPreferredSize().width-popup.getPreferredSize().width)/2, -popup.getPreferredSize().height);
				}
			}
		});
		popup.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuCanceled(PopupMenuEvent e) {
			}
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				((AbstractButton) component).setSelected(false);
			}
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			}
		});
	}

	static KeyEventDispatcher keyListener = new KeyEventDispatcher(){
		@Override
		public boolean dispatchKeyEvent(java.awt.event.KeyEvent e) {
			if(e.getID() == java.awt.event.KeyEvent.KEY_PRESSED) {
				if(e.getKeyCode()==java.awt.event.KeyEvent.VK_SHIFT)
					PlaySceneBoard.shiftHeld = true;
			}else if(e.getID() == java.awt.event.KeyEvent.KEY_RELEASED){
				StructureBuilt structure = null;
				StructureEntity entity = null;
				if(PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer]>-1){
					structure = PlaySceneBoard.structureBuilt[PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer]];
					entity = PlaySceneBoard.structureEntity[structure.type];
				}
				switch(e.getKeyCode()){
					case java.awt.event.KeyEvent.VK_SHIFT:
						PlaySceneBoard.shiftHeld = false;
					break;
					case java.awt.event.KeyEvent.VK_ESCAPE:
						PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer] = -1;
						PlayScene.structureList.clearSelection();
						PlaySceneBoard.clearUI();
					break;
					case java.awt.event.KeyEvent.VK_U:
						if(structure!=null){
							if(!structure.upgrading&&
									structure.currentLevel<entity.maxLevel)
							PlaySceneBoard.processUpgrade(PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer]);
						}
					break;
					case java.awt.event.KeyEvent.VK_S:
						if(structure!=null){
							if(!structure.upgrading&&structure.type!=PlaySceneDataInit.BASE)
								PlaySceneBoard.processSell(PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer]);
						}
					break;
					case java.awt.event.KeyEvent.VK_T:
						if(structure!=null){
							if(!structure.upgrading&&
									(entity.category|E.OFFENSIVE)!=0&&
									StructureEntity.selectedUnitEntity>-1){
								entity.buyUFO(structure, StructureEntity.selectedUnitEntity, PlaySceneBoard.currentPlayer, StructureEntity.NORMAL);
							}
						}
					break;
					case java.awt.event.KeyEvent.VK_A:
						if(structure!=null){
							if((entity.category|E.OFFENSIVE)!=0)
								entity.attack(PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer]);
						}
					break;
					case java.awt.event.KeyEvent.VK_R:
						if(structure!=null){
							if((entity.category|E.OFFENSIVE)!=0)
								entity.retreat(PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer]);
						}
					break;
					case java.awt.event.KeyEvent.VK_TAB:
						PlaySceneBoard.states = PlaySceneBoard.NORMAL;
						PlaySceneBoard.currentPlayer = 1-PlaySceneBoard.currentPlayer;
						/*if(PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer]>-1)
							PlaySceneBoard.structureEntity[PlaySceneBoard.structureBuilt[PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer]].type].updateUI(PlaySceneBoard.structureBuilt[PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer]].currentLevel, StructureEntity.NORMAL, PlaySceneBoard.currentPlayer);
						else
							PlaySceneBoard.clearUI();*/
						PlaySceneBoard.clearUI();
					break;
					case java.awt.event.KeyEvent.VK_DELETE:
						if(structure!=null)
							structure.destruct();
					break;
					case java.awt.event.KeyEvent.VK_INSERT:
						PlaySceneBoard.resourceAmount[PlaySceneBoard.currentPlayer][PlaySceneBoard.resourceEnumToId(PlaySceneDataInit.GOLD)] = 10000000;
						PlaySceneBoard.resourceAmount[PlaySceneBoard.currentPlayer][PlaySceneBoard.resourceEnumToId(PlaySceneDataInit.WATER)] = 10000000;
						PlaySceneBoard.resourceAmount[PlaySceneBoard.currentPlayer][PlaySceneBoard.resourceEnumToId(PlaySceneDataInit.POPULATION)] = 10000000;
						PlaySceneBoard.resourceAmount[PlaySceneBoard.currentPlayer][PlaySceneBoard.resourceEnumToId(PlaySceneDataInit.POPULATION_AVAILABLE)] = 10000000;
						PlaySceneBoard.resourceCap[PlaySceneBoard.currentPlayer][PlaySceneBoard.resourceEnumToId(PlaySceneDataInit.GOLD)] = 10000000;
						PlaySceneBoard.resourceCap[PlaySceneBoard.currentPlayer][PlaySceneBoard.resourceEnumToId(PlaySceneDataInit.WATER)] = 10000000;
						PlaySceneBoard.resourceCap[PlaySceneBoard.currentPlayer][PlaySceneBoard.resourceEnumToId(PlaySceneDataInit.POPULATION)] = 10000000;
						PlaySceneBoard.resourceCap[PlaySceneBoard.currentPlayer][PlaySceneBoard.resourceEnumToId(PlaySceneDataInit.POPULATION_AVAILABLE)] = 10000000;
					break;
					case java.awt.event.KeyEvent.VK_PAGE_DOWN:
						PlaySceneBoard.ai.startAttackSecond = 0;
					break;
				}
			}
			return false;
		}
	};
}