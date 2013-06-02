/*Copyright 2012 Wong Cho Ching, all rights reserved.
 * This file is released under BSD 2-clause License.
 * For details, please read LICENSE.txt in this package.*/

package playSceneUI;

import java.awt.Point;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.table.DefaultTableModel;

import com.jogamp.graph.geom.Vertex;

import enums.E;

import GUI.PlayScene;

public class StructureBuilt/* extends structureEntity*/{
	public Point pos = new Point();
	public int id;
	public int owner;
	public int type;
	public int currentLevel;
	public long previousUpdate;
	public double[] currentValue;
	public double[] valueCap;
	public boolean upgrading = true;
	public boolean upgradingCompleted = false;
	public boolean occupingSuccess = true;
	public boolean exist = true;
	public int[][] memory;
	public Path pathPointer = null;
	public int problem = -1;

	public StructureBuilt(int structureType, Point pos, int length, int id, int owner){
		this.type = structureType;
		this.pos.x = pos.x;
		this.pos.y = pos.y;
		this.id = id;
		this.owner = owner;
		PlaySceneBoard.selectedStructure[owner] = id;
		
		currentValue = new double[length+1];
		valueCap = new double[length];
		StructureEntity structEntity = PlaySceneBoard.structureEntity[structureType];
		memory = new int[structEntity.memory_n][];
		for(int i=0;i<structEntity.memory_n;i++){
			memory[i] = new int[structEntity.memorySize[i]];
			Arrays.fill(memory[i], 0);
		}
		Arrays.fill(currentValue, 0);
		Arrays.fill(valueCap, 0);
		Arrays.fill(unitReleased, 0);
		currentLevel = 0;
		if(!calculateResourceOnUpgrade())
			destruct();
		previousUpdate = PlaySceneBoard.paused?0:System.currentTimeMillis();
		upgradingCompleted = false;
		upgrading = true;
		exist = true;
	}
	public boolean upgrade(){
		if(!PlaySceneBoard.checkCost(type,currentLevel+1, owner))
			return false;
		currentLevel++;
		calculateResourceOnUpgrade();
		PlaySceneBoard.selectedStructure[owner] = id;
		PlaySceneBoard.structureEntity[type].updateUI(currentLevel, StructureEntity.NORMAL, owner);
		upgradingCompleted = false;
		upgrading = true;
		return true;
	}
	private boolean checkPrerequisite(int i, int propType, int propResId, boolean selling){
		if((propType&PlaySceneDataInit.PREREQ)!=0){
			problem = -1;
			StructureEntity structEntity = PlaySceneBoard.structureEntity[type];
			double changes = 0;
			if((propType&(PlaySceneDataInit.SET))!=0){
				changes = structEntity.value[i][currentLevel];
			}else{
				if((propType&PlaySceneDataInit.GENERATED)==0){ //if it is NOT generated.
					changes = structEntity.value[i][currentLevel];
					if((propType&PlaySceneDataInit.DELTA)!=0)
						changes -= (currentLevel>0?structEntity.value[i][currentLevel-1]:0);
				}else{
					changes = currentValue[i];
				}
			}
			/*if((propType&PlaySceneDataInit.ADD)!=0)
				changes = changes;
			else */if((propType&PlaySceneDataInit.DEDUCT)!=0)
				changes = -changes;
			changes *= selling&&((propType&PlaySceneDataInit.COST)!=0)?PlaySceneBoard.sellValue:1f;
			if((propType&PlaySceneDataInit.CONTINOUS)!=0)
				changes *= PlaySceneBoard.frameAdvance;

			if(owner!=PlaySceneBoard.humanPlayer){
				if((propType&PlaySceneDataInit.VARH)!=0)
					changes *= PlaySceneBoard.ai.varhMultiplier;
				else if((propType&PlaySceneDataInit.VARL)!=0)
					changes *= PlaySceneBoard.ai.varlMultiplier;
			}

			if((propType&PlaySceneDataInit.TILL)!=0){
				if((propType&PlaySceneDataInit.EXHAUST)!=0){
					if(PlaySceneBoard.resourceAmount[owner][propResId]+changes<0){
						problem = propResId;
						return false;
					}
				}else if((propType&PlaySceneDataInit.INTCAP)!=0){
					double capValue = valueCap[structEntity.getPropertyIntCapIDByResourceEnum(structEntity.propertiesEnum[i])];/*structEntity.getPropertyIntCapByResourceID(propResId, currentLevel);*/
					if(currentValue[i]+changes>capValue){
						problem = propResId;
						return false;
					}
				}else if((propType&PlaySceneDataInit.EXTCAP)!=0){
					/*int capValue = PlaySceneBoard.resourceCap[owner][propResId];
					if(PlaySceneBoard.resourceAmount[owner][propResId]+changes>capValue){
						problem = propResId;
						return false;
					}*/
				}
			}else if((propType&PlaySceneDataInit.LOCAL)!=0){
				return true;
			}
		}
		return true;
	}
	double oldCurrentValue = 0.0f;
	private void calculateValueChanges(int i, int propType, int propResId, boolean selling){
		if(propType!=0){
			StructureEntity structEntity = PlaySceneBoard.structureEntity[type];
			/*if((propType&(PlaySceneDataInit.SET|PlaySceneDataInit.ISCAP))!=0){
				currentValue[i] = structEntity.value[i][currentLevel];
				return;
			}*/
			double changes = 0;
			int unit_n = 1;
			if((propType&(PlaySceneDataInit.SET))!=0){
				changes = structEntity.value[i][currentLevel];
			}else{
				if((propType&PlaySceneDataInit.GENERATED)==0){ //if it is NOT generated.
					changes = structEntity.value[i][currentLevel];
					if((propType&PlaySceneDataInit.DELTA)!=0)
						changes -= (currentLevel>0?structEntity.value[i][currentLevel-1]:0);
				}else{
					changes = currentValue[i];
				}
				
			/*if((propType&PlaySceneDataInit.ADD)!=0)
				changes = changes;
			else */if((propType&PlaySceneDataInit.DEDUCT)!=0)
				changes = -changes;
			}
			changes *= selling&&((propType&PlaySceneDataInit.COST)!=0)?PlaySceneBoard.sellValue:1f;
			if((propType&PlaySceneDataInit.CONTINOUS)!=0)
				changes *= PlaySceneBoard.frameAdvance;

			if(owner!=PlaySceneBoard.humanPlayer){
				if((propType&PlaySceneDataInit.VARH)!=0)
					changes *= PlaySceneBoard.ai.varhMultiplier;
				else if((propType&PlaySceneDataInit.VARL)!=0)
					changes *= PlaySceneBoard.ai.varlMultiplier;
			}
			
			int targetResEnum = -1;
			if((propType&PlaySceneDataInit.EACH)!=0){
				targetResEnum = structEntity.propertiesParameter[i];
				unit_n = (int) currentValue[structEntity.getPropertyIDByResourceEnum(targetResEnum)];
			}
			if((propType&PlaySceneDataInit.TILL)!=0){
				if((propType&PlaySceneDataInit.EXHAUST)!=0){
					while(PlaySceneBoard.resourceAmount[owner][propResId]+changes*unit_n<0&&unit_n>0)
						unit_n--;
					changes *= unit_n;
					if(PlaySceneBoard.resourceAmount[owner][propResId]+changes>=0){
						PlaySceneBoard.resourceAmount[owner][propResId] += changes;
						currentValue[i] += changes;
					}else if(PlaySceneBoard.resourceAmount[owner][propResId]>=0){
						PlaySceneBoard.resourceAmount[owner][propResId] += currentValue[i];
						currentValue[i] = 0;
					}
				}else{
					double baseValue = -1;
					double capValue = -1;
					if((propType&PlaySceneDataInit.INTCAP)!=0){
						baseValue = currentValue[i];
						capValue = valueCap[structEntity.getPropertyIntCapIDByResourceEnum(structEntity.propertiesEnum[i])];
						//changes -= oldCurrentValue; //TODO bad design for oldCurrentValue. What if there are more than one INTCAP variable?
					}else if((propType&(PlaySceneDataInit.EXTCAP|PlaySceneDataInit.LOCAL))!=0){
						baseValue = PlaySceneBoard.resourceAmount[owner][propResId];
						capValue = PlaySceneBoard.resourceCap[owner][propResId];
					}
					if(baseValue>capValue){
						if((propType&(PlaySceneDataInit.LOCAL|PlaySceneDataInit.INTCAP))==0) //TODO bad design: INTCAP in here stands for the type PlaySceneDataInit.HOUSING
							PlaySceneBoard.resourceAmount[owner][propResId] += capValue-baseValue;
						currentValue[i] = capValue;
					}
					if((propType&PlaySceneDataInit.EACH)!=0){
						while(baseValue+changes>capValue&&unit_n>0)
							unit_n--;
					}
					changes *= unit_n;
					if(baseValue+changes<=capValue){
						if((propType&(PlaySceneDataInit.LOCAL|PlaySceneDataInit.INTCAP))==0) //TODO bad design: INTCAP in here stands for the type PlaySceneDataInit.HOUSING
							PlaySceneBoard.resourceAmount[owner][propResId] += changes;
						currentValue[i] += changes;
					}else if(baseValue<capValue){
						double fitter = capValue-baseValue;
						if((propType&(PlaySceneDataInit.LOCAL|PlaySceneDataInit.INTCAP))==0) //TODO bad design: INTCAP in here stands for the type PlaySceneDataInit.HOUSING
							PlaySceneBoard.resourceAmount[owner][propResId] += fitter;
						currentValue[i] += fitter;
					}
				}
			}else if((propType&PlaySceneDataInit.ATEXTCAP)!=0){
				PlaySceneBoard.resourceCap[owner][propResId] += changes;
			}else if((propType&PlaySceneDataInit.LOCAL)!=0){
				switch(structEntity.propertiesParameter[i]){
					case -1:
						currentValue[i] = changes;
					break;
					case -2:
						//Do nothing
					break;
					default:
						currentValue[i] = structEntity.propertiesParameter[i];
				}
			}else{
				PlaySceneBoard.resourceAmount[owner][propResId] += changes;
			}
			if((propType&PlaySceneDataInit.EACH)!=0){
				if(currentValue[structEntity.getPropertyIDByResourceEnum(targetResEnum)]-unit_n>=1) //avoid clipping of incomplete adding like 1.96 will be cliped to 1.0
					currentValue[structEntity.getPropertyIDByResourceEnum(targetResEnum)] = unit_n;
//System.out.println(currentValue[structEntity.getPropertyIDByResourceEnum(targetResEnum)]-oldCurrentValue);
				PlaySceneBoard.resourceAmount[owner][PlaySceneBoard.resourceEnumToId(targetResEnum)] += currentValue[structEntity.getPropertyIDByResourceEnum(targetResEnum)]-oldCurrentValue;
				oldCurrentValue = currentValue[structEntity.getPropertyIDByResourceEnum(targetResEnum)]; //TODO bad design: what if there are more than one oldCurrentValue? 
			}
		}
	}
	private boolean checkPrereq(int includeMask, int excldeMask, boolean destruct){
		int propType = -1;
		for(int i=0;i<PlaySceneBoard.structureEntity[type].properties_n;i++){
			int propResId = PlaySceneBoard.resourceEnumToId(PlaySceneBoard.structureEntity[type].propertiesEnum[i]);
			if(propResId>-1){
				if(!destruct)
					propType = PlaySceneBoard.structureEntity[type].propertiesType[i];
				else
					propType = PlaySceneBoard.structureEntity[type].propertiesTypeOnDestruct[i];
				if((propType&includeMask)!=0){
					if(!checkPrerequisite(i, propType, propResId, destruct)&&(PlaySceneBoard.structureEntity[type].propertiesType[i]&PlaySceneDataInit.EACH)==0)
						return false;
				}
			}
		}
		return true;
	}
	public int SUM = 0;
	public boolean execute(int resourceEnum, int propType, int value, int parameter, int oldValueMemory, int memoryMode){
		/*int oldCurrentLevel = currentLevel;
		currentLevel = 0;*/
		StructureEntity structEntity = PlaySceneBoard.structureEntity[type];
		int i = structEntity.properties_n;
		structEntity.propertiesEnum[i] = resourceEnum;
		structEntity.propertiesType[i] = propType;
		structEntity.value[i][0] = value;
		structEntity.propertiesParameter[i] = parameter;
		currentValue[i] = 0;
		if(memoryMode==SUM){ //Do not use switch here because flags might be used in future.
			for(int k=0;k<memory[oldValueMemory].length;k++)
				currentValue[i] += memory[oldValueMemory][k];
		}
		int j = structEntity.getPropertyIntCapIDByResourceEnum(resourceEnum);
		int oldProblem = problem;
		if(checkPrerequisite(i, propType, PlaySceneBoard.resourceEnumToId(resourceEnum), false)){
			calculateValueChanges(i, propType, PlaySceneBoard.resourceEnumToId(resourceEnum), false);
			//currentLevel = oldCurrentLevel;
			currentValue[j] = currentValue[i];
			return true;
		}
		problem = oldProblem;
		//currentLevel = oldCurrentLevel;
		currentValue[j] = currentValue[i];
		return false;
	}
	private void calculateChanges(int includeMask, int excldeMask, boolean destruct){
		int propType = -1;
		for(int i=0;i<PlaySceneBoard.structureEntity[type].properties_n;i++){
			int propResId = PlaySceneBoard.resourceEnumToId(PlaySceneBoard.structureEntity[type].propertiesEnum[i]);
			if(propResId>-1){
				if(!destruct)
					propType = PlaySceneBoard.structureEntity[type].propertiesType[i];
				else
					propType = PlaySceneBoard.structureEntity[type].propertiesTypeOnDestruct[i];
				if((propType&includeMask)==includeMask&&(propType&excldeMask)==0)
					calculateValueChanges(i, propType, propResId, destruct);
			}
		}
	}
	private boolean calculateResourceOnUpgrade(){
		int costFlag = PlaySceneBoard.initializing?PlaySceneDataInit.COST:0;
		for(int i=0;i<PlaySceneBoard.structureEntity[type].properties_n;i++){
			if((PlaySceneBoard.structureEntity[type].propertiesType[i]&PlaySceneDataInit.ISCAP)!=0){
				valueCap[i] = 
				PlaySceneBoard.structureEntity[type].
					value[PlaySceneBoard.structureEntity[type].
				      getPropertyIntCapIDByResourceEnum(PlaySceneBoard.structureEntity[type].
				    		  propertiesEnum[i])
				      ][currentLevel];
			}
		}
		if(!checkPrereq(0, PlaySceneDataInit.CONTINOUS|PlaySceneDataInit.GENERATED|PlaySceneDataInit.ATEXTCAP|costFlag, false))
			return false;
		calculateChanges(0, PlaySceneDataInit.CONTINOUS|PlaySceneDataInit.GENERATED|PlaySceneDataInit.ATEXTCAP|PlaySceneDataInit.EACH|costFlag, false);
		calculateChanges(PlaySceneDataInit.EACH, PlaySceneDataInit.CONTINOUS|PlaySceneDataInit.GENERATED|costFlag, false);
		return true;
	}
	public boolean calculateResourceOnFrame(){
		if(!upgrading){
			if(!upgradingCompleted){ //upgrade have just completed
				calculateChanges(PlaySceneDataInit.ATEXTCAP, 0, false);
				upgradingCompleted = true;
			}
			if(!occupingSuccess)
				return false;
			if(!checkPrereq(PlaySceneDataInit.CONTINOUS, 0, false))
				return false;
			calculateChanges(PlaySceneDataInit.CONTINOUS, PlaySceneDataInit.EACH, false);
		}
		calculateChanges(PlaySceneDataInit.CONTINOUS|PlaySceneDataInit.EACH, 0, false);
		return true;
	}
	public boolean calculateOccupy(){
		if(!(occupingSuccess=checkPrereq(PlaySceneDataInit.OCCUPY, 0, false)))
			return false;
		calculateChanges(PlaySceneDataInit.OCCUPY, PlaySceneDataInit.EACH, false);
		calculateChanges(PlaySceneDataInit.OCCUPY|PlaySceneDataInit.EACH, 0, false);
		return true;
	}
	public boolean sell(int structure){
		if(!checkPrereq(0, PlaySceneDataInit.CONTINOUS, true))
			return false;
		calculateChanges(0, PlaySceneDataInit.CONTINOUS|PlaySceneDataInit.EACH|PlaySceneDataInit.ATEXTCAP, true);
		calculateChanges(PlaySceneDataInit.EACH, PlaySceneDataInit.CONTINOUS, true);
		destruct();
		return true;
	}
	public void destruct(){
		calculateChanges(PlaySceneDataInit.ATEXTCAP, 0, true);
		if(owner==PlaySceneBoard.currentPlayer){
			PlaySceneBoard.states = PlaySceneBoard.NORMAL;
			PlaySceneBoard.clearUI();
		}
		if(type==PlaySceneDataInit.UCENTER){
			for(int i=0;i<PlaySceneBoard.unit_n;i++){
				if(PlaySceneBoard.unit[i].source==this){
					PlaySceneBoard.unit[i].destruct();
					i--;
				}
			}
			if(pathPointer!=null){
				pathPointer.destruct();
			}
		}else if(type==PlaySceneDataInit.BASE){
			PlaySceneBoard.gameover = true;
			PlaySceneBoard.victory = owner==0?false:true;
			PlaySceneBoard.gameOver();
		}
		if((PlaySceneBoard.structureEntity[type].category&(E.DEFENSIVE|E.REPAIR))!=0){
			for(int i=0;i<PlaySceneBoard.cannon_n;i++){
				if(PlaySceneBoard.cannon[i].source==this){
					PlaySceneBoard.cannon[i].destruct();
					break;
				}
			}
		}
		
		for(int i=0;i<PlaySceneBoard.structureEntity[type].width;i++){
			for(int j=0;j<PlaySceneBoard.structureEntity[type].height;j++)
				PlaySceneBoard.structureMask[pos.x+i][pos.y+j] = -1;
		}
		for(int i=id;i<PlaySceneBoard.structureBuilt_n;i++){
			PlaySceneBoard.structureBuilt[i] = PlaySceneBoard.structureBuilt[i+1];
			if(i<PlaySceneBoard.structureBuilt_n-1){
				PlaySceneBoard.structureBuilt[i].id = i;
			}
		}
		for(int i=0;i<PlaySceneBoard.boardW;i++){
			for(int j=0;j<PlaySceneBoard.boardH;j++){
				if(PlaySceneBoard.structureMask[i][j]>=id)
					PlaySceneBoard.structureMask[i][j]--;
			}
		}
		PlaySceneBoard.structureBuilt_n--;
		for(int i=0;i<PlaySceneBoard.player_n;i++){
			if(id==PlaySceneBoard.selectedStructure[i])
				PlaySceneBoard.selectedStructure[i] = -1;
			else if(id<PlaySceneBoard.selectedStructure[i])
				PlaySceneBoard.selectedStructure[i]--;
		}
		exist = false;
	}
	int states = -1;
	int[] unitReleased = new int[PlaySceneDataInit.unitEntity_n];
	int[] unitHealth = new int[20];
	int[] unitType = new int[20];
	boolean[] unitReady = new boolean[20];
	double UFOReleaseTimer = 0;
	public void execute(int type){
		switch(type){
			case E.RELEASEUNIT:
				UFOReleaseTimer = 0;
				states = E.RELEASINGUNIT;
			break;
			case E.RETREATUNIT:
				states = E.RETREATINGUNIT;
				for(int i=0;i<PlaySceneBoard.unit_n;i++){
					if(PlaySceneBoard.unit[i].source==this)
						PlaySceneBoard.unit[i].retreat();
				}
			break;
		}
	}
	
	static int releaseDelay = 750;
	public void frame(){
		if(currentValue[PlaySceneBoard.structureEntity[type].getPropertyIDByResourceEnum(PlaySceneDataInit.HEALTH)]<=0){
			destruct();
			return;
		}
		if(type==PlaySceneDataInit.UCENTER){
			StructureEntity structEntity = PlaySceneBoard.structureEntity[type];
			switch(states){
				case E.RELEASINGUNIT:
					int totalUnitTrained = 0;
					for(int i=0;i<PlaySceneDataInit.unitEntity_n;i++)
						totalUnitTrained += memory[structEntity.getMemoryIdByName("unit_n")][i];
					int totalUnitReleased = 0;
					for(int i=0;i<PlaySceneDataInit.unitEntity_n;i++)
						totalUnitReleased += unitReleased[i];
					if(totalUnitReleased<totalUnitTrained){
						UFOReleaseTimer += PlaySceneBoard.frameAdvance*PlaySceneBoard.tps;
						if(UFOReleaseTimer>releaseDelay){
							int type = -1;
							for(int i=0;i<PlaySceneDataInit.unitEntity_n;i++){
								if(unitReleased[i]<memory[structEntity.getMemoryIdByName("unit_n")][i]){
									type = i;
									break;
								}
							}
							PlaySceneBoard.unit[PlaySceneBoard.unit_n] = new DynamicUnit(PlaySceneBoard.unit_n, this, owner, type, pathPointer);
							for(int i=0;i<totalUnitTrained;i++){
								if(unitType[i]==type&&unitReady[i]){
									PlaySceneBoard.unit[PlaySceneBoard.unit_n].health = unitHealth[i];
									unitReady[i] = false;
									unitHealth[i] = 0;
									break;
								}
							}
							PlaySceneBoard.unit_n++;
							UFOReleaseTimer -= releaseDelay;
							unitReleased[type]++;
						}
					}
				break;
				case E.RETREATINGUNIT:
					int totalUnitTrained_ = 0;
					for(int i=0;i<PlaySceneDataInit.unitEntity_n;i++)
						totalUnitTrained_ += memory[structEntity.getMemoryIdByName("unit_n")][i];
					int totalUnitReleased_ = 0;
					for(int i=0;i<PlaySceneDataInit.unitEntity_n;i++)
						totalUnitReleased_ += unitReleased[i];
					if(totalUnitReleased_>0){
						for(int i=0;i<PlaySceneBoard.unit_n;i++){
							if(PlaySceneBoard.unit[i].source==this){
								int unitX = (int)PlaySceneBoard.unit[i].x/PlaySceneBoard.gridSize;
								int untiY = (int)PlaySceneBoard.unit[i].y/PlaySceneBoard.gridSize;
								if(unitX>=pos.x&&unitX<=pos.x+structEntity.width&&
										untiY>=pos.y&&untiY<=pos.y+structEntity.height){
										unitReleased[PlaySceneBoard.unit[i].type]--; // Do not move it down because this line should be done before the unit have been removed.
										for(int j=0;j<totalUnitTrained_;j++){
											if(unitType[j]==PlaySceneBoard.unit[i].type&&!unitReady[j]){
												unitReady[j] = true;
												unitHealth[j] = (int) PlaySceneBoard.unit[i].health;
												break;
											}
										}
										PlaySceneBoard.unit[i].destruct();
										i--;
								}
							}
						}
					}
					totalUnitReleased_ = 0;
					for(int i=0;i<PlaySceneDataInit.unitEntity_n;i++)
						totalUnitReleased_ += unitReleased[i];
					if(totalUnitReleased_==0){
						states = -1;
						memory[structEntity.getMemoryIdByName("attacking")][0] = E.IDLE;
					}
				break;
			}
		}
	}
}
