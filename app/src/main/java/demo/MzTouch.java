package demo;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.IntEvaluator;

import cc.kenai.demo.R;
import hugo.weaving.DebugLog;
import timber.log.Timber;

public class MzTouch {
    final Context context;
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mainTouchListener.showMainView();
                    break;
                case 0:
                    mainTouchListener.dismissMainView();
                    break;

            }
        }
    };

    final MainViewHelper mainViewHelper = new MainViewHelper();

    protected MainTouchListener mainTouchListener;
    ViewGroup mTopViewGroup;

    ImageView mMainButton;
    ImageView mStableShow, mMoveShow;

    boolean isShow = false;
    boolean haveMoved = false;

    public MzTouch(final Context context) {
        this.context = context;

        mTopViewGroup = (ViewGroup) View.inflate(context, R.layout.tools_mainviewgroup, null);

        mMainButton = (ImageView) mTopViewGroup.findViewById(R.id.mainbutton);
        mMoveShow = (ImageView) mTopViewGroup.findViewById(R.id.moveshow);
        mStableShow = (ImageView) mTopViewGroup.findViewById(R.id.stableshow);

        mainTouchListener = new MainTouchListener();
        mTopViewGroup.setOnTouchListener(mainTouchListener);

    }


    public final void init() {
        mainTouchListener = new MainTouchListener();
        mMainButton.setOnTouchListener(mainTouchListener);
        mainViewHelper.changeState(State.SHOW_HOLD);
    }

    public void destroy() {
        mainViewHelper.changeState(State.DISMISS);
    }


    class MainTouchListener implements View.OnTouchListener {
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
            detector.setIsLongpressEnabled(true);
        }

        public final void dismissMainView() {
            if (!isShow) {
                return;
            }
            isShow = false;
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.removeView(mTopViewGroup);
        }


        final MyGestureDetector detector = new MyGestureDetector(context, new MyOnGestureListener());
        final MoveGestureDetector moveDetector = new MoveGestureDetector(context, new MoveOnGestureListener());


        public boolean canTouch = true;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (canTouch) {
                if (event.getAction() != MotionEvent.ACTION_DOWN) {
                    if (detector.listener.moveState) {
                        moveDetector.onTouchEvent(event);
                        return true;
                    }
                }
                detector.onTouchEvent(event);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    detector.onUp();
                }
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


            public void onUp() {
                listener.onUp();
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

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (!afterHaveMoved) {
                    mainViewHelper.changeState(State.SHOW);
                }
                afterHaveMoved = false;
                return true;
            }
        }

        //所有关于窗口操作的逻辑都在这里处理
        //主要用来维护状态，包括位移和操作的状态切换、恢复，真正的位移处理放在包装中
        //总体思想是按次进行处理，每次按下的时候重置一次包装类的大部分参数
        class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {

            final TargetViewHelper targetViewHelper = new TargetViewHelper();

            //标识当前是否是--位移模式
            private boolean moveState = false;


            //一定会执行，并且会在onSingleTapUp 之后执行
            //所以初始化应该放在此处执行
            public boolean onDown(MotionEvent e) {
                //包括一系列的初始化
                mStableShow.setImageResource(R.drawable.round_2);
                if (!targetViewHelper.getPrepared()) {
                    targetViewHelper.prepare();
                }
                targetViewHelper.initViewHelper();
                moveState = false;
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
//              todo  MzBack.back();
                targetViewHelper.normal(handler);
                return false;
            }

            //一定会执行，并且会在onSingleTapUp 之后执行
            //所以reset应该放在此处执行
            public boolean onUp() {
                if (targetViewHelper.getInited()) {
                    targetViewHelper.update();
                } else {
                }

                //仅仅会在用户用了不正确的拖动的时候触发
                Point point = targetViewHelper.getPoint();
                if (point.x == 0 && point.y == 0) {
                    targetViewHelper.reset();
                }
                mStableShow.setImageResource(R.drawable.ic_launcher);
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (!targetViewHelper.getInited()) targetViewHelper.init();
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

    enum MOVESTATE {
        NONE, X, LEFT, RIGHT, Y
    }

    //所有关于窗口移动的操作都在此处处理
    final class TargetViewHelper {

        //用来记录从down之后传递进来的所有值
        float totalX, totalY;
        //用来记录实际的位移值
        int moveX, moveY;
        //用来记录移动状态，分为横移
        MOVESTATE movestate;


        MzWindowMoveHelper moveHelper = new MzWindowMoveHelper();

        public final void initViewHelper() {
            Point point = moveHelper.getPoint();
            moveX = point.x;
            moveY = point.y;
            totalX = totalY = 0;
            if (moveX == 0 && moveY == 0) {
                movestate = MOVESTATE.NONE;
            }
        }

        public final boolean getPrepared() {
            return moveHelper.getPrepared();
        }

        public final boolean getInited() {
            return moveHelper.getInited();
        }

        public final Point getPoint() {
            return moveHelper.getPoint();
        }

        public final void prepare() {
            moveHelper.prepare(context);
        }

        public final void init() {
            moveHelper.init();
        }


        /**
         * 传递进真实的值，内部再进行处理
         */
        public final void onMove(float x, float y) {
            //todo 先不考虑返回的问题

            totalX += x*1.5;
            totalY += y*1.5;

            dealWithMove(totalX, totalY);

            float scale = getScale(moveX + totalX, moveY + totalY);
            mStableShow.setScaleX(scale);
            mStableShow.setScaleY(scale);
        }


        public void dealWithMove(float x, float y) {
            Point point = new Point();
            switch (movestate) {
                //此时来初始化路径
                case NONE:
                    //todo 需要抽取成dp
                    if (Math.abs(x) > 50 || y > 50) {
                        double p = (180 * Math.atan2(x, y) / Math.PI);
                        Timber.d("当前角度：" + p);
                        int mp = (int) Math.abs(p);
                        if (mp < 30) {
                            movestate = MOVESTATE.Y;
                        } else if (mp < 75) {
                            if (x < 0) {
                                movestate = MOVESTATE.LEFT;
                            } else {
                                movestate = MOVESTATE.RIGHT;
                            }
                        } else if (mp < 135) {
                            movestate = MOVESTATE.X;
                        } else {
                            //说明用户用了不正确的触发条件
                            break;
                        }
                        totalX = totalY = 0;
                    }
                    break;
                case X:
                    point.x = (int) (moveX + x);
                    point.y = moveY;
                    moveX(point);
                    break;
                case LEFT:
                    point.x = (int) (moveX + x);
                    point.y = (int) (moveY + y);
                    moveLeft(point);
                    break;
                case RIGHT:
                    point.x = (int) (moveX + x);
                    point.y = (int) (moveY + y);
                    moveRight(point);
                    break;
                case Y:
                    point.x = moveX;
                    point.y = (int) (moveY + y);
                    moveY(point);
                    break;
            }
        }

        void moveX(Point myPoint) {
            if(myPoint.x>400||myPoint.x<-400){
                return;
            }
            moveHelper.move(myPoint.x, myPoint.y);
        }
        void moveY(Point myPoint) {
            if(myPoint.y>800){
                return;
            }
            if(myPoint.y<0){
                myPoint.y=0;
            }
            moveHelper.move(myPoint.x, myPoint.y);
        }

        Point tpPoint = new Point(200, 600);
        void moveRight(Point myPoint) {
            double aa = Math.atan((double) myPoint.x / (double) myPoint.y);

            double ba = Math.atan((double) tpPoint.x / (double) tpPoint.y);

            double l = Math.cos(aa - ba) * Math.sqrt(myPoint.x * myPoint.x + myPoint.y * myPoint.y);

            if(l>800){
                return;
            }

            int toY=(int) (Math.cos(ba) * l);
            if(toY<0){
                moveHelper.move(0,0);
            }else {

                moveHelper.move((int) (Math.sin(ba) * l), toY);
            }
        }

        void moveLeft(Point myPoint) {
            double aa = Math.atan((double) myPoint.x / (double) myPoint.y);

            double ba = Math.atan((double) tpPoint.x / (double) tpPoint.y);

            double l = Math.cos(aa - ba) * Math.sqrt(myPoint.x * myPoint.x + myPoint.y * myPoint.y);

            if(l>800){
                return;
            }

            int toY=(int) (Math.cos(ba) * l);
            if(toY<0){
                moveHelper.move(0,0);
            }else {

                moveHelper.move(-(int) (Math.sin(ba) * l), toY);
            }
        }

        //返回一个放大缩小的参数
        public final float getScale(float x, float y) {
            float f = 1 - (x * x + y * y) / 500000f;
            if (f < 0.3f) {
                return 0.3f;
            }
            return f;
        }

        public final void moveNoDeal(int x, int y) {
            moveHelper.move(x, y);

            float scale = getScale(x, y);
            mStableShow.setScaleX(scale);
            mStableShow.setScaleY(scale);
        }


        protected final void update() {
            moveHelper.update();
        }

        private final void reset() {
            moveHelper.reset();
        }


        @DebugLog
        final int testX(int i) {
            if (i <= 300) {
                return i;
            } else {
                return (i - 300) / 3 + 300;
            }
        }

        @DebugLog
        final int testY(int i) {
            if (i <= 500) {
                return i;
            } else {
                return (i - 500) / 3 + 500;
            }
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

        private final void moveToNormal() {
            final Point point = moveHelper.getPoint();

            SpringSystem springSystem = SpringSystem.create();

            // Add a spring to the system.
            Spring spring = springSystem.createSpring();

            // Add a listener to observe the motion of the spring.
            spring.addListener(new SimpleSpringListener() {
                IntEvaluator intEvaluator = new IntEvaluator();

                @Override
                public void onSpringUpdate(Spring spring) {
                    // You can observe the updates in the spring
                    // state by asking its current value in onSpringUpdate.
                    float f = (float) spring.getCurrentValue();

                    int jX = intEvaluator.evaluate(f, point.x, 0);
                    int jY = intEvaluator.evaluate(f, point.y, 0);
                    moveNoDeal(jX, jY);
                }

                @Override
                public void onSpringAtRest(Spring spring) {
                    super.onSpringAtRest(spring);
                    moveNormal();
                }

                @Override
                public void onSpringEndStateChange(Spring spring) {
                    super.onSpringEndStateChange(spring);
                }
            });
            spring.setEndValue(1);
        }

        final void moveNormal() {
            reset();
            haveMoved = false;
        }
    }
}
