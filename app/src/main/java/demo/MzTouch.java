package demo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cc.kenai.demo.R;
import de.greenrobot.event.EventBus;
import evens.ShowAllEvent;

public class MzTouch {
    final EvenListener evenListener = new EvenListener();
    final Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    showMainView();
                    break;
                case 0:
                    dismissMainView();
                    break;
            }
        }
    };
    Context context;
    final View.OnTouchListener touchListener = new View.OnTouchListener() {
        boolean state = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            handler.removeMessages(0);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                state = false;
                handler.sendEmptyMessage(0);
            } else {
                detector.onTouchEvent(event);
            }
            return true;
        }

        final GestureDetector detector = new GestureDetector(context, new GestureDetector.OnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                state = true;
                mMainButton.setImageResource(R.drawable.round_2);
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (state) {
                    move(-(int) distanceX, -(int) distanceY);
                    mMainButton.getX();
                    int x = (int) mMainButton.getX() - (int) distanceX;
                    int y = (int) mMainButton.getY() - (int) distanceY;
                    mMainButton.layout(x, y, x + mMainButton.getWidth(), y + mMainButton.getHeight());
                } else {
                    state = true;
                    mMainButton.setImageResource(R.drawable.round_2);
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });


    };
    ViewGroup mTopViewGroup;
    @InjectView(R.id.mainbutton)
    ImageView mMainButton;
    MyTouchView mTouchView;
    boolean isShow = false;
    boolean haveMoved = false;

    public final void init(final Context context) {
        this.context = context;

        EventBus.getDefault().register(evenListener);

        mTopViewGroup = (ViewGroup) View.inflate(context, R.layout.tools_mainviewgroup, null);
        ButterKnife.inject(mTopViewGroup);

        mTopViewGroup.setOnClickListener(v -> {
            handler.removeMessages(0);
            handler.sendEmptyMessage(0);
        });

        mMainButton.setOnTouchListener(touchListener);

        mTouchView = new MyTouchView(context);
        addTouchView();
    }

    public final void destroy() {
        EventBus.getDefault().unregister(evenListener);

        dismissTouchView();
    }

    final void showMainView() {
        if (isShow) {
            return;
        }
        isShow = true;
        mMainButton.setImageResource(R.drawable.round_1);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        layoutParams.format = PixelFormat.RGBA_8888;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        layoutParams.width = dm.widthPixels;
        layoutParams.height = dm.heightPixels;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.addView(mTopViewGroup, layoutParams);

        YoYo.with(Techniques.ZoomInUp)
                .duration(700)
                .playOn(mMainButton);

    }

    final void dismissMainView() {
        if (!isShow) {
            return;
        }
        isShow = false;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.removeView(mTopViewGroup);

    }

    final void addTouchView() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
        layoutParams.format = PixelFormat.RGBA_8888;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        layoutParams.width = dm.widthPixels / 5;
        layoutParams.height = 100;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.addView(mTouchView, layoutParams);
    }

    final void dismissTouchView() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.removeView(mTouchView);
    }

    //移动屏幕显示区域
    final void move(int x, int y) {
        MzWindowMoveHelper.move(context, x, y);
        if (x == 0 && y == 0) {
            mTouchView.setBackgroundColor(Color.TRANSPARENT);
            haveMoved = false;
            mTouchView.setText("touch");
        } else {
            if (!haveMoved) {
                mTouchView.setBackgroundColor(Color.argb(50, 50, 180, 230));
                haveMoved = true;
                mTouchView.setText("back");
            }
        }
    }

    final class EvenListener {
        public void onEvent(ShowAllEvent event) {
            handler.sendEmptyMessage(1);
            handler.sendEmptyMessageDelayed(0, 3000);
        }
    }

    final class MyTouchView extends TextView {
        public boolean specialModel = false;

        public MyTouchView(Context context) {
            super(context);

            setGravity(Gravity.CENTER);
            setText("touch");

            setOnTouchListener(new View.OnTouchListener() {
                final GestureDetector detector = new GestureDetector(getContext(), new MyOnGestureListener());

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (onSpecialTouch(event)) {
                        return true;
                    }
                    detector.onTouchEvent(event);
                    return true;
                }
            });
        }

        boolean onSpecialTouch(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                specialModel = false;
            }

            if (specialModel) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    handler.sendEmptyMessage(0);
                }
                touchListener.onTouch(mMainButton, event);
            }

            if (mMainButton.isShown()) {
                int[] local = new int[2];
                mMainButton.getLocationOnScreen(local);
                if (local[0] != 0 && local[1] != 0) {
                    int width = mMainButton.getWidth();
                    int height = mMainButton.getHeight();

                    int j1 = (int) (event.getRawX() - local[0]);
                    int j2 = (int) (event.getRawY() - local[1]);

                    Log.e("test", "getRawXY:" + event.getRawX() + "--" + event.getRawY());
                    Log.e("test", "getLocationOnScreen:" + local[0] + "--" + local[1]);

                    if (j1 > 0 && j1 < width && j2 > 0 && j2 < height) {
                        Log.e("test", "onSpecialTouch");
                        specialModel = true;
                        touchListener.onTouch(mMainButton, event);
                    }
                }
            }
            return false;
        }

        class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
            float startY;
            boolean state;

            @Override
            public boolean onDown(MotionEvent e) {
                startY = e.getY();
                state = false;
                if (haveMoved) {
                    move(0, 0);
                }
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (state) {
                    return true;
                }
                float nowY = e2.getY();
                if (Math.abs(nowY - startY) > 200) {
                    EventBus.getDefault().post(new ShowAllEvent());
                    state = true;
                    return false;
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        }

    }
}
