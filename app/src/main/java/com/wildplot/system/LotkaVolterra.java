package com.wildplot.system;

import com.wildplot.MyMath.Matrix_d;

public class LotkaVolterra implements ODE_FUN
{
	private double a,b,c,d;

	public LotkaVolterra(double a, double b, double c, double d)
	{
		this.a = a; this.b = b; this.c = c; this.d = d;
	}

	// implementation of interface
	@Override
	public int get_neq() { return 2; }

	public Vec_d get(Vec_d x, double t)
	{
		Vec_d f = new Vec_d(2);
		f.set(0,x.get(0) * (a - b * x.get(1)));
		// x.get(0) * (a - b * x.get(1))	=	x.get(0)*a-b*x.get(1)*x.get(0)
		f.set(1, -x.get(1) * (c - d * x.get(0)));
		// -x.get(1) * (c - d * x.get(0))	=	-x.get(1)*c+d*x.get(0)*x.get(1)
		return f;
	}
	
	public Matrix_d dfdx(Vec_d x, double t){
		double[][] mat = {
			{ a-b*x.get(1),			-b*x.get(0)   },
			{ d*x.get(1),			-c+d*x.get(0) }
		};
		return new Matrix_d(mat);
	}
	
	public Vec_d dfdt(Vec_d x, double t){
		double[] vec={0,0};
		return new Vec_d(vec);
	}

	@Override
	public String toString()
	{
		return "Lotka-Volterra-System with a=" + a + " b=" + b + " c=" + c + " d=" + d;
	}
}
