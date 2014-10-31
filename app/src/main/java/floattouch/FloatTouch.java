package floattouch;

import android.animation.FloatEvaluator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;

import cc.kenai.demo.R;

@TargetApi(19)
public class FloatTouch {
    final Context context;
    final Handler handler;
    final MainTouchListener mainTouchListener;
    //control position of float-window
    final ViewGroup mTopViewGroup;
    //response to touch event
    final ImageView mMainButton;
    //todo show the state of control
    final ImageView mStableShow;

    public FloatTouch(final Context context) {
        this.context = context;
        handler = new Handler();
        mainTouchListener = new MainTouchListener();
        mTopViewGroup = (ViewGroup) View.inflate(context, R.layout.floattouch_mainviewgroup, null);
        mMainButton = (ImageView) mTopViewGroup.findViewById(R.id.mainbutton);
        mStableShow = (ImageView) mTopViewGroup.findViewById(R.id.stableshow);
        mMainButton.setOnTouchListener(mainTouchListener);

        showFloatTouch();
    }

    public void destroy() {
        dismissFloatTouch();
    }

    final void showFloatTouch() {
        SharedPreferences floattouch = context.getSharedPreferences("floattouch", 0);
        int position_x = floattouch.getInt("position_x", 0);
        int position_y = floattouch.getInt("position_y", 0);


        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.width = (int) context.getResources().getDimension(R.dimen.floattouch_show);
        layoutParams.height = layoutParams.width;
        if (position_x == 0) {
            layoutParams.x = dm.widthPixels / 2 - layoutParams.width / 2;
        } else {
            layoutParams.x = position_x;
        }
        if (position_y == 0) {
            layoutParams.y = dm.heightPixels / 2;
        }else{
            layoutParams.y = position_y;
        }
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).addView(mTopViewGroup, layoutParams);

    }

    final void dismissFloatTouch() {
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).removeView(mTopViewGroup);
    }


    //receive touch event and control the state of float-window
    final class MainTouchListener implements View.OnTouchListener {
        private boolean moveState = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                changeStableTo2();
            } else if (action == MotionEvent.ACTION_UP) {
                changeStableTo1();

                if(moveState){
                    int[] position=new int[2];
                    mTopViewGroup.getLocationOnScreen(position);
                    SharedPreferences floattouch = context.getSharedPreferences("floattouch", 0);
                    floattouch.edit().putInt("position_x",position[0]).putInt("position_y",position[1]).apply();
                }
            }

            if (event.getAction() != MotionEvent.ACTION_DOWN) {
                if (moveState) {
                    moveDetector.onTouchEvent(event);
                    return true;
                }
            } else {
                moveState = false;
            }
            detector.onTouchEvent(event);
            return true;
        }

        Spring stableViewSpring;

        final void changeStableTo1() {
            if (stableViewSpring != null) {
                stableViewSpring.destroy();
            }
            SpringSystem springSystem = SpringSystem.create();
            stableViewSpring = springSystem.createSpring();
            stableViewSpring.addListener(new SimpleSpringListener() {
                float mf = mStableShow.getScaleX();
                FloatEvaluator floatEvaluator = new FloatEvaluator();

                @Override
                public void onSpringUpdate(Spring spring) {
                    float f = (float) spring.getCurrentValue();
                    float jX = floatEvaluator.evaluate(f, mf, 1f);
                    mStableShow.setScaleX(jX);
                    mStableShow.setScaleY(jX);
                }

                @Override
                public void onSpringAtRest(Spring spring) {
                    super.onSpringAtRest(spring);
                    mStableShow.setImageResource(R.drawable.floattouch_round_1);
                    stableViewSpring = null;
                }

                @Override
                public void onSpringEndStateChange(Spring spring) {
                    super.onSpringEndStateChange(spring);
                }
            });
            stableViewSpring.setEndValue(1);
        }

        final void changeStableTo2() {
            if (stableViewSpring != null) {
                stableViewSpring.destroy();
            }
            SpringSystem springSystem = SpringSystem.create();
            stableViewSpring = springSystem.createSpring();
            stableViewSpring.addListener(new SimpleSpringListener() {
                float mf = mStableShow.getScaleX();
                FloatEvaluator floatEvaluator = new FloatEvaluator();

                @Override
                public void onSpringUpdate(Spring spring) {
                    float f = (float) spring.getCurrentValue();
                    float jX = floatEvaluator.evaluate(f, mf, 0.8f);
                    mStableShow.setScaleX(jX);
                    mStableShow.setScaleY(jX);
                }

                @Override
                public void onSpringAtRest(Spring spring) {
                    super.onSpringAtRest(spring);
                    mStableShow.setImageResource(R.drawable.floattouch_round_2);
                    stableViewSpring = null;
                }

                @Override
                public void onSpringEndStateChange(Spring spring) {
                    super.onSpringEndStateChange(spring);
                }
            });
            stableViewSpring.setEndValue(1);
        }


        final class MyGestureDetector extends GestureDetector {
            public final MyOnGestureListener listener;

            public MyGestureDetector(Context context, MyOnGestureListener listener) {
                super(context, listener);
                this.listener = listener;
                setIsLongpressEnabled(true);
            }
        }

        final class MoveGestureDetector extends GestureDetector {

            public MoveGestureDetector(Context context) {
                super(context, new MoveOnGestureListener());
            }
        }

        final class MoveOnGestureListener extends GestureDetector.SimpleOnGestureListener {
            float startY;
            boolean state;

            @Override
            public boolean onDown(MotionEvent e) {
                startY = e.getY();
                state = false;
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

        //only a Proxy for move and maintain windowstate
        final class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
            final TargetViewHelper targetViewHelper = new TargetViewHelper(context, handler);

            public boolean onDown(MotionEvent e) {
                targetViewHelper.initOnDown();
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                targetViewHelper.onSingleTapUp();
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                targetViewHelper.onMove(-distanceX, -distanceY);

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
                return false;
            }

        }

        private final MyGestureDetector detector = new MyGestureDetector(context, new MyOnGestureListener());


        private final MoveGestureDetector moveDetector = new MoveGestureDetector(context);

    }

}

