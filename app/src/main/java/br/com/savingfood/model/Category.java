package br.com.savingfood.model;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by brunolemgruber on 18/07/16.
 */

public class Category implements Serializable {

    private String name;

    private String uid;

    private String img;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
