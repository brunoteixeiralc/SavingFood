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
import br.com.savingfood.model.Product;
import br.com.savingfood.utils.Money;

/**
 * Created by brunolemgruber on 16/07/16.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    protected static final String TAG = "savingFood";
    private final List<Product> products;
    private ProductAdapter.ProductOnClickListener productOnClickListener;
    private ProductAdapter.CartOnClickListener cartOnClickListener;
    private final Context context;

    public ProductAdapter(ProductAdapter.ProductOnClickListener storeOnClickListener, Context context, List<Product> products) {
        this.context = context;
        this.products = products;
        this.productOnClickListener = storeOnClickListener;
    }

    @Override
    public int getItemCount() {
        return this.products.size();
    }

    @Override
    public ProductAdapter.ProductViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_product, viewGroup, false);
        ProductAdapter.ProductViewHolder holder = new ProductAdapter.ProductViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ProductAdapter.ProductViewHolder holder, final int position) {

       final Product p = products.get(position);

       holder.name.setText(p.getName());
       holder.percent.setText(p.getPercent() + "%");
       holder.quantity.setText("Restam " + p.getQuantity() + " itens");
       holder.views.setText(p.getViews() + " visualizações");
       holder.price_from.setPaintFlags(holder.price_from.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
       holder.price_from.setText(Money.reais(new BigDecimal(p.getOld_price(), MathContext.DECIMAL64)).toString());
        holder.price_to.setText("para " + Money.reais(new BigDecimal(p.getPrice(), MathContext.DECIMAL64)).toString());
       if(p.getImg() != null){
           Glide.with(context).load(p.getImg()).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)).listener(new RequestListener<Drawable>() {
               @Override
               public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                   return false;
               }

               @Override
               public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                   holder.progressBar.setVisibility(View.GONE);
                   return false;
               }
           }).into(holder.img);

       }else{
           holder.progressBar.setVisibility(View.GONE);

       }


        if (productOnClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    productOnClickListener.onClick(holder.img, position);
                }
            });
        }
    }

    public interface ProductOnClickListener  {
        public void onClick(View view, int idx);
    }

    public interface CartOnClickListener  {
        public void onClick(View view, int idx);
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        public TextView name,price_from,price_to,views,quantity,percent;
        public ImageView img;
        public ProgressBar progressBar;

        public ProductViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            percent = (TextView) view.findViewById(R.id.percent);
            views = (TextView) view.findViewById(R.id.views);
            quantity = (TextView) view.findViewById(R.id.quantity);
            price_from = (TextView) view.findViewById(R.id.price_from);
            price_to = (TextView) view.findViewById(R.id.price_to);
            img = (ImageView) view.findViewById(R.id.img);
            progressBar = (ProgressBar) view.findViewById(R.id.progress);
        }
    }
}
