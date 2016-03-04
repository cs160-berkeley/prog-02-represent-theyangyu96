package com.example.yang.represent;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Search extends AppCompatActivity{
    private Representative[] repArray = new Representative[3];
    private String zp = "";
    @Override
     public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            zp = extras.getString("Zip_Code");
        }
        ((EditText)findViewById(R.id.zipCode)).setText(zp);

        Representative mark = new Representative("Mark Warner", "Democrat", "Sen.", "November 11, 2020","About to go live w/ " +
                "@RepMcCaul on @WolfBlitzer @CNN discussing our commission on #digitalsecurity & #privacy", R.drawable.ex1,"https://www.mwarner.com","mark@warner.com");

        Representative tim = new Representative("Tim Kaine", "Democrat", "Sen.", "November 11, 2018","I'll be on @NPR at 8:20pm to discuss the " +
                "#SuperTuesday results in Virginia. Tune in!", R.drawable.ex2,"timkaine.com","tim@akine.com");

        Representative barb = new Representative("Barbara Comstock", "Republican", "Rep.", "November 11, 2016", "Be sure to get to your regular polling " +
                "place before 7PM tonight to cast your vote! ", R.drawable.ex3,"https://comstock.house.gov","xxx@comstock.com");
        repArray[0] = mark;
        repArray[1] =  tim;
        repArray[2] = barb;
        LinearLayout parent =(LinearLayout) findViewById(R.id.repList);

        populate(mark, parent);
        populate(tim, parent);
        populate(barb, parent);

        String temp[] ={"Sen. Mark Warner","D", "Sen. Tim Kaine", "D", "Rep. Barbara Comstock", "R"};

        Intent messageIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        messageIntent.putExtra("Zip_Code", zp);
        startService(messageIntent);
    }
    //used to populate congressional view with candidate
    public void populate(final Representative person, LinearLayout parent){
        final Representative gov = person;
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                (getApplicationContext().LAYOUT_INFLATER_SERVICE);

        View to_add = inflater.inflate(R.layout.congressional,
                parent,false);

        ImageView pic = (ImageView)to_add.findViewById(R.id.pic);
        pic.setImageResource(gov.getPicAdress());

        TextView name = (TextView)to_add.findViewById(R.id.name);
        name.setText(gov.getPosition() + " " + gov.getFullName());

        TextView tweet = (TextView)to_add.findViewById(R.id.tweet);
        tweet.setText(gov.getTweet());

        TextView web = (TextView)to_add.findViewById(R.id.website);
        web.setText(gov.getWebAdress());

        TextView email = (TextView)to_add.findViewById(R.id.emailaddress);
        email.setText(gov.getEmailAdress());

        ImageButton website= (ImageButton)to_add.findViewById(R.id.web);
        website.setOnClickListener(browse);

        ImageButton mail= (ImageButton)to_add.findViewById(R.id.email);
        mail.setOnClickListener(mailTo);

        parent.addView(to_add, 0);

        if (gov.getParty().equals("Democrat")) {
            to_add.setBackgroundColor(Color.parseColor("#232066"));
        } else if (gov.getParty().equals("Republican")) {
            to_add.setBackgroundColor(Color.parseColor("#970F05"));
        }
        final View shade = to_add;
        shade.setOnClickListener(deets);
        to_add.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (gov.getParty().equals("Democrat")) {
                        shade.setBackgroundColor(Color.parseColor("#232066"));
                    } else if (gov.getParty().equals("Republican")) {
                        shade.setBackgroundColor(Color.parseColor("#970F05"));
                    }
                }
                else if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    if (gov.getParty().equals("Democrat")) {
                        shade.setBackgroundColor(Color.parseColor("#0e0c28"));
                    } else if (gov.getParty().equals("Republican")) {
                        shade.setBackgroundColor(Color.parseColor("#4b0702"));
                    }

                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    if (gov.getParty().equals("Democrat")) {
                        shade.setBackgroundColor(Color.parseColor("#232066"));
                    } else if (gov.getParty().equals("Republican")) {
                        shade.setBackgroundColor(Color.parseColor("#970F05"));
                    }
                    shade.performClick();
                }
                return true;
            }
        });

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
            String name = ((TextView)(v.findViewById(R.id.name))).getText().toString();
            lookUp.putExtra("rep", name.substring(5));
            startActivity(lookUp);
        }
    };

    public void search(View v) {
        Intent lookUp = new Intent(getBaseContext(), Search.class);
        String zp = ((EditText)findViewById(R.id.zipCode)).getText().toString();
        lookUp.putExtra("Zip_Code", zp );
        startActivity(lookUp);
    }
}
