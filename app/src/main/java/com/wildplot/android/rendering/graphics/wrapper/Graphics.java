/**
 * 
 */
package com.wildplot.android.rendering.graphics.wrapper;

import android.graphics.*;
import android.graphics.PorterDuff.Mode;
import android.graphics.Paint.Style;

/**
 * Wrapper of swing/awt graphics class for android use
 * @author Michael Goldbach
 *
 */
public class Graphics {
    private Canvas canvas;
    private Paint paint;
    
    
    
    public Graphics(Canvas canvas, Paint paint) {
        super();
        this.canvas = canvas;
        this.paint = paint;
    }

    public void drawLine(int x1, int y1, int x2, int y2){
        Style oldStyle = paint.getStyle();
        paint.setStyle(Style.STROKE);
        canvas.drawLine(x1, y1, x2, y2, paint);
        paint.setStyle(oldStyle);
    }
    
    public void drawRect(int x, int y, int width, int height){
        Style oldStyle = paint.getStyle();
        paint.setStyle(Style.STROKE);
        canvas.drawRect(x, y, x+width, y+height, paint);
        paint.setStyle(oldStyle);
    }
    
    public void fillRect(int x, int y, int width, int height){
        Style oldStyle = paint.getStyle();
        paint.setStyle(Style.FILL_AND_STROKE);
        canvas.drawRect(x, y, x+width, y+height, paint);
        paint.setStyle(oldStyle);
    }

    public Stroke getStroke(){
        return new Stroke(paint.getStrokeWidth());
    }
    
    public void setStroke(Stroke stroke){
        paint.setStrokeWidth(stroke.getStrokeSize());
    }
    
    public Rectangle getClipBounds(){
        return new Rectangle(canvas.getClipBounds());
    }
    
    public void setClip(Rectangle rectangle){
        //seems to be not necessary
    }
    
    public Color getColor(){
        return new Color(paint.getColor());
    }
    
    public void setColor(Color color){
        paint.setColor(color.getColorValue());
    }
    
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle){
        Style oldStyle = paint.getStyle();
        paint.setStyle(Style.STROKE);
        canvas.drawCircle(x, y, width/2.0f, paint);
        paint.setStyle(oldStyle);
    }
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle){
        Style oldStyle = paint.getStyle();
        paint.setStyle(Style.FILL_AND_STROKE);
        canvas.drawCircle(x, y, width/2.0f, paint);
        paint.setStyle(oldStyle);
    }
    
    public void drawImage(BufferedImage image, String tmp, int x, int y){
        //System.err.println("drawImage: " + image.getBitmap().getWidth() + " : "+ image.getBitmap().getHeight());
        Xfermode mode  = paint.getXfermode();
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
        //canvas.drawBitmap(image.getBitmap(), x, y, paint);
        canvas.drawBitmap(image.getBitmap(), canvas.getClipBounds(), canvas.getClipBounds(), paint);
        paint.setXfermode(mode);
    }
    
    public void drawString(String text, int x, int y){
        Style oldStyle = paint.getStyle();
        paint.setStyle(Style.FILL);
        canvas.drawText(text, x, y, paint);
        paint.setStyle(oldStyle);
        
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public Paint getPaint() {
        return paint;
    }

    public Object getFont() {
        // TODO Auto-generated method stub
        return null;
    }

    public FontMetrics getFontMetrics(Object font) {
        return new FontMetrics(this);
    }
    
    public FontMetrics getFontMetrics() {
        return new FontMetrics(this);
    }
    
    public void dispose(){
        //TODO: search if there is something to do with it
    }

    public int save(){
        return canvas.save();
    }

    public void restore(){
        canvas.restore();
    }

    public void rotate(float degree, float x, float y){
        canvas.rotate(degree, x, y);
    }

    public float getFontSize(){
        return paint.getTextSize();
    }
    public void setFontSize(float size){
        paint.setTextSize(size);
    }

    public void setTypeface(Typeface typeface){
        paint.setTypeface(typeface);
    }
    public Typeface getTypeface(){
        return paint.getTypeface();
    }

    public void setShadow(float radius, float dx, float dy, Color color){
        int colorVal = color.getColorValue();
        paint.setShadowLayer(radius, dx, dy, colorVal);
    }
    public void unsetShadow(){
        paint.clearShadowLayer();
    }
    
}
