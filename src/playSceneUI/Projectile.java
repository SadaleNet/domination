/*Copyright 2012 Wong Cho Ching, all rights reserved.
 * This file is released under BSD 2-clause License.
 * For details, please read LICENSE.txt in this package.*/

package playSceneUI;

import header.GLAdditional;
import header.MathSupp;
import header.SFX;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

public class Projectile {

	public double x;
	public double y;
	public int id;
	public int owner;
	public int type;
	private DynamicUnit target;
	private double attack;
	private long previousUpdate;
	private long explodeTick = 0;
	private boolean exploding = false;
	private double explosionRange = 10.0f;
	
	private double speed = 300;
	private final static double size = 5;

	public double health;
	public Projectile(int id, double x, double y, double attack, DynamicUnit target, int owner, int type){
		this.id = id;
		this.x = x;
		this.y = y;
		this.attack = attack;
		this.target = target;
		this.owner = owner;
		this.type = type;
	}
	public void destruct(){
		if(type==PlaySceneDataInit.SPLASHTOWER){
			if(!exploding){
				SFX.play("./sfx/explosion.wav");
				exploding = true;
				for(int i=0;i<PlaySceneBoard.unit_n;i++){
					if((attack>0&&PlaySceneBoard.unit[i].owner!=owner)||
							(attack<0&&PlaySceneBoard.unit[i].owner==owner)){
						if(MathSupp.distance(x,y,PlaySceneBoard.unit[i].x,PlaySceneBoard.unit[i].y)<explosionRange){
							PlaySceneBoard.unit[i].health -= attack;
						}
					}
				}
				explodeTick = PlaySceneBoard.currentTick;
				return;
			}
		}
		for(int j=id;j<PlaySceneBoard.projectile_n;j++){
			PlaySceneBoard.projectile[j] = PlaySceneBoard.projectile[j+1];
			if(PlaySceneBoard.projectile[j]!=null)
				PlaySceneBoard.projectile[j].id = j;
		}
		PlaySceneBoard.projectile_n--;
	}
	public void nextFrame(){
		if(!exploding){
			if(!target.exist){
				int shortestId = -1;
				double shortest = 0f;
				for(int i=0;i<PlaySceneBoard.unit_n;i++){
					if((attack>0&&PlaySceneBoard.unit[i].owner!=owner)||
							(attack<0&&PlaySceneBoard.unit[i].owner==owner)){
						double distance = MathSupp.distance(x,y,PlaySceneBoard.unit[i].x,PlaySceneBoard.unit[i].y);
						if(distance<shortest||shortestId==-1){
							shortest = distance;
							shortestId = i;
						}
					}
				}
				if(shortestId>-1)
					target = PlaySceneBoard.unit[shortestId];
				else
					destruct();
				return;
			}else{
				double movement = speed*PlaySceneBoard.frameAdvance/PlaySceneBoard.fps;
				if(MathSupp.distance(x, y, target.x, target.y)<movement*2){
					if(type==PlaySceneDataInit.SPLASHTOWER){
						for(int i=0;i<PlaySceneBoard.unit_n;i++){
							if(MathSupp.distance(x, y, PlaySceneBoard.unit[i].x, PlaySceneBoard.unit[i].y)<explosionRange)
								target.health -= attack;
						}
					}else{
						target.health -= attack;
					}
					destruct();
				}else{
					double angle = MathSupp.angle(x, y, target.x, target.y);
					double abscosMov = movement*Math.abs(Math.cos(angle));
					if(x<target.x)
						x += abscosMov;
					else
						x -= abscosMov;
					double abssinMov = movement*Math.abs(Math.sin(angle));
					if(y<target.y)
						y += abssinMov;
					else
						y -= abssinMov;
				}
			}
		}
	}
	void blit(GL2 gl){
		if(!exploding){
			gl.glEnable(GL.GL_TEXTURE_2D);
			
			gl.glBindTexture(GL2.GL_TEXTURE_2D, PlaySceneBoard.projectileTexture[attack>0?0:1]);
		    gl.glBegin(GL2.GL_QUADS);
		    gl.glTexCoord2d(0.0f, 1.0f);	gl.glVertex2d(x-size, y-size);
		    gl.glTexCoord2d(1.0f, 1.0f);	gl.glVertex2d(x+size, y-size);
		    gl.glTexCoord2d(1.0f, 0.0f);	gl.glVertex2d(x+size, y+size);
		    gl.glTexCoord2d(0.0f, 0.0f);	gl.glVertex2d(x-size, y+size);
		    gl.glEnd();
	
			gl.glDisable(GL.GL_TEXTURE_2D);
		}else{
			int radius = (int)(PlaySceneBoard.currentTick-explodeTick)/20;
			gl.glColor4d(1.0f, 0.0f, 0.0f, 0.8f);
			GLAdditional.glCircle3d(gl, x, y, radius);
			if(radius>explosionRange)
				destruct();
		}
	}
}
