package com.wildplot.system;

import com.wildplot.MyMath.Matrix_d;

////	Oregonator: a periodic chemical reaction
////	simplified description of this reaction:
//
////	BR_O3_- + BR_- -> H_Br_O_2
////	H_Br_O_2 + Br_- -> P		: unspecified (unimportant) prduct of the reaction
////	Br_O_3_- + H_Br_O_2 -> 2 H_Br_O_2 + Ce(IV)
////	2 H_Br_O_2 -> P
////	Ce(IV) -> Br_-
//
////	http://de.wikipedia.org/wiki/Belousov-Zhabotinsky-Reaktion
////	http://jkrieger.de/bzr/inhalt.html
//
//// our solvers won't work for this system!
//
public class Oregonator implements ODE_FUN
{
	double k0 = 1, k2 = 8e5, k3=1.28, k4= 2e3, k5=8, f=1, A=0.06, B=0.02;
	
	public Oregonator()
	{
		
	}

	// implementation of interface
	@Override
	public int get_neq() { return 3; }

	public Vec_d get(Vec_d x, double t)
	{
			Vec_d func= new Vec_d(3);
			func.set(0, k3*A*x.get(1)-k2*x.get(0)*x.get(1)+k5*A*x.get(0)-2*k4*x.get(0)*x.get(0));
			func.set(1, -k3*A*x.get(1) - k2*x.get(0)*x.get(1) + 0.5*f*k0*B*x.get(2));
			func.set(2, 2.0*k5*A*x.get(0)-k0*B*x.get(2));

			return func;
	}
	
	public Matrix_d dfdx(Vec_d x, double t){
		double[][] mat = {
			{ -k2*x.get(1)+k5*A-4*k4*x.get(0),	k3*A-k2*x.get(0),								0},
			{ -k2*x.get(1),				-k3*A-k2*x.get(0),	0.5*f*k0*B},
			{ 2*k5-A,		0, -k0*B},
			
		};
		return new Matrix_d(mat);
	}
	
	public Vec_d dfdt(Vec_d x, double t){
		double[] vec={0,0,0};
		return new Vec_d(vec);
	}
	
	public String ToString()
	{
		return "Oregonator: coefficients of reaction k="+k0;
	}
	
}
