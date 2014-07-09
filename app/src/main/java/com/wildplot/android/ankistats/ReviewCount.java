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
import android.widget.ImageView;
import com.wildplot.android.rendering.*;
import com.wildplot.android.rendering.graphics.wrapper.BufferedImage;
import com.wildplot.android.rendering.graphics.wrapper.Color;
import com.wildplot.android.rendering.graphics.wrapper.Graphics2D;
import com.wildplot.android.rendering.graphics.wrapper.Rectangle;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mig on 06.07.2014.
 */
public class ReviewCount {
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
    private double barThickness = 0.6;

    public ReviewCount(AnkiDb ankiDb, ImageView imageView, CollectionData collectionData){
        mAnkiDb = ankiDb;
        mImageView = imageView;
        mCollectionData = collectionData;
    }

    public Bitmap renderChart(int type, boolean reps){
        calculateDone(type, reps);
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

        PlotSheet plotSheet = new PlotSheet(mSeriesList[0][0]-0.5, mSeriesList[0][mSeriesList[0].length-1] + 0.5, 0, mMaxCards*1.1);
        plotSheet.setFrameThickness(mFrameThickness);

        //no title because of tab title
        //plotSheet.setTitle(mImageView.getResources().getString(mTitle));

        double xTics = ticsCalcX(targetPixelDistanceBetweenTics, rect);
        double yTics = ticsCalcY(targetPixelDistanceBetweenTics, rect);

        XAxis xaxis = new XAxis(plotSheet, 0, xTics, xTics/2.0);
        YAxis yaxis = new YAxis(plotSheet, 0, yTics, yTics/2.0);
        xaxis.setOnFrame();
        xaxis.setName(mImageView.getResources().getStringArray(R.array.due_x_axis_title)[mAxisTitles[0]]);
        xaxis.setIntegerNumbering(true);
        yaxis.setIntegerNumbering(true);
        yaxis.setName(mImageView.getResources().getString(mAxisTitles[1]));
        yaxis.setOnFrame();

        double maxCumulative = 0;
        BarGraph[] barGraphs = new BarGraph[mSeriesList.length-1];
        double[][][] cumulatives = new double[mSeriesList.length-1][][];
        for(int i = 1; i< mSeriesList.length; i++){
            double[][] bars = new double[2][];
            bars[0] = mSeriesList[0];
            bars[1] = mSeriesList[i];

            barGraphs[i-1] = new BarGraph(plotSheet,barThickness, bars, new Color(mImageView.getResources().getColor(mColors[i-1])));
            barGraphs[i-1].setFilling(true);
            barGraphs[i-1].setName(mImageView.getResources().getString(mValueLabels[i-1]));
            //barGraph.setFillColor(Color.GREEN.darker());
            barGraphs[i-1].setFillColor(new Color(mImageView.getResources().getColor(mColors[i-1])));

            cumulatives[i-1] = Utils.createCumulative(bars);
            if(i > 1){
                for(int j = 0; j< cumulatives[i-2][1].length; j++){
                    cumulatives[i-2][1][j] -= cumulatives[i-1][1][j];
                }

                if(maxCumulative < cumulatives[i-2][1][cumulatives[i-2][1].length-1]){
                    maxCumulative = cumulatives[i-2][1][cumulatives[i-2][1].length-1];
                }
            }
            if(i == mSeriesList.length -1) {
                if (maxCumulative < cumulatives[i - 1][1][cumulatives[i - 1][1].length - 1]) {
                    maxCumulative = cumulatives[i - 1][1][cumulatives[i - 1][1].length - 1];
                }
            }


        }

        PlotSheet hiddenPlotSheet = new PlotSheet(mSeriesList[0][0]-0.5, mSeriesList[0][mSeriesList[0].length-1] + 0.5, 0, maxCumulative*1.1);     //for second y-axis
        hiddenPlotSheet.setFrameThickness(mFrameThickness);

        Lines[] lineses = new Lines[mSeriesList.length-1];
        for(int i = 1; i< mSeriesList.length; i++){
            lineses[i-1] = new Lines(hiddenPlotSheet,cumulatives[i-1] ,new Color(mImageView.getResources().getColor(mColors[i-1])));
            lineses[i-1].setSize(3f);
            lineses[i-1].setShadow(5f, 3f, 3f, Color.BLACK);
            //No names to prevent double entries in legend:
            //lineses[i-1].setName(mImageView.getResources().getString(R.string.stats_cumulative));
        }

        double rightYtics = ticsCalc(targetPixelDistanceBetweenTics, rect,  maxCumulative*1.1);
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
        plotSheet.setFontSize(textSize);

        for(BarGraph barGraph : barGraphs){
            plotSheet.addDrawable(barGraph);
        }
        for(Lines lines : lineses){
            plotSheet.addDrawable(lines);
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

    public boolean calculateDone(int type, boolean reps) {
        mType = type;
        mBackwards = true;
        if (reps) {
            mTitle = R.string.stats_review_count;
            mAxisTitles = new int[] { type, R.string.stats_answers, R.string.stats_cumulative_answers };
        } else {
            mTitle = R.string.stats_review_time;
        }
        mValueLabels = new int[] { R.string.statistics_learn, R.string.statistics_relearn, R.string.statistics_young,
                R.string.statistics_mature, R.string.statistics_cram };
        mColors = new int[] { R.color.stats_learn, R.color.stats_relearn, R.color.stats_young, R.color.stats_mature,
                R.color.stats_cram };
        int num = 0;
        int chunk = 0;
        switch (type) {
            case Utils.TYPE_MONTH:
                num = 31;
                chunk = 1;
                break;
            case Utils.TYPE_YEAR:
                num = 52;
                chunk = 7;
                break;
            case Utils.TYPE_LIFE:
                num = -1;
                chunk = 30;
                break;
        }
        ArrayList<String> lims = new ArrayList<String>();
        if (num != -1) {
            lims.add("id > " + ((mCollectionData.getDayCutoff() - ((num + 1) * chunk * 86400)) * 1000));
        }
        String lim = _revlogLimitWholeOnly().replaceAll("[\\[\\]]", "");
        if (lim.length() > 0) {
            lims.add(lim);
        }
        if (lims.size() > 0) {
            lim = "WHERE ";
            while (lims.size() > 1) {
                lim += lims.remove(0) + " AND ";
            }
            lim += lims.remove(0);
        } else {
            lim = "";
        }
        String ti;
        String tf;
        if (!reps) {
            ti = "time/1000";
            if (mType == 0) {
                tf = "/60.0"; // minutes
                mAxisTitles = new int[] { type, R.string.stats_minutes, R.string.stats_cumulative_time_minutes };
            } else {
                tf = "/3600.0"; // hours
                mAxisTitles = new int[] { type, R.string.stats_hours, R.string.stats_cumulative_time_hours };
            }
        } else {
            ti = "1";
            tf = "";
        }
        ArrayList<double[]> list = new ArrayList<double[]>();
        Cursor cur = null;
        try {
            cur = mAnkiDb
                    .getDatabase()
                    .rawQuery(
                            "SELECT (cast((id/1000 - " + mCollectionData.getDayCutoff() + ") / 86400.0 AS INT))/"
                                    + chunk + " AS day, " + "sum(CASE WHEN type = 0 THEN " + ti + " ELSE 0 END)"
                                    + tf
                                    + ", " // lrn
                                    + "sum(CASE WHEN type = 1 AND lastIvl < 21 THEN " + ti + " ELSE 0 END)" + tf
                                    + ", " // yng
                                    + "sum(CASE WHEN type = 1 AND lastIvl >= 21 THEN " + ti + " ELSE 0 END)" + tf
                                    + ", " // mtr
                                    + "sum(CASE WHEN type = 2 THEN " + ti + " ELSE 0 END)" + tf + ", " // lapse
                                    + "sum(CASE WHEN type = 3 THEN " + ti + " ELSE 0 END)" + tf // cram
                                    + " FROM revlog " + lim + " GROUP BY day ORDER BY day", null);
            while (cur.moveToNext()) {
                list.add(new double[] { cur.getDouble(0), cur.getDouble(1), cur.getDouble(4), cur.getDouble(2),
                        cur.getDouble(3), cur.getDouble(5) });
            }
        } finally {
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
        }

        // small adjustment for a proper chartbuilding with achartengine
        if (type != Utils.TYPE_LIFE && (list.size() == 0 || list.get(0)[0] > -num)) {
            list.add(0, new double[] { -num, 0, 0, 0, 0, 0 });
        } else if (type == Utils.TYPE_LIFE && list.size() == 0) {
            list.add(0, new double[] { -12, 0, 0, 0, 0, 0 });
        }
        if (list.get(list.size() - 1)[0] < 0) {
            list.add(new double[] { 0, 0, 0, 0, 0, 0 });
        }

        mSeriesList = new double[6][list.size()];
        for (int i = 0; i < list.size(); i++) {
            double[] data = list.get(i);
            mSeriesList[0][i] = data[0]; // day
            mSeriesList[1][i] = data[1] + data[2] + data[3] + data[4] + data[5]; // lrn
            mSeriesList[2][i] = data[2] + data[3] + data[4] + data[5]; // relearn
            mSeriesList[3][i] = data[3] + data[4] + data[5]; // young
            mSeriesList[4][i] = data[4] + data[5]; // mature
            mSeriesList[5][i] = data[5]; // cram
            if(mSeriesList[1][i] > mMaxCards)
                mMaxCards = (int) Math.round(data[1] + data[2] + data[3] + data[4] + data[5]);

            if(data[5] >= 0.999)
                mFoundCramCards = true;

            if(data[1] >= 0.999)
                mFoundLearnCards = true;

            if(data[2] >= 0.999)
                mFoundRelearnCards = true;
        }
        mMaxElements = list.size()-1;
        return list.size() > 0;
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
