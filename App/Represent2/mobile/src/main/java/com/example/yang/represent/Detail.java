package com.example.yang.represent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Detail extends AppCompatActivity {
    String bio = null;
    String term = null;
    String party = null;
    String name = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            bio = extras.getString("bio");
        }


        new fill_in().execute(bio);
    }
    class fill_in extends AsyncTask<String, String, String> {
        Bitmap headshot = null;
        @Override
        protected String doInBackground(String... params) {
            try {
                String bio_id = params[0];
                Bitmap image = null;
                int inSampleSize = 0;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                options.inSampleSize = inSampleSize;
                try
                {
                    URL url = new URL(String.format("https://theunitedstates.io/images/congress/original/%1$s.jpg",bio_id));
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    InputStream stream = connection.getInputStream();
                    image = BitmapFactory.decodeStream(stream, null, options);
                    options.inJustDecodeBounds = false;
                    connection = (HttpURLConnection)url.openConnection();
                    stream = connection.getInputStream();
                    image = BitmapFactory.decodeStream(stream, null, options);
                    headshot = image;
                    return bio_id;
                } catch(Exception e) {
                    Log.e("ABCD", "1:"+e.toString());
                    return null;
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }
        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR1";
                return ;
            }
            ((ImageView)findViewById(R.id.headShot)).setImageBitmap(headshot);
            new basic().execute(response);
        }
    }


        class basic extends AsyncTask<String, String, String> {
            String bio_id = null;

            @Override
            protected String doInBackground(String... params) {
                try {
                    bio_id = params[0];
                    String API_Call = String.format("http://congress.api.sunlightfoundation.com/legislators?bioguide_id=%1$s&apikey=%2$s",
                            bio_id, getString(R.string.sunshine_API_key));

                    URL url = new URL(API_Call);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();
                        return stringBuilder.toString();
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (Exception e) {
                    Log.d("ABCD", e.toString());
                    return null;
                }
            }

            protected void onPostExecute(String response) {
                if (response == null) {
                    response = "THERE WAS AN ERROR1";
                }
                try {
                    JSONObject json_obj = new JSONObject(response);
                    JSONArray single_person = json_obj.getJSONArray("results");
                    JSONObject g = single_person.getJSONObject(0);

                    ((TextView) findViewById(R.id.term)).setText("Term ends: " + g.getString("term_end"));
                    ((TextView) findViewById(R.id.name)).setText(g.getString("title")+" "+g.getString("first_name") + " " + g.getString("last_name") + " (" + g.getString("party") + ")");
                    ImageView bg = ((ImageView) findViewById(R.id.backdrop));
                    if (g.getString("party").equals("D")) {
                        bg.setImageResource(R.drawable.donkey);
                    } else {
                        bg.setImageResource(R.drawable.elephant);
                    }
                    new committee_fill().execute(bio_id);
                } catch (JSONException e) {
                    Log.d("ABCD", e.toString());
                }
            }
        }



    class committee_fill extends AsyncTask<String, String, String> {
        String bio = null;
        @Override
        protected String doInBackground(String... params) {
            try {
                String bio_id = params[0];
                bio = bio_id;
                String API_Call = String.format("http://congress.api.sunlightfoundation.com/committees?member_ids=%1$s&apikey=%2$s",
                        bio_id, getString(R.string.sunshine_API_key));

                URL url = new URL(API_Call);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.d("ABCD", e.toString());
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR1";
            }
            try {
                JSONObject json_obj = new JSONObject(response);
                JSONArray committees = json_obj.getJSONArray("results");
                int num_com = committees.length();
                LinearLayout comm = ((LinearLayout)findViewById(R.id.commitee));
                LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                        (getApplicationContext().LAYOUT_INFLATER_SERVICE);

                for (int i = 0; i < num_com; i++) {
                    final JSONObject committee = committees.getJSONObject(i);
                    View to_add = inflater.inflate(R.layout.subpoints, comm, false);
                    ((TextView)to_add.findViewById(R.id.c1)).setText("- "+committee.getString("name"));
                    comm.addView(to_add, 0);
                }
                new bill_fill().execute(bio);
            } catch (JSONException e){
                Log.d("ABCD",e.toString());
            }
        }
    }

    class bill_fill extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                String bio_id = params[0];
                String API_Call = String.format("http://congress.api.sunlightfoundation.com/bills/search?cosponsor_ids=%1$s&apikey=%2$s",
                        bio_id, getString(R.string.sunshine_API_key));

                URL url = new URL(API_Call);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.d("ABCD", e.toString());
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR1";
            }
            try {
                JSONObject json_obj = new JSONObject(response);
                JSONArray bills = json_obj.getJSONArray("results");
                int num_bills = bills.length();
                LinearLayout bills_layout = ((LinearLayout)findViewById(R.id.bill));
                LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                        (getApplicationContext().LAYOUT_INFLATER_SERVICE);
                if (num_bills > 5)
                    num_bills = 7;
                for (int i = 0; i < num_bills; i++) {
                    final JSONObject bill = bills.getJSONObject(i);
                    View to_add = inflater.inflate(R.layout.subpoints, bills_layout, false);
                    String bill_name = bill.getString("short_title");
                    if (bill_name!=null && !bill_name.equals("null")) {
                        ((TextView)to_add.findViewById(R.id.c1)).setText("- "+bill_name+"\n\t"+"Introduced on "+bill.getString("introduced_on"));
                        bills_layout.addView(to_add, 0);
                    }
                }
            } catch (JSONException e){
                Log.d("ABCD",e.toString());
            }
        }
    }


}
