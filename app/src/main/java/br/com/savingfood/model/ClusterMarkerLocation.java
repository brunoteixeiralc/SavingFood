package br.com.savingfood.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by brunolemgruber on 15/07/16.
 */

public class ClusterMarkerLocation implements ClusterItem{

    private LatLng position;
    private String title;
    private String snippet;
    private Store store;

    public ClusterMarkerLocation(LatLng latLng, String title, String snippet, Store store ) {
        this.position = latLng;
        this.title = title;
        this.snippet = snippet;
        this.store = store;
    }

    public ClusterMarkerLocation(LatLng latLng) {
        this.position = latLng;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public Store getStore() {
        return store;
    }
}
