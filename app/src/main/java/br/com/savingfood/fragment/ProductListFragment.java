package br.com.savingfood.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;

import java.util.List;

import br.com.savingfood.R;
import br.com.savingfood.adapter.ProdCategAdapter;
import br.com.savingfood.adapter.ProductAdapter;
import br.com.savingfood.model.Product;
import br.com.savingfood.utils.DividerItemDecoration;
import br.com.savingfood.utils.EnumToolBar;
import br.com.savingfood.utils.Utils;

/**
 * Created by brunolemgruber on 14/07/16.
 */

public class ProductListFragment extends Fragment {

    private static View view;
    protected RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private List<Product> products;
    private String category;
    private Fragment fragment;
    private LinearLayout linearLayout;
    private LottieAnimationView animationView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.list, container, false);

        linearLayout = (LinearLayout) view.findViewById(R.id.ll_list);
        animationView = (LottieAnimationView) view.findViewById(R.id.animation_view);

        products = (List<Product>) getArguments().getSerializable("products");
        category = getArguments().getString("category");

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(category);

        Utils.setIconBar(EnumToolBar.PRODUCTLIST, toolbar);

        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appbarlayout);
        appBarLayout.setExpanded(false, true);

        mLayoutManager = new LinearLayoutManager(ProductListFragment.this.getActivity());

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(ProductListFragment.this.getContext(), LinearLayoutManager.VERTICAL));

        if(products.size() != 0) {
            linearLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            mAdapter = new ProductAdapter(onClickListener(), ProductListFragment.this.getContext(), products);
            recyclerView.setAdapter(mAdapter);

        }else{
            recyclerView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
            animationView.playAnimation();
        }

        return view;
    }

    private ProductAdapter.ProductOnClickListener onClickListener() {
        return new ProductAdapter.ProductOnClickListener() {

            @Override
            public void onClick(View view, int idx) {

                Bundle bundle = new Bundle();
                bundle.putSerializable("product",products.get(idx));

                fragment = new ProductDetailFragment();
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        };
    }
}
