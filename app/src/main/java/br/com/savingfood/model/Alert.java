package br.com.savingfood.model;

import java.io.Serializable;

/**
 * Created by brunolemgruber on 26/05/17.
 */

public class Alert implements Serializable {

    private String uid;
    private String tokenPush;

    public Alert(){}

    public Alert(String uid, String tokenPush){

        this.uid = uid;
        this.tokenPush = tokenPush;
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
}
