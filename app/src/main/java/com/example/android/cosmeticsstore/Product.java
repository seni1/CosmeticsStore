package com.example.android.cosmeticsstore;

import java.io.Serializable;

public class Product implements Serializable {

    private int id;
    private String name;
    private String price;
    private int quantity;
    private String supplierName;
    private String supplierPhone;

    public Product(int id, String name, String price, int quantity, String supplierName, String supplierPhone) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.supplierName = supplierName;
        this.supplierPhone = supplierPhone;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getId() {
        return id;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getSupplierPhone() {
        return supplierPhone;
    }
}
