package com.example.yang.represent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Detail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Representative mark = new Representative("Mark Warner", "Democrat", "Sen.", "November 11, 2020","About to go live w/ " +
                "@RepMcCaul on @WolfBlitzer @CNN discussing our commission on #digitalsecurity & #privacy", R.drawable.ex1,"https://www.mwarner.com","mark@warner.com");

        Representative tim = new Representative("Tim Kaine", "Democrat", "Sen.", "November 11, 2018","I'll be on @NPR at 8:20pm to discuss the " +
                "#SuperTuesday results in Virginia. Tune in!", R.drawable.ex2,"timkaine.com","tim@akine.com");

        Representative barb = new Representative("Barbara Comstock", "Republican", "Rep.", "November 11, 2016", "Be sure to get to your regular polling " +
                "place before 7PM tonight to cast your vote! ", R.drawable.ex3,"https://comstock.house.gov","xxx@comstock.com");
        String markComm[] = {"Senate Committee on Banking, Housing, and Urban Affairs", "Senate Committee on the Budget",
                "Senate Committee on Finance", "Senate Committee on Rules and Administration", "Senate Select Committee on Intelligence"};
        String timComm[] = {"Senate Committee on Armed Services", "Senate Committee on the Budget", "Senate Committee on Foreign Relations", "Senate Special Committee on Aging"};
        String barbComm[] = {"House Committee on Science, Space, and Technology", "House Committee on House Administration", "House Committee on Transportation and Infrastructure"};

        String barbBill[] = {"H.R. 4102: Student Loan Relief Act of 2015 \n\t " +
                "Nov 19, 2015", "H.R. 3585: Surface Transportation Research and Development Act of 2015 \n\t" +
                "Sep 22, 2015", "H.R. 1119: Research and Development Efficiency Act \n\t" +
                "May 19, 2015"};



        //get info
        Bundle extras = getIntent().getExtras();
        String name = "";
        if (extras != null) {
            name = extras.getString("rep");
        }
        //assign dummmy rep, refactor later.
        Representative dummy;
        if (name.equals(mark.getFullName())){
            dummy = mark;
        } else if (name.equals(tim.getFullName())){
            dummy = tim;
        } else {
            dummy = barb;
        }
        //set picture
        ImageView pic = (ImageView)findViewById(R.id.headShot);
        pic.setImageResource(dummy.getPicAdress());
        //set name and backdrop
        ImageView bg = (ImageView)findViewById(R.id.backdrop);
        TextView title = (TextView)findViewById(R.id.name);
        if (dummy.getParty().equals("Democrat")) {
            bg.setImageResource(R.drawable.donkey);
            title.setText(dummy.getPosition()+" "+name+" (D)");
        } else {
            bg.setImageResource(R.drawable.elephant);
            title.setText(dummy.getPosition() + " " + dummy.getFullName() + " (R)");
        }
        //set Term
        TextView termEnd = (TextView)findViewById(R.id.term);
        termEnd.setText("Term ends: "+dummy.getTermEnd());
        //fill in comm
        LinearLayout comm = ((LinearLayout)findViewById(R.id.commitee));
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                (getApplicationContext().LAYOUT_INFLATER_SERVICE);
        if (name.equals(mark.getFullName())){
            for (int i = 0; i < markComm.length; i++){
                View to_add = inflater.inflate(R.layout.subpoints,comm,false);
                ((TextView)to_add.findViewById(R.id.c1)).setText("- "+markComm[i]);
                comm.addView(to_add, 0);
            }
        } else if (name.equals(tim.getFullName())){
            for (int i = 0; i < timComm.length; i++){
                View to_add = inflater.inflate(R.layout.subpoints,comm,false);
                ((TextView)to_add.findViewById(R.id.c1)).setText("- "+timComm[i]);
                comm.addView(to_add, 0);
            }
        } else {
            for (int i = 0; i < barbComm.length; i++){
                View to_add = inflater.inflate(R.layout.subpoints,comm,false);
                ((TextView)to_add.findViewById(R.id.c1)).setText("- "+barbComm[i]);
                comm.addView(to_add, 0);
            }
        }
        //fill in bills
        LinearLayout bills = ((LinearLayout)findViewById(R.id.bill));
        LayoutInflater inflater2 = (LayoutInflater)getApplicationContext().getSystemService
                (getApplicationContext().LAYOUT_INFLATER_SERVICE);
        if (name.equals(mark.getFullName())){
            for (int i = 0; i < barbBill.length; i++){
                View to_add = inflater2.inflate(R.layout.subpoints,bills,false);
                ((TextView)to_add.findViewById(R.id.c1)).setText("- "+barbBill[i]);
                bills.addView(to_add, 0);
            }
        } else if (name.equals(tim.getFullName())){
            for (int i = 0; i < barbBill.length; i++){
                View to_add = inflater2.inflate(R.layout.subpoints,bills,false);
                ((TextView)to_add.findViewById(R.id.c1)).setText("- "+barbBill[i]);
                bills.addView(to_add, 0);
            }
        } else {
            for (int i = 0; i < barbBill.length; i++){
                View to_add = inflater2.inflate(R.layout.subpoints,bills,false);
                ((TextView)to_add.findViewById(R.id.c1)).setText("- "+barbBill[i]);
                bills.addView(to_add, 0);
            }
        }




    }

}
