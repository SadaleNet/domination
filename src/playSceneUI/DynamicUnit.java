/*Copyright 2012 Wong Cho Ching, all rights reserved.
 * This file is released under BSD 2-clause License.
 * For details, please read LICENSE.txt in this package.*/

package playSceneUI;

import header.GLAdditional;
import header.SFX;

import java.awt.Point;
import java.lang.Math;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import enums.E;

public class DynamicUnit {
	public double x;
	public double y;
	public int id;
	public StructureBuilt source;
	public int owner;
	public int type;
	public Path path;
	public boolean exist;
	private int currentNode;
	private double nodePos; // the pos between currentNode and nextNode.
	private double distanceToNextNode;
	private double HdisplacementToNextNode;
	private double VdisplacementToNextNode;
	private boolean retreating;
	private boolean enoughWorkers;

	public double health;
	public DynamicUnit(int id, StructureBuilt source, int owner, int type, Path path){
		this.id = id;
		this.source = source;
		this.owner = owner;
		this.type = type;
		this.path = path;
		currentNode = 0;
		nodePos = 0;
		retreating = false;
		exist = true;
		init();
	}
	public void init(){
		Point pos = path.getNodePos(0);
		x = pos.x;
		y = pos.y;
		calculateDistances();
		SFX.play("./sfx/build.wav");
	}
	public void destruct(){//destruct without deducting UFO_n.
		int UFO_n = 0;
		for(int i=0;i<PlaySceneDataInit.unitEntity_n;i++){
			UFO_n += source.memory[PlaySceneBoard.structureEntity[PlaySceneDataInit.UCENTER].getMemoryIdByName("unit_n")][i];
		}
		if(UFO_n<=0){
			source.states = -1;
			source.memory[PlaySceneBoard.structureEntity[PlaySceneDataInit.UCENTER].getMemoryIdByName("attacking")][0] = E.IDLE;
		}
		for(int j=id;j<PlaySceneBoard.unit_n;j++){
			PlaySceneBoard.unit[j] = PlaySceneBoard.unit[j+1];
			if(PlaySceneBoard.unit[j]!=null)
				PlaySceneBoard.unit[j].id = j;
		}
		PlaySceneBoard.unit_n--;
		exist = false;
	}
	public void kill(){ //deduct the UFO_n, then destruct.
		int totalUnitInside = 0;
		for(int i=0;i<PlaySceneDataInit.unitEntity_n;i++)
			totalUnitInside += source.memory[PlaySceneBoard.structureEntity[PlaySceneDataInit.UCENTER].getMemoryIdByName("unit_n")][i];

		for(int i=0;i<totalUnitInside;i++){
			if(source.unitType[i]==type&&!source.unitReady[i]){
				for(int j=i;j<totalUnitInside;j++){
					if(j+1<source.unitHealth.length){
						source.unitHealth[j] = source.unitHealth[j+1];
						source.unitReady[j] = source.unitReady[j+1];
						source.unitType[j] = source.unitType[j+1];
					}
				}
				break;
			}
		}
		source.memory[PlaySceneBoard.structureEntity[PlaySceneDataInit.UCENTER].getMemoryIdByName("unit_n")][type]--;
		source.currentValue[PlaySceneBoard.structureEntity[PlaySceneDataInit.UCENTER].getPropertyIDByResourceEnum(PlaySceneDataInit.UNITCAP)]--;
		source.unitReleased[type]--;
		destruct();
		SFX.play("./sfx/UFOKilled.wav");
	}
	double oldNodePos; 
	public void nextFrame(){
		if(path.n>=2){
			if(health<=0){
				kill();
				return;
			}else if(health>PlaySceneBoard.unitEntity[type].health){
				health = PlaySceneBoard.unitEntity[type].health;
			}
			if(PlaySceneBoard.resourceAmount[owner][PlaySceneBoard.resourceEnumToId(PlaySceneDataInit.POPULATION_AVAILABLE)]>=PlaySceneBoard.unitEntity[type].workerRequired
					&&source.problem==-1){
				PlaySceneBoard.resourceAmount[owner][PlaySceneBoard.resourceEnumToId(PlaySceneDataInit.POPULATION_AVAILABLE)] -= PlaySceneBoard.unitEntity[type].workerRequired;
				enoughWorkers = true;
			}else{
				enoughWorkers = false;
				if(!retreating)
					return;
			}
			oldNodePos = nodePos;
			boolean move = true;
			int gridX = (int) (x/PlaySceneBoard.gridSize);
			int gridY = (int) (y/PlaySceneBoard.gridSize);
			if(gridX>=0&&gridY>=0&&gridX<PlaySceneBoard.boardW&&gridY<PlaySceneBoard.boardH){
				int structureBuildId = PlaySceneBoard.structureMask[gridX][gridY];
				if(structureBuildId>-1){
					if(!retreating&&PlaySceneBoard.structureBuilt[structureBuildId].owner!=owner){
						StructureBuilt entity = PlaySceneBoard.structureBuilt[PlaySceneBoard.structureMask[gridX][gridY]];
						entity.currentValue[PlaySceneBoard.structureEntity[entity.type].getPropertyIDByResourceEnum(PlaySceneDataInit.HEALTH)] -= (double)(PlaySceneBoard.unitEntity[type].attack)*PlaySceneBoard.frameAdvance/PlaySceneBoard.fps;
						move = false;
					}
				}
			}else{
				destruct();
			}
			if(move)
				nodePos += (double)(PlaySceneBoard.unitEntity[type].speed)*PlaySceneBoard.frameAdvance/PlaySceneBoard.fps*(retreating?5.0f:1.0f);
			if(!retreating){
				while(nodePos>distanceToNextNode){
					currentNode++;
					nodePos -= distanceToNextNode;
					calculateDistances();
					Point pos = path.getNodePos(currentNode);
					x = pos.x;
					y = pos.y;
					oldNodePos = 0;
				}
			}
			if(move){
				double deltaNodePos = nodePos-oldNodePos;
				x += deltaNodePos*HdisplacementToNextNode/distanceToNextNode;
				y += deltaNodePos*VdisplacementToNextNode/distanceToNextNode;
			}
		}
	}
	private void calculateDistances(){
		Point p1;
		Point p2;
		if(!retreating){
			p1 = path.getNodePos(currentNode);
			p2 = path.getNodePos(currentNode+1);
		}else{
			p1 = new Point();
			p1.x = (int) x; //int casting error is insignificant, which can be ignored. 
			p1.y = (int) y;
			p2 = path.getNodePos(0);
		}
		HdisplacementToNextNode = p2.x-p1.x;
		VdisplacementToNextNode = p2.y-p1.y;
		double Xsquare = HdisplacementToNextNode*HdisplacementToNextNode;
		double Ysquare = VdisplacementToNextNode*VdisplacementToNextNode;
		distanceToNextNode = Math.sqrt((Xsquare+Ysquare));
	}
	void blit(GL2 gl){
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, PlaySceneBoard.unitEntity[type].texture[owner==PlaySceneBoard.currentPlayer?0:1]);
		int size = PlaySceneBoard.unitEntity[type].size/2;
	    gl.glBegin(GL2.GL_QUADS);
	    gl.glTexCoord2d(0.0f, 1.0f);	gl.glVertex2d(x-size, y-size);
	    gl.glTexCoord2d(1.0f, 1.0f);	gl.glVertex2d(x+size, y-size);
	    gl.glTexCoord2d(1.0f, 0.0f);	gl.glVertex2d(x+size, y+size);
	    gl.glTexCoord2d(0.0f, 0.0f);	gl.glVertex2d(x-size, y+size);
	    gl.glEnd();
	    if(!enoughWorkers&&(PlaySceneBoard.currentTick/500)%2==0&&owner==PlaySceneBoard.currentPlayer){
			gl.glBindTexture(GL2.GL_TEXTURE_2D, PlaySceneDataInit.texture[PlaySceneBoard.resourceEnumToId(PlaySceneDataInit.POPULATION_AVAILABLE)]);
		    gl.glBegin(GL2.GL_QUADS);
		    gl.glTexCoord2d(0.0f, 1.0f);	gl.glVertex2d(x-size, y-size);
		    gl.glTexCoord2d(1.0f, 1.0f);	gl.glVertex2d(x+size, y-size);
		    gl.glTexCoord2d(1.0f, 0.0f);	gl.glVertex2d(x+size, y+size);
		    gl.glTexCoord2d(0.0f, 0.0f);	gl.glVertex2d(x-size, y+size);
		    gl.glEnd();

			gl.glBindTexture(GL2.GL_TEXTURE_2D, PlaySceneDataInit.deselectIcon[0]);
		    gl.glBegin(GL2.GL_QUADS);
		    gl.glTexCoord2d(0.0f, 1.0f);	gl.glVertex2d(x-size, y-size);
		    gl.glTexCoord2d(1.0f, 1.0f);	gl.glVertex2d(x+size, y-size);
		    gl.glTexCoord2d(1.0f, 0.0f);	gl.glVertex2d(x+size, y+size);
		    gl.glTexCoord2d(0.0f, 0.0f);	gl.glVertex2d(x-size, y+size);
		    gl.glEnd();
	    }
	    
		gl.glDisable(GL.GL_TEXTURE_2D);
	}
	void blitHealthBar(GL2 gl){
		if(health<PlaySceneBoard.unitEntity[type].health){
			int size = PlaySceneBoard.unitEntity[type].size/2;
	        float x1 = (float) (x-size);
	        float x2 = (float) (x+size);
	        float y1 = (float) (y+size*40/100);
	        float y2 = (float) (y+size);
		    gl.glColor4d(0.5f, 0.2f, 0.2f, 0.6f);
		    gl.glBegin(GL2.GL_QUADS);
		    gl.glVertex2d(x1, y1);
		    gl.glVertex2d(x2, y1);
		    gl.glVertex2d(x2, y2);
		    gl.glVertex2d(x1, y2);
		    
	        gl.glColor4d(0.2f, 0.5f, 0.2f, 0.6f);
		    gl.glVertex2d(x1, y1);
		    gl.glVertex2d(x1+(x2-x1)*health/PlaySceneBoard.unitEntity[type].health, y1);
		    gl.glVertex2d(x1+(x2-x1)*health/PlaySceneBoard.unitEntity[type].health, y2);
		    gl.glVertex2d(x1, y2);
		    gl.glEnd();
		    gl.glColor4d(1.0f, 1.0f, 1.0f, 1.0f);
		}
	}
	void retreat(){
		retreating = true;
		calculateDistances();
		oldNodePos = 0;
	}
}
