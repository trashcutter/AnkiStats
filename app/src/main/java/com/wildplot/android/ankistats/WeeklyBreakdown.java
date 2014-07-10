/****************************************************************************************
 * Copyright (c) 2011 Norbert Nagold <norbert.nagold@gmail.com>                         *
 * Copyright (c) 2014 Michael Goldbach <trashcutter@googlemail.com>                     *
 *                                                                                      *
 * This program is free software; you can redistribute it and/or modify it under        *
 * the terms of the GNU General Public License as published by the Free Software        *
 * Foundation; either version 3 of the License, or (at your option) any later           *
 * version.                                                                             *
 *                                                                                      *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY      *
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A      *
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.             *
 *                                                                                      *
 * You should have received a copy of the GNU General Public License along with         *
 * this program.  If not, see <http://www.gnu.org/licenses/>.                           *
 ****************************************************************************************/
package com.wildplot.android.ankistats;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import com.wildplot.android.rendering.*;
import com.wildplot.android.rendering.graphics.wrapper.BufferedImage;
import com.wildplot.android.rendering.graphics.wrapper.Color;
import com.wildplot.android.rendering.graphics.wrapper.Graphics2D;
import com.wildplot.android.rendering.graphics.wrapper.Rectangle;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by mig on 06.07.2014.
 */
public class WeeklyBreakdown {
    private AnkiDb mAnkiDb;
    private ImageView mImageView;
    private CollectionData mCollectionData;

    private int mFrameThickness = 60;
    private int targetPixelDistanceBetweenTics = 150;

    private boolean mFoundLearnCards = false;
    private boolean mFoundCramCards = false;

    private int mMaxCards = 0;
    private int mMaxElements = 0;
    private int mType;
    private int mTitle;
    private boolean mBackwards;
    private int[] mValueLabels;
    private int[] mColors;
    private int[] mAxisTitles;
    private double[][] mSeriesList;
    private boolean mFoundRelearnCards;
    private double mBarThickness = 0.8;
    private String mAverage;
    private String mLongest;
    private double[][] mCumulative;
    private double mPeak;
    private double mMcount;

    public WeeklyBreakdown(AnkiDb ankiDb, ImageView imageView, CollectionData collectionData){
        mAnkiDb = ankiDb;
        mImageView = imageView;
        mCollectionData = collectionData;
    }

    public Bitmap renderChart(int type){
        calculateBreakdown(type);
        int height = mImageView.getMeasuredHeight();
        int width = mImageView.getMeasuredWidth();

        if(height <=0 || width <= 0){
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

        PlotSheet plotSheet = new PlotSheet(mSeriesList[0][0]-0.5, mSeriesList[0][mSeriesList[0].length-1] + 0.5, 0, mPeak*1.03);
        plotSheet.setFrameThickness(mFrameThickness);

        //no title because of tab title
        //plotSheet.setTitle(mImageView.getResources().getString(mTitle));

        double xTics = ticsCalcX(targetPixelDistanceBetweenTics, rect);
        double yTics = ticsCalcY(targetPixelDistanceBetweenTics, rect);

        XAxis xaxis = new XAxis(plotSheet, 0, xTics, xTics/2.0);
        YAxis yaxis = new YAxis(plotSheet, 0, yTics, yTics/2.0);
        double[] timePositions = {0, 1, 2, 3, 4, 5, 6};
        xaxis.setExplicitTics(timePositions, mImageView.getResources().getStringArray(R.array.stats_week_days));
        xaxis.setOnFrame();
        xaxis.setName(mImageView.getResources().getString(mAxisTitles[0]));
        xaxis.setIntegerNumbering(true);
        yaxis.setIntegerNumbering(true);
        yaxis.setName(mImageView.getResources().getString(mAxisTitles[1]));
        yaxis.setOnFrame();

        //double maxCumulative = mCumulative[1][mCumulative[1].length-1];
        PlotSheet hiddenPlotSheet = new PlotSheet(mSeriesList[0][0]-0.5, mSeriesList[0][mSeriesList[0].length-1] + 0.5, 0, mMcount*1.03);     //for second y-axis
        hiddenPlotSheet.setFrameThickness(mFrameThickness);
        BarGraph[] barGraphs = new BarGraph[2];
        for(int i = 1; i< 3; i++){
            double[][] bars = new double[2][];
            bars[0] = mSeriesList[0];
            bars[1] = mSeriesList[i];

            PlotSheet usedPlotSheet = (i==2)?hiddenPlotSheet : plotSheet;
            double barThickness = (i==1)? mBarThickness : 0.2;
            barGraphs[i-1] = new BarGraph(usedPlotSheet, barThickness, bars, new Color(mImageView.getResources().getColor(mColors[i-1])));
            barGraphs[i-1].setFilling(true);
            barGraphs[i-1].setName(mImageView.getResources().getString(mValueLabels[i-1]));
            //barGraph.setFillColor(Color.GREEN.darker());
            barGraphs[i-1].setFillColor(new Color(mImageView.getResources().getColor(mColors[i-1])));
        }

        double rightYtics = ticsCalc(targetPixelDistanceBetweenTics, rect,  mMcount);
        YAxis rightYaxis = new YAxis(hiddenPlotSheet, 0, rightYtics, rightYtics/2.0);
        rightYaxis.setIntegerNumbering(true);
        rightYaxis.setName(mImageView.getResources().getString(mAxisTitles[2]));
        rightYaxis.setOnRightSideFrame();

        int red = Color.LIGHT_GRAY.getRed();
        int green = Color.LIGHT_GRAY.getGreen();
        int blue = Color.LIGHT_GRAY.getBlue();

        Color newGridColor = new Color(red,green,blue, 222);

        XGrid xGrid = new XGrid(plotSheet, 0, targetPixelDistanceBetweenTics);
        YGrid yGrid = new YGrid(plotSheet, 0, targetPixelDistanceBetweenTics);

        xGrid.setColor(newGridColor);
        yGrid.setColor(newGridColor);
        yGrid.setExplicitTics(timePositions);
        plotSheet.setFontSize(textSize);

        for(BarGraph barGraph : barGraphs){
            plotSheet.addDrawable(barGraph);
        }

        plotSheet.addDrawable(xaxis);
        plotSheet.addDrawable(yaxis);
        plotSheet.addDrawable(rightYaxis);
        plotSheet.addDrawable(xGrid);
        plotSheet.addDrawable(yGrid);
        plotSheet.paint(g);
        Bitmap bitmap = bufferedFrameImage.getBitmap();
        bitmap.prepareToDraw();
        return bitmap;
    }

    public boolean calculateBreakdown(int type) {
        mTitle = R.string.stats_weekly_breakdown;
        mAxisTitles = new int[] { R.string.stats_time_of_day, R.string.stats_percentage_correct, R.string.stats_reviews };

        mValueLabels = new int[] { R.string.stats_percentage_correct, R.string.stats_answers};
        mColors = new int[] { R.color.stats_counts, R.color.stats_hours};

        mType = type;
        String lim = _revlogLimitWholeOnly().replaceAll("[\\[\\]]", "");

        if (lim.length() > 0) {
            lim = " and " + lim;
        }

        Calendar sd = GregorianCalendar.getInstance();
        sd.setTimeInMillis(mCollectionData.getCrt() * 1000);
        Calendar cal = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();



/* date formatter in local timezone */
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        sdf.setTimeZone(tz);

/* print your timestamp and double check it's the date you expect */

        String localTime = sdf.format(new Date(mCollectionData.getCrt() * 1000)); // I assume your timestamp is in seconds and you're converting to milliseconds?

        int pd = _periodDays();
        if(pd > 0){
            lim += " and id > "+ ((mCollectionData.getDayCutoff()-(86400*pd))*1000);
        }

        int hourOfDay =sd.get(GregorianCalendar.HOUR_OF_DAY);
        long cutoff = mCollectionData.getDayCutoff();
        long cut = cutoff  - sd.get(Calendar.HOUR_OF_DAY)*3600;



        ArrayList<double[]> list = new ArrayList<double[]>();
        Cursor cur = null;
        String query = "SELECT strftime('%w',datetime( cast(id/ 1000  -" + sd.get(Calendar.HOUR_OF_DAY)*3600 +
                " as int), 'unixepoch')) as wd, " +
                "sum(case when ease = 1 then 0 else 1 end) / " +
                "cast(count() as float) * 100, " +
                "count() " +
                "from revlog " +
                "where type in (0,1,2) " + lim +" " +
                "group by wd " +
                "order by wd";
        Log.d(AnkiStatsApplication.TAG, sd.get(Calendar.HOUR_OF_DAY) + " : " +cutoff + " weekly breakdown query: " + query);
        try {
            cur = mAnkiDb
                    .getDatabase()
                    .rawQuery(query, null);
            while (cur.moveToNext()) {
                list.add(new double[] { cur.getDouble(0), cur.getDouble(1), cur.getDouble(2) });
            }


        } finally {
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
        }

        //TODO adjust for breakdown, for now only copied from intervals
        // small adjustment for a proper chartbuilding with achartengine
//        if (list.size() == 0 || list.get(0)[0] > 0) {
//            list.add(0, new double[] { 0, 0, 0 });
//        }
//        if (num == -1 && list.size() < 2) {
//            num = 31;
//        }
//        if (type != Utils.TYPE_LIFE && list.get(list.size() - 1)[0] < num) {
//            list.add(new double[] { num, 0, 0 });
//        } else if (type == Utils.TYPE_LIFE && list.size() < 2) {
//            list.add(new double[] { Math.max(12, list.get(list.size() - 1)[0] + 1), 0, 0 });
//        }


        mSeriesList = new double[4][list.size()];
        mPeak = 0.0;
        mMcount = 0.0;
        double minHour = Double.MAX_VALUE;
        double maxHour = 0;
        for (int i = 0; i < list.size(); i++) {
            double[] data = list.get(i);
            int hour = (int)data[0];

            //double hour = data[0];
            if(hour < minHour)
                minHour = hour;

            if(hour > maxHour)
                maxHour = hour;

            double pct = data[1];
            if (pct > mPeak)
                mPeak = pct;

            mSeriesList[0][i] = hour;
            mSeriesList[1][i] = pct;
            mSeriesList[2][i] = data[2];
            if(i==0){
                mSeriesList[3][i] = pct;
            } else {
                double prev = mSeriesList[3][i-1];
                double diff = pct-prev;
                diff /= 3.0;
                diff = Math.round(diff*10.0)/10.0;

                mSeriesList[3][i] = prev+diff;
            }

            if (data[2] > mMcount)
                mMcount = data[2];
            if(mSeriesList[1][i] > mMaxCards)
                mMaxCards = (int) mSeriesList[1][i];
        }

        mMaxElements = (int)(maxHour -minHour);
        return list.size() > 0;
    }

    private int _periodDays(){
        if(mType == Utils.TYPE_MONTH){
            return 30;
        } else if(mType == Utils.TYPE_YEAR){
            return 365;
        } else
            return -1;
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
    private String _revlogLimitWholeOnly() {
        return "";
    }

}
