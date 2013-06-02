/*Copyright 2012 Wong Cho Ching, all rights reserved.
 * This file is released under BSD 2-clause License.
 * For details, please read LICENSE.txt in this package.*/

package playSceneUI;

import java.awt.Point;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

public class Path {
	private Point[] nodes;
	public boolean loop;
	public int n;
	public int id;
	public StructureBuilt source;
	public Path(int size, StructureBuilt source, int id){
		nodes = new Point[size];
		this.source = source;
		this.id = id;
	}
	public void destruct(){
		for(int j=id;j<PlaySceneBoard.path_n;j++){
			PlaySceneBoard.path[j] = PlaySceneBoard.path[j+1];
			if(PlaySceneBoard.path[j]!=null)
				PlaySceneBoard.path[j].id = j;
		}
		PlaySceneBoard.path_n--;
	}
	public void addNode(int x, int y){
		nodes[n++] = new Point(x,y);
	}
	public Point getNodePos(int x){
		if(!loop){
			if(((x/n)%2)==0) //even number
				return nodes[x%n];
			else //odd number
				return nodes[n-(x%n)-1];
		}else{
			return nodes[x%n];
		}
	}
	public void blit(GL2 gl, boolean modifing){
	    gl.glPushMatrix();
	    gl.glLoadIdentity();
	    if(modifing)
	    	gl.glColor3f(0.0f, 1.0f, 0.0f);
	    else
	    	gl.glColor3f(1.0f, 0.0f, 0.0f);
	    gl.glLineWidth(2.0f);
	    gl.glBegin(GL.GL_LINE_STRIP);
		for(int i=0;i<n;i++){
	        gl.glVertex2d(nodes[i].x, nodes[i].y);
		}
	    gl.glEnd();
	    gl.glPointSize(5.0f);
	    gl.glBegin(GL.GL_POINTS);
		for(int i=0;i<n;i++){
	        gl.glVertex2d(nodes[i].x, nodes[i].y);
		}
	    gl.glEnd();
	    gl.glPopMatrix();
	}
}
