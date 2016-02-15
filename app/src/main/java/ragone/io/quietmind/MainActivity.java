package ragone.io.quietmind;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lantouzi.wheelview.WheelView;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView monday;
    private WheelView wheelView;
    private CountDownTimer timer;
    private int selectedTime;
    private SwitchCompat bellsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bellsSwitch = (SwitchCompat) findViewById(R.id.enableBells);
        final PlayPauseView view = (PlayPauseView) findViewById(R.id.play_pause_view);
        view.toggle();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.toggle();
                if(view.getDrawable().isPlay()) {
                    wheelView.setEnabled(false);
                    bellsSwitch.setEnabled(false);
                    selectedTime = wheelView.getSelectedPosition() + 1;
                    timer = new CountDownTimer(selectedTime * 60000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            wheelView.smoothSelectIndex((int) Math.ceil(millisUntilFinished / 60000));
                            Log.v("Timer", String.valueOf(millisUntilFinished));
                        }

                        @Override
                        public void onFinish() {
                            view.toggle();
                            bellsSwitch.setEnabled(true);
                            wheelView.smoothSelectIndex(selectedTime - 1);
                        }
                    }.start();
                } else {
                    timer.cancel();
                    wheelView.setEnabled(true);
                    bellsSwitch.setEnabled(true);
                    wheelView.smoothSelectIndex(selectedTime - 1);
                }
            }
        });

        monday = (ImageView) findViewById(R.id.monday_check);
        monday.setImageResource(R.drawable.check_red);

        wheelView = (WheelView) findViewById(R.id.wheel);
        List<String> data = new LinkedList<>();
        for(int i = 1; i <= 60; i++) {
            data.add(String.valueOf(i));
        }
        wheelView.setItems(data);
        wheelView.selectIndex(14);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
