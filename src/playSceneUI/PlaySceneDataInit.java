/*Copyright 2012 Wong Cho Ching, all rights reserved.
 * This file is released under BSD 2-clause License
 * For details, please read LICENSE.txt in this package.*/

package playSceneUI;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
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
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;

import enums.E;

import GUI.PlayScene;

public class PlaySceneDataInit {
	private static int id = 0;
	public static int POPULATION = id++;
	public static int POPULATION_AVAILABLE = id++; //special variable: refill in every frame.
	public static int GOLD = id++;
	public static int WATER = id++;
	public static int FARAWAY = id++;
	public static int HEALTH = id++;
	public static int UNITCAP = id++;
	public static int UPGRADETIME = id++;
	public static int DESCRIPTION = id++; //NOTE remember to add enum with striped *_GEN and * because I used genID+1 to recognize the ID.
	
	public static int ATTACK = id++;
	public static int RANGE = id++;
	public static int RATE = id++;
	public static int CANNONICONID = id++;

	//public final static int NOTHING = 0x00010
	public final static int ADD = 0x0001;
	public final static int DEDUCT = 0x0002;
	public final static int SET = 0x0004;
	//public final static int ABSOLUTE = 0x0000;
	public final static int DELTA = 0x0010;
	public final static int CONTINOUS = 0x0020;
	public final static int GENERATED = 0x0040;
	public final static int OCCUPY = 0x0080;
	//public final static int DOACTION_ANYWAY = 0x0000;
	public final static int TILL = 0x0100;
	public final static int ATEXTCAP = 0x0200; // raising/lowering a cap
	//public final static int CAPLESS = 0x0000;
	public final static int INTCAP = 0x1000;
	public final static int EXTCAP = 0x2000;
	public final static int EXHAUST = 0x4000;
	//public final static int NONPREREQ = 0x0000000;
	public final static int PREREQ = 0x8000000;
	public final static int COST = 0x4000000;
	public final static int EACH = 0x2000000;
	public final static int ISCAP = 0x1000000;
	public final static int LOCAL = 0x0800000;
	public final static int VARH = 0x0400000; //enemy difficulty variable, higher is harder. TODO NOT working for many attributes due to my laziness of coding
	public final static int VARL = 0x0200000; //enemy difficulty variable, lower is harder. TODO NOT working for many attributes due to my laziness of coding

	public PlaySceneDataInit(){
		constructResourcePanelUI();
		createStructureList();
		createUnitList();
		constructStructureUI();
		/*structureEntity[0].addProperty("attack", new int[]{100,200}, "%d", false);
		structureEntity[0].addProperty("gold", new int[]{50,100}, "%d", true);*/
	}
	static BufferedImage image;
	static int[] texture = new int[5];
	static int[] deselectIcon = new int[1];
	static int[] upgradeIcon = new int[1];
	static void loadTextures(GL2 gl){
		gl.glGenTextures(5, texture, 0);
		gl.glGenTextures(1, deselectIcon, 0);
		gl.glGenTextures(1, upgradeIcon, 0);
		for(int i=0;i<PlaySceneBoard.resourceIcon.length+2;i++){
			try{
				if(i==PlaySceneBoard.resourceIcon.length)
					image = ImageIO.read(new File("./images/GL/deselect.png"));
				else if(i==PlaySceneBoard.resourceIcon.length+1)
					image = ImageIO.read(new File("./images/GL/upgrading.png"));
				else{
					if(PlaySceneBoard.resourceEnumMap[i]==GOLD)
						image = ImageIO.read(new File("./images/GL/resources/gold.png"));
					else if(PlaySceneBoard.resourceEnumMap[i]==WATER)
						image = ImageIO.read(new File("./images/GL/resources/water.png"));
					else if(PlaySceneBoard.resourceEnumMap[i]==POPULATION)
						image = ImageIO.read(new File("./images/GL/resources/population.png"));
					else if(PlaySceneBoard.resourceEnumMap[i]==POPULATION_AVAILABLE)
						image = ImageIO.read(new File("./images/GL/resources/populationAvailable.png"));
					else if(PlaySceneBoard.resourceEnumMap[i]==FARAWAY)
						image = ImageIO.read(new File("./images/GL/resources/farAway.png"));
				}
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

			if(i==PlaySceneBoard.resourceIcon.length)
				gl.glBindTexture(GL2.GL_TEXTURE_2D, deselectIcon[0]);
			else if(i==PlaySceneBoard.resourceIcon.length+1)
				gl.glBindTexture(GL2.GL_TEXTURE_2D, upgradeIcon[0]);
			else
				gl.glBindTexture(GL2.GL_TEXTURE_2D, texture[i]);
			gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 1);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
			gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
			gl.glTexImage2D (GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, image.getWidth(), image.getHeight(), 0, GL2.GL_RGBA, 
					GL.GL_UNSIGNED_BYTE, byteBuffer);
		}
	}
	private void constructStructureUI(){
		final String[] icons  = new String[structure_n]; // +1 for cancel building
		icons[0] = "./images/deselect.png";
		for(int i=0;i<structure_n-1;i++)
			icons[i+1] = PlaySceneBoard.structureEntity[i].iconPath;
		PlayScene.structureList.setModel(new AbstractListModel(){
			private static final long serialVersionUID = 1L;
			String[] values = icons;
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
	}
	static JLabel[] resDataLabel;
	private void constructResourcePanelUI(){
		PlaySceneBoard.resourceEnumMap = new int[]{		GOLD,	WATER,	POPULATION,		POPULATION_AVAILABLE,	FARAWAY,	HEALTH, UNITCAP, UPGRADETIME, 
		};PlaySceneBoard.resourceName = new String[]{	"gold",	"water","population",	"population available",
		};PlaySceneBoard.resourceAmount = new double[][]{new double[]{
														500,	0,		5,				5,
		},new double[]{								500,	0,		5,				5,
		}};PlaySceneBoard.resourceCap = new int[][]{new int[]{
														500,	0,		5,				5,
		},new int[]{									500,	0,		5,				5,
		}};PlaySceneBoard.resourceIcon = new String[]{	"./images/resources/gold.png", "./images/resources/water.png", "./images/resources/population.png",	"./images/resources/populationAvailable.png", "./images/resources/farAway.png",
		};
		PlaySceneBoard.resourceInitValue = new double[PlaySceneBoard.resourceAmount.length][PlaySceneBoard.resourceAmount[0].length];
		for(int i=0;i<PlaySceneBoard.resourceAmount.length;i++){
			for(int j=0;j<PlaySceneBoard.resourceAmount[i].length;j++)
				PlaySceneBoard.resourceInitValue[i][j] = PlaySceneBoard.resourceAmount[i][j];
		}
		PlaySceneBoard.resourceInitCap = new int[PlaySceneBoard.resourceCap.length][PlaySceneBoard.resourceCap[0].length];
		for(int i=0;i<PlaySceneBoard.resourceCap.length;i++){
			for(int j=0;j<PlaySceneBoard.resourceCap[i].length;j++)
				PlaySceneBoard.resourceInitCap[i][j] = PlaySceneBoard.resourceCap[i][j];
		}
		PlaySceneBoard.localResourceSeperator = PlaySceneBoard.resourceName.length;
		JLabel[] resNameLabel = new JLabel[PlaySceneBoard.resourceEnumMap.length];
		resDataLabel = new JLabel[PlaySceneBoard.resourceEnumMap.length];
		for(int i=0;i<PlaySceneBoard.localResourceSeperator;i++){
				resNameLabel[i] = new JLabel();
				resNameLabel[i].setToolTipText(PlaySceneBoard.resourceName[i]);
				resNameLabel[i].setIcon(new ImageIcon(PlaySceneBoard.resourceIcon[i]));
				//resNameLabel[i].setText(PlaySceneBoard.resourceName[i]);
				resDataLabel[i] = new JLabel(Math.round(PlaySceneBoard.resourceAmount[PlaySceneBoard.currentPlayer][i])+"/"+Math.round(PlaySceneBoard.resourceCap[PlaySceneBoard.currentPlayer][i]));
				PlayScene.resourcePanel.add(resNameLabel[i]);
				PlayScene.resourcePanel.add(resDataLabel[i]);
		}
	}
	public static void updateResourcePanelUI(){
		for(int i=0;i<PlaySceneBoard.localResourceSeperator;i++)
			resDataLabel[i].setText(Math.round(PlaySceneBoard.resourceAmount[PlaySceneBoard.currentPlayer][i])+"/"+Math.round(PlaySceneBoard.resourceCap[PlaySceneBoard.currentPlayer][i]));
	}
	private static int id3 = 0;
	final static int WELL = id3++;
	final static int HOUSING = id3++;
	final static int GOLDMINE = id3++;
	final static int BANK = id3++;
	final static int SNIPER = id3++;
	final static int MACHINEGUN = id3++;
	final static int HOSPITAL = id3++;
	final static int SPLASHTOWER = id3++;
	final static int UCENTER = id3++;
	
	/*
	final static int WELL = id3++;
	final static int HOUSING = id3++;
	final static int GOLDMINE = id3++;
	final static int BANK = id3++;
	final static int SNIPER = id3++;
	final static int MACHINEGUN = id3++;
	final static int HOSPITAL = id3++;
	final static int ULTIMATETOWER = id3++;
	final static int UCENTER = id3++;*/
	
	
	public final static int BASE = id3++;
	public static int structure_n = id3;
	
	
	private static int id4 = 0;
	final static int ECONOMICALUFO = id4++;
	final static int SPEEDYUFO = id4++;
	final static int SUPERUFO = id4++;
	public static int unitEntity_n = id4;

	private static int id5 = 0;
	public final static int SHOWONLY = id5++;
	public final static int CAPMOD = id5++;
	public final static int UNITLIST = id5++;
	public final static int PATH = id5++;
	private void createStructureList(){
		PlaySceneBoard.structureEntity = new StructureEntity[structure_n];
		PlaySceneBoard.structureEntity[WELL] = new StructureEntity("Well", "./images/structures/well.png", "./images/GL/structures/well.png", 1, 2, 2, E.RESOURCES|E.STORAGE, 1000, 0);
		PlaySceneBoard.structureEntity[WELL].setPropertiesByTable(
		new int[]{			-1,			WATER,							WATER,				GOLD,									POPULATION_AVAILABLE,				HEALTH,				UPGRADETIME,	DESCRIPTION,
		},new String[]{		"Level",	"Water generate",				"Water capacity",	"Cost:gold",							"Workers required",					"Health",			"Build time",	"",
		},new int[]{		0,			ADD|CONTINOUS|TILL|EXTCAP,		ADD|DELTA|ATEXTCAP,	DEDUCT|TILL|EXHAUST|PREREQ|COST|VARL,	DEDUCT|OCCUPY|TILL|EXHAUST|PREREQ,	SET|LOCAL|ISCAP,	SET|LOCAL,		0,
		},new int[]{		-1,			-1,								-1,					-1,										-1,									-1,					-1,				-1,
		},new int[]{		0,			0,								DEDUCT|ATEXTCAP,	ADD|COST,								0,									0,					0,				0,
		},new boolean[]{	true,		true,							true,				true,									true,								true,				true,			false,
		},new String[]{		"%d",		"%d/s",							"%d",				"$%d",									"%d",								"%d",				"%ds",			null,
		},new String[][]{{	"1",		"200",							"200",				"100",									"1",								"80",				"1",			"Water is essential for life."
			,},{			"2",		"500",							"750",				"300",									"2",								"100",				"5",			"Buy a larger well for more water."
			,},{			"3",		"1000",							"2000",				"750",									"4",								"120",				"7",			"We are still thirsty!"
			,},
		});

		PlaySceneBoard.structureEntity[HOUSING] = new StructureEntity("Housing", "./images/structures/housing.png", "./images/GL/structures/housing.png", 1, 2, 2, E.RESOURCES|E.STORAGE, 1000, 0);
		PlaySceneBoard.structureEntity[HOUSING].setPropertiesByTable(
		new int[]{			-1,			POPULATION, 				POPULATION,						GOLD,									WATER,										HEALTH,				UPGRADETIME,	DESCRIPTION,
		},new String[]{		"Level",	"Population generate",		"Capacity",						"Cost:gold",							"Consume water/each",						"Health",			"Build time",	"",
		},new int[]{		0,			ADD|CONTINOUS|TILL|INTCAP,	ADD|DELTA|ATEXTCAP|ISCAP,		DEDUCT|TILL|EXHAUST|PREREQ|COST|VARL,	DEDUCT|CONTINOUS|TILL|EXHAUST|PREREQ|EACH,	SET|LOCAL|ISCAP,	SET|LOCAL,		0,
		},new int[]{		-1,			-1,							-1,								-1,										POPULATION,									-1,					-1,				-1,
		},new int[]{		0,			DEDUCT|GENERATED,			DEDUCT|ATEXTCAP,				ADD|COST,								0,											0,					0,				0,
		},new boolean[]{	true,		true,						true,							true,									true,										true,				true,			false,
		},new String[]{		"%d",		"%d/s",						"%d",							"$%d",									"%d/sec",									"%d",				"%ds",			null,
		},new String[][]{{	"1",		"1",						"10",							"100",									"10",										"100",				"1",			"Buy housing for more population.",
			},{				"2",		"2",						"20",							"300",									"12",										"1000",				"5",			"More housing = higher productivity",
			},{				"3",		"3",						"50",							"1000",									"14",										"1500",				"7",			"Upgrade for a huge house.",
			},
		});
		PlaySceneBoard.structureEntity[HOUSING].setStructureUI(
		new String[]{	"Population",
		},new int[]{	CAPMOD,
		},new int[]{	POPULATION,
		});

		PlaySceneBoard.structureEntity[GOLDMINE] = new StructureEntity("Gold Mine", "./images/structures/goldMine.png", "./images/GL/structures/goldMine.png", 1, 2, 2, E.RESOURCES, 1000, 0);
		PlaySceneBoard.structureEntity[GOLDMINE].setPropertiesByTable(
		new int[]{			-1,			GOLD,							GOLD,								POPULATION_AVAILABLE,				HEALTH,				UPGRADETIME,	DESCRIPTION,
		},new String[]{		"Level",	"Gold generation",				"Cost:gold",						"Workers required",					"Health",			"Build time",	"",
		},new int[]{		0,			ADD|CONTINOUS|TILL|EXTCAP|VARH,	DEDUCT|TILL|EXHAUST|PREREQ|COST,	DEDUCT|OCCUPY|TILL|EXHAUST|PREREQ,	SET|LOCAL|ISCAP,	SET|LOCAL,		0,
		},new int[]{		-1,			-1,								-1,									-1,									-1,					-1,				-1,
		},new int[]{		0,			0,								ADD|COST,							0,									0,					0,				0,
		},new boolean[]{	true,		true,							true,								true,								true,				true,			false,
		},new String[]{		"%d",		"$%d/s",						"$%d",								"%d",								"%d",				"%ds",			null,
		},new String[][]{{	"1",		"2",							"100",								"2",								"20",				"5",			"Generate Gold!"
			,},{			"2",		"4",							"250",								"4",								"25",				"10",			"$$$$ :D"
				,},{		"3",		"8",							"600",								"8",								"30",				"20",			"Become a millionaire."
				,},
		});

		PlaySceneBoard.structureEntity[BANK] = new StructureEntity("Bank", "./images/structures/storage.png", "./images/GL/structures/storage.png", 1, 2, 2, E.STORAGE, 1000, 0);
		PlaySceneBoard.structureEntity[BANK].setPropertiesByTable(
		new int[]{			-1,			GOLD,						GOLD,									POPULATION_AVAILABLE,				HEALTH,				UPGRADETIME,	DESCRIPTION,
		},new String[]{		"Level",	"Gold capacity",			"Cost:gold",							"Workers required",					"Health",			"Build time",	"",
		},new int[]{		0,			ADD|DELTA|ATEXTCAP,			DEDUCT|TILL|EXHAUST|PREREQ|COST|VARL,	DEDUCT|OCCUPY|TILL|EXHAUST|PREREQ,	SET|LOCAL|ISCAP,	SET|LOCAL,		0,
		},new int[]{		-1,			-1,							-1,										-1,									-1,					-1,				-1,
		},new int[]{		0,			DEDUCT|ATEXTCAP,			ADD|COST,								0,									0,					0,				0,
		},new boolean[]{	true,		true,						true,									true,								true,				true,			false,
		},new String[]{		"%d",		"%d",						"$%d",									"%d",								"%d",				"%ds",			null,
		},new String[][]{{	"1",		"500",						"100",									"1",								"50",				"1",			"Raise the capacity of gold."
			,},{			"2",		"1000",						"200",									"2",								"75",				"5",			"Store more gold!"
				,},{		"3",		"5000",						"1000",									"5",								"100",				"7",			"Store more, more, and more!"
			,},
		});

		PlaySceneBoard.structureEntity[SNIPER] = new StructureEntity("Sniper", "./images/structures/sniperTowerBase.png", "./images/GL/structures/sniperTowerBase.png", 1, 2, 2, E.DEFENSIVE, 1000, 0);
		PlaySceneBoard.structureEntity[SNIPER].setPropertiesByTable(
		new int[]{			-1,			ATTACK,				RANGE,				RATE,				CANNONICONID,	GOLD,									POPULATION_AVAILABLE,				HEALTH,				UPGRADETIME,	DESCRIPTION,
		},new String[]{		"Level",	"Attack",			"Range",			"Rate",				"",				"Cost:gold",							"Workers required",					"Health",			"Build time",	"",
		},new int[]{		0,			ADD|DELTA|LOCAL,	ADD|DELTA|LOCAL,	ADD|DELTA|LOCAL,	SET|LOCAL,		DEDUCT|TILL|EXHAUST|PREREQ|COST|VARL,	DEDUCT|OCCUPY|TILL|EXHAUST|PREREQ,	SET|LOCAL|ISCAP,	SET|LOCAL,		0,
		},new int[]{		-1,			-1,					-1,					-1,					-1,				-1,										-1,									-1,					-1,				-1,
		},new int[]{		0,			0,					0,					0,					0,				ADD|COST,								0,									0,					0,				0,
		},new boolean[]{	true,		true,				true,				true,				false,			true,									true,								true,				true,			false,
		},new String[]{		"%d",		"%d",				"%d",				"%d/m",				"",				"$%d",									"%d",								"%d",				"%ds",			null,
		},new String[][]{{	"1",		"20",				"15",				"10",				"0",			"100",									"1",								"10",				"1",			"Defend your base! <font color='#FF0000'><b>Build limit:5</b></font>"
			,},{			"2",		"30",				"17",				"15",				"0",			"200",									"3",								"15",				"10",			"Higher level = better"
				,},{		"3",		"40",				"20",				"20",				"0",			"500",									"6",								"20",				"25",			"Cover the entire screen."
				,},
		});

		PlaySceneBoard.structureEntity[MACHINEGUN] = new StructureEntity("Machine Gun Tower", "./images/structures/machineGunTowerBase.png", "./images/GL/structures/machineGunTowerBase.png", 1, 2, 2, E.DEFENSIVE, 1000, 1);
		PlaySceneBoard.structureEntity[MACHINEGUN].setPropertiesByTable(
		new int[]{			-1,			ATTACK,				RANGE,				RATE,				CANNONICONID,	GOLD,									POPULATION_AVAILABLE,				HEALTH,				UPGRADETIME,	DESCRIPTION,
		},new String[]{		"Level",	"Attack",			"Range",			"Rate",				"",				"Cost:gold",							"Workers required",					"Health",			"Build time",	"",
		},new int[]{		0,			ADD|DELTA|LOCAL,	ADD|DELTA|LOCAL,	ADD|DELTA|LOCAL,	SET|LOCAL,		DEDUCT|TILL|EXHAUST|PREREQ|COST|VARL,	DEDUCT|OCCUPY|TILL|EXHAUST|PREREQ,	SET|LOCAL|ISCAP,	SET|LOCAL,		0,
		},new int[]{		-1,			-1,					-1,					-1,					-1,				-1,										-1,									-1,					-1,				-1,
		},new int[]{		0,			0,					0,					0,					0,				ADD|COST,								0,									0,					0,				0,
		},new boolean[]{	true,		true,				true,				true,				false,			true,									true,								true,				true,			false,
		},new String[]{		"%d",		"%d",				"%d",				"%d/m",				"",				"$%d",									"%d",								"%d",				"%ds",			null,
		},new String[][]{{	"1",		"2",				"7",				"400",				"1",			"200",									"2",								"10",				"5",			"Fast shooting tower <font color='#FF0000'><b>Build limit:5</b></font>"
			,},{			"2",		"3",				"8",				"600",				"1",			"400",									"4",								"15",				"15",			"Shooting more faster."
				,},{		"3",		"4",				"10",				"1000",				"1",			"800",									"7",								"20",				"30",			"Enjoy the rapidness!"
				,},
		});

		PlaySceneBoard.structureEntity[HOSPITAL] = new StructureEntity("Hospital", "./images/structures/healingTowerBase.png", "./images/GL/structures/healingTowerBase.png", 1, 2, 2, E.REPAIR, 1000, 2);
		PlaySceneBoard.structureEntity[HOSPITAL].setPropertiesByTable(
		new int[]{			-1,			ATTACK,				RANGE,				RATE,				CANNONICONID,	GOLD,									POPULATION_AVAILABLE,				HEALTH,				UPGRADETIME,	DESCRIPTION,
		},new String[]{		"Level",	"Heal UFOs",		"Range",			"Rate",				"",				"Cost:gold",							"Workers required",					"Health",			"Build time",	"",
		},new int[]{		0,			DEDUCT|DELTA|LOCAL,	ADD|DELTA|LOCAL,	ADD|DELTA|LOCAL,	SET|LOCAL,		DEDUCT|TILL|EXHAUST|PREREQ|COST|VARL,	DEDUCT|OCCUPY|TILL|EXHAUST|PREREQ,	SET|LOCAL|ISCAP,	SET|LOCAL,		0,
		},new int[]{		-1,			-1,					-1,					-1,					-1,				-1,										-1,									-1,					-1,				-1,
		},new int[]{		0,			0,					0,					0,					0,				ADD|COST,								0,									0,					0,				0,
		},new boolean[]{	true,		true,				true,				true,				false,			true,									true,								true,				true,			false,
		},new String[]{		"%d",		"%d",				"%d",				"%d/m",				"",				"$%d",									"%d",								"%d",				"%ds",			null,
		},new String[][]{{	"1",		"1",				"10",				"100",				"2",			"200",									"2",								"50",				"5",			"Heal UFOs"
			,},{			"2",		"3",				"12",				"110",				"2",			"400",									"4",								"80",				"15",			"HP+ :)"
				,},{		"3",		"6",				"15",				"140",				"2",			"1000",									"7",								"100",				"30",			"Hire some skillful doctors."
				,},
		});
		

		PlaySceneBoard.structureEntity[SPLASHTOWER] = new StructureEntity("Splash Tower", "./images/structures/ultimateTowerBase.png", "./images/GL/structures/ultimateTowerBase.png", 1, 2, 2, E.DEFENSIVE, 1000, 3);
		PlaySceneBoard.structureEntity[SPLASHTOWER].setPropertiesByTable(
		new int[]{			-1,			ATTACK,					RANGE,				RATE,				CANNONICONID,	GOLD,									POPULATION_AVAILABLE,				HEALTH,				UPGRADETIME,	DESCRIPTION,
		},new String[]{		"Level",	"Attack",				"Range",			"Rate",				"",				"Cost:gold",							"Workers required",					"Health",			"Build time",	"",
		},new int[]{		0,			ADD|DELTA|LOCAL|VARH,	ADD|DELTA|LOCAL,	ADD|DELTA|LOCAL,	SET|LOCAL,		DEDUCT|TILL|EXHAUST|PREREQ|COST|VARL,	DEDUCT|OCCUPY|TILL|EXHAUST|PREREQ,	SET|LOCAL|ISCAP,	SET|LOCAL,		0,
		},new int[]{		-1,			-1,						-1,					-1,					-1,				-1,										-1,									-1,					-1,				-1,
		},new int[]{		0,			0,						0,					0,					0,				ADD|COST,								0,									0,					0,				0,
		},new boolean[]{	true,		true,					true,				true,				false,			true,									true,								true,				true,			false,
		},new String[]{		"%d",		"%d",					"%d",				"%d/m",				"",				"$%d",									"%d",								"%d",				"%ds",			null,
		},new String[][]{{	"1",		"20",					"9",				"70",				"3",			"10000",								"20",								"10000",			"10",			"Deal splash damage <font color='#FF0000'><b>Build limit:5</b></font>"
			,},{			"2",		"25",					"10",				"90",				"3",			"20000",								"50",								"200000",			"25",			"Boom! Boom! Boom!"
				,},{		"3",		"50",					"11",				"150",				"3",			"50000",								"100",								"5000000",			"50",			"Crazy cost $$$!"
				,},
		});

		PlaySceneBoard.structureEntity[UCENTER] = new StructureEntity("UFO Center", "./images/structures/UFOCenter.png", "./images/GL/structures/UFOCenter.png", 1, 2, 2, E.OFFENSIVE, 1000, 0);
		PlaySceneBoard.structureEntity[UCENTER].setPropertiesByTable(
		new int[]{			-1,			GOLD,									POPULATION_AVAILABLE,				UNITCAP,			HEALTH,				UPGRADETIME,	DESCRIPTION,
		},new String[]{		"Level",	"Cost:gold",							"Workers required",					"Units limit",		"Health",			"Build time",	"",
		},new int[]{		0,			DEDUCT|TILL|EXHAUST|PREREQ|COST|VARL,	DEDUCT|OCCUPY|TILL|EXHAUST|PREREQ,	SET|LOCAL|ISCAP,	SET|LOCAL|ISCAP,	SET|LOCAL,		0,
		},new int[]{		-1,			-1,										-1,									-2,					-1,					-1,				-1,
		},new int[]{		0,			ADD|COST,								0,									0,					0,					0,				0,
		},new boolean[]{	true,		true,									true,								true,				true,				true,			false,
		},new String[]{		"%d",		"$%d",									"%d",								"%d",				"%d",				"%ds",			null,
		},new String[][]{{	"1",		"100",									"5",								"5",				"10",				"1",			"Attack Enemy!! <font color='#FF0000'><b>You can build 5 only</b></font>"
			,},{			"2",		"400",									"10",								"10",				"15",				"5",			"Unlock Speedy UFO!"
			,},{			"3",		"1500",									"50",								"20",				"20",				"10",			"Unlock Super UFO!"
			,},
		});
		PlaySceneBoard.structureEntity[UCENTER].setStructureUI(
		new String[]{	"Units",	"",				"",
		},new int[]{	SHOWONLY,	UNITLIST,	PATH,
		},new int[]{	UNITCAP,	0,				0,
		});
		PlaySceneBoard.structureEntity[UCENTER].setCustomMemory(
		new String[]{	"attacking",	"unit_n",
		},new int[]{	1,				unitEntity_n,
		});

		PlaySceneBoard.structureEntity[BASE] = new StructureEntity("Base", "./images/structures/base.png", "./images/GL/structures/base.png", 1, 4, 4, 0, 1000, 0);
		PlaySceneBoard.structureEntity[BASE].setPropertiesByTable(
		new int[]{			-1,			HEALTH,				UPGRADETIME,	DESCRIPTION,
		},new String[]{		"Level",	"Health",			"",				"",
		},new int[]{		0,			SET|LOCAL|ISCAP,	SET|LOCAL,		0,
		},new int[]{		-1,			-1,					-1,				-1,
		},new int[]{		0,			0,					0,				0,
		},new boolean[]{	true,		true,				false,			false,
		},new String[]{		"%d",		"%d",				"",				null,
		},new String[][]{{	"1",		"1000",				"0",			"Protect it well or you will be defeated!",
			},
		});

		for(int i=0;i<structure_n;i++){
			PlaySceneBoard.structureEntity[i].insertStructureUI(
					new String[]{	"Health",
					},new int[]{	SHOWONLY,
					},new int[]{	HEALTH,
					});
		}
		//TODO There should be a UI to select structures to be used before the game start.
		PlaySceneBoard.structureEntityMap = new int[structure_n];
		for(int i=0;i<structure_n;i++)
			PlaySceneBoard.structureEntityMap[i] = i;
	}
	private void createUnitList(){
		PlaySceneBoard.unitEntity = new UnitEntity[unitEntity_n];

		/*final static int ECONOMICALUFO = id4++;
		final static int SPEEDYUFO = id4++;
		final static int SUPERUFO = id4++;*/
		//UnitEntity(String name, ImageIcon icon, int health, int attack, int speed, int costGold, int workerRequired)
		//																																						health		attack	speed	costGold	worker
		PlaySceneBoard.unitEntity[ECONOMICALUFO] = new UnitEntity("Economical UFO",	"./images/UFOs/economicalUFO.png", "./images/GL/UFOs/economicalUFO.png",	20, 		5,		60,		50,			1	);
		PlaySceneBoard.unitEntity[SPEEDYUFO] = new UnitEntity("Speedy UFO", "./images/UFOs/speedyUFO.png", "./images/GL/UFOs/speedyUFO.png",					35, 		10, 	150,	100,		2	);
		PlaySceneBoard.unitEntity[SUPERUFO] = new UnitEntity("Super UFO", "./images/UFOs/superUFO.png", "./images/GL/UFOs/superUFO.png",						100000000, 	3, 		30,		20000,		100	);
	}
	public static int structureNameToID(String name){
		for(int i=0;i<PlaySceneBoard.structureEntity.length;i++){
			if(PlaySceneBoard.structureEntity[i].equals(name))
				return i;
		}
		System.out.println("Should not reach here! in PlaySceneDataInit.structureNameToID()");
		try{
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
}
