/*Copyright 2012 Wong Cho Ching, all rights reserved.
 * This file is released under BSD 2-clause License.
 * For details, please read LICENSE.txt in this package.*/

package playSceneUI;

import header.G;

import java.awt.Component;

import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.swing.AbstractListModel;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import locale.L;

import enums.E;

import UIOverrides.BuildListCellRender;


import GUI.PlayScene;

public class StructureEntity{
	public String name;
	public String iconPath;
	public String GLIconPath;
	public int width;
	public int height;
	public int openableLevel;
	public int category;
	public int updateInterval; //for generator like water generator(a.k.a. well)

	public int unlockReq;

	public int[] propertiesEnum; //[Property]
	private String[] propertiesName; //[Property]
	public int[] propertiesType; //[Property]
	public int[] propertiesTypeOnDestruct; //[Property]
	public int[] propertiesParameter; //[Property]
	public int[][] value; //[Property][Level]
	public boolean[] isVisible; //[Property]
	public boolean[] isCosts; //[Property]
	private String[] format; //[Property]
	private String[] description; //[Level]
	public int properties_n=0;
	private int isVisible_n=0;
	public int maxLevel=0;
	private int descriptionColumn = 0;
	
	public StructureEntity(String name, String iconPath, String GLIconPath, int openableLevel, int width, int height, int category, int updateInterval, int unlockReq){
		this.name = name;
		this.iconPath = iconPath;
		this.GLIconPath = GLIconPath;
		this.openableLevel = openableLevel-1;
		this.width = width;
		this.height = height;
		this.category = category;
		this.updateInterval = updateInterval;
		this.unlockReq = unlockReq;
	}
	BufferedImage image;
	int[] texture = new int[2];
	public void loadTexture(GL2 gl){
		try{
			image = ImageIO.read(new File(GLIconPath));
		}catch (IOException e){
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
	/* format:
	 * isVisible row
	 * isCosts row
	 * name row (use this row to detect data type. When needed, convert the data from string to int.) Tips: Integer.parseInt( string )
	 * data for level 1
	 * data for level 2
	 * ...
	 */
	public void setPropertiesByTable(int[] propertiesEnumArray, String[] propertiesNameArray, int[] propertiesTypeArray, int[] propertiesParameterArray, int[] propertiesTypeOnDestructArray, boolean[] isVisibleArray, String[] formatArray, String[][] table){
		properties_n = propertiesEnumArray.length;
		maxLevel = table.length;
		propertiesEnum = new int[properties_n+1];
		propertiesName = new String[properties_n];
		propertiesType = new int[properties_n+1];
		propertiesParameter = new int[properties_n+1];
		propertiesTypeOnDestruct = new int[properties_n];
		isVisible = new boolean[properties_n];
		isCosts = new boolean[properties_n];
		format = new String[properties_n];
		value = new int[properties_n+1][];
		description = new String[maxLevel];
		for(int i=0;i<properties_n;i++){
			isVisible[i] = isVisibleArray[i];
			isCosts[i] = (propertiesTypeArray[i]&PlaySceneDataInit.COST)!=0;
			propertiesEnum[i] = propertiesEnumArray[i];
			propertiesName[i] = propertiesNameArray[i];
			propertiesType[i] = propertiesTypeArray[i];
			propertiesParameter[i] = propertiesParameterArray[i];
			propertiesTypeOnDestruct[i] = propertiesTypeOnDestructArray[i];
			format[i] = formatArray[i];
			if(isVisible[i])
				isVisible_n++;
			if(propertiesEnum[i]==PlaySceneDataInit.DESCRIPTION)
				descriptionColumn = i;
		}
		value[properties_n] = new int[1];
		for(int i=0;i<properties_n;i++){
			value[i] = new int[maxLevel];
			for(int j=0;j<maxLevel;j++){
				if(i==descriptionColumn)
					description[j] = table[j][i];
				else{
					value[i][j] = Integer.parseInt(table[j][i]);
				}
			}
		}
		maxLevel--; //Since level are 0-based, maxLevel should be deducted by one. However, we need the value before -1 for array allocation. Therefore, we put -1 here, instead of when initializing the variable. Please notice that maxLevel is level_n-1.
	}

	public String[] memoryName;
	public int[] memorySize;
	public int memory_n;
	public void setCustomMemory(String[] name, int[] size){
		memory_n = name.length;
		memoryName = new String[memory_n];
		memorySize = new int[memory_n];
		for(int i=0;i<memory_n;i++){
			memoryName[i] = name[i];
			memorySize[i] = size[i];
		}
	}
	public int getMemoryIdByName(String string){
		for(int i=0;i<memory_n;i++){
			if(memoryName[i].equals(string))
				return i;
		}
		return -1;
	}
	private int UI_n;
	private String[] UIentity;
	private int[] UIentityType;
	private int[] UIentityArg0;
	public void setStructureUI(String[] entity, int[] entityType, int[] arg0){
		UI_n = entity.length;
		UIentity = new String[UI_n];
		UIentityType = new int[UI_n];
		UIentityArg0 = new int[UI_n];
		for(int i=0;i<UI_n;i++){
			UIentity[i] = entity[i];
			UIentityType[i] = entityType[i];
			UIentityArg0[i] = arg0[i];
		}
	}
	public void insertStructureUI(String[] entity, int[] entityType, int[] arg0){
		//Damn Java garbage collector! I cannot pre-allocate more memory for array. Instead, I've to reallocate and clone the array inorder to expand the array.
		String[] oldUIentity = UIentity;
		UIentity = new String[UI_n+entity.length];
		int[] oldUIentityType = UIentityType;
		UIentityType = new int[UI_n+entity.length];
		int[] oldUIentityArg0 = UIentityArg0;
		UIentityArg0 = new int[UI_n+entity.length];
		for(int i=0;i<UI_n;i++){
			UIentity[i] = oldUIentity[i];
			UIentityType[i] = oldUIentityType[i];
			UIentityArg0[i] = oldUIentityArg0[i];
		}
		for(int i=0;i<entity.length;i++){
			UIentity[UI_n+i] = entity[i];
			UIentityType[UI_n+i] = entityType[i];
			UIentityArg0[UI_n+i] = arg0[i];
		}
		UI_n += entity.length;
	}
	public int getPropertyIntCapByResourceID(int resId, int currentLevel){ //only properties inside the variable "value" can be accessed in this way.
		int Enum = PlaySceneBoard.resourceEnumMap[resId];
		for(int i=0;i<properties_n;i++){
			if(propertiesEnum[i]==Enum&&(propertiesType[i]&PlaySceneDataInit.ISCAP)!=0){
				return value[i][currentLevel];
			}
		}
		return -1;
	}
	/*public int getPropertyIDByResourceID(int ID){ //only properties inside the variable "value" can be accessed in this way.
		int Enum = PlaySceneBoard.resourceEnumMap[ID];
		int hold = -1;
		for(int i=0;i<properties_n;i++){
			if(propertiesEnum[i]==Enum){
				if((propertiesType[i]&PlaySceneDataInit.ISCAP)!=0){
					hold = i;
				}else{
					return i;
				}
			}
		}
		return hold;
	}*/
	public int getPropertyIDByResourceEnum(int Enum){ //only properties inside the variable "value" can be accessed in this way.
		int hold = -1;
		for(int i=0;i<properties_n;i++){
			if(propertiesEnum[i]==Enum){
				if((propertiesType[i]&PlaySceneDataInit.ISCAP)!=0){
					hold = i;
				}else{
					return i;
				}
			}
		}
		return hold;
	}
	public int getPropertyIntCapIDByResourceEnum(int Enum){ //only properties inside the variable "value" can be accessed in this way.
		for(int i=0;i<properties_n;i++){
			if(propertiesEnum[i]==Enum&&(propertiesType[i]&PlaySceneDataInit.ISCAP)!=0)
				return i;
		}
		return -1;
	}
	
	/*public boolean propertyTypeExist(int type){
		for(int i=0;i<properties_n;i++){
			if((propertiesType[i]|type))
				return true;
		}
		return false;
	}*/
	public static int selectedUnitEntity = -1;
	
	JButton commandButton = new JButton(); // for UFOList
	ActionListener commandActionListener;
	JButton pathButton = new JButton(); // for creating path
	ActionListener pathActionListener;

	MouseAdapter mouseListener;
	
	public final static byte NORMAL = 0;
	public final static byte PURCHASING = 1;
	public final static byte SELLING = 2;

	private int currentLevel;
	
	private void addCommandListener(){
		if(commandActionListener!=null)
			commandButton.removeActionListener(commandActionListener);
		final StructureBuilt structureBuilt = PlaySceneBoard.structureBuilt[PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer]];
		commandActionListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if(PlaySceneBoard.states==PlaySceneBoard.NORMAL){
					switch(structureBuilt.memory[getMemoryIdByName("attacking")][0]){
						case E.IDLE:
							attack(PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer]);
						break;
						case E.ATTACKING:
							retreat(PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer]);
							commandButton.setEnabled(false);
						break;
						/*case E.RETREATING:
							commandButton.setText("retreating... :P");
							structureBuilt.memory[getMemoryIdByName("attacking")][0] = E.RETREATING;
						break;*/
					}
					//TODO: WORKING ON HERE: WHAT NOW?
				}
			}
		};
		commandButton.addActionListener(commandActionListener);
	}
	
	public void createPath(StructureBuilt b){
		if(b.pathPointer==null){
			PlaySceneBoard.path_n++;
			b.pathPointer = new Path(1000, b, PlaySceneBoard.path_n); //TODO Smart buffer.
		}else{
			int oldId = b.pathPointer.id;
			b.pathPointer = new Path(1000, b, oldId); //TODO Smart buffer.
		}
		b.pathPointer.addNode(
			b.pos.x*PlaySceneBoard.gridSize+PlaySceneBoard.gridSize*width/2,
			b.pos.y*PlaySceneBoard.gridSize+PlaySceneBoard.gridSize*height/2);
	}
	
	private void addPathListener(){
		if(pathActionListener!=null)
			pathButton.removeActionListener(pathActionListener);
		final StructureBuilt structureBuilt = PlaySceneBoard.structureBuilt[PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer]];
		pathActionListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(PlaySceneBoard.states==PlaySceneBoard.NORMAL){
					createPath(structureBuilt);
					PlaySceneBoard.states = PlaySceneBoard.SETTINGPATH;
					pathButton.setText(L.s("Finish!"));
					if(structureBuilt.pathPointer.n>1){
						pathButton.setEnabled(true);
					}else{
						pathButton.setEnabled(false);
						pathButton.removeActionListener(pathActionListener);
					}
				}else if(PlaySceneBoard.states==PlaySceneBoard.SETTINGPATH){
					PlaySceneBoard.states = PlaySceneBoard.NORMAL;
					pathButton.setText(L.s("Create Path!"));
				}
			}
		};
		pathButton.addActionListener(pathActionListener);
	}
	
	Box unitStates;
	JList UFOList;
	
	public void updateUI(final int currentLevel, final byte status, int owner){ //if currentLevel = -1, then selling.
		try{
			if(owner!=PlaySceneBoard.currentPlayer)
				return;
			this.currentLevel = currentLevel;
			PlaySceneBoard.clearUI();
			Object[][] cell;
			int pos = 0;
			cell = new Object[isVisible_n][3];
			for(int i=0;i<properties_n;i++){
				if(isVisible[i]){
					cell[pos][0] = propertiesName[i];
					if(status==PURCHASING){
						cell[pos][1] = "-";
						cell[pos][2] = String.format(format[i], value[i][currentLevel]);
					}else{
						if(status==SELLING&&isCosts[i])
							cell[pos][1] = String.format(format[i], (int)(value[i][currentLevel]*PlaySceneBoard.sellValue));
						else
							cell[pos][1] = String.format(format[i], value[i][currentLevel]);
						if(currentLevel+1<=maxLevel)
							cell[pos][2] = String.format(format[i], value[i][currentLevel+1]);
						else
							cell[pos][2] = "-";
					}
					pos++;
				}
			}
			PlayScene.structurePropTable.getTableHeader().setVisible(true);
			PlayScene.structurePropTable.setVisible(true);
			PlayScene.structurePropTable.setModel(new DefaultTableModel(cell, new String[]{L.s("Properties"), L.s("Cur."), L.s("Next")}));
			PlayScene.structurePropTable.getColumnModel().getColumn(0).setPreferredWidth(125);
			PlayScene.structurePropTable.getColumnModel().getColumn(0).setMinWidth(125);
			PlayScene.structurePropTable.getColumnModel().getColumn(0).setMaxWidth(125);
			PlayScene.structurePropTable.getColumnModel().getColumn(1).setPreferredWidth(50);
			PlayScene.structurePropTable.getColumnModel().getColumn(1).setMinWidth(50);
			PlayScene.structurePropTable.getColumnModel().getColumn(1).setMaxWidth(50);
			PlayScene.structurePropTable.getColumnModel().getColumn(2).setPreferredWidth(50);
			PlayScene.structurePropTable.getColumnModel().getColumn(2).setMinWidth(50);
			PlayScene.structurePropTable.getColumnModel().getColumn(2).setMaxWidth(50);
	
			boolean locked = false;
			if(status==PURCHASING){
				if(G.levelCompleted<unlockReq)
					locked = true;
				
			}
			if(!locked){
				PlayScene.lblStructureName.setText("<html><center>"+name+"</center></html>");
				PlayScene.lblStructureName.setVisible(true);
				if(currentLevel<maxLevel||this==PlaySceneBoard.structureEntity[PlaySceneDataInit.BASE])
					PlayScene.lblStructureDescription.setText("<html>"+description[currentLevel>=maxLevel||status==PURCHASING?currentLevel:currentLevel+1]+"</html>");
				else
					PlayScene.lblStructureDescription.setText("<html>Upgrade maxed out! =D</html>");
				PlayScene.lblStructureDescription.setVisible(true);
				PlayScene.btnStructureUpgrade.setVisible(status==PURCHASING||currentLevel>=maxLevel?false:true);
				PlayScene.tglbtnStrucutreSell.setVisible(status==PURCHASING?false:true);
			}else{
				PlayScene.structurePropTable.getTableHeader().setVisible(false);
				PlayScene.structurePropTable.setVisible(false);
				PlayScene.lblStructureName.setVisible(false);
				PlayScene.lblStructureDescription.setText("<html><font color='#FF0000'><center><b>LOCKED</b> until completion of stage "+unlockReq+"</center></font></html>");
				PlayScene.lblStructureDescription.setVisible(true);
				PlayScene.lblStructureName.setVisible(false);
				PlayScene.btnStructureUpgrade.setVisible(false);
				PlayScene.tglbtnStrucutreSell.setVisible(false);
			}
			if(PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer]>-1){
				if(owner!=PlaySceneBoard.currentPlayer)
					return;
				final StructureBuilt structureBuilt = PlaySceneBoard.structureBuilt[PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer]];
				if(structureBuilt.type==PlaySceneDataInit.BASE)
					PlayScene.upgrade_sell.setVisible(false);
				else
					PlayScene.upgrade_sell.setVisible(true);
				if(!structureBuilt.upgrading){
					PlayScene.structureDetails.repaint();
					for(int i=0;i<UI_n;i++){
						/*private String[] UIentity;
						private int[] UIentityType;
						private int[] UIentityArg0;*/
						
						if(UIentityType[i]==PlaySceneDataInit.SHOWONLY){ //Damn Java! I **wish** I can use switch statement with Enum. Oh well, I can do it in C++.
							Box horizontalBox = Box.createHorizontalBox();
							PlayScene.structureDetails.add(horizontalBox);
							JLabel label = new JLabel(UIentity[i]);
							horizontalBox.add(label);
							
							Component glueHori = Box.createHorizontalGlue();
							horizontalBox.add(glueHori);
	
							PlaySceneBoard.currentValue[i].setText(Integer.toString((int)structureBuilt.currentValue[getPropertyIDByResourceEnum(UIentityArg0[i])]));
							horizontalBox.add(PlaySceneBoard.currentValue[i]);
							JLabel slash = new JLabel("/");
							horizontalBox.add(slash);
							JLabel capLabel = new JLabel(Integer.toString((int)structureBuilt.valueCap[getPropertyIntCapIDByResourceEnum(UIentityArg0[i])]));
							horizontalBox.add(capLabel);
						}else if(UIentityType[i]==PlaySceneDataInit.CAPMOD){
							Box horizontalBox = Box.createHorizontalBox();
							PlayScene.structureDetails.add(horizontalBox);
							JLabel label = new JLabel(UIentity[i]);
							horizontalBox.add(label);
							
							Component glueHori = Box.createHorizontalGlue();
							horizontalBox.add(glueHori);
	
							double current = structureBuilt.currentValue[getPropertyIDByResourceEnum(UIentityArg0[i])];
							double currentCap = structureBuilt.valueCap[getPropertyIntCapIDByResourceEnum(UIentityArg0[i])];
							int cap = value[getPropertyIntCapIDByResourceEnum(UIentityArg0[i])][structureBuilt.currentLevel];
							PlaySceneBoard.currentValue[i].setText(Double.toString(current));
							horizontalBox.add(PlaySceneBoard.currentValue[i]);
							JLabel slash = new JLabel("/");
							horizontalBox.add(slash);
							final JSpinner spinner = new JSpinner();
							spinner.setModel(new SpinnerNumberModel((int)currentCap, 0, (int)cap, 1));
							spinner.setPreferredSize(new Dimension(0,0));
							spinner.setMaximumSize(new Dimension(32767, 30));
							final int finalI = i;
							spinner.addChangeListener(new ChangeListener(){
								@Override
								public void stateChanged(ChangeEvent e) {
									structureBuilt.valueCap[getPropertyIntCapIDByResourceEnum(UIentityArg0[finalI])] = ((Integer)spinner.getValue()).doubleValue();
								}});
							horizontalBox.add(spinner);
						}else if(UIentityType[i]==PlaySceneDataInit.UNITLIST&&structureBuilt.type==PlaySceneDataInit.UCENTER){
							Box commandBox = Box.createHorizontalBox();
							Box listBox = Box.createHorizontalBox();
							final Box nameBox = Box.createHorizontalBox();
							final Box propertiesBox = Box.createHorizontalBox();
							final Box upgradeNotificationBox = Box.createHorizontalBox();
							unitStates = Box.createVerticalBox();
							final Box trainBox = Box.createHorizontalBox();
							PlayScene.structureDetails.add(commandBox);
							PlayScene.structureDetails.add(listBox);
							PlayScene.structureDetails.add(nameBox);
							PlayScene.structureDetails.add(propertiesBox);
							PlayScene.structureDetails.add(upgradeNotificationBox);
							PlayScene.structureDetails.add(trainBox);
							PlayScene.structureDetails.add(unitStates);
	
							//commandBox
							commandButton = new JButton(L.s("Attack!(A)"));
							commandButton.setEnabled(false);
							commandBox.add(commandButton);
							addCommandListener();
							
							//listBox
							UFOList = new JList();
							listBox.add(UFOList);
							if(PlaySceneBoard.structureBuilt[PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer]].currentLevel+1<PlaySceneDataInit.unitEntity_n)
								upgradeNotificationBox.add(new JLabel("<html><center><font color='#FF0000'><b>"+L.s("Upgrade to unlock more units!")+"</b></font></center><html>"));
							UFOList.setCellRenderer(new BuildListCellRender());
	
							int entity_n = (PlaySceneBoard.structureBuilt[PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer]].currentLevel+1<PlaySceneDataInit.unitEntity_n?
									PlaySceneBoard.structureBuilt[PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer]].currentLevel+1:PlaySceneDataInit.unitEntity_n);
							//int entity_n = PlaySceneDataInit.unitEntity_n; //FIXME replace the code with above.
							final String[] icons  = new String[entity_n+1]; // +1 for cancel building
							icons[0] = "./images/deselect.png";
							for(int j=0;j<entity_n;j++)
								icons[j+1] = PlaySceneBoard.unitEntity[j].iconPath;
							UFOList.setModel(new AbstractListModel(){
								private static final long serialVersionUID = 1L;
								String[] values = icons;
								@Override public int getSize(){return values.length;}
								@Override public Object getElementAt(int index) {return values[index];}
							});
							UFOList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
							UFOList.setVisibleRowCount(1); //TODO: This allow only line list only. If there are too much entity, scroll bar will appear, which is not good.
							UFOList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
							UFOList.setPreferredSize(UFOList.getPreferredSize());
							if(selectedUnitEntity>=0){
								UFOList.setSelectedIndex(selectedUnitEntity+1);
								updateUnitInfo(structureBuilt, nameBox, propertiesBox, trainBox, status);
							}
							UFOList.removeMouseListener(mouseListener);
							UFOList.addMouseMotionListener(mouseListener);
							mouseListener = new MouseAdapter() {
								int oldIndex;
								@Override
								public void mousePressed(MouseEvent e){
									oldIndex = UFOList.getSelectedIndex();
									selectedUnitEntity = UFOList.getSelectedIndex()-1;
									if(selectedUnitEntity>=0){
										nameBox.setVisible(false);
										propertiesBox.setVisible(false);
										trainBox.setVisible(false);
										nameBox.setVisible(true);
										propertiesBox.setVisible(true);
										trainBox.setVisible(true);
										updateUnitInfo(structureBuilt, nameBox, propertiesBox, trainBox, status);
									}else{
										UFOList.clearSelection();
										nameBox.setVisible(false);
										propertiesBox.setVisible(false);
										trainBox.setVisible(false);
									}
									PlayScene.structureDetails.validate();
									PlayScene.structureDetails.repaint();
								}
								@Override
								public void mouseDragged(MouseEvent e){
									if(oldIndex>0)
										UFOList.setSelectedIndex(oldIndex);
									else
										UFOList.clearSelection();
								}
							};
							UFOList.addMouseListener(mouseListener);
							UFOList.addMouseMotionListener(mouseListener);
	
							unitStates.removeAll();
							for(int j=0;j<PlaySceneDataInit.unitEntity_n;j++){
								Box horizontalBox = Box.createHorizontalBox();
								PlayScene.structureDetails.add(horizontalBox);
								horizontalBox.add(new JLabel(PlaySceneBoard.unitEntity[j].name));
								
								Component glueHori = Box.createHorizontalGlue();
								horizontalBox.add(glueHori);
	
								horizontalBox.add(new JLabel(Integer.toString((int)structureBuilt.memory[this.getMemoryIdByName("unit_n")][j])));
								unitStates.add(horizontalBox);
							}
							PlayScene.structureDetails.validate();
							PlayScene.structureDetails.repaint();
						}else if(UIentityType[i]==PlaySceneDataInit.PATH){
							Box horizontalBox = Box.createHorizontalBox();
							PlayScene.structureDetails.add(horizontalBox);
							pathButton = new JButton(L.s("Create Path!"));
							horizontalBox.add(pathButton);
							addPathListener();
						}
					}
				}else{
					PlayScene.upgrade_sell.setVisible(false);
					PlayScene.btnStructureUpgrade.setEnabled(false);
					Box buildingBox = Box.createHorizontalBox();
					PlayScene.structureDetails.add(buildingBox);
					buildingBox.add(new JLabel(L.s("Building:")));
					PlayScene.buildingBar.setVisible(true);
					Component glueHori = Box.createHorizontalGlue();
					buildingBox.add(glueHori);
					buildingBox.add(PlayScene.buildingBar);
				}
				frameValueSync();
			}
		}catch(ArrayIndexOutOfBoundsException e){
			System.out.println("Array out of bound in UI, catched, ignoring.");
		}
		//String.format(format, i);
	}
	public void frameValueSync(){
		final StructureBuilt structureBuilt = PlaySceneBoard.structureBuilt[PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer]];
		if(structureBuilt.owner!=PlaySceneBoard.currentPlayer)
			return;
		if(!structureBuilt.upgrading){
			for(int i=0;i<UI_n;i++){
				if(UIentityType[i]==PlaySceneDataInit.SHOWONLY||UIentityType[i]==PlaySceneDataInit.CAPMOD){
					PlaySceneBoard.currentValue[i].setText(Integer.toString((int)PlaySceneBoard.structureBuilt[PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer]].currentValue[getPropertyIDByResourceEnum(UIentityArg0[i])]));
				}else if(UIentityType[i]==PlaySceneDataInit.UNITLIST){
					switch(structureBuilt.memory[getMemoryIdByName("attacking")][0]){
						case E.IDLE:
							int totalUnitInside = 0;
							for(int j=0;j<PlaySceneDataInit.unitEntity_n;j++)
								totalUnitInside += structureBuilt.memory[this.getMemoryIdByName("unit_n")][j];
							if(totalUnitInside==0){
								commandButton.setText(L.s("Attack!(Train UFO first!)"));
								commandButton.removeActionListener(commandActionListener);
								commandButton.setEnabled(false);
							}else if(structureBuilt.pathPointer==null||PlaySceneBoard.states==PlaySceneBoard.SETTINGPATH){
								commandButton.setText(L.s("Attack!(Create path first!)"));
								commandButton.removeActionListener(commandActionListener);
								commandButton.setEnabled(false);
							}else{
								commandButton.setText(L.s("Attack!(A)"));
								addCommandListener();
								commandButton.setEnabled(true);
							}
							if(PlaySceneBoard.states==PlaySceneBoard.SETTINGPATH&&structureBuilt.pathPointer.n<=1){
								pathButton.removeActionListener(pathActionListener);
								pathButton.setEnabled(false);
							}else{
								addPathListener();
								pathButton.setEnabled(true);
							}
						break;
						case E.ATTACKING:
							commandButton.setText(L.s("RETREAT D:(R)"));
							addCommandListener();
							commandButton.setEnabled(true);
							pathButton.removeActionListener(pathActionListener);
							pathButton.setEnabled(false);
						break;
						case E.RETREATING:
							commandButton.setText(L.s("Retreating..."));
							commandButton.removeActionListener(commandActionListener);
							commandButton.setEnabled(false);
							pathButton.removeActionListener(pathActionListener);
							pathButton.setEnabled(false);
						break;
					}

					unitStates.removeAll();
					for(int j=0;j<PlaySceneDataInit.unitEntity_n;j++){
						Box horizontalBox = Box.createHorizontalBox();
						PlayScene.structureDetails.add(horizontalBox);
						horizontalBox.add(new JLabel(PlaySceneBoard.unitEntity[j].name));
						
						Component glueHori = Box.createHorizontalGlue();
						horizontalBox.add(glueHori);

						horizontalBox.add(new JLabel(Integer.toString((int)structureBuilt.memory[this.getMemoryIdByName("unit_n")][j])));
						unitStates.add(horizontalBox);
					}
					selectedUnitEntity = UFOList.getSelectedIndex()-1;
					if(selectedUnitEntity>-1){
						if(PlaySceneBoard.resourceAmount[PlaySceneBoard.currentPlayer][PlaySceneBoard.resourceEnumToId(PlaySceneDataInit.GOLD)]<PlaySceneBoard.unitEntity[selectedUnitEntity].costGold)
							trainButton.setEnabled(false);
						else
							trainButton.setEnabled(true);
					}
				}else if(UIentityType[i]==PlaySceneDataInit.PATH){
					if(PlaySceneBoard.states==PlaySceneBoard.SETTINGPATH){
						pathButton.setText(L.s("Finish!"));
						if(structureBuilt.pathPointer.n>1)
							pathButton.setEnabled(true);
						else
							pathButton.setEnabled(false);
					}else{
						pathButton.setText(L.s("Create Path!"));
					}
				}
			}
		}else{
			PlayScene.buildingBar.setValue((int)(100*(double)(PlaySceneBoard.currentTick-structureBuilt.previousUpdate)/(double)(value[getPropertyIDByResourceEnum(PlaySceneDataInit.UPGRADETIME)][structureBuilt.currentLevel]*1000)));
		}
		if(!structureBuilt.upgrading&&PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer]>-1){
			boolean enoughResource;
			if(currentLevel<maxLevel)
				enoughResource = PlaySceneBoard.checkCost(PlaySceneBoard.structureBuilt[PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer]].type,PlaySceneBoard.structureBuilt[PlaySceneBoard.selectedStructure[PlaySceneBoard.currentPlayer]].currentLevel+1, PlaySceneBoard.currentPlayer);
			else
				enoughResource = false;
			PlayScene.btnStructureUpgrade.setEnabled(enoughResource);
		}
	}
	
	JButton trainButton;
	public void updateUnitInfo(final StructureBuilt structureBuilt, Box nameBox, Box propertiesBox, Box trainBox, final byte status){
		nameBox.removeAll();
		propertiesBox.removeAll();
		trainBox.removeAll();

		//nameBox
		JLabel unitName = new JLabel("<html><center>"+L.s(PlaySceneBoard.unitEntity[selectedUnitEntity].name)+"</center></html>"); //TODO unitEntity:name
		nameBox.add(unitName);
		unitName.setHorizontalAlignment(SwingConstants.CENTER);
		unitName.setAlignmentX(Component.CENTER_ALIGNMENT);

		//propertiesBox
		JTable unitPropTable = new JTable();
		UnitEntity u = PlaySceneBoard.unitEntity[selectedUnitEntity];
		unitPropTable.setModel(new DefaultTableModel(
			new Object[][]{
				{"Health",	u.health,},
				{"Attack",	u.attack,},
				{"Speed",	u.speed,},
				{"cost:Gold",	u.costGold,},
				{"Workers required",	u.workerRequired,},
			},
			new String[]{
					L.s("Properties"), L.s("Value")
			}
		));
		unitPropTable.setEnabled(false);
		final Box propertiesBox2 = Box.createVerticalBox();
		propertiesBox2.add(unitPropTable.getTableHeader());
		propertiesBox2.add(unitPropTable);
		propertiesBox.add(propertiesBox2);

		//trainBox
		trainButton = new JButton(L.s("Train(T)"));
		trainBox.add(trainButton);
		if(PlaySceneBoard.resourceAmount[PlaySceneBoard.currentPlayer][PlaySceneBoard.resourceEnumToId(PlaySceneDataInit.GOLD)]<PlaySceneBoard.unitEntity[selectedUnitEntity].costGold)
			trainButton.setEnabled(false);
		trainButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				buyUFO(structureBuilt, selectedUnitEntity, PlaySceneBoard.currentPlayer, status);
			}
		});
	}
	public boolean buyUFO(StructureBuilt structureBuilt, int type, int player, byte status){
		if(type>structureBuilt.currentLevel||category!=E.OFFENSIVE)
			return false;
		UnitEntity u = PlaySceneBoard.unitEntity[type];
		if(u.costGold<=PlaySceneBoard.resourceAmount[player][PlaySceneBoard.resourceEnumToId(PlaySceneDataInit.GOLD)]&&
		structureBuilt.currentValue[getPropertyIDByResourceEnum(PlaySceneDataInit.UNITCAP)]<structureBuilt.valueCap[getPropertyIDByResourceEnum(PlaySceneDataInit.UNITCAP)]&&
		type>=0){
			int totalUnitInside = 0;
			for(int i=0;i<PlaySceneDataInit.unitEntity_n;i++)
				totalUnitInside += structureBuilt.memory[getMemoryIdByName("unit_n")][i];

System.out.println("Meow "+totalUnitInside+","+type);
			structureBuilt.unitHealth[totalUnitInside] = PlaySceneBoard.unitEntity[type].health;
			structureBuilt.unitType[totalUnitInside] = type;
			structureBuilt.unitReady[totalUnitInside] = true;
			structureBuilt.memory[getMemoryIdByName("unit_n")][type]++;
			PlaySceneBoard.resourceAmount[player][PlaySceneBoard.resourceEnumToId(PlaySceneDataInit.GOLD)] -= u.costGold;
			structureBuilt.currentValue[getPropertyIDByResourceEnum(PlaySceneDataInit.UNITCAP)]++;

			updateUI(currentLevel, status, player);
			return true;
		}
		return false;
	}
	public void attack(int id){
		if((category&E.OFFENSIVE)!=0){
			if(PlaySceneBoard.structureBuilt[id].memory[getMemoryIdByName("attacking")][0]==E.IDLE&&
					PlaySceneBoard.structureBuilt[id].pathPointer!=null){
				int totalUnits = 0;
				for(int i=0;i<PlaySceneDataInit.unitEntity_n;i++)
					totalUnits += PlaySceneBoard.structureBuilt[id].memory[getMemoryIdByName("unit_n")][i];
				if(PlaySceneBoard.structureBuilt[id].pathPointer.n>=2&&
						(PlaySceneBoard.states==PlaySceneBoard.NORMAL||PlaySceneBoard.currentPlayer!=PlaySceneBoard.structureBuilt[id].owner)&&
						totalUnits>0){
					PlaySceneBoard.structureBuilt[id].memory[getMemoryIdByName("attacking")][0] = E.ATTACKING;
					PlaySceneBoard.structureBuilt[id].execute(E.RELEASEUNIT);
				}
			}
		}
	}
	public void retreat(int id){
		if((category&E.OFFENSIVE)!=0){
			if(PlaySceneBoard.structureBuilt[id].memory[getMemoryIdByName("attacking")][0]==E.ATTACKING){
				PlaySceneBoard.structureBuilt[id].memory[getMemoryIdByName("attacking")][0] = E.RETREATING;
				PlaySceneBoard.structureBuilt[id].execute(E.RETREATUNIT);
			}
		}
	}
}
