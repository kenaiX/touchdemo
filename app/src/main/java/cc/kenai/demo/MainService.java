
package cc.kenai.demo;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import demo.FlipStable;
import demo.MzTouch;
import demo.TouchStable;
import hugo.weaving.DebugLog;

public class MainService extends Service implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    MzTouch tools;
    SharedPreferences preferences;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = getSharedPreferences("demo", 0);
        preferences.registerOnSharedPreferenceChangeListener(this);
        changeDemo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        if (tools != null) {
            tools.destroy();
            tools = null;
        }
    }


    @DebugLog
    String changeDemo() {
        if (tools != null) {
            tools.destroy();
            tools = null;
        }
        switch (Integer.valueOf(preferences.getString("demo", "" + 0))) {
            case 0:
                tools=new TouchStable(this);
                ((TouchStable)tools).initForStable();
                return "stable";
            case 1:
            default:
                tools = new MzTouch(this);
                tools.initForFree();
                return "bottom";
            case 2:
                tools=new FlipStable(this);
                ((FlipStable)tools).initForStable();
                return "free";
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("demo")) {
            MainService.this.changeDemo();
        }
    }
}
