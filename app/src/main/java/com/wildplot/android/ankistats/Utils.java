/****************************************************************************************
 * Copyright (c) 2009 Daniel Svärd <daniel.svard@gmail.com>                             *
 * Copyright (c) 2009 Edu Zamora <edu.zasu@gmail.com>                                   *
 * Copyright (c) 2011 Norbert Nagold <norbert.nagold@gmail.com>                         *
 * Copyright (c) 2012 Kostas Spyropoulos <inigo.aldana@gmail.com>                       *
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

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by mig on 06.07.2014.
 */
public class Utils {
    public static int layoutCnt = 0;
    public static final int TYPE_MONTH = 0;
    public static final int TYPE_YEAR = 1;
    public static final int TYPE_LIFE = 2;

    private static final int TIME_SECONDS = 0;
    private static final int TIME_MINUTES = 1;
    private static final int TIME_HOURS = 2;
    private static final int TIME_DAYS = 3;
    private static final int TIME_MONTHS = 4;
    private static final int TIME_YEARS = 5;

    public static final int TIME_FORMAT_DEFAULT = 0;
    public static final int TIME_FORMAT_IN = 1;
    public static final int TIME_FORMAT_BEFORE = 2;
    private static NumberFormat mCurrentNumberFormat = null;

    /** Given a list of integers, return a string '(int1,int2,...)'. */
    public static String ids2str(long[] ids) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        if (ids != null) {
            String s = Arrays.toString(ids);
            sb.append(s.substring(1, s.length() - 1));
        }
        sb.append(")");
        return sb.toString();
    }
    public static long[] arrayList2array(List<Long> list) {
        long[] ar = new long[list.size()];
        int i = 0;
        for (long l : list) {
            ar[i++] = l;
        }
        return ar;
    }

    public static double[][] createCumulative(double [][] nonCumulative){
        double[][] cumulativeValues = new double[2][nonCumulative[0].length];
        cumulativeValues[0][0] = nonCumulative[0][0];
        cumulativeValues[1][0] = nonCumulative[1][0];
        for(int i = 1; i<nonCumulative[0].length; i++){
            cumulativeValues[0][i] = nonCumulative[0][i];
            cumulativeValues[1][i] = cumulativeValues[1][i-1] + nonCumulative[1][i];

        }

        return cumulativeValues;
    }

    public static double[] createCumulative(double [] nonCumulative){
        double[] cumulativeValues = new double[nonCumulative.length];
        cumulativeValues[0] = nonCumulative[0];
        for(int i = 1; i<nonCumulative.length; i++){
            cumulativeValues[i] = cumulativeValues[i-1] + nonCumulative[i];
        }
        return cumulativeValues;
    }
    public static double[] createCumulativeInPercent(double [] nonCumulative, double total){
        return createCumulativeInPercent(nonCumulative, total, -1);
    }

    //use -1 on ignoreIndex if you do not want to exclude anything
    public static double[] createCumulativeInPercent(double [] nonCumulative, double total, int ignoreIndex){
        double[] cumulativeValues = new double[nonCumulative.length];
        if(total < 1)
            cumulativeValues[0] = 0;
        else if (0 != ignoreIndex)
            cumulativeValues[0] = nonCumulative[0] / total * 100.0;

        for(int i = 1; i<nonCumulative.length; i++){
            if(total < 1){
                cumulativeValues[i] = 0;
            } else if (i != ignoreIndex)
                cumulativeValues[i] = cumulativeValues[i-1] + nonCumulative[i] / total * 100.0;
            else
                cumulativeValues[i] = cumulativeValues[i-1];
        }
        return cumulativeValues;
    }

    /**
     * Return a string representing a time span (eg '2 days').
     */
    public static String fmtTimeSpan(int time) {
        return fmtTimeSpan(time, 0, false, false);
    }
    public static String fmtTimeSpan(int time, boolean _short) {
        return fmtTimeSpan(time, 0, _short, false);
    }
    public static String fmtTimeSpan(int time, int format, boolean _short, boolean boldNumber) {
        int type;
        int unit = 99;
        int point = 0;
        if (Math.abs(time) < 60 || unit < 1) {
            type = TIME_SECONDS;
        } else if (Math.abs(time) < 3600 || unit < 2) {
            type = TIME_MINUTES;
        } else if (Math.abs(time) < 60 * 60 * 24 || unit < 3) {
            type = TIME_HOURS;
        } else if (Math.abs(time) < 60 * 60 * 24 * 29.5 || unit < 4) {
            type = TIME_DAYS;
        } else if (Math.abs(time) < 60 * 60 * 24 * 30 * 11.95 || unit < 5) {
            type = TIME_MONTHS;
            point = 1;
        } else {
            type = TIME_YEARS;
            point = 1;
        }
        double ftime = convertSecondsTo(time, type);

        int formatId;
        if (false){//_short) {
            //formatId = R.array.next_review_short;
        } else {
            switch (format) {
                case TIME_FORMAT_IN:
                    if (Math.round(ftime * 10) == 10) {
                        formatId = R.array.next_review_in_s;
                    } else {
                        formatId = R.array.next_review_in_p;
                    }
                    break;
                case TIME_FORMAT_BEFORE:
                    if (Math.round(ftime * 10) == 10) {
                        formatId = R.array.next_review_before_s;
                    } else {
                        formatId = R.array.next_review_before_p;
                    }
                    break;
                case TIME_FORMAT_DEFAULT:
                default:
                    if (Math.round(ftime * 10) == 10) {
                        formatId = R.array.next_review_s;
                    } else {
                        formatId = R.array.next_review_p;
                    }
                    break;
            }
        }



        String timeString = String.format(AnkiStatsApplication.getAppResources().getStringArray(formatId)[type], boldNumber ? "<b>" + fmtDouble(ftime, point) + "</b>" : fmtDouble(ftime, point));
        if (boldNumber && time == 1) {
            timeString = timeString.replace("1", "<b>1</b>");
        }
        return timeString;
    }

    private static double convertSecondsTo(int seconds, int type) {
        switch (type) {
            case TIME_SECONDS:
                return seconds;
            case TIME_MINUTES:
                return seconds / 60.0;
            case TIME_HOURS:
                return seconds / 3600.0;
            case TIME_DAYS:
                return seconds / 86400.0;
            case TIME_MONTHS:
                return seconds / 2592000.0;
            case TIME_YEARS:
                return seconds / 31536000.0;
            default:
                return 0;
        }
    }

    /**
     * @return a string with decimal separator according to current locale
     */
    public static String fmtDouble(Double value) {
        return fmtDouble(value, 1);
    }
    public static String fmtDouble(Double value, int point) {
        // only retrieve the number format the first time
        if (mCurrentNumberFormat == null) {
            mCurrentNumberFormat = NumberFormat.getInstance(Locale.getDefault());
        }
        mCurrentNumberFormat.setMaximumFractionDigits(point);
        return mCurrentNumberFormat.format(value);
    }

}
