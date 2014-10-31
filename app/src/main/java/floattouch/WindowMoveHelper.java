package floattouch;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

import java.util.concurrent.atomic.AtomicBoolean;


public class WindowMoveHelper {
    public static final int SHOWWP = 0, INIT = 1, MOVESHOW = 2, MOVEWIN = 3, RESET = 4;
    private AtomicBoolean isPrepare = new AtomicBoolean(false);
    private AtomicBoolean isInited = new AtomicBoolean(false);
    private WindowManager wm;

    public void prepare(Context context) {
        if (isPrepare.compareAndSet(false, true)) {
            wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.moveTopAppWinow(SHOWWP, 0, 0);
        }
    }

    public void init() {
        if (isPrepare.get()) {
            if (isInited.compareAndSet(false, true)) {
                wm.moveTopAppWinow(INIT, 0, 0);
            }
        }
    }

    public void move(int x, int y) {
        if (isInited.get()) {
            wm.moveTopAppWinow(MOVESHOW, x, y);
        }
    }

    public void update() {
        if (isInited.get()) {
            wm.moveTopAppWinow(MOVEWIN, 0, 0);
        }
    }

    public void reset() {
        if (isPrepare.compareAndSet(true, false)) {
            wm.moveTopAppWinow(RESET, 0, 0);
            wm = null;
            isInited.set(false);
        }
    }


    public boolean getIsPrepared() {
        return isPrepare.get();
    }

    public boolean getIsInited() {
        return isInited.get();
    }


    public static Point getPoint(Context context) {
        Point point = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getMovedWinPoint();
        if (point == null) {
            point = new Point(0, 0);
        }
        return point;
    }

    public static void setListener(Context context, final MoveListener listener) {
        WindowManager.MoveWinListener moveWinListener = new WindowManager.MoveWinListener() {
            @Override
            public void toResetMovedWin() {
                if (listener != null) {
                    listener.toResetMovedWin();
                }
            }

            @Override
            public void movedWinHasBeenReset() {
                if (listener != null) {
                    listener.movedWinHasBeenReset();
                }
            }
        };
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).setMoveWinListener(moveWinListener);
    }

    public interface MoveListener {
        public void toResetMovedWin();

        public void movedWinHasBeenReset();

    }

}
