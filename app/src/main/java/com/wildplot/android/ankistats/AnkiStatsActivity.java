package com.wildplot.android.ankistats;

import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;


public class AnkiStatsActivity extends Activity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anki_stats);
        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(1);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

//        ViewPager.OnPageChangeListener pageChangelistener = new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int pageSelected) {
//                ChartFragment currentFragment = (ChartFragment) mSectionsPagerAdapter.getItem(pageSelected);
//                currentFragment.checkAndUpdate();
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        };

        //mViewPager.setOnPageChangeListener(pageChangelistener);
        float size = new TextView(this).getTextSize();
        ((AnkiStatsApplication)getApplication()).setmStandardTextSize(size);
    }

    public ViewPager getViewPager(){
        return mViewPager;
    }

    public SectionsPagerAdapter getSectionsPagerAdapter() {
        return mSectionsPagerAdapter;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
        ChartFragment currentFragment = (ChartFragment) mSectionsPagerAdapter.getItem(tab.getPosition());
        currentFragment.checkAndUpdate();
        //System.err.println("!!!!!<<<<onTabSelected" + tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        ChartFragment currentFragment = (ChartFragment) mSectionsPagerAdapter.getItem(tab.getPosition());
        currentFragment.checkAndUpdate();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            if (object instanceof ChartFragment) {
                ((ChartFragment) object).checkAndUpdate();
            }
            //don't return POSITION_NONE, avoid fragment recreation.
            return super.getItemPosition(object);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return ChartFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.stats_forecast).toUpperCase(l);
                case 1:
                    return getString(R.string.stats_review_count).toUpperCase(l);
                case 2:
                    return getString(R.string.stats_review_time).toUpperCase(l);
                case 3:
                    return getString(R.string.stats_review_intervals).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A chart fragment containing a ImageView.
     */
    public static class ChartFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private ImageView mChart;
        private int mHeight = 0;
        private int mWidth = 0;
        private ChartFragment mInstance = null;
        private int mSectionNumber;
        private Menu mMenu;
        private int mType  = Utils.TYPE_MONTH;
        private boolean mIsCreated = false;
        private ViewPager mActivityPager;
        private SectionsPagerAdapter mActivitySectionPagerAdapter;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ChartFragment newInstance(int sectionNumber) {
            ChartFragment fragment = new ChartFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public ChartFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            Bundle bundle = getArguments();
            mSectionNumber = bundle.getInt(ARG_SECTION_NUMBER);
            //int sectionNumber = 0;
            System.err.println("sectionNumber: " + mSectionNumber);
            View rootView = inflater.inflate(R.layout.fragment_anki_stats, container, false);
            mChart = (ImageView) rootView.findViewById(R.id.imageViewChart);

            createChart();
            mHeight = mChart.getMeasuredHeight();
            mWidth = mChart.getMeasuredWidth();
            mInstance = this;
            mType = ((AnkiStatsApplication) getActivity().getApplication()).getStatType();
            mIsCreated = true;
            mActivityPager = ((AnkiStatsActivity)getActivity()).getViewPager();
            mActivitySectionPagerAdapter = ((AnkiStatsActivity)getActivity()).getSectionsPagerAdapter();
            return rootView;
        }

        private void createChart(){
            if(mSectionNumber == 1) {
                ((AnkiStatsApplication) getActivity().getApplication()).createForecastChart(mChart);
            } else if(mSectionNumber == 2) {
                ((AnkiStatsApplication) getActivity().getApplication()).createReviewCountChart(mChart);
            } else if(mSectionNumber == 3) {
                ((AnkiStatsApplication) getActivity().getApplication()).createReviewTimeChart(mChart);
            } else if(mSectionNumber == 4) {
                ((AnkiStatsApplication) getActivity().getApplication()).createIntervalChart(mChart);
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            checkAndUpdate();

        }

        public void checkAndUpdate(){
            System.err.println("<<<<<<<checkAndUpdate" + mSectionNumber);
            if(!mIsCreated)
                return;
            int height = mChart.getMeasuredHeight();
            int width = mChart.getMeasuredWidth();
            if(height != 0 && width != 0){
                if(mHeight != height || mWidth != width || mType != ((AnkiStatsApplication) getActivity().getApplication()).getStatType()){
                    mHeight = height;
                    mWidth = width;
                    mType = ((AnkiStatsApplication) getActivity().getApplication()).getStatType();
                    createChart();
                }
            }
        }

        //This seems to be called on every tab change, so using it to update
        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
            mMenu = menu;
            System.err.println("in onCreateOptionsMenu");
            inflater.inflate(R.menu.anki_stats, menu);
            checkAndUpdate();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            System.err.println("in onOptionsItemSelected");
            AnkiStatsApplication ankiStatsApplication = ((AnkiStatsApplication) getActivity().getApplication());

            MenuItem monthItem  = (MenuItem)mMenu.findItem(R.id.action_month);
            MenuItem yearItem = (MenuItem)mMenu.findItem(R.id.action_year);
            MenuItem allItem = (MenuItem)mMenu.findItem(R.id.action_life_time);

            int id = item.getItemId();
            if(id == R.id.action_month) {
                if(ankiStatsApplication.getStatType() != Utils.TYPE_MONTH){
                    ankiStatsApplication.setStatType(Utils.TYPE_MONTH);
                    monthItem.setChecked(true);
                    yearItem.setChecked(false);
                    allItem.setChecked(false);
                    mActivitySectionPagerAdapter.notifyDataSetChanged();
                    //createChart();
                    //mActivityPager.invalidate();
                }

            } else if(id == R.id.action_year) {
                if(ankiStatsApplication.getStatType() != Utils.TYPE_YEAR){
                    ankiStatsApplication.setStatType(Utils.TYPE_YEAR);
                    monthItem.setChecked(false);
                    yearItem.setChecked(true);
                    allItem.setChecked(false);
                    mActivitySectionPagerAdapter.notifyDataSetChanged();
                    //createChart();
                    //mActivityPager.invalidate();
                }
            } else if(id == R.id.action_life_time) {
                if(ankiStatsApplication.getStatType() != Utils.TYPE_LIFE){
                    ankiStatsApplication.setStatType(Utils.TYPE_LIFE);
                    monthItem.setChecked(false);
                    yearItem.setChecked(false);
                    allItem.setChecked(true);
                    mActivitySectionPagerAdapter.notifyDataSetChanged();
                    //createChart();
                    //mActivityPager.invalidate();
                }
            }
            return true;
        }

        @Override
        public void onPrepareOptionsMenu(Menu menu) {
            super.onPrepareOptionsMenu(menu);
            MenuItem monthItem  = (MenuItem)menu.findItem(R.id.action_month);
            MenuItem yearItem = (MenuItem)menu.findItem(R.id.action_year);
            MenuItem allItem = (MenuItem)menu.findItem(R.id.action_life_time);
            AnkiStatsApplication ankiStatsApplication = ((AnkiStatsApplication) getActivity().getApplication());

            monthItem.setChecked(ankiStatsApplication.getStatType() == Utils.TYPE_MONTH);
            yearItem.setChecked(ankiStatsApplication.getStatType() == Utils.TYPE_YEAR);
            allItem.setChecked(ankiStatsApplication.getStatType() == Utils.TYPE_LIFE);

        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }

}
