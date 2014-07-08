package com.wildplot.system;

public class Classes {

	// in java: class HarmonicOscillator implements ODE_FUN
	// in addition this class needs to be in a separate file
	

	// LotkaVolterra:
	// ===============
	//
	// x1(t):=x(t) : prey		(Beutetier)
	// x2(t):=y(t) : predator	(Raubtier)
	//
	// time evolution:
	// ===============
	//		dx/dt = x*(a-b*y)		a>0 b>0
	//		dy/dt = -y*(c-d*x)		c>0 d>0
	//
	//		a: growth rate of preys
	//		b: loss rate of preys due to predators
	//		c: loss rate of predators
	//		d: growth rate of predators due to preys
	//
	// good parameters to play with:
	// =============================
	// a=0.5  b=0.25  c=0.5  d=0.1  x[0]=1  x[1]=0.4

	// for further information see:
	// http://de.wikipedia.org/wiki/Lotka-Volterra-Gleichungen
	// http://en.wikipedia.org/wiki/Lotka-Volterra_equation
	// and references therein

	
//
//	// a set of 3 chemical reactions
//	// our solvers won't work for this system!
//	public class TripleReaction implements ODE_FUN
//	{
//		public int neq { get { return 3; } }
//
//		public Vec_d this[Vec_d x, double t]
//		                  {
//			get
//			{
//				Vec_d f=new Vec_d(3);
//				f[0]=-0.04*x[0] + 1.0E4*x[1]*x[2];
//				f[1]=0.04*x[0] - 1.0E4*x[1]*x[2] - 3.0E7*x[1]*x[1];
//				f[2]=3.0E7*x[1]*x[1];
//				return f;
//			}
//		                  }
//	}
//
//
//
//	//	Oregonator: a periodic chemical reaction
//	//	simplified description of this reaction:
//
//	//	BR_O3_- + BR_- -> H_Br_O_2
//	//	H_Br_O_2 + Br_- -> P		: unspecified (unimportant) prduct of the reaction
//	//	Br_O_3_- + H_Br_O_2 -> 2 H_Br_O_2 + Ce(IV)
//	//	2 H_Br_O_2 -> P
//	//	Ce(IV) -> Br_-
//
//	//	http://de.wikipedia.org/wiki/Belousov-Zhabotinsky-Reaktion
//	//	http://jkrieger.de/bzr/inhalt.html
//
//	// our solvers won't work for this system!
//
//	public class Oregonator implements ODE_FUN
//	{
//		double[] k=new Vec_d(5); // Reaktionsgeschwindigkeiten
//
//		public Oregonator()
//		{
//			// k[0]=1.34; k[1]=1.6E9; k[2]=8.0E3; k[3]=4.0E7; k[4]=1.0;
//			k[0]=1.28; k[1]=8.0; k[2]=8.0E5; k[3]=2.0E3; k[4]=1.0;
//		}
//
//		// implementation of interface
//		public int neq { get { return 5; } }
//
//		public Vec_d this[Vec_d x, double t]
//		                  {
//			get
//			{
//				Vec_d f= new Vec_d(5);
//				f[0]=-k[0]*x[0]*x[1]-k[2]*x[0]*x[2];
//				f[1]=-k[0]*x[0]*x[1]-k[1]*x[1]*x[2]+k[4]*x[4];
//				f[2]=k[0]*x[0]*x[1]-k[1]*x[1]*x[2]+k[2]*x[0]*x[2]-2.0*k[3]*x[2]*x[2];
//				f[3]=k[1]*x[1]*x[2]+k[3]*x[2]*x[2];
//				f[4]=k[2]*x[0]*x[2]-k[4]*x[4];
//				return f;
//			}
//		                  }
//		public override string ToString()
//		{
//			return "Oregonator: coefficients of reaction k="+k;
//		}
//	}
}
