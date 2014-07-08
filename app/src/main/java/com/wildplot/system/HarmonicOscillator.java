package com.wildplot.system;

import com.wildplot.MyMath.Matrix_d;

public class HarmonicOscillator implements ODE_FUN
{
	public double omega;//{get; private set;}
	public double tau;//{get; private set;}

	private double om2;

	public HarmonicOscillator(double omega, double tau)
	{
		this.omega=omega;
		this.tau=tau;
		this.om2=omega*omega;
	}

	// implementation of interface ODE_FUN
	@Override
	public int get_neq() {  return 2; }
	// in java:
	// public int get_neq() {return 2;}

	public Vec_d get(Vec_d x, double t)
	{
		Vec_d f = new Vec_d(2);
		f.set(0,x.get(1));
		f.set(1,-x.get(1) / tau - om2 * x.get(0));
		return f;
	}
	// in java you need the 'get()' function instead of 'this[]'
	
	public Matrix_d dfdx(Vec_d x, double t){
		double[][] mat = {{0,1},{-(omega*omega),-0.5}};
		return new Matrix_d(mat);
	}
	
	public Vec_d dfdt(Vec_d x, double t){
		double[] vec={0,0};
		return new Vec_d(vec);
	}

	@Override
	public String toString()
	{
		return "Harmonic Oscillator with omega=" + omega + " and relaxation time tau=" + tau;
	}
}
