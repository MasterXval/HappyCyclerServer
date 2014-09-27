package com.happycyclerserver.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends Activity {

    @InjectView(R.id.start_button) Button mStartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @OnClick(R.id.start_button)
    public void onStartButtonClicked() {
        if (CyclerBleServer.get().isRunning()) {
            CyclerBleServer.get().stop();
            mStartButton.setText(getString(R.string.start));
            return;
        }

        CyclerBleServer.get().start();
        mStartButton.setText(getString(R.string.stop));
    }

}
