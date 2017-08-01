package br.com.savingfood.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.savingfood.R;
import br.com.savingfood.firebase.FirebaseServices;
import br.com.savingfood.model.Alert;
import br.com.savingfood.model.Product;
import br.com.savingfood.utils.Config;
import br.com.savingfood.utils.EnumToolBar;
import br.com.savingfood.utils.Utils;

/**
 * Created by brunolemgruber on 16/07/16.
 */

public class ProductDetailFragment extends Fragment {

    private View view;
    private TextView description,price_from,price_to,quantity,days_left;
    private ImageView img,mIcon_notification_on,mIcon_notification_off;
    private Product product;
    private Toolbar toolbar;
    private DatabaseReference mDatabase;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Date d_date;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.content_product_detail, container, false);

        price_from = (TextView) view.findViewById(R.id.price_from);
        price_to = (TextView) view.findViewById(R.id.price_to);
        quantity = (TextView) view.findViewById(R.id.quantity);
        description = (TextView) view.findViewById(R.id.description);
        days_left = (TextView) view.findViewById(R.id.days_left);

        product = (Product) getArguments().getSerializable("product");

        try {
            d_date =  new SimpleDateFormat("dd/MM/yyyy").parse(product.getDue_date());
            days_left.setText(String.valueOf(remainDays(new Date(),d_date)) + " dias");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(ProductDetailFragment.this.getContext());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("product").child(product.getUid()).child("views").setValue(product.getViews() + 1);

        toolbar =(Toolbar)getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(product.getName());

        Utils.setIconBar(EnumToolBar.PRODUCTDETAIL,toolbar);

        img = (ImageView) getActivity().findViewById(R.id.img);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);

        mIcon_notification_on = (ImageView) toolbar.findViewById(R.id.ic_notification_on);
        mIcon_notification_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences pref = view.getContext().getSharedPreferences(Config.SHARED_PREF, 0);
                String regId = pref.getString("regId", null);

                Alert alert = new Alert(FirebaseAuth.getInstance().getCurrentUser().getUid(),regId);
                FirebaseServices.deleteAlert(product.getName(),alert);

                mIcon_notification_on.setVisibility(View.GONE);
                mIcon_notification_off.setVisibility(View.VISIBLE);

                Utils.openSnack(view,"Alerta deletado.");

            }
        });

        mIcon_notification_off = (ImageView) toolbar.findViewById(R.id.ic_notification_off);
        mIcon_notification_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle params = new Bundle();
                params.putString("alert_click", "alert_on");
                params.putString("product_name", product.getName());
                mFirebaseAnalytics.logEvent("active_alert", params);

                SharedPreferences pref = view.getContext().getSharedPreferences(Config.SHARED_PREF, 0);
                String regId = pref.getString("regId", null);

                if(regId != null){
                    Alert alert = new Alert(FirebaseAuth.getInstance().getCurrentUser().getUid(),regId);
                    FirebaseServices.saveAlert(product.getName(),alert);

                    mIcon_notification_on.setVisibility(View.VISIBLE);
                    mIcon_notification_off.setVisibility(View.GONE);

                    Utils.openSnack(view,"Alerta salvo.");

                }else{
                    Utils.openSnack(view,"Tivemos um problema.Tente novamente mais tarde.");
                }

            }
        });

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
                    return false;
                }
            }).diskCacheStrategy(DiskCacheStrategy.ALL).into(img);
        }else{

        }

        return view;
    }

    private long remainDays(Date d1 , Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }
}

