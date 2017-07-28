package br.com.savingfood.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.savingfood.R;
import br.com.savingfood.adapter.ProductAdapter;
import br.com.savingfood.model.Product;
import br.com.savingfood.model.Store;
import br.com.savingfood.utils.DividerItemDecoration;
import br.com.savingfood.utils.EnumToolBar;
import br.com.savingfood.utils.Utils;

/**
 * Created by brunolemgruber on 15/07/16.
 */

public class DetailStoreFragment extends Fragment implements OnMapReadyCallback,SearchView.OnQueryTextListener{

    private View view;
    protected RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private Store store;
    private SupportMapFragment mapFragment;
    private Fragment fragment;
    private RecyclerView.Adapter mAdapter;
    private TextView name,address,distance;
    public ImageView img, mIconFilter;
    private DatabaseReference mDatabase;
    private Toolbar toolbar;
    private List<Product> products = new ArrayList<>();
    private BottomSheetDialog dialog;
    private TextView moreViews,lessViews,morePrice,lessPrice,moreQuatity,lessQuatity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Utils.openDialog(DetailStoreFragment.this.getContext(),"Carregando produtos");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.content_store_detail, container, false);
        } catch (InflateException e) {
        }

        store = (Store) getArguments().getSerializable("store");

        getProducts();

        toolbar =(Toolbar)getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle("");

        Utils.setIconBar(EnumToolBar.STOREDETAIL,toolbar);

        mIconFilter = (ImageView) toolbar.findViewById(R.id.ic_filter);
        mIconFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 abrirBottomSheerFilter();
            }
        });

        name = (TextView) view.findViewById(R.id.name);
        address = (TextView) view.findViewById(R.id.address);
        distance = (TextView) view.findViewById(R.id.km);

        img = (ImageView) getActivity().findViewById(R.id.img);
        Glide.with(DetailStoreFragment.this.getContext()).load(store.getImg()).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        }).diskCacheStrategy(DiskCacheStrategy.ALL).into(img);

        name.setText(store.getName());
        address.setText(store.getAddress());

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        distance.setText(String.valueOf(df.format(store.getDistance())) + " km");

        if(recyclerView == null){

            recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
            mLayoutManager = new LinearLayoutManager(DetailStoreFragment.this.getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setHasFixedSize(true);
            recyclerView.addItemDecoration(new DividerItemDecoration(DetailStoreFragment.this.getContext(),LinearLayoutManager.VERTICAL));

        }

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(DetailStoreFragment.this);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return true;
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.addMarker(new MarkerOptions().position(new LatLng(store.getLat(),store.getLng())));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(store.getLat(),store.getLng()), 5f);
        googleMap.moveCamera(cameraUpdate);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void getProducts(){

        mDatabase.child("product").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                products.clear();

                if (dataSnapshot.hasChildren()) {

                    for (DataSnapshot st : dataSnapshot.getChildren()) {

                        Product product = st.getValue(Product.class);

                        DataSnapshot storesSnap = st.child("stores");
                        for (DataSnapshot sSnap : storesSnap.getChildren()) {
                            if(sSnap.getKey().equalsIgnoreCase(store.getKeyStore())){
                                product.setUid(st.getKey());
                                products.add(product);
                                break;
                            }
                        }
                    }
                }

                if(products.size() != 0){
                    recyclerView.setVisibility(View.VISIBLE);
                    mAdapter = new ProductAdapter(onClickListener(),DetailStoreFragment.this.getContext(),products);
                    recyclerView.setAdapter(mAdapter);

                }

                Utils.closeDialog(DetailStoreFragment.this.getContext());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private ProductAdapter.ProductOnClickListener onClickListener() {
        return new ProductAdapter.ProductOnClickListener() {
            @Override
            public void onClick(View view, int idx) {

                Product product = products.get(idx);
                Bundle bundle = new Bundle();
                bundle.putSerializable("product",product);

                fragment = new ProductDetailFragment();
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        };
    }

    private void filterProducts(final String type, final String node){

        Utils.openDialog(DetailStoreFragment.this.getContext(),"Filtrando");

        mDatabase.child("product").orderByChild(node).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                products.clear();

                if (dataSnapshot.hasChildren()) {

                    for (DataSnapshot st : dataSnapshot.getChildren()) {

                        DataSnapshot storesSnap = st.child("stores");
                        for (DataSnapshot sSnap : storesSnap.getChildren()) {
                            if(sSnap.getKey().equalsIgnoreCase(store.getKeyStore())){
                                Product product = st.getValue(Product.class);
                                product.setFieldToFilter(node);
                                product.setUid(st.getKey());
                                products.add(product);
                                break;
                            }
                        }
                    }
                }

                if(products.size() != 0){

                    if(type.equalsIgnoreCase("more")){
                        Collections.sort(products, Collections.reverseOrder());
                    }

                    recyclerView.setVisibility(View.VISIBLE);
                    mAdapter = new ProductAdapter(onClickListener(),DetailStoreFragment.this.getContext(),products);
                    recyclerView.setAdapter(mAdapter);

                }

                dialog.dismiss();
                Utils.closeDialog(DetailStoreFragment.this.getContext());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void abrirBottomSheerFilter(){

       View view = DetailStoreFragment.this.getActivity().getLayoutInflater().inflate(R.layout.bottom_sheet_filter,null);
       dialog = new BottomSheetDialog(view.getContext());
       dialog.setContentView(view);
       dialog.show();

       lessViews = (TextView) view.findViewById(R.id.less_view);
       lessViews.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               filterProducts("less","views");
           }
       });

        moreViews = (TextView) view.findViewById(R.id.more_views);
        moreViews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                filterProducts("more","views");
            }
        });

        moreQuatity = (TextView) view.findViewById(R.id.more_quantity);
        moreQuatity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                filterProducts("more","quantity");
            }
        });

        lessQuatity = (TextView) view.findViewById(R.id.less_quantity);
        lessQuatity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                filterProducts("less","quantity");
            }
        });

        morePrice = (TextView) view.findViewById(R.id.more_price);
        morePrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                filterProducts("more","price");
            }
        });

        lessPrice = (TextView) view.findViewById(R.id.less_price);
        lessPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                filterProducts("less","price");
            }
        });

    }
}
