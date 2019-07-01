package com.update.plugin.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TestService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
