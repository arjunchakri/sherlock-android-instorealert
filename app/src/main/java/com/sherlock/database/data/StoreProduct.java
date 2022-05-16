package com.sherlock.database.data;

public class StoreProduct {

    String productName;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public StoreProduct(String productName) {
        this.productName = productName;
    }
}
