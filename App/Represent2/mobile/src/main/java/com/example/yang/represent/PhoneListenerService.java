package com.example.yang.represent;

/**
 * Created by Yang on 3/3/2016.
 */

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class PhoneListenerService extends WearableListenerService {

    //   WearableListenerServices don't need an iBinder or an onStartCommand: they just need an onMessageReceieved.
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("ABCD", "message received");
        if( messageEvent.getPath().equalsIgnoreCase("/detail") ) {

            // Value contains the String we sent over in WatchToPhoneService, "good job"
            // Make a toast with the String
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Intent i = new Intent(this, Detail.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("bio", value);
            startActivity(i);
            // so you may notice this crashes the phone because it's
            //''sending message to a Handler on a dead thread''... that's okay. but don't do this.
            // replace sending a toast with, like, starting a new activity or something.
            // who said skeleton code is untouchable? #breakCSconceptions

        } else if (messageEvent.getPath().equalsIgnoreCase("/cong")) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
//            Intent i = new Intent(this, Search.class);
//
//            i.putExtra("zp", value);
//            i.putExtra("ss", "y");
//            startActivity(i);
            new Sunshine().execute(value);
        }
        else {
            super.onMessageReceived( messageEvent );
        }

    }

    class Sunshine extends AsyncTask<String, String, String> {
        private String zp = "";
        @Override
        protected String doInBackground(String... params) {
            try {
                zp = params[0];
                URL url = new URL(String.format("http://congress.api.sunlightfoundation.com/legislators/locate?zip=%1$s&apikey=%2$s", zp , getString(R.string.sunshine_API_key)));
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
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR1";
            }
            Log.d("ABCD", response);
            Intent lookUp = new Intent(getBaseContext(), Search.class);
            lookUp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            lookUp.putExtra("json", response);
            lookUp.putExtra("zp", zp);
            lookUp.putExtra("loc", "");
            startActivity(lookUp);
        }
    }
}
