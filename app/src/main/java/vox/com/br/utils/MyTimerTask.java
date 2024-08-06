package vox.com.br.utils;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import java.util.TimerTask;


public class MyTimerTask extends TimerTask {

    public boolean hasStarted = false;

    @Override
    public void run() {
        this.hasStarted = true;
        Log.e(TAG, "executado! " );
    }

    public boolean hasRunStarted() {
        return this.hasStarted;
    }

}
