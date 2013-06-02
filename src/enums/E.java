/*Copyright 2012 Wong Cho Ching, all rights reserved.
 * This file is released under BSD 2-clause License.
 * For details, please read LICENSE.txt in this package.*/

package enums;

public class E {
	//for UFO attack
	public final static int IDLE = 0;
	public final static int ATTACKING = 1;
	public final static int RETREATING = 2;
	
	//for structureBuilt work()
	public final static int RELEASEUNIT = 0;
	public final static int RETREATUNIT = 1;
	
	//for structureBuilt states
	public final static int RELEASINGUNIT = 0;
	public final static int RETREATINGUNIT = 1;
	
	//for structureEntity category
	public final static int RESOURCES = 0x1;
	public final static int STORAGE = 0x2;
	public final static int OFFENSIVE = 0x4;
	public final static int DEFENSIVE = 0x8;
	public final static int REPAIR = 0x10;
	
}
