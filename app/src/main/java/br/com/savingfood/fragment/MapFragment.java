package br.com.savingfood.fragment;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.savingfood.R;
import br.com.savingfood.model.ClusterMarkerLocation;
import br.com.savingfood.model.Product;
import br.com.savingfood.model.Store;
import br.com.savingfood.utils.EnumToolBar;
import br.com.savingfood.utils.Utils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static br.com.savingfood.R.id.map;


/**
 * Created by brunolemgruber on 30/01/15.
 */
@RuntimePermissions
public class MapFragment extends Fragment implements com.google.android.gms.maps.OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap gMap;
    private View view;
    private SupportMapFragment mapFragment;
    private GoogleApiClient googleApiClient;
    private ImageView mIconListImageView;
    private DatabaseReference mDatabase;
    private List<Store> storeList = new ArrayList<>();
    private ClusterMarkerLocation clickedClusterItem;
    private ClusterManager<ClusterMarkerLocation> clusterManager;
    private Fragment fragment;
    private Bundle bundle;
    private Toolbar toolbar;
    private Location locationNow;
    private FirebaseAnalytics mFirebaseAnalytics;
    private boolean findAllProduct = true;
    private List<Product> products = new ArrayList<>();
    private AppBarLayout appBarLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.content_map, container, false);
        } catch (InflateException e) {
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(MapFragment.this.getContext());

        if(getArguments() != null) {
            findAllProduct = getArguments().getBoolean("loadAllProducts");
            products = (List<Product>) getArguments().getSerializable("products");
        }

        toolbar =(Toolbar)getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle("Lojas próximas");

        Utils.setIconBar(EnumToolBar.STOREMAP,toolbar);

        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appbarlayout);
        appBarLayout.setExpanded(false);

        mIconListImageView = (ImageView) toolbar.findViewById(R.id.ic_listStore);
        mIconListImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragment = new StoreListFragment();
                bundle = new Bundle();
                bundle.putSerializable("stores", (Serializable) storeList);
                fragment.setArguments(bundle);

                if(fragment != null) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.add(R.id.fragment_container, fragment).addToBackStack(null).commit();
                }

                Bundle params = new Bundle();
                params.putString("icon_click", "ic_list");
                mFirebaseAnalytics.logEvent("type_view_store", params);
            }
        });

        mapFragment = (com.google.android.gms.maps.SupportMapFragment) getChildFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(MapFragment.this);
        googleApiClient = new GoogleApiClient.Builder(MapFragment.this.getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        return view;

    }

    private void setUpMap(Location l) {

        if (gMap != null && l != null) {

            Utils.openDialog(MapFragment.this.getContext(),"Carregando lojas");

            CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(new LatLng(l.getLatitude(),l.getLongitude()), 15);
            gMap.animateCamera(zoom);
            initMarkers(l);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("Saving Foods", "Conectado ao google play service");
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("Saving Foods", "Conexão interrompida");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("Saving Foods", "Erro ao conectar: " + connectionResult);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        MapFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);

    }

    @Override
    @NeedsPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;
        MapFragmentPermissionsDispatcher.setlocationWithCheck(this,gMap);

    }

    @Override
    public void onStart() {

        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {

        stopLocationUpdates();
        googleApiClient.disconnect();

        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {

        if(locationNow == null || (locationNow.getLatitude() != location.getLatitude() && locationNow.getLongitude() != location.getLongitude())){
            locationNow = location;
            setUpMap(location);
        }
    }


    private void stopLocationUpdates(){

        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);

    }

    public void startLocationUpdates(){

        MapFragmentPermissionsDispatcher.setStartLocationUpdatesWithCheck(this);
    }

    @NeedsPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
    public void setlocation(GoogleMap gMap){

        gMap.setMyLocationEnabled(true);
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        Location l = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        setUpMap(l);
    }

    @NeedsPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
    public void setStartLocationUpdates(){

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    private void initMarkers(Location l) {

        clusterManager = new ClusterManager<ClusterMarkerLocation>( MapFragment.this.getContext(), gMap );

        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<ClusterMarkerLocation>() {
            @Override
            public boolean onClusterItemClick(ClusterMarkerLocation clusterMarkerLocation) {
                clickedClusterItem = clusterMarkerLocation;
                return false;
            }
        });

        gMap.setOnCameraIdleListener(clusterManager);
        gMap.setOnMarkerClickListener(clusterManager);
        gMap.setOnInfoWindowClickListener(clusterManager);
        //gMap.setInfoWindowAdapter(clusterManager.getMarkerManager());

        //clusterManager.getMarkerCollection().setOnInfoWindowAdapter(new ClusterMarkerLocationAdapter());
        //clusterManager.setRenderer(new ClusterRenderer(MapFragment.this.getContext(),gMap,clusterManager));
        if(storeList == null || storeList.size() == 0)
            getStores(clusterManager,l);

        clusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<ClusterMarkerLocation>() {
            @Override
            public void onClusterItemInfoWindowClick(ClusterMarkerLocation clusterMarkerLocation) {

                Bundle bundle = new Bundle();
                bundle.putSerializable("store",clusterMarkerLocation.getStore());

                fragment = new DetailStoreFragment();
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });

    }

    public class ClusterMarkerLocationAdapter implements GoogleMap.InfoWindowAdapter{

        private final View view;

        ClusterMarkerLocationAdapter() {
            view = getActivity().getLayoutInflater().inflate(R.layout.info_windows_map, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {

            TextView title = (TextView) view.findViewById(R.id.txtTitle);
            title.setText(clickedClusterItem.getTitle());

            return view;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }

    private void getStores(final ClusterManager<ClusterMarkerLocation> clManager, final Location l){

        mDatabase.child("network").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                storeList.clear();

                if(dataSnapshot.hasChildren()){

                    if(dataSnapshot.hasChildren()){
                        for (DataSnapshot nt: dataSnapshot.getChildren()) {
                            for (DataSnapshot st: nt.getChildren()) {

                                    Store store = st.getValue(Store.class);

                                    Location storeLocation=new Location("storeLocation");
                                    storeLocation.setLatitude(store.getLat());
                                    storeLocation.setLongitude(store.getLng());

                                    Float distanceTo = l.distanceTo(storeLocation) / 1000;

                                    store.setDistance(distanceTo);

                                    store.setNetwork(nt.getKey());
                                    store.setKeyStore(st.getKey());

                                    if(!findAllProduct && products.size() > 0){
                                       for (Product p : products){
                                           if(p.getmStores().contains(store.getKeyStore())){
                                              storeList.add(store);
                                              clManager.addItem(new ClusterMarkerLocation(new LatLng(store.getLat(), store.getLng()),store.getName(),store.getAddress(),store));
                                            }
                                        }
                                    }else{
                                        storeList.add(store);
                                        clManager.addItem(new ClusterMarkerLocation(new LatLng(store.getLat(), store.getLng()),store.getName(),store.getAddress(),store));
                                    }
                            }
                        }
                        clManager.cluster();
                        stopLocationUpdates();
                        Utils.closeDialog(MapFragment.this.getContext());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Utils.closeDialog(MapFragment.this.getContext());
            }
        });
    }
}
