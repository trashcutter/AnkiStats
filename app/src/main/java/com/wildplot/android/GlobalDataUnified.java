package com.wildplot.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import android.util.Log;
import com.wildplot.android.control.FunctionParserWrapper;
import com.wildplot.android.densityFunctions.ASH;
import com.wildplot.android.densityFunctions.Density2D;
import com.wildplot.android.densityFunctions.KDE;
import com.wildplot.android.kernelFunctions.*;
import com.wildplot.android.newParsing.TopLevelParser;
import com.wildplot.android.parsing.FunctionParser;
import com.wildplot.android.parsing.SplineInterpolation;
import com.wildplot.android.regressionFunctions.LinearRegression;
import com.wildplot.android.rendering.*;
import com.wildplot.android.rendering.graphics.wrapper.BufferedImage;
import com.wildplot.android.rendering.graphics.wrapper.Color;
import com.wildplot.android.rendering.interfaces.*;



import android.app.Application;

public class GlobalDataUnified extends Application {

    private ReentrantLock mConfigLock = new ReentrantLock();

    private boolean kdeIsActivated = false;

    public enum Kernel { Gaussian, Uniform, Cauchy, Cosine, Epanechnikov, Picard, Quartic, Triangular, Tricube, Triweight  }
    public enum TouchPointType {points, linespoints, spline}

    private HashMap<String, TopLevelParser> parserRegister = new HashMap<String, TopLevelParser>();

    private Kernel kernel = Kernel.Gaussian;


    private boolean hasLinearRegression = false;
    private double lambda;
    private int m;

    //Assignment relevant Variables
    private double originX = 0.0;
    private double originY = 0.0;
    private double widthX = 1.0;
    private double widthY = 1.0;
    private int mX = 4;
    private int mY = 4;
	private boolean isLogX = false;
	
	private boolean isLogY = false;
	
	private boolean hasGrid = false;
	
	private int yTicPixelDistance = 75;
	private int xTicPixelDistance = 75;
	
	private int yMinorTicPixelDistance = 20;
	private int xMinorTicPixelDistance = 20;
	
	private Vector<Double> xPointVector = new Vector<Double>();
	private Vector<Double> yPointVector = new Vector<Double>();
	private double[][] touchPoints;
	private boolean arrayHasChanged = true;
	
	private Color touchPointColor =  Color.RED;

	private float lineThickness = 2;
	
	private TouchPointType touchPointType = TouchPointType.points;
	
	private boolean updated 								= false;
	private Vector<Function2D> func2DVector 				= new Vector<Function2D>();
    private Vector<String> funcExpressionVector			    = new Vector<String>();
    private String funcExpression3D = null;
	private Vector<double[][]> linesPointVector 			= new Vector<double[][]>();
	private Vector<double[][]> linesVector 					= new Vector<double[][]>();
	private HashMap<Object, String> NameList 				= new HashMap<Object, String>();
	private HashMap<Object, Color> colorDef 				= new HashMap<Object, Color>();
	private Vector<double[][]> pointVector 					= new Vector<double[][]>();
	private HashMap<double[][], Boolean> isSpline			= new HashMap<double[][], Boolean>();
	
	private final Color[] gradientColors = {
			Color.RED, Color.GREEN, Color.BLUE, Color.MAGENTA, Color.CYAN, Color.DARK_GRAY, Color.LIGHT_GRAY, Color.YELLOW
		};
	private int colorCnt = 1;
	
	
	private double xstart = -10;
	private double xend = 10;
	private double ystart = -10;
	private double yend = 10;
	
	private Vector<Drawable> paintables = new Vector<Drawable>();
	private BufferedImage plotImage = null;
	private AdvancedPlotSheet plotSheet = new AdvancedPlotSheet(xstart, xend, ystart, yend, plotImage);
	private boolean hasFrame = false; 
	
	
	private XAxis xaxis = new XAxis(plotSheet, 0, 50, 25);
	private YAxis yaxis = new YAxis(plotSheet, 0, 50, 25);
	private boolean isAxisOnFrame= false;
	
	private FunctionParser parser = new FunctionParser();
	private ArrayList<String> functionNames = new ArrayList<String>();
	private boolean plotCommandIssued = false;

    private int frameBorderPixelSize = 80;
    private double func3DScaleOrder = 0.9;
    private int colorCount = 150;

	
	//this probably brings a lot of errors if new stuff is unregarded in this method
	public void reset(){
        mConfigLock.lock();
		xstart = -10;
		xend = 10;
		ystart = -10;
		yend = 10;
		isLogX = false;
		
		isLogY = false;
		
		hasGrid = false;
		
		yTicPixelDistance = 75;
		xTicPixelDistance = 75;
		
		yMinorTicPixelDistance = 20;
		xMinorTicPixelDistance = 20;
		
		xPointVector.removeAllElements();
		yPointVector.removeAllElements();

		arrayHasChanged = true;
		
		lineThickness = 0.0f;
		
		touchPointType = TouchPointType.points;
		
		updated 				= true;
		func2DVector.removeAllElements();
		linesPointVector.removeAllElements();
		linesVector.removeAllElements();
		NameList.clear();
		colorDef.clear();
		pointVector.removeAllElements();
		isSpline.clear();
        funcExpressionVector.removeAllElements();
        funcExpression3D = null;
		colorCnt = 1;
		

		
		paintables.removeAllElements();
		
		plotSheet = new AdvancedPlotSheet(xstart, xend, ystart, yend, plotImage);
		hasFrame = false; 
		
		
		xaxis = new XAxis(plotSheet, 0, 50, 25);
		yaxis = new YAxis(plotSheet, 0, 50, 25);
		isAxisOnFrame= false;
		
		//parser = new FunctionParser();
		functionNames.clear();
		plotCommandIssued = false;
        kdeIsActivated = false;
        mConfigLock.unlock();
	}
	
	
	/**
	 * add a function from the parser to the plot
	 * @param functionName name of the function needed for function parser
	 * @return true if everything went OK, else false
	 */
	public void plot(String functionName) {
		FunctionParserWrapper func = new FunctionParserWrapper(parser, functionName);
		plot(func, functionName+"(x)");
	}
	
	/**
	 * add a direct Function2D object to the plot
	 * @param func the function that should be plotted
	 * @param name name of function for legend
	 * @return true if everything went OK, else false
	 */
	public void plot(Function2D func, String name) {
		
		//only set the limits, if they are not set by user directly because these 
		//bounds given here are(or will be) calculated by the program parser

        mConfigLock.lock();
		//TODO: abrastern vorher (Dopplungen)
		func2DVector.add(func);
		NameList.put(func, name);
		this.colorDef.put(func, gradientColors[colorCnt++%(gradientColors.length)]);
		this.updated = true;
        mConfigLock.unlock();
	}

    public void plotWithNewParser(String expression){
        mConfigLock.lock();
        this.colorDef.put(expression, gradientColors[colorCnt++%(gradientColors.length)]);
        this.funcExpressionVector.add(expression);
        this.updated = true;
        mConfigLock.unlock();
    }

    public void splotWithNewParser(String expression){
        mConfigLock.lock();
        this.funcExpression3D = expression;
        this.updated = true;
        mConfigLock.unlock();
    }
	
	public void tablePlot(double[][] points, String name, boolean isSpline) {
        mConfigLock.lock();
		this.pointVector.add(points);
		NameList.put(points, name);
		this.isSpline.put(points, isSpline);
		this.colorDef.put(points, gradientColors[colorCnt++%(gradientColors.length)]);
		this.updated = true;
        mConfigLock.unlock();
	}
	
	/**
	 * draw points and connect them with lines using data from array
	 * @param points array with data points
	 * @param name for legend (not yet implemented)
	 */
	public void linesPoints(double[][] points, String name) {
        mConfigLock.lock();
		this.linesPointVector.add(points);
		NameList.put(points, name);
		this.colorDef.put(points, gradientColors[colorCnt++%(gradientColors.length)]);
		this.updated = true;
        mConfigLock.unlock();
	}
	
	/**
	 * draw points and connect them with lines using data from array
	 * @param points array with data points
	 * @param name for legend (not yet implemented)
	 */
	public void lines(double[][] points, String name) {
        mConfigLock.lock();
		this.linesVector.add(points);
		NameList.put(points, name);
		this.colorDef.put(points, gradientColors[colorCnt++%(gradientColors.length)]);
		this.updated = true;
        mConfigLock.unlock();
	}
	
	public FunctionParser getParser() {
		return parser;
	}
	public void setParser(FunctionParser parser) {
		this.parser = parser;
	}
	public ArrayList<String> getFunctionNames() {
		return functionNames;
	}
	public void setFunctionNames(ArrayList<String> functionNames) {
		this.functionNames = functionNames;
	}
	public void addFunctionName(String name) {
		this.functionNames.add(name);
	}
	
	
	public boolean isPlotCommandIssued() {
		return plotCommandIssued;
	}
	public void setPlotCommandIssued(boolean plotCommandIssued) {
		this.plotCommandIssued = plotCommandIssued;
	}
	
	public void setXrange(double xstart, double xend) {
        mConfigLock.lock();

        this.xstart = (xstart<=xend)?  xstart :xend;
        this.xend = (xstart<=xend)?  xend :xstart;
		this.updated = true;
        mConfigLock.unlock();
	}
	
	public void setYrange(double ystart, double yend) {
        mConfigLock.lock();
        this.ystart = (ystart<=yend)?  ystart :yend;
        this.yend = (ystart<=yend)?  yend :ystart;
		this.updated = true;
        mConfigLock.unlock();
	}
	public AdvancedPlotSheet getPlotSheet(BufferedImage oldPlotImage) {
        mConfigLock.lock();
        updatePoints();
		this.plotSheet = new AdvancedPlotSheet(xstart, xend, ystart, yend, oldPlotImage);
		xaxis = new XAxis(plotSheet, 0, xTicPixelDistance, xMinorTicPixelDistance);
		yaxis = new YAxis(plotSheet, 0, yTicPixelDistance, yMinorTicPixelDistance);
		
		if(this.isLogX)
			this.plotSheet.setLogX();
		if(this.isLogY){
			this.plotSheet.setLogY();
			this.yaxis.setLog();
		}
		if(hasFrame){
			this.plotSheet.setFrameThickness(frameBorderPixelSize);
			if(this.isAxisOnFrame) {
				xaxis.setOnFrame();
				yaxis.setOnFrame();
			} else {
				xaxis.unsetOnFrame();
				yaxis.unsetOnFrame();
			}
		}
		
		if(this.hasGrid){
			YGrid yGrid;

			yGrid = new YGrid(plotSheet, 0,xTicPixelDistance);

			XGrid xGrid;

			xGrid = new XGrid(plotSheet, 0, yTicPixelDistance);

			plotSheet.addDrawable(yGrid);
			plotSheet.addDrawable(xGrid);
		}
		
		
		for(double[][] points:this.linesVector){
			Color thisColor = colorDef.get(points);
			Lines lines = new Lines(plotSheet, points, thisColor);
			plotSheet.addDrawable(lines);
		}
		for(double[][] points:this.linesPointVector){
		    Color thisColor = colorDef.get(points);
			LinesPoints linesPoints = new LinesPoints(plotSheet, points, thisColor);
			plotSheet.addDrawable(linesPoints);
		}
		for(Function2D func:func2DVector){
			FunctionDrawer functionDrawer = new FunctionDrawer(func, plotSheet, colorDef.get(func));
			functionDrawer.setSize(lineThickness);
			plotSheet.addDrawable(functionDrawer);
		}

        for(String expression: funcExpressionVector){
            TopLevelParser func = new TopLevelParser(expression, parserRegister);
            parserRegister.put(func.getFuncName(), func);
            System.err.println(expression);
            System.err.println(func.getFuncName() + "=" + func.f(1.0));

            FunctionDrawer functionDrawer = new FunctionDrawer(func, plotSheet, colorDef.get(expression));
            functionDrawer.setSize(lineThickness);
            plotSheet.addDrawable(functionDrawer);
        }
		
		
		if(this.xPointVector.size() > 0){
            Log.i("WildPlot::GlobalDataUnified", "Plotting points on sheet. TouchPointType: " + touchPointType);
            //draw hand drawn points
			switch(touchPointType) {
			case  points: PointDrawer2D pointDrawer = new PointDrawer2D(plotSheet, this.touchPoints, touchPointColor);
			plotSheet.addDrawable(pointDrawer); break;
			case linespoints: LinesPoints linesPoints = new LinesPoints(plotSheet, this.touchPoints, touchPointColor);
			plotSheet.addDrawable(linesPoints); break;
			case spline:
				System.err.println("spline processing!");
				for(int i = 0; i< touchPoints[0].length; i++)
				    System.err.println("sp: " + touchPoints[0][i] + " : " + touchPoints[1][i]);
				SplineInterpolation interpol = new SplineInterpolation(this.touchPoints[0], this.touchPoints[1]);
				double leftLimit = this.touchPoints[0][0];
				double rightLimit = this.touchPoints[0][0];
				for(int i = 0; i< this.touchPoints[0].length;i++){
					if(leftLimit > this.touchPoints[0][i]){
						leftLimit = this.touchPoints[0][i];
					}
					if(rightLimit < this.touchPoints[0][i]){
						rightLimit = this.touchPoints[0][i];
					}
					
				}
				System.err.println("spline limits: left: " + leftLimit + "right: " + rightLimit);
				System.err.println("spline Test: " + interpol.f(0));
				FunctionDrawer functionDrawer = new FunctionDrawer(interpol, plotSheet, touchPointColor, leftLimit, rightLimit);
				functionDrawer.setSize(lineThickness);
				plotSheet.addDrawable(functionDrawer); break;
			}
				
			
		}
		
		for(double[][] points:pointVector){
			if(this.isSpline.get(points)) {
				SplineInterpolation interpol = new SplineInterpolation(points[0], points[1]);
				double leftLimit = points[0][0];
				double rightLimit = points[0][0];
				for(int i = 0; i< points[0].length;i++){
					if(leftLimit > points[0][i]){
						leftLimit = points[0][i];
					}
					if(rightLimit < points[0][i]){
						rightLimit = points[0][i];
					}
					
				}
				
				FunctionDrawer functionDrawer = new FunctionDrawer(interpol, plotSheet, colorDef.get(points), leftLimit, rightLimit);
				functionDrawer.setSize(lineThickness);
				plotSheet.addDrawable(functionDrawer);
				
			}else{
				PointDrawer2D pointDrawer = new PointDrawer2D(plotSheet, points, colorDef.get(points));
				plotSheet.addDrawable(pointDrawer);
				}
		}

        if(funcExpression3D != null) {
            System.out.println(funcExpression3D + "!!!!!!!!!");
            //set good parameters to use relief better
            xaxis.setOnFrame();
            yaxis.setOnFrame();
            plotSheet.setFrameThickness(frameBorderPixelSize); //to be able to show legend
            TopLevelParser func3D = new TopLevelParser(funcExpression3D, this.parserRegister);
            parserRegister.put(func3D.getFuncName(), func3D);

            ReliefDrawer reliefDrawer = new ReliefDrawer(func3DScaleOrder, colorCount, func3D, plotSheet, true);
            reliefDrawer.setThreadCnt(3);
            reliefDrawer.setPixelSkip(3);
            plotSheet.addDrawable(reliefDrawer);
            plotSheet.addDrawable(reliefDrawer.getLegend());
        }

        double[][] pointsOfAssignment = touchPoints;
        if (kdeIsActivated && pointsOfAssignment != null && pointsOfAssignment[0].length > 0 && funcExpression3D == null) {
            XAxisHistoGram histogramX = new XAxisHistoGram(plotSheet, pointsOfAssignment, this.originX,
                    this.widthX, Color.red);
            YAxisHistoGram histogramY = new YAxisHistoGram(plotSheet, pointsOfAssignment, this.originY,
                    this.widthY, Color.red);
            histogramX.setFilling(true);
            histogramY.setFilling(true);
            histogramX.setFillColor(new Color(0f, 1f, 0f, 0.5f));
            histogramY.setFillColor(new Color(0f, 1f, 0f, 0.5f));
            Function2D kernelFunc;
            switch (kernel) {
                case Uniform:
                    kernelFunc = new UniformKernel();
                    break;
                case Cauchy:
                    kernelFunc = new CauchyKernel();
                    break;
                case Cosine:
                    kernelFunc = new CosineKernel();
                    break;
                case Epanechnikov:
                    kernelFunc = new EpanechnikovKernel();
                    break;
                case Picard:
                    kernelFunc = new PicardKernel();
                    break;
                case Quartic:
                    kernelFunc = new QuarticKernel();
                    break;
                case Triangular:
                    kernelFunc = new TriangularKernel();
                    break;
                case Tricube:
                    kernelFunc = new TricubeKernel();
                    break;
                case Triweight:
                    kernelFunc = new TriweightKernel();
                    break;
                default:
                case Gaussian:
                    kernelFunc = new GaussianKernel();

            }
            Density2D density2D = new Density2D(pointsOfAssignment, this.widthX, this.widthY, kernelFunc);
            ReliefDrawer reliefDrawer = new ReliefDrawer(func3DScaleOrder, colorCount, density2D, plotSheet, true);
            reliefDrawer.setThreadCnt(2);
            PointDrawer2D pointDrawer = new PointDrawer2D(plotSheet, pointsOfAssignment, Color.red.darker());
            KDE kde = new KDE(pointsOfAssignment[0], this.widthX, kernelFunc);
            FunctionDrawer KDEfunctionX = new FunctionDrawer(kde, plotSheet, new Color(255, 0, 0));
            KDE kdeY = new KDE(pointsOfAssignment[1], this.widthY, kernelFunc);
            FunctionDrawer_y KDEfunctionY = new FunctionDrawer_y(kdeY, plotSheet, new Color(255, 0, 0));
            ASH ashX = new ASH(this.originX, mX, this.widthX, pointsOfAssignment[0]);
            FunctionDrawer ASHFunctionX = new FunctionDrawer(ashX, plotSheet, new Color(0, 0, 255));
            ASH ashY = new ASH(this.originX, mY, this.widthY, pointsOfAssignment[1]);
            FunctionDrawer_y ASHFunctionY = new FunctionDrawer_y(ashY, plotSheet, new Color(0, 0, 255));
            histogramX.setOnFrame();
            histogramY.setOnFrame();
            KDEfunctionX.setOnFrame();
            KDEfunctionY.setOnFrame();
            ASHFunctionX.setOnFrame();
            ASHFunctionY.setOnFrame();
            histogramX.setAutoscale(1000);
            histogramY.setAutoscale(1000);
            KDEfunctionX.setAutoscale(1000);
            KDEfunctionY.setAutoscale(1000);
            ASHFunctionX.setAutoscale(1000);
            ASHFunctionY.setAutoscale(1000);
            double maxX = histogramX.getMaxValue();
            double tmpMax = KDEfunctionX.getMaxValue(1000);
            if (maxX < tmpMax) {
                maxX = tmpMax;
            }
            tmpMax = ASHFunctionX.getMaxValue(1000);
            if (maxX < tmpMax) {
                maxX = tmpMax;
            }
            double maxY = histogramY.getMaxValue();
            tmpMax = KDEfunctionY.getMaxValue(1000);
            if (maxY < tmpMax) {
                maxY = tmpMax;
            }
            tmpMax = ASHFunctionY.getMaxValue(1000);
            if (maxY < tmpMax) {
                maxY = tmpMax;
            }
            double extraSpaceFactor = 0.1;
            histogramX.setExtraScaleFactor(1.0 / (maxX + extraSpaceFactor * maxX));
            histogramY.setExtraScaleFactor(1.0 / (maxY + extraSpaceFactor * maxY));
            KDEfunctionX.setExtraScaleFactor(1.0 / (maxX + extraSpaceFactor * maxX));
            KDEfunctionY.setExtraScaleFactor(1.0 / (maxY + extraSpaceFactor * maxY));
            ASHFunctionX.setExtraScaleFactor(1.0 / (maxX + extraSpaceFactor * maxX));
            ASHFunctionY.setExtraScaleFactor(1.0 / (maxY + extraSpaceFactor * maxY));
            plotSheet.addDrawable(reliefDrawer);
            plotSheet.addDrawable(reliefDrawer.getLegend());
            //			plotSheet.addDrawable(yGrid);
            //			plotSheet.addDrawable(xGrid);
            plotSheet.addDrawable(histogramX);
            plotSheet.addDrawable(histogramY);
            plotSheet.addDrawable(KDEfunctionX);
            plotSheet.addDrawable(KDEfunctionY);
            plotSheet.addDrawable(ASHFunctionX);
            plotSheet.addDrawable(ASHFunctionY);
            plotSheet.addDrawable(pointDrawer);
            if (this.hasLinearRegression) {
                LinearRegression linearRegression = new LinearRegression(pointsOfAssignment, m, lambda);
                FunctionDrawer linearRegressionFuncDraw = new FunctionDrawer(linearRegression, plotSheet, Color.cyan);
                linearRegressionFuncDraw.setSize(2.0f);
                plotSheet.addDrawable(linearRegressionFuncDraw);
            }
        }
		plotSheet.addDrawable(xaxis);
		plotSheet.addDrawable(yaxis);
		
		updated = false;
        mConfigLock.unlock();
		return plotSheet;
	}

    //no lock in this one, calling method must do the lock!
    private void updatePoints(){
        if(arrayHasChanged) {
            this.touchPoints = new double[2][xPointVector.size()];

            for(int i= 0; i< xPointVector.size();i++){
                this.touchPoints[0][i] = xPointVector.get(i);
                this.touchPoints[1][i] = yPointVector.get(i);
            }
            arrayHasChanged = false;
        }
    }

	/**
	 * activate grid lines on plot
	 */
	public void setGrid() {
        mConfigLock.lock();
		this.hasGrid = true;
		this.updated = true;
        mConfigLock.unlock();
	}
	
	/**
	 * deactivate grid lines on plot (standard behavior)
	 */
	public void unsetGrid() {
        mConfigLock.lock();
		this.hasGrid = false;
		this.updated = true;
        mConfigLock.unlock();
	}
	public void setFrame(){
        mConfigLock.lock();
		this.hasFrame = true;
		this.updated = true;
        mConfigLock.unlock();
	}
	
	public void unsetFrame(){
        mConfigLock.lock();
		this.hasFrame = false;
		this.updated = true;
        mConfigLock.unlock();
	}
	
	public void setAxisOnFrame() {
        mConfigLock.lock();
		this.isAxisOnFrame = true;
		this.updated = true;
        mConfigLock.unlock();
	}
	
	public void unsetAxisOnFrame() {
        mConfigLock.lock();
		this.isAxisOnFrame = false;
		this.updated = true;
        mConfigLock.unlock();
	}
	
	public void addPaintable(Drawable paint) {
        mConfigLock.lock();
		this.paintables.add(paint);
		this.updated = true;
        mConfigLock.unlock();
	}
	public boolean isUpdated() {
		return updated;
	}

	public double getXstart() {
		return xstart;
	}

	public double getXend() {
		return xend;
	}

	public double getYstart() {
		return ystart;
	}

	public double getYend() {
		return yend;
	}

	public boolean isHasFrame() {
		return hasFrame;
	}

	public TouchPointType getTouchPointType() {
		return touchPointType;
	}

	public void setTouchPointType(TouchPointType buttonType) {
        mConfigLock.lock();
		this.touchPointType = buttonType;
		this.updated = true;
        mConfigLock.unlock();
	}
	
	public void addPointFromScreen(double x, double y){
		mConfigLock.lock();
        this.xPointVector.add(x);
		this.yPointVector.add(y);
		this.arrayHasChanged = true;
		this.updated = true;
        mConfigLock.unlock();
	}
	
	public void setLogX() {
        mConfigLock.lock();
		this.isLogX = true;
		this.updated = true;
        mConfigLock.unlock();
	}

	public void setLogY() {
        mConfigLock.lock();
		this.isLogY = true;
		this.updated = true;
        mConfigLock.unlock();
	}
	
	public void unsetLogX() {
        mConfigLock.lock();
		this.isLogX = false;
		this.updated = true;
        mConfigLock.unlock();
	}

	public void unsetLogY() {
        mConfigLock.lock();
		this.isLogY = false;
		this.updated = true;
        mConfigLock.unlock();
	}

	public boolean isLogX() {
		return isLogX;
	}

	public boolean isLogY() {
		return isLogY;
	}

	public boolean isHasGrid() {
		return hasGrid;
	}

    public void activateKde(){
        mConfigLock.lock();
        kdeIsActivated = true;
        this.updated = true;
        mConfigLock.unlock();
    }
    public void deactivateKde(){
        mConfigLock.lock();
        kdeIsActivated = false;
        this.updated = true;
        mConfigLock.unlock();
    }

    public boolean isKdeActivated(){
        return kdeIsActivated;
    }
    public void setOriginX(double originX) {
        mConfigLock.lock();
        this.originX = originX;
        this.updated = true;
        mConfigLock.unlock();
    }

    public double getOriginX(){
        return originX;
    }

    public double getOriginY(){
        return originY;
    }


    public void setOriginY(double originY) {
        mConfigLock.lock();
        this.originY = originY;
        this.updated = true;
        mConfigLock.unlock();
    }

    public void setWidthX(double widthX) {
        mConfigLock.lock();
        this.widthX = widthX;
        this.updated = true;
        mConfigLock.unlock();
    }

    public double getWidthX(){
        return widthX;
    }

    public void setWidthY(double widthY) {
        mConfigLock.lock();
        this.widthY = widthY;
        this.updated = true;
        mConfigLock.unlock();
    }

    public double getWidthY(){
        return widthY;
    }

    public void sethX(int hX) {
        mConfigLock.lock();
        this.mX = hX;
        this.updated = true;
        mConfigLock.unlock();
    }

    public int gethX(){
        return mX;
    }

    public void sethY(int hY) {
        mConfigLock.lock();
        this.mY = hY;
        this.updated = true;
        mConfigLock.unlock();
    }
    public int gethY(){
        return mY;
    }
    public double[][] getPointsOfAssignment() {
        updatePoints();
        return touchPoints;
    }

    public void setKernel(Kernel kernel) {
        mConfigLock.lock();
        this.kernel = kernel;
        this.updated = true;
        mConfigLock.unlock();
    }

    public Kernel getKernel(){
        return kernel;
    }

    public void setLinearRegression(int m, double lambda){
        mConfigLock.lock();
        this.hasLinearRegression = true;
        this.m = m;
        this.lambda =  lambda;
        this.updated = true;
        mConfigLock.unlock();
    }
}
