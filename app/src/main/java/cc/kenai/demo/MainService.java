package cc.kenai.demo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import floattouch.FloatTouch;

public class MainService extends Service {
    FloatTouch tools;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        tools = new FloatTouch(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        tools.destroy();
    }
}
