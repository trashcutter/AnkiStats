package com.wildplot.android.ankistats;

import android.database.Cursor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * LibAnki stuff we need for this to work
 */
public class CollectionData {
    private AnkiDb mAnkiDb;
    // BEGIN: SQL table columns
    private long mCrt;
    private long mMod;
    private long mScm;
    private boolean mDty;
    private int mUsn;
    private long mLs;
    private JSONObject mConf;
    private HashMap<Long, JSONObject> mDecks;
    private HashMap<Long, JSONObject> mDconf;
    private boolean mChanged =true;
    private int mToday;
    private long mDayCutoff;

    public CollectionData(AnkiDb ankiDb){
        mAnkiDb = ankiDb;
        loadCol();
        _updateCutoff();
    }

    public int getToday() {
        return mToday;
    }
    public long getDayCutoff() {
        return mDayCutoff;
    }
    public ArrayList<JSONObject> allDecks() {
        ArrayList<JSONObject> decks = new ArrayList<JSONObject>();
        Iterator<JSONObject> it = mDecks.values().iterator();
        while (it.hasNext()) {
            decks.add(it.next());
        }
        return decks;
    }

    private void _updateCutoff() {
        // calculate days since col created and store in mToday
        mToday = 0;
        Calendar crt = GregorianCalendar.getInstance();
        crt.setTimeInMillis(mCrt*1000); // creation time (from crt as stored in database)
        Calendar fromNow = GregorianCalendar.getInstance(); // decremented towards crt

        // code to avoid counting years worth of days
        int yearSpan = fromNow.get(Calendar.YEAR) - crt.get(Calendar.YEAR);
        if (yearSpan > 1) { // at least one full year has definitely lapsed since creation
            int toJump = 365 * (yearSpan - 1);
            fromNow.add(Calendar.YEAR, -toJump);
            if (fromNow.compareTo(crt) < 0) { // went too far, reset and do full count
                fromNow = GregorianCalendar.getInstance();
            } else {
                mToday += toJump;
            }
        }

        // count days backwards
        while (fromNow.compareTo(crt) > 0) {
            fromNow.add(Calendar.DAY_OF_MONTH, -1);
            if (fromNow.compareTo(crt) >= 0) {
                mToday++;
            }
        }

        crt.add(Calendar.DAY_OF_YEAR, mToday + 1);
        mDayCutoff = crt.getTimeInMillis() / 1000;
    }

    private void loadCol() {
        Cursor cursor = null;
        try {
            // Read in deck table columns
            cursor = mAnkiDb.getDatabase().rawQuery(
                    "SELECT crt, mod, scm, dty, usn, ls, " +
                            "conf, decks, dconf FROM col", null);
            if (!cursor.moveToFirst()) {
                return;
            }
            mCrt = cursor.getLong(0);
            mMod = cursor.getLong(1);
            mScm = cursor.getLong(2);
            mDty = cursor.getInt(3) == 1; // No longer used
            mUsn = cursor.getInt(4);
            mLs = cursor.getLong(5);
            try {
                mConf = new JSONObject(cursor.getString(6));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            loadDecks(cursor.getString(7), cursor.getString(8));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void loadDecks(String decks, String dconf) {
        mDecks = new HashMap<Long, JSONObject>();
        mDconf = new HashMap<Long, JSONObject>();
        try {
            JSONObject decksarray = new JSONObject(decks);
            JSONArray ids = decksarray.names();
            for (int i = 0; i < ids.length(); i++) {
                String id = ids.getString(i);
                JSONObject o = decksarray.getJSONObject(id);
                long longId = Long.parseLong(id);
                mDecks.put(longId, o);
            }
            JSONObject confarray = new JSONObject(dconf);
            ids = confarray.names();
            for (int i = 0; i < ids.length(); i++) {
                String id = ids.getString(i);
                mDconf.put(Long.parseLong(id), confarray.getJSONObject(id));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        mChanged = false;
    }
}
