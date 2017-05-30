package br.com.savingfood.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.savingfood.model.Alert;
import br.com.savingfood.model.User;

/**
 * Created by brunolemgruber on 27/05/17.
 */

public final class FirebaseServices {

    private static DatabaseReference databaseReference;

    public static void saveUser(User user){
        databaseReference = FirebaseDatabase.getInstance().getReference("user").child(user.getUid());
        databaseReference.setValue(user);
    }

    public void updateUserToken(){

    }

    public static void saveAlert(String productName,Alert alert){
        databaseReference = FirebaseDatabase.getInstance().getReference("alert").child(productName).child(alert.getUid());
        databaseReference.setValue(alert.getTokenPush());
    }

    public static void deleteAlert(String productName,Alert alert){
        databaseReference = FirebaseDatabase.getInstance().getReference("alert").child(productName).child(alert.getUid());
        databaseReference.removeValue();
    }

}
