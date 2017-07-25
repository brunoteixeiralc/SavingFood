package br.com.savingfood.model;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.List;

/**
 * Created by brunolemgruber on 18/07/16.
 */

public class Product implements Serializable,Comparable<Product> {

    private String name;

    private String uid;

    private Long bar_code;

    private String category;

    private String description;

    private String img;

    private Double price;

    private Double old_price;

    private String short_unit_measurement;

    private String unit_measurement;

    private String unit_quantity;

    private Double wholesale_price;

    private int views;

    private int quantity;

    private boolean inCart;

    private int percent;

    private String due_date;

    private String fieldToFilter;

    private List<String> mStores;

    public Long getBar_code() {
        return bar_code;
    }

    public void setBar_code(Long bar_code) {
        this.bar_code = bar_code;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getShort_unit_measurement() {
        return short_unit_measurement;
    }

    public void setShort_unit_measurement(String short_unit_measurement) {
        this.short_unit_measurement = short_unit_measurement;
    }

    public String getUnit_measurement() {
        return unit_measurement;
    }

    public void setUnit_measurement(String unit_measurement) {
        this.unit_measurement = unit_measurement;
    }

    public String getUnit_quantity() {
        return unit_quantity;
    }

    public void setUnit_quantity(String unit_quantity) {
        this.unit_quantity = unit_quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isInCart() {
        return inCart;
    }

    public void setInCart(boolean inCart) {
        this.inCart = inCart;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getWholesale_price() {
        return wholesale_price;
    }

    public void setWholesale_price(Double wholesale_price) {
        this.wholesale_price = wholesale_price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public Double getOld_price() {
        return old_price;
    }

    public void setOld_price(Double old_price) {
        this.old_price = old_price;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    @Override
    public int compareTo(@NonNull Product product) {

       if(product.getFieldToFilter().equalsIgnoreCase("views")){
           return this.views - product.getViews();
       }else if(product.getFieldToFilter().equalsIgnoreCase("quantity")){
           return this.quantity - product.getQuantity();
       }else if(product.getFieldToFilter().equalsIgnoreCase("price")){
           return Double.compare(this.price,product.getPrice());
       }
        return 0;
    }

    public String getFieldToFilter() {
        return fieldToFilter;
    }

    public void setFieldToFilter(String fieldToFilter) {
        this.fieldToFilter = fieldToFilter;
    }

    public List<String> getmStores() {
        return mStores;
    }

    public void setmStores(List<String> mStores) {
        this.mStores = mStores;
    }
}
