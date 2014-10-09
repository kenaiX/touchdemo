
package cc.kenai.demo;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import demo.MzTouch;
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
        preferences = getSharedPreferences(getPackageName() + "_preferences", 0);
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

    final static int DEMO_BOTTOM = 1, DEMO_FREE = 2;

    @DebugLog
    String changeDemo() {
        if (tools != null) {
            tools.destroy();
            tools = null;
        }
        switch (Integer.valueOf(preferences.getString("demo", "" + DEMO_BOTTOM))) {
            case DEMO_BOTTOM:
            default:
                tools = new MzTouch(this);
                tools.initForBottom();
                return "bottom";
            case DEMO_FREE:
                tools = new MzTouch(this);
                tools.initForFree();
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
