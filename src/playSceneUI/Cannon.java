/*Copyright 2012 Wong Cho Ching, all rights reserved.
 * This file is released under BSD 2-clause License.
 * For details, please read LICENSE.txt in this package.*/

package playSceneUI;

import header.G;
import header.GLAdditional;
import header.MathSupp;
import header.SFX;

import java.awt.Point;
import java.lang.Math;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

public class Cannon{
	public double x;
	public double y;
	public int id;
	public StructureBuilt source;
	public StructureEntity entity;
	public int owner;
	public int textureid;
	public double rotation;
	private boolean enoughWorkers;
	public double timer = 0;
	public boolean loaded;

	public double health;
	public Cannon(int id, StructureBuilt source, int owner){
		this.id = id;
		this.source = source;
		this.owner = owner;
		rotation = 0;
		loaded = true;
		
		entity = PlaySceneBoard.structureEntity[source.type];

		textureid = entity.value[entity.getPropertyIDByResourceEnum(PlaySceneDataInit.CANNONICONID)][source.currentLevel];
		x = (source.pos.x+PlaySceneBoard.structureEntity[source.type].width/2-PlaySceneBoard.structureEntity[source.type].width*0.04f)*PlaySceneBoard.gridSize;
		y = (source.pos.y+PlaySceneBoard.structureEntity[source.type].height/2+PlaySceneBoard.structureEntity[source.type].height*0.114286f)*PlaySceneBoard.gridSize;
		
	}
	public void destruct(){
		for(int j=id;j<PlaySceneBoard.cannon_n;j++){
			PlaySceneBoard.cannon[j] = PlaySceneBoard.cannon[j+1];
			if(PlaySceneBoard.cannon[j]!=null)
				PlaySceneBoard.cannon[j].id = j;
		}
		PlaySceneBoard.cannon_n--;
	}
	public void nextFrame(){
		long delay = 1000*60/entity.value[entity.getPropertyIDByResourceEnum(PlaySceneDataInit.RATE)][source.currentLevel];
		if(loaded){
			int attack = entity.value[entity.getPropertyIDByResourceEnum(PlaySceneDataInit.ATTACK)][source.currentLevel];
			if(source.type==PlaySceneDataInit.HOSPITAL) //TODO too lazy! Should use a better method.
				attack = -attack;
			int shortestId = -1;
			double shortest = 0f;
			for(int i=0;i<PlaySceneBoard.unit_n;i++){
				if((attack>0&&PlaySceneBoard.unit[i].owner!=owner)||
						(attack<0&&PlaySceneBoard.unit[i].owner==owner)){
					double distance = MathSupp.distance(x,y,PlaySceneBoard.unit[i].x,PlaySceneBoard.unit[i].y);
					if(distance<entity.value[entity.getPropertyIDByResourceEnum(PlaySceneDataInit.RANGE)][source.currentLevel]*PlaySceneBoard.gridSize&&
							(source.type==PlaySceneDataInit.HOSPITAL?PlaySceneBoard.unit[i].health<PlaySceneBoard.unitEntity[PlaySceneBoard.unit[i].type].health:true)){
						if(distance<shortest||shortestId==-1){
							shortest = distance;
							shortestId = i;
						}
					}
				}
			}
			if(shortestId>-1&&!source.upgrading&&source.problem==-1){
				PlaySceneBoard.projectile[PlaySceneBoard.projectile_n] =
						new Projectile(PlaySceneBoard.projectile_n, x, y, Math.min(attack, PlaySceneBoard.unit[shortestId].health), PlaySceneBoard.unit[shortestId], owner, source.type);
				PlaySceneBoard.projectile_n++;
				rotation = MathSupp.angle(x,y,PlaySceneBoard.unit[shortestId].x,PlaySceneBoard.unit[shortestId].y);
				loaded = false;
				timer -= delay;
				SFX.play("./sfx/shoot.wav");
			}
		}else if(timer>delay){
			loaded = true;
		}else{
			timer += PlaySceneBoard.frameAdvance*PlaySceneBoard.tps;
		}
	}
	void blit(GL2 gl){
		gl.glEnable(GL.GL_TEXTURE_2D);
		double sizeH = PlaySceneBoard.structureEntity[source.type].width*PlaySceneBoard.gridSize/2; // /2 because we use a special bilting system. Please read below.
		double sizeV = PlaySceneBoard.structureEntity[source.type].height*PlaySceneBoard.gridSize/2;
		
		gl.glPushMatrix();
		gl.glTranslated(x, y, 0.0f);
		gl.glRotated(rotation, 0.0f, 0.0f, 1.0f);
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, PlaySceneBoard.cannonTexture[textureid]);
	    gl.glBegin(GL2.GL_QUADS);
	    gl.glTexCoord2d(0.0f, 1.0f);	gl.glVertex2d(-sizeH, -sizeV);
	    gl.glTexCoord2d(1.0f, 1.0f);	gl.glVertex2d(sizeH, -sizeV);
	    gl.glTexCoord2d(1.0f, 0.0f);	gl.glVertex2d(sizeH, sizeV);
	    gl.glTexCoord2d(0.0f, 0.0f);	gl.glVertex2d(-sizeH, sizeV);
	    gl.glEnd();
	    
		gl.glPopMatrix();
		gl.glDisable(GL.GL_TEXTURE_2D);
	}

	void blitRange(GL2 gl){
		gl.glColor4d(1.0f, 1.0f, 1.0f, 0.5f);
		GLAdditional.glCircle3d(gl, x, y, entity.value[entity.getPropertyIDByResourceEnum(PlaySceneDataInit.RANGE)][source.currentLevel]*PlaySceneBoard.gridSize);
		gl.glColor4d(1.0f, 1.0f, 1.0f, 1.0f);
	}
}
