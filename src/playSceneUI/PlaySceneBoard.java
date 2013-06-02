/*Copyright 2012 Wong Cho Ching, all rights reserved.
 * This file is released under BSD 2-clause License.
 * For details, please read LICENSE.txt in this package.*/

package playSceneUI;


import header.BGM;
import header.G;
import header.MathSupp;
import header.SFX;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;

import com.jogamp.opengl.util.FPSAnimator;

import enums.E;

import GUI.Domination;
import GUI.PlayScene;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import java.io.File;
import java.io.IOException;
import java.lang.System;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;

import locale.L;

public class PlaySceneBoard{
	private static int id = 0;
	public static final int NORMAL = id++;
	public static final int SETTINGPATH = id++;
	public static int states = NORMAL;

	public static UnitEntity[] unitEntity;
	static DynamicUnit[] unit; //TODO: modify buffer if needed.
	static int unit_n = 0;

	public static Path[] path; //TODO: modify buffer if needed.
	public static int path_n;

	static Cannon[] cannon; //TODO: modify buffer if needed.
	static int cannon_n = 0;

	static Projectile[] projectile; //TODO: modify buffer if needed.
	static int projectile_n = 0;

	static int[] cannonTexture = new int[4];
	static int[] projectileTexture = new int[2];

	final static int gridSize = 15;
	public final static int fps = 60;
	public final static double tps = 1000/(float)fps;
	public static double frameAdvance = 1;
	public static long currentTick = 0;
	
	public static JLabel[] currentValue = new JLabel[16];

	public static StructureEntity[] structureEntity;
	public static int[] structureEntityMap; //Since some structure may not be shown on the battle, a map for chosen structure to be used in the battle is required.

	public static int[] resourceEnumMap;
	public static int localResourceSeperator;
	public static String[] resourceName;
	public static String[] resourceIcon;
	public static double[][] resourceAmount;
	public static double[][] resourceInitValue;
	public static int[][] resourceCap;
	public static int[][] resourceInitCap;

	/*TODO public static DynamicUnit[] dynamicUnit = new DynamicUnit[51200];
	public static int dynamicUnit_n = 0;*/
	
	public static int boardW = 28; //TODO should not be fixed. It should varies for every map.
	public static int boardH = 29; //TODO should not be fixed. It should varies for every map.
	public static int[][] structureMask; //map for structureBuilt, -1 means empty
	public static StructureBuilt[] structureBuilt;
	public static int[] selectedStructure;
	public static int structureBuilt_n = 0;
	
	public static int currentPlayer = 0;
	public static int player_n = 2;
	public static int humanPlayer = 0;

	public static float sellValue = 0.8f; //TODO should not be fixed.

	private FPSAnimator frameController;
	private long previousTimeTick = System.currentTimeMillis();
	private long pausedTimeTick = System.currentTimeMillis();

	public static boolean initializing;
	public static boolean shiftHeld = false;

	public static boolean paused;
	public static boolean gameover;
	public static boolean victory;
	public static AI ai;

	final GLCanvas glcanvas;
    int boundWidth = 420;
    int boundHeight = 436;
    
    boolean buildProblem;
    
	public PlaySceneBoard(){
		initializing = true;

		unit = new DynamicUnit[10240]; //TODO: modify buffer if needed.
		unit_n = 0;
		path = new Path[5120]; //TODO: modify buffer if needed.
		path_n = 0;
		cannon = new Cannon[5120]; //TODO: modify buffer if needed.
		cannon_n = 0;
		projectile = new Projectile[10240]; //TODO: modify buffer if needed.
		projectile_n = 0;
		structureMask = new int[boardW][boardH]; //map for structureBuilt, -1 means empty
		structureBuilt = new StructureBuilt[boardW*boardH];
		selectedStructure = new int[2];
		structureBuilt_n = 0;
		
		currentPlayer = 0;
		currentTick = System.currentTimeMillis();
		frameAdvance = 1.0f;
		gameover = false;
		
		buildProblem = false;
		
		paused = false;

		for(int i=0;i<16;i++)
			currentValue[i] = new JLabel();
		for(int i=0;i<boardW;i++)
			Arrays.fill(structureMask[i], -1);
		

	    GLProfile glprofile = GLProfile.getDefault();
	    GLCapabilities glcapabilities = new GLCapabilities(glprofile);
	    glcanvas = new GLCanvas(glcapabilities);
	    glcanvas.setBounds(0, 36, boundWidth, boundHeight);
	    glcanvas.addGLEventListener(new Drawable());
	    glcanvas.addMouseListener(new MouseAdapter(){
	    	@Override public void mouseReleased(MouseEvent e) {
	    		mouseClicked = true;
			}
	    });
	    glcanvas.addFocusListener(new FocusListener(){
			@Override
			public void focusGained(FocusEvent e) {
	    		if(PlayScene.structureList.getSelectedIndex()>-1)
	    			mouseClicked = true;
			}
			@Override public void focusLost(FocusEvent e) {}
	    });
	    glcanvas.addMouseMotionListener(new MouseMotionListener(){
			@Override public void mouseDragged(MouseEvent e){}
			@Override public void mouseMoved(MouseEvent e) {
				glcanvas.requestFocusInWindow();
			}
	    });
	    frameController = new FPSAnimator(glcanvas, fps);
	    frameController.start();

	    PlayScene.playScene.add( glcanvas );

		buildStructure(new Point(0, 0), PlaySceneDataInit.BASE, 0);
		buildStructure(new Point(boardW-structureEntity[PlaySceneDataInit.BASE].width, boardH-structureEntity[PlaySceneDataInit.BASE].height), PlaySceneDataInit.BASE, 1);
		ai = new AI(1, PlayScene.stage);
		for(int i=0;i<player_n;i++){
			selectedStructure[i] = -1;
		}
		
		BGM.play("./music/playScene.wav", true);
	    initializing = false;
	}
	
	static final int cannonTexture_n = 4;
	private void loadCannonTextures(GL2 gl){
		BufferedImage image = null;
		gl.glGenTextures(cannonTexture_n, cannonTexture, 0);
		gl.glGenTextures(2, projectileTexture, 0);
		for(int i=0;i<cannonTexture_n+2;i++){
			try{
				switch(i){
					case 0:
						image = ImageIO.read(new File("./images/GL/structures/sniperTowerTop.png"));
					break;
					case 1:
						image = ImageIO.read(new File("./images/GL/structures/machineGunTowerTop.png"));
					break;
					case 2:
						image = ImageIO.read(new File("./images/GL/structures/healingTowerTop.png"));
					break;
					case 3:
						image = ImageIO.read(new File("./images/GL/structures/ultimateTowerTop.png"));
					break;
					case cannonTexture_n:
						image = ImageIO.read(new File("./images/GL/projectile.png"));
					break;
					case cannonTexture_n+1:
						image = ImageIO.read(new File("./images/GL/projectile2.png"));
					break;
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

			if(i<cannonTexture_n)
				gl.glBindTexture(GL2.GL_TEXTURE_2D, cannonTexture[i]);
			else
				gl.glBindTexture(GL2.GL_TEXTURE_2D, projectileTexture[i-cannonTexture_n]);
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
	
	private class Drawable implements GLEventListener{
        @Override
        public void reshape( GLAutoDrawable glautodrawable, int x, int y, int width, int height ) {
        	GL2 gl = glautodrawable.getGL().getGL2();
            gl.glMatrixMode(GL2.GL_PROJECTION);
            gl.glLoadIdentity();

            // coordinate system origin at lower left with width and height same as the window
            GLU glu = new GLU();
            glu.gluOrtho2D(0.0f, width, 0.0f, height);

			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glDisable(GL2.GL_DEPTH_TEST);
			gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);  
			gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
			gl.glBlendFunc (GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA); 
			gl.glEnable (GL2.GL_BLEND);

            gl.glLoadIdentity();

            gl.glViewport(0, 0, width, height);

        }
        
       @Override
        public void init( GLAutoDrawable glautodrawable ) {
		   GL2 gl = glautodrawable.getGL().getGL2();
		   for(int i=0;i<PlaySceneDataInit.unitEntity_n;i++)
			   unitEntity[i].loadTexture(gl);
		   for(int i=0;i<PlaySceneDataInit.structure_n;i++)
			   structureEntity[i].loadTexture(gl);
		   PlaySceneDataInit.loadTextures(gl);
		   
		   loadCannonTextures(gl);
        }
        
        @Override
        public void dispose( GLAutoDrawable glautodrawable ) {
        }
    	boolean previousPaused = false;
        @Override
        public void display( GLAutoDrawable glautodrawable ) {
        	paused = PlayScene.tglbtnPause.isSelected();
        	if(!paused){
        		if(previousPaused)
        			unpause();
        		if(!gameover)
        			nextFrame();
	        	previousPaused = false;
        	}else{
        		if(!previousPaused)
        			pausedTimeTick = System.currentTimeMillis();
        		previousPaused = true;
        	}
        	if(!gameover)
        		handleInput();
    		draw(glautodrawable);
        }
	}
	Point mousepos = new Point();
	static Point selectedStructurePos = new Point();
	boolean mouseClicked = false;
	int structureToBuild;
	
	private static boolean checkDistanceEligibility(Point2D pos, int owner){
		for(int k=0;k<structureBuilt_n;k++){
			if(structureBuilt[k].owner==owner){
				double Bx = structureBuilt[k].pos.x+structureEntity[structureBuilt[k].type].width/2;
				double By = structureBuilt[k].pos.y+structureEntity[structureBuilt[k].type].height/2;
				double Px = pos.getX();
				double Py = pos.getY();
				if(MathSupp.distance(Bx,By,Px,Py)<=5){
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean checkBuildLimit(int type, int owner){
		if(type==PlaySceneDataInit.UCENTER||type==PlaySceneDataInit.SNIPER||type==PlaySceneDataInit.MACHINEGUN||type==PlaySceneDataInit.SPLASHTOWER){ //TODO Bad design. Should set it in PlaySceneDataInit as an attribute
			int n=0;
			for(int i=0;i<structureBuilt_n;i++){
				if(structureBuilt[i].owner==owner&&structureBuilt[i].type==type){
					n++;
				}
			}
			if(n>=5)
				return false;
		}
		return true;
	}
	
	public static boolean buildStructure(Point pos, int type, int owner){
		boolean fail = false;
    	if(G.levelCompleted+owner<structureEntity[type].unlockReq&&owner==humanPlayer) //TODO G.levelCompleted+owner does not make sense.
    		return false;
		if(pos.x<boardW&&pos.y<boardH){
			if(!checkBuildLimit(type, owner))
				return false;
			for(int i=0;i<structureEntity[type].width;i++){
				for(int j=0;j<structureEntity[type].height;j++){
					if(pos.x+i>=boardW||pos.y+j>=boardH||pos.x+i<0||pos.y+j<0){
						fail = true;
						break;
					}
					if(structureMask[pos.x+i][pos.y+j]!=-1){
						fail = true;
						break;
					}
				}
			}
			Point2D centerPos = new Point2D.Double(pos.x+structureEntity[type].width/2,pos.y+structureEntity[type].height/2);
			if(!fail&&(initializing||checkDistanceEligibility(centerPos, owner))){
				if(checkCost(type, 0, owner)||initializing){
					for(int i=0;i<structureEntity[type].width;i++){
						for(int j=0;j<structureEntity[type].height;j++)
							structureMask[pos.x+i][pos.y+j] = structureBuilt_n;
					}
					structureBuilt[structureBuilt_n] = new StructureBuilt(type, pos, structureEntity[type].properties_n, structureBuilt_n, owner);
					selectedStructure[owner] = structureBuilt_n;
					if(!shiftHeld&&owner==currentPlayer)
						PlayScene.structureList.clearSelection();
					if((structureEntity[type].category&(E.DEFENSIVE|E.REPAIR))!=0){
						cannon[cannon_n] = new Cannon(cannon_n, structureBuilt[structureBuilt_n], owner);
						cannon_n++;
					}
					if(!initializing&&owner==currentPlayer)
						SFX.play("./sfx/build.wav");
					structureBuilt_n++;
				}else{
					fail = true;
				}
			}
		}
		return !fail;
	}
	
	
    private void handleInput(){
    	structureToBuild = PlayScene.structureList.getSelectedIndex()-1;
    	mousepos = MouseInfo.getPointerInfo().getLocation();
    	mousepos.x -= Domination.frame.getX();
    	mousepos.y -= Domination.frame.getY();
    	mousepos.x -= PlayScene.playScene.getX();
    	mousepos.y -= PlayScene.playScene.getY();
    	mousepos.y = Domination.frame.getHeight()-mousepos.y; //Used to invert y-axis.
    	boolean fail = false;
		if(mouseClicked){
			if(states==NORMAL){
		    	if(structureToBuild>-1){
		    		structureToBuild = structureEntityMap[structureToBuild];
			    	selectedStructurePos.x = (mousepos.x-(structureEntity[structureToBuild].width-1)*gridSize/2)/gridSize;
			    	selectedStructurePos.y = (mousepos.y-(structureEntity[structureToBuild].width-1)*gridSize/2)/gridSize;
			    	fail = !buildStructure(selectedStructurePos, structureToBuild, currentPlayer);
	    		}else{
	    			structureToBuild = -1;
			    	selectedStructurePos.x = mousepos.x/gridSize;
			    	selectedStructurePos.y = mousepos.y/gridSize;
			    	clearUI();
	    		}
		    	if(!fail){
		    		int oldSelectedStructure = selectedStructure[currentPlayer];
		    		if(selectedStructurePos.x>=0&&selectedStructurePos.y>=0&&selectedStructurePos.x<boardW&&selectedStructurePos.y<boardH)
		    			selectedStructure[currentPlayer] = structureMask[selectedStructurePos.x][selectedStructurePos.y];
			    	if(selectedStructure[currentPlayer]>-1){
		    			if(structureBuilt[selectedStructure[currentPlayer]].owner==currentPlayer){
				    		structureEntity[structureBuilt[selectedStructure[currentPlayer]].type].updateUI(structureBuilt[selectedStructure[currentPlayer]].currentLevel, StructureEntity.NORMAL, currentPlayer);
					    	structureEntity[structureBuilt[selectedStructure[currentPlayer]].type].frameValueSync();
		    			}else{
				    		selectedStructure[currentPlayer] = -1;
		    			}
			    	}else{
			    		selectedStructure[currentPlayer] = buildProblem?oldSelectedStructure:-1;
			    	}
		    	}
			}else if(states==SETTINGPATH){
				if(mousepos.x>=0&&mousepos.y>=0&&mousepos.x<=boundWidth-gridSize/2&&mousepos.y<=boundHeight-gridSize/2&&selectedStructure[currentPlayer]>-1)
					structureBuilt[selectedStructure[currentPlayer]].pathPointer.addNode((int)(mousepos.x/gridSize)*gridSize+gridSize/2, (int)(mousepos.y/gridSize)*gridSize+gridSize/2);
			}
		}
    }
    private void blitStructure(int x, int y, int type, int id, GL2 gl){
    	int w = structureEntity[type].width;
    	int h = structureEntity[type].height;

        float x1 = x*gridSize;
        float x2 = x*gridSize+w*gridSize;
        float y1 = y*gridSize;
        float y2 = y*gridSize+h*gridSize;
	    
		gl.glEnable(GL.GL_TEXTURE_2D);
		if(id>-1)
			gl.glBindTexture(GL2.GL_TEXTURE_2D, structureEntity[type].texture[structureBuilt[id].owner==currentPlayer?0:1]);
		else
			gl.glBindTexture(GL2.GL_TEXTURE_2D, structureEntity[type].texture[0]);
	    gl.glBegin(GL2.GL_QUADS);
	    gl.glTexCoord2d(0.0f, 1.0f);	gl.glVertex2d(x1, y1);
	    gl.glTexCoord2d(1.0f, 1.0f);	gl.glVertex2d(x2, y1);
	    gl.glTexCoord2d(1.0f, 0.0f);	gl.glVertex2d(x2, y2);
	    gl.glTexCoord2d(0.0f, 0.0f);	gl.glVertex2d(x1, y2);
	    gl.glEnd();
		gl.glDisable(GL.GL_TEXTURE_2D);

    }
    private void blitProblem(int x, int y, int type, int id, GL2 gl){
    	int w = structureEntity[type].width;
    	int h = structureEntity[type].height;

        float x1 = x*gridSize;
        float x2 = x*gridSize+w*gridSize;
        float y1 = y*gridSize;
        float y2 = y*gridSize+h*gridSize;

    	gl.glEnable(GL.GL_TEXTURE_2D);
		boolean go = false;
		if(id>-1){
			if(structureBuilt[id].owner==currentPlayer&&structureBuilt[id].problem>-1&&(currentTick/500)%2==0){
				gl.glBindTexture(GL2.GL_TEXTURE_2D, PlaySceneDataInit.texture[structureBuilt[id].problem]);
				go = true;
			}
		}else{
			if(!checkCost(type,0,currentPlayer)){
				gl.glBindTexture(GL2.GL_TEXTURE_2D, PlaySceneDataInit.texture[resourceEnumToId(PlaySceneDataInit.GOLD)]);
				go = true;
			}else if(id==-2){
				gl.glBindTexture(GL2.GL_TEXTURE_2D, PlaySceneDataInit.texture[resourceEnumToId(PlaySceneDataInit.FARAWAY)]);
				go = true;
			}else if(id==-3){
				gl.glBindTexture(GL2.GL_TEXTURE_2D, PlaySceneDataInit.deselectIcon[0]);
			    gl.glBegin(GL2.GL_QUADS);
			    gl.glTexCoord2d(0.0f, 1.0f);	gl.glVertex2d(x1, y1);
			    gl.glTexCoord2d(1.0f, 1.0f);	gl.glVertex2d(x2, y1);
			    gl.glTexCoord2d(1.0f, 0.0f);	gl.glVertex2d(x2, y2);
			    gl.glTexCoord2d(0.0f, 0.0f);	gl.glVertex2d(x1, y2);
			    gl.glEnd();
			}
		}
		if((id>=0?structureBuilt[id].upgrading&&structureBuilt[id].owner==currentPlayer:false)&&(currentTick/500)%2==1){
			gl.glBindTexture(GL2.GL_TEXTURE_2D, PlaySceneDataInit.upgradeIcon[0]);
		    gl.glBegin(GL2.GL_QUADS);
		    gl.glTexCoord2d(0.0f, 1.0f);	gl.glVertex2d(x1, y1);
		    gl.glTexCoord2d(1.0f, 1.0f);	gl.glVertex2d(x2, y1);
		    gl.glTexCoord2d(1.0f, 0.0f);	gl.glVertex2d(x2, y2);
		    gl.glTexCoord2d(0.0f, 0.0f);	gl.glVertex2d(x1, y2);
		    gl.glEnd();
		}else if(go){
		    gl.glBegin(GL2.GL_QUADS);
		    gl.glTexCoord2d(0.0f, 1.0f);	gl.glVertex2d(x1, y1);
		    gl.glTexCoord2d(1.0f, 1.0f);	gl.glVertex2d(x2, y1);
		    gl.glTexCoord2d(1.0f, 0.0f);	gl.glVertex2d(x2, y2);
		    gl.glTexCoord2d(0.0f, 0.0f);	gl.glVertex2d(x1, y2);
		    gl.glEnd();

			gl.glBindTexture(GL2.GL_TEXTURE_2D, PlaySceneDataInit.deselectIcon[0]);
		    gl.glBegin(GL2.GL_QUADS);
		    gl.glTexCoord2d(0.0f, 1.0f);	gl.glVertex2d(x1, y1);
		    gl.glTexCoord2d(1.0f, 1.0f);	gl.glVertex2d(x2, y1);
		    gl.glTexCoord2d(1.0f, 0.0f);	gl.glVertex2d(x2, y2);
		    gl.glTexCoord2d(0.0f, 0.0f);	gl.glVertex2d(x1, y2);
		    gl.glEnd();
		}
		gl.glDisable(GL.GL_TEXTURE_2D);
    }
    private void blitStructureHealthBar(int x, int y, int type, int id, GL2 gl){
	    double currentHealth = structureBuilt[id].currentValue[structureEntity[type].getPropertyIDByResourceEnum(PlaySceneDataInit.HEALTH)];
	    double healthCap = structureEntity[type].value[structureEntity[type].getPropertyIDByResourceEnum(PlaySceneDataInit.HEALTH)][structureBuilt[id].currentLevel];
	   if(currentHealth<healthCap){
	    	int w = structureEntity[type].width;
	    	int h = structureEntity[type].height;

	        float x1 = x*gridSize;
	        float x2 = x*gridSize+w*gridSize;
	        float y1 = y*gridSize+h*gridSize*80/100;
	        float y2 = y*gridSize+h*gridSize;
		    gl.glColor4d(0.5f, 0.2f, 0.2f, 0.6f);
		    gl.glBegin(GL2.GL_QUADS);
		    gl.glVertex2d(x1, y1);
		    gl.glVertex2d(x2, y1);
		    gl.glVertex2d(x2, y2);
		    gl.glVertex2d(x1, y2);
		    gl.glEnd();

		    gl.glBegin(GL2.GL_QUADS);
	        gl.glColor4d(0.2f, 0.5f, 0.2f, 0.6f);
		    gl.glVertex2d(x1, y1);
		    gl.glVertex2d(x1+(x2-x1)*currentHealth/healthCap, y1);
		    gl.glVertex2d(x1+(x2-x1)*currentHealth/healthCap, y2);
		    gl.glVertex2d(x1, y2);
		    gl.glEnd();
		    gl.glColor4d(1.0f, 1.0f, 1.0f, 1.0f);
	   }
    }
    int framePos = 0;
    private void draw(GLAutoDrawable glautodrawable){
    	buildProblem = false;
    	GL2 gl = glautodrawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
		if(states==NORMAL){
	    	if(structureToBuild>-1){ //building a structure.
	    		if(G.levelCompleted+currentPlayer>=structureEntity[structureToBuild].unlockReq){ // TODO G.levelCompleted+currentPlayer does not make sense
		    		int x = (mousepos.x-(structureEntity[structureToBuild].width-1)*gridSize/2)/gridSize;
		    		int y = (mousepos.y-(structureEntity[structureToBuild].width-1)*gridSize/2)/gridSize;
	
					Point2D centerPos = new Point2D.Double(x+structureEntity[structureToBuild].width/2,y+structureEntity[structureToBuild].height/2);
					if(!checkDistanceEligibility(centerPos, currentPlayer)){
						blitStructure(x, y, structureToBuild, -2, gl);
						blitProblem(x, y, structureToBuild, -2, gl);
						buildProblem = true;
					}else{
						boolean fail = false;
						for(int i=0;i<structureEntity[structureToBuild].width;i++){
							for(int j=0;j<structureEntity[structureToBuild].height;j++){
								if(x+i>=boardW||y+j>=boardH||x+i<0||y+j<0){
									fail = true;
									break;
								}
								if(structureMask[x+i][y+j]!=-1){
									fail = true;
									break;
								}
							}
						}
						if(!checkBuildLimit(structureToBuild, currentPlayer))
							fail = true;
						if(fail){
							blitStructure(x, y, structureToBuild, -3, gl);
							blitProblem(x, y, structureToBuild, -3, gl);
							buildProblem = true;
						}else{
							blitStructure(x, y, structureToBuild, -1, gl);
							blitProblem(x, y, structureToBuild, -1, gl);
						}
					}
				}
	    	}
		}
    	if(selectedStructure[currentPlayer]>-1){ //blit selected structure
	        gl.glColor4fv(new float[]{1f, 1f, 1f, 1f}, 0);
	    	int w = structureEntity[structureBuilt[selectedStructure[currentPlayer]].type].width;
	    	int h = structureEntity[structureBuilt[selectedStructure[currentPlayer]].type].height;
			gl.glRectf(structureBuilt[selectedStructure[currentPlayer]].pos.x*gridSize, structureBuilt[selectedStructure[currentPlayer]].pos.y*gridSize,
					structureBuilt[selectedStructure[currentPlayer]].pos.x*gridSize+gridSize*w, structureBuilt[selectedStructure[currentPlayer]].pos.y*gridSize+gridSize*h);
			blitStructure(structureBuilt[selectedStructure[currentPlayer]].pos.x, structureBuilt[selectedStructure[currentPlayer]].pos.y, structureBuilt[selectedStructure[currentPlayer]].type, selectedStructure[currentPlayer], gl);
			blitProblem(structureBuilt[selectedStructure[currentPlayer]].pos.x, structureBuilt[selectedStructure[currentPlayer]].pos.y, structureBuilt[selectedStructure[currentPlayer]].type, selectedStructure[currentPlayer], gl);
			if((structureEntity[structureBuilt[selectedStructure[currentPlayer]].type].category&(E.DEFENSIVE|E.REPAIR))!=0){
				for(int i=0;i<cannon_n;i++){
					if(cannon[i].source==structureBuilt[selectedStructure[currentPlayer]]){
						cannon[i].blitRange(gl);
						break;
					}
				}
			}
			if(structureBuilt[selectedStructure[currentPlayer]].pathPointer!=null){
    			if(states==NORMAL){
    				structureBuilt[selectedStructure[currentPlayer]].pathPointer.blit(gl, false);
    			}else if(states==SETTINGPATH){
    				structureBuilt[selectedStructure[currentPlayer]].pathPointer.blit(gl, true);
    			}
			}
    	}
    	for(int i=0;i<structureBuilt_n;i++){
    		if(i!=selectedStructure[currentPlayer])
    			blitStructure(structureBuilt[i].pos.x, structureBuilt[i].pos.y, structureBuilt[i].type, i, gl);
    	}
    	for(int i=0;i<cannon_n;i++)
    		cannon[i].blit(gl);
    	for(int i=0;i<structureBuilt_n;i++)
    		blitProblem(structureBuilt[i].pos.x, structureBuilt[i].pos.y, structureBuilt[i].type, i, gl);
    	for(int i=0;i<unit_n;i++)
    		unit[i].blit(gl);
    	for(int i=0;i<projectile_n;i++)
    		projectile[i].blit(gl);

    	for(int i=0;i<structureBuilt_n;i++)
    		blitStructureHealthBar(structureBuilt[i].pos.x, structureBuilt[i].pos.y, structureBuilt[i].type, i, gl);
    	for(int i=0;i<unit_n;i++)
    		unit[i].blitHealthBar(gl);
		gl.glFlush();
		mouseClicked = false;

    	if(framePos%10==0)
    		PlaySceneDataInit.updateResourcePanelUI();
		framePos++;
    }
    
    private void nextFrame(){
    	currentTick = System.currentTimeMillis();
    	frameAdvance = ((double)(currentTick-previousTimeTick))/tps;
    	previousTimeTick = System.currentTimeMillis();

    	//special variables
    	for(int i=0;i<player_n;i++){
	    	resourceAmount[i][resourceEnumToId(PlaySceneDataInit.POPULATION_AVAILABLE)] = resourceAmount[i][resourceEnumToId(PlaySceneDataInit.POPULATION)];
	    	resourceCap[i][resourceEnumToId(PlaySceneDataInit.POPULATION_AVAILABLE)] = (int)resourceAmount[i][resourceEnumToId(PlaySceneDataInit.POPULATION)];
    	}

    	//make changes to states and update UI
    	boolean updateStructureUI = false;
    	for(int h=0;h<PlaySceneDataInit.structure_n;h++){
	    	for(int i=0;i<structureBuilt_n;i++){
	    		if(structureBuilt[i].type==h){
		    		structureBuilt[i].calculateOccupy();
		    		StructureEntity structureEnt = structureEntity[structureBuilt[i].type];
		    		if(structureEnt.updateInterval>-1){
		    			if(i==selectedStructure[currentPlayer]&&(currentTick-structureBuilt[i].previousUpdate>structureEnt.updateInterval||structureBuilt[i].upgrading))
		    				updateStructureUI = true;
		    			if(structureBuilt[i].upgrading){
		    				if(currentTick-structureBuilt[i].previousUpdate>structureEnt.value[structureEnt.getPropertyIDByResourceEnum(PlaySceneDataInit.UPGRADETIME)][structureBuilt[i].currentLevel]*1000){
		    					structureBuilt[i].previousUpdate += structureEnt.value[structureEnt.getPropertyIDByResourceEnum(PlaySceneDataInit.UPGRADETIME)][structureBuilt[i].currentLevel]*1000;
		    					structureBuilt[i].upgrading = false;
		    					if(i==selectedStructure[currentPlayer])
		    						structureEntity[structureBuilt[i].type].updateUI(structureBuilt[i].currentLevel, StructureEntity.NORMAL, currentPlayer);
		    				}
		    			}
		    			if(!structureBuilt[i].upgrading){
			    			while(currentTick-structureBuilt[i].previousUpdate>structureEnt.updateInterval){
			        			structureBuilt[i].previousUpdate += structureEnt.updateInterval;
			        			structureBuilt[i].calculateResourceOnFrame();
			    			}
		    			}
		    		}
		    		structureBuilt[i].frame();
	    		}
	    	}
    	}
    	if(updateStructureUI&&selectedStructure[currentPlayer]>-1){
    		structureEntity[structureBuilt[selectedStructure[currentPlayer]].type].updateUI(structureBuilt[selectedStructure[currentPlayer]].currentLevel, StructureEntity.NORMAL, currentPlayer);
	    	structureEntity[structureBuilt[selectedStructure[currentPlayer]].type].frameValueSync();
    	}

    	for(int i=0;i<cannon_n;i++){
    		if(!cannon[i].source.upgrading)
    			cannon[i].nextFrame();
    	}
    	for(int i=0;i<unit_n;i++){
    		if(!unit[i].source.upgrading)
    			unit[i].nextFrame();
    	}
    	for(int i=0;i<projectile_n;i++)
    		projectile[i].nextFrame();

    	for(int i=0;i<resourceCap.length;i++){
    		for(int j=0;j<resourceCap[i].length;j++){
    			if(resourceAmount[i][j]>resourceCap[i][j])
    				resourceAmount[i][j] = resourceCap[i][j];
    		}
    	}
    	
		ai.processAI();
    }
    private void unpause(){
    	for(int i=0;i<structureBuilt_n;i++){
    		if(structureEntity[structureBuilt[i].type].updateInterval>-1){
    			if(structureBuilt[i].previousUpdate==0) // The structure is built when pausing.
    				structureBuilt[i].previousUpdate = System.currentTimeMillis();
    			else
    				structureBuilt[i].previousUpdate += System.currentTimeMillis()-pausedTimeTick;
    		}
    	}
    	previousTimeTick += System.currentTimeMillis()-pausedTimeTick;
    }
	public static void clearUI(){
		PlayScene.lblStructureName.setVisible(false);
		PlayScene.structurePropTable.setVisible(false);
		PlayScene.structurePropTable.getTableHeader().setVisible(false);
		PlayScene.lblStructureDescription.setVisible(false);
		PlayScene.btnStructureUpgrade.setVisible(false);
		PlayScene.tglbtnStrucutreSell.setVisible(false);
		PlayScene.StructureUI.removeAll();
		PlayScene.structureDetails.removeAll();
		PlayScene.buildingBar.setVisible(false);
		PlayScene.rightPanel2.revalidate();
		PlayScene.rightPanel2.repaint();
	}
	public static void processUpgrade(int id){ //selectedStructure[player]
		if(structureBuilt[id].upgradingCompleted){
			structureBuilt[id].upgrade();
		}
	}
	public static void processSell(int id){ // migrate it to StructureBuilt
		if(structureBuilt[id].upgradingCompleted){
			int owner = structureBuilt[id].owner;
			structureBuilt[id].sell(id); //do this at very first because selectedStructure is going to be modifies.
			selectedStructure[owner] = -1;
			PlayScene.structureList.clearSelection();
			clearUI();
		}
	}
	//library
	public static boolean checkCost(int structureType, int level, int player){
		StructureEntity e = PlaySceneBoard.structureEntity[structureType];
		if(level<=e.maxLevel||structureType==PlaySceneDataInit.BASE){
			boolean ok = true;
			for(int i=0;i<e.properties_n;i++){
				int propId = resourceEnumToId(e.propertiesEnum[i]);
				if(propId>-1){
					int dummy = PlaySceneDataInit.DEDUCT|PlaySceneDataInit.PREREQ|PlaySceneDataInit.COST;
					if((e.propertiesType[i]&dummy)==dummy){
						if(PlaySceneBoard.resourceAmount[player][propId]<e.value[i][level]*
								(e.getPropertyIDByResourceEnum(PlaySceneDataInit.GOLD)==-1?
									1.0f:
									((e.propertiesType[e.getPropertyIDByResourceEnum(PlaySceneDataInit.GOLD)]|PlaySceneDataInit.VARL)==0||
									ai==null||player==humanPlayer)?
										1.0f:ai.varlMultiplier)){
							ok = false;
						}
					}
				}
			}
			return ok;
		}
		return false;
	}
	public static int resourceEnumToId(int Enum){
		for(int i=0;i<PlaySceneBoard.resourceEnumMap.length;i++){
			if(PlaySceneBoard.resourceEnumMap[i]==Enum)
				return i;
		}
		return -1;
	}
	
	public static void gameOver(){
		PlayScene.lblUnlocked.setVisible(false);
		if(victory){
			PlayScene.victoryLabel.setImage("./images/victory.png");
			PlayScene.lblCongrats.setText("Congratulation!");
			if(PlayScene.stage+1>G.levelCompleted){
				G.levelCompleted++;
				G.saveData();
				switch(PlayScene.stage){
					case 0:
						PlayScene.lblUnlocked.setText("Machine Gun Tower unlocked!");
					break;
					case 1:
						PlayScene.lblUnlocked.setText("Hospital unlocked!");
					break;
					case 2:
						PlayScene.lblUnlocked.setText("Ultimate Tower unlocked!");
					break;
				}
				PlayScene.lblUnlocked.setVisible(true);
			}
			BGM.play("./music/victory.wav", false);
		}else{
			PlayScene.victoryLabel.setImage("./images/defeated.png");
			PlayScene.lblCongrats.setText("Better luck next time!");
			BGM.play("./music/defeated.wav", false);
		}

		PlayScene.result.setVisible(true);
		clearUI();
	}

}
