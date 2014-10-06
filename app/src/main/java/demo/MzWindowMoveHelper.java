package demo;

import android.content.Context;
import android.view.WindowManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MzWindowMoveHelper {

    public static void move(Context context, int offsetX, int offsetY) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        try {
            Method method = wm.getClass().getMethod("moveTopAppShow", new Class[]{Integer.TYPE, Integer.TYPE});
            method.invoke(wm, offsetX, offsetY);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    public static void update(Context context, int offsetX, int offsetY) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        try {
            Method method = wm.getClass().getMethod("moveTopAppWin", new Class[]{Integer.TYPE, Integer.TYPE});
            method.invoke(wm, offsetX, offsetY);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
