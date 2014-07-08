package com.wildplot.android.rendering;

import com.wildplot.android.rendering.graphics.wrapper.Color;
import com.wildplot.android.rendering.graphics.wrapper.Graphics;
import com.wildplot.android.rendering.graphics.wrapper.Rectangle;
import com.wildplot.android.rendering.interfaces.Drawable;


public class PieChart implements Drawable {

	private double[] values;
	private double[] prozent;
	private double sum;
	private int farbbestimmung;
	PlotSheet plotSheet;

	final public static Color[] myCols = {
		new Color(255,  0,  0,180),
		new Color(0  ,255,  0,180),
		new Color(0  ,0,  255,180),
		
		new Color(255,255,  0,180),
		new Color(  0,255,255,180),
		new Color(255,  0,255,180)
		};

	public PieChart(PlotSheet plotSheet, double[] vals){
		this.plotSheet = plotSheet;
		values = vals;
		//values = new double[] {5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5};
		prozent = new double[values.length];
		for(double v:values) sum+=v;
		prozent[0]=values[0]/sum;
		for(int i=1; i<values.length; i++){
			prozent[i]=prozent[i-1]+values[i]/sum;
		}
		farbbestimmung=myCols.length;
		if((values.length-1)%(myCols.length)==0)farbbestimmung=myCols.length-1;
	}

	/*
	 * (non-Javadoc)
	 * @see rendering.Drawable#isOnFrame()
	 */
	@Override
	public boolean isOnFrame() {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see rendering.Drawable#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g){
		Rectangle field = g.getClipBounds();
		int realBorder=plotSheet.getFrameThickness() + 3;
		int diameter=Math.min(field.width, field.height)-2*realBorder;
		
		int xCenter = (int)(field.width/2.0);
		int yCenter = (int)(field.height/2.0);
		Color oldColor = g.getColor();
		
		int xMiddle = xCenter - (int)(diameter/2.0);
		int yMiddle = yCenter - (int)(diameter/2.0);
		
		int currentAngle = 0;
		int nextAngle = (int)(360.0*prozent[0]);
		int tmp = 0;
		for(int i = 1; i<prozent.length; i++) {
			g.setColor(myCols[i%farbbestimmung]);
			g.fillArc(xMiddle, yMiddle, (int)diameter, (int)(diameter), currentAngle, nextAngle - currentAngle);
			currentAngle = nextAngle;
			nextAngle = (int)(360.0*prozent[i]);
			tmp = i+1;
		}
		
		//last one does need some corrections to fill a full circle:
		g.setColor(myCols[tmp%farbbestimmung]);
		g.fillArc(xMiddle, yMiddle, diameter, diameter, currentAngle, 360 - currentAngle);
		g.setColor(Color.black);
		g.drawArc(xMiddle, yMiddle, diameter, diameter, 0, 360);
		
		//Beschriftung
		g.setColor(Color.black);
		g.drawString(""+Math.round(((prozent[0])*100)*100)/100.0+"%", (int)(xCenter+Math.cos(prozent[0]*Math.PI)*0.375*diameter)-20, (int)(yCenter-Math.sin(prozent[0]*Math.PI)*0.375*diameter));
		for(int j=1;j<prozent.length;j++)
		{
			
			g.drawString(""+Math.round((((prozent[j]-prozent[j-1]))*100)*100)/100.0+"%", (int)(xCenter+Math.cos((prozent[j-1]+(prozent[j]-prozent[j-1])*0.5)*360*Math.PI/180.0)*0.375*diameter)-20, (int)(yCenter-Math.sin((prozent[j-1]+(prozent[j]-prozent[j-1])*0.5)*360*Math.PI/180.0)*0.375*diameter));
		}
		
		g.setColor(oldColor);
	}

	@Override
	public void abortAndReset() {
		// TODO Auto-generated method stub
		
	}

    @Override
    public boolean isClusterable() {
        return true;
    }

    @Override
    public boolean isCritical() {
        return false;
    }
}
