package br.com.savingfood.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.com.savingfood.R;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.list, container, false);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_BACK) {
                        getFragmentManager().popBackStack();
                        Utils.setIconBar(EnumToolBar.STOREDETAIL, toolbar);
                        toolbar.setTitle("");

                        appBarLayout.setExpanded(true, true);
                        return true;
                    }
                }
                return false;
            }
        });

        products = (List<Product>) getArguments().getSerializable("products");
        category = getArguments().getString("category");

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(category);

        Utils.setIconBar(EnumToolBar.PRODUCTLIST, toolbar);

        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appbarlayout);
        appBarLayout.setExpanded(false, true);

        mLayoutManager = new LinearLayoutManager(ProductListFragment.this.getActivity());
        mAdapter = new ProductAdapter(onClickListener(), ProductListFragment.this.getContext(), products);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(ProductListFragment.this.getContext(), LinearLayoutManager.VERTICAL));

        return view;
    }

    private ProductAdapter.ProductOnClickListener onClickListener() {
        return new ProductAdapter.ProductOnClickListener() {

            @Override
            public void onClick(View view, int idx) {

            }
        };
    }
}
