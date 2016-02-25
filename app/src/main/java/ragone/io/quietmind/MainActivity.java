package ragone.io.quietmind;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.lantouzi.wheelview.WheelView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String MY_PREF = "my_prefs";
    private WheelView wheelView;
    private CountDownTimer timer;
    private int selectedTime;
    private List<MyImageView> days;
    private int currentDay;
    private int count = 0;
    private CoordinatorLayout coordinatorLayout;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupDays();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        final PlayPauseView view = (PlayPauseView) findViewById(R.id.play_pause_view);
        view.toggle();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.toggle();
                if (view.getDrawable().isPlay()) {
                    setInputFieldEnabled(false);
                    showSnackBar();
                    mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.bell2);
                    mediaPlayer.start();
                    selectedTime = wheelView.getSelectedPosition() + 1;
                    timer = new CountDownTimer(selectedTime * 60000, 1000) {
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
                            view.toggle();
                            setInputFieldEnabled(true);
                            wheelView.smoothSelectIndex(selectedTime - 1);
                            days.get(currentDay - 1).setImageResource(R.drawable.check_green);
                            days.get(currentDay - 1).setIsCompleted(true);
                            saveData();
                            if(isAllDaysCompleted()) {
                                // Show dialog to increase time
                            }
                        }
                    }.start();
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
        wheelView = (WheelView) findViewById(R.id.wheel);
        List<String> data = new LinkedList<>();
        for(int i = 1; i <= 60; i++) {
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
        MyImageView sunday = (MyImageView) findViewById(R.id.sunday_check);
        MyImageView monday = (MyImageView) findViewById(R.id.monday_check);
        MyImageView tuesday = (MyImageView) findViewById(R.id.tuesday_check);
        MyImageView wednesday = (MyImageView) findViewById(R.id.wednesday_check);
        MyImageView thursday = (MyImageView) findViewById(R.id.thursday_check);
        MyImageView friday = (MyImageView) findViewById(R.id.friday_check);
        MyImageView saturday = (MyImageView) findViewById(R.id.saturday_check);

        days.add(sunday);
        days.add(monday);
        days.add(tuesday);
        days.add(wednesday);
        days.add(thursday);
        days.add(friday);
        days.add(saturday);

        sunday.setIsCompleted(isDayComplete("sunday"));
        monday.setIsCompleted(isDayComplete("monday"));
        tuesday.setIsCompleted(isDayComplete("tuesday"));
        wednesday.setIsCompleted(isDayComplete("wednesday"));
        thursday.setIsCompleted(isDayComplete("thursday"));
        friday.setIsCompleted(isDayComplete("friday"));
        saturday.setIsCompleted(isDayComplete("saturday"));

        Calendar calendar = Calendar.getInstance();
        currentDay = calendar.get(Calendar.DAY_OF_WEEK);

        // if any days after current day in week are completed,
        // new week must have started so set all days to not completed.
        boolean isNewWeek = false;
        if(currentDay != Calendar.SUNDAY) {
            for (int i = currentDay + 1; i < days.size(); i++) {
                if (days.get(i).isCompleted()) {
                    isNewWeek = true;
                    break;
                }
            }
        }

        if(isNewWeek) {
            sunday.setIsCompleted(false);
            monday.setIsCompleted(false);
            tuesday.setIsCompleted(false);
            wednesday.setIsCompleted(false);
            thursday.setIsCompleted(false);
            friday.setIsCompleted(false);
            saturday.setIsCompleted(false);
            saveData();
        }

        for(MyImageView day : days) {
            if(day.isCompleted()) {
                day.setImageResource(R.drawable.check_green);
            } else {
                day.setImageResource(R.drawable.blank);
            }
        }
    }

    private void setInputFieldEnabled(boolean isEnabled) {
        wheelView.setEnabled(isEnabled);
    }

    private void saveData() {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREF, MODE_PRIVATE).edit();
        editor.putInt("time", wheelView.getSelectedPosition());
        editor.putBoolean("sunday", days.get(0).isCompleted());
        editor.putBoolean("monday", days.get(1).isCompleted());
        editor.putBoolean("tuesday", days.get(2).isCompleted());
        editor.putBoolean("wednesday", days.get(3).isCompleted());
        editor.putBoolean("thursday", days.get(4).isCompleted());
        editor.putBoolean("friday", days.get(5).isCompleted());
        editor.putBoolean("saturday", days.get(6).isCompleted());
        editor.commit();
    }

    private int getTime() {
        SharedPreferences prefs = getSharedPreferences(MY_PREF, MODE_PRIVATE);
        return prefs.getInt("time", 14);
    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Take a deep breath...", Snackbar.LENGTH_LONG);

        snackbar.show();
    }

    private boolean isDayComplete(String day) {
        SharedPreferences prefs = getSharedPreferences(MY_PREF, MODE_PRIVATE);
        return prefs.getBoolean(day, false);
    }

    private boolean isAllDaysCompleted() {
        boolean result = true;
        for(MyImageView day : days) {
            if(!day.isCompleted()) {
                result = false;
                break;
            }
        }
        return result;
    }
}
