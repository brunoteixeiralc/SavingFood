package br.com.savingfood.fragment;

import android.app.PendingIntent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
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

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.Serializable;
import java.util.List;

import br.com.savingfood.R;
import br.com.savingfood.adapter.StoreAdapter;
import br.com.savingfood.model.Product;
import br.com.savingfood.model.Store;
import br.com.savingfood.utils.DividerItemDecoration;
import br.com.savingfood.utils.EnumToolBar;
import br.com.savingfood.utils.Utils;

/**
 * Created by brunolemgruber on 14/07/16.
 */

public class StoreListFragment extends Fragment {

    private static View view;
    protected RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private Fragment fragment;
    private ImageView mIconMapImageView,img_bg;
    private List<Store> storeList;
    private Toolbar toolbar;
    private FirebaseAnalytics mFirebaseAnalytics;
    private AppBarLayout appBarLayout;
    private List<Product> products;

    private PendingIntent geoFencePendingIntent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.list, container, false);
//        view.setFocusableInTouchMode(true);
//        view.requestFocus();
//        view.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int i, KeyEvent keyEvent) {
//                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
//                    if (i == KeyEvent.KEYCODE_BACK) {
//                        getFragmentManager().popBackStack();
//                        Utils.setIconBar(EnumToolBar.STOREMAP,toolbar);
//
//                        appBarLayout.setExpanded(false,true);
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(StoreListFragment.this.getContext());

        toolbar =(Toolbar)getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle("Lojas Próximas");

        Utils.setIconBar(EnumToolBar.STORELIST,toolbar);

        img_bg = (ImageView) getActivity().findViewById(R.id.img);
        img_bg.setImageResource(R.drawable.img_loja_proxima);

        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appbarlayout);
        appBarLayout.setExpanded(true,true);

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

        if(getArguments() != null){
            storeList = (List<Store>) getArguments().getSerializable("stores");
        }

        mLayoutManager = new LinearLayoutManager(StoreListFragment.this.getActivity());
        mAdapter = new StoreAdapter(onClickListener(),StoreListFragment.this.getContext(),storeList);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(StoreListFragment.this.getContext(),LinearLayoutManager.VERTICAL));

        return  view;
    }

    private GeofencingRequest createGeofenceRequest(Geofence geofence ) {
        Log.d("savingfoods", "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER )
                .addGeofence( geofence )
                .build();
    }

    private Geofence createGeofence(Double lat, Double lng, float radius ) {
        Log.d("savingfoods", "createGeofence");
        return new Geofence.Builder()
                .setRequestId("SavingFoods_geofence")
                .setCircularRegion( lat, lng, radius)
                .setExpirationDuration( 60 * 60 * 1000 )
                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT )
                .build();
    }

//    private PendingIntent createGeofencePendingIntent() {
//        Log.d("savingfoods", "createGeofencePendingIntent");
//        if ( geoFencePendingIntent != null )
//            return geoFencePendingIntent;
//
//        Intent intent = new Intent( this, GeofenceTrasitionService.class);
//        return PendingIntent.getService(
//                StoreListFragment.this.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
//    }
//
//    private void addGeofence(GeofencingRequest request) {
//        Log.d("savingfoods", "addGeofence");
//
//        if (checkPermission())
//            LocationServices.GeofencingApi.addGeofences(
//                    googleApiClient,
//                    request,
//                    createGeofencePendingIntent()
//            ).setResultCallback(this);
//    }
//
//    // Start Geofence creation process
//    private void startGeofence(Double lat, Double lng) {
//        Log.i("savingfoods", "startGeofence()");
//            Geofence geofence = createGeofence( lat,lng, 100.0f );
//            GeofencingRequest geofenceRequest = createGeofenceRequest( geofence );
//            addGeofence( geofenceRequest );
//    }

    private StoreAdapter.StoreOnClickListener onClickListener() {
        return new StoreAdapter.StoreOnClickListener() {
            @Override
            public void onClickSticker(View view, int idx) {

                Store storeSelected = storeList.get(idx);

                Bundle bAnalytics = new Bundle();
                bAnalytics.putString(FirebaseAnalytics.Param.ITEM_ID, storeSelected.getKeyStore());
                bAnalytics.putString(FirebaseAnalytics.Param.ITEM_NAME, storeSelected.getName());
                bAnalytics.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "store");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bAnalytics);

                Bundle bundle = new Bundle();
                bundle.putSerializable("store",storeSelected);
                bundle.putSerializable("products", (Serializable) storeSelected.getProducts());

                fragment = new DetailStoreFragment();
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

               // startGeofence(storeSelected.getLat(),storeSelected.getLng());
            }
        };
    }
}
