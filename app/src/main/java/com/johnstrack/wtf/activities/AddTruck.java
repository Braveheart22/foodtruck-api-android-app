package com.johnstrack.wtf.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.johnstrack.wtf.R;
import com.johnstrack.wtf.constants.Constants;
import com.johnstrack.wtf.data.DataService;

public class AddTruck extends AppCompatActivity {

    // Variables
    private EditText truckName;
    private EditText foodType;
    private EditText avgCost;
    private EditText latitude;
    private EditText longitude;

    private Button addBtn;
    private Button cancelBtn;
    String authToken;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_truck);

        truckName = (EditText) findViewById(R.id.new_truck_name);
        foodType = (EditText) findViewById(R.id.new_truck_food_type);
        avgCost = (EditText) findViewById(R.id.new_avg_cost);
        latitude = (EditText) findViewById(R.id.new_latitude);
        longitude = (EditText) findViewById(R.id.new_longitude);

        addBtn = (Button) findViewById(R.id.add_truck);
        cancelBtn = (Button) findViewById(R.id.cancel_truck);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        authToken = prefs.getString(Constants.AUTH_TOKEN, "Does not exist");

        final AddTruckInterface listener = new AddTruckInterface() {
            @Override
            public void success(Boolean success) {
                finish();
            }
        };

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = truckName.getText().toString();
                final String type = foodType.getText().toString();
                final Double cost = Double.parseDouble(avgCost.getText().toString());
                final Double lat = Double.parseDouble(latitude.getText().toString());
                final Double longi = Double.parseDouble(longitude.getText().toString());

                DataService.getInstance().addTruck(name, type, cost, lat, longi, getBaseContext(), listener, authToken);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public interface AddTruckInterface {
        void success (Boolean success);
    }
}