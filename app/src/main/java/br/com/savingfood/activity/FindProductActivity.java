package br.com.savingfood.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.savingfood.R;
import br.com.savingfood.model.Product;
import br.com.savingfood.utils.Utils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by brunolemgruber on 10/07/17.
 */

public class FindProductActivity extends AppCompatActivity {

    private Button btnSeeAll,btnFindProduct;
    private EditText txtFindProduct;
    private DatabaseReference mDatabase;
    private List<Product> products = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        txtFindProduct = (EditText) findViewById(R.id.txtFindProduct);

        btnFindProduct = (Button) findViewById(R.id.btn_find_product);
        btnFindProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.openDialog(FindProductActivity.this,"Buscando produto");
                findProduct();
            }
        });

        btnSeeAll = (Button) findViewById(R.id.btn_see_all);
        btnSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMainActivity(true);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void loadMainActivity(boolean loadAllProduct){

        Intent intent = new Intent(FindProductActivity.this,MainActivity.class);

        if(!loadAllProduct){
            Bundle bundle = new Bundle();
            bundle.putSerializable("products", (Serializable) products);
            intent.putExtra("bundle_products",bundle);
        }

        intent.putExtra("loadAllProducts",loadAllProduct);
        startActivity(intent);
    }

    private void findProduct(){

        mDatabase.child("discounts").orderByChild("name").equalTo(txtFindProduct.getText().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                products.clear();

                if (dataSnapshot.hasChildren()) {

                    for (DataSnapshot st : dataSnapshot.getChildren()) {

                        Product product = st.getValue(Product.class);

//                        DataSnapshot storesSnap = st.child("stores");
//                        for (DataSnapshot sSnap : storesSnap.getChildren()) {
//                            if(product.getmStores() == null)
//                                product.setmStores(new ArrayList<String>());
//                            product.getmStores().add(sSnap.getKey());
//                        }
//
//                        product.setUid(st.getKey());
                        products.add(product);
                    }
                }

                Utils.closeDialog(FindProductActivity.this);
                if(products.size() != 0){
                    loadMainActivity(false);
                }else{
                    Toast.makeText(getApplicationContext(), "Não achamos nenhum produto!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }
}
