package br.com.savingfood.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import br.com.savingfood.R;
import br.com.savingfood.model.ClusterMarkerLocation;

/**
 * Created by brunolemgruber on 15/07/16.
 */

public class ClusterRenderer extends DefaultClusterRenderer<ClusterMarkerLocation> {

    private final IconGenerator mClusterIconGenerator;
    private Context context;

    public ClusterRenderer(Context context, GoogleMap map, ClusterManager<ClusterMarkerLocation> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
        mClusterIconGenerator = new IconGenerator(context);
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterMarkerLocation item, MarkerOptions markerOptions) {

        BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.defaultMarker(205);

        markerOptions.icon(markerDescriptor);
    }

    @Override
    protected void onClusterItemRendered(ClusterMarkerLocation clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<ClusterMarkerLocation> cluster, MarkerOptions markerOptions) {

        mClusterIconGenerator.setBackground(
                ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.circle_background));
        mClusterIconGenerator.setTextAppearance(R.style.AppTheme_WhiteTextAppearance);

        final Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }
}
