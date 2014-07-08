package com.wildplot.android.kernelFunctions;


import com.wildplot.android.rendering.interfaces.Function2D;

public class CauchyKernel implements Function2D {

	@Override
	public double f(double x) {
		return (1.0/(Math.PI*(1.0+x*x)));
	}

}
