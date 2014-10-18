package demo;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

import java.lang.reflect.Method;

import hugo.weaving.DebugLog;
import timber.log.Timber;

public class MzWindowMoveHelper {
    public static final int SHOWWP = 0, INIT = 1, MOVESHOW = 2, MOVEWIN = 3, RESET = 4;

    private WindowManager wm;
    boolean isPrepare = false;
    boolean isInited = false;

    @DebugLog
    public void prepare(Context context) {
        isPrepare = true;
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        try {
            Method method = WindowManager.class.getMethod("moveTopAppWinow", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE});
            method.invoke(wm, SHOWWP, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DebugLog
    public void init() {
        isInited = true;
        try {
            Method method = WindowManager.class.getMethod("moveTopAppWinow", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE});
            method.invoke(wm, INIT, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DebugLog
    public void move(int x, int y) {
        if(isInited) {
            try {
                Method method = WindowManager.class.getMethod("moveTopAppWinow", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE});
                method.invoke(wm, MOVESHOW, x, y);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @DebugLog
    public void update() {
        if(isInited) {
            try {
                Method method = WindowManager.class.getMethod("moveTopAppWinow", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE});
                method.invoke(wm, MOVEWIN, 0, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @DebugLog
    public void reset() {
        if(!isPrepare){
            Timber.e("reset error : have been reseted one more times");
            return;
        }
        try {
            Method method = WindowManager.class.getMethod("moveTopAppWinow", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE});
            method.invoke(wm, RESET, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        wm = null;
        isPrepare = false;
        isInited = false;
    }

    @DebugLog
    public Point getPoint() {
        Point point = null;
        try {
            Method method = WindowManager.class.getMethod("getMovedWinPoint");
            point = (Point) method.invoke(wm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (point == null) {
            point = new Point(0, 0);
        }
        return point;
    }

    public boolean getPrepared() {
        return isPrepare;
    }

    public boolean getInited() {
        return isInited;
    }

}
