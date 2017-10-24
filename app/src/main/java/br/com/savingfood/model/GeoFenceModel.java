package br.com.savingfood.model;

import io.realm.RealmObject;

/**
 * Created by brunolemgruber on 26/05/17.
 */

public class GeoFenceModel extends RealmObject {

    private String uid;
    private String store;
    private String enter_date;
    private String exit_date;
    private String key;

    public GeoFenceModel(){}

    public GeoFenceModel(String uid,String store,String enter_date,String exit_date,String key){
       this.uid = uid;
       this.store = store;
       this.enter_date = enter_date;
       this.exit_date = exit_date;
       this.key = key;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getEnter_date() {
        return enter_date;
    }

    public void setEnter_date(String enter_date) {
        this.enter_date = enter_date;
    }

    public String getExit_date() {
        return exit_date;
    }

    public void setExit_date(String exit_date) {
        this.exit_date = exit_date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
