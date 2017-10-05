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

public class ViewHolderCategory extends RecyclerView.ViewHolder {

    private TextView name;
    private ImageView img;
    private ProgressBar progressBar;

    public ViewHolderCategory(View view) {
        super(view);
        name = (TextView) view.findViewById(R.id.name);
        img = (ImageView) view.findViewById(R.id.img);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);

    }

    public TextView getName() {
        return name;
    }

    public void setName(TextView name) {
        this.name = name;
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
}
