package com.wildplot.android.regressionFunctions;


import com.wildplot.MyMath.Matrix_d;
import com.wildplot.MyMath.Polynomial;
import com.wildplot.MyMath.Vector_d;
import com.wildplot.android.rendering.interfaces.Function2D;

public class LinearRegression implements Function2D {

	
	private final int X = 0;
	private final int Y = 1;
	private double[][] points;
	private int M;
	private double lambda;
	private Polynomial function;
	
	public LinearRegression(double[][] points, int m, double lambda) {
		super();
		this.points = points;
		M = m;
		this.lambda = lambda;
		calc();
	}

	private void calc(){
		double[][] equationSystem = new double[M+1][M+1];
		double[] rightSideOfEquation = new double[M+1];
		for(int i = 0; i<=M; i++){
		    for(int m=0; m<=M; m++){
                for(int n = 0; n< points[0].length; n++){
                    equationSystem[i][m] += Math.pow(points[X][n], i+m);
                }
                if(i==m){
                    equationSystem[i][m] += lambda;
                }
            }
			
			for(int n = 0; n< points[0].length; n++){
				rightSideOfEquation[i] += points[Y][n]*Math.pow(points[X][n], i); 
			}
			
		}
		Matrix_d eqMatrix = new Matrix_d(equationSystem);

		Vector_d rightSideVector = new Vector_d(rightSideOfEquation);
		eqMatrix.print("Equation System left side");
		rightSideVector.print("Equation System right side");
//		System.out.println("Creating inverse Matrix of equation System with Gauss jordan");
//		eqMatrix.inv().print("Test print of inverse equation Matrix");
		Vector_d omegaVec = eqMatrix.solve(rightSideVector);
		
		omegaVec.print("Omega Vector:");
		
		function = new Polynomial(omegaVec);
		//System.err.println("Error of Regression for current m: "+HistoGramControl.calcError(function, points));
		
	}
	
	@Override
	public double f(double x) {
		
		return function.get(x);
	}

}
