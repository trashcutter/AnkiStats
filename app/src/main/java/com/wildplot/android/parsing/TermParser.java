 package com.wildplot.android.parsing;

import java.util.HashMap;

 /**
 * This class handles computation of term values and stores variable values. At any given moment any variable
 * must have one specific value. Thus its parsing function obtains either terms or variable assignments.
 * @author C. Otto, R. Meier
 *
 */
public class TermParser {

//	public static void main(String[] args) {
//		
//		/* tests an examples */
//		
//		TermParser bla = new TermParser();
//		System.out.println(bla.parse("x=-90+10*(13+2.6*10)"));
//		System.out.println(bla.var_map.entrySet());
//		System.out.println(bla.parse("2*x-2^3")+"\n\n\n\n");
//		
//		for(double i=0; i<7.0; i++){
//			bla.parse("x="+(i*0.5));
//			System.out.println(bla.var_map.entrySet()+"\tf(x)="+bla.parse("2*x^2-5*x"));
//		}
//		System.out.println();
//		for(double i=0; i<7.0; i++){
//			bla.setVar("x", i*0.1);
//			System.out.println(bla.var_map.entrySet()+"\tf(x)="+bla.parse("cos(x*2*PI)"));
//		}
//		System.out.println();
//		for(double i=0; i<7.0; i++){
//			bla.setVar("x", i*0.1);
//			System.out.println(bla.var_map.entrySet()+"\tf(x)="+bla.parse("cos(x*2*PI)"));
//		}
//		
//		/* ----------------- */
//	}
	

	/* Determines the type of the current token during the parsing process. */
	private token_value curr_tok;
	
	/* Determines the numeric value of the current token during the parsing process. */
	private double number_value;
	
	/* Determines the string representation of the current token during the parsing process. */
	private String string_value;
	
	/* Determines the current position in the input sequence during the parsing process*/
	private int curr_pos;
	
	/* The current input sequence that is parsed. */
	private String input;
	
	/* Stores the values of all declared variables. */
	private HashMap<String,Double> var_map;
	
	/* Contains all possible token types. */
	private enum token_value {
		NAME, NUMBER, END, PLUS, MINUS, MUL, DIV, EXP, LOG, PRINT, ASSIGN,  LP, RP, COS, SIN, SQRT
	};
	/* Maps the type of a token to its String representation via its array index. */
	private final char[] charToEnum = {
		' ' , ' '   , ' ',  '+', '-'  , '*', '/', '^', '_', ' '  , '='   , '(',')', ' ', ' '
	};
	
	/**
	 * Constructor to initialize a new TermParser object
	 */
	public TermParser(){
		var_map = new HashMap <String, Double>();
		var_map.put("pi", Math.PI);
		var_map.put("e", Math.E);
		clear();
	}
	
	/**
	 * Returns the current value of a stored variable
	 * @param variableName
	 * @return
	 */
	public double getVarValue(String variableName){
		return var_map.get(variableName);
	}
	
	/**
	 * This method parses terms or variable declarations represented by an input String. Variable declaration
	 * information is stored and the input value is returned while term values are computed first and then returned.
	 * @param inputString represents either a term or a variable declaration
	 * @return term value
	 */
	public double parse(String inputString){
		clear();
		input = inputString;
		return expr(true);
	}
	
	/**
	 * Modifies or declares a variable of given name, directly.
	 * @param name of the variable
	 * @param value of the variable
	 */
	public void setVar(String name, double value){
		var_map.put(name, value);
	}
	
	/*
	 * Reset old term information and temporary information in order to parse another term. 
	 */
	private void clear(){
		curr_pos=-1;
		number_value=0;
		string_value="";
		curr_tok = token_value.PRINT;
		input="";
	}
	
	/*
	 * Each call will store information of the next token of the current input expression into the temporary variables.
	 * The current token type is stored in curr_tok curr_tok, while names and values are stored into number_value and
	 * string_value. A token is an information subunit which means it can be a variable, a number or an operator.
	 */
	private void getToken(){
		boolean primaryFirst=false;			// determines whether the current token is a primary (true) or an operator (false)
		boolean primaryIsReal=false;		// determines whether the current primary is a numerical number (true) or a name (false)
		boolean checkedPrimaryType=false;	// determine whether the primary type has already been checked (true) or not (false)
		
		while(true){
			curr_pos++;
			if(curr_pos>input.length()-1){
				curr_tok=token_value.END;
				break;
			}
			if(
				input.charAt(curr_pos)=='+' ||
				input.charAt(curr_pos)=='-' ||
				input.charAt(curr_pos)=='*'	||	
				input.charAt(curr_pos)=='/'	||
				input.charAt(curr_pos)=='('	||
				input.charAt(curr_pos)==')'	||
				input.charAt(curr_pos)=='^'	||
				input.charAt(curr_pos)=='_'	||
				input.charAt(curr_pos)=='='
			){
				if(!primaryFirst){
					for(int i=0; i<charToEnum.length; i++){
						if(input.charAt(curr_pos) == charToEnum[i]){
							curr_tok=token_value.values()[i];
						}
					}
				}
				else{
					curr_pos--;
				}
				break;
			}
			else{
				if(!checkedPrimaryType){
					checkedPrimaryType=true;
					int num = (int)input.charAt(curr_pos);
					if(num > 47 && num < 58){ // number case
						primaryIsReal=true;
					}
					string_value="";
				}
				primaryFirst=true;
				string_value+=input.charAt(curr_pos);
			}
		}
		
		if(primaryFirst){
			if(primaryIsReal){
				number_value = Double.parseDouble(string_value);
				curr_tok=token_value.NUMBER;
			}
			else{
				curr_tok=token_value.NAME;
			}
		}
	}
	
	/*
	 * Adds step by step from left to right all addends of the sum at the current input sequence position and computes the sum's value.
	 * Resembles the highest computation level and the lowest priority.
	 * @param get determines whether the end is reached or not
	 * @return the value of the current sum
	 */
	private double expr(boolean get){
		// compute the left argument which has to be a primary value or an expression with a higher priority than sums
		double left = term(get);
		// add all addends until the end of the sum is reached
		for(;;){ 
			switch(curr_tok){
				case PLUS:{
					left += term(true);
					break;
				}
				case MINUS:{
					left -= term(true);
					break;
				}
				default:{
					return left;
				}
			}
		}
	}
	
	/*
	 * Multiplies step by step from left to right all factors of the product at the current input sequence position and computes the product's value.
	 * @param get determines whether the end is reached or not
	 * @return the value of the current product
	 */
	private double term(boolean get){
		// compute the left argument which has to be a primary value or an expression with a higher priority than products
		double left = pot(get);
		for(;;){
			switch(curr_tok){
				case MUL:{
					left*=pot(true);
					break;
				}
				case DIV:{
					double d;
					if((d=pot(true)) != 0){
						left/=d;
						break;
					}
					//System.err.println("Error::TermParser::term(): Invalid operation. Division by 0.");
					return Double.NaN;
				}
				default:{
					return left;
				}
			}
		}
	}
	
	/*
	 * Processes the left argument with the right argument of an exponentiation or logarithm operator and returns the result. 
	 * @param get determines whether the end is reached or not
	 * @return the value of the current computation.
	 */
	private double pot(boolean get){ 
		// FIXME: The method is still broken for "x_y_z" or "x^y_z" ... etc. expressions ! Hence the unusual method description.
		double left = prim(get); 
		for(;;){
			switch(curr_tok){
				case EXP:{
					left=Math.pow(left, prim(true));
					break;
				}
				case LOG:{
					double d=prim(true);
					if(left >= 0){
						left=Math.log(left)/Math.log(d);
						break;
					}
//					System.err.println("Error::TermParser::pot(): Invalid operation. Logarithm of a negative number.");
					return Double.NaN;
				}
				default:{
					return left;
				}
			}
		}
	}
	
	/*
	 * Returns the value of either numeric numbers, variables or bracket dependent expressions. 
	 * Resembles the lowest computation level and the highest priority.
	 * @param get determines whether the end is reached or not
	 * @return computed value
	 */
	private double prim(boolean get){
		if(get) getToken();	// get the next token
		switch(curr_tok){
			case NUMBER:{
				// if the current token is a number obtain the next operator and return the number value
				double val = number_value;
				getToken(); // get the next operator so that higher level methods can perform computation
				return val;
			}
			case NAME:{
				// if the current token is a name... 
				//		... either return the output of the mathematical function determined by the name ...
				if     (string_value.equals("cos"))		return Math.cos( prim(true) );
				else if(string_value.equals("sin"))		return Math.sin( prim(true) );
				else if(string_value.equals("tan"))		return Math.tan( prim(true) );
				else if(string_value.equals("acos"))	return Math.acos( prim(true) );
				else if(string_value.equals("asin"))	return Math.asin( prim(true) );
				else if(string_value.equals("atan"))	return Math.atan( prim(true) );
				else if(string_value.equals("cosh"))	return Math.cosh( prim(true) );
				else if(string_value.equals("sinh"))	return Math.sinh( prim(true) );
				else if( string_value.equals("log") ||
					string_value.equals("lg") )			return Math.log10( prim(true) );
				else if(string_value.equals("ln"))		return Math.log( prim(true) );
				else if(string_value.equals("sqrt"))	return Math.sqrt( prim(true) );
				else{
				//		... or return the value of the variable (which is determined by the var_map) or the made assignment
					double val=Double.NaN; 
					String key = string_value;

					if(var_map.containsKey(string_value)){
						val = var_map.get(string_value);
						getToken(); // get the next operator so that higher level methods can perform computation
						if(curr_tok == token_value.ASSIGN){
							val = expr(true);
							var_map.put(key, val);
						}
					}
					else{	// if the variable is not contained an assignment HAS to be performed !
						getToken();
						if(curr_tok == token_value.ASSIGN){
							val = expr(true);
							var_map.put(key, val);
						}
						else{
							System.err.println("Error::TermParser::prim(): Undeclared variable. Expected '=' character.");
							return Double.NaN;
						}
					}
					return val;
				}
			}
			case MINUS:{
				return - prim(true);
			}
			case LP:{
				// if the current token is a left bracket compute the value represented by the term inside the brackets and return it
				double ex = expr(true);
				if(curr_tok != token_value.RP){
					System.err.println("Error::TermParser::prim(): Missing bracket. Expected ')' character.");
					return Double.NaN;
				}
				getToken();
				return ex;
			}
			default:{
				// if no token matches are found even at the lowest computation level, there are invalid components
				System.err.println("Error::TermParser::prim(): Invalid component. Expected primary value.");
			}
		}
		return Double.NaN;
	}
}
