package com.wildplot.android.densityFunctions;


import com.wildplot.android.rendering.interfaces.StepFunction2D;

public class ASH implements StepFunction2D {
	
	double x0, M, h;
	double[] points;	
	int firstBin = 0;
	int lastBin = 0;

	public ASH(double x0, double M, double h, double[] points) {
		super();
		this.x0 = x0;
		this.M = M;
		this.h = h;
		this.points = points;
		calcStartAndEndI();
	}
	
	private void calcStartAndEndI(){
		for(int i = 0; i< points.length; i++){
			//three cases, bigger, smaller, or in area
			int j = 0;
			if((points[i] > x0+ (j-1)*h) && (points[i] >x0 + j*h )) {
				while(!((points[i] >= x0+ (j-1)*h) && (points[i] <x0 + j*h )))
				{
					j++;
				}
				
			}
			if((points[i] < x0+ (j-1)*h) && (points[i] <x0 + j*h )){
				while(!((points[i] >= x0+ (j-1)*h) && (points[i] <x0 + j*h )))
				{
					j--;
				}
			}
			setBin(j);
		}
	}
	
	private void setBin(int j){
		if(j<firstBin){
			firstBin = j;
		}
		if(j> lastBin) {
			lastBin = j;
		}
	}

	@Override
	public double f(double x) {
		double sum = 0;
		
		for(int i = 0; i<points.length; i++){
			
			double sumPart = 0;
			for(int l = 0; l<M; l++){
				for(int j=firstBin; j<= lastBin; j++ ){
					sumPart+= isElementOfSubBin(points[i],j,l)* isElementOfSubBin(x,j,l);
				}
			}
			sum += sumPart/(M*h);
			
		}
		

		return sum/points.length;
	}
	
	private double isElementOfSubBin(double xi, int j, int l){
		return (xi >= ((x0+(j-1)*h + (l*h)/M)) && xi< (x0+j*h+(l*h)/M)  )?1:0;
	}


}
