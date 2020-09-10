package com.bawp.babyneeds;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
public class MyBroadcastReciver extends BroadcastReceiver {
    long[] pattern = {0,1000,500,1000,500,1000,500,1000,500,1000,500};
    @Override
    public void onReceive(Context context, Intent intent) {
        Vibrator vibrator = (Vibrator) context.getSystemService
                (context.VIBRATOR_SERVICE);

            vibrator.vibrate(pattern,-1);
        }
    }

