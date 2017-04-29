package br.com.savingfood.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import br.com.savingfood.R;

/**
 * Created by brunolemgruber on 29/04/17.
 */

public class CustomTextView extends android.support.v7.widget.AppCompatTextView {
    private int mColor;
    private Paint paint;

    public CustomTextView (Context context) {
        super(context);
        init(context);
    }

    public CustomTextView (Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomTextView (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        Resources resources = context.getResources();
        //Color
        mColor = resources.getColor(R.color.red);

        paint = new Paint();
        paint.setColor(mColor);
        //Width
        paint.setStrokeWidth(5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0, 25, getWidth(), 25, paint);
    }
}