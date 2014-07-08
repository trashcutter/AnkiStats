package com.wildplot.android;

import java.util.concurrent.locks.ReentrantLock;

import android.util.Log;
import com.wildplot.android.rendering.*;
import com.wildplot.android.rendering.graphics.wrapper.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class PlotView extends View implements Runnable
{
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private ReentrantLock finishedImageCheckLock = new ReentrantLock();
    private boolean isJustInitialized = false;
    
    private boolean finishedImageIsOnDisplay = false;
    private boolean hasGlobalDataDelivered = false;
    Rect field = new Rect();
    private Thread plotCreatorThread = null;
    private int savedPlotWidth  = 0;
    private int savedPlotHeight     = 0;
    private int unitsToScroll = 0;
    private boolean hasScrolled = false;
	Bitmap bitmap;
	BufferedImage buffImage;
	Canvas bitmapCanvas;
	GlobalDataUnified globalData = new GlobalDataUnified();
	AdvancedPlotSheet plotSheet;
	
	
	
	boolean isInitialized;
	Paint paint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
	PlotView thisView;
	
	public PlotView(Context context)
	{
		super(context);
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
	}
	public PlotView(Context context, AttributeSet attrs)
    {
        super(context,attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }
	public void setGlobalData(GlobalDataUnified globalData){
        Log.i("WildPlot::PlotView", "in setGlobalData");
	    this.globalData = globalData;
        this.plotSheet = globalData.getPlotSheet(null);
        setFocusable(true);
        setFocusableInTouchMode(true);

//      this.setOnTouchListener(this);

        paint.setColor(Color.WHITE);
        paint.setAntiAlias(false);
        paint.setStyle(Style.FILL_AND_STROKE);
        paint.setStrokeWidth(0.0f);
        
        isInitialized = false;
        
        Thread thread = new Thread(this);
        thread.start();
        this.thisView = this;
        hasGlobalDataDelivered = true;
	}

	private void init()
	{
	    plotSheet = globalData.getPlotSheet(null);
        Log.i("WildPlot::PlotView", "view: " + getHeight() +":" + getWidth());
		bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);

		bitmapCanvas = new Canvas(bitmap);
		//  bitmapCanvas.setBitmap(bitmap);

		paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		bitmapCanvas.drawPaint(paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
		
		paint.setColor(Color.BLACK);
		this.setBackgroundColor(Color.WHITE);
		bitmapCanvas.drawColor(Color.WHITE);
		
		isInitialized = true;
        isJustInitialized = true;
//		initODE();

		
	}

	
	
	
	@Override
	public void onDraw(Canvas canvas)
	{

        int cnt = 0;
        while(!hasGlobalDataDelivered){
            try {
                Thread.sleep(50);
                cnt++;
                Log.w("WildPlot::PlotView", "waited " + cnt + " cycles for global data object");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


	    canvas.drawColor(Color.WHITE);
		if (!isInitialized)
			init();
		
				
		
		Graphics g = new Graphics2D(canvas, paint);
		
        this.getDrawingRect(field);
        Rectangle rectangle = new Rectangle(field);
		g.setClip(rectangle);
		canvas.drawColor(Color.WHITE, Mode.CLEAR);
		
        BufferedImage plotImage = plotSheet.getPlotImage();
        finishedImageCheckLock.lock();
        if(plotSheet.isFinished() && !this.finishedImageIsOnDisplay)
            this.finishedImageIsOnDisplay = true;
        if(plotImage != null){
            g.drawImage(plotImage, null, 0, 0);
        }
        finishedImageCheckLock.unlock();
        
		//plotSheet.paint(g);
		//canvas.drawBitmap(bitmap, 0, 0, paint);
	}

//	public boolean onTouch(View view, MotionEvent event)
//	{
//		bitmapCanvas.drawCircle(event.getX(), event.getY(), 5, paint);
//
//
//		invalidate();
//		return true;
//	}

	@Override
	public void run() {

		
		while(true) {
		    this.getDrawingRect(field);
	        Rectangle canvasBoundarys = new Rectangle(field);
	        //System.err.println("APlotView: field: " + field.width() + " : " + field.height());
            //checkAndProcessZooming(canvasBoundarys);
            try {
                if( (field.width() != 0 && field.height() != 0) && checkAndProcessImageReconstruction(canvasBoundarys)){
                    this.postInvalidate();
                }
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                System.err.println("Plot image could not be created: internal threading error!");
                System.exit(-1);
            }

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private boolean checkAndProcessImageReconstruction(Rectangle field) throws InterruptedException{
	    
        boolean newConstructionOfPlotImageIsNecessary = (plotSheet.getPlotImage() == null && this.plotCreatorThread == null) ||  field.width != savedPlotWidth || field.height != savedPlotHeight || globalData.isUpdated() || isJustInitialized;
        isJustInitialized = false;
        
        if(newConstructionOfPlotImageIsNecessary){
            System.err.println("checkingimageReconstruction!!!");
            
            if(this.plotCreatorThread != null){
                plotSheet.abortOperation();
                plotCreatorThread.join();
                System.gc();
            }
            
            if(globalData.isUpdated()){
                plotSheet = globalData.getPlotSheet(plotSheet.getPlotImage());
            }
            //System.gc();
            plotCreatorThread = new Thread(plotSheet);
            plotSheet.setClip(field);
            plotCreatorThread.start();
            finishedImageIsOnDisplay = false;
            
            savedPlotWidth =  field.width;
            savedPlotHeight = field.height;
        }
        resetResizeEvents();
        try{
            finishedImageCheckLock.lock();
            boolean finishedImageWasDisplayed = true;
            if(!finishedImageIsOnDisplay && plotSheet.isFinished()){
                finishedImageWasDisplayed = false;
            }
            return newConstructionOfPlotImageIsNecessary || !finishedImageWasDisplayed || !plotSheet.isFinished();
        } finally{
            finishedImageCheckLock.unlock();
        }
    }
    
    
    private void resetResizeEvents(){
        this.hasScrolled = false;
        this.unitsToScroll = 0; 
    }
    

	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    int x = (int)event.getX();
	    int y = (int)event.getY();
//	    System.err.println("I am touched");
	    
	    switch (event.getAction()) {
	        case MotionEvent.ACTION_UP: 
	        	Rect field = new Rect();
	        	this.getDrawingRect(field);
	        	Rectangle rectangle = new Rectangle(field);
	        	double[] coord = this.plotSheet.toCoordinatePoint(x, y, rectangle);
	        	
	        	this.globalData.addPointFromScreen(coord[0], coord[1]);break;
	    }
	    
	    //mScaleDetector.onTouchEvent(event);
	return true;
	}
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
	    @Override
	    public boolean onScale(ScaleGestureDetector detector) {
	        
	        mScaleFactor *= detector.getScaleFactor();

	        // Don't let the object get too small or too large.
	        mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

	        invalidate();
	        return true;
	    }
	}

}