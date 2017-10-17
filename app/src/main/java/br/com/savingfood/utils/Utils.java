package br.com.savingfood.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import br.com.savingfood.R;

/**
 * Created by brunolemgruber on 14/09/16.
 */

public final class Utils {

    private static SimpleArcDialog simpleArcDialog;
    private static ArcConfiguration arcConfiguration;
    private static ImageView mIconList,mIconMap,mIconFilter,mIconNotificationOff,mIconNotificationOn,mIconRoute;

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void openKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.SHOW_FORCED);
        }
    }

    public static String getTelephoneNumber(Context context){
        TelephonyManager tMgr =(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        return mPhoneNumber;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static void openSnack(View view, String text){
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        snackbar.show();

    }

    public static void openDialog(Context context, String text){

        int[] colors = {Color.parseColor("#729762")};

        arcConfiguration = new ArcConfiguration(context);
        arcConfiguration.setLoaderStyle(SimpleArcLoader.STYLE.SIMPLE_ARC);
        arcConfiguration.setColors(colors);
        arcConfiguration.setText(text);
        simpleArcDialog = new SimpleArcDialog(context);
        simpleArcDialog.setConfiguration(arcConfiguration);
        simpleArcDialog.show();
    }

    public static void closeDialog(Context context){
        if(simpleArcDialog != null)
            simpleArcDialog.dismiss();
    }

    public static void setIconBar(EnumToolBar enumIconBar, Toolbar toolbar){

        mIconList = (ImageView) toolbar.findViewById(R.id.ic_listStore);
        mIconMap = (ImageView) toolbar.findViewById(R.id.ic_mapStore);
        mIconFilter = (ImageView) toolbar.findViewById(R.id.ic_filter);
        mIconNotificationOff = (ImageView) toolbar.findViewById(R.id.ic_notification_off);
        mIconNotificationOn = (ImageView) toolbar.findViewById(R.id.ic_notification_on);
        mIconRoute = (ImageView) toolbar.findViewById(R.id.ic_route);

        mIconList.setVisibility(View.GONE);
        mIconMap.setVisibility(View.GONE);
        mIconFilter.setVisibility(View.GONE);
        mIconNotificationOff.setVisibility(View.GONE);
        mIconNotificationOn.setVisibility(View.GONE);
        mIconRoute.setVisibility(View.GONE);

        switch (enumIconBar){
            case PRODUCTLIST:{
                break;
            }
            case STORELIST:{
                break;
            }
            case STOREMAP: {
                mIconList.setVisibility(View.VISIBLE);
                break;
            }
            case STOREMAPDIRECTIONS: {
                break;
            }
            case STOREDETAIL: {
                mIconFilter.setVisibility(View.VISIBLE);
                mIconRoute.setVisibility(View.VISIBLE);
                break;
            }
            case PRODUCTDETAIL: {
                mIconNotificationOff.setVisibility(View.VISIBLE);
                mIconNotificationOn.setVisibility(View.GONE);
                break;
            }

            default:break;
        }

    }
}
