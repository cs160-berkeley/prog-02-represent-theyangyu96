package com.example.yang.represent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class picker extends Activity {

    private TextView mTextView;
    private Context _this = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);

        final DotsPageIndicator mPageIndicator;
        final GridViewPager mViewPager;

        Bundle extras = getIntent().getExtras();
        String zp ="";
        if (extras != null) {
            zp = extras.getString("Zip_Code");
        }

        final String[][] data = {
                { "Barbara Comstock (R)", "Tim Kaine (D)", "Mark Warner (D)" },
                { zp+"\n"+"Obama\t55%\n Romney\t45%", "Row 1, Col 1", "Row 1, Col 2" },
        };

        // Get UI references
        mPageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
        mViewPager = (GridViewPager) findViewById(R.id.pager);
        // Assigns an adapter to provide the content for this pager
        mViewPager.setAdapter(new myAdpater(data, this));
        mPageIndicator.setPager(mViewPager);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity

    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter
            if (mAccel > 100) {
                Intent newZip = new Intent(_this, WatchToPhoneService.class);
                newZip.putExtra("Zip_Code", (Math.round(Math.random() * 89999) + 10000)+"");
                newZip.putExtra("path", "cong");
                try
                {
                    Thread.sleep(100);
                }
                catch (Exception e){}
                startService(newZip);
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    private static final class myAdpater extends GridPagerAdapter {

        String[][] mData;
        Context mc;

        private myAdpater(String[][] data, Context mc) {
            mData = data;
            this.mc = mc;
        }

        @Override
        public int getRowCount() {
            return mData.length;
        }

        @Override
        public int getColumnCount(int row) {
            return mData[row].length;
        }

        @Override
        public Object instantiateItem(ViewGroup viewGroup, int i, int i1) {

            if (i == 1) {
                View pop = View.inflate(mc, R.layout.mycard,null);
                final View temp = pop;
                final String tempName = mData[i][i1];
                ((TextView)pop.findViewById(R.id.card_title)).setText("2012 Election\n" + mData[i][0]);
                viewGroup.addView(pop);
                return pop;
            }
            else if (i ==0 && i1 ==0) {
                View pop = View.inflate(mc, R.layout.mycard,null);
                final View temp = pop;
                final String tempName = mData[i][i1];
                ((TextView)pop.findViewById(R.id.card_title)).setText("Rep. " + mData[i][i1]);
                ((Button)(pop.findViewById(R.id.button))).setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View b) {
                        Intent sendIntent = new Intent(mc, WatchToPhoneService.class);
                        sendIntent.putExtra("person", tempName.substring(0, tempName.length() - 4));
                        mc.startService(sendIntent);
                    }
                });
                viewGroup.addView(pop);
                return pop;
            }
            View pop = View.inflate(mc, R.layout.mycard,null);
            final View temp = pop;
            final String tempName = mData[i][i1];
            ((TextView)pop.findViewById(R.id.card_title)).setText("Sen. " + mData[i][i1]);
            ((Button)(pop.findViewById(R.id.button))).setOnClickListener(new Button.OnClickListener() {
                public void onClick(View b) {
                    Intent sendIntent = new Intent(mc, WatchToPhoneService.class);
                    sendIntent.putExtra("person", tempName.substring(0, tempName.length() - 4));
                    mc.startService(sendIntent);
                }
            });
            viewGroup.addView(pop);
            return pop;

        }

        @Override
        public void destroyItem(ViewGroup viewGroup, int i, int i1, Object o) {
            viewGroup.removeView((View)o);
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view.equals(o);
        }
    }
}
