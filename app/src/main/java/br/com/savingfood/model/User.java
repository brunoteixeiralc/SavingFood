package br.com.savingfood.model;

import java.io.Serializable;

/**
 * Created by brunolemgruber on 26/05/17.
 */

public class User implements Serializable {

    private String uid;
    private String device_id;
    private String tokenPush;
    private String email;
    private String name;

    public User(){}

    public User (String uid,String device_id,String tokenPush, String email, String name){

        this.uid = uid;
        this.device_id = device_id;
        this.tokenPush = tokenPush;
        this.email = email;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getTokenPush() {
        return tokenPush;
    }

    public void setTokenPush(String tokenPush) {
        this.tokenPush = tokenPush;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
