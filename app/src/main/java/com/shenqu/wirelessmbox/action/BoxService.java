package com.shenqu.wirelessmbox.action;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by JongLim on 2016/12/15.
 */

public class BoxService extends Service {
    private static final String TAG = "BoxService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
