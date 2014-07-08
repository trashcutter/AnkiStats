package com.wildplot.system;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Vector;

import android.widget.ProgressBar;

import com.wildplot.MyMath.Matrix_d;



public class ODE {
	//		public ODE_FUN f { get; private set; }	// rhs of this ode
	//		public double ta { get; private set; }	// start time	
	//		public double tb { get; private set; }	// end time
	//		public Vec_d xa { get; private set; }	// initialcondition x(ta)
	//
	//		public int n { get; private set; }		// number of timesteps
	//		public double dt { get; private set; }	// timestep itself
	//
	//		public double[] t { get; private set; }	// sample points in time
	//		public Vec_d[] x { get; private set; }	// sample points of solution
	//		public Vec_d[] e { get; private set; }	// error estimate (if available)

	// in java:
	private ODE_FUN f;
	public ODE_FUN get_ODE_Fun() {return f;}
	private double ta;
	public double get_ta() {return ta;}
	private double tb;
	public double get_tb() {return tb;}
	private Vec_d xa;
	public Vec_d get_xa(){return xa;}
	private int n;
	public int get_n(){return n;}
	private double dt;
	public double get_dt() {return dt;}
	private double[] t;
	public double[] get_t(){return t;}
	private Vec_d[] x;
	public Vec_d[] get_x(){return x;}
	private Vec_d[] e;
	public Vec_d[] get_e(){return e;}
	private Vec_d[] Xdot;
	public Vec_d[] getXdot() {return Xdot;}

	// ... and similar for all the other variables

	public enum SOLVER { RKE1, RKE2, RKE3, RKE4, RKE5, RKI1, RKI4, RKI4Old, BS };
	// in java: adopt it...

	private Integrator rk;

	public ODE(ODE_FUN f, double ta, double tb, Vec_d xa)
	{
		this.f = f;
		this.ta = ta; this.tb = tb;
		this.xa = xa;
	}

	// interpolation for solution at any point between ta and tb
	// interpolation is done using so called "cubic Hermite-Interpolation"
	// Hermite-Interpolation uses function values x[k] and x[k+1]
	// as well as derivative information dx/dt[k] and dx/dt[k+1]
	// these 4 informations define a unique polynomial of order 3
	// for algorithms of higher order, one would like a higher order
	// interpolation scheme; this is possible but not implemented here...
	// for instance: to achieve 7th order accuracy, which would be
	// appropriate for high order algorithms (BS, RKE5, RKI4),
	// we would use the information at t[k-1], t[k], t[k+1] and t[k+2]
	// (8 informations) to define a unique polynomial of degree 7

	// this functions performs the cubic Hermite-Interpolation
	// k defines the intervall for which we do the interpolation
	// tt must be between t[k] and t[k+1]
	// h is the width of this interval
	// interpolation is done in terms of a cubic polynomial in s,
	// where s is a dimensionless time-variable defined such, that
	// s=0 belongs to t[k] and s=1 belongs to t[k+1]
	public Vec_d X(int k, double tt)
	{
		double h=t[k+1]-t[k];
		double s=(tt-t[k])/h,s1=s-1.0, s2=1.0-2.0*s;
		Vec_d v1, v2, v3, v4, v5, v6, v7, v8;
		v1=x[k+1].sub(x[k]);
		v2=(v1).mul(s2);
		Vec_d xdotK=Xdot[k];
		v3=xdotK.mul(s1);
		v4=Xdot[k+1].mul(s);
		v5=v3.add(v4);
		v6=v2.add(v5);
		v7=v6.mul(h);
		v8=v7.mul(s1);
		
		return    v8.sub( x[k].mul(s1).add(x[k+1].mul(s)));
	}

	// locate the index k, for the interval (t[k],t[k+1]) to which tt belongs
	// usually we set up a table with increasing values for tt
	// in that case, last_k is used as a pointer to the last interval, i.e.
	// to minimize the effort to search for the correct k
	// k=-1 is returned, if tt is outside t[0] and t[n]
	private static int last_k=0;
	public int locate(double tt)
	{
		if (tt<t[last_k])
		{
			last_k=0;
			if (tt<t[0]) return -1;
		}
		int k=-1;
		for(int i=last_k+1; i<t.length; i++)
			if (tt<=t[i]) { k=i-1; break; }
		return k;
	}

	// Hermite-Interpolation for arbitrary value of tt
	public Vec_d X(double tt)
	{
		int k=locate(tt);
		if (k==-1)
		{
			System.out.println("ODE::X: tt outside tabulated values!");
			System.out.println("tt="+tt);
			System.exit(-1);
		}
		return X(k,tt);
	}

	// create a table of interpolated values of the solution 
	// on a uniform grid with m+1 equidistant points
	public Vec_d[] dense_output(double[] tt)
	{
		int m = tt.length;
		Vec_d[] y = new Vec_d[m];
		tt[0]=t[0];
		y[0]=x[0];
		double h=(this.t[n]-this.t[0])/(m-1);

		for (int i=1; i<m; i++) y[i]=X(tt[i]=tt[i-1]+h);
		return y;
	}
	
	// solve this ode with given solver and given number of timesteps
	public void solve(SOLVER solver, int n)
	{
		this.rk = new Integrator(this, solver);
		this.n = n;
		this.dt = (tb - ta) / n;
		this.t = new double[n + 1];
		this.x = new Vec_d[n + 1];
		this.e=new Vec_d[n+1];

		t[0] = ta;
		x[0] = xa;
		for (int i = 0; i < n; i++)
		{
			e[i]=new Vec_d(f.get_neq());
			x[i+1]=x[i].add(rk.get(x[i],t[i], dt));
			t[i + 1] = t[i] + dt;
		}
		Xdot[n]=f.get(x[n], t[n]);
	}
	
	public void solve(SOLVER solver, int n, ProgressBar progressBar)
	{
		progressBar.setMax(100);
		int cnt = n/10;
		
		this.rk = new Integrator(this, solver);
		this.n = n;
		this.dt = (tb - ta) / n;
		this.t = new double[n + 1];
		this.x = new Vec_d[n + 1];
		this.e=new Vec_d[n+1];
		this.Xdot=new Vec_d[n+1];						// derivative dx/dt
		
		t[0] = ta;
		x[0] = xa;
//		System.err.println("!!!Progress Start!!!: ");
		for (int i = 0; i < n; i++)
		{
			if(i%cnt==0){
				progressBar.setProgress((int)(100.0*((float)i/(float)n)));
//				progress = (int)(100.0*((float)i/(float)n));
//				System.err.println("!!!ProgressUpdate!!!: " + (100.0*((float)i/(float)n)));
			}
			e[i]=new Vec_d(f.get_neq());
			x[i+1]=x[i].add(rk.get(x[i],t[i], dt));
			t[i + 1] = t[i] + dt;
			Xdot[i]=f.get(x[i], t[i]);
		}
		Xdot[n]=f.get(x[n], t[n]);
		progressBar.setProgress(100);
	}
	public void solve_implicit(int n)
	{
		//this.rk = new RungeKutta(this, solver);
		this.n = n;
		this.dt = (tb - ta) / n;
		this.t = new double[n + 1];
		this.x = new Vec_d[n + 1];
		this.e=new Vec_d[n+1];
		
		
		t[0] = ta;
		x[0] = xa;
		

		for (int i = 0; i < n; i++)
		{
//			e[i]=new Vec_d(f.get_neq());
//			x[i+1]=x[i].add(rk.get(x[i],t[i], dt));
			Matrix_d A = f.dfdx(x[i], t[i]).mul(-this.dt);
			for(int k = 0; k< f.get_neq();k++){
				A.set(k, k, A.get(k, k)+1);
			}

			x[i+1] = x[i].add(Matrix_d.mxv(A.inv().mul(dt),f.get(x[i], t[i])));
			t[i+1] = t[i]+dt;
		}
	}
	// solve ODE with adpative timestep control
	// on input, tol is the required tolerance and n is an estimate (may be
	// wrong or false) of the total number of steps required

	public void solve(double tol, int n)
	{
		// class members
		this.n = n;
		this.dt = (tb - ta) / n;						// need some timestep at start
		this.rk = new Integrator(this, SOLVER.RKE5);		// timestep control only for RK5
		this.x=new Vec_d[n+1];							// time series
		this.t=new double[n+1];							// timesteps
		this.e=new Vec_d[n+1];							// error estimates
		

		// local variables
		boolean rejected = false;							// has the last step been rejected ?
		double max = 10.0;								// maximum increase of timestep
		double min = 0.2;								// minimum decrease of timestep
		double save = 0.99;								// a safety factor; should be close to 1


		t[0]=ta;
		x[0] = xa;										// initial conditions
		e[0] = new Vec_d(f.get_neq(), 0);
		//this.Xdot[0] = 
		int cnt=0;										// counter for the number of steps
		do												// loop over timesteps
		{
			boolean converged = false;						// we are not converged yet
			while (!converged)							// convergency-loop
			{
				Vec_d dx = rk.get(x[cnt], t[cnt], dt);		// 
				double err = rk.get_Error().Max();
				double s;								// scale factor for timestep
				
				//if(cnt%1000==0){System.out.println("  in slope num "+cnt+"\t"+err+"\t"+tol+rk.get_Error());}
//				if(Double.isNaN(err)) System.exit(-1);
				
				if (err < tol)							// step will be accepted
				{
					if (err == 0.0) s = max;			// pretty unlikely but may happen
					else
					{
						s = save * Math.pow(tol / err, 0.2);// estimate of new timestep
						if (s > max) s = max;			// algorithm should not be to greedy
					}
					t[cnt + 1] = t[cnt] + dt;			// fullfill this timestep
					x[cnt + 1] = x[cnt].add(dx);			// ...
					e[cnt + 1] = rk.get_Error();				// ...
					if (!rejected) dt *= s;				// increase (next) timestep
					rejected = false;					// this step has been accepted
					converged = true;					// this one was good and is done
					cnt++;								// increase counter
				}
				else									// step will be rejected
				{
					s = save * Math.pow(tol / err, 0.2);// estimate of new timestep
					if (s < min) s = min;				// algorithm should not be to greedy
					dt *= s;							// decrease this timestep
					rejected = true;
				}
			}											// end of convergency-loop
			if (cnt >= this.n) this.n = copy();			// next timestep fill fail
		}												// since we need mor memory
		while (t[cnt-1] <= tb);							// end of loop over timesteps

		// everything went well; finally we adjust the sizes of the arrays
		double[] tt=new double[cnt];
		System_Array_Copy(t,tt,cnt); t=tt;
		for(double d:t)System.out.println(d+"\t");

		Vec_d[] xx=new Vec_d[cnt];
		for (int i = 0; i < cnt; i++) xx[i] = new Vec_d(Vec_d.getVec(x[i]));
		x=xx;
		Vec_d[] ee=new Vec_d[cnt];
		for (int i = 0; i < cnt; i++) ee[i] = new Vec_d(Vec_d.getVec(e[i]));
		e=ee;
		this.n = cnt;
	}

	private int copy()
	{
		int m=t.length;					// actual length of time series
		// how much steps will we probably need? we assume the last timestep
		// to be repeated until we reach tb
		int n=(int)((tb-t[m-1])/dt)+m;	// estimated new length of time series

		double[] tt=new double[n+1]; 
		System_Array_Copy(t,tt,m); 
		t=tt;

		Vec_d[] xx=new Vec_d[n+1];
		for(int i=0; i<m; i++) xx[i]=new Vec_d(Vec_d.getVec(x[i]));
		x=xx;
		Vec_d[] ee=new Vec_d[n+1];
		for (int i = 0; i < m; i++) ee[i] = new Vec_d(Vec_d.getVec(e[i]));
		e=ee;
		return n;
	}

	// print the solution to given TextWriter
	public void print(BufferedWriter tw) throws IOException
	{
		// write information about ode
		tw.write(f + " ta="+ta+" tb="+tb+" initialconditions: " + xa + " timesteps: "+n);
		tw.newLine();
		
		tw.write(t[0]+"\t"+0+"\t");
		for (int k = 0; k < f.get_neq(); k++){
			tw.write(x[0].get(k)+"\t");
			if(e[0]!=null) tw.write(e[0].get(k)+"\t");
			else tw.write("_-_"+"\t");
		}
		tw.write("\r\n");
		for (int i = 1; i < t.length; i++)
		{
			tw.write(t[i]+"\t"+(t[i]-t[i-1])+"\t");
			for (int k = 0; k < f.get_neq(); k++) {
				tw.write(x[i].get(k)+"\t");
				if(e[i]!=null) tw.write(e[i].get(k)+"\t");
			}
			tw.write("\r\n");
		}
	}
	
	public void printPhasePlot(BufferedWriter tw, int colNr1, int colNr2) throws IOException
	{
		if(colNr2%2==0)
			tw.write(x[0].get((colNr1-2)/2)+"\t");
		else
			tw.write(e[0].get((colNr1-3)/2)+"\t");
		
		if(colNr2%2==0)
			tw.write(x[0].get((colNr2-2)/2)+"\t");
		else
			tw.write(e[0].get((colNr2-3)/2)+"\t");
		tw.write("\r\n");
		
		for (int i = 1; i < t.length; i++)
		{
			if(colNr2%2==0)
				tw.write(x[i].get((colNr1-2)/2)+"\t");
			else
				tw.write(e[i].get((colNr1-3)/2)+"\t");
			
			if(colNr2%2==0)
				tw.write(x[i].get((colNr2-2)/2)+"\t");
			else
				tw.write(e[i].get((colNr2-3)/2)+"\t");
			tw.write("\r\n");
		}
	}
	
	public void printCol(BufferedWriter tw, int colNr) throws IOException
	{
//		tw.write(f + " ta="+ta+" tb="+tb+" initialconditions: " + xa + " timesteps: "+n);
		if(colNr<1) 
			return;
		else if(colNr==1){
			tw.write(t[0]+"\t"+0);
			tw.write("\r\n");
			for (int i = 1; i < t.length; i++)
			{
				tw.write(t[i]+"\t"+(t[i]-t[i-1])+"\t");
				tw.write("\r\n");
			}
		}
		else{
			tw.write(t[0]+"\t");
			if(colNr%2==0)
				tw.write(x[0].get((colNr-2)/2)+"\t");
			else
				tw.write(e[0].get((colNr-3)/2)+"\t");
			tw.write("\r\n");
			for (int i = 1; i < t.length; i++)
			{
				tw.write(t[i]+"\t");
				if(colNr%2==0)
					tw.write(x[i].get((colNr-2)/2)+"\t");
				else
					tw.write(e[i].get((colNr-3)/2)+"\t");
				tw.write("\r\n");
			}
		}
	}
	public void printCol(Vector<Double> xVals, Vector<Double> yVals, int colNr)
	{
//		tw.write(f + " ta="+ta+" tb="+tb+" initialconditions: " + xa + " timesteps: "+n);
		if(colNr<1) 
			return;
		else if(colNr==1){
//			tw.write(t[0]+"\t"+0);
			xVals.add(t[0]); yVals.add(0.0);
//			tw.write("\r\n");
			for (int i = 1; i < t.length; i++)
			{
				xVals.add(t[i]); yVals.add((t[i]-t[i-1]));
//				tw.write(t[i]+"\t"+(t[i]-t[i-1])+"\t");
//				tw.write("\r\n");
			}
		}
		else{
			xVals.add(t[0]);
//			tw.write(t[0]+"\t");
			if(colNr%2==0)
				yVals.add(x[0].get((colNr-2)/2));
//				tw.write(x[0].get((colNr-2)/2)+"\t");
			else
				yVals.add(e[0].get((colNr-3)/2));
//				tw.write(e[0].get((colNr-3)/2)+"\t");
//			tw.write("\r\n");
			for (int i = 1; i < t.length; i++)
			{
				xVals.add(t[i]);
//				tw.write(t[i]+"\t");
				if(colNr%2==0)
					yVals.add(x[i].get((colNr-2)/2));
//					tw.write(x[i].get((colNr-2)/2)+"\t");
				else
					yVals.add(e[i].get((colNr-3)/2));
//					tw.write(e[i].get((colNr-3)/2)+"\t");
//				tw.write("\r\n");
			}
		}
	}

	// internal classes that represent a particular Runge-Kutta-Solver
	// this is an internal class (internal to class ODE) since ists use 
	// makes only sence in conjunction with class ODE
	// class RungeKutta itself contains internal classes  for each particular Runge-Kutta-Solver
	// these are named acording to their order: RK1, RK2,...RK5
	// this is a major redesign of our program
	// the usefullness of this redesign lies in the fact, that in the old version,
	// we had a switch statement in the loop over the time steps, i.e. for each time
	// we had to decide, which solver to use
	// this new version checks the kind of solver at the beginning of the call to function 'solve',
	// sets the solver and than uses this solver for all time steps
	// for future development it was necessary to include the time step into the calls to the solver,
	// since in the next development-step we use varying time steps

	private interface Algorithm 
	{
		public Vec_d get(Vec_d x, double t, double dt);
		Vec_d Error();
		Vec_d Xdot();
		int Order();
	}

	
	private class Integrator
	{
		
		Algorithm rk;
		// in java this again becomes a function call:
		// Vec_d get(Vec_d x, double t, double dt);

		// ctor
		public Integrator(ODE ode, SOLVER solver)
		{
			// here we set the solver once and for all
			switch (solver)
			{
			case RKE1: rk = new RK1E(ode); break;
			case RKE2: rk = new RK2E(ode); break;
			case RKE3: rk = new RK3E(ode); break;
			case RKE4: rk = new RK4E(ode); break;
			case RKE5: rk = new RK5E(ode); break;
			case RKI1: rk = new RK1I(ode); break;
			case RKI4: rk = new RK4I(ode); break;
			case RKI4Old: rk = new RK4IOLD(ode); break;
			case BS: rk = new BS(ode); break;
			}
		}

		// this is the function, we call from class ODE
		public Vec_d get(Vec_d x, double t, double dt) { return rk.get(x, t, dt); }
		// in java, a get-function again!

		public Vec_d get_Error(){ return rk.Error(); }
		
		private class RK1E implements Algorithm
		{
			ODE_FUN f;
			Vec_d dx;
			Vec_d Xdot;
			public RK1E(ODE ode) { this.f = ode.f; }
			public Vec_d get(Vec_d x, double t, double dt) { 
				Xdot = f.get(x, t);
				dx = f.get(x, t).mul(dt); 
				return dx;
			}
			@Override
			public Vec_d Error() {  return null; } // no error available
			@Override
			public Vec_d Xdot() {
				// TODO Auto-generated method stub
				return Xdot;
			}
			@Override
			public int Order() {
				
				// TODO Auto-generated method stub
				return 1;
			}
		}

		private class RK2E implements Algorithm
		{
			ODE_FUN f;
			Vec_d dx, k1;
			Vec_d Xdot;
			public RK2E(ODE ode) { this.f = ode.f; }
			public Vec_d get(Vec_d x, double t, double dt){
				Xdot = f.get(x, t);
					k1 = f.get(x, t).mul(dt);
					dx = f.get(x.add(k1.mul(0.5)), t + 0.5 * dt).mul(dt);
					return dx;
			}

			public Vec_d Error () { return null; } // no error available
			@Override
			public Vec_d Xdot() {
				// TODO Auto-generated method stub
				return Xdot;
			}
			@Override
			public int Order() {
				// TODO Auto-generated method stub
				return 2;
			}
		}

		private class RK3E implements Algorithm
		{
			ODE_FUN f;
			Vec_d Xdot;
			Vec_d dx, k1, k2, k3;
			public RK3E(ODE ode) {this.f = ode.f; }
			public Vec_d get(Vec_d x, double t, double dt)
			{
				Xdot = f.get(x, t);
					k1 = f.get(x, t).mul(dt);
					k2 = f.get(x.add(k1.mul(0.5)), t + 0.5 * dt).mul(dt);
					k3 = f.get(x.add(k1.mul(-1.0)).add(k2.mul(2.0)), t + dt).mul(dt);
					dx = (k1.add(k2.mul(4.0)).add(k3)).mul(1.0/6.0);
					return dx;
			}

			public Vec_d Error () { return null; } // no error available
			@Override
			public Vec_d Xdot() {
				// TODO Auto-generated method stub
				return Xdot;
			}
			@Override
			public int Order() {
				// TODO Auto-generated method stub
				return 3;
			}
		}

		private class RK4E implements Algorithm
		{
			ODE_FUN f;
			Vec_d Xdot;
			Vec_d dx, k1, k2, k3, k4;
			public RK4E(ODE ode) {this.f = ode.f; }
			public Vec_d get(Vec_d x, double t, double dt)
			{
				Xdot = f.get(x, t);
					k1 = f.get(x, t).mul(dt);
					k2 = f.get(x.add(k1.mul(0.5)), t + 0.5 * dt).mul(dt);
					k3 = f.get(x.add(k2.mul(0.5)), t + 0.5 * dt).mul(dt);
					k4 = f.get(x.add(k3), t + dt).mul(dt);
					dx = (k1.add((k2.add(k3)).mul(2.0).add(k4))).mul(1.0/6.0);
					return dx;
			}

			public Vec_d Error () { return null; } // no error available
			@Override
			public Vec_d Xdot() {
				// TODO Auto-generated method stub
				return Xdot;
			}
			@Override
			public int Order() {
				// TODO Auto-generated method stub
				return 4;
			}
		}

		// RK5: Dormand-Prince coefficients for Runge-Kutta method of order 5
		//		with embedded Runge-Kutta solver of order 4 for timestep control
		//		this is the most recommendes solver
		//		see Numerical Recipes for more details

		private class RK5E implements Algorithm
		{
			ODE_FUN f;
			Vec_d Xdot;
			Vec_d dx5, dx4;

			// here we denote all the necessary coefficients in a so called Butcher-tableau
			// the parameters of any Runge-Kutta solver can be described in such a tableau,
			// but its usefullness is for solvers of high order such as this solver

			Vec_d c = new Vec_d(new double[] { 0.0, 0.2, 0.3, 0.8, 8.0 / 9.0, 1.0, 1.0 });
			Vec_d b = new Vec_d(new double[] {35.0/384.0, 0.0, 500.0/1113.0, 125.0/192.0,
					-2187.0/6784.0, 11.0/84.0, 0.0});
			Vec_d d = new Vec_d(new double[]{5179.0/57600.0, 0.0, 7571.0/16695.0, 393.0/640.0,
					-92097.0/339200.0, 187.0/2100.0, 1.0/40.0});

			double[][] a = new double[7][];
			Vec_d[] k = new Vec_d[7];
			Vec_d xtmp = new Vec_d(7);

			public RK5E(ODE ode)
			{
				this.f = ode.f;
				a[0] = new double[] { };
				a[1] = new double[] { 0.2 };
				a[2] = new double[] { 3.0 / 40.0, 9.0 / 40.0 };
				a[3] = new double[] { 44.0 / 45.0, -56.0 / 15.0, 32.0 / 9.0 };
				a[4] = new double[] { 19372.0 / 6561.0, -25360.0 / 2187.0, 64448.0 / 6561.0, -212.0 / 729.0 };
				a[5] = new double[] { 9017.0 / 3168.0, -355.0 / 33.0, 46732.0 / 5247.0, 49.0 / 176.0, -5103.0 / 18656.0 };
				a[6] = new double[] { 35.0 / 384.0, 0.0, 500.0 / 1113.0, 125.0 / 192.0, -2187.0 / 6784.0, 11.0 / 84.0 };
			}

			public Vec_d get(Vec_d x, double t, double dt)
			{	
				Xdot = f.get(x, t);
					k[0] = f.get(x, t).mul(dt);
					for (int i = 1; i < 7; i++)
					{
						System_Array_Copy(x, xtmp, f.get_neq());
						for (int ii = 0; ii < i; ii++)
							for (int j = 0; j < f.get_neq(); j++) xtmp.set(j,xtmp.get(j)+a[i][ii] * k[ii].get(j));
						k[i] = f.get(xtmp, t + dt * c.get(i)).mul(dt);
					}

					dx5 = k[0].mul(b.get(0));
					dx4 = k[0].mul(d.get(0));
					for (int i = 1; i < 7; i++)
					{
						dx5 = dx5.add( k[i].mul(b.get(i)) );
						dx4 = dx4.add( k[i].mul(d.get(i)) );
					}
					return dx5;
			}

			public Vec_d Error() { return dx5.add(dx4.mul(-1.0));} // no error available

			@Override
			public Vec_d Xdot() {
				// TODO Auto-generated method stub
				return Xdot;
			}

			@Override
			public int Order() {
				// TODO Auto-generated method stub
				return 5;
			}
		}
		
		private class RK1I implements Algorithm
		{
			Vec_d Xdot;
			ODE_FUN f;
			Vec_d dx;
			public RK1I(ODE ode) { this.f = ode.f; }
			public Vec_d get(Vec_d x, double t, double dt) { 
				Matrix_d A = f.dfdx(x, t).mul(-dt);
				for(int k = 0; k< f.get_neq();k++){
					A.set(k, k, A.get(k, k)+1);
				}
				Xdot = f.get(x, t);

				dx = Matrix_d.mxv(A.inv().mul(dt),f.get(x, t));
				
//				dx = f.get(x, t).mul(dt); 
				return dx;
			}
			@Override
			public Vec_d Error() {  return null; } // no error available
			@Override
			public Vec_d Xdot() {
				// TODO Auto-generated method stub
				return Xdot;
			}
			@Override
			public int Order() {
				// TODO Auto-generated method stub
				return 1;
			}
		}
		private class RK4I implements Algorithm{
			
			 double c2 = 0.386,                  c3 = 0.21; 
			 double c4 = 0.63;
			 double d1 = 0.2500000000000000e+00, d2 =-0.1043000000000000e+00;
			 double d3 = 0.1035000000000000e+00, d4 =-0.3620000000000023e-01;
			 double a21= 0.1544000000000000e+01, a31= 0.9466785280815826e+00;
			 double a32= 0.2557011698983284e+00, a41= 0.3314825187068521e+01;
			 double a42= 0.2896124015972201e+01, a43= 0.9986419139977817e+00;
			 double a51= 0.1221224509226641e+01, a52= 0.6019134481288629e+01;
			 double a53= 0.1253708332932087e+02, a54=-0.6878860361058950e+00;
			 double c21=-0.5668800000000000e+01, c31=-0.2430093356833875e+01;
			 double c32=-0.2063599157091915e+00, c41=-0.1073529058151375e+00;
			 double c42=-0.9594562251023355e+01, c43=-0.2047028614809616e+02;
			 double c51= 0.7496443313967647e+01, c52=-0.1024680431464352e+02;
			 double c53=-0.3399990352819905e+02, c54= 0.1170890893206160e+02;
			 double c61= 0.8083246795921522e+01, c62=-0.7981132988064893e+01;
			 double c63=-0.3152159432874371e+02, c64= 0.1631930543123136e+02;
			 double c65=-0.6058818238834054e+01;
			 double gam= 0.2500000000000000e+00;
			
			ODE_FUN f;
			Vec_d dx, y, df;
			Matrix_d A, B;
			Vec_d k1, k2, k3, k4, k5, k6;
			Vec_d error;
			Vec_d Xdot;
			
			public RK4I(ODE ode){ this.f = ode.f; }

			@Override
			public Vec_d get(Vec_d x, double t, double dt) {
				A  = f.dfdx(x, t).mul(-1.0);
				df = f.dfdt(x, t).mul(dt);
				
				for (int i=0; i<f.get_neq(); i++) A.set(i, i, A.get(i, i)+1.0/(gam*dt));

				B = A.inv();
				Xdot = f.get(x, t);
				k1 = B.mxv(f.get(x, t).add(df.mul(d1)));
				k2 = B.mxv(f.get(x.add(k1.mul(a21)),                                   t + c2*dt).add(df.mul(d2)).add(k1.mul(c21/dt)));
				k3 = B.mxv(f.get(x.add(k1.mul(a31)).add(k2.mul(a32)),                  t + c3*dt).add(df.mul(d3)).add(k1.mul(c31/dt)).add(k2.mul(c32/dt)));
				k4 = B.mxv(f.get(x.add(k1.mul(a41)).add(k2.mul(a42).add(k3.mul(a43))), t + c4*dt).add(df.mul(d4)).add(k1.mul(c41/dt)).add(k2.mul(c42/dt)).add(k3.mul(c43/dt)));
				
				y = x.add(k1.mul(a51)).add(k2.mul(a52)).add(k3.mul(a53)).add(k4.mul(a54));
				k5 = B.mxv(f.get(y, t+dt).add(k1.mul(c51/dt)).add(k2.mul(c52/dt)).add(k3.mul(c53/dt)).add(k4.mul(c54/dt)));
				
				y = y.add(k5);
				k6 = B.mxv(f.get(y, t+dt).add(k1.mul(c61/dt)).add(k2.mul(c62/dt)).add(k3.mul(c63/dt)).add(k4.mul(c64/dt)).add(k5.mul(c65/dt)));

				error=B.mxv(k6);
				dx = y.add(error).sub(x);
				return dx;
			}

			@Override
			public Vec_d Error() {
				return error;
			}

			@Override
			public Vec_d Xdot() {
				// TODO Auto-generated method stub
				return Xdot;
			}

			@Override
			public int Order() {
				// TODO Auto-generated method stub
				return 4;
			}
			
		}
		
		
		
		
		private class RK4IOLD implements Algorithm
		{
			double c2=0.386, c3=0.21, c4=0.63;

			double d1 = 0.2500000000000000e+00, d2 =-0.1043000000000000e+00;
			double d3 = 0.1035000000000000e+00, d4 =-0.3620000000000023e-01;
			double a21= 0.1544000000000000e+01, a31= 0.9466785280815826e+00;
			double a32= 0.2557011698983284e+00, a41= 0.3314825187068521e+01;
			double a42= 0.2896124015972201e+01, a43= 0.9986419139977817e+00;
			double a51= 0.1221224509226641e+01, a52= 0.6019134481288629e+01;
			double a53= 0.1253708332932087e+02, a54=-0.6878860361058950e+00;
			double c21=-0.5668800000000000e+01, c31=-0.2430093356833875e+01;
			double c32=-0.2063599157091915e+00, c41=-0.1073529058151375e+00;
			double c42=-0.9594562251023355e+01, c43=-0.2047028614809616e+02;
			double c51= 0.7496443313967647e+01, c52=-0.1024680431464352e+02;
			double c53=-0.3399990352819905e+02, c54= 0.1170890893206160e+02;
			double c61= 0.8083246795921522e+01, c62=-0.7981132988064893e+01;
			double c63=-0.3152159432874371e+02, c64= 0.1631930543123136e+02;
			double c65=-0.6058818238834054e+01;
			double gam= 0.2500000000000000e+00;

			// next coefficients only needed for dense output of the solution
			// this feature is not yet implemented
	//		double bet2p=0.0317, bet3p=0.0635, bet4p=0.3438;
	//		double d21= 0.1012623508344586e+02, d22=-0.7487995877610167e+01;
	//		double d23=-0.3480091861555747e+02, d24=-0.7992771707568823e+01;
	//		double d25= 0.1025137723295662e+01, d31=-0.6762803392801253e+00;
	//		double d32= 0.6087714651680015e+01, d33= 0.1643084320892478e+02;
	//		double d34= 0.2476722511418386e+02, d35=-0.6594389125716872e+01;

			ODE_FUN f;
			Vec_d dx, y, df;
			Matrix_d A, B;
			Vec_d k1, k2, k3, k4, k5, k6, Error;
			Vec_d Xdot;
			
			public RK4IOLD(ODE ode) { this.f = ode.f; }
			public Vec_d get(Vec_d x, double t, double dt) { 
				
				A = f.dfdx(x, t).mul(-1.0);					// Jacobian

				
				df = f.dfdt(x, t).mul(dt);				// df/dt * dt (partial derivative x dt)
				for (int i=0; i<f.get_neq(); i++) A.set(i, i, A.get(i, i)+1.0/(gam*dt));
				B=A.inv();
				
				Xdot = f.get(x, t);
				
				k1 = B.mxv(f.get(x, t).add(df.mul(d1)));
				k2 = B.mxv(f.get(x.add(k1.mul(a21)), 									t + c2*dt).add(df.mul(d2)).add(k1.mul(c21/dt)));
				k3 = B.mxv(f.get(x.add(k1.mul(a31)).add(k2.mul(a32)), 					t + c3*dt).add(df.mul(d3)).add(k1.mul(c31/dt)).add(k2.mul(c32/dt)));
				
				
				k4 = B.mxv(f.get(x.add(k1.mul(a41)).add(k2.mul(a42)).add(k3.mul(a43)), t+c4*dt).add(df.mul(d4)).add(k1.mul((c41/dt))).add(k2.mul(c42/dt)).add(k3.mul(c43/dt)));
				
				
				y  = x.add(k1.mul(a51)).add(k2.mul(a52)).add(k3.mul(a53)).add(k4.mul(a54));
				k5 = B.mxv(f.get(y, t+dt).add(k1.mul(c51/dt)).add(k2.mul(c52/dt)).add(k3.mul(c53/dt)).add(k4.mul(c54/dt)));

				y=y.add(k5);
				k6 = f.get(y, t+dt).add(k1.mul(c61/dt)).add(k2.mul(c62/dt)).add(k3.mul(c63/dt)).add(k4.mul(c64/dt)).add(k5.mul(c65/dt));

				Error=B.mxv(k6);
				dx=y.add(Error).sub(x);
				return dx;
				
			}
			@Override
			public Vec_d Error() {  return Error; } // no error available
			@Override
			public Vec_d Xdot() {
				// TODO Auto-generated method stub
				return Xdot;
			}
			@Override
			public int Order() {
				// TODO Auto-generated method stub
				return 4;
			}
		}
		// Bulirsch-Stoer algorithm for solving an ODE
		// most recommended solver for smooth problems and high accuracy
		// due to the high orders, stepsizes can be very large;
		// for instance: the harmonic oszillator problem for  omega=1 and tau=5
		// will be integrated from ta=0 to tb=20 in about 30 steps with 
		// full machine-precision (1.0e-14)
		private class BS implements Algorithm
		{
			private int NMAX;				// NMAX is the depth of extrapolation
			public int getNMAX(){
				return NMAX;
			}
			
			public void setNMAX(int val) {
				NMAX = val;
			}
			
			ODE_FUN f;							// reference to rhs of ODE
			Vec_d[][] P;							// Tableau for Extrapolation
			double[] hh;							// stepsizes
			Vec_d[] y;							// approximation for in-betweeen steps
			private Vec_d Xdot;
			
			public Vec_d getXdot() { return Xdot;}
			public void setXdot(Vec_d xDotNew) {Xdot = xDotNew;}
			
			
			// Ctor
			public BS(ODE ode) 
			{ 
				this.f = ode.f;				
				NMAX=8;						// usually values for NMAX between 6 and 14
				P=new Vec_d[NMAX][NMAX];	// yield the minmum number of function evaluations
				hh=new double[NMAX];
			}
			public Vec_d get(Vec_d x, double t, double dt){
					int n=0;
					double tt;
					for(int i=0; i<NMAX; i++)
					{
						n+=2;									// sequence of number of subintervals is 2,4,6,....
						double h=dt/n; 
						hh[i] = h*h;				// (sub)-stepsize for this n
						y=new Vec_d[n+1];						// modified midpoint-rule starts here
						tt=t; y[0]=x;
						Xdot=f.get(x, tt);
						y[1]=f.get(x, tt).mul(h).add(y[0]);			// first step: Euler-step to t+h
						for (int j=1; j<n; j++)
							y[j+1]=y[j-1].add(f.get(y[j], tt+=h).mul(2.0*h));	// midpoint-rule for all other steps
						P[i][ 0]=y[n];							// fill first column of tableau
						for (int k=1; k<=i; k++)				// Extrapolation to h²=0 (Neville algorithm)
							//P[i][k]=P[i][k-1]-hh[i]/(P[i][k-1].sub(P[i-1][k-1])).mul((hh[i]-hh[i-k]));
						
						P[i][k]=P[i][k-1].add((P[i][k-1].sub(P[i-1][k-1])).mul(-hh[i]/(hh[i]-hh[i-k])));
					}
					return P[NMAX-1][ NMAX-1].sub(x);					// return the correction 
				
			}

			public Vec_d Error()  { return P[NMAX-1][NMAX-1].sub(P[NMAX-2][NMAX-2]);  }
			public int Order() { return 2*NMAX-1;  }

			@Override
			public Vec_d Xdot() {
				// TODO Auto-generated method stub
				return Xdot;
			}

		} // class BS

		

	} // class RungeKutta
	
	public static void System_Array_Copy(Vec_d x, Vec_d y, int length){
		for(int i=0; i<length; i++){ 
			Vec_d.getVec(y)[i]=Vec_d.getVec(x)[i];
		}
	}
	public static void System_Array_Copy(double[] x, double[] y, int length){
        System.arraycopy(x, 0, y, 0, length);
	}
}
