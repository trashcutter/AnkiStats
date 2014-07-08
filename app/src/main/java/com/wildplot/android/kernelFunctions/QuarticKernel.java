package com.wildplot.android.kernelFunctions;


import com.wildplot.android.rendering.interfaces.Function2D;

/**
 * also called BiweightKernel
 *
 */
public class QuarticKernel implements Function2D {

	@Override
	public double f(double x) {
		return (Math.abs(x)<=1)?((15.0/16.0)*(1.0-(x*x))*(1.0-(x*x))):0;
	}

}
