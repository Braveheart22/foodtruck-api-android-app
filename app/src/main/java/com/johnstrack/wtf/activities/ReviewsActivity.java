package com.johnstrack.wtf.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.johnstrack.wtf.R;
import com.johnstrack.wtf.adapter.FoodTruckAdapter;
import com.johnstrack.wtf.adapter.ReviewAdapter;
import com.johnstrack.wtf.data.DataService;
import com.johnstrack.wtf.model.FoodTruck;
import com.johnstrack.wtf.model.FoodTruckReview;
import com.johnstrack.wtf.view.itemDecorator;

import java.util.ArrayList;

public class ReviewsActivity extends AppCompatActivity {

    // Variables
    private FoodTruck foodTruck;
    private ArrayList<FoodTruckReview> reviews = new ArrayList<>();
    private ReviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        foodTruck = getIntent().getParcelableExtra(FoodTrucksListActivity.EXTRA_ITEM_TRUCK);
        System.out.println(foodTruck.getName());

        ReviewInterface listener = new ReviewInterface() {
            @Override
            public void success(Boolean success) {
                if (success) {
                    System.out.println("Reviews go here...");
                    setUpRecycler();
                    if (reviews.size() == 0) {
                        Toast.makeText(getBaseContext(), "No reviews for this Food Truck", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        reviews = DataService.getInstance().downloadReviews(this, foodTruck, listener);

    }

    public interface ReviewInterface {
        void success (Boolean success);
    }

    private void setUpRecycler() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_reviews);
        recyclerView.setHasFixedSize(true);
        adapter = new ReviewAdapter(reviews);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new itemDecorator(0, 0, 0, 10));
    }
}
