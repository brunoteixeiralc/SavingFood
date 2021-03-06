package br.com.savingfood.adapter;

import android.content.Context;
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
import java.text.DecimalFormat;
import java.util.List;

import br.com.savingfood.R;
import br.com.savingfood.model.Store;
import br.com.savingfood.utils.Utils;


/**
 * Created by brunolemgruber on 15/07/16.
 */

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {

    protected static final String TAG = "savingfoods";
    private final List<Store> stores;
    private StoreOnClickListener storeOnClickListener;
    private final Context context;

    public StoreAdapter(StoreOnClickListener storeOnClickListener, Context context, List<Store> stores) {
        this.context = context;
        this.stores = stores;
        this.storeOnClickListener = storeOnClickListener;
    }

    @Override
    public int getItemCount() {
        return this.stores.size();
    }

    @Override
    public StoreViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_store, viewGroup, false);

        StoreViewHolder holder = new StoreViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final StoreViewHolder holder, final int position) {

        Store s = stores.get(position);

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        holder.progressBar.setVisibility(View.VISIBLE);
        holder.distance.setText(String.valueOf(df.format(s.getDistance()) + " km"));
        holder.name.setText(s.getName());
        holder.address.setText(s.getAddress());

        RequestOptions myOptions = new RequestOptions().circleCrop().diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context).load(s.getImg()).apply(myOptions).listener(new RequestListener<Drawable>() {
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

        if (storeOnClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    storeOnClickListener.onClickSticker(holder.itemView, position);
                }
            });
        }
    }

    public interface StoreOnClickListener  {
        public void onClickSticker(View view, int idx);
    }

    public static class StoreViewHolder extends RecyclerView.ViewHolder {

        public TextView name,address,distance;
        public ImageView img;
        public ProgressBar progressBar;

        public StoreViewHolder(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.name);
            distance = (TextView) view.findViewById(R.id.distance);
            address = (TextView) view.findViewById(R.id.address);
            img = (ImageView) view.findViewById(R.id.img);
            progressBar = (ProgressBar) view.findViewById(R.id.progress);

        }
    }

}
