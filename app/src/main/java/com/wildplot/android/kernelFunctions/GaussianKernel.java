package com.wildplot.android.kernelFunctions;


import com.wildplot.android.rendering.interfaces.Function2D;

public class GaussianKernel implements Function2D {

	@Override
	public double f(double x) {
		return (1.0/(Math.sqrt(2.0*Math.PI)))*Math.exp(-0.5*x*x);
	}

}
