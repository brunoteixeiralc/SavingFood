package br.com.savingfood.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import br.com.savingfood.R;
import br.com.savingfood.model.Product;
import br.com.savingfood.utils.EnumToolBar;
import br.com.savingfood.utils.Utils;

/**
 * Created by brunolemgruber on 16/07/16.
 */

public class ProductDetailFragment extends Fragment {

    private View view;
    private Button btnAddCart,btnRemoveCart;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    private TextView name,price,description;
    private EditText quantity;
    private ImageView img;
    private Product product;
    private ProgressBar progressBar;
    private Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.content_product_detail, container, false);

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);
        name = (TextView) view.findViewById(R.id.name);
        price = (TextView) view.findViewById(R.id.price);
        description = (TextView) view.findViewById(R.id.description);
        img = (ImageView) view.findViewById(R.id.img);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);

        product = (Product) getArguments().getSerializable("product");

        toolbar =(Toolbar)getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(product.getName() + " - Detalhes");

        Utils.setIconBar(EnumToolBar.PRODUCTSDETAIL,toolbar);

        name.setText(product.getName() + " " + product.getUnit_quantity());
        price.setText("R$ " + product.getPrice());
        description.setText(product.getDescription());
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

        return view;
    }
}

