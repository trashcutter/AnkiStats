package com.wildplot.android.kernelFunctions;


import com.wildplot.android.rendering.interfaces.Function2D;

public class EpanechnikovKernel implements Function2D {

	@Override
	public double f(double x) {
		return (Math.abs(x)<=1)?(0.75*(1.0-(x*x))):0;
	}

}
