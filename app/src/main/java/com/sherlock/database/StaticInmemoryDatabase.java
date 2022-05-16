package com.sherlock.database;

import com.sherlock.database.data.StoreCoordinate;
import com.sherlock.database.data.StoreProduct;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticInmemoryDatabase {

    public static final float STORE_PROXIMITY = 150f;
    public static final float AISLE_PROXIMITY = 20f;

    /*
        This mapping holds the aisle locations of a particular walmart store.
        This will be cross referenced with the user preferences (wishlist) data.
     */
    // PER STORE (for now)
    public static final Map<String, StoreCoordinate> STORE_AILE_COORDINATES = new HashMap<String, StoreCoordinate>() {{
        put("aisle-deli", new StoreCoordinate(36.368060d, -94.225434d));
        put("aisle-pharmacy", new StoreCoordinate(36.368512d, -94.224427d));
        put("aisle-freezer", new StoreCoordinate(36.367334d, -94.224675d));
        put("aisle-clothing", new StoreCoordinate(36.367155d, -94.225202d));
        put("aisle-groceries", new StoreCoordinate(36.368844d, -94.225455d));
    }};

    // PER USER (for now)
    public static final Map<String, List<StoreProduct>> USER_PREFERREDPRODUCTS = new HashMap<String, List<StoreProduct>>() {{
        put("aisle-pharmacy", Arrays.asList(
                new StoreProduct("Benadryl")
        ));
        put("aisle-freezer", Arrays.asList(
                new StoreProduct("Butter"),
                new StoreProduct("Yogurt")
        ));
        put("aisle-groceries", Arrays.asList(
                new StoreProduct("Bread"),
                new StoreProduct("Brown rice")
        ));
    }};

}
