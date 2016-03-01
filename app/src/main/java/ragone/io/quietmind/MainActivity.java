package ragone.io.quietmind;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.lantouzi.wheelview.WheelView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String MY_PREF = "my_prefs";
    private static final String VIPASSANA = "vipassana";
    private final String STREAK = "streak";
    private final String TIME = "time";
    private final String LAST_DAY = "lastday";
    private WheelView wheelView;
    private CountDownTimer timer;
    private int selectedIndex;
    private List<SmoothCheckBox> days;
    private String lastDay;
    private int count = 0;
    private CoordinatorLayout coordinatorLayout;
    private MediaPlayer mediaPlayer;
    private int streak;
    private PlayPauseView playPauseView;
    private LinearLayout dayLayout;
    private SwitchCompat vipassanaMode;
    private TextView bigText;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mClient;
    private ShowcaseView scv;
    private int counter = 0;
    private MyDrawer myDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        bigText = (TextView) findViewById(R.id.bigText);
        bigText.setVisibility(View.INVISIBLE);
        vipassanaMode = (SwitchCompat) findViewById(R.id.vipassanaMode);
        if(getVipassanaSelected()) {
            vipassanaMode.setChecked(true);
        }
        vipassanaMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    wheelView.smoothSelectIndex(59);
                    wheelView.setEnabled(false);

                } else {
                    wheelView.smoothSelectIndex(selectedIndex);
                    wheelView.setEnabled(true);
                }
                saveData();
            }
        });
        wheelView = (WheelView) findViewById(R.id.wheel);
        playPauseView = (PlayPauseView) findViewById(R.id.play_pause_view);

        setupDays();

        setupPlayPauseButton();
        setupWheel();

        showShowcase();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private void showShowcase() {

        ViewTarget target = new ViewTarget(R.id.dayLayout, this);
        myDrawer = new MyDrawer(getResources(), MainActivity.this);

        scv = new ShowcaseView.Builder(this)
                .setTarget(target)
                .setStyle(R.style.MyTheme)
                .setContentTitle("Streaks!")
                .setContentText("Keep track of how many days in a row you have meditated.")
                .setOnClickListener(this)
                .blockAllTouches()
                .setShowcaseDrawer(myDrawer)
                .build();
    }

    @Override
    public void onClick(View v) {
        switch (counter) {
            case 0:
                ViewTarget target2 = new ViewTarget(R.id.vipassanaMode, this);
                scv.setTarget(target2);
                scv.setContentTitle("Vipassana Mode!");
                scv.setContentText("Fixed 60 minute meditation by S. N. Goenka. Sadhu! Sadhu! Sadhu!");
                break;
            case 1:
                scv.hide();
                break;
        }
        counter++;
    }


    private void setupPlayPauseButton() {
        playPauseView.toggle();
        playPauseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPauseView.toggle();
                if (playPauseView.getDrawable().isPlay()) {
                    setInputFieldEnabled(false);
                    showSnackBar();
                    if (vipassanaMode.isChecked()) {
                        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.vipassanastart);
                    } else {
                        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.bell2);
                    }
                    mediaPlayer.start();
                    selectedIndex = wheelView.getSelectedPosition();
                    timer = new myCountDownTimer((selectedIndex + 1) * 60000, 1000).start();
                } else {
                    timer.cancel();
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    setInputFieldEnabled(true);
                    wheelView.smoothSelectIndex(selectedIndex);
                }
            }
        });
    }

    private void setupWheel() {
        List<String> data = new LinkedList<>();
        for (int i = 1; i <= 90; i++) {
            data.add(String.valueOf(i));
        }
        wheelView.setItems(data);
        wheelView.selectIndex(getTime());
        if(vipassanaMode.isChecked()) {
            wheelView.setEnabled(false);
        }
        wheelView.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
            @Override
            public void onWheelItemChanged(WheelView wheelView, int position) {
                saveData();
            }

            @Override
            public void onWheelItemSelected(WheelView wheelView, int position) {
                selectedIndex = position;
                saveData();
            }
        });
    }

    private void setupDays() {
        days = new ArrayList<>();
        dayLayout = (LinearLayout) findViewById(R.id.dayLayout);

        streak = getStreak();
        Log.v("streak", ""+streak);
        Log.v("lastday", ""+getLastDay());
        Log.v("yesterday", ""+getYesterday());
        Log.v("currentday", ""+getCurrentDay());
        if(!getLastDay().equals(getYesterday()) && !getLastDay().equals(getCurrentDay()) && streak != 0) {
            if(streak > 1) {
                SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
                pDialog.setTitleText("Oh no!");
                pDialog.setContentText("Your " + streak + " day streak is over!");
                pDialog.setConfirmText("Ok");
                pDialog.setCancelable(false);
                pDialog.show();
            }
            streak = 0;
            saveData();
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
        if(!vipassanaMode.isChecked()) {
            wheelView.setEnabled(isEnabled);
        }
        vipassanaMode.setEnabled(isEnabled);
    }

    private void saveData() {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREF, MODE_PRIVATE).edit();
        editor.putInt(TIME, wheelView.getSelectedPosition());
        editor.putInt(STREAK, streak);
        editor.putString(LAST_DAY, lastDay);
        editor.putBoolean(VIPASSANA, vipassanaMode.isChecked());
        editor.commit();
    }

    private boolean getVipassanaSelected() {
        SharedPreferences prefs = getSharedPreferences(MY_PREF, MODE_PRIVATE);
        return prefs.getBoolean(VIPASSANA, false);
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
//        return "11/11/2016";
        return prefs.getString(LAST_DAY, "");
    }

    private void showSnackBar() {
        bigText.setVisibility(View.VISIBLE);
        AnimationSet set1 = new AnimationSet(true);
        final AnimationSet set2 = new AnimationSet(true);
        final AlphaAnimation ani1 = new AlphaAnimation(0.0f, 1.0f);
        final AlphaAnimation ani2 = new AlphaAnimation(1.0f, 0.0f);
        final ScaleAnimation anis1 = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        final ScaleAnimation anis2 = new ScaleAnimation(1.0f, 0.5f, 1.0f, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ani1.setDuration(5000);
        ani2.setDuration(5000);
        anis1.setDuration(5000);
        anis2.setDuration(5000);

        set1.addAnimation(ani1);
        set1.addAnimation(anis1);

        set2.addAnimation(ani2);
        set2.addAnimation(anis2);

        set1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.v("", "Animation start");
                bigText.startAnimation(set2);
                bigText.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        bigText.startAnimation(set1);
//
//        Snackbar.make(coordinatorLayout, "Take a deep breath...", Snackbar.LENGTH_LONG)
//                .show();
    }

    private String getCurrentDay() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//        return "12/11/2016";
        return dateFormat.format(cal.getTime());
    }

    private String getYesterday() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        cal.add(Calendar.DATE, -1);
//        return "11/11/2016";
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
        MediaPlayer vipassanaPlayer;

        public myCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int timeLeftInMinutes = (int) Math.ceil(millisUntilFinished / 60000);
            wheelView.smoothSelectIndex(timeLeftInMinutes);
            if(vipassanaPlayer == null && millisUntilFinished < 809400 && vipassanaMode.isChecked()) {
                vipassanaPlayer = MediaPlayer.create(MainActivity.this, R.raw.vipassanaend);
                vipassanaPlayer.start();
            }
        }

        @Override
        public void onFinish() {
            if(!vipassanaMode.isChecked()) {
                mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.bell1);
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
                            mediaPlayer = null;
                            count = 0;
                        }
                    }
                });
                mediaPlayer.start();
            }
            playPauseView.toggle();
            setInputFieldEnabled(true);
            wheelView.smoothSelectIndex(selectedIndex);
            if(!getLastDay().equals(getCurrentDay()) && getLastDay().equals(getYesterday()) || streak == 0) {
                streak++;
                days.get(streak % 7 - 1).setChecked(true, true);
                if(streak % 7 == 0) {
                    SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                    pDialog.setTitleText("You're on a " + streak + " day streak!");
                    pDialog.setConfirmText("I'm awesome");
                    pDialog.setCancelable(false);
                    pDialog.show();
                }
            }
            lastDay = getCurrentDay();
            saveData();
        }
    }
}
