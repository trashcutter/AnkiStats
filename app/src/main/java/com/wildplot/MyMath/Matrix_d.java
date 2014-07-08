//
// a class for real valued dense matrices
//
package com.wildplot.MyMath;

import com.wildplot.system.Vec_d;

public class Matrix_d
{
	// data

	private int n,  m;
	double[][] v;

	// access to data
	public int M()
	{
		return m;
	}

	public int N()
	{
		return n;
	}

	public double[][] V()
	{
		return v;
	}

	public void set(int i, int j, double value)
	{
		v[i][j] = value;
	}

	public double get(int i, int j)
	{
		return v[i][j];
	}

	public Vector_d getRow(int i)
	{
		Vector_d r = new Vector_d(n);
		for (int j = 0; j < n; j++)
		{
			r.set(j, v[i][j]);
		}
		return r;
	}

	public void setRow(int i, Vector_d r)
	{
		if (r.N() != n)
		{
			System.err.println("Matrix_d::setRow: Length Mismatch! n=" + n + "  r.n=" + r.N());
			System.exit(-1);
		}
		for (int j = 0; j < n; j++)
		{
			v[i][j] = r.get(j);
		}
	}

	public Vector_d getCol(int j)
	{
		Vector_d r = new Vector_d(m);
		for (int i = 0; i < m; i++)
		{
			r.set(i, v[i][j]);
		}
		return r;
	}

	public void setCol(int j, Vector_d r)
	{
		if (r.N() != m)
		{
			System.err.println("Matrix_d::setCol: Length Mismatch! m=" + m + "  r.n=" + r.N());
			System.exit(-1);
		}
		for (int i = 0; i < m; i++)
		{
			v[i][j] = r.get(i);
		}
	}

	// utilities
	private static void check_length(String name, int n1, int n2)
	{
		if (n1 != n2)
		{
			System.err.println("Matrix_d::" + name + ": Length Mismatch: n1=" + n1 + "  n2=" + n2);
			System.exit(-1);
		}
	}

//	public static void print(String name, Matrix_d A)
//	{
//		System.out.print(name + "=\n");
//		for (int i = 0; i < A.m; i++)
//		{
//			Vector_d x = A.getRow(i);
//			x.print();
//		}
//	}
//
//	public void print(String name)
//	{
//		print(name, this);
//	}

//	public static void printf(String name, String format, Matrix_d A)
//	{
//		System.out.print(name + "=\n");
//		for (int i = 0; i < A.m; i++)
//		{
//			Vector_d x = A.getRow(i);
//			x.printf(format);
//		}
//	}
//
//	public void printf(String name, String format)
//	{
//		printf(name, format, this);
//	}

	// Ctors
	public Matrix_d(int m, int n)
	{
		this.m = m;
		this.n = n;
		this.v = new double[m][n];
	}

	public Matrix_d(int m, int n, double a)
	{
		this.m = m;
		this.n = n;
		this.v = new double[m][n];
		for (int i = 0; i < m; i++)
		{
			for (int j = 0; j < n; j++)
			{
				this.v[i][j] = a;
			}
		}
	}

	public Matrix_d(double[][] a)
	{
		this.m = a.length;
		this.n = a[0].length;
		this.v = new double[m][n];
		for (int i = 0; i < m; i++)
		{
            System.arraycopy(a[i], 0, this.v[i], 0, n);
		}
	}

	// Arithmetics
	public static Matrix_d add(Matrix_d A, Matrix_d B)
	{
		check_length("add:FirstIndex:", A.m, B.m);
		check_length("add:SecondIndex:", A.n, B.n);

		Matrix_d C = new Matrix_d(A.m, A.n);
		for (int i = 0; i < A.m; i++)
		{
			for (int j = 0; j < A.n; j++)
			{
				C.v[i][j] = A.v[i][j] + B.v[i][j];
			}
		}
		return C;
	}

	public Matrix_d add(Matrix_d B)
	{
		return add(this, B);
	}

	public static Matrix_d sub(Matrix_d A, Matrix_d B)
	{
		check_length("sub:FirstIndex:", A.m, B.m);
		check_length("sub:SecondIndex:", A.n, B.n);

		Matrix_d C = new Matrix_d(A.m, A.n);
		for (int i = 0; i < A.m; i++)
		{
			for (int j = 0; j < A.n; j++)
			{
				C.v[i][j] = A.v[i][j] - B.v[i][j];
			}
		}
		return C;
	}

	public Matrix_d sub(Matrix_d B)
	{
		return sub(this, B);
	}

	public static Matrix_d mul(Matrix_d A, double a)
	{
		Matrix_d B = new Matrix_d(A.m, A.n);
		for (int i = 0; i < A.m; i++)
		{
			for (int j = 0; j < A.n; j++)
			{
				B.v[i][j] = A.v[i][j] * a;
			}
		}
		return B;
	}

	public static Matrix_d mul(double a, Matrix_d A)
	{
		return mul(A, a);
	}

	public Matrix_d mul(double a)
	{
		return mul(this, a);
	}

	public static Matrix_d div(Matrix_d A, double a)
	{
		Matrix_d B = new Matrix_d(A.m, A.n);
		for (int i = 0; i < A.m; i++)
		{
			for (int j = 0; j < A.n; j++)
			{
				B.v[i][j] = A.v[i][j] / a;
			}
		}
		return B;
	}

	public Matrix_d div(double a)
	{
		return div(this, a);
	}

	// Matrix x Vector
	public static Vector_d mxv(Matrix_d A, Vector_d x)
	{
		check_length("mxv:", A.n, x.N());
		double[] vx = x.V();
		double vy[] = new double[A.m];

		for (int i = 0; i < A.m; i++)
		{
			for (int j = 0; j < A.n; j++)
			{
				vy[i] += A.v[i][j] * vx[j];
			}
		}
		return new Vector_d(vy);
	}

	public Vector_d mxv(Vector_d x)
	{
		return mxv(this, x);
	}
	
	// Matrix x Vector
	public static Vec_d mxv(Matrix_d A, Vec_d x)
	{
		check_length("mxv:", A.n, x.N());
		double[] vx = x.getVec();
		double vy[] = new double[A.m];

		for (int i = 0; i < A.m; i++)
		{
			for (int j = 0; j < A.n; j++)
			{
				vy[i] += A.v[i][j] * vx[j];
			}
		}
		return new Vec_d(vy);
	}

	public Vec_d mxv(Vec_d x)
	{
		return mxv(this, x);
	}

	// Matrix x Matrix
	public static Matrix_d mxm(Matrix_d A, Matrix_d B)
	{
		check_length("mxm", A.n, B.m);
		double[][] vc = new double[A.m][B.n];
		for (int i = 0; i < A.m; i++)
		{
			for (int j = 0; j < B.n; j++)
			{
				for (int k = 0; k < A.n; k++)
				{
					vc[i][j] += A.v[i][k] * B.v[k][j];
				}
			}
		}
		return new Matrix_d(vc);
	}

	public Matrix_d mxm(Matrix_d B)
	{
		return mxm(this, B);
	}

	// row operations on a matrix
	public void changeRows(int i1, int i2)
	{
		for (int j = 0; j < n; j++)
		{
			// swap the values
			double tmp = v[i1][j];
			v[i1][j] = v[i2][j];
			v[i2][j] = tmp;
		}
	}

	public void mulRow(int i, double a)
	{
		for (int j = 0; j < n; j++)
		{
			v[i][j] *= a;
		}
	}

	public void addRow(int i1, int i2)
	{
		for (int j = 0; j < n; j++)
		{
			v[i2][j] += v[i1][j];
		}
	}

	public void addRow(int i1, int i2, double a)
	{
		// add a x row(i1) to row(i2): r2=a*r1
		for (int j = 0; j < n; j++)
		{
			v[i2][j] += a * v[i1][j];
		}
	}

	// this is the function we developed in Lab
	public Matrix_d gauss()
	{
		Matrix_d B = new Matrix_d(v);

		int mn = m > n ? n : m;

		for (int i = 0; i < mn; i++)
		{
			// pivot search
			Vector_d xi = B.getCol(i);
			int max = xi.max_abs_index(i);
			if (max != i)
			{
				B.changeRows(i, max);
			}
			if (Math.abs(B.v[i][i]) == 0.0)
			{
				System.out.println("gauss: Column i=" + i + " is completely 0!");
				System.out.println("need to change columns instead of rows!");
				System.exit(-1);
			}
			// elimination step
			final double pivot = B.v[i][i];
			for (int j = i; j < n; j++)
			{
				B.v[i][j] /= pivot;
			}
			for (int k = i + 1; k < m; k++)
			{
				final double factor = B.v[k][i];
				for (int j = i; j < n; j++)
				{
					B.v[k][j] -= factor * B.v[i][j];
				}
			}
		}
		return B;
	}

    public Matrix_d inv()
    {
        // invert a quadratic matrix using gauss-jordan algorithm

        // check if this matrix is quadratic
        if (m != n)
        {
            System.err.println("Matrix_d::inv: Matrix not quadratic! m=" + m + "  n=" + n);
            System.exit(-1);
        }

        // create a new matrix  of size m*2n; remember that m==n !!!
        Matrix_d B = new Matrix_d(m, 2 * n);

        // copy Matrix A to the left half of Matrix B and unity to the
        // right half of Matrix B

        // Example:
        //
        //		(1,2,3)			(1,2,3,1,0,0)
        //    A=(2,3,1)  -->> B=(2,3,1,0,1,0)
        //		(1,4,9)			(1,4,9,0,0,1)

        for (int i = 0; i < m; i++)
        {
            System.arraycopy(v[i], 0, B.v[i], 0, n);
            B.v[i][i + n] = 1.0;		// right half
        }

        // now we apply gauss-algorithm to Matrix B
        for (int i = 0; i < m; i++)	// for each row
        {
            // (partial) pivot search;
            // for a full pivot search we would need to search the columns as well
            // but this somewhat more complicated, so we do not do it here
            Vector_d xi = B.getCol(i);
            int max = xi.max_abs_index(i);
            if (max != i)
            {
                B.changeRows(i, max);		// change rows if necessary
            }
            if (Math.abs(B.v[i][i]) == 0.0)
            {
                System.out.println("inv: Column i=" + i + " is completely 0!");
                System.out.println("need to change columns as well as rows!");
                System.exit(-1);
            }
            final double pivot = B.v[i][i];
            for (int j = i; j < 2 * n; j++)
            {
                B.v[i][j] /= pivot;	// scale row i
            }
            // elimination step ...

            for (int k = i + 1; k < m; k++)	// ... eliminate in all rows below i
            {
                final double factor = B.v[k][i];
                for (int j = i; j < 2 * n; j++)
                {
                    B.v[k][j] -= factor * B.v[i][j];
                }
            }

            for (int k = 0; k <= i - 1; k++) // ... eliminate in all rows above i
            {
                final double factor = B.v[k][i];
                for (int j = i; j < 2 * n; j++)
                {
                    B.v[k][j] -= factor * B.v[i][j];
                }
            }
        }

        // extract the right half from matrix B; this is the inverse!
        Matrix_d C = new Matrix_d(m, n);
        for (int i = 0; i < m; i++)
        {
            System.arraycopy(B.v[i], 0 + n, C.v[i], 0, n);
        }

        // ... and we are done; return C as the inverse of this matrix
        return C;
    }
    public Vector_d solve(Vector_d  b)
    {
        return this.inv().mxv(b);
    }

    public static void print(String name, Matrix_d A)
    {
        System.out.print(name + "=\n");
        for (int i = 0; i < A.m; i++)
        {
            Vector_d x = A.getRow(i);
            x.print();
        }
    }

    public void print(String name)
    {
        print(name, this);
    }

    public static void printf(String name, String format, Matrix_d A)
    {
        System.out.print(name + "=\n");
        for (int i = 0; i < A.m; i++)
        {
            Vector_d x = A.getRow(i);
            x.printf(format);
        }
    }

    public void printf(String name, String format)
    {
        printf(name, format, this);
    }
    
} // class Matrix_d
