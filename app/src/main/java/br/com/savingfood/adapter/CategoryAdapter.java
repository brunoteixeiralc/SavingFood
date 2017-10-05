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
import br.com.savingfood.utils.Money;

/**
 * Created by brunolemgruber on 16/07/16.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    protected static final String TAG = "savingFood";
    private final List<Category> categorys;
    private CategoryAdapter.CategoryOnClickListener categoryOnClickListener;
    private final Context context;

    public CategoryAdapter(CategoryAdapter.CategoryOnClickListener categoryOnClickListener, Context context, List<Category> categorys) {
        this.context = context;
        this.categorys = categorys;
        this.categoryOnClickListener = categoryOnClickListener;
    }

    @Override
    public int getItemCount() {
        return this.categorys.size();
    }

    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_category, viewGroup, false);
        CategoryAdapter.CategoryViewHolder holder = new CategoryAdapter.CategoryViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final CategoryAdapter.CategoryViewHolder holder, final int position) {

       final Category p = categorys.get(position);

       holder.name.setText(p.getName());
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


        if (categoryOnClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    categoryOnClickListener.onClick(holder.img, position);
                }
            });
        }
    }

    public interface CategoryOnClickListener  {
        public void onClick(View view, int idx);
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public ImageView img;
        public ProgressBar progressBar;

        public CategoryViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            img = (ImageView) view.findViewById(R.id.img);
            progressBar = (ProgressBar) view.findViewById(R.id.progress);
        }
    }
}
