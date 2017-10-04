package com.johnstrack.wtf.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.johnstrack.wtf.R;
import com.johnstrack.wtf.activities.FoodTrucksListActivity;
import com.johnstrack.wtf.holder.FoodTruckHolder;
import com.johnstrack.wtf.model.FoodTruck;

import java.util.ArrayList;

/**
 * Created by John on 9/28/2017.
 */

public class FoodTruckAdapter extends RecyclerView.Adapter<FoodTruckHolder>{

    private ArrayList<FoodTruck> trucks;

    public FoodTruckAdapter(ArrayList<FoodTruck> trucks) {
        this.trucks = trucks;
    }

    @Override
    public void onBindViewHolder(FoodTruckHolder holder, int position) {
        final  FoodTruck truck = trucks.get(position);
        holder.updateUI(truck);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FoodTrucksListActivity.getFoodTrucksListActivity().loadFoodTruckDetailActivity(truck);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trucks.size();
    }

    @Override
    public FoodTruckHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View truckCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_foodtruck, parent, false);
        return new FoodTruckHolder(truckCard);
    }
}
