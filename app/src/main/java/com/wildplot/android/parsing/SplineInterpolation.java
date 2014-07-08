package com.wildplot.android.parsing;

import com.wildplot.android.rendering.interfaces.Function2D;




/**
 * This class implements the spline interpolation of two given arrays x and y. 
 * The overall resulting interpolation originating from all splines can be 
 * accessed by a simple function call f(x).
 * 
 * @author Christian Otto, Richard Meier
 */
public class SplineInterpolation implements Function2D{
	
	private int n;
	private double[] x, y, u, r, k, a, b, c, d;
	
	public SplineInterpolation(double[] x, double[] y){
		if(x.length != y.length){
			System.err.println("Length of x and y are not identical!");
			System.exit(-1);
		}
		this.n = x.length;
		this.x = x;
		this.y = y;
		this.u = new double[n];
		this.r = new double[n];
		this.k = new double[n];
		this.a = new double[n-1];
		this.b = new double[n-1];
		this.c = new double[n-1];
		this.d = new double[n-1];
		
		sort(); // run Bubblesort
		calc(); 
		calcParameter();
	}
	
//	public static void main(String[] args){
//		double[] x = {9,7,4,5,2,3,1,6,8};
//		double[] y = {1,2,3,4,5,6,7,8,9};
//		SplineInterpolation si = new SplineInterpolation(x, y);
//		
//		System.out.println(si.f(7.5));
//	}
	
	
	@SuppressWarnings("unused")
	/**
	 * Used to print the x- and the corresponding y-values
	 */
	private void printValues(){
		for(int i=0; i<x.length; i++){
			System.out.printf("%1.1f\t%1.1f\n",x[i],y[i]);
		}
	}
	
	/**
	 * Bubblesort used to sort x- with the corresponding y-values
	 */
	private void sort(){
		boolean sorting = true;
		double tmpX = 0.0d, tmpY = 0.0d;
		
		while(sorting){
			sorting = false;
			for(int i=0; i<x.length-1; i++){
				if(x[i]>x[i+1]){
					tmpX = x[i];
					tmpY = y[i];
					
					x[i] = x[i+1];
					y[i] = y[i+1];
					
					x[i+1] = tmpX;
					y[i+1] = tmpY;
					sorting = true;
				}
			}
		}
	}
	
	private void calc(){
		u[1] = 2*(h(0)+h(1));
		r[1] = e(1)-e(0);
		for(int i=2; i < n-1; i++){
			u[i] = 2 * (h(i) + h(i-1)) - h(i-1) * h(i-1) / u[i-1];
			r[i] = (e(i) - e(i-1)) - r[i-1] * h(i-1) / u[i-1];
		}
		k[k.length-1] = 0;
		k[0]          = 0;
		for(int i=k.length-2; i>0; i--) k[i] = (r[i] - h(i) * k[i+1]) / u[i];
	}
	
	/**
	 * Calculates the distance between the given x value at the index i and the following x value at the index i+1.
	 * @param i index of the x value in the given array
	 * @return the distance
	 */
	private double h(int i){ return x[i+1]-x[i]; }
	
	private double e(int i){ return 6/h(i)*(y[i+1]-y[i]); }
	
	private void calcParameter(){
		for(int i=0; i<k.length-1; i++){
			d[i] = y[i];
			b[i] = k[i]/2;
			a[i] = (k[i+1]-k[i])/(6*h(i));
			c[i] = (y[i+1]-y[i])/h(i)-(2*h(i)*k[i]+h(i)*k[i+1])/6;
		}
	}
	
	/**
	 * Function to calculate values in a given interval and for a certain point
	 * @param t
	 * @param i
	 * @return
	 */
	private double S(double t, int i)
	{ 	
		//       d     + c * x         + b * x^2                  + a * x^3
		//return d[i]  + c[i]*(t-x[i]) + b[i]*Math.pow(t-x[i], 2) + a[i]*Math.pow(t-x[i], 3);
		
		//	   d    +    x     * (c    +    x     * (b    +    x     * a)) HORNERSCHEMA
		return d[i] + (t-x[i]) * (c[i] + (t-x[i]) * (b[i] + (t-x[i]) * a[i]));
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see rendering.Function2D#f(double)
	 */
	public double f(double x) {
		//Check which interval has to be used
		for(int i=1; i<this.x.length-1; i++){ 
		    if(x < this.x[i]) 
		        return S(x,i-1); 
		}
		return S(x,this.x.length-2);
	}
}
