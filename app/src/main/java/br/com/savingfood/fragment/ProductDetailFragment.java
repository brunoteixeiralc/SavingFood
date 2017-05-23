package br.com.savingfood.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.savingfood.R;
import br.com.savingfood.model.Product;

/**
 * Created by brunolemgruber on 16/07/16.
 */

public class ProductDetailFragment extends Fragment {

    private View view;
    private CoordinatorLayout coordinatorLayout;
    private TextView name,description,price_from,price_to,quantity;;
    private ImageView img;
    private Product product;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.content_product_detail, container, false);

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);
        name = (TextView) view.findViewById(R.id.name);
        price_from = (TextView) view.findViewById(R.id.price_from);
        price_to = (TextView) view.findViewById(R.id.price_to);
        quantity = (TextView) view.findViewById(R.id.quantity);
        description = (TextView) view.findViewById(R.id.description);
        img = (ImageView) view.findViewById(R.id.img);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);

        product = (Product) getArguments().getSerializable("product");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("product").child(product.getUid()).child("views").setValue(product.getViews() + 1);

        toolbar =(Toolbar)getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(product.getName());

        name.setText(product.getName());
        price_from.setText("R$ " + product.getOld_price());
        price_to.setText("para R$ " + product.getPrice());
        description.setText(product.getDescription());
        quantity.setText(product.getQuantity() + " unidades. Vencimento " + product.getDue_date());
        if(product.getImg() != null){
            Glide.with(ProductDetailFragment.this.getContext()).load(product.getImg()).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).diskCacheStrategy(DiskCacheStrategy.ALL).into(img);
        }else{
            progressBar.setVisibility(View.GONE);
        }

        return view;
    }
}

