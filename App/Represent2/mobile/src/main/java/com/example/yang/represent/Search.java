package com.example.yang.represent;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class Search extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private String zp = "";
    private String real_zp = "";
    private String JSON_String = "";
    private String[] rep_list;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;
    private String location_check;

    @Override
     public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        String ss = "";
        //Get the ZIP code from previous call
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            zp = extras.getString("zp");
            JSON_String = extras.getString("json");
            location_check = extras.getString("loc");
           // ss = extras.getString("ss");
        }
        ((EditText)findViewById(R.id.zipCode)).setText(zp);
//        Log.d("FINAL", zp);
//        if (ss == null)
//            Log.d("FINAL", "SS is null");
//        else
//            Log.d("FINAL", ss);
//        if (ss!=null && !ss.isEmpty()) {
//            Log.d("FINAL","started SS");
//            zp = ((EditText)findViewById(R.id.zipCode)).getText().toString();
//            Log.d("FINAL",zp);
//            if( zp != null && !zp.isEmpty()) {
//                String API_Call = String.format("http://congress.api.sunlightfoundation.com/legislators/locate?zip=%1$s&apikey=%2$s", zp , getString(R.string.sunshine_API_key));
//                location_check = null;
//                new Sunshine().execute(API_Call);
//            }
//        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                        //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        new load_in().execute(JSON_String);
    }
    public void location_look(View v) {
        String API_Call = String.format("http://congress.api.sunlightfoundation.com/legislators/locate?latitude=%1$s&longitude=%2$s&apikey=%3$s",
                currentLatitude, currentLongitude, getString(R.string.sunshine_API_key));

        Log.d("ABCD", API_Call + " from location_look");
        location_check = "y";
        zp = "Here!";
        new Sunshine().execute(API_Call);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Now lets connect to the API
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");

        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }


    /**
     * If connected get lat and long
     *
     */
    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();

            Toast.makeText(this, "Loading!", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
            /*
             * Google Play services can resolve some errors it detects.
             * If the error has a resolution, try sending an Intent to
             * start a Google Play services activity that can resolve
             * error.
             */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                    /*
                     * Thrown if Google Play services canceled the original
                     * PendingIntent
                     */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
                /*
                 * If no resolution is available, display a dialog to the
                 * user with the error.
                 */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /**
     * If locationChanges change lat and long
     *
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

    }


    //onclick method to browse website
    private View.OnClickListener browse = new View.OnClickListener() {
        public void onClick(View v) {
           String url = ((TextView) ((View) v.getParent()).findViewById(R.id.website)).getText().toString();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    };


    private View.OnClickListener mailTo = new View.OnClickListener() {
        public void onClick(View v) {
            String emailAddress = ((TextView) ((View) v.getParent()).findViewById(R.id.emailaddress)).getText().toString();
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.setType("text/plain");
            String[] mailList = new String[1];
            mailList[0] = emailAddress;
            emailIntent.putExtra(emailIntent.EXTRA_EMAIL, mailList);
            startActivity(emailIntent.createChooser(emailIntent, "Send email..."));
        }
    };

    private View.OnClickListener deets = new View.OnClickListener() {
        public void onClick(View v) {
            Intent lookUp = new Intent(getBaseContext(), Detail.class);
            String bio = ((TextView)(v.findViewById(R.id.bio))).getText().toString();
            String term = ((TextView)(v.findViewById(R.id.term))).getText().toString();
            String name = ((TextView)(v.findViewById(R.id.name))).getText().toString();
            String party = ((TextView)(v.findViewById(R.id.party))).getText().toString();
            ImageView img = ((ImageView)(v.findViewById(R.id.pic)));
            img.buildDrawingCache();
            Bitmap image= img.getDrawingCache();
            Bundle extras = new Bundle();
            lookUp.putExtra("bio", bio);
            Log.d("ABCD", "Starting detail view");
            startActivity(lookUp);
        }
    };

    public void search(View v) {
         zp = ((EditText)findViewById(R.id.zipCode)).getText().toString();
        if( zp != null && !zp.isEmpty()) {
            String API_Call = String.format("http://congress.api.sunlightfoundation.com/legislators/locate?zip=%1$s&apikey=%2$s", zp , getString(R.string.sunshine_API_key));
            location_check = null;
            new Sunshine().execute(API_Call);
        }
    }

    class Sunshine extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
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
            lookUp.putExtra("json", response);
            lookUp.putExtra("zp", zp);
            lookUp.putExtra("loc", location_check);
            startActivity(lookUp);
        }
    }

    class load_in extends AsyncTask<String, String, String> {

        Bitmap[] bm = null;
        @Override
        protected String doInBackground(String... params) {
                String JSON_String = params[0];
            try {
                JSONObject json_obj = new JSONObject(JSON_String);
                JSONArray people = json_obj.getJSONArray("results");
                int num_people = people.length();
                bm = new Bitmap[num_people];
                for (int i = 0; i < num_people; i++) {
                    final JSONObject rep = people.getJSONObject(i);
                    Bitmap image = null;
                    int inSampleSize = 0;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    options.inSampleSize = inSampleSize;
                    try
                    {
                        URL url = new URL(String.format("https://theunitedstates.io/images/congress/original/%1$s.jpg",rep.getString("bioguide_id")));
                        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                        InputStream stream = connection.getInputStream();
                        image = BitmapFactory.decodeStream(stream, null, options);
                        options.inJustDecodeBounds = false;
                        connection = (HttpURLConnection)url.openConnection();
                        stream = connection.getInputStream();
                        image = BitmapFactory.decodeStream(stream, null, options);
                        bm[i] = image;
                    } catch(Exception e) {
                        Log.e("ABCD", "1:"+e.toString());
                    }

                }
            } catch (Exception e) {
                Log.d("ABCD", e.toString());
            }
            return JSON_String;
        }
        protected void onPostExecute(String response) {
            int num_people = 0;
            JSONObject json_obj = null;
            JSONArray people = null;
            try {
                json_obj = new JSONObject(response);
                people = json_obj.getJSONArray("results");
                num_people = people.length();
                rep_list = new String[num_people * 2];
            } catch (JSONException e) {
                Log.d("ABCD", "ON_POST_EXECUTE");
            }
            for (int i = 0; i < num_people; i++) {

                try {
                    final JSONObject rep = people.getJSONObject(i);
                    final Bitmap img = bm[i];
                    rep_list[2*i] = rep.getString("title")+" " +rep.getString("first_name")+" "+rep.getString("last_name")+" ("+rep.getString("party")+")";
                    rep_list[2*i+1] = rep.getString("bioguide_id");
                    TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
                        @Override
                        public void success(Result<AppSession> appSessionResult) {

                            AppSession session = appSessionResult.data;
                            TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
                            try {
                                twitterApiClient.getStatusesService().userTimeline(null, rep.getString("twitter_id"), 1, null, null, false,
                                        false, false, true, new Callback<List<Tweet>>() {
                                            @Override
                                            public void success(Result<List<Tweet>> listResult) {
                                                String t = listResult.data.get(0).text;
                                                try {
                                                    Representative temp = new Representative(rep.getString("first_name") + " " + rep.getString("last_name"), rep.getString("party"),
                                                            rep.getString("title"), rep.getString("term_end"), t, "http://theunitedstates.io/images/congress/original/C001105.jpg",
                                                            rep.getString("website"), rep.getString("oc_email"),rep.getString("bioguide_id"));
                                                    populate(temp, (LinearLayout) findViewById(R.id.repList), img);
                                                } catch (JSONException a) {
                                                    Log.d("ABCD", "When populating");
                                                }
                                            }
                                            @Override
                                            public void failure(TwitterException e) {
                                                e.printStackTrace();
                                            }
                                        });

                            } catch (JSONException a) {
                                Log.d("ABCD", "When populating");
                            }
                        }

                        @Override
                        public void failure(TwitterException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (JSONException e) {
                    Log.d("ABCD", "error in twitter");
                }
            }

            new Reverse_Geo().execute();
        }



        public void populate(final Representative person, LinearLayout parent, Bitmap img){
            final Representative gov = person;
            LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                    (getApplicationContext().LAYOUT_INFLATER_SERVICE);

            View to_add = inflater.inflate(R.layout.congressional,
                    parent,false);


            TextView name = (TextView)to_add.findViewById(R.id.name);
            String fill = gov.getPosition() + " " + gov.getFullName();
            name.setText(fill);
            if (fill.length() >= 20) {
                name.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
            }

            TextView tweet = (TextView)to_add.findViewById(R.id.tweet);
            tweet.setText(gov.getTweet());

            TextView web = (TextView) to_add.findViewById(R.id.website);
            web.setText(gov.getWebAdress());

            TextView email = (TextView) to_add.findViewById(R.id.emailaddress);
            email.setText(gov.getEmailAdress());

            TextView bio = (TextView) to_add.findViewById(R.id.bio);
            bio.setText(gov.getBio());

            TextView term = (TextView)to_add.findViewById(R.id.term);
            term.setText(gov.getTermEnd());

            TextView party = (TextView)to_add.findViewById(R.id.party);
            party.setText(gov.getParty());

            ImageView prof = (ImageView) to_add.findViewById(R.id.pic);
            prof.setImageBitmap(img);

            ImageButton website= (ImageButton)to_add.findViewById(R.id.web);
            website.setOnClickListener(browse);

            ImageButton mail= (ImageButton)to_add.findViewById(R.id.email);
            mail.setOnClickListener(mailTo);



            parent.addView(to_add, 0);

            if (gov.getParty().equals("D")) {
                to_add.setBackgroundColor(Color.parseColor("#232066"));
            } else if (gov.getParty().equals("R")) {
                to_add.setBackgroundColor(Color.parseColor("#970F05"));
            }
            final View shade = to_add;
            shade.setOnClickListener(deets);
            to_add.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {

                    if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        if (gov.getParty().equals("D")) {
                            shade.setBackgroundColor(Color.parseColor("#232066"));
                        } else if (gov.getParty().equals("R")) {
                            shade.setBackgroundColor(Color.parseColor("#970F05"));
                        }
                    } else if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        if (gov.getParty().equals("D")) {
                            shade.setBackgroundColor(Color.parseColor("#0e0c28"));
                        } else if (gov.getParty().equals("R")) {
                            shade.setBackgroundColor(Color.parseColor("#4b0702"));
                        }

                    } else if (event.getAction() == MotionEvent.ACTION_UP) {

                        if (gov.getParty().equals("D")) {
                            shade.setBackgroundColor(Color.parseColor("#232066"));
                        } else if (gov.getParty().equals("R")) {
                            shade.setBackgroundColor(Color.parseColor("#970F05"));
                        }
                        shade.performClick();
                    }
                    return true;
                }
            });

        }



    }


    class Reverse_Geo extends AsyncTask<String, String, String> {

        private String county, locality;
        private String state;
        @Override
        protected String doInBackground(String... params) {
            Log.d("ABCD", "Location_check is" + location_check);
            Log.d("ABCD", "zp is" + zp);
            if (location_check != null && location_check.equals("y")) {
                try {
                    URL url = new URL(String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%1$s&key=%2$s", currentLatitude + "," + currentLongitude,
                            getString(R.string.google_API_key)));
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
                    Log.e("ERROR", e.getMessage(), e);
                    return null;
                }
            }
            else {
                try {
                    URL url = new URL(String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%1$s&key=%2$s", zp,
                            getString(R.string.google_API_key)));
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
                    Log.e("ERROR", e.getMessage(), e);
                    return null;
                }
            }
        }

        protected void onPostExecute(String response) {
            if (response == null) {
                return;
            }
            try {
                JSONObject json_obj = new JSONObject(response);
                Log.d("ABCD", json_obj.toString());
                JSONArray locations = json_obj.getJSONArray("results");
                JSONArray checker = locations.getJSONObject(0).getJSONArray("address_components");
                Log.d("ABCD", checker.toString());
                for (int i = 0; i < checker.length(); i++) {
                    if ((checker.getJSONObject(i)).getJSONArray("types").getString(0).equals("locality")) {
                        locality = (checker.getJSONObject(i)).getString("short_name");
                    }else if ((checker.getJSONObject(i)).getJSONArray("types").getString(0).equals("administrative_area_level_2")) {
                        county = (checker.getJSONObject(i)).getString("short_name");
                    } else if ((checker.getJSONObject(i)).getJSONArray("types").getString(0).equals("administrative_area_level_1")) {
                        state = (checker.getJSONObject(i)).getString("short_name");
                    } else if ((checker.getJSONObject(i)).getJSONArray("types").getString(0).equals("postal_code")) {
                        real_zp = (checker.getJSONObject(i)).getString("short_name");
                    }
                }
                Log.d("ABCD", county + ", " + state);
            } catch (JSONException e) {
                Log.d("ABCD", e.toString());
            }
            Intent messageIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
            messageIntent.putExtra("Zip_Code", county+", "+state);
            if (county == null || county.equals("null")) {
                messageIntent.putExtra("Zip_Code", locality+", "+state);
            }
            messageIntent.putExtra("rep_list", rep_list);

            Log.d("ABCD", "Starting message service");
            startService(messageIntent);
        }

    }



}




