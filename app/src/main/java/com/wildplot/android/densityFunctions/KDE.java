package com.wildplot.android.densityFunctions;


import com.wildplot.android.rendering.interfaces.Function2D;

public class KDE implements Function2D {
	private double[] points;
	private double h;
	private Function2D k;
	
	
	public KDE(double[] points, double h, Function2D k) {
		super();
		this.points = points;
		this.h = h;
		this.k = k;
	}


	@Override
	public double f(double x) {
		double sum = 0;
		
		double preSumFactor = 1.0/(points.length*h);
		
		for(int i = 0; i<points.length; i++){
			sum+=k.f((x-points[i])/h);
		}
		
		
		return preSumFactor*sum;
	}

}
