package com.johnstrack.wtf.data;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.johnstrack.wtf.activities.AddReviewActivity;
import com.johnstrack.wtf.activities.AddTruck;
import com.johnstrack.wtf.activities.FoodTrucksListActivity;
import com.johnstrack.wtf.activities.ModifyActivity;
import com.johnstrack.wtf.activities.ReviewsActivity;
import com.johnstrack.wtf.constants.Constants;
import com.johnstrack.wtf.model.FoodTruck;
import com.johnstrack.wtf.model.FoodTruckReview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by John on 9/29/2017.
 */

public class DataService {

    private static DataService instance = new DataService();

    public static DataService getInstance() {
        return instance;
    }

    private DataService() {
    }

    // Request all the FoodTrucks

    public ArrayList<FoodTruck> downloadAllFoodTrucks (Context context, final FoodTrucksListActivity.TrucksDownloaded listener) {

        String url = Constants.GET_FOOD_TRUCKS;
        final ArrayList<FoodTruck> foodTruckList = new ArrayList<>();

        final JsonArrayRequest getTrucks = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    JSONArray foodTrucks = response;
                    for (int i = 0; i < foodTrucks.length(); i++) {
                        JSONObject foodTruck = foodTrucks.getJSONObject(i);
                        String name = foodTruck.getString("name");
                        String id = foodTruck.getString("_id");
                        String  foodType = foodTruck.getString("foodtype");
                        Double avgCost = foodTruck.getDouble("avgcost");

                        JSONObject geometry = foodTruck.getJSONObject("geometry");
                        JSONObject coordinates = geometry.getJSONObject("coordinates");
                        Double latitude = coordinates.getDouble("lat");
                        Double longitude = coordinates.getDouble("long");

                        FoodTruck newFoodTruck = new FoodTruck(id, name, foodType, avgCost, latitude, longitude);
                        foodTruckList.add(newFoodTruck);
                    }
                } catch (JSONException e){
                    Log.v("JSON", "EXC" + e.getLocalizedMessage());
                }
                listener.success(true);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("API", "Err" + error.getLocalizedMessage());
            }
        });

        Volley.newRequestQueue(context).add(getTrucks);
        return foodTruckList;
    }

    // Request all the FoodTruck Reviews
    public ArrayList<FoodTruckReview> downloadReviews (Context context, FoodTruck foodTruck, final ReviewsActivity.ReviewInterface listener) {

        String url = Constants.GET_REVIEWS + foodTruck.getId();
        final ArrayList<FoodTruckReview> reviewList = new ArrayList<>();

        final JsonArrayRequest getReviews = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println(response.toString());

                try {
                    JSONArray reviews = response;
                    for (int i = 0; i < reviews.length(); i++) {
                        JSONObject review = reviews.getJSONObject(i);
                        String title = review.getString("title");
                        String id = review.getString("_id");
                        String  text = review.getString("text");

                        FoodTruckReview newFoodTruckReview = new FoodTruckReview(id, title, text);
                        reviewList.add(newFoodTruckReview);
                    }
                } catch (JSONException e){
                    Log.v("JSON", "EXC" + e.getLocalizedMessage());
                }
                //System.out.println("This is the food truck name: " + foodTruckList.get(0).getName());
                listener.success(true);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("API", "Err" + error.getLocalizedMessage());
            }
        });

        Volley.newRequestQueue(context).add(getReviews);
        return reviewList;
    }

    // AddReview Post
    public void addReview(String title, String text, FoodTruck foodTruck, Context context, final AddReviewActivity.AddReviewInterface listener, String authToken) {

        try {
            System.out.println("Add Button tapped...");
            String url = Constants.ADD_REVIEW + foodTruck.getId();
            System.out.println(url);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("title", title);
            jsonBody.put("text", text);
            jsonBody.put("foodtruck", foodTruck.getId());
            final String mRequestBody = jsonBody.toString();
            final String bearer = "Bearer " + authToken;
            System.out.println(mRequestBody);
            System.out.println(bearer);

            JsonObjectRequest reviewRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String message = response.getString("message");
                        Log.i("JSON Message", message);
                    } catch (JSONException e){
                        Log.v("JSON", "Exception: " + e.getLocalizedMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported encoding", mRequestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    if (response.statusCode == 200) {
                        listener.success(true);
                    }
                    return super.parseNetworkResponse(response);
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", bearer);

                    return headers;
                }
            };


            Volley.newRequestQueue(context).add(reviewRequest);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    // Add Truck Post
    public void addTruck(String name, String foodType, Double avgCost, Double latitude, Double longitude, Context context, final AddTruck.AddTruckInterface listener, String authToken) {

        try {
            String url = Constants.ADD_TRUCK;

            JSONObject geometry = new JSONObject();
            JSONObject coordinates = new JSONObject();
            coordinates.put("lat", latitude);
            coordinates.put("long", longitude);
            geometry.putOpt("coordinates", coordinates);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", name);
            jsonBody.put("foodtype", foodType);
            jsonBody.put("avgcost", avgCost);
            jsonBody.put("geometry", geometry);

            final String mRequestBody = jsonBody.toString();
            final String bearer = "Bearer " + authToken;

            JsonObjectRequest addTruck = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String message = response.getString("message");
                        Log.i("JSON Message", message);
                    } catch (JSONException e){
                        Log.v("JSON", "Exception: " + e.getLocalizedMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported encoding", mRequestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    if (response.statusCode == 200) {
                        listener.success(true);
                    }
                    return super.parseNetworkResponse(response);
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", bearer);

                    return headers;
                }
            };


            Volley.newRequestQueue(context).add(addTruck);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    // Modify Truck Post
    public void modifyTruck(FoodTruck foodTruck, Context context, final ModifyActivity.ModifyTruckInterface listener, String authToken) {

        try {
            String url = Constants.MODIFY_TRUCK + foodTruck.getId();

            JSONObject geometry = new JSONObject();
            JSONObject coordinates = new JSONObject();
            coordinates.put("lat", foodTruck.getLatitude());
            coordinates.put("long", foodTruck.getLongitude());
            geometry.putOpt("coordinates", coordinates);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", foodTruck.getName());
            jsonBody.put("foodtype", foodTruck.getFoodType());
            jsonBody.put("avgcost", foodTruck.getAvgCost());
            jsonBody.put("geometry", geometry);

            final String mRequestBody = jsonBody.toString();
            final String bearer = "Bearer " + authToken;

            JsonObjectRequest addTruck = new JsonObjectRequest(Request.Method.PUT, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String message = response.getString("message");
                        Log.i("JSON Message", message);
                    } catch (JSONException e){
                        Log.v("JSON", "Exception: " + e.getLocalizedMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported encoding", mRequestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    if (response.statusCode == 200) {
                        listener.success(true);
                    }
                    return super.parseNetworkResponse(response);
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", bearer);

                    return headers;
                }
            };

            Volley.newRequestQueue(context).add(addTruck);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}