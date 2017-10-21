package br.com.savingfood.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.savingfood.R;
import br.com.savingfood.adapter.ProdCategAdapter;
import br.com.savingfood.model.Category;
import br.com.savingfood.model.Product;
import br.com.savingfood.model.Store;
import br.com.savingfood.utils.DividerItemDecoration;
import br.com.savingfood.utils.EnumToolBar;
import br.com.savingfood.utils.Utils;

/**
 * Created by brunolemgruber on 15/07/16.
 */

public class DetailStoreFragment extends Fragment{

    private View view;
    protected RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private Store store;
    private Fragment fragment;
    private RecyclerView.Adapter mAdapter;
    private TextView name,address,distance;
    public ImageView img, mIconFilter,mIconRoute;
    private DatabaseReference mDatabase;
    private Toolbar toolbar;
    private List<Object> products;
    private List<Product> productsByCategory;
    //private BottomSheetDialog dialog;
    //private TextView moreViews,lessViews,morePrice,lessPrice,moreQuatity,lessQuatity;
    private AppBarLayout appBarLayout;
    private LottieAnimationView animationView;
    private LinearLayout linearLayout;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private SwipeRefreshLayout swipeRefreshLayout;

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

        setHasOptionsMenu(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Utils.openDialog(DetailStoreFragment.this.getContext(),"Carregando ofertas.");

        if(getArguments() != null){
            store = (Store) getArguments().getSerializable("store");
        }

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(DetailStoreFragment.this.getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(DetailStoreFragment.this.getContext(),LinearLayoutManager.VERTICAL));

        toolbar =(Toolbar)getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle("");

        Utils.setIconBar(EnumToolBar.STOREDETAIL,toolbar);

        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appbarlayout);
        appBarLayout.setExpanded(true,true);

        linearLayout = (LinearLayout) view.findViewById(R.id.ll_store_detail);
        animationView = (LottieAnimationView) view.findViewById(R.id.animation_view);

        mIconFilter = (ImageView) toolbar.findViewById(R.id.ic_filter);
        mIconFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 //abrirBottomSheerFilter();
            }
        });

        mIconRoute = (ImageView) toolbar.findViewById(R.id.ic_route);
        mIconRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new MapDirectionsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("destination",store.getAddress());
                fragment.setArguments(bundle);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });

        if(productsByCategory == null ){
            productsByCategory = new ArrayList<>();
        }

        if(products == null || products.size() == 0){
            products = new ArrayList<>();
            getProducts(store.getNetwork(),store.getName());

        }else{
            linearLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            mAdapter = new ProdCategAdapter(onClickListener(),products,DetailStoreFragment.this.getContext());
            recyclerView.setAdapter(mAdapter);
            Utils.closeDialog(DetailStoreFragment.this.getContext());
        }

        name = (TextView) view.findViewById(R.id.name);
        address = (TextView) view.findViewById(R.id.address);
        distance = (TextView) view.findViewById(R.id.km);

        img = (ImageView) getActivity().findViewById(R.id.img);
        Glide.with(DetailStoreFragment.this.getContext()).load(store.getImg()).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                return false;
            }
        }).into(img);

        name.setText(store.getName());
        address.setText(store.getAddress());

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        distance.setText(String.valueOf(df.format(store.getDistance())) + " km");

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getProducts(store.getNetwork(),store.getName());
            }
        });

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return false;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            searchView.setQueryHint("Digite o produto");
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.i("onQueryTextChange", newText);

                    if(newText.length() >= 4){
                        searchProduct(newText);

                    } else if(newText.isEmpty()){
                        getProducts(store.getNetwork(),store.getName());
                    }

                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i("onQueryTextSubmit", query);

                    searchProduct(query);

                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void searchProduct(String text) {

        mDatabase.child("discounts").orderByChild("search").startAt(text).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                products.clear();

                if (dataSnapshot.hasChildren()) {

                    for (DataSnapshot st : dataSnapshot.getChildren()) {

                        Product product = st.getValue(Product.class);
                        product.setUid(st.getKey());
                        products.add(product);
                    }
                }

                if(products.size() != 0) {
                    linearLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    mAdapter = new ProdCategAdapter(onClickListener(),products,DetailStoreFragment.this.getContext());
                    recyclerView.setAdapter(mAdapter);

                }else{
                    recyclerView.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                    animationView.playAnimation();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getProducts(String network,String store){

        mDatabase.child("discounts").orderByChild("search_network").equalTo(network+"_"+store).limitToFirst(3).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                products.clear();

                if (dataSnapshot.hasChildren()) {

                    for (DataSnapshot st : dataSnapshot.getChildren()) {

                        Product product = st.getValue(Product.class);
                        product.setUid(st.getKey());
                        products.add(product);
                    }
                }
                getCategory();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getCategory(){

        mDatabase.child("category").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot st : dataSnapshot.getChildren()) {
                        Category category = new Category();
                        category.setName(st.getKey());
                        products.add(category);
                    }
                }

                if(products.size() != 0) {
                    linearLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    mAdapter = new ProdCategAdapter(onClickListener(),products,DetailStoreFragment.this.getContext());
                    recyclerView.setAdapter(mAdapter);

                }else{
                    recyclerView.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                    animationView.playAnimation();
                }

                swipeRefreshLayout.setRefreshing(false);
                Utils.closeDialog(DetailStoreFragment.this.getContext());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private ProdCategAdapter.onClickListener onClickListener() {
        return new ProdCategAdapter.onClickListener() {
            @Override
            public void onClick(View view, int idx) {

                Object o = products.get(idx);
                if(o instanceof Product){

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("product",(Product)o);
                    bundle.putSerializable("store",store);

                    fragment = new ProductDetailFragment();
                    fragment.setArguments(bundle);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

                }else{
                    Utils.openDialog(DetailStoreFragment.this.getContext(),"Carregando produtos.");
                    getProductsByCategory(((Category)o).getName());

                }
            }
        };
    }

    private void getProductsByCategory(final String category) {

        productsByCategory.clear();

        mDatabase.child("discounts").orderByChild("category").equalTo(category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    for (DataSnapshot st : dataSnapshot.getChildren()) {
                        Product product = st.getValue(Product.class);
                        product.setUid(st.getKey());
                        productsByCategory.add(product);
                    }
                }

                 Bundle bundle = new Bundle();
                 bundle.putSerializable("products", (Serializable) productsByCategory);
                 bundle.putSerializable("category", category);

                 fragment = new ProductListFragment();
                 fragment.setArguments(bundle);
                 FragmentTransaction transaction = getFragmentManager().beginTransaction();
                 transaction.replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

                Utils.closeDialog(DetailStoreFragment.this.getContext());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    private void filterProducts(final String type, final String node){
//
//        Utils.openDialog(DetailStoreFragment.this.getContext(),"Filtrando");
//
//        mDatabase.child("discounts").orderByChild(node).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                products.clear();
//
//                if (dataSnapshot.hasChildren()) {
//
//                    for (DataSnapshot st : dataSnapshot.getChildren()) {
//
//                          Product product = st.getValue(Product.class);
//                          if(product.getSearch().equalsIgnoreCase(store.getNetwork()+"_"+store.getName())){
//                              product.setFieldToFilter(node);
//                              products.add(product);
//                          }
//                    }
//                }
//
//                if(products.size() != 0){
//
//                    if(type.equalsIgnoreCase("more")){
//                        Collections.sort(products, Collections.reverseOrder());
//                    }
//
//                    recyclerView.setVisibility(View.VISIBLE);
//                    mAdapter = new ProdCategAdapter(onClickListener(),products,DetailStoreFragment.this.getContext());
//                    recyclerView.setAdapter(mAdapter);
//
//                }
//
//                dialog.dismiss();
//                Utils.closeDialog(DetailStoreFragment.this.getContext());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }


//    private void abrirBottomSheerFilter(){
//
//       View view = DetailStoreFragment.this.getActivity().getLayoutInflater().inflate(R.layout.bottom_sheet_filter,null);
//       dialog = new BottomSheetDialog(view.getContext());
//       dialog.setContentView(view);
//       dialog.show();
//
//       lessViews = (TextView) view.findViewById(R.id.less_view);
//       lessViews.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View view) {
//
//               filterProducts("less","views");
//           }
//       });
//
//        moreViews = (TextView) view.findViewById(R.id.more_views);
//        moreViews.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                filterProducts("more","views");
//            }
//        });
//
//        moreQuatity = (TextView) view.findViewById(R.id.more_quantity);
//        moreQuatity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                filterProducts("more","quantity");
//            }
//        });
//
//        lessQuatity = (TextView) view.findViewById(R.id.less_quantity);
//        lessQuatity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                filterProducts("less","quantity");
//            }
//        });
//
//        morePrice = (TextView) view.findViewById(R.id.more_price);
//        morePrice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                filterProducts("more","price");
//            }
//        });
//
//        lessPrice = (TextView) view.findViewById(R.id.less_price);
//        lessPrice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                filterProducts("less","price");
//            }
//        });
//
//    }
}
