package br.com.savingfood.utils;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.savingfood.R;
import br.com.savingfood.activity.MainActivity;
import br.com.savingfood.model.GeoFenceModel;
import io.realm.Realm;

/**
 * Created by brunolemgruber on 10/10/2017.
 */

public class GeofenceTrasitionService extends IntentService{

    private static final String TAG = GeofenceTrasitionService.class.getSimpleName();
    public static final int GEOFENCE_NOTIFICATION_ID = 0;
    private String store;
    private String username,uid;
    private int geoFenceTransition;
    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private GeoFenceModel geoFenceModel;
    private String mLastUpdateTime;
    private Realm realm;

    public GeofenceTrasitionService() {
        super(TAG);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        uid = intent.getStringExtra("uid");
        username = intent.getStringExtra("username");
        store = intent.getStringExtra("store");

        // Handling errors
        if ( geofencingEvent.hasError() ) {
            String errorMsg = getErrorString(geofencingEvent.getErrorCode() );
            Log.e( TAG, errorMsg );
            return;
        }

        // Retrieve GeofenceTrasition
        geoFenceTransition = geofencingEvent.getGeofenceTransition();
        // Check if the transition type
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ) {
            // Get the geofence that were triggered
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            // Create a detail message with Geofences received
            String geofenceTransitionDetails = getGeofenceTrasitionDetails(geoFenceTransition, triggeringGeofences );
            // Send notification details as a String
            sendNotification( geofenceTransitionDetails );
        }
    }

    private String getGeofenceTrasitionDetails(int geoFenceTransition, List<Geofence> triggeringGeofences) {

        ArrayList<String> triggeringGeofencesList = new ArrayList<>();
        for ( Geofence geofence : triggeringGeofences ) {
            triggeringGeofencesList.add( geofence.getRequestId() );
        }

        String status = null;
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER )
            status = "Estamos quase no " + store + " !";
        else if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT )
            status = "Obrigado. ";
        return status;
    }

    private void sendNotification( String msg ) {
        Log.i(TAG, "sendNotification: " + msg );

        Intent notificationIntent = MainActivity.makeNotificationIntent(getApplicationContext(), msg);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificatioMng = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        notificatioMng.notify(GEOFENCE_NOTIFICATION_ID, createNotification(msg, notificationPendingIntent));

        mLastUpdateTime = Date.formatToString("dd/MM/yyyy HH:mm:ss",new java.util.Date());

        if(geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){

            String key = mDatabase.push().getKey();
            geoFenceModel = new GeoFenceModel(uid,store,mLastUpdateTime,"",key);
            mDatabase.child("geofence").child(uid).child(key).setValue(geoFenceModel);

            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.copyToRealm(geoFenceModel);
            realm.commitTransaction();

        }else if(geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){

            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            GeoFenceModel geoFenceModel = realm.where(GeoFenceModel.class).findFirst();
            geoFenceModel.setExit_date(mLastUpdateTime);

            mDatabase.child("geofence").child(uid).child(geoFenceModel.getKey()).setValue(geoFenceModel);

            realm.clear(GeoFenceModel.class);
            realm.commitTransaction();
        }

    }

    private android.app.Notification createNotification(String msg, PendingIntent notificationPendingIntent) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(msg)
                .setContentText(geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ? "Aproveite as ofertas! Boas compras " + username + " !" :
                "Espero que tenha feito boas compras " + username + " !")
                .setContentIntent(notificationPendingIntent)
                .setDefaults(android.app.Notification.DEFAULT_LIGHTS | android.app.Notification.DEFAULT_VIBRATE | android.app.Notification.DEFAULT_SOUND)
                .setAutoCancel(true);
        return notificationBuilder.build();
    }

    private static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown error.";
        }
    }
}

