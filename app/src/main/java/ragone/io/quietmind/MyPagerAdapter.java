package ragone.io.quietmind;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

import ragone.io.quietmind.fragment.MyFragment;

/**
 * Created by ragone on 9/03/16.
 */
public class MyPagerAdapter extends PagerAdapter {

    private final Random random = new Random();
    private final SparseArray<View> mHolderArray = new SparseArray<>();
    private Context context;

    public MyPagerAdapter(Context context) {
        this.context = context;
    }

    @Override public int getCount() {
        return 9;
    }

    @Override public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView(mHolderArray.get(position));
    }

    @Override public Object instantiateItem(ViewGroup view, int position) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View theInflatedView = new View(view.getContext());

        switch (position) {
            case 0:
                theInflatedView = inflater.inflate(R.layout.stage1, null);
                break;
            case 1:
                theInflatedView = inflater.inflate(R.layout.stage2, null);
                break;
            case 2:
                theInflatedView = inflater.inflate(R.layout.stage3, null);
                break;
        }

        view.addView(theInflatedView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mHolderArray.put(position, theInflatedView);
        return theInflatedView;
    }

    @Override public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
