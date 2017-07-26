package br.com.savingfood.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import br.com.savingfood.R;
import br.com.savingfood.adapter.StoreAdapter;
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
    private ImageView mIconMapImageView,mIconListImageView;
    private List<Store> storeList;
    private Toolbar toolbar;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.list, container, false);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(StoreListFragment.this.getContext());

        storeList = (List<Store>) getArguments().getSerializable("stores");

        toolbar =(Toolbar)getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle("Lojas nas proximidades");

        Utils.setIconBar(EnumToolBar.STORELIST,toolbar);

        mIconListImageView = (ImageView) toolbar.findViewById(R.id.ic_listStore);

        mIconMapImageView = (ImageView) toolbar.findViewById(R.id.ic_mapStore);
        mIconMapImageView.setVisibility(View.VISIBLE);
        mIconMapImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragment = new MapFragment();

                if(fragment != null) {
                    getFragmentManager().popBackStack();
                    mIconMapImageView.setVisibility(View.GONE);
                    mIconListImageView.setVisibility(View.VISIBLE);
                }

                Bundle params = new Bundle();
                params.putString("icon_click", "ic_map");
                mFirebaseAnalytics.logEvent("type_view_store", params);

            }
        });

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

                fragment = new DetailStoreFragment();
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        };
    }

}
