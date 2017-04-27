package br.com.savingfood.utils;

/**
 * Created by brunolemgruber on 07/10/16.
 */

public class EnumStatus {

    public enum Status {

        FINDING_SHOPPER("Finding Shopper"),
        SHOPPER_BUYING("Shopper Buying"),
        WAITING_USER_APPROVE("Waiting User To Approve"),
        SHOPPER_PAYING("Shopper Paying"),
        SHOPPER_PAYED("Shopper Payed"),
        USER_RECEIVED("User Received"),
        SHOPPER_DECLINED("Shopper Declined"),

        PRODUCT_NOT_FIND("Not Find"),
        PRODUCT_WAITING("Waiting Status"),
        PRODUCT_FIND("Find");

        private String name;

        Status(String name){
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

}

