package com.example.yang.represent;

/**
 * Created by Yang on 3/3/2016.
 */

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

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
            i.putExtra("rep", value);
            startActivity(i);
            // so you may notice this crashes the phone because it's
            //''sending message to a Handler on a dead thread''... that's okay. but don't do this.
            // replace sending a toast with, like, starting a new activity or something.
            // who said skeleton code is untouchable? #breakCSconceptions

        } else if (messageEvent.getPath().equalsIgnoreCase("/cong")) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Intent i = new Intent(this, Search.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("Zip_Code", value);
            startActivity(i);
        }
        else {
            super.onMessageReceived( messageEvent );
        }

    }
}
