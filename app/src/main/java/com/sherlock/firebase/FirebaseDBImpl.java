package com.sherlock.firebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.sherlock.database.data.StoreCoordinate;
import com.sherlock.database.data.StoreProduct;
import com.sherlock.firebase.data.RTDatabaseBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirebaseDBImpl {

    private static final FirebaseDBImpl INSTANCE = new FirebaseDBImpl();
    public static final String MYDATABASE = "MYDATABASE";

    public static synchronized FirebaseDBImpl getInstance() {
        return INSTANCE;
    }

    public static void cacheDatabaseToLocal(Context context, View view) {

        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String msg = String.valueOf(dataSnapshot.getValue());
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(MYDATABASE, msg);
                editor.apply();

                Snackbar.make(view," Stored database to local ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("firebase", "Error getting data", databaseError.toException());
            }
        });

    }

    private static RTDatabaseBean getDB(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String mydatabase = preferences.getString(MYDATABASE, null);
        if (mydatabase == null) {
            return null;
        }
        Gson gson = new Gson();
        return gson.fromJson(mydatabase, RTDatabaseBean.class);
    }

    public static Map<String, StoreCoordinate> getStoreAisleCoordinates(Context context) {
        RTDatabaseBean db = getDB(context);
        if(db == null) {
            return null;
        }
        return db.getStoreAisles();
    }

    public static List<String> getPreferredProductNames(String aisleName, Context context) {
        Map<String, List<StoreProduct>> preferredProducts = getPreferredProducts(context);
        if(preferredProducts == null) {
            return null;
        }
        List<StoreProduct> storeProducts = preferredProducts.get(aisleName);
        if(storeProducts == null || storeProducts.isEmpty()) {
            return null;
        }
        List<String> product = new ArrayList<>();
        for(StoreProduct storeProduct : storeProducts) {
            product.add(storeProduct.getProductName());
        }
        return product;
    }

    public static Map<String, List<StoreProduct>> getPreferredProducts(Context context) {
        RTDatabaseBean db = getDB(context);
        if(db == null) {
            return null;
        }
        return db.getPreferredProducts();
    }


}
