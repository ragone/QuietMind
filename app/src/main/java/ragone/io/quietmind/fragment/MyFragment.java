package ragone.io.quietmind.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.relex.circleindicator.CircleIndicator;
import ragone.io.quietmind.MyPagerAdapter;
import ragone.io.quietmind.R;

public class MyFragment extends Fragment {

    private static final String LAST_VIEWED_STAGE = "last_viewed_stage";
    private static final String MY_PREF = "my_prefs";

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.viewpager, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ViewPager viewpager = (ViewPager) view.findViewById(R.id.viewpager);
        CircleIndicator indicator = (CircleIndicator) view.findViewById(R.id.indicator);
        viewpager.setAdapter(new MyPagerAdapter(view.getContext()));
        indicator.setViewPager(viewpager);
        SharedPreferences prefs = view.getContext().getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
        int lastViewed = prefs.getInt(LAST_VIEWED_STAGE, 0);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                SharedPreferences.Editor editor = getContext().getSharedPreferences(MY_PREF, Context.MODE_PRIVATE).edit();
                editor.putInt(LAST_VIEWED_STAGE, position);
                editor.commit();
            }

            @Override
            public void onPageSelected(int position) {
                SharedPreferences.Editor editor = getContext().getSharedPreferences(MY_PREF, Context.MODE_PRIVATE).edit();
                editor.putInt(LAST_VIEWED_STAGE, position);
                editor.commit();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewpager.setCurrentItem(lastViewed);

    }
}
