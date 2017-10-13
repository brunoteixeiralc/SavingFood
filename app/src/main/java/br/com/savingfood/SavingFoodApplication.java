package br.com.savingfood;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.FacebookSdk;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by brunolemgruber on 14/07/16.
 */

public class SavingFoodApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FacebookSdk.setApplicationId(getApplicationContext().getResources().getString(R.string.facebook_app_id));

        FacebookSdk.sdkInitialize(getApplicationContext());

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Nexa-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}

