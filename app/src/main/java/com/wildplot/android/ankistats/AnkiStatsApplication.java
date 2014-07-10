package com.wildplot.android.ankistats;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ImageView;

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

    @Override
    public void onCreate() {
        super.onCreate();
        OpenDatabaseTask openDatabaseTask = new OpenDatabaseTask();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        path += "/AnkiDroid/collection.anki2";
        openDatabaseTask.execute(path);

        sInstance = this;
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


    public void createForecastChart(ImageView imageView){
        CreateForecastChartTask createForecastChartTask = new CreateForecastChartTask();
        createForecastChartTask.execute(imageView);
    }

    public void createReviewCountChart(ImageView imageView){
        CreateReviewCountTask createReviewCountTask = new CreateReviewCountTask();
        createReviewCountTask.execute(imageView);
    }
    public void createReviewTimeChart(ImageView imageView){
        CreateReviewTimeTask createReviewTimeTask = new CreateReviewTimeTask();
        createReviewTimeTask.execute(imageView);
    }
    public void createIntervalChart(ImageView imageView){
        CreateIntervalTask createIntervalTask = new CreateIntervalTask();
        createIntervalTask.execute(imageView);
    }
    public void createBreakdownChart(ImageView imageView){
        CreateBreakdownTask createBreakdownTask = new CreateBreakdownTask();
        createBreakdownTask.execute(imageView);
    }
    public void createAnswerButtonTask(ImageView imageView){
        CreateAnswerButtonTask createAnswerButtonTask = new CreateAnswerButtonTask();
        createAnswerButtonTask.execute(imageView);
    }
    public void createCardsTypesTask(ImageView imageView){
        CreateCardsTypesChart createCardsTypesChart = new CreateCardsTypesChart();
        createCardsTypesChart.execute(imageView);
    }

    private class CreateForecastChartTask extends AsyncTask<ImageView, Void, Bitmap>{
        ImageView mImageView;
        @Override
        protected Bitmap doInBackground(ImageView... params) {
            mImageView = params[0];
            //int tag = (Integer)mImageView.getTag();
            while(!mDatabaseLoaded){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Forecast forecast = new Forecast(mDatabase, params[0], mCollectionData);
            return forecast.renderChart(mStatType);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null)
                mImageView.setImageBitmap(bitmap);
        }
    }
    private class CreateReviewCountTask extends AsyncTask<ImageView, Void, Bitmap>{
        ImageView mImageView;
        @Override
        protected Bitmap doInBackground(ImageView... params) {
            mImageView = params[0];
            //int tag = (Integer)mImageView.getTag();
            while(!mDatabaseLoaded){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ReviewCount reviewCount = new ReviewCount(mDatabase, params[0], mCollectionData);
            return reviewCount.renderChart(mStatType, true);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null)
                mImageView.setImageBitmap(bitmap);
        }
    }
    private class CreateReviewTimeTask extends AsyncTask<ImageView, Void, Bitmap>{
        ImageView mImageView;
        @Override
        protected Bitmap doInBackground(ImageView... params) {
            mImageView = params[0];
            //int tag = (Integer)mImageView.getTag();
            while(!mDatabaseLoaded){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ReviewCount reviewCount = new ReviewCount(mDatabase, params[0], mCollectionData);
            return reviewCount.renderChart(mStatType, false);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null)
                mImageView.setImageBitmap(bitmap);
        }
    }
    private class CreateIntervalTask extends AsyncTask<ImageView, Void, Bitmap>{
        ImageView mImageView;
        @Override
        protected Bitmap doInBackground(ImageView... params) {
            mImageView = params[0];
            //int tag = (Integer)mImageView.getTag();
            while(!mDatabaseLoaded){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Intervals intervals = new Intervals(mDatabase, params[0], mCollectionData);
            return intervals.renderChart(mStatType);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null)
                mImageView.setImageBitmap(bitmap);
        }
    }
    private class CreateBreakdownTask extends AsyncTask<ImageView, Void, Bitmap>{
        ImageView mImageView;
        @Override
        protected Bitmap doInBackground(ImageView... params) {
            mImageView = params[0];
            //int tag = (Integer)mImageView.getTag();
            while(!mDatabaseLoaded){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            HourlyBreakdown hourlyBreakdown = new HourlyBreakdown(mDatabase, params[0], mCollectionData);
            return hourlyBreakdown.renderChart(mStatType);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null)
                mImageView.setImageBitmap(bitmap);
        }
    }
    private class CreateAnswerButtonTask extends AsyncTask<ImageView, Void, Bitmap>{
        ImageView mImageView;
        @Override
        protected Bitmap doInBackground(ImageView... params) {
            mImageView = params[0];
            //int tag = (Integer)mImageView.getTag();
            while(!mDatabaseLoaded){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            AnswerButton answerButton = new AnswerButton(mDatabase, params[0], mCollectionData);
            return answerButton.renderChart(mStatType);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null)
                mImageView.setImageBitmap(bitmap);
        }
    }
    private class CreateCardsTypesChart extends AsyncTask<ImageView, Void, Bitmap>{
        ImageView mImageView;
        @Override
        protected Bitmap doInBackground(ImageView... params) {
            mImageView = params[0];
            //int tag = (Integer)mImageView.getTag();
            while(!mDatabaseLoaded){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            CardsTypes cardsTypes = new CardsTypes(mDatabase, params[0], mCollectionData);
            return cardsTypes.renderChart(mStatType);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null)
                mImageView.setImageBitmap(bitmap);
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
