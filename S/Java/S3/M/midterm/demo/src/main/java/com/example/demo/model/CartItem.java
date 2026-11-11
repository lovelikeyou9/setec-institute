package com.example.demo.model;

public class CartItem {
    private Long productId;
    private String productName;
    private double price;
    private int qty;

    public CartItem() {}

    public CartItem(Long productId, String productName, double price, int qty) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.qty = qty;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }

    public double getLineTotal() { return price * qty; }
}
