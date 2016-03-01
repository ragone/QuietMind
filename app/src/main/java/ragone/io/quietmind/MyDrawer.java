package ragone.io.quietmind;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;

import com.github.amlcurran.showcaseview.MaterialShowcaseDrawer;

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

    public MyDrawer(Resources resources, Context context) {
        super(resources);

        this.context = context;

        this.eraserPaint = new Paint();
        this.eraserPaint.setColor(0xFFFFFF);
        this.eraserPaint.setAlpha(0);
        this.eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        this.eraserPaint.setAntiAlias(true);
    }

    public void showcaseChanger(Canvas bufferCanvas, float x, float y) {

        if(counter == 0) {
            left = x - CompatUtils.dp2px(context, 200);
            top = y - CompatUtils.dp2px(context, 20);
            right = x + CompatUtils.dp2px(context, 200);
            bottom = y + CompatUtils.dp2px(context, 20);
        } else {
            left = x - CompatUtils.dp2px(context, 30);
            top = y - CompatUtils.dp2px(context, 20);
            right = x + CompatUtils.dp2px(context, 160);
            bottom = y + CompatUtils.dp2px(context, 20);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bufferCanvas.drawRoundRect(left, top, right, bottom, 20, 20, eraserPaint);
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
