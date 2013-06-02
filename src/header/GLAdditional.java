/*Copyright 2012 Wong Cho Ching, all rights reserved.
 * This file is released under BSD 2-clause License.
 * For details, please read LICENSE.txt in this package.*/

package header;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

public class GLAdditional {
	static double Pi = 3.1415926535;
	public static void glCircle3d(GL2 gl, double x, double y, int radius) {
	    double angle; // in radians
	    gl.glBegin(GL.GL_TRIANGLE_FAN);
        gl.glVertex2d(x, y);
	    for(int i = 0; i < 65; i++) {
	        angle = i*2*Pi/64;
	        gl.glVertex2d(x + (Math.cos(angle) * radius), y + (Math.sin(angle) * radius));
	    }
	    gl.glEnd();
	}  
}
