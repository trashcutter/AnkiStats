package com.wildplot.android.kernelFunctions;


import com.wildplot.android.rendering.interfaces.Function2D;

public class TricubeKernel implements Function2D {

	@Override
	public double f(double x) {
		return (Math.abs(x)<=1)?((70.0/81.0)*(1.0-Math.abs(x*x*x))*(1.0-Math.abs(x*x*x))*(1.0-Math.abs(x*x*x))):0;
	}

}
