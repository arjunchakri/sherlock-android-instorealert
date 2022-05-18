package com.sherlock.firebase.data;

import com.sherlock.database.data.StoreCoordinate;
import com.sherlock.database.data.StoreProduct;

import java.util.List;
import java.util.Map;

public class RTDatabaseBean {

    String testkey;
    Map<String, StoreCoordinate> storeAisles;
    Map<String, List<StoreProduct>> preferredProducts;

    public String getTestkey() {
        return testkey;
    }

    public void setTestkey(String testkey) {
        this.testkey = testkey;
    }

    public Map<String, StoreCoordinate> getStoreAisles() {
        return storeAisles;
    }

    public void setStoreAisles(Map<String, StoreCoordinate> storeAisles) {
        this.storeAisles = storeAisles;
    }

    public RTDatabaseBean() {
    }

    @Override
    public String toString() {
        return "RTDatabaseBean{" +
                "testkey='" + testkey + '\'' +
                ", storeAisles=" + storeAisles +
                ", preferredProducts=" + preferredProducts +
                '}';
    }

    public Map<String, List<StoreProduct>> getPreferredProducts() {
        return preferredProducts;
    }

    public void setPreferredProducts(Map<String, List<StoreProduct>> preferredProducts) {
        this.preferredProducts = preferredProducts;
    }

    public RTDatabaseBean(String testkey, Map<String, StoreCoordinate> storeAisles, Map<String, List<StoreProduct>> preferredProducts) {
        this.testkey = testkey;
        this.storeAisles = storeAisles;
        this.preferredProducts = preferredProducts;
    }
}
