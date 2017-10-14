package br.com.savingfood.model;

import io.realm.RealmObject;

/**
 * Created by brunolemgruber on 26/05/17.
 */

public class User extends RealmObject {

    private String uid;
    private String tokenPush;
    private String email;
    private String name;

    public User(){}

    public User (String uid,String tokenPush, String email, String name){

        this.uid = uid;
        this.tokenPush = tokenPush;
        this.email = email;
        this.name = name;
    }

    public User (String uid, String email, String name){

        this.uid = uid;
        this.email = email;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
