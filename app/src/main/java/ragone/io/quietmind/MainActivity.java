package ragone.io.quietmind;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.lantouzi.wheelview.WheelView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String MY_PREF = "my_prefs";
    private final String STREAK = "streak";
    private final String TIME = "time";
    private final String LAST_DAY = "lastday";
    private WheelView wheelView;
    private CountDownTimer timer;
    private int selectedTime;
    private List<SmoothCheckBox> days;
    private String lastDay;
    private int count = 0;
    private CoordinatorLayout coordinatorLayout;
    private MediaPlayer mediaPlayer;
    private int streak;
    private PlayPauseView playPauseView;
    private LinearLayout dayLayout;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupDays();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        wheelView = (WheelView) findViewById(R.id.wheel);
        playPauseView = (PlayPauseView) findViewById(R.id.play_pause_view);

        setupPlayPauseButton();
        setupWheel();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setupPlayPauseButton() {
        playPauseView.toggle();
        playPauseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                days.get(0).setText("12");

                playPauseView.toggle();
                if (playPauseView.getDrawable().isPlay()) {
                    setInputFieldEnabled(false);
                    showSnackBar();
                    mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.bell2);
                    mediaPlayer.start();
                    selectedTime = wheelView.getSelectedPosition() + 1;
                    timer = new myCountDownTimer(selectedTime * 60000, 1000).start();
                } else {
                    timer.cancel();
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    setInputFieldEnabled(true);
                    wheelView.smoothSelectIndex(selectedTime - 1);
                }
            }
        });
    }

    private void setupWheel() {
        List<String> data = new LinkedList<>();
        for (int i = 1; i <= 60; i++) {
            data.add(String.valueOf(i));
        }
        wheelView.setItems(data);
        wheelView.selectIndex(getTime());
        wheelView.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
            @Override
            public void onWheelItemChanged(WheelView wheelView, int position) {
            }

            @Override
            public void onWheelItemSelected(WheelView wheelView, int position) {
                saveData();
                Log.v("Wheel", "onWheelItemSelected");
            }
        });
    }

    private void setupDays() {
        days = new ArrayList<>();
        dayLayout = (LinearLayout) findViewById(R.id.dayLayout);

        streak = getStreak();
        if(!getLastDay().equals(getYesterday()) && !getLastDay().equals(getCurrentDay())) {
            streak = 0;
        }
        int streakRemain = streak % 7;
        int dayStart = streak - streakRemain;

        for (int i = dayStart; i < dayStart + 7; i++) {
            SmoothCheckBox checkBox = new SmoothCheckBox(MainActivity.this);
            checkBox.setText("" + (i + 1));
            checkBox.setEnabled(false);
            int a = CompatUtils.dp2px(MainActivity.this, 30);
            int b = CompatUtils.dp2px(MainActivity.this, 6);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(a, a);
            params.setMargins(b, b, b, b);
            checkBox.setLayoutParams(params);

            if(i < streak) {
                checkBox.setChecked(true);
            }
            dayLayout.addView(checkBox);
            days.add(checkBox);
        }
    }

    private void setInputFieldEnabled(boolean isEnabled) {
        wheelView.setEnabled(isEnabled);
    }

    private void saveData() {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREF, MODE_PRIVATE).edit();
        editor.putInt(TIME, wheelView.getSelectedPosition());
        editor.putInt(STREAK, streak);
        editor.putString(LAST_DAY, lastDay);
        editor.commit();
    }

    private int getTime() {
        SharedPreferences prefs = getSharedPreferences(MY_PREF, MODE_PRIVATE);
        return prefs.getInt(TIME, 14);
    }

    private int getStreak() {
        SharedPreferences prefs = getSharedPreferences(MY_PREF, MODE_PRIVATE);
        return prefs.getInt(STREAK, 0);
    }

    private String getLastDay() {
        SharedPreferences prefs = getSharedPreferences(MY_PREF, MODE_PRIVATE);
        return prefs.getString(LAST_DAY, "");
    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Take a deep breath...", Snackbar.LENGTH_LONG);

        snackbar.show();
    }

    private boolean isAllDaysCompleted() {
        boolean result = true;
        for (SmoothCheckBox day : days) {
            if (!day.isChecked()) {
                result = false;
                break;
            }
        }
        return result;
    }

    private String getCurrentDay() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(cal.getTime());
    }

    private String getYesterday() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://ragone.io.quietmind/http/host/path")
        );
        AppIndex.AppIndexApi.start(mClient, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://ragone.io.quietmind/http/host/path")
        );
        AppIndex.AppIndexApi.end(mClient, viewAction);
        mClient.disconnect();
    }

    private class myCountDownTimer extends CountDownTimer {
        public myCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int timeLeftInMinutes = (int) Math.ceil(millisUntilFinished / 60000);
            wheelView.smoothSelectIndex(timeLeftInMinutes);
        }

        @Override
        public void onFinish() {
            MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.bell1);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                int maxCount = 2;

                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (count < maxCount) {
                        count++;
                        mediaPlayer.seekTo(0);
                        mediaPlayer.start();
                    } else {
                        mediaPlayer.release();
                        count = 0;
                    }
                }
            });
            mediaPlayer.start();
            playPauseView.toggle();
            setInputFieldEnabled(true);
            wheelView.smoothSelectIndex(selectedTime - 1);
            if(!getLastDay().equals(getCurrentDay()) && getLastDay().equals(getYesterday())) {
                streak++;
                days.get(streak % 7 - 1).setChecked(true, true);
            }
            lastDay = getCurrentDay();
            saveData();
            if (isAllDaysCompleted()) {
                // Show dialog to increase time
            }
        }
    }
}
