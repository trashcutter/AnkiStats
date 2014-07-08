// Matrix_z (int n, int m, complex c)
// V()

package com.wildplot.MyMath;

public class Matrix_z
{
    private Complex[][] v;
    private int m,n;

    public int M(){return m;}
    public int N(){return n;}

    public Complex   get(int i, int j)           {return v[i][j];}
    public void     set(int i, int j, Complex a) {v[i][j]=a;}
    
    public Matrix_z(Complex[][] C)
    {
        this.m = v.length;
        this.n = v[0].length;
        this.v = new Complex[m][n];
        //System.out.println(this+"Es ist eine "+this.i+"x"+this.j+" Matrix");
        for (int k=0;k<m;k++)
            for (int l=0;l<n;l++)
                v[k][l]=new Complex(C[k][l]);
    }

    public Matrix_z(int m, int n, Complex c)
    {
        this.m = m;
        this.n = n;
        this.v = new Complex[m][n];
        //System.out.println(this+"Es ist eine "+this.i+"x"+this.j+" Matrix");
        for (int k=0;k<m;k++)
            for (int l=0;l<n;l++)
                v[k][l]=c;
    }

    public Matrix_z(Matrix_z C)
    {
        this.m = C.v.length;
        this.n = C.v[0].length;
        this.v = new Complex[m][n];
        //System.out.println(this+"Es ist eine "+this.i+"x"+this.j+" Matrix");
        for (int k=0;k<m;k++)
            System.arraycopy(C.v[k], 0, v[k], 0, n);
    }

    public Matrix_z(int i, int j)
    {
        this.m = i;
        this.n = j;
        this.v = new Complex[m][n];
    }

    public static Matrix_z trp(Matrix_z c)
    {
        Matrix_z mt = new Matrix_z(c.n,c.m);
            for (int i=0; i<c.m; i++) //in eine Zeile gehen
                for (int j=0; j<c.n; j++) //die Spalten einer Zeile durchlaufen
                    mt.v[j][i]=c.v[i][j];
        return mt;
    }

    public Matrix_z trp()
    {
        return trp(this);
    }

    public static Matrix_z add(Matrix_z c, Matrix_z d)
    {
    checkLength("Add",c,d);
        Matrix_z res = new Matrix_z(c.m,c.n);
            for (int i=0; i<c.m; i++)
                for (int j=0; j<c.n; j++)
                    res.v[i][j]=Complex.add(c.v[i][j],d.v[i][j]);
        return c;
    }

    public Matrix_z add(Matrix_z M)
    {
        return add(this, M);
    }

    public static Matrix_z sub(Matrix_z c,Matrix_z d)
    {
    checkLength("Sub",c,d);
        Matrix_z res = new Matrix_z(c.m,c.n);
            for (int i=0; i<c.m; i++)
                for (int j=0; j<c.n; j++)
                    res.v[i][j]=Complex.sub(c.v[i][j], d.v[i][j]);
        return res;
    }

    public Matrix_z sub(Matrix_z M)
    {
        return sub(this, M);
    }

    public static Matrix_z mxm(Matrix_z c, Matrix_z d)
    {
        checkLength("mxm",c,d);
        Matrix_z res= new Matrix_z(c.m,d.n);
             for (int m=0;m<c.m;m++)
                for (int l=0;l<d.n;l++)
                    for (int n=0;n<c.n;n++)
                        res.v[m][l]=res.v[m][l].add( Complex.mul(c.v[m][n],d.v[n][l]) );
        return res;
    }

    public Matrix_z mxm(Matrix_z M)
    {
        return mxm(this, M);
    }

    public static Matrix_z mul(Matrix_z c, double value)
    {
        Matrix_z res = new Matrix_z(c.m,c.n);
        for (int i=0;i<c.m;i++)
            for (int j=0;j<c.m;j++)
                res.v[i][j]=Complex.mul(c.v[i][j], value);
        return c;
    }

    public Matrix_z mul (double value)
    {
        return mul(this,value);
    }


    public static Matrix_z div(Matrix_z c, double value)
    {
        Matrix_z res = new Matrix_z(c.m,c.n);
        for (int i=0;i<c.m;i++)
            for (int j=0;j<c.n;j++)
                res.v[i][j]=Complex.div(c.v[i][j], value);
        return c;
    }

    public Matrix_z div(double value)
    {
        return div(this, value);
    }

    public static void checkLength(String text, Matrix_z d, Matrix_z c)
    {
        if (d.m!=c.m || d.n!=c.n)
        {System.out.println("Matrix_z::"+text+": Die Dimensionen passen nicht für die Operation!");
         System.exit(-304);}
    }

    public static void checkLength(String text, Matrix_z d, Vector_z c)
    {
        if (d.m!=c.n)
        {System.out.println("Matrix_z::"+text+": Die Dimensionen passen nicht für die Operation!");
         System.exit(-304);}
    }

    public static void checkLength(String text, Matrix_z d, Vector_d c)
    {
        if (d.m!=c.n)
        {System.out.println("Matrix_z::"+text+": Die Dimensionen passen nicht für die Operation!");
         System.exit(-304);}
    }

    public void print (String name)
    {
        System.out.println(name+"=");
        for (int i=0;i<m;i++)
        {
            System.out.print("[");
            for (int j=0;j<n-1;j++)
                System.out.print(Complex.print(this.v[i][j])+" ");
            System.out.println(v[i][n-1]+"]");
        }
    }

    public static Vector_z mxv( Matrix_z A, Vector_z x)
    {
        checkLength("mxv(Mz*Vz)", A, x);
        Vector_z c=new Vector_z(A.m);
        for (int i=0; i<A.m; i++)
            for (int j=0; j<x.n;j++)
                c.v[i] = Complex.mul(A.v[i][j], x.v[j]);
        return c;
    }

    public Vector_z mxv( Matrix_z A, Vector_d x)
    {
        checkLength("mxv(Mz*Vd)", A, x);
        Vector_z c=new Vector_z(m);
        for (int i=0; i<m; i++)
            for (int j=0; j<x.n;j++)
                c.v[i] = Complex.mul(A.v[i][j], x.v[j]);
        return c;
    }

    public void setCol (int j, Vector_z x)
    {
        if(x.n!=m)
        {
            System.err.println("Länge stimmt nicht überein");
            System.exit(-1);
        }
        for (int i=0; i<this.M(); i++)
            v[i][j]=x.v[i];
    }

    public void setRow (int i, Vector_z x)
    {
        if(x.n!=n)
        {
            System.err.println("Länge stimmt nicht überein");
            System.exit(-1);
        }
        System.arraycopy(x.v, 0, v[i], 0, this.N());
    }

    public void mulRow (int i, double n)
    {
         //alternativ auch: Meins gefällt mir besser ^^ -> ist kürzer:
         Vector_z x = getRow(i);
         Vector_z y = x.mul(n);
         setRow(i,y);
    }

    public void divRow (int i, double n)
    {
        //for (int j=0;j<N();j++) v[i][j]/=n;
         Vector_z x = getRow(i);
         Vector_z y = x.div(n);
         setRow(i,y);
    }

    public void addRow (int i1, int i2)
    {
        //for (int j=0;j<N();j++) v[i2][j]+=v[i1][j];
         Vector_z x1 = getRow(i1);
         Vector_z x2 = getRow(i2);
         Vector_z y = x1.add(x2);
         setRow(i2,y);
    }

    public void subRow (int i1, int i2)
    {
        //for (int j=0;j<N();j++) v[i2][j]-=v[i1][j];
         Vector_z x1 = getRow(i1);
         Vector_z x2 = getRow(i2);
         Vector_z y = x2.sub(x1);
         setRow(i2,y);
    }

    public void changeRow (int i1, int i2)
    {
        Vector_z x1 = getRow(i1);
        Vector_z x2 = getRow(i2);

        this.setRow(i1, x2);
        this.setRow(i2, x1);
    }

    public Vector_z getRow(int k)
    {
        Complex[] a = new Complex[this.m];
        for (int j=0; j<this.m; j++)
        {
            a[j]=this.v[j][k-1];
        }
        return new Vector_z(a);
    }

    public Vector_z getCol(int k)
    {
        Complex[] a= new Complex[this.n];
        System.arraycopy(this.v[k - 1], 0, a, 0, this.n);
        return new Vector_z(a);
    }

    public Matrix_z gauss()
	{
		Matrix_z B = new Matrix_z(v);

		int mn = m > n ? n : m;

		for (int i = 0; i < mn; i++)
		{
			// pivot search
			Vector_z xi = B.getCol(i);
			int max = xi.max_abs_index(i);
			if (max != i)
			{
				B.changeRow(i, max);
			}
			if (Complex.abs(B.v[i][i]) == 0.0)
			{
				System.out.println("gauss: Column i=" + i + " is completely 0!");
				System.out.println("need to change columns instead of rows!");
				System.exit(-1);
			}
			// elimination step
			final Complex pivot = B.v[i][i];
			for (int j = i; j < n; j++)
			{
				B.v[i][j] = Complex.div(B.v[i][j],pivot);
			}
			for (int k = i + 1; k < m; k++)
			{
				final Complex factor = B.v[k][i];
				for (int j = i; j < n; j++)
				{
					//B.v[k][j] -= factor * B.v[i][j];
                                        B.v[k][j] = Complex.sub(B.v[i][j], Complex.mul(factor,B.v[i][j]));
				}
			}
		}
		return B;
	}

    public Matrix_z inv()
	{
		// invert a quadratic matrix using gauss-jordan algorithm

		// check if this matrix is quadratic

                Complex one = new Complex(1,0);

		if (m != n)
		{
			System.err.println("Matrix_d::inv: Matrix not quadratic! m=" + m + "  n=" + n);
			System.exit(-1);
		}

		// create a new matrix  of size m*2n; remember that m==n !!!
		Matrix_z B = new Matrix_z(m, 2 * n);

		// copy Matrix A to the left half of Matrix B and unity to the
		// right half of Matrix B

		// Example:
		//
		//      (1,2,3)		(1,2,3,1,0,0)
		//    A=(2,3,1)  -->> B=(2,3,1,0,1,0)
		//      (1,4,9)		(1,4,9,0,0,1)

		for (int i = 0; i < m; i++)
		{
            System.arraycopy(v[i], 0, B.v[i], 0, n);
			B.v[i][i + n] = one;		// right half
		}

		// now we apply gauss-algorithm to Matrix B
		for (int i = 0; i < m; i++)	// for each row
		{
			// (partial) pivot search;
			// for a full pivot search we would need to search the columns as well
			// but this somewhat more complicated, so we do not do it here
			Vector_z xi = B.getCol(i);
			int max = xi.max_abs_index(i);
			if (max != i)
			{
				B.changeRow(i, max);		// change rows if necessary
			}
			if (Complex.abs(B.v[i][i]) == 0.0)
			{
				System.out.println("inv: Column i=" + i + " is completely 0!");
				System.out.println("need to change columns as well as rows!");
				System.exit(-1);
			}
			final Complex pivot = B.v[i][i];
			for (int j = i; j < 2 * n; j++)
			{
				B.v[i][j] = Complex.div(B.v[i][j],pivot);	// scale row i
			}
			// elimination step ...

			for (int k = i + 1; k < m; k++)	// ... eliminate in all rows below i
			{
				final Complex factor = B.v[k][i];
				for (int j = i; j < 2 * n; j++)
				{
					//B.v[k][j] -= factor * B.v[i][j];
                                        B.v[k][j] = Complex.sub(B.v[i][j], Complex.mul(factor,B.v[i][j]));
				}
			}

			for (int k = 0; k <= i - 1; k++) // ... eliminate in all rows above i
			{
				final Complex factor = B.v[k][i];
				for (int j = i; j < 2 * n; j++)
				{
					//B.v[k][j] -= factor * B.v[i][j];
                                        B.v[k][j] = Complex.sub(B.v[i][j], Complex.mul(factor,B.v[i][j]));
				}
			}
		}

		// extract the right half from matrix B; this is the inverse!
		Matrix_z C = new Matrix_z(m, n);
		for (int i = 0; i < m; i++)
		{
            System.arraycopy(B.v[i], 0 + n, C.v[i], 0, n);
		}

		// ... and we are done; return C as the inverse of this matrix
		return C;
    }
}
