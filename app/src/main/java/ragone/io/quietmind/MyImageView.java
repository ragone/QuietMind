package ragone.io.quietmind;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.Calendar;

public class MyImageView extends ImageView {

    private boolean isCompleted = false;

    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
}
