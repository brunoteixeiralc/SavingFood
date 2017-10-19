package br.com.savingfood.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.savingfood.R;
import br.com.savingfood.fragment.StoreListFragment;
import br.com.savingfood.model.Product;
import br.com.savingfood.model.Store;
import br.com.savingfood.singleton.GoogleApiSingleton;
import br.com.savingfood.utils.Config;
import br.com.savingfood.utils.LocationHelper;
import br.com.savingfood.utils.PermissionUtils;
import br.com.savingfood.utils.Utils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by brunolemgruber on 14/07/16.
 */

public class MainActivity extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,ActivityCompat.OnRequestPermissionsResultCallback,
        PermissionUtils.PermissionResultCallback{

    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();
    private Location mLastLocation;
    boolean isPermissionGranted;

    private Fragment fragment;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private Bundle bundle;
    private LocationHelper locationHelper;

    private List<Store> storeList = new ArrayList<>();
    private DatabaseReference mDatabase;

    private Toolbar toolbar;
    private AppBarLayout appBarLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        locationHelper=new LocationHelper(this);
        locationHelper.checkpermission();
        if (locationHelper.checkPlayServices()) {
            locationHelper.buildGoogleApiClient();
        }

        mAuth = FirebaseAuth.getInstance();
        
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());

                        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                        String regId = pref.getString("regId", null);

                        //User user = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(),"",regId);
                        //FirebaseServices.saveUser(user);
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInAnonymously", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getAction().equals(Config.PUSH_NOTIFICATION)){

                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };


//        bundle.putBoolean("loadAllProducts", getIntent().getBooleanExtra("loadAllProducts",false));
//        if(!getIntent().getBooleanExtra("loadAllProducts",false)){
//            bundle.putSerializable("products",getIntent().getBundleExtra("bundle_products").getSerializable("products"));
//        }


        fragment = new StoreListFragment();

        mLastLocation=locationHelper.getLocation();
        if (mLastLocation != null) {
            getStores(mLastLocation);
            Utils.openDialog(this,"Procurando lojas.");
        }
    }

    private void goStoresFragment(){

            bundle = new Bundle();
            bundle.putSerializable("stores", (Serializable) storeList);
            fragment.setArguments(bundle);

            getAddress(mLastLocation.getLatitude(),mLastLocation.getLongitude());

            if(fragment != null)
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    public void getAddress(Double latitude,Double longitude)
    {
        Address locationAddress;

        locationAddress=locationHelper.getAddress(latitude,longitude);

        if(locationAddress!=null)
        {
            String address = locationAddress.getAddressLine(0);
            String address1 = locationAddress.getAddressLine(1);
            String city = locationAddress.getLocality();
            String state = locationAddress.getAdminArea();
            String country = locationAddress.getCountryName();
            String postalCode = locationAddress.getPostalCode();

            String currentLocation;

            if(!TextUtils.isEmpty(address))
            {
                currentLocation=address;

                if (!TextUtils.isEmpty(address1))
                    currentLocation+="\n"+address1;

                if (!TextUtils.isEmpty(city))
                {
                    currentLocation+="\n"+city;

                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation+=" - "+postalCode;
                }
                else
                {
                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation+="\n"+postalCode;
                }

                if (!TextUtils.isEmpty(state))
                    currentLocation+="\n"+state;

                if (!TextUtils.isEmpty(country))
                    currentLocation+="\n"+country;

              SharedPreferences pref = this.getSharedPreferences(Config.SHARED_PREF, 0);
              SharedPreferences.Editor editor = pref.edit();
              editor.putString("actual_address", currentLocation);
              editor.commit();

            }

        }
        else
            showToast("Something went wrong");
    }

    public void showToast(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    private void getStores(final Location l){

        mDatabase.child("network").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                storeList.clear();

                if(dataSnapshot.hasChildren()){

                    for (DataSnapshot nt: dataSnapshot.getChildren()) {
                        for (DataSnapshot st: nt.getChildren()) {

                            Store store = st.getValue(Store.class);
                            store.setProducts(new ArrayList<Product>());

                            Location storeLocation=new Location("storeLocation");
                            storeLocation.setLatitude(store.getLat());
                            storeLocation.setLongitude(store.getLng());

                            Float distanceTo = l.distanceTo(storeLocation) / 1000;

                            store.setDistance(distanceTo);

                            store.setNetwork(nt.getKey());
                            store.setKeyStore(st.getKey());

                            storeList.add(store);
                        }             
                    }
                    goStoresFragment();
                    Utils.closeDialog(MainActivity.this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Utils.closeDialog(MainActivity.this);
            }
        });
    }

    public static Intent makeNotificationIntent(Context geofenceService, String msg)
    {
        Log.d(TAG,msg);
        return new Intent(geofenceService,MainActivity.class);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        locationHelper.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLastLocation=locationHelper.getLocation();

        if (mLastLocation != null) {
            getStores(mLastLocation);
            Utils.openDialog(this,"Procurando lojas.");
            GoogleApiSingleton.getInstance(locationHelper.getGoogleApiCLient());

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        locationHelper.connectApiClient();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    // Permission check functions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationHelper.onRequestPermissionsResult(requestCode,permissions,grantResults);

    }

    @Override
    public void PermissionGranted(int request_code) {
        Log.i("PERMISSION","GRANTED");
        isPermissionGranted=true;
    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {
        Log.i("PERMISSION PARTIALLY","GRANTED");
    }

    @Override
    public void PermissionDenied(int request_code) {
        Log.i("PERMISSION","DENIED");
    }

    @Override
    public void NeverAskAgain(int request_code) {
        Log.i("PERMISSION","NEVER ASK AGAIN");
    }

}
