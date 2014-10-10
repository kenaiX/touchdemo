package demo;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import hugo.weaving.DebugLog;

public class MzWindowMoveHelper {
    public static final int INIT = 0, MOVESHOW = 1, MOVEWIN = 2, RESET = 3;

    private WindowManager wm;

    @DebugLog
    public void init(Context context) {
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        try {
            Method method = WindowManager.class.getMethod("moveTopAppWinow", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE});
            method.invoke(wm, INIT, 0, 0);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @DebugLog
    public void move(int x, int y) {
        try {
            Method method = WindowManager.class.getMethod("moveTopAppWinow", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE});
            method.invoke(wm, MOVESHOW, x, y);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @DebugLog
    public void update() {
        try {
            Method method = WindowManager.class.getMethod("moveTopAppWinow", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE});
            method.invoke(wm, MOVEWIN, 0, 0);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @DebugLog
    public void reset() {
        try {
            Method method = WindowManager.class.getMethod("moveTopAppWinow", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE});
            method.invoke(wm, RESET, 0, 0);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        wm = null;
    }
@DebugLog
    public Point getPoint(){
        Point point=null;
        try {
            Method method = WindowManager.class.getMethod("getMovedWinPoint");
            point= (Point) method.invoke(wm);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if(point==null){
            point=new Point(0,0);
        }
        return point;
    }

}
