package com.wildplot.system;

import com.wildplot.MyMath.Matrix_d;
import com.wildplot.MyMath.Vector_d;

public class Glyco implements ODE_FUN{
	
	private double Glycose=12.8174,K_Glucose=0.37,K_ATP=0.1;
	private double Vmax_egal=1398.0,Vmax=50.2747,Vmax_4=44.7287;
	private double Vmax_f_3=140.282,Vmax_r_3=140.282;
	private double K_Gluc6P_3=0.8,K_Fruc6P_3=0.15,K_Fruc6P_4=0.021;
	private double k2=2.26,k5=6.04662,k6=68.48,k7=3.21,K=0.15;
	private double k8f=432.9,k8r=133.33;
	private Matrix_d N;
	
	public Glyco(){
		N=new Matrix_d(6,8);
		N.set(0, 0, 1); N.set(0, 1, -1); N.set(0, 2, -1);
		N.set(1, 2, 1); N.set(1, 3, -1);
		N.set(2, 3, 1); N.set(2, 4, -1);
		N.set(3, 0, -1); N.set(3, 1, -1); N.set(3, 3, -1); N.set(3, 5, 1); N.set(3, 6, -1); N.set(3, 7, -1);
		N.set(4, 0, 1); N.set(4, 1, 1); N.set(4, 3, 1); N.set(4, 5, -1); N.set(4, 6, 1); N.set(4, 7, 2);
		N.set(5, 7, -1);
	}

	@Override
	public Vec_d get(Vec_d x, double t) {
//		Vec_d f=new Vec_d(6);
//		f.set(0, nu1(x)-nu2(x)-nu3(x));
//		f.set(1, nu3(x)-nu4(x));
//		f.set(2, nu4(x)-nu5(x));
//		f.set(3, -nu1(x)-nu2(x)-nu4(x)+nu6(x)-nu7(x)-nu8(x));
//		f.set(4, nu1(x)+nu2(x)+nu4(x)-nu6(x)+nu7(x)+2*nu8(x));
//		f.set(5, -nu8(x));
//		return f;
		
		Vector_d calc=Matrix_d.mxv(N, n(x));
		return new Vec_d(calc);
	}

	@Override
	public int get_neq() {
		return 6;
	}
	
	private Vector_d n (Vec_d x){
		Vector_d out=new Vector_d(8);
		out.set(0, nu1(x));
		out.set(1, nu2(x));
		out.set(2, nu3(x));
		out.set(3, nu4(x));
		out.set(4, nu5(x));
		out.set(5, nu6(x));
		out.set(6, nu7(x));
		out.set(7, nu8(x));
		return out;
	}
	
	double nu1(Vec_d x) {
		//return (vmax1*x.get(3)*glycose)/(1+(x.get(3)/katp1)+(glycose/kglucose)+(x.get(3)/katp1)*(glycose/kglucose));
		return Vmax*x.get(3)/(K_ATP+x.get(3));}
	double nu2(Vec_d x) {return k2*x.get(3)*x.get(0);}
	double nu3(Vec_d x) {
		return 
				( Vmax_f_3/K_Gluc6P_3*x.get(0) - Vmax_r_3/K_Fruc6P_3*x.get(1) ) /
				( 1.0 + x.get(0)/K_Gluc6P_3 + x.get(1)/K_Fruc6P_3)
		;
	}
	double nu4(Vec_d x) {
		return 
				( Vmax_4 * x.get(1) * x.get(1)) /
				( K_Fruc6P_4*(1+K*(x.get(3)/x.get(5)*(x.get(3)/x.get(5)))) + x.get(1)*x.get(1) )
		;
	}
	double nu5(Vec_d x) {return k5*x.get(2);}
	double nu6(Vec_d x) {return k6*x.get(4);}
	double nu7(Vec_d x) {return k7*x.get(3);}
	double nu8(Vec_d x) {return k8f*x.get(3)*x.get(5)-k8r*x.get(4)*x.get(4);}

	@Override
	public Vec_d dfdt(Vec_d x, double t) {
		return new Vec_d(6);
	}

	@Override
	public Matrix_d dfdx(Vec_d x, double t) {
		return N;
	}
}
