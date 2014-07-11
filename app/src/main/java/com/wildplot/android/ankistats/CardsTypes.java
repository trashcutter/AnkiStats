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

import java.util.ArrayList;

/**
 * Created by mig on 06.07.2014.
 */
public class CardsTypes {
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

    public CardsTypes(AnkiDb ankiDb, ImageView imageView, CollectionData collectionData){
        mAnkiDb = ankiDb;
        mImageView = imageView;
        mCollectionData = collectionData;
    }

    public Bitmap renderChart(int type){
        calculateCardsTypes(type);
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

        PlotSheet plotSheet = new PlotSheet(0, 15, 0, 15);
        plotSheet.setFrameThickness(mFrameThickness);
        plotSheet.unsetBorder();
        PieChart pieChart = new PieChart(plotSheet, mSeriesList[0]);

        Color[] colors = {new Color(mImageView.getResources().getColor(mColors[0])),
                new Color(mImageView.getResources().getColor(mColors[1])),
                new Color(mImageView.getResources().getColor(mColors[2])),
                new Color(mImageView.getResources().getColor(mColors[3]))};
        pieChart.setColors(colors);
        pieChart.setName(mImageView.getResources().getString(mValueLabels[0]) + ": " + (int)mSeriesList[0][0]);
        LegendDrawable legendDrawable1 = new LegendDrawable();
        LegendDrawable legendDrawable2 = new LegendDrawable();
        LegendDrawable legendDrawable3 = new LegendDrawable();
        legendDrawable1.setColor(new Color(mImageView.getResources().getColor(mColors[1])));
        legendDrawable2.setColor(new Color(mImageView.getResources().getColor(mColors[2])));
        legendDrawable3.setColor(new Color(mImageView.getResources().getColor(mColors[3])));

        legendDrawable1.setName(mImageView.getResources().getString(mValueLabels[1]) + ": " + (int)mSeriesList[0][1]);
        legendDrawable2.setName(mImageView.getResources().getString(mValueLabels[2]) + ": " + (int)mSeriesList[0][2]);
        legendDrawable3.setName(mImageView.getResources().getString(mValueLabels[3]) + ": " + (int)mSeriesList[0][3]);

        plotSheet.setFontSize(textSize);
        plotSheet.addDrawable(pieChart);
        plotSheet.addDrawable(legendDrawable1);
        plotSheet.addDrawable(legendDrawable2);
        plotSheet.addDrawable(legendDrawable3);

        plotSheet.paint(g);
        Bitmap bitmap = bufferedFrameImage.getBitmap();
        bitmap.prepareToDraw();
        return bitmap;
    }

    public boolean calculateCardsTypes(int type) {
        mTitle = R.string.stats_cards_types;
        mAxisTitles = new int[] { R.string.stats_answer_type, R.string.stats_answers, R.string.stats_cumulative_correct_percentage };

        mValueLabels = new int[] {R.string.statistics_mature, R.string.statistics_young_and_learn, R.string.statistics_unlearned, R.string.statistics_suspended};
        mColors = new int[] { R.color.stats_mature, R.color.stats_young, R.color.stats_unseen, R.color.stats_suspended };



        mType = type;


        ArrayList<double[]> list = new ArrayList<double[]>();
        double[] pieData;
        Cursor cur = null;
        String query = "select " +
                "sum(case when queue=2 and ivl >= 21 then 1 else 0 end), -- mtr\n" +
                "sum(case when queue in (1,3) or (queue=2 and ivl < 21) then 1 else 0 end), -- yng/lrn\n" +
                "sum(case when queue=0 then 1 else 0 end), -- new\n" +
                "sum(case when queue<0 then 1 else 0 end) -- susp\n" +
                "from cards where did in " + _limitWholeOnly();
        Log.d(AnkiStatsApplication.TAG, "CardsTypes query: " + query);

        try {
            cur = mAnkiDb
                    .getDatabase()
                    .rawQuery(query, null);

            cur.moveToFirst();
            pieData = new double[]{ cur.getDouble(0), cur.getDouble(1), cur.getDouble(2), cur.getDouble(3) };


        } finally {
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
        }

        //TODO adjust for CardsTypes, for now only copied from intervals
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

        mSeriesList = new double[1][4];
        mSeriesList[0]= pieData;
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
