package com.wildplot.android.parsing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class handles function parsing and stores function information. Any image value of a function that has already 
 * been declared via parsing can be computed. Function names have to be unique.
 * This class utilizes the TermParser class in order to perform term computations.
 * @author C. Otto, R. Meier
 *
 */
public class FunctionParser {

//	public static void main(String[] args) {
//		/* tests an examples */
//		FunctionParser fp = new FunctionParser();
//		fp.parse("x=5");
//		fp.parse("x^2+cos(x)");
//		fp.parse("f(t)=2*t+5");
//		fp.parse("f(2)");
//		/* ----------------- */
//	}
	
    private ReentrantLock reentrantLock = new ReentrantLock();
    
    
	/* Stores how many domain variables a function holds and which names they have. */
	HashMap<String,String[]> functionVariables; 	// <function , variable>
	
	/* Associates the term representing the image value computation formula with its function name. */
	HashMap<String,String> functionTerms; 			// <function , term>
	
	/* The internal TermParser object used for term computation. */
	TermParser terPars;

	/**
	 * Constructor to initialize a new FunctionParser object
	 */
	public FunctionParser(){
		functionVariables = new HashMap<String,String[]>();
		functionTerms = new HashMap<String,String>();
		terPars = new TermParser();
	}
	
	/**
	 * This method declares new functions for further computations according to an input String if said String represents 
	 * a valid declaration. If the input is no valid declaration the String is handled like a term and the method tries to 
	 * compute the value represented by the term, which will then be printed via "System.out". 
	 * If any of these operations was successful "true" is returned, else "false" is returned.
	 * @param input
	 * @return whether input could be applied or not
	 */
	public boolean parse(String input){
		double result = Double.NaN;
		boolean action = false;
		
		if(input.contains(")=")){
			String[] assignment = input.split("=");
			if(assignment==null || assignment.length < 2) return false;
			
			String function="", variable="";
			boolean readVariable=false, corrupt=true;
			for(int i=0; i<assignment[0].length(); i++){
				if(assignment[0].charAt(i) == '(') readVariable=true;
				else if(assignment[0].charAt(i) == ')' && i==assignment[0].length()-1) corrupt=false;
				else if(readVariable) variable += assignment[0].charAt(i);
				else function += assignment[0].charAt(i);
			}
			if(corrupt){
				System.err.println("Invalid function assignment!");
				return false;
			}
			else {
				functionVariables.put(function, variable.split(","));
				functionTerms.put(function, assignment[1]);
				return true;
			}
		}
		else{
			result=terPars.parse(input);
			action=true;
		}
		
		if(result != Double.NaN && action){
			System.out.println("input = "+result);
			return true;
		}
		return false;
	}

	/**
	 * This method computes the image value of a declared function for a given domain value.
	 * @param functionName name of the function which image value should be computed
	 * @param inputVariable domain value
	 * @return image value
	 */
	public double getFunctionOutput(String functionName, double inputVariable){
	    reentrantLock.lock();
	    try{
    		terPars.setVar(functionVariables.get(functionName)[0], inputVariable);
    		return terPars.parse( functionTerms.get(functionName) );
	    }finally {
	        reentrantLock.unlock();
	    }
	}
	
	/**
	 * This method computes the image value of a declared, 'n'-dimensional function for an array of 'n' domain values.
	 * @param functionName name of the function which image value should be computed
	 * @param inputVariables array containing all required domain values
	 * @return image value
	 */
	public double getFunctionOutput(String functionName, double[] inputVariables){
	    reentrantLock.lock();
        try{
    		for(int i=0; i<inputVariables.length; i++) 
    			terPars.setVar(functionVariables.get(functionName)[i], inputVariables[i]);
    		return terPars.parse( functionTerms.get(functionName) );
        }finally {
            reentrantLock.unlock();
        }
	}
	
	/**
	 * This method computes and prints the image value of a 'n'-dimensional function for an array of 'n' domain 
	 * values if the given function has already been declared.
	 * @param functionName name of the function which image value should be computed
	 * @param inputVariables array containing all required domain values
	 * @return whether function has been declared or not
	 */
	public boolean parseGetFX(String functionName, String[] inputVariables){
		Iterator<String> ith = functionTerms.keySet().iterator();
		while(ith.hasNext()){
			String function = ith.next();
			if(function.equals(functionName)){
				System.out.print( function + '(');
				for(int i=0; i<inputVariables.length; i++) {
					terPars.setVar( functionVariables.get(functionName)[i], Double.parseDouble(inputVariables[i]) );
					if(i != inputVariables.length-1)	System.out.print( inputVariables[i] + "," );
					else 	System.out.print( inputVariables[i] );
				}
				System.out.println(")=" + terPars.parse( functionTerms.get(functionName) ));
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if a function of the given name has already been declared.
	 * @param functionName
	 * @return
	 */
	public boolean containsFunction(String functionName){
		return functionTerms.containsKey(functionName);
	}
	
	/**
	 * Calculates the x-coordinate limits for a plot of a given function. The limits are
	 * determined according to the functions monotonic properties towards plus and minus
	 * infinity.
	 * @param functionName
	 * @return the left and the right x-limit in array form
	 */
	public double[] monotonyAnalysis(String functionName){
		double start = checkStart(functionName, 0), increment=0.01;
		double step = start, monothonyStart=start;
		double[] increase = new double[3], output = new double[2];
		double deltaIncrease = 0;
		boolean ascend=true;
		int monothonyCount = 0;
		long endCount=0;
		
		/* right side */
		while(monothonyCount < 50000 && endCount<500000){
			endCount++;
			increase[1] = increase[0];
			increase[0] = (getFunctionOutput(functionName,step)+getFunctionOutput(functionName,step+0.01)) / 0.01;
			if(Math.abs(increase[0]-increase[1])<deltaIncrease){
				increment+=0.001;
			} else{
				increment=0.01;
			}
			deltaIncrease = Math.abs(increase[0]-increase[1]);
			if(increase[0] > increase[1]){
				if(ascend) monothonyCount++;
				else {
					monothonyCount=0;
					monothonyStart=step;
				}
				ascend=true;
			} else{
				if(!ascend) monothonyCount++;
				else {
					monothonyCount=0;
					monothonyStart=step;
				}
				ascend=false;
			}
			step+=increment;
		}
		output[0]=monothonyStart+calculateBorderDistance(increment, step); //(Math.exp((Math.log(increment)/2)))+1;
		
		/* left side */
		step = start;
		monothonyStart=start;
		increase = new double[3];
		deltaIncrease = 0;
		ascend=true;
		monothonyCount = 0;
		endCount=0;
		while(monothonyCount < 50000 && endCount<500000){
			endCount++;
			increase[1] = increase[0];
			increase[0] = (getFunctionOutput(functionName,-step)+getFunctionOutput(functionName,-step-0.01)) / 0.01;
			if(Math.abs(increase[0]-increase[1])<deltaIncrease){
				increment+=0.001;
			} else{
				increment=0.01;
			}
			deltaIncrease = Math.abs(increase[0]-increase[1]);
			if(increase[0] > increase[1]){
				if(ascend) monothonyCount++;
				else {
					monothonyCount=0;
					monothonyStart=-step;
				}
				ascend=true;
			} else{
				if(!ascend) monothonyCount++;
				else {
					monothonyCount=0;
					monothonyStart=-step;
				}
				ascend=false;
			}
			step+=increment;
		}
		output[1]=monothonyStart-calculateBorderDistance(increment, step);//(Math.exp((Math.log(increment)/2)))-1;
		return output;
	}
	
	/* this method calculates the distance of a border from the stopping point of the monothony analysis */
	private double calculateBorderDistance(double increment, double step){
		if(increment>25) return 1.0+increment;
		else return 1+increment+Math.exp((Math.log(step)/2));
	}
	
	/* this method checks if a chosen starting point for a plot is valid and if not determines a new one close to it */
	private double checkStart(String functionName,double start){
		double value = getFunctionOutput(functionName,start);
		if(value == Double.NaN || value == Double.POSITIVE_INFINITY || value == Double.POSITIVE_INFINITY) return checkStart(functionName, start-1);
		else return start;
	}
}
