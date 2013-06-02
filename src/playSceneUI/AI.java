/*Copyright 2012 Wong Cho Ching, all rights reserved.
 * This file is released under BSD 2-clause License.
 * For details, please read LICENSE.txt in this package.*/

package playSceneUI;

import header.MathSupp;

import java.awt.Point;
import java.lang.reflect.Array;
import java.util.Arrays;

import enums.E;

import GUI.PlayScene;

public class AI {
	long lastUpdated;
	/*int MOREISBETTER = 0x0;
	int NOEXHAUST = 0x1;

	int NOCAP = 0x0; //do not reach cap as possible
	int TOCAP = 0x1; //reach cap as possible
	
	int DELTA = 0x2;
	int EXACT = 0x4;
	
	//				GOLD,				WATER,				POPULATION,	POPULATION_AVAILABLE,
	int[] value = {	MOREISBETTER|DELTA,	NOEXHAUST|EXACT,	TOCAP,		NOEXHAUST|EXACT};*/
	double[] resourcesDelta;
	double[] resourcesCap; //assume that all are not upgrading
	int owner;
	int level;
	int toBuild_upgrade;
	int civilization = 0;
	int updateInterval = 1000;
	int UFOUpdateInterval = 200;

	double varhMultiplier = 1.0f;
	double varlMultiplier = 1.0f;

	double peacefulness = 1.0f;
	int previousStructuresOwned = 0;
	double previousBaseHealth = 0;

	final int hospitalMax_n = 5;
	StructureBuilt[] hospital = new StructureBuilt[hospitalMax_n];
	boolean[] hospitalExist = new boolean[hospitalMax_n];
	double[] healingTimeLeft = new double[hospitalMax_n];
	int hospital_n = 0;
	
	final int UCenterMax_n = 5;
	StructureBuilt[] UCenter = new StructureBuilt[UCenterMax_n];
	boolean[] UCenterExist = new boolean[UCenterMax_n];
	int[] toHospital = new int[UCenterMax_n];
	int UCenter_n = 0;

	final static int EMPTY = 0;
	final static int RAISINGPOPULATION = 1;
	final static int ATTACKING = 2;
	final static int RETREATING = 3;
	final static int HEALING = 4;
	final static int HOUSED = 5;
	int attackStates = EMPTY;
	double secondsPassed = 0;
	public double startAttackSecond = 0;

	double millitaryReserve = 0;

	boolean defensiveStructure = false;
	float[][] clivilizationLevelTable = new float[][]{
			//				Level1	Level2	Level3 , please note that in date, level1 is represented as 0, level2 is represented as 1 and so on.
			new float[]{	1.0f,	0.0f,	0.0f}, //0
			new float[]{	0.5f,	0.5f,	0.0f}, //1
			new float[]{	0.3f,	0.7f,	0.0f}, //2
			new float[]{	0.2f,	0.6f,	0.2f}, //3
			new float[]{	0.0f,	0.6f,	0.4f}, //4
			new float[]{	0.0f,	0.2f,	0.8f}, //5
			new float[]{	0.0f,	0.0f,	1.0f}, //6
			};
	int[][] clivilizationToUFOCenter_n = new int[][]{
			//			level1	level2	level3
			new int[]{	1,		0,		0,}, //0
			new int[]{	2,		0,		0,}, //1
			new int[]{	2,		1,		0,}, //2
			new int[]{	3,		2,		0,}, //3
			new int[]{	1,		3,		1,}, //4
			new int[]{	0,		2,		3,}, //5
			new int[]{	0,		0,		5,}, //6
			};
	double protectionThreshold = 1.0f;
	double[][] protectionMask = new double[PlaySceneBoard.boardW][PlaySceneBoard.boardH];
	int maxLevel = 3;
	
	boolean initializing = true;
	public AI(int id, int level){
		owner = id;
		//PlaySceneBoard.buildStructure(new Point(20, 20), PlaySceneDataInit.MACHINEGUN, owner);
		resourcesDelta = new double[PlaySceneBoard.localResourceSeperator];
		resourcesCap = new double[PlaySceneBoard.localResourceSeperator];
		lastUpdated = PlaySceneBoard.currentTick;
		this.level = level;
		Arrays.fill(UCenter, null);
		Arrays.fill(UCenterExist, false);
		Arrays.fill(hospital, null);
		Arrays.fill(hospitalExist, false);
		
		switch(this.level){
			case 0:
				varhMultiplier = 0.5f;	varlMultiplier = 1.2f;
				startAttackSecond = 90;
				toBuild_upgrade = PlaySceneDataInit.SNIPER; build(22, 27);
				toBuild_upgrade = PlaySceneDataInit.SNIPER; build(26, 23);
				toBuild_upgrade = PlaySceneDataInit.WELL; build(22, 25);
				toBuild_upgrade = PlaySceneDataInit.WELL; build(24, 23);
				toBuild_upgrade = PlaySceneDataInit.HOUSING; build(20, 27);
				toBuild_upgrade = PlaySceneDataInit.HOUSING; build(26, 21);
				toBuild_upgrade = PlaySceneDataInit.WELL; build(18, 27);
				toBuild_upgrade = PlaySceneDataInit.WELL; build(16, 27);
				toBuild_upgrade = PlaySceneDataInit.WELL; build(26, 19);
				toBuild_upgrade = PlaySceneDataInit.WELL; build(26, 17);
				toBuild_upgrade = PlaySceneDataInit.GOLDMINE; build(20, 25);
				toBuild_upgrade = PlaySceneDataInit.GOLDMINE; build(24, 21);
				toBuild_upgrade = PlaySceneDataInit.UCENTER; build(20, 21);
				
				toBuild_upgrade = PlaySceneDataInit.MACHINEGUN; build(14, 17);
				toBuild_upgrade = PlaySceneDataInit.HOUSING; build(12, 17);
				toBuild_upgrade = PlaySceneDataInit.HOUSING; build(16, 17);
				toBuild_upgrade = PlaySceneDataInit.HOUSING; build(14, 19);
				toBuild_upgrade = PlaySceneDataInit.HOUSING; build(14, 15);
			break;
			case 1:
				varhMultiplier = 0.6f;	varlMultiplier = 1.1f;
				startAttackSecond = 120;
				PlaySceneBoard.resourceInitValue[owner][retd(PlaySceneDataInit.POPULATION)] = 30;
				PlaySceneBoard.resourceAmount[owner][retd(PlaySceneDataInit.POPULATION)] = 30;
				PlaySceneBoard.resourceInitCap[owner][retd(PlaySceneDataInit.POPULATION)] = 30;
				PlaySceneBoard.resourceCap[owner][retd(PlaySceneDataInit.POPULATION)] = 30;
				PlaySceneBoard.resourceInitValue[owner][retd(PlaySceneDataInit.POPULATION_AVAILABLE)] = 30;
				PlaySceneBoard.resourceAmount[owner][retd(PlaySceneDataInit.POPULATION_AVAILABLE)] = 30;
				PlaySceneBoard.resourceInitCap[owner][retd(PlaySceneDataInit.POPULATION_AVAILABLE)] = 30;
				PlaySceneBoard.resourceCap[owner][retd(PlaySceneDataInit.POPULATION_AVAILABLE)] = 30;
				
				toBuild_upgrade = PlaySceneDataInit.HOSPITAL; build(22, 23);
				toBuild_upgrade = PlaySceneDataInit.UCENTER; build(20, 21);
				toBuild_upgrade = PlaySceneDataInit.MACHINEGUN; build(22, 21);
				toBuild_upgrade = PlaySceneDataInit.MACHINEGUN; build(20, 23);

				toBuild_upgrade = PlaySceneDataInit.SNIPER; build(22, 27);
				toBuild_upgrade = PlaySceneDataInit.SNIPER; build(26, 23);

				toBuild_upgrade = PlaySceneDataInit.WELL; build(20, 27);
				toBuild_upgrade = PlaySceneDataInit.WELL; build(22, 25);
				toBuild_upgrade = PlaySceneDataInit.WELL; build(24, 23);
				toBuild_upgrade = PlaySceneDataInit.WELL; build(26, 21);

				toBuild_upgrade = PlaySceneDataInit.GOLDMINE; build(20, 25);
				toBuild_upgrade = PlaySceneDataInit.GOLDMINE; build(24, 21);

				toBuild_upgrade = PlaySceneDataInit.MACHINEGUN; build(10, 27);
				toBuild_upgrade = PlaySceneDataInit.SNIPER; build(14, 23);
				toBuild_upgrade = PlaySceneDataInit.MACHINEGUN; build(18, 19);
				toBuild_upgrade = PlaySceneDataInit.SNIPER; build(16, 17);
				toBuild_upgrade = PlaySceneDataInit.SNIPER; build(22, 15);
				toBuild_upgrade = PlaySceneDataInit.MACHINEGUN; build(26, 11);

				toBuild_upgrade = PlaySceneDataInit.HOUSING; build(8, 27);
				toBuild_upgrade = PlaySceneDataInit.HOUSING; build(10, 25);
				toBuild_upgrade = PlaySceneDataInit.HOUSING; build(12, 23);
				toBuild_upgrade = PlaySceneDataInit.HOUSING; build(14, 21);
				toBuild_upgrade = PlaySceneDataInit.HOUSING; build(16, 19);
				toBuild_upgrade = PlaySceneDataInit.HOUSING; build(18, 17);
				toBuild_upgrade = PlaySceneDataInit.HOUSING; build(20, 15);
				toBuild_upgrade = PlaySceneDataInit.HOUSING; build(22, 13);
				toBuild_upgrade = PlaySceneDataInit.HOUSING; build(24, 11);
				toBuild_upgrade = PlaySceneDataInit.HOUSING; build(26, 9);
			break;
			case 2:
				varhMultiplier = 1.1f;	varlMultiplier = 0.9f;
				startAttackSecond = 75;

				PlaySceneBoard.resourceInitValue[owner][retd(PlaySceneDataInit.POPULATION)] = 120;
				PlaySceneBoard.resourceAmount[owner][retd(PlaySceneDataInit.POPULATION)] = 120;
				PlaySceneBoard.resourceInitCap[owner][retd(PlaySceneDataInit.POPULATION)] = 120;
				PlaySceneBoard.resourceCap[owner][retd(PlaySceneDataInit.POPULATION)] = 120;
				PlaySceneBoard.resourceInitValue[owner][retd(PlaySceneDataInit.POPULATION_AVAILABLE)] = 120;
				PlaySceneBoard.resourceAmount[owner][retd(PlaySceneDataInit.POPULATION_AVAILABLE)] = 120;
				PlaySceneBoard.resourceInitCap[owner][retd(PlaySceneDataInit.POPULATION_AVAILABLE)] = 120;
				PlaySceneBoard.resourceCap[owner][retd(PlaySceneDataInit.POPULATION_AVAILABLE)] = 120;

				toBuild_upgrade = PlaySceneDataInit.WELL; build(22, 25);
				toBuild_upgrade = PlaySceneDataInit.WELL; build(22, 27);
				toBuild_upgrade = PlaySceneDataInit.WELL; build(20, 27);
				toBuild_upgrade = PlaySceneDataInit.WELL; build(18, 27);

				toBuild_upgrade = PlaySceneDataInit.SNIPER; build(16, 27);
				toBuild_upgrade = PlaySceneDataInit.SNIPER; build(16, 25);
				toBuild_upgrade = PlaySceneDataInit.SNIPER; build(16, 23);
				toBuild_upgrade = PlaySceneDataInit.SNIPER; build(18, 25);

				toBuild_upgrade = PlaySceneDataInit.BANK; build(16, 21);
				toBuild_upgrade = PlaySceneDataInit.BANK; build(16, 19);
				toBuild_upgrade = PlaySceneDataInit.BANK; build(16, 17);
				toBuild_upgrade = PlaySceneDataInit.BANK; build(18, 17);

				toBuild_upgrade = PlaySceneDataInit.SPLASHTOWER; build(18, 19);
				toBuild_upgrade = PlaySceneDataInit.SPLASHTOWER; build(18, 21);
				toBuild_upgrade = PlaySceneDataInit.SPLASHTOWER; build(20, 19);
				toBuild_upgrade = PlaySceneDataInit.SPLASHTOWER; build(20, 21);

				toBuild_upgrade = PlaySceneDataInit.GOLDMINE; build(20, 17);
				toBuild_upgrade = PlaySceneDataInit.GOLDMINE; build(22, 17);
				toBuild_upgrade = PlaySceneDataInit.GOLDMINE; build(24, 17);
				toBuild_upgrade = PlaySceneDataInit.GOLDMINE; build(26, 17);

				toBuild_upgrade = PlaySceneDataInit.MACHINEGUN; build(24, 19);
				toBuild_upgrade = PlaySceneDataInit.MACHINEGUN; build(26, 19);
				toBuild_upgrade = PlaySceneDataInit.MACHINEGUN; build(26, 21);
				toBuild_upgrade = PlaySceneDataInit.MACHINEGUN; build(26, 23);

				toBuild_upgrade = PlaySceneDataInit.HOUSING; build(22, 19);
				toBuild_upgrade = PlaySceneDataInit.HOUSING; build(22, 21);
				toBuild_upgrade = PlaySceneDataInit.HOUSING; build(24, 21);
				toBuild_upgrade = PlaySceneDataInit.HOUSING; build(24, 23);

				toBuild_upgrade = PlaySceneDataInit.HOSPITAL; build(22, 23);
				toBuild_upgrade = PlaySceneDataInit.HOSPITAL; build(20, 23);
				toBuild_upgrade = PlaySceneDataInit.HOSPITAL; build(18, 23);
				toBuild_upgrade = PlaySceneDataInit.HOSPITAL; build(20, 25);

			break;
		}
		initializing = false;
	}

	private Point getLeftTopPos(int x, int y){
		while(x>0&&y<PlaySceneBoard.boardH){
			x--;y++;
		} //NEVER remove this curve bracket! Otherwise, it will be interpreted as that "y++;" is not inside the while loop.
		x++;y--;
		return new Point(x,y);
	}
	private Point getFrontLine(){
		int x = PlaySceneBoard.boardW-1;
		int y = PlaySceneBoard.boardH-1;
		int direction = -1;
		while(!(outBound(x-1,y-1)&outBound(x-1,y)&outBound(x,y-1))){
			boolean changeDirection = false;
			if(outBound(x,y-1)){ // hit the bottom
				x--; direction = LU;
				changeDirection = true;
			}else if(outBound(x-1,y)){ //hit the left
				y--; direction = RD;
				changeDirection = true;
			}else if(outBound(x,y+1)){ // hit the top
				x--; direction = RD;
				changeDirection = true;
			}else if(outBound(x+1,y)){ // hit the right
				y--; direction = LU;
				changeDirection = true;
			}
			if(changeDirection){
				if(!colli(x,y))
					return getLeftTopPos(x,y);
			}
			switch(direction){
				case LU:
					x--; y++;
				break;
				case RD:
					x++; y--;
				break;
			}
			if(!colli(x,y))
				return getLeftTopPos(x,y);
		}
		return new Point(0,0);
	}

	private boolean build(int x, int y){
		if(toBuild_upgrade<=-1)
			return false;
		boolean horOK = false;
		boolean verOK = false;
		if(!initializing){
			if(Math.random()<0.8f&&!defensiveStructure&&toBuild_upgrade!=PlaySceneDataInit.UCENTER)
				return false;
			int width = PlaySceneBoard.structureEntity[toBuild_upgrade].width;
			int height = PlaySceneBoard.structureEntity[toBuild_upgrade].height;
			for(int i=0;i<width;i++){ // check blocking
				for(int j=0;j<height;j++){
					if(colli(x+i,y+j)){
						return false;
					}
				}
			}
			for(int i=0;i<width;i++){
				if(colli(x+i,y+height)){
					horOK = true;
					break;
				}
			}
			for(int i=0;i<height;i++){
				if(colli(x+width,y+i)){
					verOK = true;
					break;
				}
			}
		}else{
			horOK = true;
			verOK = true;
		}
		if(toBuild_upgrade==PlaySceneDataInit.HOSPITAL){
			int w = PlaySceneBoard.structureEntity[toBuild_upgrade].width;
			int h = PlaySceneBoard.structureEntity[toBuild_upgrade].height;
			if(outBound(x,y-1)||outBound(x,y+h+1)||outBound(x+w+1,y)||outBound(x-1,y)) //When AI path to a hospital, it enclose the hospital. This line is done for avoiding UFO out of bound, which looks strange.
				return false;
		}
		if(horOK&&verOK){
			if(PlaySceneBoard.buildStructure(new Point(x, y), toBuild_upgrade, owner)){
				if(PlaySceneBoard.currentPlayer==owner&&PlaySceneBoard.selectedStructure[owner]>-1)
					PlaySceneBoard.structureEntity[toBuild_upgrade].updateUI(PlaySceneBoard.structureBuilt[PlaySceneBoard.selectedStructure[owner]].currentLevel, StructureEntity.NORMAL, owner);
				if(toBuild_upgrade==PlaySceneDataInit.HOSPITAL){
					for(int i=0;i<hospitalMax_n;i++){
						if(!hospitalExist[i]){
							hospital[i] = PlaySceneBoard.structureBuilt[PlaySceneBoard.selectedStructure[owner]];
							hospitalExist[i] = true;
							hospital_n++;
							break;
						}
					}
				}else if(toBuild_upgrade==PlaySceneDataInit.UCENTER&&PlaySceneBoard.selectedStructure[owner]>-1){
					if(PlaySceneBoard.structureBuilt[PlaySceneBoard.selectedStructure[owner]].type==PlaySceneDataInit.UCENTER){
						for(int i=0;i<UCenterMax_n;i++){
							if(!UCenterExist[i]){
								UCenter[i] = PlaySceneBoard.structureBuilt[PlaySceneBoard.selectedStructure[owner]];
								UCenterExist[i] = true;
								UCenter_n++;
								break;
							}
						}
					}
				}
				toBuild_upgrade = -1;
				return true;
			}else if(!PlaySceneBoard.checkBuildLimit(toBuild_upgrade, owner)){
				toBuild_upgrade = -1;
				return true;
			}
		}
		return false;
	}

	private void upgrade(int toLevel){
		if(toLevel>0){
			int n=1;
			boolean done = false;
			while(toLevel-n>=0&&!done){
				for(int i=0;i<PlaySceneBoard.structureBuilt_n;i++){
					StructureBuilt b = PlaySceneBoard.structureBuilt[i];
					if(b.owner==owner&&b.type==toBuild_upgrade){
						if(b.currentLevel==toLevel-n){
							boolean upgradeSuccess = b.upgrade();
							if(upgradeSuccess){
								toBuild_upgrade = -1;
								return;
							}/*else if(toBuild_upgrade==PlaySceneDataInit.UCENTER){ //We want UCenter to be upgrade to exact the level given, without trying to upgrade it to a lower level.
								return;
							}*/
						}
					}
				}
				n++;
			}
		}
		int x = PlaySceneBoard.boardW-1;
		int y = PlaySceneBoard.boardH-1;
		int direction = -1;
		/*if(defensiveStructure){
			Point originalNode = getFrontLine();
			Point node = new Point(originalNode.x, originalNode.y);
			double[] protectionMaskFrontLine = new double[100];
			int offset = 0;
			while(node.x<PlaySceneBoard.boardW&&node.y>0){
//System.out.println((node.x<PlaySceneBoard.boardW)+","+(node.y>0)+"  where x="+node.x);
				protectionMaskFrontLine[offset++] = protectionMask[node.x][node.y];
				node.x++;node.y--;
			}
			node.x--;node.y++;
			int start = 0;
			int end = offset;
			int blockedInARow = 0;
			for(int i=0;i<offset;i++){
				if(protectionMaskFrontLine[i]<protectionThreshold){
					if(start==-1)
						start = i;
					blockedInARow = 0;
				}else{
					blockedInARow++;
					if(start!=-1&&blockedInARow>3){
						end = i;
						break;
					}
				}
			}
			x = originalNode.x+(end-start)/2;
			y = originalNode.y-(end-start)/2;
System.out.println(x+","+y);
			direction = Math.random()>0.5f?RD:LU;
		}else{*/
			x = PlaySceneBoard.boardW-1;
			y = PlaySceneBoard.boardH-1;
			direction = -1;
		//}
		/*
		 * 621
		 * 753
		 * 984 
		 * 
		 * !If hit bottom, go left then left up
		 * !If hit left, go down then right down
		 * If hit right, go down then left up
		 * If hit top, go left then right down
		 * 		left	down
		 * LU	down	right
		 * RD	top		left
		 */
		while(!(outBound(x-1,y-1)&outBound(x-1,y)&outBound(x,y-1))){
			boolean changeDirection = false;
			if(outBound(x,y-1)){ // hit the bottom
				x--; direction = LU;
				changeDirection = true;
			}else if(outBound(x-1,y)){ //hit the left
				y--; direction = RD;
				changeDirection = true;
			}else if(outBound(x,y+1)){ // hit the top
				x--; direction = RD;
				changeDirection = true;
			}else if(outBound(x+1,y)){ // hit the right
				y--; direction = LU;
				changeDirection = true;
			}
			if(changeDirection)
				if(build(x,y))
					return;
			switch(direction){
				case LU:
					x--; y++;
				break;
				case RD:
					x++; y--;
				break;
			}
			if(build(x,y))
				return;
		}
	}
	
	private void processBuildingOrUpgrading(){
		int[] structureOfThisTypeBuilt_n = new int[maxLevel];
		int structureOfThisTypeBuiltTotal_n = 0;
		for(int i=0;i<PlaySceneBoard.structureBuilt_n;i++){
			StructureBuilt b = PlaySceneBoard.structureBuilt[i];
			if(b.owner==owner&&b.type==toBuild_upgrade){
				structureOfThisTypeBuilt_n[b.currentLevel]++;
				structureOfThisTypeBuiltTotal_n++;
			}
		}
		if(structureOfThisTypeBuiltTotal_n>0&&toBuild_upgrade!=PlaySceneDataInit.UCENTER){
			int[] goalValue = new int[maxLevel];
			int allocationLeft = structureOfThisTypeBuiltTotal_n;
			for(int i=0;i<maxLevel;i++){
				int closest = MathSupp.closest(structureOfThisTypeBuiltTotal_n, clivilizationLevelTable[civilization][i]);
				allocationLeft -= closest;
				goalValue[i] = closest;
			}
			goalValue[0] += allocationLeft;
			for(int i=0;i<maxLevel;i++){
				if(structureOfThisTypeBuilt_n[i]<goalValue[i]){
					upgrade(i);
					return;
				}
			}
			upgrade(0);
		}else{
			for(int i=0;i<maxLevel;i++){
//System.out.println(structureOfThisTypeBuilt_n[i]+"<"+clivilizationToUFOCenter_n[civilization][i]);
				if(structureOfThisTypeBuilt_n[i]<clivilizationToUFOCenter_n[civilization][i]){
					upgrade(i);
					return;
				}
			}
			toBuild_upgrade = -1;
			selectAndBuild();
		}
	}
	
	final static int RD = 0; //moving to right down
	final static int LU = 1; //moving to left up

	private void selectAndBuild(){
		chooseStructure();
System.out.println(toBuild_upgrade);
		if(toBuild_upgrade!=-1&&PlaySceneBoard.currentTick>suspenseTick+suspenseDuration)
			processBuildingOrUpgrading();
	}
	
	long suspenseTick = 0;
	long suspenseDuration = 10000; //10 second
	public void processAI(){
		calculateCivilization();
		calculatePossiblyHospitalAndUCenterDestructed();
		if(secondsPassed>startAttackSecond&&PlaySceneBoard.currentTick-lastUpdated>UFOUpdateInterval)
			calculateAttacks();
		else
			secondsPassed += PlaySceneBoard.frameAdvance/PlaySceneBoard.fps;
		if(PlaySceneBoard.currentTick-lastUpdated>updateInterval){
			calculateResourcesDelta();
			calculateProtectedMask();
			calculatePeacefulness();
			calculateProtectionThreshold();
			raiseMillitaryReserve();
			selectAndBuild();
			previousStructuresOwned = 0;
			for(int i=0;i<PlaySceneBoard.structureBuilt_n;i++){
				StructureBuilt b = PlaySceneBoard.structureBuilt[i];
				if(b.owner==owner)
					previousStructuresOwned++;
			}
			lastUpdated = PlaySceneBoard.currentTick;
		}
	}
	double populationShouldHave = 0;
	private void chooseStructure(){
		defensiveStructure = false;
		if(Math.random()>peacefulness)
			toBuild_upgrade = -1;
//System.out.println(populationShouldHave);
		if(resourcesDelta[retd(PlaySceneDataInit.WATER)]<=100){
			toBuild_upgrade = PlaySceneDataInit.WELL;
		}else if(populationShouldHave<resourcesCap[retd(PlaySceneDataInit.POPULATION)]*0.3f){
			toBuild_upgrade = PlaySceneDataInit.HOUSING;
		}else if(toBuild_upgrade==-1){
			double saveGold = 100+millitaryReserve;
			for(int i=0;i<PlaySceneBoard.structureBuilt_n;i++){
				StructureBuilt b = PlaySceneBoard.structureBuilt[i];
				if(b.owner==owner&&b.type==PlaySceneDataInit.GOLDMINE){
					StructureEntity e = PlaySceneBoard.structureEntity[b.type];
					saveGold += e.value[e.getPropertyIDByResourceEnum(PlaySceneDataInit.GOLD)][b.currentLevel]*10;
				}
			}
			saveGold *= peacefulness;
			if(PlaySceneBoard.resourceAmount[owner][retd(PlaySceneDataInit.GOLD)]>=resourcesCap[retd(PlaySceneDataInit.GOLD)]*0.9f&&
				resourcesCap[retd(PlaySceneDataInit.GOLD)]<saveGold*6){
					toBuild_upgrade = PlaySceneDataInit.BANK;
			}else{
				int lackDefense_n = 0;
				for(int n=0;n<PlaySceneBoard.structureBuilt_n;n++){
					StructureBuilt b = PlaySceneBoard.structureBuilt[n];
					if(b.owner==owner&&protectionMask[b.pos.x][b.pos.y]<protectionThreshold){
						lackDefense_n++;
					}
				}
	
				if(lackDefense_n>5){
					if(civilization<1)
						toBuild_upgrade = PlaySceneDataInit.SNIPER;
					else if(civilization<3)
						toBuild_upgrade = Math.random()>0.5f?PlaySceneDataInit.SNIPER:PlaySceneDataInit.MACHINEGUN;
					else if(civilization<5)
						toBuild_upgrade = Math.random()>0.8f?PlaySceneDataInit.SNIPER:PlaySceneDataInit.MACHINEGUN;
					else if(civilization<6){
						double rand = Math.random();
						if(rand<0.15f)
							toBuild_upgrade = PlaySceneDataInit.SNIPER;
						else if(rand<0.7f)
							toBuild_upgrade = PlaySceneDataInit.MACHINEGUN;
						else
							toBuild_upgrade = PlaySceneDataInit.SPLASHTOWER;
					}else{
						double rand = Math.random();
						if(rand<0.1f)
							toBuild_upgrade = PlaySceneDataInit.SNIPER;
						else if(rand<0.5f)
							toBuild_upgrade = PlaySceneDataInit.MACHINEGUN;
						else
							toBuild_upgrade = PlaySceneDataInit.SPLASHTOWER;
					}
					defensiveStructure = true;
				}else{
					if(PlaySceneBoard.resourceAmount[owner][retd(PlaySceneDataInit.GOLD)]>saveGold){
						double rand = Math.random();
						if(rand<0.05f&&civilization>=3&&hospital_n<hospitalMax_n){
							toBuild_upgrade = PlaySceneDataInit.HOSPITAL;
						}else{
							toBuild_upgrade = PlaySceneDataInit.GOLDMINE;
						}
					}
				}
			}
		}
		if(suspenseTick==0&&toBuild_upgrade!=PlaySceneDataInit.WELL&&toBuild_upgrade!=PlaySceneDataInit.HOUSING&&
				PlaySceneBoard.resourceAmount[owner][retd(PlaySceneDataInit.POPULATION)]<resourcesCap[retd(PlaySceneDataInit.POPULATION)]-5)
			suspenseTick = PlaySceneBoard.currentTick;
		else
			suspenseTick = 0;
	}

	private boolean colli(int x, int y){ //check collision.
		if(outBound(x,y))
			return true;
		return PlaySceneBoard.structureMask[x][y]>-1?true:false;
	}

	private boolean outBound(int x, int y){ //check out of bound
		if(x<0||y<0||x>=PlaySceneBoard.boardW||y>=PlaySceneBoard.boardH)
			return true;
		return false;
	}

	private void calculateResourcesDelta(){
		for(int i=0;i<resourcesDelta.length;i++){
			resourcesDelta[i] = 0;
			resourcesCap[i] = PlaySceneBoard.resourceInitCap[owner][i];
		}
		populationShouldHave = PlaySceneBoard.resourceInitValue[owner][retd(PlaySceneDataInit.POPULATION)];
		for(int i=0;i<PlaySceneBoard.structureBuilt_n;i++){
			StructureBuilt b = PlaySceneBoard.structureBuilt[i];
			if(b.owner==owner){
				StructureEntity e = PlaySceneBoard.structureEntity[b.type];
				for(int j=0;j<e.propertiesType.length;j++){
					if((e.propertiesType[j]&PlaySceneDataInit.CONTINOUS)!=0){
						double temp = 0;
						if((e.propertiesType[j]&PlaySceneDataInit.ADD)!=0)
							temp += e.value[j][b.currentLevel];
						else if((e.propertiesType[j]&PlaySceneDataInit.DEDUCT)!=0)
							temp -= e.value[j][b.currentLevel];
						if((e.propertiesType[j]&PlaySceneDataInit.EACH)!=0){
							temp *= e.value[e.getPropertyIntCapIDByResourceEnum(e.propertiesParameter[j])][b.currentLevel];//b.valueCap[e.getPropertyIDByResourceEnum(e.propertiesParameter[j])];
						}
						if((e.propertiesType[j]&PlaySceneDataInit.VARH)!=0)
							temp *= varhMultiplier;
						else if((e.propertiesType[j]&PlaySceneDataInit.VARL)!=0)
							temp *= varlMultiplier;
						resourcesDelta[retd(e.propertiesEnum[j])] += temp;
					}
					if((e.propertiesType[j]&PlaySceneDataInit.ATEXTCAP)!=0){
						double temp = 0;
						if((e.propertiesType[j]&PlaySceneDataInit.ADD)!=0)
							temp += e.value[j][b.currentLevel];
						else if((e.propertiesType[j]&PlaySceneDataInit.DEDUCT)!=0)
							temp -= e.value[j][b.currentLevel];
						if((e.propertiesType[j]&PlaySceneDataInit.EACH)!=0){
							temp *= b.currentValue[e.getPropertyIDByResourceEnum(e.propertiesParameter[j])];
						}
						resourcesCap[retd(e.propertiesEnum[j])] += temp;
					}
				}
				if(e.getPropertyIDByResourceEnum(PlaySceneDataInit.POPULATION_AVAILABLE)!=-1)
					populationShouldHave -= e.value[e.getPropertyIDByResourceEnum(PlaySceneDataInit.POPULATION_AVAILABLE)][b.currentLevel];
				if(b.type==PlaySceneDataInit.HOUSING){
						populationShouldHave += e.value[e.getPropertyIntCapIDByResourceEnum(PlaySceneDataInit.POPULATION)][b.currentLevel];
				}else if(b.type==PlaySceneDataInit.UCENTER){
					for(int t=0;t<PlaySceneBoard.unitEntity.length;t++)
						populationShouldHave -= PlaySceneBoard.unitEntity[t].workerRequired*b.memory[e.getMemoryIdByName("unit_n")][t];
				}
			}
		}
		populationShouldHave -= populationRequiredForUFO;
	}
	
	private void calculateProtectedMask(){
		for(int i=0;i<PlaySceneBoard.boardW;i++){
			for(int j=0;j<PlaySceneBoard.boardH;j++){
				protectionMask[i][j] = 0;
				for(int n=0;n<PlaySceneBoard.structureBuilt_n;n++){
					StructureBuilt b = PlaySceneBoard.structureBuilt[n];
					if(b.owner==owner){
						StructureEntity e = PlaySceneBoard.structureEntity[b.type];
						if((e.category&E.DEFENSIVE)!=0){
							protectionMask[i][j] += Math.max(0, e.value[e.getPropertyIDByResourceEnum(PlaySceneDataInit.ATTACK)][b.currentLevel]*
									e.value[e.getPropertyIDByResourceEnum(PlaySceneDataInit.RATE)][b.currentLevel]*
									(e.value[e.getPropertyIDByResourceEnum(PlaySceneDataInit.RANGE)][b.currentLevel]-MathSupp.distance(i,j,b.pos.x+e.width/2,b.pos.y+e.height/2))
									);
						}
					}
				}
			}
		}
	}
	
	private void calculateCivilization(){
		double pop = PlaySceneBoard.resourceCap[owner][retd(PlaySceneDataInit.POPULATION_AVAILABLE)];
		if(pop<50)
			civilization = 0;
		else if(pop<100)
			civilization = 1;
		else if(pop<200)
			civilization = 2;
		else if(pop<300&&level>=1)
			civilization = 3;
		else if(pop<400&&level>=1)
			civilization = 4;
		else if(pop<500&&level>=2)
			civilization = 5;
		else
			civilization = 6;
	}
	
	final double baseUCenterRiskValue = 300.0f;
	private void calculateProtectionThreshold(){
		protectionThreshold = 1.0f;
		int UFOCenter_n = 0;
		for(int i=0;i<PlaySceneBoard.structureBuilt_n;i++){
			StructureBuilt b = PlaySceneBoard.structureBuilt[i];
			if(b.owner!=owner&&b.type==PlaySceneDataInit.UCENTER){
				switch(b.currentLevel){
					case 0: protectionThreshold += baseUCenterRiskValue; break;
					case 1: protectionThreshold += baseUCenterRiskValue*5; break; //Max: 6x
					case 2: protectionThreshold += baseUCenterRiskValue*200; break; //Max: 400x
				}
				UFOCenter_n++;
			}
		}
		protectionThreshold *= UFOCenter_n;
		for(int n=0;n<PlaySceneBoard.structureBuilt_n;n++){
			StructureBuilt b = PlaySceneBoard.structureBuilt[n];
			if(b.type==PlaySceneDataInit.BASE){
				StructureEntity e = PlaySceneBoard.structureEntity[b.type];
				protectionThreshold *= Math.pow(e.value[e.getPropertyIDByResourceEnum(PlaySceneDataInit.HEALTH)][b.currentLevel]/b.currentValue[e.getPropertyIDByResourceEnum(PlaySceneDataInit.HEALTH)],2);
			}
		}
		protectionThreshold /= peacefulness;
	}
	
	private void calculatePossiblyHospitalAndUCenterDestructed(){
		for(int i=0;i<hospitalMax_n;i++){
			if(hospitalExist[i]){
				if(hospital[i]!=null&&hospital[i].exist){
					hospitalExist[i] = true;
				}else{
					hospitalExist[i] = false;
					hospital_n--;
				}
			}
		}
		for(int i=0;i<UCenterMax_n;i++){
			if(UCenterExist[i]){
				if(UCenter[i]!=null&&UCenter[i].exist){
					UCenterExist[i] = true;
				}else{
					UCenterExist[i] = false;
					UCenter_n--;
				}
			}
		}
	}

	private void calculatePeacefulness(){
		int currentStructureOwned = 0;
		double baseHealth = 0;
		for(int i=0;i<PlaySceneBoard.structureBuilt_n;i++){
			StructureBuilt b = PlaySceneBoard.structureBuilt[i];
			if(b.owner==owner){
				currentStructureOwned++;
				if(b.type==PlaySceneDataInit.BASE){
					StructureEntity e = PlaySceneBoard.structureEntity[b.type];
					baseHealth = b.currentValue[e.getPropertyIDByResourceEnum(PlaySceneDataInit.HEALTH)];
				}
			}
		}
		if(currentStructureOwned<previousStructuresOwned)
			peacefulness *= Math.pow(0.85f, previousStructuresOwned-currentStructureOwned);
		else
			peacefulness += 0.01f;
		if(baseHealth<previousBaseHealth)
			peacefulness = 0.000000001f;
		if(peacefulness>1.0f)
			peacefulness = 1.0f;
		previousBaseHealth = baseHealth;
	}

	int[] toAttackUnit = new int[3];
	int populationRequiredForUFO = 0;
	double costToBeIncurredForUFO = 0;
	double extraPower = 1.0f;
	private boolean decideAttack(){
		double originalMillitaryReserve = millitaryReserve;
		int[] limit = new int[3];
		populationRequiredForUFO = 0;
		Arrays.fill(toAttackUnit, 0);
		for(int i=0;i<UCenterMax_n;i++){
			if(UCenterExist[i]){
				for(int j=0;j<=UCenter[i].currentLevel;j++){
					limit[j] += PlaySceneBoard.structureEntity[PlaySceneDataInit.UCENTER].value[PlaySceneBoard.structureEntity[PlaySceneDataInit.UCENTER].getPropertyIDByResourceEnum(PlaySceneDataInit.UNITCAP)][UCenter[i].currentLevel];
					int currentlyHoused = UCenter[i].memory[PlaySceneBoard.structureEntity[PlaySceneDataInit.UCENTER].getMemoryIdByName("unit_n")][j];
					toAttackUnit[j] += currentlyHoused;
					limit[j] -= currentlyHoused;
				}
			}
		}
		/*int t = 0;
		int weakest = 0;
		boolean done = false;
		while(!done){ //attackStates
			while(toAttackUnit[t]<limit[t]&&!done){
				if(t>weakest){
					millitaryReserve += PlaySceneBoard.unitEntity[weakest].costGold;
					populationRequiredForUFO -= PlaySceneBoard.unitEntity[weakest].workerRequired;
					toAttackUnit[weakest]--;
				}
				millitaryReserve -= PlaySceneBoard.unitEntity[t].costGold;
				populationRequiredForUFO += PlaySceneBoard.unitEntity[t].workerRequired;
				toAttackUnit[t]++;
				if(millitaryReserve<=0){
					if(t>weakest){
						millitaryReserve -= PlaySceneBoard.unitEntity[weakest].costGold;
						populationRequiredForUFO += PlaySceneBoard.unitEntity[weakest].workerRequired;
						toAttackUnit[weakest]++;
					}
					millitaryReserve += PlaySceneBoard.unitEntity[t].costGold;
					populationRequiredForUFO -= PlaySceneBoard.unitEntity[t].workerRequired;
					toAttackUnit[t]--;
					done = true;
				}
				if(toAttackUnit[weakest]<0)
					weakest++;
			}
			t++;
			if(t>=3)
				break;
		}*/
		int t = 2;
		boolean done = false;
		while(!done){ //attackStates
			while(toAttackUnit[t]<limit[t]&&!done){
				if(millitaryReserve-PlaySceneBoard.unitEntity[t].costGold>0){
					millitaryReserve -= PlaySceneBoard.unitEntity[t].costGold;
					populationRequiredForUFO += PlaySceneBoard.unitEntity[t].workerRequired;
					toAttackUnit[t]++;
				}else{
					break;
				}
			}
			t--;
			if(t<0)
				break;
		}
		double unitPower = baseUCenterRiskValue;
		double ourPower = toAttackUnit[0]*unitPower+
				toAttackUnit[1]*unitPower*4f+
				toAttackUnit[2]*unitPower*5000f;
		ourPower *= Math.pow(UCenter_n, 0.8f);
		
		double theirPower = 0.0f; 
		int theirStructure_n = 0;
		int theirDefensiveStructure_n = 0;
		int theirWall_n = 0;
		for(int n=0;n<PlaySceneBoard.structureBuilt_n;n++){
			StructureBuilt b = PlaySceneBoard.structureBuilt[n];
			if(b.owner!=owner){
				StructureEntity e = PlaySceneBoard.structureEntity[b.type];
				if((e.category&E.DEFENSIVE)!=0){
					if(MathSupp.distance(PlaySceneBoard.structureEntity[PlaySceneDataInit.BASE].width,PlaySceneBoard.structureEntity[PlaySceneDataInit.BASE].height,
							b.pos.x+e.width/2,b.pos.y+e.height/2)<e.value[e.getPropertyIDByResourceEnum(PlaySceneDataInit.RANGE)][b.currentLevel]){
						theirPower += e.value[e.getPropertyIDByResourceEnum(PlaySceneDataInit.ATTACK)][b.currentLevel]*
								e.value[e.getPropertyIDByResourceEnum(PlaySceneDataInit.RATE)][b.currentLevel]*
								e.value[e.getPropertyIDByResourceEnum(PlaySceneDataInit.RANGE)][b.currentLevel]*
								e.value[e.getPropertyIDByResourceEnum(PlaySceneDataInit.HEALTH)][b.currentLevel]/600;
					}else{
						theirPower += e.value[e.getPropertyIDByResourceEnum(PlaySceneDataInit.ATTACK)][b.currentLevel]*
								e.value[e.getPropertyIDByResourceEnum(PlaySceneDataInit.RATE)][b.currentLevel]*
								e.value[e.getPropertyIDByResourceEnum(PlaySceneDataInit.RANGE)][b.currentLevel]*
								e.value[e.getPropertyIDByResourceEnum(PlaySceneDataInit.HEALTH)][b.currentLevel]/900;
					}
					theirDefensiveStructure_n++;
				}
				theirStructure_n++;
				if(b.type==PlaySceneDataInit.HOUSING)
					theirWall_n++;
			}
		}
		theirPower *= (Math.pow((double)theirDefensiveStructure_n, 1.5f)+theirWall_n)/(double)Math.max(1 ,(theirStructure_n-theirDefensiveStructure_n-theirWall_n));
		theirPower *= extraPower;
System.out.println(ourPower+" vs "+theirPower+"( extra:"+extraPower+" $$:"+millitaryReserve+" Def_n"+theirDefensiveStructure_n+" st_n:"+theirStructure_n+")");
System.out.println("nyan: "+UCenter_n);
	if(millitaryReserve>0){
			if(ourPower>theirPower){
				costToBeIncurredForUFO = originalMillitaryReserve-millitaryReserve;
				millitaryReserve = originalMillitaryReserve;
				return true;
			}else{
				toBuild_upgrade = PlaySceneDataInit.UCENTER;
			}
		}
		millitaryReserve = originalMillitaryReserve;
		return false;
	}

	private void raiseMillitaryReserve(){
		if(secondsPassed>startAttackSecond){
			switch(civilization){
				case 0: millitaryReserve += resourcesDelta[retd(PlaySceneDataInit.GOLD)]*0.4f; break;
				case 1: millitaryReserve += resourcesDelta[retd(PlaySceneDataInit.GOLD)]*0.5f; break;
				case 2: millitaryReserve += resourcesDelta[retd(PlaySceneDataInit.GOLD)]*0.6f; break;
				case 3: millitaryReserve += resourcesDelta[retd(PlaySceneDataInit.GOLD)]*0.7f; break;
				case 4: millitaryReserve += resourcesDelta[retd(PlaySceneDataInit.GOLD)]*0.8f; break;
				case 5: millitaryReserve += resourcesDelta[retd(PlaySceneDataInit.GOLD)]*0.9f; break;
				case 6: millitaryReserve += resourcesDelta[retd(PlaySceneDataInit.GOLD)]*0.95f; break;
			}
			if(peacefulness<0.3f){
				millitaryReserve = 0;
			}else if(millitaryReserve>PlaySceneBoard.resourceAmount[owner][retd(PlaySceneDataInit.GOLD)]){
				millitaryReserve = PlaySceneBoard.resourceAmount[owner][retd(PlaySceneDataInit.GOLD)]*0.8f;
			}else if(millitaryReserve<0){
				millitaryReserve = 0;
			}
		}
	}
	
	boolean waitingToRaisePopulation = false;
	int originalUnits_n;
	private void calculateAttacks(){
		if(attackStates==EMPTY||attackStates==HOUSED||attackStates==HEALING){ //check whether AI should attack or not
/*for(int i=0;i<UCenterMax_n;i++){
	StructureEntity e = PlaySceneBoard.structureEntity[PlaySceneDataInit.UCENTER];
	if(UCenterExist[i]){
System.out.println(UCenter[i].memory[e.getMemoryIdByName("attacking")][0]==E.IDLE);
	}
}*/
			if(decideAttack()){
				if(attackStates==HEALING){
					waitingToRaisePopulation = true;
					for(int i=0;i<UCenterMax_n;i++){
						if(UCenterExist[i])
							PlaySceneBoard.structureEntity[PlaySceneDataInit.UCENTER].retreat(UCenter[i].id);
					}
					attackStates = RETREATING;
				}else{
					boolean idle = true;
					for(int i=0;i<UCenterMax_n;i++){
						StructureEntity e = PlaySceneBoard.structureEntity[PlaySceneDataInit.UCENTER];
						if(UCenterExist[i]){
							if(UCenter[i].memory[e.getMemoryIdByName("attacking")][0]!=E.IDLE){
								idle = false;
								break;
							}
						}
					}
					if(idle)
						attackStates = RAISINGPOPULATION;
				}
			}
		}
		if(attackStates==RAISINGPOPULATION){
			if(millitaryReserve<costToBeIncurredForUFO)
				attackStates = HOUSED;
		}
		if(attackStates==RAISINGPOPULATION){ //check whether raising population is finished
System.out.println(populationRequiredForUFO);
			if(PlaySceneBoard.resourceAmount[owner][retd(PlaySceneDataInit.POPULATION_AVAILABLE)]>=populationRequiredForUFO){
				int total = toAttackUnit[0]+toAttackUnit[1]+toAttackUnit[2];
				int t = 0;
				while(total>0){
					while(toAttackUnit[t]>0){
						for(int i=0;i<UCenterMax_n;i++){
							if(UCenterExist[i]){
								if(UCenter[i].currentLevel>=t){
									PlaySceneBoard.selectedStructure[owner] = UCenter[i].id;
									PlaySceneBoard.structureEntity[PlaySceneDataInit.UCENTER].buyUFO(UCenter[i], t, owner, StructureEntity.NORMAL);
									toAttackUnit[t]--;
									total--;
								}
							}
						}
					}
					t++;
				}
				originalUnits_n = 0;
				for(int i=0;i<UCenterMax_n;i++){
					if(UCenterExist[i]){
						PlaySceneBoard.structureEntity[PlaySceneDataInit.UCENTER].createPath(UCenter[i]);
						UCenter[i].pathPointer.addNode((int)((double)Math.random()*(double)PlaySceneBoard.boardW*PlaySceneBoard.gridSize),
								(int)(Math.random()*(double)PlaySceneBoard.boardH*PlaySceneBoard.gridSize));
						UCenter[i].pathPointer.addNode(0, 0);
						PlaySceneBoard.structureEntity[PlaySceneDataInit.UCENTER].attack(UCenter[i].id);
						originalUnits_n += UCenter[i].memory[PlaySceneBoard.structureEntity[PlaySceneDataInit.UCENTER].getMemoryIdByName("unit_n")][0]+
								UCenter[i].memory[PlaySceneBoard.structureEntity[PlaySceneDataInit.UCENTER].getMemoryIdByName("unit_n")][1]+
								UCenter[i].memory[PlaySceneBoard.structureEntity[PlaySceneDataInit.UCENTER].getMemoryIdByName("unit_n")][2];
					}
				}
				populationRequiredForUFO = 0;
				attackStates = ATTACKING;
				waitingToRaisePopulation = false;
			}
		}

		if(attackStates==ATTACKING){ //Check whether worth retreating
			int total = 0;
			for(int i=0;i<UCenterMax_n;i++){
				if(UCenterExist[i]){
					for(int j=0;j<PlaySceneDataInit.unitEntity_n;j++)
						total += PlaySceneBoard.unitEntity[j].workerRequired*UCenter[i].memory[PlaySceneBoard.structureEntity[PlaySceneDataInit.UCENTER].getMemoryIdByName("unit_n")][j];
				}
			}
			if(total<=originalUnits_n/5){
				for(int i=0;i<UCenterMax_n;i++){
					if(UCenterExist[i]){
						PlaySceneBoard.structureEntity[PlaySceneDataInit.UCENTER].retreat(UCenter[i].id);
						toHospital[i] = -1;
					}
				}
				attackStates = RETREATING;
				extraPower *= 1.2f;
			}
		}
		if(attackStates==HEALING){ //Check whether healing is finished. If yes, retreat.
			boolean allHealed = true;
			for(int i=0;i<PlaySceneBoard.unit_n;i++){
				if(PlaySceneBoard.unit[i].owner==owner){
					if(Math.round(PlaySceneBoard.unit[i].health)<PlaySceneBoard.unitEntity[PlaySceneBoard.unit[i].type].health){
						allHealed = false;
						break;
					}
				}
			}
			for(int i=0;i<UCenterMax_n;i++){ //unitHealth
				if(UCenterExist[i]){
					StructureEntity e = PlaySceneBoard.structureEntity[PlaySceneDataInit.UCENTER];
					for(int j=0;j<UCenter[i].memory[e.getMemoryIdByName("unit_n")][0]+UCenter[i].memory[e.getMemoryIdByName("unit_n")][1]+UCenter[i].memory[e.getMemoryIdByName("unit_n")][2];j++){ //unitHealth
						if(UCenter[i].unitReady[j]&&UCenter[i].unitHealth[j]<PlaySceneBoard.unitEntity[UCenter[i].unitType[j]].health){
							allHealed = false;
							break;
						}
					}
				}
			}
			if(allHealed){
				for(int i=0;i<UCenterMax_n;i++){
					if(UCenterExist[i]){
						PlaySceneBoard.structureEntity[PlaySceneDataInit.UCENTER].retreat(UCenter[i].id);
						toHospital[i] = -1;
					}
				}
				attackStates = RETREATING;
			}
		}

		if(attackStates==ATTACKING||attackStates==RETREATING||attackStates==HOUSED||attackStates==HEALING){ //check whether all units are killed :(
			boolean allKilled = true;
			for(int i=0;i<UCenterMax_n;i++){
				if(UCenterExist[i]){
					StructureEntity e = PlaySceneBoard.structureEntity[PlaySceneDataInit.UCENTER];
					if(UCenter[i].memory[e.getMemoryIdByName("unit_n")][0]+UCenter[i].memory[e.getMemoryIdByName("unit_n")][1]+UCenter[i].memory[e.getMemoryIdByName("unit_n")][2]>0){
						allKilled = false;
						break;
					}
				}
			}
			if(allKilled){
				for(int i=0;i<UCenterMax_n;i++){
					if(UCenterExist[i])
						toHospital[i] = -1;
				}
				attackStates = EMPTY;
				waitingToRaisePopulation = false;
			}
		}
		
		if(attackStates==RETREATING){ //check whether retreating is finished
			boolean retreated = true;
			for(int i=0;i<PlaySceneBoard.unit_n;i++){
				if(PlaySceneBoard.unit[i].owner==owner){
					retreated = false;
					break;
				}
			}
			if(retreated){
				if(waitingToRaisePopulation){
					attackStates = RAISINGPOPULATION;
					waitingToRaisePopulation = false;
				}else{
					attackStates = HOUSED;
					waitingToRaisePopulation = false;
				}
				for(int i=0;i<UCenterMax_n;i++){
					if(UCenterExist[i])
						toHospital[i] = -1;
				}
			}
		}

		if(attackStates==HOUSED){ //check whether AI should be healed
			boolean housed = true;
			for(int i=0;i<UCenterMax_n;i++){
				StructureEntity e = PlaySceneBoard.structureEntity[PlaySceneDataInit.UCENTER];
				if(UCenterExist[i]){
					if(UCenter[i].memory[e.getMemoryIdByName("attacking")][0]!=E.IDLE){
						housed = false;
						break;
					}
				}
			}
			boolean allHealed = true;
			for(int i=0;i<UCenterMax_n;i++){ //unitHealth
				if(UCenterExist[i]){
					StructureEntity e = PlaySceneBoard.structureEntity[PlaySceneDataInit.UCENTER];
					for(int j=0;j<UCenter[i].memory[e.getMemoryIdByName("unit_n")][0]+UCenter[i].memory[e.getMemoryIdByName("unit_n")][1]+UCenter[i].memory[e.getMemoryIdByName("unit_n")][2];j++){ //unitHealth
						if(UCenter[i].unitReady[j]&&UCenter[i].unitHealth[j]<PlaySceneBoard.unitEntity[UCenter[i].unitType[j]].health){
							allHealed = false;
							break;
						}
					}
				}
			}
			if(housed&&!allHealed){
				for(int i=0;i<UCenterMax_n;i++){
					calculateHealingTimeLeft();
					if(UCenterExist[i]){
						int shorestId = -1;
						double shortestTime = 999999999;
						for(int j=0;j<hospitalMax_n;j++){
							if(hospitalExist[j]){
								if(healingTimeLeft[j]<shortestTime){
									shortestTime = healingTimeLeft[j];
									shorestId = j;
								}
							}
						}
						if(shorestId>-1){
							PlaySceneBoard.structureEntity[PlaySceneDataInit.UCENTER].createPath(UCenter[i]);
							for(int a=0;a<10;a++){
								UCenter[i].pathPointer.addNode((hospital[shorestId].pos.x-1)*PlaySceneBoard.gridSize
										, (hospital[shorestId].pos.y-1)*PlaySceneBoard.gridSize);
								UCenter[i].pathPointer.addNode((hospital[shorestId].pos.x+3)*PlaySceneBoard.gridSize,
										(hospital[shorestId].pos.y-1)*PlaySceneBoard.gridSize);
								UCenter[i].pathPointer.addNode((hospital[shorestId].pos.x+3)*PlaySceneBoard.gridSize,
										(hospital[shorestId].pos.y+3)*PlaySceneBoard.gridSize);
								UCenter[i].pathPointer.addNode((hospital[shorestId].pos.x-1)*PlaySceneBoard.gridSize
										, (hospital[shorestId].pos.y+3)*PlaySceneBoard.gridSize);
							}
							PlaySceneBoard.structureEntity[PlaySceneDataInit.UCENTER].attack(UCenter[i].id);
							toHospital[i] = shorestId;
							attackStates = HEALING;
						}
					}
				}
			}
		}
	}
	
	private void calculateHealingTimeLeft(){
		for(int i=0;i<hospitalMax_n;i++){
			if(hospitalExist[i]){
				healingTimeLeft[i] = 0;
			}
		}
		for(int i=0;i<UCenterMax_n;i++){
			if(UCenterExist[i]){
				if(toHospital[i]>-1){
					for(int j=0;j<PlaySceneBoard.unit_n;j++){
						if(UCenter[i]==PlaySceneBoard.unit[i].source){
							healingTimeLeft[toHospital[i]] += (1-(PlaySceneBoard.unit[i].health/PlaySceneBoard.unitEntity[PlaySceneBoard.unit[i].type].health))/-PlaySceneBoard.structureEntity[PlaySceneDataInit.HOSPITAL].value[retd(PlaySceneDataInit.ATTACK)][hospital[toHospital[i]].currentLevel];
						}
					}
				}
			}
		}
	}
	
	private int retd(int e){
		return PlaySceneBoard.resourceEnumToId(e);
	}
}
