package floattouch;

import android.app.Instrumentation;
import android.os.Looper;
import android.view.KeyEvent;

public class BackSimulate {
    public static void back() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Instrumentation inst = new Instrumentation();
                inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                Looper.loop();
            }
        }).start();
    }
}
