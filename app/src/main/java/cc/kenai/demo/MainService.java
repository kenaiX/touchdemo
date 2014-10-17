package cc.kenai.demo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import demo.MzTouch;

public class MainService extends Service {
    MzTouch tools;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        tools = new MzTouch(this);
        tools.init();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        tools.destroy();
    }
}
