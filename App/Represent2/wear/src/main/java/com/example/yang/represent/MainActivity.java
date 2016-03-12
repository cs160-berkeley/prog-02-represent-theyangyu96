package com.example.yang.represent;

import android.app.Activity;
import android.os.Bundle;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "ofalq8OvCpRkIBUsApLXrwedY";
    private static final String TWITTER_SECRET = "L0lvZy8zO1zUJG2hnmyUoObrw40wTlLqHsrh7UeE6OXjE8pqBg";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);
//        startActivity(new Intent(this, picker.class));
    }
}
