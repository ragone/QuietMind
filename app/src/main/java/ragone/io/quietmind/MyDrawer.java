package ragone.io.quietmind;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;

import com.github.amlcurran.showcaseview.MaterialShowcaseDrawer;

import java.util.List;

/**
 * Created by ragone on 1/03/16.
 */
public class MyDrawer extends MaterialShowcaseDrawer {


    private final Paint eraserPaint;
    private final Context context;
    private float left;
    private float top;
    private float right;
    private float bottom;
    private int counter = 0;
    private List<SmoothCheckBox> days;
    private float loc1x;
    private float loc2x;

    public MyDrawer(Resources resources, Context context, List<SmoothCheckBox> days) {
        super(resources);

        this.context = context;
        this.days = days;
        Rect corner1 = new Rect();
        days.get(0).getLocalVisibleRect(corner1);
        Rect corner2 = new Rect();
        days.get(6).getLocalVisibleRect(corner2);

        loc1x = (float) corner1.left;
        loc2x = (float) corner2.right;

        this.eraserPaint = new Paint();
        this.eraserPaint.setColor(0xFFFFFF);
        this.eraserPaint.setAlpha(0);
        this.eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        this.eraserPaint.setAntiAlias(true);
    }

    public void showcaseChanger(Canvas bufferCanvas, float x, float y) {
        Log.v("Location", loc1x + "");
        Log.v("Location", loc2x + "");
        if(counter == 0) {
            left = x - CompatUtils.dp2px(context, 200);
            Log.v("Location", left + "");
            top = y - CompatUtils.dp2px(context, 30);
            right = x + CompatUtils.dp2px(context, 200);
            bottom = y + CompatUtils.dp2px(context, 20);
        } else if(counter == 1) {
            left = x - CompatUtils.dp2px(context, 30);
            top = y - CompatUtils.dp2px(context, 20);
            right = x + CompatUtils.dp2px(context, 160);
            bottom = y + CompatUtils.dp2px(context, 20);
        } else {
            left = x - CompatUtils.dp2px(context, 30);
            top = y - CompatUtils.dp2px(context, 20);
            right = x + CompatUtils.dp2px(context, 30);
            bottom = y + CompatUtils.dp2px(context, 20);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bufferCanvas.drawRoundRect(left, top, right, bottom, 200, 200, eraserPaint);
        } else {
            bufferCanvas.drawRect(left, top, right, bottom, eraserPaint);
        }
    }

    @Override
    public void drawShowcase(Bitmap buffer, float x, float y, float scaleMultiplier) {
        Canvas bufferCanvas = new Canvas(buffer);
//        bufferCanvas.drawCircle(x, y, super.getBlockedRadius(), eraserPaint);
        showcaseChanger(bufferCanvas, x, y);
        counter++;
    }
}
