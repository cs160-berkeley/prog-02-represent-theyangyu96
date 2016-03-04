package com.example.yang.represent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void search(View v) {
        Intent lookUp = new Intent(getBaseContext(), Search.class);
        String zp = ((EditText)findViewById(R.id.zipCode)).getText().toString();
        lookUp.putExtra("Zip_Code", zp );

        startActivity(lookUp);

    }

}
