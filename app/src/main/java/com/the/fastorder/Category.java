package com.the.fastorder;

public class Category {

    private String productTitle;
    private String productIcon;
    private String originalPrice;
    private String productId;
    private String productCategory;
    private String productDiscription;
    private String productQuantaty;
    private String timestemp;
    private String uid;

    public Category() {
    }

    public Category(String productTitle, String productIcon, String originalPrice, String productId, String productCategory, String productDiscription, String productQuantaty,  String timestemp, String uid) {
        this.productTitle = productTitle;
        this.productIcon = productIcon;
        this.originalPrice = originalPrice;
        this.productId = productId;
        this.productCategory = productCategory;
        this.productDiscription = productDiscription;
        this.productQuantaty = productQuantaty;

        this.timestemp = timestemp;
        this.uid = uid;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductIcon() {
        return productIcon;
    }

    public void setProductIcon(String productIcon) {
        this.productIcon = productIcon;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductDiscription() {
        return productDiscription;
    }

    public void setProductDiscription(String productDiscription) {
        this.productDiscription = productDiscription;
    }

    public String getProductQuantaty() {
        return productQuantaty;
    }

    public void setProductQuantaty(String productQuantaty) {
        this.productQuantaty = productQuantaty;
    }

    public String getTimestemp() {
        return timestemp;
    }

    public void setTimestemp(String timestemp) {
        this.timestemp = timestemp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
