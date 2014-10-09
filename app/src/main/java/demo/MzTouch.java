
package demo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import cc.kenai.demo.R;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.animation.ValueAnimator;
import hugo.weaving.DebugLog;

public class MzTouch {
    final Context context;
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
    final MainViewHelper mainViewHelper = new MainViewHelper();
    final TargetViewHelper targetViewHelper = new TargetViewHelper();

    MainTouchListener touchListener;
    ViewGroup mTopViewGroup;

    ImageView mMainButton;
    ImageView mStableShow, mMoveShow;

    MyTouchView mTouchView;
    boolean isShow = false;
    boolean haveMoved = false;

    public MzTouch(final Context context) {
        this.context = context;

        mTopViewGroup = (ViewGroup) View.inflate(context, R.layout.tools_mainviewgroup, null);

        mMainButton = (ImageView) mTopViewGroup.findViewById(R.id.mainbutton);
        mMoveShow = (ImageView) mTopViewGroup.findViewById(R.id.moveshow);
        mStableShow = (ImageView) mTopViewGroup.findViewById(R.id.stableshow);

        mTopViewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewHelper.changeState(State.DISMISS);
            }
        });

    }

    public final void initForBottom() {
        mTouchView = new MyTouchViewForBottom(context);
        mTouchView.addTouchView();
    }

    public final void initForFree() {
        mTouchView = new MyTouchViewFree(context);
        mTouchView.addTouchView();
    }

    public final void destroy() {

        mTouchView.dismissTouchView();
    }

    final void showMainView() {
        if (isShow) {
            return;
        }
        isShow = true;

        mMoveShow.setVisibility(View.INVISIBLE);
        mStableShow.setImageResource(R.drawable.round_1);
        mStableShow.setScaleX(1f);
        mStableShow.setScaleY(1f);

        touchListener = new MainTouchListener();
        mMainButton.setOnTouchListener(touchListener);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        layoutParams.format = PixelFormat.RGBA_8888;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        layoutParams.width = dm.widthPixels;
        layoutParams.height = dm.heightPixels;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.gravity = Gravity.TOP | Gravity.START;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.addView(mTopViewGroup, layoutParams);

        YoYo.with(Techniques.ZoomInUp)
                .duration(700)
                .withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        touchListener.canTouch = false;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        touchListener.canTouch = true;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).playOn(mStableShow);

    }

    final void dismissMainView() {
        if (!isShow) {
            return;
        }
        isShow = false;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.removeView(mTopViewGroup);

    }

    class MainTouchListener implements View.OnTouchListener {
        public boolean canTouch = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (canTouch) {
                mainViewHelper.changeState(State.HOLD);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    detector.end();
                } else {
                    detector.onTouchEvent(event);
                }
                return true;
            } else {
                return false;
            }
        }

        class MyGestureDetector extends GestureDetector {
            MyOnGestureListener listener = new MyOnGestureListener();

            public MyGestureDetector(Context context, MyOnGestureListener listener) {
                super(context, listener);
                this.listener = listener;

            }

            public void end() {
                listener.end();
            }
        }

        class MyOnGestureListener implements GestureDetector.OnGestureListener {
            boolean state = false;
            float moveX,
                    moveY, totalY;

            void first() {
                state = true;
                mMoveShow.setVisibility(View.VISIBLE);
                mStableShow.setImageResource(R.drawable.round_2);
                moveX = moveY = 0;
            }

            public void end() {
                state = false;
                mStableShow.setImageResource(R.drawable.round_1);
                mStableShow.animate().scaleX(1f);
                mStableShow.animate().scaleY(1f);
                mainViewHelper.changeState(State.DISMISS);
                targetViewHelper.normalDelay(handler, 2000);
            }

            @Override
            public boolean onDown(MotionEvent e) {
                first();
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
                    totalY -= distanceY;
                    if (totalY > 0) {
                        int x = (int) mMoveShow.getX() - (int) distanceX;
                        int y = (int) mMoveShow.getY() - (int) distanceY;
                        mMoveShow.layout(x, y, x + mMoveShow.getWidth(), y + mMoveShow.getHeight());
                        changeMainShowScale(-distanceX, -distanceY);
                    }

                } else {
                    first();
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

            final void changeMainShowScale(float x, float y) {
                moveX += x;
                moveY += y;
                float f = 1 - ((moveX) * (moveX) + (moveY) * (moveY)) / 500000f;
                if (f > 0.3f) {
                    mStableShow.setScaleY(f);
                    mStableShow.setScaleX(f);
                    targetViewHelper.move((int) x, (int) y);
                }
            }
        }

        final MyGestureDetector detector = new MyGestureDetector(context, new MyOnGestureListener()) {

        };

    }

    public static enum State {
        SHOW, SHOW_HOLD, DISMISS, HOLD
    }

    final class MainViewHelper {
        @DebugLog
        public void changeState(State state) {
            switch (state) {
                case SHOW:
                    handler.sendEmptyMessage(1);
                    handler.sendEmptyMessageDelayed(0, 3000);
                    break;
                case SHOW_HOLD:
                    handler.removeMessages(0);
                    handler.sendEmptyMessage(1);
                    break;
                case DISMISS:
                    handler.removeMessages(0);
                    handler.sendEmptyMessage(0);
                    break;
                case HOLD:
                    handler.removeMessages(0);
                    break;
            }
        }
    }

    final class TargetViewHelper {
        int totalMoveX, totalMoveY;

        public final void move(int x, int y) {
            totalMoveX += x;
            totalMoveY += y;
            if (x != 0 || y != 0) {
                MzWindowMoveHelper.move(context, x, y);
                if (!haveMoved) {
                    mTouchView.setBackgroundColor(Color.argb(50, 50, 180, 230));
                    haveMoved = true;
                    mTouchView.setText("back");
                }
            }
        }

        public void normalDelay(Handler handler, long d) {
            handler.removeCallbacks(run);
            handler.postDelayed(run, d);
        }

        public void normal(Handler handler) {
            handler.removeCallbacks(run);
            handler.post(run);
        }

        Runnable run = new Runnable() {
            @Override
            public void run() {
                moveToNormal();
            }
        };

        public final void moveToNormal() {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
            valueAnimator.setDuration(1000);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                IntEvaluator intEvaluator = new IntEvaluator();

                int thisX = totalMoveX,
                        thisY = totalMoveY;

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float f = (float) animation.getAnimatedValue();
                    int jX = intEvaluator.evaluate(f, totalMoveX, 0);
                    int jY = intEvaluator.evaluate(f, totalMoveY, 0);
                    move(jX - thisX, jY - thisY);
                    thisX = jX;
                    thisY = jY;
                }
            });
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    moveNormal();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            valueAnimator.setInterpolator(new AccelerateInterpolator());
            valueAnimator.start();

        }

        final void moveNormal() {
            MzWindowMoveHelper.move(context, 0, 0);
            mTouchView.setBackgroundColor(Color.TRANSPARENT);
            haveMoved = false;
            mTouchView.setText("touch");
            totalMoveX = totalMoveY = 0;
        }
    }

    abstract class MyTouchView extends TextView {

        public MyTouchView(Context context) {
            super(context);
        }

        abstract void addTouchView();

        final void dismissTouchView() {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.removeView(mTouchView);
        }
    }

    final class MyTouchViewForBottom extends MyTouchView {
        public boolean specialModel = false;

        public MyTouchViewForBottom(Context context) {
            super(context);

            setGravity(Gravity.CENTER);
            setText("touch");

            setOnTouchListener(new View.OnTouchListener() {
                final GestureDetector detector = new GestureDetector(getContext(),
                        new MyOnGestureListener());

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
                    if (j1 > 0 && j1 < width && j2 > 0 && j2 < height) {
                        specialModel = true;
                        touchListener.onTouch(mMainButton, event);
                    }
                }
            }
            return false;
        }

        final void addTouchView() {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
            layoutParams.format = PixelFormat.RGBA_8888;
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            layoutParams.width = dm.widthPixels / 5;
            layoutParams.height = 100;
            layoutParams.x = 0;
            layoutParams.y = 0;
            layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.addView(mTouchView, layoutParams);
        }

        class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
            float startY;
            boolean state;

            @Override
            public boolean onDown(MotionEvent e) {
                startY = e.getY();
                state = false;
                if (haveMoved) {
                    targetViewHelper.normal(handler);
                }
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (state) {
                    return true;
                }
                float nowY = e2.getY();
                if (Math.abs(nowY - startY) > 100) {
                    mainViewHelper.changeState(State.SHOW_HOLD);
                    state = true;
                    return false;
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        }

    }

    final class MyTouchViewFree extends MyTouchView {
        public MyTouchViewFree(Context context) {
            super(context);

            setGravity(Gravity.CENTER);
            setText("touch");

            setOnTouchListener(new View.OnTouchListener() {
                final GestureDetector detector = new GestureDetector(getContext(),
                        new MyOnGestureListener());

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    detector.onTouchEvent(event);
                    return true;
                }
            });
        }

        final void addTouchView() {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
            layoutParams.format = PixelFormat.RGBA_8888;
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            layoutParams.width = dm.widthPixels / 5;
            layoutParams.height = 100;
            layoutParams.x = 0;
            layoutParams.y = 0;
            layoutParams.gravity = Gravity.START | Gravity.TOP;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.addView(mTouchView, layoutParams);
        }

        class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
            float startY;
            boolean state;
            boolean afterHaveMoved = false;

            @Override
            public boolean onDown(MotionEvent e) {
                startY = e.getY();
                state = false;
                if (haveMoved) {
                    afterHaveMoved = true;
                    targetViewHelper.normal(handler);
                }
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                WindowManager.LayoutParams params = (WindowManager.LayoutParams) MyTouchViewFree.this
                        .getLayoutParams();
                params.x = (int) (e2.getRawX() - params.width / 2);
                params.y = (int) (e2.getRawY() - params.height / 2);
                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                wm.updateViewLayout(MyTouchViewFree.this, params);

                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (!afterHaveMoved) {
                    mainViewHelper.changeState(State.SHOW);
                }
                afterHaveMoved = false;
                return true;
            }
        }

    }
}
