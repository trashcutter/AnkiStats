package com.wildplot.system;

import com.wildplot.MyMath.Matrix_d;

// a set of 3 chemical reactions
// our solvers won't work for this system!
public class TripleReaction implements ODE_FUN
{
	public int get_neq() { return 3; }

	public Vec_d get(Vec_d x, double t)
	{
			Vec_d f=new Vec_d(3);
			f.set(0, -0.013*x.get(0) + 1.000*x.get(0)*x.get(2));
			f.set(1, -2500.0*x.get(1)*x.get(2));
			f.set(2, -0.013*x.get(0)-1000.0*x.get(0)*x.get(2)-2500.0*x.get(1)*x.get(2));
			return f;
	}
	
	public Matrix_d dfdx(Vec_d x, double t){
		double[][] mat = {
			{ -0.013 + 1000+x.get(2), 	0.0 							  ,	1.000*x.get(0) },
			{ 0.0					,	-2500*x.get(2)					  ,	-2500.0*x.get(1)},
			{ -0.013-1000.0*x.get(2),	-2500*x.get(2)					  ,	-1000.0*x.get(0)-2500.0*x.get(1)}
		};
		return new Matrix_d(mat);
	}
	
	public Vec_d dfdt(Vec_d x, double t){
		double[] vec={0,0};
		return new Vec_d(vec);
	}
}