package demo;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;

import cc.kenai.demo.R;

/**
 * Created by kenai on 14/10/11.
 */
public class FlipStable extends MzTouch {
    public FlipStable(Context context) {
        super(context);
    }

    public final void initForStable() {
        mainTouchListener = new MainTouchListener();
        mMainButton.setOnTouchListener(mainTouchListener);

        mainViewHelper.changeState(State.SHOW_HOLD);
    }

    public final void destroy() {
        mainViewHelper.changeState(State.DISMISS);
    }


    class MainTouchListener extends MzTouch.MainTouchListener {
        public final void showMainView() {
            if (isShow) {
                return;
            }
            isShow = true;

            mMoveShow.setVisibility(View.INVISIBLE);
            mStableShow.setImageResource(R.drawable.ic_launcher);
            mStableShow.setScaleX(1f);
            mStableShow.setScaleY(1f);

            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.width = 150;
            layoutParams.height = 150;
            layoutParams.x = dm.widthPixels / 2;
            layoutParams.y = dm.heightPixels / 2;
            layoutParams.gravity = Gravity.START | Gravity.TOP;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.addView(mTopViewGroup, layoutParams);

            YoYo.with(Techniques.Tada)
                    .duration(500)
                    .withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mainTouchListener.canTouch = true;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mainTouchListener.canTouch = true;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).playOn(mStableShow);
            detector.prepare();
            detector.setIsLongpressEnabled(true);
        }

        public final void dismissMainView() {
            if (!isShow) {
                return;
            }
            isShow = false;
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.removeView(mTopViewGroup);
            detector.mayReset();
        }


        final MyGestureDetector detector = new MyGestureDetector(context, new MyOnGestureListener());
        final MoveGestureDetector moveDetector = new MoveGestureDetector(context, new MoveOnGestureListener());


        public boolean canTouch = true;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (canTouch) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    detector.end();
                }
                if (event.getAction() != MotionEvent.ACTION_DOWN) {
                    if (detector.listener.moveState) {
                        moveDetector.onTouchEvent(event);
                        return true;
                    }
                }
                detector.onTouchEvent(event);

                return true;
            } else {
                return false;
            }
        }

        class MyGestureDetector extends GestureDetector {
            public MyOnGestureListener listener = new MyOnGestureListener();

            public MyGestureDetector(Context context, MyOnGestureListener listener) {
                super(context, listener);
                this.listener = listener;

            }

            void prepare() {
                listener.prepare();
            }

            void mayReset() {
                if (!haveMoved) {
                    listener.mayReset();
                }
            }


            public void end() {
                listener.end();
            }
        }

        class MoveGestureDetector extends GestureDetector {

            public MoveGestureDetector(Context context, MoveOnGestureListener listener) {
                super(context, listener);

            }


        }

        class MoveOnGestureListener extends GestureDetector.SimpleOnGestureListener {
            float startY;
            boolean state;
            boolean afterHaveMoved = false;

            @Override
            public boolean onDown(MotionEvent e) {
                startY = e.getY();
                state = false;
                if (haveMoved) {
                    afterHaveMoved = true;
                }
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                WindowManager.LayoutParams params = (WindowManager.LayoutParams) mTopViewGroup
                        .getLayoutParams();
                params.x = (int) (e2.getRawX() - params.width / 2);
                params.y = (int) (e2.getRawY() - params.height / 2);
                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                wm.updateViewLayout(mTopViewGroup, params);

                return true;
            }

        }

        class MyOnGestureListener implements GestureDetector.OnGestureListener {

            final TargetViewHelper targetViewHelper = new TargetViewHelper();

            public boolean moveState = false;

            boolean state = false;
            float moveX,
                    moveY, totalY;

            void prepare() {
                targetViewHelper.prepare();
            }

            void mayReset() {
                if (!haveMoved) {
                    targetViewHelper.reset();
                }
            }

            void first() {
                state = true;
                mMoveShow.setVisibility(View.VISIBLE);
                mStableShow.setImageResource(R.drawable.round_2);
                moveX = moveY = 0;
                if (!targetViewHelper.getInited()) {
                    targetViewHelper.prepare();
                    targetViewHelper.init();
                }
            }

            public void end() {
                state = false;
                moveState = false;
                mStableShow.setImageResource(R.drawable.ic_launcher);
                mStableShow.animate().scaleY(1f).scaleX(1f).start();
                targetViewHelper.normalDelay(handler, 2000);
            }

            @Override
            public boolean onDown(MotionEvent e) {
                first();
                moveState = false;
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
                changeMainShowScale(-distanceX, -distanceY);
                return true;
            }


            @Override
            public void onLongPress(MotionEvent e) {
                moveState = true;
                Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(100);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (velocityY < -50 || Math.abs(velocityX) > 50) {
                    if (velocityX == 0 || Math.abs(velocityY / velocityX) > 3) {
                        targetViewHelper.moveTo(0, 600);
                        targetViewHelper.normalDelay(handler, 3000);
                    } else if (velocityX < -50) {
                        targetViewHelper.moveTo(-300, 600);
                    } else if (velocityX > 50) {
                        targetViewHelper.moveTo(300, 600);
                    }
                }

                return true;
            }

            final void changeMainShowScale(float x, float y) {
                moveX += x;
                moveY += y;


                float f = 1 - (moveX * moveX + moveY * moveY) / 500000f;
                if (f > 0.3f) {
                }

                if (f > 0) {
                    mStableShow.setScaleX(f);
                    mStableShow.setScaleY(f);
                }


            }
        }


    }
}
