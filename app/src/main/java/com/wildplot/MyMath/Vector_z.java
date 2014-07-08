package com.wildplot.MyMath;

public class Vector_z
{
    int n;
    Complex[] v;

    public Complex get(int i) {return v[i];}
    public void set(int i, Complex a) {v[i]=a;}

    public Vector_z(int n)
    {
        this.n=n;
        this.v=new Complex[n];
    }

    public Vector_z(int n, Complex a)
    {
        this.n=n;
        this.v=new Complex[n];
        for(int i=0; i<n; i++) v[i]=new Complex(a);
    }

    public Vector_z(Complex[] a)
    {
        this.n=a.length;
        this.v=new Complex[n];
        for(int i=0; i<n; i++) v[i]=new Complex(a[i]);
    }

    public Vector_z(double[] a, double[] b)
    {
        checkLength("Vector_z",a.length,b.length);
        n=a.length;
        v = new Complex[n];
        for(int i=0; i<n; i++) v[i]=new Complex(a[i],b[i]);
    }

//    Wie sind komplexe Zahlen (Zahlen in der Gaußschen Zahlenebene generell)
//    zu vergleichen???
//    Ausschlißelich über die Länge des Vektors?

    public static Complex max(Vector_z z)
    {
        double max = Complex.abs(z.v[0]);
        int index = 0;
        for(int i=0;i<z.v.length;i++)
            if (Complex.abs(z.v[i])>max)
            {
                max=Complex.abs(z.v[i]);
                index=i;
            }
        return z.v[index];
    }

    public Complex max()
    {
        return max(this);
    }

    public static Complex min(Vector_z z)
    {
        double min = Complex.abs(z.v[0]);
        int index = 0;
        for(int i=0;i<z.v.length;i++)
            if (Complex.abs(z.v[i])<min)
            {
                min=Complex.abs(z.v[i]);
                index=i;
            }
        return z.v[index];
    }

    public Complex min()
    {
        return min(this);
    }

    public static void checkLength(String type, int z, int x)
    {
         if(z != x){System.err.println("Vector_z::"+type+":Length mismath z_n="+z+"x_n="+x);System.exit(-1);}
    }

    public static Vector_z add(Vector_z x,Vector_z z)
    {
        checkLength("add",z.n,x.n);
        Vector_z r = new Vector_z(x.n);
            for(int i=0; i<x.n; i++) r.v[i]=Complex.add(x.v[i],z.v[i]);
        return r;
    }

    public static Vector_z add(Vector_d x,Vector_z z)
    {
        checkLength("add",z.n,x.n);
            Vector_z r = new Vector_z(x.n);
            for(int i=0; i<x.n; i++) r.v[i]=Complex.add(x.v[i],z.v[i]);
        return r;
    }

    public static Vector_z add(Vector_z z,Vector_d x)
    {
       checkLength("add",z.n,x.n);
            Vector_z r = new Vector_z(x.n);
            for(int i=0; i<x.n; i++) r.v[i]=Complex.add(x.v[i],z.v[i]);
        return r;
    }

    public Vector_z add(Vector_z z)
    {
        return add(this, z);
    }

    public Vector_z add(Vector_d z)
    {
        return add(this,z);
    }

    public static Vector_z sub(Vector_z x, Vector_z z)
    {
    checkLength("sub",z.n,x.n);
        Vector_z r = new Vector_z(x.n);
            for(int i=0; i<x.n; i++) r.v[i]=Complex.sub(x.v[i],z.v[i]);
        return r;
    }

    public static Vector_z sub(Vector_z z, Vector_d x)
    {
    checkLength("sub",z.n,x.n);
        Vector_z r = new Vector_z(x.n);
            for(int i=0; i<x.n; i++) r.v[i]=Complex.sub(z.v[i],x.v[i]);
        return r;
    }

    public static Vector_z sub(Vector_d x, Vector_z z)
    {
    checkLength("sub",z.n,x.n);
        Vector_z r = new Vector_z(x.n);
            for(int i=0; i<x.n; i++) r.v[i]=Complex.sub(x.v[i],z.v[i]);
        return r;
    }

    public Vector_z sub(Vector_z z)
    {
        return sub(this,z);
    }

    public Vector_z sub(Vector_d z)
    {
        return sub(this,z);
    }

    public static Vector_z div(Vector_z x,double value)
    {
        if(value == 0){System.err.println("Vector_z::Div:Division by zero");System.exit(-1);}
        Vector_z r = new Vector_z(x.n);
            for(int i=0; i<x.n; i++) r.v[i]=Complex.div(x.v[i],value);
        return r;
    }

    public Vector_z div(double value)
    {
        return div(this,value);
    }

    public static Vector_z mul(Vector_z x, double value)
    {
        Vector_z r = new Vector_z(x.n);
            for(int i=0; i<x.n; i++) r.v[i]=Complex.mul(value, x.v[i]);
        return r;
    }

    public static Vector_z mul(double value,Vector_z x)
    {
        Vector_z r = new Vector_z(x.n);
            for(int i=0; i<x.n; i++) r.v[i]=Complex.mul(value, x.v[i]);
        return r;
    }

    public Vector_z mul(double value)
    {
       return mul(this, value);
    }

    public Complex vxv(Vector_z z)
    {
        return vxv(this,z);
    }

    public static Complex vxv(Vector_z x,Vector_z z)
    {
    checkLength("vxv",x.n,z.n);
        Complex c = new Complex(0);
        for(int i=0; i<x.n;i++)
        {
            Complex c0 = Complex.mul(Complex.conj(x.v[i]), z.v[i]); //<x-conj*y>
            c = Complex.add(c, c0);
        }
        return c;
    }

    public static Complex vxv(Vector_d x,Vector_z z)
    {
    checkLength("vxv",x.n,z.n);   
        Complex c = new Complex(0);
        for(int i=0; i<x.n;i++)
        {
            Complex c0 = Complex.mul(x.v[i], z.v[i]); //<conj(x)*y>
            c = Complex.add(c, c0);
        }
        return c;
    }
    
    public static Complex vxv(Vector_z z, Vector_d x)
    {
    checkLength("vxv",x.n,z.n);   
        Complex c = new Complex(0);
        for(int i=0; i<x.n;i++)
        {
            Complex c0 = Complex.mul(x.v[i], z.v[i]); //<conj(x)*y>
            c = Complex.add(c, c0);
        }
        return c;
    }

    public static double nrm(Vector_z z)
    {
        return Math.sqrt(nrm2(z));
    }

    public double nrm()
    {
        return nrm(this);
    }

    public static double nrm2(Vector_z z)
    {
        double nrm2 =0;
        for (int i=0;i<z.n;i++)
        {
            Complex z2 = Complex.mul(Complex.conj(z.v[i]),z.v[i]);
            nrm2 += z2.X();
        }
        return nrm2;
    }

    public double nrm2()
    {
        return nrm2(this);
    }

    public static int minIndex(Vector_z z)
    {
        Complex min= min(z);
        int index =0;
        for (int i=0;i<z.n;i++)
            if (z.v[i]==min)  index=i;
        return index;
    }

    public int minIndex()
    {
        return minIndex(this);
    }

    public static int maxIndex(Vector_z z)
    {
        Complex max= max(z);
        int index =0;
        for (int i=0;i<z.n;i++)
            if (z.v[i]==max)  index=i;
        return index;
    }

    public int maxIndex()
    {
        return maxIndex(this);
    }

    public static void print(Vector_z z,String text)
    {
        System.out.print(text+"\n (");
        for (int i=0; i<z.n-1; i++)
        {
            System.out.println(Complex.print(z.v[i]));
        }
        System.out.println(Complex.print(z.v[z.n-1])+")");
    }

    public static int max_abs_index(int i0, Vector_z x)
    {
        int m=i0;
        for(int i=i0+1; i<x.n; i++)
            if(Complex.abs(x.v[i])>Complex.abs(x.v[m]))
                m=i;
        return m;
    }

    public int max_abs_index(int i0)
    {
        return max_abs_index(i0,this);
    }

    @Override
    public String toString()
    {
        String s="(";
        for(int i=0; i<n-1; i++) s+=v[i]+", ";
        s+=v[n-1]+")";
        return s;
    }

    public void print(String text)
    {
        print(this,text);
    }
}
