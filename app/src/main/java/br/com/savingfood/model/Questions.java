package br.com.savingfood.model;

import java.io.Serializable;

/**
 * Created by brunolemgruber on 26/05/17.
 */

public class Questions implements Serializable {

    private String first;
    private String second;

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }
}
