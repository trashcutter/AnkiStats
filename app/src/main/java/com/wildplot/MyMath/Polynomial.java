package com.wildplot.MyMath;

public class Polynomial
{
    //data
    Vector_d a;


    //Ctor
    public Polynomial(Vector_d a)
    {
        this.a=a;
    }

    public Polynomial(double [] a)
    {
        this.a=new Vector_d(a);
    }

    // p(x)= a_0 + a_1*x + a_2*x^2 + a_3x^3
    //     = a_0 + x(a_1 + x(a_2 + x a_3));
    //     = a_2 + x a_3;
    //     = a_1 + x * sum;
    //     = a_0 + x * sum;
    public double get(double x) //PN Auswerten an der Stelle x
    {
        double sum=a.get(a.N()-1);

        for (int i=a.N()-2; i>=0; i--)
        {
            sum=sum*x+a.get(i);
        }
        return sum;
    }

    // p(x)  = a_0 + a_1*x + a_2*x^2 + a_3x^3
    // p'(x) = a_1 + 2 * a_2*x + 3 * a_3x^2
    // p'(x) = a_1 + x(2*a_2 +x(3*a_3))
    //       = a_2 * 2 + x * a_3 * 3;
    //       = a_1 + x * sum;
    //       = a_0 + x * sum;
    public double getDerivative(double x)
    {
        double sum=a.get(a.N()-1)*(a.N()-1);

        for (int i=a.N()-2; i>0; i--)
        {
            sum=sum*x+i*a.get(i);
        }
        return sum;
    }

//    public double getDerivative(int i, double x)
//    {
//        double sum=a.get(a.N()-1)*(a.N()-1);
//
//        for (int i=a.N()-2; i>0; i--)
//        {
//            sum=sum*x+i*a.get(i);
//        }
//        return sum;
//    }

//    Interpolation
//            p(x)=a0+a1*x+a2*x^2+a3*x^3
//
//            a0 + a1*x0 + a2*x0^2 + a2*x0^2 + a3*x0^3 =y0
//            a0 + a1*x1 + a2*x1^2 + a2*x1^2 + a3*x1^3 =y1
//            a0 + a1*x2 + a2*x2^2 + a2*x2^2 + a3*x2^3 =y2
//            a0 + a1*x3 + a2*x3^2 + a2*x3^2 + a3*x3^3 =y3
//
//                         Ax=b
//            (1 x0 x0^2 x0^3)  (a0)      (y0)
//            (1 x1 x1^2 x1^3)  (a1)      (y1)
//            (1 x2 x2^2 x2^3)  (a2)  =   (y2)
//            (1 x3 x3^2 x3^3)  (a3)      (y3)
//
//                    X        * a    =    y
}
