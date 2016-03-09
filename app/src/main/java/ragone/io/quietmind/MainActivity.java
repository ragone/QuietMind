package ragone.io.quietmind;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ragone.io.quietmind.animation.GuillotineAnimation;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String MY_PREF = "my_prefs";
    private static final String VIPASSANA = "vipassana";
    private static final String FIRST_TIME = "first_time";
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
    private ShowcaseView scv;
    private int counter = 0;
    private MyDrawer myDrawer;
    private boolean firstTime;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager notificationManager;
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = (LinearLayout) findViewById(R.id.main_layout);
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


        if (isFirstTime()) {
            showShowcase();
        }

        ImageView btn = (ImageView) findViewById(R.id.content_hamburger);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StatsActivity.class);
                startActivity(intent);
            }
        });
    }


    private boolean isFirstTime() {
        SharedPreferences prefs = getSharedPreferences(MY_PREF, MODE_PRIVATE);
        return prefs.getBoolean(FIRST_TIME, true);
    }


    private void showShowcase() {
        Handler handler = new Handler();
        for (int i = 1; i <= days.size(); i++) {
            final int finalI = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(counter == 0) {
                        days.get(finalI - 1).setChecked(true, true);
                    }
                }
            }, 1000 * i);
        }

        ViewTarget target = new ViewTarget(R.id.dayLayout, this);
        myDrawer = new MyDrawer(getResources(), MainActivity.this, days);

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
                scv.setContentTitle("Vipassanā Mode!");
                scv.setContentText("Fixed 60 minutes meditation by S. N. Goenka. Sadhu! Sadhu! Sadhu!");

                Handler handler = new Handler();
                for (int i = 1; i<=days.size() ;i++) {
                    final int finalI = i;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SmoothCheckBox checkBox = days.get(finalI - 1);
                            if (checkBox.isChecked()) {
                                checkBox.setChecked(false, true);
                            }
                        }
                    }, 100 * i);
                }
                break;
            case 1:
                scv.hide();
                firstTime = false;
                saveData();
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
                    setupNotification();
                } else {
                    timer.cancel();
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    setInputFieldEnabled(true);
                    wheelView.smoothSelectIndex(selectedIndex);
                    removeNotification();
                }
            }
        });
    }

    private void removeNotification() {
        notificationManager.cancel(001);
    }

    private void setupNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        1,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("Meditation in progress")
                        .setContentText("Time left: " + wheelView.getSelectedPosition() + 1)
                        .setColor(getResources().getColor(R.color.float_color))
                        .setOngoing(true)
                        .setShowWhen(false)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentIntent(resultPendingIntent);
        Notification notification = mBuilder.build();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(001, notification);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        notificationManager.cancel(001);
    }

    private void setupWheel() {
        List<String> data = new LinkedList<>();
        for (int i = 1; i <= 90; i++) {
            data.add(String.valueOf(i));
        }
        wheelView.setItems(data);
        wheelView.selectIndex(getTime());
        if (vipassanaMode.isChecked()) {
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
        Log.v("currentday", "" + getCurrentDay());

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

    @Override
    protected void onStart() {
        super.onStart();

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
            updateDays();
            saveData();
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
        editor.putBoolean(FIRST_TIME, firstTime);
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
//        return 6;
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

    private String getMinutesAndSeconds(long millis) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }

    private void updateDays() {
        int streakRemain = streak % 7;
        int dayStart = streak - streakRemain;

        for (int i = dayStart; i < dayStart + 7; i++) {
            SmoothCheckBox checkBox = days.get(i%7);
            checkBox.setText("" + (i + 1));
        }
    }


    private class myCountDownTimer extends CountDownTimer {
        MediaPlayer vipassanaPlayer;

        public myCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int timeLeftInMinutes = (int) Math.floor(millisUntilFinished / 60000);
            wheelView.smoothSelectIndex(timeLeftInMinutes);
            if(vipassanaPlayer == null && millisUntilFinished < 809400 && vipassanaMode.isChecked()) {
                vipassanaPlayer = MediaPlayer.create(MainActivity.this, R.raw.vipassanaend);
                vipassanaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        vipassanaPlayer.release();
                        vipassanaPlayer = null;
                    }
                });
                vipassanaPlayer.start();
            }
            mBuilder.setContentText("Time left: " + getMinutesAndSeconds(millisUntilFinished));
            int timeInMillis = (selectedIndex + 1) * 60000;
            mBuilder.setProgress(timeInMillis, timeInMillis - (int) millisUntilFinished, false);
            Notification notification = mBuilder.build();
            notificationManager.notify(001, notification);
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
                if(streak % 7 == 0) {
                    days.get(6).setChecked(true, true);
                    final SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                    pDialog.setTitleText("You're on a " + streak + " day streak!");
                    pDialog.setConfirmText("I'm awesome");
                    pDialog.setCancelable(false);
                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            pDialog.dismissWithAnimation();
                            Handler handler = new Handler();
                            for (int i = 1; i<=days.size() ;i++) {
                                final int finalI = i;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        days.get(finalI - 1).setChecked(false, true);
                                    }
                                }, 100 * i);
                            }
                        }
                    });
                    pDialog.show();
                    updateDays();
                } else {
                    days.get(streak % 7 - 1).setChecked(true, true);
                }
            }
            lastDay = getCurrentDay();
            removeNotification();
            saveData();
        }
    }
}
