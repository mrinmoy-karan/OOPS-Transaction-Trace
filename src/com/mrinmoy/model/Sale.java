package com.mrinmoy.model;

public class Sale extends Transaction {
    public Sale(String id, double amount, String method) {
        super(id, amount, "SALE", method);
    }
}