package com.runningmusic.utils;

import android.graphics.Bitmap;
import android.view.View;

/**
 * Created by guofuming on 29/2/16.
 */
public class ImageUtil {
    public static String TAG = ImageUtil.class.getName();

    public static Bitmap createBitmapByView(View view) {
        view.setDrawingCacheEnabled(true);
        // view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
        // MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        // view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        // view.buildDrawingCache(true);

        Bitmap temBitmap = view.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(temBitmap);

        // Bitmap bitmap = Bitmap.createBitmap( view.getLayoutParams().width,
        // view.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        // Canvas c = new Canvas(bitmap);
        // view.layout(0, 0, view.getLayoutParams().width,
        // view.getLayoutParams().height);
        // view.draw(c);
        //
        // view.setDrawingCacheEnabled(false);
        return bitmap;
    }

}
