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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class picker extends Activity {

    private TextView mTextView;
    private Context _this = this;
    private String[] bio_guide = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);

        final DotsPageIndicator mPageIndicator;
        final GridViewPager mViewPager;

        Bundle extras = getIntent().getExtras();
        String[] list;
        String[][] data = null;
        if (extras != null) {
            list= extras.getStringArray("list");

            data = new String[2][list.length/2];
            bio_guide = new String[list.length/2];
            JSONObject election_data = null;
            JSONObject result = null;
            try {
                election_data = new JSONObject(loadJSONFromAsset("election.json"));
                result = new JSONObject (election_data.getString(list[0]));
                data[1][0] = list[0]+"\n"+"Obama\t\t"+result.getString("obama")+"%"+"\n Romney\t\t"+result.getString("romney")+"%";
            } catch (JSONException e) {
                Log.d("ABCD", e.toString());
                data[1][0] = list[0]+"\n"+"No polling data available";
            }

            for (int i = 1; i<(list.length+1)/2;i++) {
                data[0][i-1] = list[2*i-1];
                bio_guide[i-1] = list[2*i];
            }
        }

        final String[][] val = data;

        // Get UI references
        mPageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
        mViewPager = (GridViewPager) findViewById(R.id.pager);
        // Assigns an adapter to provide the content for this pager
        mViewPager.setAdapter(new myAdpater(val, bio_guide,this));
        mPageIndicator.setPager(mViewPager);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

    public String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            return null;
        }
        return json;
    }

    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    private boolean shaked = false;

    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter
            if (mAccel > 100 && !shaked) {
                JSONArray valid_zp = null;
                String ran_zp = null;
                try {
                    valid_zp = new JSONArray(loadJSONFromAsset("valid-zips.json"));
                    ran_zp = valid_zp.getString((int)(Math.random()* valid_zp.length()));
                    Log.d("ABCD", ran_zp);
                } catch (JSONException e) {
                }
                Intent newZip = new Intent(_this, WatchToPhoneService.class);
                newZip.putExtra("Zip_Code", ran_zp);
                newZip.putExtra("path", "cong");
                shaked = true;
                startService(newZip);
            }
            else if (shaked && mAccel < 100) {
                shaked = false;
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
        String[] bg;

        private myAdpater(String[][] data, String[] bg, Context mc) {
            mData = data;
            this.mc = mc;
            this.bg = bg;
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
                ((TextView)pop.findViewById(R.id.card_title)).setText(mData[i][0]);
                viewGroup.addView(pop);
                return pop;
            }
            View pop = View.inflate(mc, R.layout.mycard, null);
            final View temp = pop;
            final String tempName = mData[i][i1];
            ((TextView) pop.findViewById(R.id.card_title)).setText(mData[i][i1]);
            final String bio_id = bg[i1];
            ((Button)(pop.findViewById(R.id.button))).setOnClickListener(new Button.OnClickListener() {
                public void onClick(View b) {
                    Intent sendIntent = new Intent(mc, WatchToPhoneService.class);
                    sendIntent.putExtra("bg", bio_id);
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
