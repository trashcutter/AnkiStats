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

import java.util.Arrays;
import java.util.List;

/**
 * Created by mig on 06.07.2014.
 */
public class Utils {
    public static int layoutCnt = 0;
    public static final int TYPE_MONTH = 0;
    public static final int TYPE_YEAR = 1;
    public static final int TYPE_LIFE = 2;
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
}
