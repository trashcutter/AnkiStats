package com.wildplot.android.ankistats;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.widget.ImageView;
import com.wildplot.android.rendering.*;
import com.wildplot.android.rendering.graphics.*;
import com.wildplot.android.rendering.graphics.wrapper.BufferedImage;
import com.wildplot.android.rendering.graphics.wrapper.Color;
import com.wildplot.android.rendering.graphics.wrapper.Graphics2D;
import com.wildplot.android.rendering.graphics.wrapper.Rectangle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by mig on 06.07.2014.
 */
public class Forecast {
    private AnkiDb mAnkiDb;
    private ImageView mImageView;
    private CollectionData mCollectionData;

    private int mFrameThickness = 60;

    int mMaxCards = 0;
    int mMaxElements = 0;
    private int mType;
    private int mTitle;
    private boolean mBackwards;
    private int[] mValueLabels;
    private int[] mColors;
    private int[] mAxisTitles;
    private double[][] mSeriesList;
    private double barThickness = 0.6;

    public Forecast(AnkiDb ankiDb, ImageView imageView, CollectionData collectionData){
        mAnkiDb = ankiDb;
        mImageView = imageView;
        mCollectionData = collectionData;
    }

    public Bitmap renderChart(int type){
        calculateDue(type);
        int height = mImageView.getMeasuredHeight();
        int width = mImageView.getMeasuredWidth();

        if(height <=0 && width <= 0){
            return null;
        }

        BufferedImage bufferedFrameImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufferedFrameImage.createGraphics();
        Rectangle rect = new Rectangle(width, height);
        g.setClip(rect);
        g.setColor(Color.BLACK);
        float textSize = AnkiStatsApplication.getInstance().getmStandardTextSize()*0.75f;
        g.setFontSize(textSize);

        float FontHeigth = g.getFontMetrics().getHeight(true);
        mFrameThickness = Math.round( FontHeigth * 4.0f);
        //System.out.println("frame thickness: " + mFrameThickness);

        PlotSheet plotSheet = new PlotSheet(-0.5, mMaxElements + 0.5, 0, mMaxCards*1.1);
        plotSheet.setFrameThickness(mFrameThickness);

        //no title because of tab title
        //plotSheet.setTitle(mImageView.getResources().getString(mTitle));

        double xTics = ticsCalcX(150, rect);
        double yTics = ticsCalcY(150, rect);

        XAxis xaxis = new XAxis(plotSheet, 0, xTics, xTics/2.0);
        YAxis yaxis = new YAxis(plotSheet, 0, yTics, yTics/2.0);
        xaxis.setOnFrame();
        xaxis.setName(mImageView.getResources().getStringArray(R.array.due_x_axis_title)[mAxisTitles[0]]);
        xaxis.setIntegerNumbering(true);
        yaxis.setIntegerNumbering(true);
        yaxis.setName(mImageView.getResources().getString(mAxisTitles[1]));
        yaxis.setOnFrame();


        double[][] bars = new double[2][];
        bars[0] = mSeriesList[0];
        bars[1] = mSeriesList[1];
        BarGraph barGraph = new BarGraph(plotSheet,barThickness, bars, new Color(mImageView.getResources().getColor(mColors[0])));
        barGraph.setFilling(true);
        barGraph.setName(mImageView.getResources().getString(mValueLabels[0]));
        //barGraph.setFillColor(Color.GREEN.darker());
        barGraph.setFillColor(new Color(mImageView.getResources().getColor(mColors[0])));
        double[][] bars2 = new double[2][];
        bars2[0] = mSeriesList[0];
        bars2[1] = mSeriesList[2];
        BarGraph barGraphMature = new BarGraph(plotSheet,barThickness, bars2, new Color(mImageView.getResources().getColor(mColors[1])));
        barGraphMature.setFilling(true);
        barGraphMature.setFillColor(new Color(mImageView.getResources().getColor(mColors[1])));
        barGraphMature.setName(mImageView.getResources().getString(mValueLabels[1]));

        double[][] cumulative = Utils.createCumulative(bars);
        PlotSheet hiddenPlotSheet = new PlotSheet(-0.5, mMaxElements + 0.5, 0, cumulative[1][cumulative[1].length-1]*1.1);     //for second y-axis

        Lines lines = new Lines(hiddenPlotSheet,cumulative ,Color.black);
        lines.setSize(3f);
        lines.setName(mImageView.getResources().getString(R.string.stats_cumulative));
        lines.setShadow(5f, 3f, 3f, Color.BLACK);

        hiddenPlotSheet.setFrameThickness(mFrameThickness);
        double rightYtics = ticsCalc(150, rect,  cumulative[1][cumulative[1].length-1]*1.1);
        YAxis rightYaxis = new YAxis(hiddenPlotSheet, 0, rightYtics, rightYtics/2.0);
        rightYaxis.setIntegerNumbering(true);
        rightYaxis.setName(mImageView.getResources().getString(mAxisTitles[2]));
        rightYaxis.setOnRightSideFrame();

        int red = Color.LIGHT_GRAY.getRed();
        int green = Color.LIGHT_GRAY.getGreen();
        int blue = Color.LIGHT_GRAY.getBlue();

        Color newGridColor = new Color(red,green,blue, 222);

        XGrid xGrid = new XGrid(plotSheet, 0, 150);
        YGrid yGrid = new YGrid(plotSheet, 0, 150);

        xGrid.setColor(newGridColor);
        yGrid.setColor(newGridColor);
        plotSheet.setFontSize(textSize);

        plotSheet.addDrawable(barGraph);
        plotSheet.addDrawable(barGraphMature);
        plotSheet.addDrawable(lines);
        plotSheet.addDrawable(xaxis);
        plotSheet.addDrawable(yaxis);
        plotSheet.addDrawable(rightYaxis);
        plotSheet.addDrawable(xGrid);
        plotSheet.addDrawable(yGrid);
        plotSheet.paint(g);
        return bufferedFrameImage.getBitmap();
    }

    /**
     * Due and cumulative due
     * ***********************************************************************************************
     */
    private boolean calculateDue(int type) {
        mType = type;
        mBackwards = false;
        mTitle = R.string.stats_forecast;
        mValueLabels = new int[] { R.string.statistics_young, R.string.statistics_mature };
        mColors = new int[] { R.color.stats_young, R.color.stats_mature };
        mAxisTitles = new int[] { type, R.string.stats_cards, R.string.stats_cumulative_cards };
        int end = 0;
        int chunk = 0;
        switch (type) {
            case Utils.TYPE_MONTH:
                end = 31;
                chunk = 1;
                break;
            case Utils.TYPE_YEAR:
                end = 52;
                chunk = 7;
                break;
            case Utils.TYPE_LIFE:
                end = -1;
                chunk = 30;
                break;
        }
        String lim = "";// AND due - " + mCol.getSched().getToday() + " >= " + start; // leave this out in order to show
        // card too which were due the days before
        if (end != -1) {
            lim += " AND day <= " + end;
        }

        ArrayList<int[]> dues = new ArrayList<int[]>();
        Cursor cur = null;
        try {
            String query;
            query = "SELECT (due - " + mCollectionData.getToday() + ")/" + chunk
                    + " AS day, " // day
                    + "count(), " // all cards
                    + "sum(CASE WHEN ivl >= 21 THEN 1 ELSE 0 END) " // mature cards
                    + "FROM cards WHERE did IN " + _limitWholeOnly() + " AND queue IN (2,3)" + lim
                    + " GROUP BY day ORDER BY day";
            //TODO remove:
            System.out.println("forecast query: " + query);
            cur = mAnkiDb
                    .getDatabase()
                    .rawQuery(query, null);
            while (cur.moveToNext()) {
                dues.add(new int[] { cur.getInt(0), cur.getInt(1), cur.getInt(2) });
            }
        } finally {
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
        }
        // small adjustment for a proper chartbuilding with achartengine
        if (dues.size() == 0 || dues.get(0)[0] > 0) {
            dues.add(0, new int[] { 0, 0, 0 });
        }
        if (end == -1 && dues.size() < 2) {
            end = 31;
        }
        if (type != Utils.TYPE_LIFE && dues.get(dues.size() - 1)[0] < end) {
            dues.add(new int[] { end, 0, 0 });
        } else if (type == Utils.TYPE_LIFE && dues.size() < 2) {
            dues.add(new int[] { Math.max(12, dues.get(dues.size() - 1)[0] + 1), 0, 0 });
        }

        mSeriesList = new double[3][dues.size()];
        for (int i = 0; i < dues.size(); i++) {
            int[] data = dues.get(i);

            if(data[1] > mMaxCards)
                mMaxCards =data[1];

            mSeriesList[0][i] = data[0];
            mSeriesList[1][i] = data[1];
            mSeriesList[2][i] = data[2];
        }
        mMaxElements = dues.size()-1;
        return dues.size() > 0;
    }


    public double ticsCalcX(int pixelDistance, Rectangle field){
        double deltaRange =mMaxElements - 0;
        int ticlimit = field.width/pixelDistance;
        double tics = Math.pow(10, (int)Math.log10(deltaRange/ticlimit));
        while(2.0*(deltaRange/(tics)) <= ticlimit) {
            tics /= 2.0;
        }
        while((deltaRange/(tics))/2 >= ticlimit) {
            tics *= 2.0;
        }
        return tics;
    }

    public double ticsCalcY(int pixelDistance, Rectangle field){
        double deltaRange = mMaxCards - 0;
        int ticlimit = field.height/pixelDistance;
        double tics = Math.pow(10, (int)Math.log10(deltaRange/ticlimit));
        while(2.0*(deltaRange/(tics)) <= ticlimit) {
            tics /= 2.0;
        }
        while((deltaRange/(tics))/2 >= ticlimit) {
            tics *= 2.0;
        }
        return tics;
    }

    public double ticsCalc(int pixelDistance, Rectangle field, double deltaRange){
        int ticlimit = field.height/pixelDistance;
        double tics = Math.pow(10, (int)Math.log10(deltaRange/ticlimit));
        while(2.0*(deltaRange/(tics)) <= ticlimit) {
            tics /= 2.0;
        }
        while((deltaRange/(tics))/2 >= ticlimit) {
            tics *= 2.0;
        }
        return tics;
    }


    /**
     * Daily cutoff ************************************************************* **********************************
     * This function uses GregorianCalendar so as to be sensitive to leap years, daylight savings, etc.
     */
    private String _limitWholeOnly() {

        ArrayList<Long> ids = new ArrayList<Long>();
        for (JSONObject d : mCollectionData.allDecks()) {
            try {
                ids.add(d.getLong("id"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return Utils.ids2str(Utils.arrayList2array(ids));

    }



}
