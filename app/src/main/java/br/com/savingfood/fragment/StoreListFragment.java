package br.com.savingfood.fragment;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.savingfood.R;
import br.com.savingfood.adapter.StoreAdapter;
import br.com.savingfood.model.Product;
import br.com.savingfood.model.Store;
import br.com.savingfood.model.User;
import br.com.savingfood.singleton.GoogleApiSingleton;
import br.com.savingfood.utils.DividerItemDecoration;
import br.com.savingfood.utils.EnumToolBar;
import br.com.savingfood.utils.GeofenceTrasitionService;
import br.com.savingfood.utils.Utils;
import io.realm.Realm;

/**
 * Created by brunolemgruber on 14/07/16.
 */

public class StoreListFragment extends Fragment implements ResultCallback<Status> {

    private static View view;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private Fragment fragment;
    private ImageView mIconMapImageView, img_bg;
    private List<Store> storeList;
    private Toolbar toolbar;
    private FirebaseAnalytics mFirebaseAnalytics;
    private AppBarLayout appBarLayout;
    private List<Product> products;
    private GoogleApiClient googleApiClient;
    private PendingIntent geoFencePendingIntent;
    private Store storeSelected;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.list, container, false);

        Utils.openDialog(StoreListFragment.this.getContext(),"Carregando lojas.");

        Double mLatitude = getArguments().getDouble("mLatitude");
        Double mLongitude = getArguments().getDouble("mLongitude");
        Location location = new Location("location");
        location.setLatitude(mLatitude);
        location.setLongitude(mLongitude);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(StoreListFragment.this.getContext());

        if(storeList == null)
            storeList = new ArrayList<>();

        getStores(location);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle("Lojas Próximas");

        Utils.setIconBar(EnumToolBar.STORELIST, toolbar);

        img_bg = (ImageView) getActivity().findViewById(R.id.img);
        img_bg.setImageResource(R.drawable.img_loja_proxima);

        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appbarlayout);
        appBarLayout.setExpanded(true, true);

//        mIconMapImageView = (ImageView) toolbar.findViewById(R.id.ic_mapStore);
//        mIconMapImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                getFragmentManager().popBackStack();
//                Utils.setIconBar(EnumToolBar.STOREMAP,toolbar);
//
//                appBarLayout.setExpanded(false,true);
//
//                Bundle params = new Bundle();
//                params.putString("icon_click", "ic_map");
//                mFirebaseAnalytics.logEvent("type_view_store", params);
//
//            }
//        });

//        if (getArguments() != null) {
//            storeList = (List<Store>) getArguments().getSerializable("stores");
//        }

        mLayoutManager = new LinearLayoutManager(StoreListFragment.this.getActivity());
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(StoreListFragment.this.getContext(), LinearLayoutManager.VERTICAL));

        return view;
    }

    private GeofencingRequest createGeofenceRequest(Geofence geofence) {
        Log.d("savingfoods", "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();
    }

    private Geofence createGeofence(Double lat, Double lng, float radius) {
        Log.d("savingfoods", "createGeofence");

        return new Geofence.Builder()
                .setRequestId("SavingFoods_Geofence")
                .setCircularRegion(lat, lng, radius)
                .setExpirationDuration(60 * 60 * 1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }

    private PendingIntent createGeofencePendingIntent() {
        Log.d("savingfoods", "createGeofencePendingIntent");
        if (geoFencePendingIntent != null)
            return geoFencePendingIntent;

        Intent intent = new Intent(StoreListFragment.this.getContext(), GeofenceTrasitionService.class);
        intent.putExtra("store",storeSelected.getName());

        Realm realm = Realm.getDefaultInstance();
        User user = realm.where(User.class).findFirst();

        intent.putExtra("username",user.getName());
        intent.putExtra("uid",user.getUid());
        return PendingIntent.getService(StoreListFragment.this.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void addGeofence(GeofencingRequest request) {
        Log.d("savingfoods", "addGeofence");

        if (ActivityCompat.checkSelfPermission(StoreListFragment.this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.GeofencingApi.addGeofences(googleApiClient, request, createGeofencePendingIntent()).setResultCallback(this);
    }

    private void startGeofence(Double lat, Double lng) {
        Log.i("savingfoods", "startGeofence()");
        Geofence geofence = createGeofence(lat, lng, 200.0f);
        GeofencingRequest geofenceRequest = createGeofenceRequest(geofence);
        addGeofence(geofenceRequest);
    }

    private StoreAdapter.StoreOnClickListener onClickListener() {
        return new StoreAdapter.StoreOnClickListener() {
            @Override
            public void onClickSticker(View view, int idx) {

                storeSelected = storeList.get(idx);

                googleApiClient = GoogleApiSingleton.getInstance(null).get_GoogleApiClient();
                startGeofence(storeSelected.getLat(), storeSelected.getLng());

                Bundle bAnalytics = new Bundle();
                bAnalytics.putString(FirebaseAnalytics.Param.ITEM_ID, storeSelected.getKeyStore());
                bAnalytics.putString(FirebaseAnalytics.Param.ITEM_NAME, storeSelected.getName());
                bAnalytics.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "store");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bAnalytics);

                Bundle bundle = new Bundle();
                bundle.putSerializable("store", storeSelected);
                bundle.putSerializable("products", (Serializable) storeSelected.getProducts());

                fragment = new DetailStoreFragment();
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        };
    }

    public void getStores(final Location l) {

        if(mDatabase == null)
            mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("network").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(storeList == null){
                    storeList = new ArrayList<>();
                }
                storeList.clear();

                if (dataSnapshot.hasChildren()) {

                    for (DataSnapshot nt : dataSnapshot.getChildren()) {
                        for (DataSnapshot st : nt.getChildren()) {

                            Store store = st.getValue(Store.class);
                            store.setProducts(new ArrayList<Product>());

                            Location storeLocation = new Location("storeLocation");
                            storeLocation.setLatitude(store.getLat());
                            storeLocation.setLongitude(store.getLng());

                            Float distanceTo = l.distanceTo(storeLocation) / 1000;

                            store.setDistance(distanceTo);

                            store.setNetwork(nt.getKey());
                            store.setKeyStore(st.getKey());

                            storeList.add(store);
                        }
                    }

                    mAdapter = new StoreAdapter(onClickListener(), StoreListFragment.this.getContext(), storeList);
                    recyclerView.setAdapter(mAdapter);

                    Utils.closeDialog(StoreListFragment.this.getContext());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Utils.closeDialog(StoreListFragment.this.getContext());
            }
        });
    }

    @Override
    public void onResult(@NonNull Status status) {
        Log.i("savingfoods", "onResult: " + status);
        if (status.isSuccess()) {}
    }
}
