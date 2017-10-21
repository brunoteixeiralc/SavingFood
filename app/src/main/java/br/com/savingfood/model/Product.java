package br.com.savingfood.model;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by brunolemgruber on 18/07/16.
 */

public class Product implements Serializable,Comparable<Product> {

    private String name;

    private String uid;

    private String bar_code;

    private String description;

    private String img;

    private Double price;

    private Double old_price;

    private int views;

    private int quantity;

    private int percent;

    private String due_date;

    private String fieldToFilter;

    private String search;

    private String search_network;

    private String tags;

    private String category;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
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

    public String getFieldToFilter() {
        return fieldToFilter;
    }

    public void setFieldToFilter(String fieldToFilter) {
        this.fieldToFilter = fieldToFilter;
    }

    public String getBar_code() {
        return bar_code;
    }

    public void setBar_code(String bar_code) {
        this.bar_code = bar_code;
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

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSearch_network() {
        return search_network;
    }

    public void setSearch_network(String search_network) {
        this.search_network = search_network;
    }
}
