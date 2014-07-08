package com.wildplot.MyMath;

public class Rational
{
    	private long x;
	private long y;

    public Rational(long x, long y) {this.x=x;this.y=y;}
    public Rational(long a)         {this.x=a;this.y=1;}
    public Rational add(Rational r) {dieTryin(r);return reduce(this.x*r.y+r.x*this.y,this.y*r.y);}
    public Rational sub(Rational r) {dieTryin(r);return reduce(this.x*r.y-r.x*this.y,this.y*r.y);}

    public Rational mul(Rational r) {dieTryin(r);return reduce(this.x*r.x,this.y*r.y);}
    public Rational div(Rational r) {dieTryin(r);return reduce(this.x*r.y,r.x*this.y);}

    public long gcd(long x, long y)
    {
        long rr=1;
        long rout=0;
        while (rr !=0)
        {
            rr=x%y;
            x=y;
            rout=y;
            y=rr;
        }
        return rout;

    }

    public Rational reduce(long r1, long r2)
    {
        Rational r = new Rational(r1,r2);
        dieTryin(r);
        long gcd = gcd(r1,r2);
        return new Rational(r1/gcd,r2/gcd);
    }

    public Rational pow(int n)
    {

        if(this.y==0)
        {
        System.out.println("Die Bitch!");
        System.exit(-666);
        }

        if (n==0)
        {
            return new Rational(1,1);
        }
        else if (n>0)
        {
            Rational r0=new Rational((long)Math.pow (this.x,n),(long)Math.pow (this.y,n));
            return reduce(r0.x,r0.y);
        }
        else
        {
            n = n*(-1);
            Rational r0=new Rational(1/(long)(Math.pow(this.x,n)),1/(long)(Math.pow(this.y,n)));
            return reduce(r0.x,r0.y);
        }
    }

    public void dieTryin(Rational r)                    {if(r.x==0)System.out.println("Die Bitch!");System.exit(-666);}
    public static void print(String text, Rational r)   {System.out.println(text+": ("+r.x+","+r.y+")");}
    public static void print(Rational r)                {System.out.println("("+r.x+" / "+r.y+")");}
}
