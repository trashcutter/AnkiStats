package com.wildplot.MyMath;

import com.wildplot.system.Vec_d;

public class Vector_d
{
    int n;
    double[] v;

    public double get(int i) {return v[i];}
    public void set(int i, double a) {v[i]=a;}

    public int   N(){return n;}
    public double[] V(){return v;}

    public Vector_d(int n)
    {
        this.n=n;
        this.v=new double[n];
        for (int i=0; i<n; i++) v[i]=0;
    }

    public Vector_d(int n, double a)
    {
        this.n=n;
        this.v=new double[n];
        for (int i=0; i<n; i++) v[i]=a;
    }

    public Vector_d(double[] a)
    {
        this.n=a.length;
        this.v=new double[n];
        System.arraycopy(a, 0, v, 0, n);
    }
    
    public Vector_d(Vec_d a)
    {
        this.n=Vec_d.getVec(a).length;
        this.v=new double[n];
        for (int i=0; i<n; i++) v[i]=Vec_d.getVec(a)[i];
    }

    public Vector_d(Vector_d a)
    {
        this.n=a.n;
        this.v=new double[n];
        System.arraycopy(a.v, 0, v, 0, n);
    }

    public static void checkLength(String type, int z, int x)
    {
       if(z != x){System.err.println("Vector_z::"+type+":Length mismath z_n="+z+"x_n="+x);System.exit(-1);}
    }

    public static double max(Vector_d z)
    {
        double max=z.v[0];
        for(int i=0;i<z.v.length;i++)
            if (z.v[i]>max) max=z.v[i];
        return max;
    }

    public double max()
    {
        return max(this);
    }

    public static double min(Vector_d z)
    {
        double max=z.v[0];
        for(int i=0;i<z.v.length;i++)
            if (z.v[i]<max) max=z.v[i];
        return max;
    }

    public double min()
    {
        return min(this);
    }

    public static double maxAbs(Vector_d z)
    {
        double max=Math.sqrt(z.v[0]*z.v[0]);
        for(int i=0;i<z.v.length;i++)
            if (Math.sqrt(z.v[0]*z.v[0])>max) max=Math.sqrt(z.v[0]*z.v[0]);
        return max;
    }

    public double maxAbs()
    {
        return maxAbs(this);
    }

    public static double minAbs(Vector_d z)
    {
        double min=Math.sqrt(z.v[0]*z.v[0]);
        for(int i=0;i<z.v.length;i++)
            if (Math.sqrt(z.v[0]*z.v[0])<min) min=Math.sqrt(z.v[0]*z.v[0]);
        return min;
    }

    public double minAbs()
    {
        return minAbs(this);
    }

    public static double maxIndex(Vector_d z)
    {
        double max=max(z);
        double maxIndex=0;
        int k=0;
        for(int i=0;i<z.v.length;i++)
            if (max==z.v[i]) {maxIndex=k;}
            k++;
        return k;
    }
    
    public static int max_abs_index(int i0, Vector_d x)
    {
        int m=i0;
        for(int i=i0+1; i<x.n; i++)
            if(Math.abs(x.v[i])>Math.abs(x.v[m]))
                m=i;
        return m;
    }

    public int max_abs_index(int i0)
    {
        return max_abs_index(i0,this);
    }

    public double maxIndex()
    {
        return maxIndex(this);
    }

    public static double minIndex(Vector_d z)
    {
        double min=min(z);
        double minIndex=0;
        int k=0;
        for(int i=0;i<z.v.length;i++)
            if (min==z.v[i]) {minIndex=k;}
            k++;
        return k;
    }

    public double minIndex()
    {
        return maxIndex(this);
    }

    public static Vector_d add(Vector_d x ,Vector_d z)
    {
    checkLength("Vector_d::add:Vector_d",x.n,z.n);
        Vector_d r = new Vector_d(x.n);
        for (int i=0;i<x.n;i++)
            r.v[i]=x.v[i]+z.v[i];
        return z;
    }

    public static Vector_z add(Vector_d x, Vector_z z) //stellt dem Benutzer frei ob complexer oder normaler Vektor zuerst genannt wird
    {
       return Vector_z.add(x, z);
    }

    public Vector_z add(Vector_z z)
    {
        return add(this, z);
    }

    public Vector_d add(Vector_d z)
    {
        return add(this, z);
    }

    public static Vector_d sub(Vector_d x, Vector_d z)
    {
    checkLength("sub",z.n,x.n);
        Vector_d r = new Vector_d(x.n);
        for (int i=0;i<x.n;i++)
            r.v[i]=x.v[i]-z.v[i];
        return r;
    }

    public static Vector_z sub(Vector_d x, Vector_z z)
    {
        return Vector_z.sub(x,z);
    }

    public Vector_z sub(Vector_z z)
    {
        return sub(this,z);
    }

    public Vector_d sub(Vector_d z)
    {
        return sub(this,z);
    }

    public static Vector_d div(Vector_d x,double value)
    {
    if(value == 0){System.err.println("Vector_d::Div:Division by zero");System.exit(-1);}
        Vector_d r = new Vector_d(x.n);
        for (int i=0;i<x.n;i++)
            r.v[i]=x.v[i]/value;
        return r;
    }

    public Vector_d div(double value)
    {
        return div(this,value);
    }

    public static Vector_d mul(Vector_d x, double value)
    {
        Vector_d r = new Vector_d(x.n);
        for (int i=0;i<x.n;i++)
            r.v[i]=x.v[i]*value;
        return r;
    }

    public static Vector_d mul(double value,Vector_d x)
    {
        Vector_d r = new Vector_d(x.n);
        for (int i=0;i<x.n;i++)
            r.v[i]=x.v[i]*value;
        return r;
    }

    public Vector_d mul(double value)
    {
       return mul(this, value);
    }

    public double vxv(Vector_d z)
    {
        return vxv(this,z);
    }

    public static double vxv(Vector_d x,Vector_d z)
    {
    checkLength("vxv:DOUBLE:",x.n,z.n);
        double sp =0;
        for(int i=0; i<x.n;i++)sp+=x.v[i]*z.v[i];
        return sp;
    }

    public static Complex vxv(Vector_d x,Vector_z z)
    {
        return Vector_z.vxv(x, z);
    }

    public Complex vxv(Vector_z z)
    {
        return vxv(this,z);
    }

    public static double nrm2(Vector_d z)
    {
        int r=0;
        for (int i=0;i<z.n;i++)
            r+=z.v[i]*z.v[i];
        return r;
    }

    public double nrm2()
    {
        return nrm2(this);
    }

    public static double nrm(Vector_d z)
    {
        double r=0;
        return r=Math.sqrt(nrm2(z));
    }

    public double nrm()
    {
        return nrm(this);
    }
        
    public static Vector_d xprod(Vector_d x, Vector_d z)
    {
        if (x.n!=3 || z.n!=3){System.err.println("Vector_d::xProd:Vektoren sind nicht beide 3 dimensional");System.exit(-1);}
        Vector_d res = new Vector_d(3);
        res.v[0]=x.v[1]*z.v[2]-x.v[2]*z.v[1];
        res.v[1]=x.v[2]*z.v[0]-x.v[0]*z.v[2];
        res.v[2]=x.v[0]*z.v[1]-x.v[1]*z.v[0];
        return res;
    }

    public Vector_d xprod(Vector_d x)
    {
        return xprod(this,x);
    }

    @Override
    public String toString()
    {
        String s="(";
        for(int i=0; i<n-1; i++) s+=v[i]+", ";
        s+=v[n-1]+")";
        return s;
    }

    public static void print(String name, Vector_d x)
    {
        System.out.print(name + "=(");
        for (int i = 0; i < x.n - 1; i++)
        {
            System.out.print(x.v[i] + ", ");
        }
        System.out.print(x.v[x.n - 1] + ")\n");
    }

    public void print(String name)
    {
        print(name, this);
    }

    public static void print(Vector_d x)
    {
        System.out.print("(");
        for (int i = 0; i < x.n - 1; i++)
        {
            System.out.print(x.v[i] + ", ");
        }
        System.out.print(x.v[x.n - 1] + ")\n");
    }

    public void print() {print(this);}

    public static void printf(String name, String format, Vector_d x)
    {
        System.out.print(name + "=(");
        for (int i = 0; i < x.n - 1; i++)
        {
            System.out.printf(format+", ", x.v[i]);
        }
        System.out.printf(format+")\n", x.v[x.n - 1]);
    }

    public void printf(String name, String format)
    {
        printf(name, format, this);
    }

    public static void printf(String format, Vector_d x)
    {
        System.out.print("(");
        for (int i = 0; i < x.n - 1; i++)
        {
            System.out.printf(format+", ", x.v[i]);
        }
        System.out.printf(format+")\n", x.v[x.n - 1]);
    }

    public void printf(String format) {printf(format, this);}
}
