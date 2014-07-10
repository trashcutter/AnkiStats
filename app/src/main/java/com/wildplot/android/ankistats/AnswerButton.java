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

import java.util.*;

/**
 * Created by mig on 06.07.2014.
 */
public class AnswerButton {
    private AnkiDb mAnkiDb;
    private ImageView mImageView;
    private CollectionData mCollectionData;

    private int mFrameThickness = 60;
    private int targetPixelDistanceBetweenTics = 150;


    private int mMaxCards = 0;
    private int mMaxElements = 0;
    private int mType;
    private int mTitle;
    private boolean mBackwards;
    private int[] mValueLabels;
    private int[] mColors;
    private int[] mAxisTitles;
    private double[][] mSeriesList;
    private double mBarThickness = 0.8;
    private double[][] mCumulative;

    public AnswerButton(AnkiDb ankiDb, ImageView imageView, CollectionData collectionData){
        mAnkiDb = ankiDb;
        mImageView = imageView;
        mCollectionData = collectionData;
    }

    public Bitmap renderChart(int type){
        calculateAnswerButtons(type);
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

        PlotSheet plotSheet = new PlotSheet(0, 15, 0, mMaxCards*1.03);
        plotSheet.setFrameThickness(mFrameThickness);

        //no title because of tab title
        //plotSheet.setTitle(mImageView.getResources().getString(mTitle));

        double xTics = ticsCalcX(targetPixelDistanceBetweenTics, rect);
        double yTics = ticsCalcY(targetPixelDistanceBetweenTics, rect);

        XAxis xaxis = new XAxis(plotSheet, 0, xTics, xTics/2.0);
        YAxis yaxis = new YAxis(plotSheet, 0, yTics, yTics/2.0);
        double[] timePositions = {1,2,3,6,7,8,9,11,12,13,14};
        xaxis.setExplicitTics(timePositions, mImageView.getResources().getStringArray(R.array.stats_eases_ticks));
        xaxis.setOnFrame();
        xaxis.setName(mImageView.getResources().getString(mAxisTitles[0]));
        xaxis.setIntegerNumbering(true);
        yaxis.setIntegerNumbering(true);
        yaxis.setName(mImageView.getResources().getString(mAxisTitles[1]));
        yaxis.setOnFrame();



        BarGraph[] barGraphs = new BarGraph[mSeriesList.length-1];
        for(int i = 1; i< mSeriesList.length; i++){
            double[][] bars = new double[2][];
            bars[0] = mSeriesList[0];
            bars[1] = mSeriesList[i];

            barGraphs[i-1] = new BarGraph(plotSheet, mBarThickness, bars, new Color(mImageView.getResources().getColor(mColors[i-1])));
            barGraphs[i-1].setFilling(true);
            barGraphs[i-1].setName(mImageView.getResources().getString(mValueLabels[i-1]));
            //barGraph.setFillColor(Color.GREEN.darker());
            barGraphs[i-1].setFillColor(new Color(mImageView.getResources().getColor(mColors[i-1])));
        }

        PlotSheet hiddenPlotSheet = new PlotSheet(0, 15, 0, 101);     //for second y-axis
        hiddenPlotSheet.setFrameThickness(mFrameThickness);

        Lines[] lineses = new Lines[mCumulative.length - 1];
        for(int i = 1; i< mCumulative.length; i++){
            double[][] cumulatives = new double[][]{mCumulative[0], mCumulative[i]};
            lineses[i-1] = new Lines(hiddenPlotSheet,cumulatives ,new Color(mImageView.getResources().getColor(mColors[i-1])));
            lineses[i-1].setSize(3f);
            lineses[i-1].setShadow(5f, 3f, 3f, Color.BLACK);
            //No names to prevent double entries in legend:
            //lineses[i-1].setName(mImageView.getResources().getString(R.string.stats_cumulative));
        }

        double rightYtics = ticsCalc(targetPixelDistanceBetweenTics, rect,  101);
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

    public boolean calculateAnswerButtons(int type) {
        mTitle = R.string.stats_answer_buttons;
        mAxisTitles = new int[] { R.string.stats_answer_type, R.string.stats_answers, R.string.stats_cumulative_correct_percentage };

        mValueLabels = new int[] { R.string.statistics_learn, R.string.statistics_young, R.string.statistics_mature};
        mColors = new int[] { R.color.stats_learn, R.color.stats_young, R.color.stats_mature};

        mType = type;
        String lim = _revlogLimitWholeOnly().replaceAll("[\\[\\]]", "");

        String lims = "";   //TODO when non whole collection selection is possible test this!
        int days = 0;

        if (lim.length() > 0)
            lims += lim + " and ";

        if (type == Utils.TYPE_MONTH)
            days = 30;
        else if (type == Utils.TYPE_YEAR)
            days = 365;
        else
            days = -1;

        if (days > 0)
            lims += "id > " + ((mCollectionData.getDayCutoff()-(days*86400))*1000);
        if (lims.length() > 0)
            lim = "where " + lims;
        else
            lim = "";

        ArrayList<double[]> list = new ArrayList<double[]>();
        Cursor cur = null;
        String query = "select (case " +
                "                when type in (0,2) then 0 " +
                "        when lastIvl < 21 then 1 " +
                "        else 2 end) as thetype, " +
                "        (case when type in (0,2) and ease = 4 then 3 else ease end), count() from revlog " + lim + " "+
                "        group by thetype, ease " +
                "        order by thetype, ease";

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

        //TODO adjust for AnswerButton, for now only copied from intervals
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


        double[] totals= new double[3];
        for (int i = 0; i < list.size(); i++) {
            double[] data = list.get(i);
            int currentType = (int)data[0];
            double ease = data[1];
            double cnt = data[2];

            totals[currentType] += cnt;
        }
        int badNew = 0;
        int badYoung = 0;
        int badMature = 0;


        mSeriesList = new double[4][list.size()+1];

        for (int i = 0; i < list.size(); i++) {
            double[] data = list.get(i);
            int currentType = (int)data[0];
            double ease = data[1];
            double cnt = data[2];

            if (currentType == 1)
                ease += 5;
            else if(currentType == 2)
                ease += 10;

            if((int)ease == 1){
                badNew = i;
            }

            if((int)ease == 6){
                badYoung = i;
            }
            if((int)ease == 11){
                badMature = i;
            }
            mSeriesList[0][i] = ease;
            mSeriesList[1+currentType][i] = cnt;
            if(cnt > mMaxCards)
                mMaxCards = (int) cnt;
        }
        mSeriesList[0][list.size()] = 15;

        mCumulative = new double[4][];
        mCumulative[0] = mSeriesList[0];
        mCumulative[1] = Utils.createCumulativeInPercent(mSeriesList[1], totals[0], badNew);
        mCumulative[2] = Utils.createCumulativeInPercent(mSeriesList[2], totals[1], badYoung);
        mCumulative[3] = Utils.createCumulativeInPercent(mSeriesList[3], totals[2], badMature);

        if(type == 2){
            System.err.println("TEst");
        }

        mMaxElements = 15;      //bars are positioned from 1 to 14
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
