package com.wildplot.system;

import com.wildplot.MyMath.Matrix_d;

public class StiffExample implements ODE_FUN{

	@Override
	public Vec_d get(Vec_d x, double t) {
		Vec_d f=new Vec_d(2);
		f.set(0,998*x.get(0)+1998*x.get(1));
		f.set(1,-999*x.get(0)-1999*x.get(1));
		return f;
	}
	
	public Matrix_d dfdx(Vec_d x, double t){
		double[][] mat = {
			{ 998,		1998  },
			{ -999,		-1999 }
		};
		return new Matrix_d(mat);
	}
	
	public Vec_d dfdt(Vec_d x, double t){
		double[] vec={0,0};
		return new Vec_d(vec);
	}

	@Override
	public int get_neq() {
		// TODO Auto-generated method stub
		return 2;
	}

}
