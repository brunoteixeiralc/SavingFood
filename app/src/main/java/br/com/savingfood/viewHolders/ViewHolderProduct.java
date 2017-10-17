package br.com.savingfood.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import br.com.savingfood.R;

/**
 * Created by brunolemgruber on 05/10/2017.
 */

public class ViewHolderProduct extends RecyclerView.ViewHolder {

    private TextView price_from;
    private TextView price_to;
    private TextView views;
    //private TextView quantity;
    private TextView percent;
    private TextView name;
    private ImageView img;
    private ProgressBar progressBar;

    public ViewHolderProduct(View view) {
        super(view);
        name = (TextView) view.findViewById(R.id.name);
        percent = (TextView) view.findViewById(R.id.percent);
        views = (TextView) view.findViewById(R.id.views);
        //quantity = (TextView) view.findViewById(R.id.quantity);
        price_from = (TextView) view.findViewById(R.id.price_from);
        price_to = (TextView) view.findViewById(R.id.price_to);
        img = (ImageView) view.findViewById(R.id.img);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
    }

    public TextView getPrice_from() {
        return price_from;
    }

    public void setPrice_from(TextView price_from) {
        this.price_from = price_from;
    }

    public TextView getPrice_to() {
        return price_to;
    }

    public void setPrice_to(TextView price_to) {
        this.price_to = price_to;
    }

    public TextView getViews() {
        return views;
    }

    public void setViews(TextView views) {
        this.views = views;
    }

    public TextView getPercent() {
        return percent;
    }

    public void setPercent(TextView percent) {
        this.percent = percent;
    }

    public ImageView getImg() {
        return img;
    }

    public void setImg(ImageView img) {
        this.img = img;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public TextView getName() {
        return name;
    }

    public void setName(TextView name) {
        this.name = name;
    }
}

