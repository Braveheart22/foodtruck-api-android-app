package com.johnstrack.wtf.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.johnstrack.wtf.R;
import com.johnstrack.wtf.constants.Constants;
import com.johnstrack.wtf.data.DataService;
import com.johnstrack.wtf.model.FoodTruck;

public class ModifyActivity extends AppCompatActivity {

    private FoodTruck foodTruck;
    private TextView truckName;
    private TextView foodType;
    private TextView avgCost;
    private TextView latitude;
    private TextView longitde;

    private Button modifyBtn;
    private Button cancelBtn;
    String authToken;
    SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        truckName = (TextView) findViewById(R.id.truck_name);
        foodType = (TextView) findViewById(R.id.food_type);
        avgCost = (TextView) findViewById(R.id.avg_cost);
        latitude = (TextView) findViewById(R.id.latitude);
        longitde = (TextView) findViewById(R.id.longitude);

        modifyBtn = (Button) findViewById(R.id.modify_btn);
        cancelBtn = (Button) findViewById(R.id.cancel_btn);

        foodTruck = getIntent().getParcelableExtra(FoodTrucksListActivity.EXTRA_ITEM_TRUCK);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        authToken = prefs.getString(Constants.AUTH_TOKEN, "Does not exist");

        final ModifyTruckInterface listener = new ModifyTruckInterface() {
            @Override
            public void success(Boolean success) {
                finish();
            }
        };

        updateUI();

        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                foodTruck.setName(truckName.getText().toString());
                foodTruck.setFoodType(foodType.getText().toString());
                foodTruck.setAvgCost(Double.parseDouble(avgCost.getText().toString()));
                foodTruck.setLatitude(Double.parseDouble(latitude.getText().toString()));
                foodTruck.setLongitude(Double.parseDouble(longitde.getText().toString()));

                System.out.println("(Before going back to the detail)The Food Truck name is: " + foodTruck.getName());

                DataService.getInstance().modifyTruck(foodTruck, getBaseContext(), listener, authToken);

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void updateUI () {
        truckName.setText(foodTruck.getName());
        foodType.setText(foodTruck.getFoodType());
        avgCost.setText(Double.toString(foodTruck.getAvgCost()));
        latitude.setText(Double.toString(foodTruck.getLatitude()));
        longitde.setText(Double.toString(foodTruck.getLongitude()));
    }

    public interface ModifyTruckInterface {
        void success (Boolean success);
    }
}

