package com.mrinmoy.model;

public class Refund extends Transaction {
    public Refund(String id, double amount, String method) {
        super(id, amount, "REFUND", method);
    }
}