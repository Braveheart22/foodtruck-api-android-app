package com.johnstrack.wtf.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.johnstrack.wtf.R;
import com.johnstrack.wtf.model.FoodTruck;

import org.w3c.dom.Text;

/**
 * Created by John on 9/28/2017.
 */

public class FoodTruckHolder extends RecyclerView.ViewHolder {

    private TextView truckName;
    private TextView foodType;
    private TextView avgCost;

    public FoodTruckHolder(View itemView) {
        super(itemView);

        this.truckName = (TextView) itemView.findViewById(R.id.food_truck_name);
        this.foodType = (TextView) itemView.findViewById(R.id.food_truck_type);
        this.avgCost = (TextView) itemView.findViewById(R.id.food_truck_cost);
    }

    public void updateUI(FoodTruck truck) {
        truckName.setText(truck.getName());
        foodType.setText(truck.getFoodType());
        avgCost.setText("$" + truck.getAvgCost());
    }
}
