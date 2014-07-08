package com.wildplot.system;

import com.wildplot.MyMath.Matrix_d;

public interface ODE_FUN {
	// the right hand side of this ode-system
	Vec_d get(Vec_d x, double t);

	// number of equations in this ode-system
	int get_neq();
	
	Matrix_d dfdx(Vec_d x, double t);
	Vec_d dfdt(Vec_d x, double t);
}
