package com.wildplot.android.kernelFunctions;


import com.wildplot.android.rendering.interfaces.Function2D;

public class TriangularKernel implements Function2D {

	@Override
	public double f(double x) {
		return (Math.abs(x)<=1)?(1.0-Math.abs(x)):0;
	}

}
