package cc.kenai.demo;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import demo.MzTouch;
import evens.ChangeDemo;

public class MainService extends Service {
    MzTouch tools;
    SharedPreferences preferences;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = getSharedPreferences(getPackageName() + "_preferences", 0);
        preferences.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if (key.equals("demo")) {
                changeDemo();
            }
        });
        changeDemo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (tools != null) {
            tools.destroy();
            tools = null;
        }
    }


    void changeDemo() {
        if (tools != null) {
            tools.destroy();
            tools = null;
        }
        switch (Integer.valueOf(preferences.getString("demo", ""+ChangeDemo.DEMO_BOTTOM))) {
            case ChangeDemo.DEMO_BOTTOM:
            default:
                tools = new MzTouch(this);
                tools.initForBottom();
                break;
            case ChangeDemo.DEMO_FREE:
                tools = new MzTouch(this);
                tools.initForFree();
                break;
        }
    }

}
