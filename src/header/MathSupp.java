/*Copyright 2012 Wong Cho Ching, all rights reserved.
 * This file is released under BSD 2-clause License.
 * For details, please read LICENSE.txt in this package.*/

package header;

public class MathSupp {
	static double pi = 3.1415926535f;
	public static double distance(double x1, double y1, double x2, double y2){
		return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
	}
	public static double angle(double x1, double y1, double x2, double y2){
		//double tan =  Math.atan((y2-y1)/(x2-x1))*180;
		if(x1>x2)
			return Math.atan((y2-y1)/(x2-x1))*180/pi+180;
		return Math.atan((y2-y1)/(x2-x1))*180/pi;
	}
	public static int closest(int n, double ratio){
		int closestId = -1;
		double closestValue = 9999999999999.0f;
		for(int i=0;i<n;i++){
			double temp = Math.abs((double)i/(double)n-ratio);
			if(temp<closestValue){
				closestId = i;
				closestValue = temp;
			}
		}
		return closestId;
	}
}
