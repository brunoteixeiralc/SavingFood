package br.com.savingfood.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import br.com.savingfood.R;
import br.com.savingfood.model.Category;
import br.com.savingfood.model.Product;
import br.com.savingfood.utils.Money;
import br.com.savingfood.viewHolders.ViewHolderCategory;
import br.com.savingfood.viewHolders.ViewHolderProduct;

/**
 * Created by brunolemgruber on 16/07/16.
 */

public class ProdCategAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> items;

    private final int PRODUCT = 0, CATEGORY = 1;

    private final Context context;

    public ProdCategAdapter(List<Object> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case PRODUCT:
                View v1 = inflater.inflate(R.layout.list_item_product, parent, false);
                viewHolder = new ViewHolderProduct(v1);
                break;
            case CATEGORY:
                View v2 = inflater.inflate(R.layout.list_item_category, parent, false);
                viewHolder = new ViewHolderCategory(v2);
                break;
            default:
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case PRODUCT:
                ViewHolderProduct vhProduct = (ViewHolderProduct) holder;
                configureViewHolderProduct(vhProduct, position);
                break;
            case CATEGORY:
                ViewHolderCategory vhCategory = (ViewHolderCategory) holder;
                configureViewHolderCategory(vhCategory, position);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Product) {
            return PRODUCT;
        } else if (items.get(position) instanceof Category) {
            return CATEGORY;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    private void configureViewHolderProduct(final ViewHolderProduct vhProduct, int position) {
        Product product = (Product) items.get(position);
        if (product != null) {
            vhProduct.getName().setText(product.getName());
            vhProduct.getPercent().setText(product.getPercent() + "%");
            vhProduct.getQuantity().setText("Restam " + product.getQuantity() + " itens");
            vhProduct.getViews().setText(product.getViews() + " visualizações");
            vhProduct.getPrice_from().setPaintFlags(vhProduct.getPrice_from().getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            vhProduct.getPrice_from().setText(Money.reais(new BigDecimal(product.getOld_price(), MathContext.DECIMAL64)).toString());
            vhProduct.getPrice_to().setText("para " + Money.reais(new BigDecimal(product.getPrice(), MathContext.DECIMAL64)).toString());
            if(product.getImg() != null){
                Glide.with(context).load(product.getImg()).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                        vhProduct.getProgressBar().setVisibility(View.GONE);
                        return false;
                    }
                }).into(vhProduct.getImg());

            }else{
                vhProduct.getProgressBar().setVisibility(View.GONE);

            }
        }
    }

    private void configureViewHolderCategory(final ViewHolderCategory vhCategory, int position) {
        Category category = (Category) items.get(position);
        if (category != null) {
            vhCategory.getName().setText(category.getName());
            if(category.getImg() != null){
                Glide.with(context).load(category.getImg()).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                        vhCategory.getProgressBar().setVisibility(View.GONE);
                        return false;
                    }
                }).into(vhCategory.getImg());

            }else{
                vhCategory.getProgressBar().setVisibility(View.GONE);

            }
        }
    }
}
