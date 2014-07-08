package com.wildplot.system;

import com.wildplot.MyMath.Vector_d;

public class Vec_d {
	double[] v;							// array to hold the data of a vector
	public int N() {return v.length;}	// length of vector

	// indexing
	public double get(int i) {return v[i];}
	public void set(int i, double value) {v[i]=value;}
	public double[] getBackVector() {return v;}
	// Ctors
	public Vec_d(int n) { v = new double[n]; }	// vector with n elements
	
	public Vec_d(int n, double a){ 
		this(n);
		cpy(a, v);
	}	// all elements same value
	
	public Vec_d(double[] a) { 
		this(a.length);
		cpy(a, v);
	}// vector from array
	
	public Vec_d(Vector_d a) { 
		this(a.V().length);
		cpy(a.V(), v);
	}// vector from array

	public String toString()
	{
		String s = "[";
		for (int i = 0; i < v.length-1; i++) s += v[i]+", ";
		s+=v[v.length-1]+"]";
		return s;
	}

	public static double[] getVec(Vec_d x) { return x.v; }
	public double[] getVec() { return this.v; }

	// utilities (helper functions)
	private static void cpy(double a, double[] z) { for (int i = 0; i < z.length; i++) z[i] = a; }
	private static void cpy(double[] x, double[] z) {
        System.arraycopy(x, 0, z, 0, z.length);
    }
	private static void inv(double[] x, double[] z) { for (int i = 0; i < z.length; i++) z[i] = -x[i]; }

	private static void add(double a, double[] y, double[] z) { for (int i = 0; i < z.length; i++) z[i] = a + y[i]; }
	private static void add(double[] x, double a, double[] z) { for (int i = 0; i < z.length; i++) z[i] = x[i] + a; }
	private static void add(double[] x, double[] y, double[] z) { for (int i = 0; i < z.length; i++) z[i] = x[i] + y[i]; }

	private static void sub(double a, double[] y, double[] z) { for (int i = 0; i < z.length; i++) z[i] = a - y[i]; }
	private static void sub(double[] x, double a, double[] z) { for (int i = 0; i < z.length; i++) z[i] = x[i] - a; }
	private static void sub(double[] x, double[] y, double[] z) { for (int i = 0; i < z.length; i++) z[i] = x[i] - y[i]; }

	private static void mul(double a, double[] y, double[] z) { for (int i = 0; i < z.length; i++) z[i] = a * y[i]; }
	private static void mul(double[] x, double a, double[] z) { for (int i = 0; i < z.length; i++) z[i] = x[i] * a; }
	private static void mul(double[] x, double[] y, double[] z) { for (int i = 0; i < z.length; i++) z[i] = x[i] * y[i]; }

	private static double mul(double[] x, double[] y) { double s = 0.0; for (int i = 0; i < y.length; i++) s += x[i] * y[i]; return s; }

	private static void div(double a, double[] y, double[] z) { for (int i = 0; i < z.length; i++) z[i] = a / y[i]; }
	private static void div(double[] x, double a, double[] z) { for (int i = 0; i < z.length; i++) z[i] = x[i] / a; }
	private static void div(double[] x, double[] y, double[] z) { for (int i = 0; i < z.length; i++) z[i] = x[i] / y[i]; }

//	// all functions with 'operator' are not possible in java; but see below
//	// unary op +/- : let x,z be Vectors; then these operator allow us to write z=+x and z=-x
//	public static Vec_d operator +(Vec_d x) { Vec_d z = new Vec_d(x.N); cpy(x.v, z.v); return z; }
//	public static Vec_d operator -(Vec_d x) { Vec_d z = new Vec_d(x.N); inv(x.v, z.v); return z; }
//
//	// Arithmetics: Vec op Vec: let x,y,z be Vectors; then these operators allow us to write z=x+y and so on
//	public static Vec_d operator +(Vec_d x, Vec_d y) { Vec_d z = new Vec_d(y.N); add(x.v, y.v, z.v); return z; }
//	public static Vec_d operator -(Vec_d x, Vec_d y) { Vec_d z = new Vec_d(y.N); sub(x.v, y.v, z.v); return z; }
//	public static Vec_d operator *(Vec_d x, Vec_d y) { Vec_d z = new Vec_d(y.N); mul(x.v, y.v, z.v); return z; }
//	public static Vec_d operator /(Vec_d x, Vec_d y) { Vec_d z = new Vec_d(y.N); div(x.v, y.v, z.v); return z; }
//
//	// Arithmetics: double op Vec and Vec op double: 
//	public static Vec_d operator +(double a, Vec_d y) { Vec_d z = new Vec_d(y.N); add(a, y.v, z.v); return z; }
//	public static Vec_d operator +(Vec_d x, double a) { Vec_d z = new Vec_d(x.N); add(x.v, a, z.v); return z; }
//	public static Vec_d operator -(double a, Vec_d y) { Vec_d z = new Vec_d(y.N); sub(a, y.v, z.v); return z; }
//	public static Vec_d operator -(Vec_d x, double a) { Vec_d z = new Vec_d(x.N); sub(x.v, a, z.v); return z; }
//	public static Vec_d operator *(double a, Vec_d y) { Vec_d z = new Vec_d(y.N); mul(a, y.v, z.v); return z; }
//	public static Vec_d operator *(Vec_d x, double a) { Vec_d z = new Vec_d(x.N); mul(x.v, a, z.v); return z; }
//	public static Vec_d operator /(double a, Vec_d y) { Vec_d z = new Vec_d(y.N); div(a, y.v, z.v); return z; }
//	public static Vec_d operator /(Vec_d x, double a) { Vec_d z = new Vec_d(x.N); div(x.v, a, z.v); return z; }
//
//	// inner product; scalar product
//	public static double operator ^(Vec_d x, Vec_d y) { return mul(x.v, y.v); }

	// how we would use vectors in java... no operator-overloading ... what a mess!

	// unary minus
	public Vec_d inv() { Vec_d z = new Vec_d(N()); inv(v, z.v); return z; }

	// Arithmetics: Vec op Vec
	public Vec_d add(Vec_d y) { Vec_d z = new Vec_d(N()); add(v, y.v, z.v); return z; }
	public Vec_d sub(Vec_d y) { Vec_d z = new Vec_d(N()); sub(v, y.v, z.v); return z; }
	public Vec_d mul(Vec_d y) { Vec_d z = new Vec_d(N()); mul(v, y.v, z.v); return z; }
	public Vec_d div(Vec_d y) { Vec_d z = new Vec_d(N()); div(v, y.v, z.v); return z; }

	// Arithemtics: Vec op double
	public Vec_d add(double a) { Vec_d z = new Vec_d(N()); add(v, a, z.v); return z; }
	public Vec_d sub(double a) { Vec_d z = new Vec_d(N()); sub(v, a, z.v); return z; }
	public Vec_d mul(double a) { Vec_d z = new Vec_d(N()); mul(v, a, z.v); return z; }
	public Vec_d div(double a) { Vec_d z = new Vec_d(N()); div(v, a, z.v); return z; }
	// Arithmetics: double op Vec not possible in java

	// inner product; scalar product
	public double sdot(Vec_d y) { return mul(v, y.v); }

	// some more utilities for Vectors

	public double Max(){
			double max = Math.abs(v[0]);
			for (int i = 1; i < N(); i++) max = Math.max(max, Math.abs(v[i]));
			return max;
	}

	public double Min(){
			double min = Math.abs(v[0]);
			for (int i = 1; i < N(); i++) min = Math.min(min, Math.abs(v[i]));
			return min;
	}
}
