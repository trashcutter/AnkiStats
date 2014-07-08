package com.wildplot.android.densityFunctions;


import com.wildplot.android.rendering.interfaces.Function2D;
import com.wildplot.android.rendering.interfaces.Function3D;

public class Density2D implements Function3D {

	private double[][] points = {{},{}};
	private double hx, hy;
	private Function2D k;
	
	
	
	public Density2D(double[][] points, double hx, double hy, Function2D k) {
		super();
		this.points = points;
		this.hx = hx;
		this.hy = hy;
		this.k = k;
	}



	@Override
	public double f(double x, double y) {
		double sum = 0;
		
		double preSumFactor = 1.0/(points[0].length*hx*hy);
		
		for(int i = 0; i<points[0].length; i++){
			sum+=k.f((x-points[0][i])/hx)*k.f((y-points[1][i])/hy);
		}
		
		
		return preSumFactor*sum;
	}

}
