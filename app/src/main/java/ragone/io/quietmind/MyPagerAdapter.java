package ragone.io.quietmind;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Random;

import ragone.io.quietmind.fragment.MyFragment;

/**
 * Created by ragone on 9/03/16.
 */
public class MyPagerAdapter extends PagerAdapter {

    private static final String MY_PREF = "my_prefs";
    private static final String INTRO = "intro";
    private static final String STAGE1 = "stage1";
    private static final String STAGE2 = "stage2";
    private static final String STAGE3 = "stage3";
    private static final String STAGE4 = "stage4";
    private static final String STAGE5 = "stage5";
    private static final String STAGE6 = "stage6";
    private static final String STAGE7 = "stage7";
    private static final String STAGE8 = "stage8";
    private static final String STAGE9 = "stage9";
    private static final String STAGE10 = "stage10";


    private final Random random = new Random();
    private final SparseArray<View> mHolderArray = new SparseArray<>();
    private Context context;

    public MyPagerAdapter(Context context) {
        this.context = context;
    }

    @Override public int getCount() {
        return 15;
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
        ScrollView theInflatedView = new ScrollView(view.getContext());
        LinearLayout linearLayout = new LinearLayout(view.getContext());


        SmoothCheckBox checkBox = new SmoothCheckBox(view.getContext());
        checkBox.setText("DONE");
        checkBox.setEnabled(true);
        int a = CompatUtils.dp2px(view.getContext(), 60);
        int b = CompatUtils.dp2px(view.getContext(), 20);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(a, a);
        params.setMargins(0, b, 0, 0);
        checkBox.setLayoutParams(params);
        final SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREF, context.MODE_PRIVATE).edit();
        SharedPreferences prefs = context.getSharedPreferences(MY_PREF, context.MODE_PRIVATE);


        switch (position) {
            case 0:
                theInflatedView = (ScrollView) inflater.inflate(R.layout.intro, null);
                break;
            case 1:
                theInflatedView = (ScrollView) inflater.inflate(R.layout.stage1, null);
                checkBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                        editor.putBoolean(STAGE1, checkBox.isChecked());
                        editor.commit();

                    }
                });
                checkBox.setChecked(prefs.getBoolean(STAGE1, false), false);
                linearLayout = (LinearLayout) theInflatedView.findViewById(R.id.layout1);
                break;
            case 2:
                theInflatedView = (ScrollView)inflater.inflate(R.layout.stage2, null);
                checkBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                        editor.putBoolean(STAGE2, checkBox.isChecked());
                        editor.commit();
                    }
                });
                checkBox.setChecked(prefs.getBoolean(STAGE2, false), false);
                linearLayout = (LinearLayout) theInflatedView.findViewById(R.id.layout2);
                break;
            case 3:
                theInflatedView = (ScrollView)inflater.inflate(R.layout.stage3, null);
                checkBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                        editor.putBoolean(STAGE3, checkBox.isChecked());
                        editor.commit();
                    }
                });
                checkBox.setChecked(prefs.getBoolean(STAGE3, false), false);
                linearLayout = (LinearLayout) theInflatedView.findViewById(R.id.layout3);
                break;
            case 4:
                theInflatedView = (ScrollView)inflater.inflate(R.layout.milestone1, null);
                break;
            case 5:
                theInflatedView = (ScrollView)inflater.inflate(R.layout.stage4, null);
                checkBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                        editor.putBoolean(STAGE4, checkBox.isChecked());
                        editor.commit();
                    }
                });
                checkBox.setChecked(prefs.getBoolean(STAGE4, false), false);
                linearLayout = (LinearLayout) theInflatedView.findViewById(R.id.layout4);
                break;
            case 6:
                theInflatedView = (ScrollView)inflater.inflate(R.layout.stage5, null);
                checkBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                        editor.putBoolean(STAGE5, checkBox.isChecked());
                        editor.commit();
                    }
                });
                checkBox.setChecked(prefs.getBoolean(STAGE5, false), false);
                linearLayout = (LinearLayout) theInflatedView.findViewById(R.id.layout5);
                break;
            case 7:
                theInflatedView = (ScrollView)inflater.inflate(R.layout.stage6, null);
                checkBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                        editor.putBoolean(STAGE6, checkBox.isChecked());
                        editor.commit();
                    }
                });
                checkBox.setChecked(prefs.getBoolean(STAGE6, false), false);
                linearLayout = (LinearLayout) theInflatedView.findViewById(R.id.layout6);
                break;
            case 8:
                theInflatedView = (ScrollView)inflater.inflate(R.layout.milestone2, null);
                break;
            case 9:
                theInflatedView = (ScrollView)inflater.inflate(R.layout.stage7, null);
                checkBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                        editor.putBoolean(STAGE7, checkBox.isChecked());
                        editor.commit();
                    }
                });
                checkBox.setChecked(prefs.getBoolean(STAGE7, false), false);
                linearLayout = (LinearLayout) theInflatedView.findViewById(R.id.layout7);
                break;
            case 10:
                theInflatedView = (ScrollView)inflater.inflate(R.layout.milestone3, null);
                break;
            case 11:
                theInflatedView =(ScrollView) inflater.inflate(R.layout.stage8, null);
                checkBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                        editor.putBoolean(STAGE8, checkBox.isChecked());
                        editor.commit();
                    }
                });
                checkBox.setChecked(prefs.getBoolean(STAGE8, false), false);
                linearLayout = (LinearLayout) theInflatedView.findViewById(R.id.layout8);
                break;
            case 12:
                theInflatedView = (ScrollView)inflater.inflate(R.layout.stage9, null);
                checkBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                        editor.putBoolean(STAGE9, checkBox.isChecked());
                        editor.commit();
                    }
                });
                checkBox.setChecked(prefs.getBoolean(STAGE9, false), false);
                linearLayout = (LinearLayout) theInflatedView.findViewById(R.id.layout9);
                break;
            case 13:
                theInflatedView = (ScrollView)inflater.inflate(R.layout.stage10, null);
                checkBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                        editor.putBoolean(STAGE10, checkBox.isChecked());
                        editor.commit();
                    }
                });
                checkBox.setChecked(prefs.getBoolean(STAGE10, false), false);
                linearLayout = (LinearLayout) theInflatedView.findViewById(R.id.layout10);
                break;
            case 14:
                theInflatedView = (ScrollView)inflater.inflate(R.layout.milestone4, null);
                break;
        }
        editor.commit();
        linearLayout.addView(checkBox);

        view.addView(theInflatedView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mHolderArray.put(position, theInflatedView);
        return theInflatedView;
    }

    @Override public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
