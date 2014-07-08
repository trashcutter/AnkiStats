package com.wildplot.MyMath;
public class Complex
{
    final double PI=Math.PI;
    final double E=Math.E;

    private double x;	// real part of a complex
    private double y;	// imaginary part of a complex;

    public double X()       {return x;}
    public double real()    {return x;}

    public double Y()       {return y;}
    public double imag()    {return y;}

    public void setX(double x) {this.x=x;}
    public void setY(double y) {this.y=y;}

    public Complex()                        {this.x=0; this.y=0;}
    public Complex(double x, double y)      {this.x=x;this.y=y;}
    public Complex(double a)                {this.x=a;this.y=0;}
    public Complex(Complex a)               {this.x=a.x; this.y=a.y;}

    @Override
    public String toString() {return "["+x+","+y+"]";}
    
    public double toBow(double degrees)     {double bow=(PI*degrees)/360;return bow;}

    public static Complex conj(Complex z)   {return new Complex(z.x,(-1)*z.y);}

    public double[] toPolar(Complex z)      {double[] p=new double[2];
                                               p[0]=abs(z);
                                               p[1]=Math.atan2(z.y,z.x);
                                             return p;}

    public Complex fromPolar(double[] p){return new Complex(p[0]*Math.cos(p[1]),p[0]*Math.sin(p[1]));}
    //e^z=e^x*(cos (y)+ i sin(y))
    public Complex exp()                {return new Complex(Math.pow(E,this.x)*Math.cos(this.y),Math.pow(E,this.x)*Math.sin(this.y));}

    public Complex pow(int n)
    {
        double[] p = toPolar(this);
        if      (n==0)                  return new Complex(1,0);
        else if (n>0)                   return new Complex(Math.pow(p[0],n)*Math.cos(n*Math.cos(p[1])),Math.pow(p[0],n)*Math.sin(n*Math.sin(p[1])));
        else                            n = n*(-1);return new Complex(1/(Math.pow(p[0],n)*Math.cos(n*Math.cos(p[1]))),1/(Math.pow(p[0],n)*Math.sin(n*Math.sin(p[1]))));
    }
    

    public static double  abs (Complex z)      {double abs = (Math.sqrt(abs2(z)));return abs;}
    public static double abs2 (Complex z)      {double abs2=(z.x*z.x + z.y*z.y);return abs2;}

    public          Complex add (Complex z)             {return add(this,z);}
    public          Complex add (double n)              {return add(this,n);}
    public static   Complex add (Complex x, Complex z)  {return new Complex(x.x+z.x,x.y+z.y);}   
    public static   Complex add (double n, Complex z)   {return new Complex(n+z.x,z.y);}
    public static   Complex add (Complex z, double n)   {return new Complex(n+z.x,z.y);}

    public          Complex sub (Complex z)             {return sub(this,z);}
    public          Complex sub (double n)              {return sub(this,n);}
    public static   Complex sub (Complex x, Complex z)  {return new Complex(x.x-z.x,x.y-z.y);}
    public static   Complex sub (double n, Complex z)   {return new Complex(n-z.x,-z.y);}
    public static   Complex sub (Complex z, double n)   {return new Complex(z.x-n,z.y);}

    public          Complex mul (Complex z)             {return mul(this,z);}
    public          Complex mul (double n)              {return mul(this,n);}
    public static   Complex mul (Complex x, Complex z)  {return new Complex(x.x*z.x-x.y*z.y,x.x*z.y+z.x*x.y);}
    public static   Complex mul (Complex z, double n)   {return new Complex(z.x*n,z.y*n);}
    public static   Complex mul (double n, Complex z)   {return new Complex(z.x*n,z.y*n);}

    public          Complex div (Complex z)             {return div(this,z);}
    public          Complex div (double n)              {return div(this,n);}
    public static   Complex div (Complex x, Complex z)  {Complex z1 = x.mul(conj(z));return new Complex(z1.x/abs2(z),z1.y/abs2(z));}
    public static   Complex div (Complex z, double n)   {return new Complex(z.x/n,z.y/n);}
    public static   Complex div (double n, Complex z)   {Complex z1 = mul(n,conj(z));return new Complex(z1.x/abs2(z),z1.y/abs2(z));}
    
                                        // this x  y
                                        // this a+ib   (a+ib)(c+id)     (a*c-b*d)   a*d+c*b
                                        //      ---- = ------------ =   --------- + -------
                                        //   z  c-id       c²+d²          c²+d²      c²+d²
                                        //   z  x  y

    public static void print(String text, Complex z)    {System.out.println(text+": ("+z.x+","+z.y+")");}
    public static String print(Complex z)                 {return new String(z.x+"|"+z.y+"i");}
    public static void print(double x, double y)        {System.out.println("("+x+" ; "+y+")");}
}