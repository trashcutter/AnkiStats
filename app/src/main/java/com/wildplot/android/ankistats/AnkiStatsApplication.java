package com.wildplot.android.ankistats;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * Created by mig on 06.07.2014.
 */
public class AnkiStatsApplication extends Application {
    /**
     * Tag for logging messages.
     */
    public static final String TAG = "AnkiDroidStats";
    private static AnkiStatsApplication sInstance;
    public static final int SDK_VERSION = android.os.Build.VERSION.SDK_INT;
    private AnkiDb mDatabase;
    private boolean mDatabaseLoaded = false;
    private CollectionData mCollectionData;
    private float mStandardTextSize = 10f;
    private int mStatType = Utils.TYPE_MONTH;
    private String mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AnkiDroid";
    private String mCollectionFileName ="/collection.anki2";

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public String getStandardFilePath(){
        return mFilePath;
    }

    public void setFilePath(String path){
        mFilePath = path;
    }

    public void loadDb(){
        mDatabaseLoaded = false;
        OpenDatabaseTask openDatabaseTask = new OpenDatabaseTask();
        openDatabaseTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mFilePath + mCollectionFileName);
        //openDatabaseTask.execute(mFilePath + mCollectionFileName);
        Log.d(TAG, "loading db");
    }

    public static AnkiStatsApplication getInstance() {
        return sInstance;
    }


    public static void disableDatabaseWriteAheadLogging(SQLiteDatabase db) {
        if(android.os.Build.VERSION.SDK_INT >= 16)
            disableDatabaseWriteAheadLogging(db, true);
    }
    @TargetApi(16)
    public static void disableDatabaseWriteAheadLogging(SQLiteDatabase db, boolean is16) {
        db.disableWriteAheadLogging();
    }


    public void createForecastChart(View... views){
        CreateForecastChartTask createForecastChartTask = new CreateForecastChartTask();
        createForecastChartTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, views);
    }

    public void createReviewCountChart(View... views){
        CreateReviewCountTask createReviewCountTask = new CreateReviewCountTask();
        createReviewCountTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, views);
    }
    public void createReviewTimeChart(View... views){
        CreateReviewTimeTask createReviewTimeTask = new CreateReviewTimeTask();
        createReviewTimeTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, views);
    }
    public void createIntervalChart(View... views){
        CreateIntervalTask createIntervalTask = new CreateIntervalTask();
        createIntervalTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, views);
    }
    public void createBreakdownChart(View... views){
        CreateBreakdownTask createBreakdownTask = new CreateBreakdownTask();
        createBreakdownTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, views);
    }
    public void createWeeklyBreakdownChart(View... views){
        CreateWeeklyBreakdownTask createWeeklyBreakdownTask = new CreateWeeklyBreakdownTask();
        createWeeklyBreakdownTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, views);
    }
    public void createAnswerButtonTask(View... views){
        CreateAnswerButtonTask createAnswerButtonTask = new CreateAnswerButtonTask();
        createAnswerButtonTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, views);
    }
    public void createCardsTypesTask(View... views){
        CreateCardsTypesChart createCardsTypesChart = new CreateCardsTypesChart();
        createCardsTypesChart.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, views);
    }

    private class CreateForecastChartTask extends AsyncTask<View, Void, Bitmap>{
        ImageView mImageView;
        ProgressBar mProgressBar;
        @Override
        protected Bitmap doInBackground(View... params) {
            mImageView = (ImageView)params[0];
            mProgressBar = (ProgressBar) params[1];
            //int tag = (Integer)mImageView.getTag();
            while(!mDatabaseLoaded){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Forecast forecast = new Forecast(mDatabase, mImageView, mCollectionData);
            return forecast.renderChart(mStatType);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null){

                mProgressBar.setVisibility(View.GONE);
                mImageView.setVisibility(View.VISIBLE);
                mImageView.setImageBitmap(bitmap);
            }
        }

    }
    private class CreateReviewCountTask extends AsyncTask<View, Void, Bitmap>{
        ImageView mImageView;
        ProgressBar mProgressBar;
        @Override
        protected Bitmap doInBackground(View... params) {
            mImageView = (ImageView)params[0];
            mProgressBar = (ProgressBar) params[1];
            //int tag = (Integer)mImageView.getTag();
            while(!mDatabaseLoaded){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ReviewCount reviewCount = new ReviewCount(mDatabase, mImageView, mCollectionData);
            return reviewCount.renderChart(mStatType, true);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null){
                mImageView.setImageBitmap(bitmap);
                mProgressBar.setVisibility(View.GONE);
                mImageView.setVisibility(View.VISIBLE);
            }
        }

    }
    private class CreateReviewTimeTask extends AsyncTask<View, Void, Bitmap>{
        ImageView mImageView;
        ProgressBar mProgressBar;
        @Override
        protected Bitmap doInBackground(View... params) {
            mImageView = (ImageView)params[0];
            mProgressBar = (ProgressBar) params[1];
            //int tag = (Integer)mImageView.getTag();
            while(!mDatabaseLoaded){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ReviewCount reviewCount = new ReviewCount(mDatabase, mImageView, mCollectionData);
            return reviewCount.renderChart(mStatType, false);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null){
                mImageView.setImageBitmap(bitmap);
                mProgressBar.setVisibility(View.GONE);
                mImageView.setVisibility(View.VISIBLE);
            }
        }

    }
    private class CreateIntervalTask extends AsyncTask<View, Void, Bitmap>{
        ImageView mImageView;
        ProgressBar mProgressBar;
        @Override
        protected Bitmap doInBackground(View... params) {
            mImageView = (ImageView)params[0];
            mProgressBar = (ProgressBar) params[1];
            //int tag = (Integer)mImageView.getTag();
            while(!mDatabaseLoaded){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Intervals intervals = new Intervals(mDatabase, mImageView, mCollectionData);
            return intervals.renderChart(mStatType);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null){
                mImageView.setImageBitmap(bitmap);
                mProgressBar.setVisibility(View.GONE);
                mImageView.setVisibility(View.VISIBLE);
            }
        }

    }
    private class CreateBreakdownTask extends AsyncTask<View, Void, Bitmap>{
        ImageView mImageView;
        ProgressBar mProgressBar;
        @Override
        protected Bitmap doInBackground(View... params) {
            mImageView = (ImageView)params[0];
            mProgressBar = (ProgressBar) params[1];
            //int tag = (Integer)mImageView.getTag();
            while(!mDatabaseLoaded){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            HourlyBreakdown hourlyBreakdown = new HourlyBreakdown(mDatabase, mImageView, mCollectionData);
            return hourlyBreakdown.renderChart(mStatType);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null){
                mImageView.setImageBitmap(bitmap);
                mProgressBar.setVisibility(View.GONE);
                mImageView.setVisibility(View.VISIBLE);
            }
        }

    }
    private class CreateWeeklyBreakdownTask extends AsyncTask<View, Void, Bitmap>{
        ImageView mImageView;
        ProgressBar mProgressBar;
        @Override
        protected Bitmap doInBackground(View... params) {
            mImageView = (ImageView)params[0];
            mProgressBar = (ProgressBar) params[1];
            //int tag = (Integer)mImageView.getTag();
            while(!mDatabaseLoaded){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            WeeklyBreakdown weeklyBreakdown = new WeeklyBreakdown(mDatabase, mImageView, mCollectionData);
            return weeklyBreakdown.renderChart(mStatType);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null){
                mImageView.setImageBitmap(bitmap);
                mProgressBar.setVisibility(View.GONE);
                mImageView.setVisibility(View.VISIBLE);
            }
        }

    }
    private class CreateAnswerButtonTask extends AsyncTask<View, Void, Bitmap>{
        ImageView mImageView;
        ProgressBar mProgressBar;
        @Override
        protected Bitmap doInBackground(View... params) {
            mImageView = (ImageView)params[0];
            mProgressBar = (ProgressBar) params[1];
            //int tag = (Integer)mImageView.getTag();
            while(!mDatabaseLoaded){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            AnswerButton answerButton = new AnswerButton(mDatabase, mImageView, mCollectionData);
            return answerButton.renderChart(mStatType);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null){
                mImageView.setImageBitmap(bitmap);
                mProgressBar.setVisibility(View.GONE);
                mImageView.setVisibility(View.VISIBLE);
            }
        }

    }
    private class CreateCardsTypesChart extends AsyncTask<View, Void, Bitmap>{
        ImageView mImageView;
        ProgressBar mProgressBar;
        @Override
        protected Bitmap doInBackground(View... params) {
            mImageView = (ImageView)params[0];
            mProgressBar = (ProgressBar) params[1];
            //int tag = (Integer)mImageView.getTag();
            while(!mDatabaseLoaded){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            CardsTypes cardsTypes = new CardsTypes(mDatabase, mImageView, mCollectionData);
            return cardsTypes.renderChart(mStatType);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null){
                mImageView.setImageBitmap(bitmap);
                mProgressBar.setVisibility(View.GONE);
                mImageView.setVisibility(View.VISIBLE);
            }
        }
    }
    private class OpenDatabaseTask extends  AsyncTask<String, Void, AnkiDb>{

        @Override
        protected AnkiDb doInBackground(String... params) {
            AnkiDb ankiDb = new AnkiDb(params[0]);
            mCollectionData = new CollectionData(ankiDb);
            return ankiDb;
        }

        @Override
        protected void onPostExecute(AnkiDb ankiDb) {
            mDatabase = ankiDb;
            mDatabaseLoaded = true;
            Log.d(TAG, "loading db finished");
        }
    }

    public float getmStandardTextSize() {
        return mStandardTextSize;
    }
    public void setmStandardTextSize(float mStandardTextSize) {
        this.mStandardTextSize = mStandardTextSize;
    }

    public int getStatType() {
        return mStatType;
    }

    public void setStatType(int mStatType) {
        this.mStatType = mStatType;
    }
    public static Resources getAppResources() {
        return sInstance.getResources();
    }
}
