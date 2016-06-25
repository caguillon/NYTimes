package com.example.caguillon.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.caguillon.nytimessearch.R;

public class FilterActivity extends AppCompatActivity {
//Check this code; not sure it's right...

    private EditText begin_date;
    //private Spinner sort;
    //private String news_desk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        //begin_date = (EditText) findViewById(R.id.etDate);
        //sort = (Spinner) findViewById(R.id.spinner);
        //news_desk = () findViewById(R.id.);

    }

    public void onSubmit(View view) {
        Intent intent = new Intent();

        //intent.putExtra("date", (Parcelable) begin_date);

        setResult(RESULT_OK, intent);
        finish();
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.cbArts:
                if (checked) {
                    Toast.makeText(this, "Arts has been clicked", Toast.LENGTH_LONG).show();
                    // Lookup Arts
                }
                else {
                    // Don't lookup Arts
                }
                break;
            case R.id.cbFashionAndStyle:
                if (checked) {
                    Toast.makeText(this, "Fashion & Style has been clicked", Toast.LENGTH_LONG).show();
                    // Lookup Fashion & Style
                }
                else {
                    // Don't lookup Fashion & Style
                }
                break;
            case R.id.cbSports:
                if (checked) {
                    Toast.makeText(this, "Sports has been clicked", Toast.LENGTH_LONG).show();
                    // Lookup Sports
                }
                else {
                    // Don't lookup Sports
                }
                break;
        }
    }

}
