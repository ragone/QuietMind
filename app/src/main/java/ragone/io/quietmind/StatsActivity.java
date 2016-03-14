package ragone.io.quietmind;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ragone.io.quietmind.fragment.MyFragment;

public class StatsActivity extends AppCompatActivity {

    private static final String MY_PREF = "my_prefs";
    private final String STREAK = "streak";
    private final String LONGEST_STREAK = "longeststreak";
    private final String TOTAL_TIME = "totaltime";
    private final String AVERAGE_TIME = "averagetime";
    private static final String LAST_VIEWED_STAGE = "last_viewed_stage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        TextView currentStreak = (TextView) findViewById(R.id.currentstreak);
        TextView longestStreak = (TextView) findViewById(R.id.longeststreak);
        TextView totalTime = (TextView) findViewById(R.id.totaltime);
        TextView averageTime = (TextView) findViewById(R.id.averagetime);
//        ImageView imageView = (ImageView) findViewById(R.id.close_button);

        currentStreak.setText(getCurrentStreak());
        longestStreak.setText(getLongestStreak());
        totalTime.setText(getTotalTime());
        averageTime.setText(getAverageTime());
//        currentStreak.setText("25 days");
//        longestStreak.setText("38 days");
//        totalTime.setText("145.2 hours");
//        averageTime.setText("20.9 min.");
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });


        Fragment demoFragment = Fragment.instantiate(this, MyFragment.class.getName());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, demoFragment);
        fragmentTransaction.commit();

        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        int count = getSupportFragmentManager().getBackStackEntryCount();
                        ActionBar actionbar = getSupportActionBar();
                        if (actionbar != null) {
                            actionbar.setDisplayHomeAsUpEnabled(count > 0);
                            actionbar.setDisplayShowHomeEnabled(count > 0);
                        }
                    }
                });

    }

    private String getAverageTime() {
        SharedPreferences prefs = getSharedPreferences(MY_PREF, MODE_PRIVATE);
        int result = prefs.getInt(AVERAGE_TIME, 0);
        if(result == 0) {
            return "-";
        }
        return result + " min.";
    }

    private String getTotalTime() {
        SharedPreferences prefs = getSharedPreferences(MY_PREF, MODE_PRIVATE);
        int result = prefs.getInt(TOTAL_TIME, 0);
        if(result == 0) {
            return "-";
        }
        float hours = result / 60;

        if(result > 1) {
            return hours + " hours";
        } else {
            return hours + " hour";
        }
    }

    private String getLongestStreak() {
        SharedPreferences prefs = getSharedPreferences(MY_PREF, MODE_PRIVATE);
        int result = prefs.getInt(LONGEST_STREAK, 0);
        if(result == 0) {
            return "-";
        }
        if(result > 1) {
            return result + " days";
        } else {
            return result + " day";
        }
    }

    private String getCurrentStreak() {
        SharedPreferences prefs = getSharedPreferences(MY_PREF, MODE_PRIVATE);
        int result = prefs.getInt(STREAK, 0);
        if(result == 0) {
            return "-";
        }

        if(result > 1) {
            return result + " days";
        } else {
            return result + " day";
        }
    }


}
