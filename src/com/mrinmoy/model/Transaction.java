package com.mrinmoy.model;

public abstract class Transaction {
    protected String id;
    protected double amount;
    protected String type;
    protected String paymentMethod;
    protected String timestamp;

    public Transaction(String id, double amount, String type, String paymentMethod) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.paymentMethod = paymentMethod;
        this.timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String toCsv() {
        return String.format("%s,%s,%s,%.2f,%s", timestamp, id, type, amount, paymentMethod);
    }

}
