package br.com.savingfood.utils;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by brunolemgruber on 10/10/2017.
 */

public class GoogleApiSingleton {

    private static final String TAG = "GoogleApiClient";
    private static GoogleApiSingleton instance = null;

    private static GoogleApiClient mGoogleApiClient = null;

    protected GoogleApiSingleton() {

    }

    public static GoogleApiSingleton getInstance(GoogleApiClient aGoogleApiClient) {

        if(instance == null) {
            instance = new GoogleApiSingleton();

            if (mGoogleApiClient == null)
                mGoogleApiClient = aGoogleApiClient;
        }

        return instance;
    }

    public GoogleApiClient get_GoogleApiClient(){
        return mGoogleApiClient;
    }
}
