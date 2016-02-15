package ragone.io.quietmind;

import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.lantouzi.wheelview.WheelView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WheelView wheelView;
    private CountDownTimer timer;
    private int selectedTime;
    private SwitchCompat bellsSwitch;
    private List<ImageView> days;
    private int currentDay;
    private float brightness;
    private SoundPool soundPool;
    private int sound;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        days = new ArrayList<>();
        days.add((ImageView) findViewById(R.id.sunday_check));
        days.add((ImageView) findViewById(R.id.monday_check));
        days.add((ImageView) findViewById(R.id.tuesday_check));
        days.add((ImageView) findViewById(R.id.wednesday_check));
        days.add((ImageView) findViewById(R.id.thursday_check));
        days.add((ImageView) findViewById(R.id.friday_check));
        days.add((ImageView) findViewById(R.id.saturday_check));

        Calendar calendar = Calendar.getInstance();
        currentDay = calendar.get(Calendar.DAY_OF_WEEK);

//        bellsSwitch = (SwitchCompat) findViewById(R.id.enableBells);
        final PlayPauseView view = (PlayPauseView) findViewById(R.id.play_pause_view);
        view.toggle();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.toggle();
                if(view.getDrawable().isPlay()) {
                    setInputFieldEnabled(false);
                    MediaPlayer.create(MainActivity.this, R.raw.bell2).start();
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
                        }
                    }.start();
                } else {
                    timer.cancel();
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
        wheelView.selectIndex(14);
    }

    private void setInputFieldEnabled(boolean isEnabled) {
//        bellsSwitch.setEnabled(isEnabled);
        wheelView.setEnabled(isEnabled);
    }
}
