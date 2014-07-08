package com.wildplot.android.control;

import java.util.HashMap;

import com.wildplot.android.parsing.FunctionParser;
import com.wildplot.android.rendering.interfaces.Function2D;
import com.wildplot.android.rendering.interfaces.Function3D;



public class FunctionParserWrapper implements Function3D, Function2D{
	
	
	private HashMap<Long, HashMap<Long,Double>> map = new HashMap<Long, HashMap<Long,Double>>();
	private FunctionParser funcParse = null;
	private String funcName = null;
	
	boolean buffered = false;
	public FunctionParserWrapper(FunctionParser funcParse, String funcName, boolean buffered) {
		super();
		this.funcParse = funcParse;
		this.funcName = funcName;
		this.buffered = buffered;
	}
	
	public FunctionParserWrapper(FunctionParser funcParse, String funcName) {
		super();
		this.funcParse = funcParse;
		this.funcName = funcName;
		this.buffered = false;
	}


	public double f2(double x, double y) {
		double[] doubArr = {x,y};
		return funcParse.getFunctionOutput(funcName, doubArr);
	}
	
	public double f(double x, double y) {
		return(buffered)?bufferedF(x, y) :  f2(x, y);
		
	}

	public double bufferedF(double x, double y){
		HashMap<Long,Double> firstMap = map.get(Double.doubleToLongBits(x));
		if(firstMap!= null) {
			Double doub = firstMap.get(Double.doubleToLongBits(y));
			if(doub != null) {
				return doub;
			} else {
				doub = f2(x,y);
				firstMap.put(Double.doubleToLongBits(y), doub);
				return doub;
			} 
		} else {
			Double doub = f2(x,y);
			firstMap = new HashMap<Long, Double>();
			firstMap.put(Double.doubleToLongBits(y), doub);
			map.put(Double.doubleToLongBits(x),	firstMap);
			return doub;
		}
	}
	
	public String getFuncName() {
		return funcName;
	}

	@Override
	public double f(double x) {
//		System.err.println("!!!!!!: "+funcName);
		return funcParse.getFunctionOutput(funcName, x);
	}

}
