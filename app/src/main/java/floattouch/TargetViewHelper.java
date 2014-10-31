package floattouch;

import android.animation.IntEvaluator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;

import java.util.concurrent.atomic.AtomicBoolean;

import cc.kenai.demo.R;

//move window and maintain window state
@TargetApi(19)
class TargetViewHelper {
    private enum MOVESTATE {
        NONE, X_LEFT, X_RIGHT, LEFT, RIGHT, Y
    }

    private enum TOSTATE {
        X_LEFT, X_RIGHT, LEFT, RIGHT, UP, BOTTOM
    }

    final Context context;
    final Handler handler;
    final WindowMoveHelper moveHelper = new WindowMoveHelper();
    final WindowMoveHelper.MoveListener moveStateListener;
    final int distance_x, distance_y, distance_prepare;

    //todo must check
    MoveToRunnable moveToRunnable;
    MoveToNormalRunnable moveToNormalRunnable;
    float totalX, totalY;
    //mark if the touch event has been handled
    private boolean hasDeal = false;
    private MOVESTATE movestate = MOVESTATE.NONE;

    public TargetViewHelper(Context context, final Handler handler) {
        this.context = context;
        this.handler = handler;
        distance_x = (int) context.getResources().getDimension(R.dimen.floattouch_move_x);
        distance_y = (int) context.getResources().getDimension(R.dimen.floattouch_move_y);
        distance_prepare = (int) context.getResources().getDimension(R.dimen.floattouch_prepare);
        moveStateListener = new WindowMoveHelper.MoveListener() {
            @Override
            public void toResetMovedWin() {
                //todo may repeat many time
                normal();
            }

            @Override
            public void movedWinHasBeenReset() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        resetWindowHelper();
                        initTargetViewHelper();
                    }
                });
            }
        };
        WindowMoveHelper.setListener(context, moveStateListener);
    }

    public final void initOnDown() {
        if (!moveHelper.getIsPrepared()) moveHelper.prepare(context);
        totalX = totalY = 0;
        hasDeal = false;
    }

    public final void onMove(float scrollX, float scrollY) {
        totalX += scrollX;
        totalY += scrollY;
        if (!hasDeal) {
            hasDeal = dealWithMove(totalX, totalY);
        }
    }

    public final boolean onSingleTapUp() {
        if (moveHelper.getIsInited()) {
            normal();
            return true;
        } else {
            BackSimulate.back();
            moveHelper.reset();
            return false;
        }
    }

    final void initTargetViewHelper() {
        totalX = totalY = 0;
        movestate = MOVESTATE.NONE;
        if (moveToNormalRunnable != null) {
            moveToNormalRunnable.state.set(false);
        }
        if (moveToRunnable != null) {
            moveToRunnable.state.set(false);
        }
    }


    final void resetWindowHelper() {
        movestate = MOVESTATE.NONE;
        if (moveToNormalRunnable != null) {
            moveToNormalRunnable.state.set(false);
        }
        if (moveToRunnable != null) {
            moveToRunnable.state.set(false);
        }
        moveHelper.reset();
    }


    final void moveto(MOVESTATE state) {
        if (!moveHelper.getIsInited()) moveHelper.init();
        switch (state) {
            case NONE:
                normal();
                movestate = MOVESTATE.NONE;
                break;
            case X_LEFT:
                moveTo(-distance_x, 0);
                movestate = MOVESTATE.X_LEFT;
                break;
            case X_RIGHT:
                moveTo(distance_x, 0);
                movestate = MOVESTATE.X_RIGHT;
                break;
            case LEFT:
                moveTo(-distance_x, distance_y);
                movestate = MOVESTATE.LEFT;
                break;
            case RIGHT:
                moveTo(distance_x, distance_y);
                movestate = MOVESTATE.RIGHT;
                break;
            case Y:
                moveTo(0, distance_y);
                movestate = MOVESTATE.Y;
                break;
        }
    }

    final boolean dealWithMove(float x, float y) {
        if (Math.abs(x) > distance_prepare || y > distance_prepare) {
            TOSTATE tostate;
            double p = (180 * Math.atan2(x, y) / Math.PI);
            int mp = (int) Math.abs(p);
            if (mp < 30) {
                tostate = TOSTATE.BOTTOM;
            } else if (mp < 75) {
                if (x < 0) {
                    tostate = TOSTATE.LEFT;
                } else {
                    tostate = TOSTATE.RIGHT;
                }
            } else if (mp < 135) {
                if (p < 0) {
                    tostate = TOSTATE.X_LEFT;
                } else {
                    tostate = TOSTATE.X_RIGHT;
                }
            } else {
                tostate = TOSTATE.UP;
            }

            switch (movestate) {
                case NONE:
                    switch (tostate) {
                        case X_LEFT:
                            moveto(MOVESTATE.X_LEFT);
                            break;
                        case X_RIGHT:
                            moveto(MOVESTATE.X_RIGHT);
                            break;
                        case LEFT:
                            moveto(MOVESTATE.LEFT);
                            break;
                        case RIGHT:
                            moveto(MOVESTATE.RIGHT);
                            break;
                        case UP:
                            moveHelper.reset();
                            break;
                        case BOTTOM:
                            moveto(MOVESTATE.Y);
                            break;
                    }
                    break;
                case X_LEFT:
                    switch (tostate) {
                        case X_LEFT:
                            moveto(MOVESTATE.X_LEFT);
                            break;
                        case X_RIGHT:
                            moveto(MOVESTATE.NONE);
                            break;
                        case LEFT:
                            moveto(MOVESTATE.LEFT);
                            break;
                        case RIGHT:
                            moveto(MOVESTATE.RIGHT);
                            break;
                        case UP:
                            moveto(MOVESTATE.NONE);
                            break;
                        case BOTTOM:
                            moveto(MOVESTATE.LEFT);
                            break;
                    }
                    break;
                case X_RIGHT:
                    switch (tostate) {
                        case X_LEFT:
                            moveto(MOVESTATE.NONE);
                            break;
                        case X_RIGHT:
                            moveto(MOVESTATE.X_RIGHT);
                            break;
                        case LEFT:
                            moveto(MOVESTATE.LEFT);
                            break;
                        case RIGHT:
                            moveto(MOVESTATE.RIGHT);
                            break;
                        case UP:
                            moveto(MOVESTATE.NONE);
                            break;
                        case BOTTOM:
                            moveto(MOVESTATE.RIGHT);
                            break;
                    }
                    break;
                case LEFT:
                    switch (tostate) {
                        case X_LEFT:
                            moveto(MOVESTATE.LEFT);
                            break;
                        case X_RIGHT:
                            moveto(MOVESTATE.Y);
                            break;
                        case LEFT:
                            moveto(MOVESTATE.LEFT);
                            break;
                        case RIGHT:
                            moveto(MOVESTATE.RIGHT);
                            break;
                        case UP:
                            moveto(MOVESTATE.NONE);
                            break;
                        case BOTTOM:
                            moveto(MOVESTATE.LEFT);
                            break;
                    }
                    break;
                case RIGHT:
                    switch (tostate) {
                        case X_LEFT:
                            moveto(MOVESTATE.Y);
                            break;
                        case X_RIGHT:
                            moveto(MOVESTATE.RIGHT);
                            break;
                        case LEFT:
                            moveto(MOVESTATE.LEFT);
                            break;
                        case RIGHT:
                            moveto(MOVESTATE.RIGHT);
                            break;
                        case UP:
                            moveto(MOVESTATE.NONE);
                            break;
                        case BOTTOM:
                            moveto(MOVESTATE.RIGHT);
                            break;
                    }
                    break;
                case Y:
                    switch (tostate) {
                        case X_LEFT:
                            moveto(MOVESTATE.LEFT);
                            break;
                        case X_RIGHT:
                            moveto(MOVESTATE.RIGHT);
                            break;
                        case LEFT:
                            moveto(MOVESTATE.LEFT);
                            break;
                        case RIGHT:
                            moveto(MOVESTATE.RIGHT);
                            break;
                        case UP:
                            moveto(MOVESTATE.NONE);
                            break;
                        case BOTTOM:
                            moveto(MOVESTATE.Y);
                            break;
                    }
                    break;
            }
            return true;
        }
        return false;

    }

    final void normal() {
        if (moveToNormalRunnable != null) {
            moveToNormalRunnable.state.set(false);
        }
        moveToNormalRunnable = new MoveToNormalRunnable();
        handler.post(moveToNormalRunnable);
    }

    final void moveTo(int toX, int toY) {
        if (moveToRunnable != null) {
            moveToRunnable.state.set(false);
        }
        moveToRunnable = new MoveToRunnable(toX, toY);
        handler.post(moveToRunnable);
    }

    final class MoveToRunnable implements Runnable {
        final int toX, toY;
        public AtomicBoolean state = new AtomicBoolean(false);

        MoveToRunnable(int toX, int toY) {
            this.toX = toX;
            this.toY = toY;
        }

        @Override
        public void run() {
            if (state.compareAndSet(false, true)) {
                moveTo();
            }
        }


        private void moveTo() {
            final Point point = WindowMoveHelper.getPoint(context);
            SpringSystem springSystem = SpringSystem.create();
            Spring spring = springSystem.createSpring();
            spring.addListener(new SimpleSpringListener() {
                IntEvaluator intEvaluator = new IntEvaluator();

                @Override
                public void onSpringUpdate(Spring spring) {
                    float f = (float) spring.getCurrentValue();
                    int jX = intEvaluator.evaluate(f, point.x, toX);
                    int jY = intEvaluator.evaluate(f, point.y, toY);
                    if (state.get()) moveHelper.move(jX, jY);
                }

                @Override
                public void onSpringAtRest(Spring spring) {
                    super.onSpringAtRest(spring);
                    moveHelper.update();
                }

                @Override
                public void onSpringEndStateChange(Spring spring) {
                    super.onSpringEndStateChange(spring);
                }
            });
            spring.setEndValue(1);
        }
    }

    final class MoveToNormalRunnable implements Runnable {
        public AtomicBoolean state = new AtomicBoolean(false);

        @Override
        public void run() {
            if (state.compareAndSet(false, true)) {
                if (moveToRunnable != null) {
                    moveToRunnable.state.set(false);
                }
                moveToNormal();
            }
        }

        private void moveToNormal() {
            final Point point = WindowMoveHelper.getPoint(context);
            SpringSystem springSystem = SpringSystem.create();
            Spring spring = springSystem.createSpring();
            spring.addListener(new SimpleSpringListener() {
                IntEvaluator intEvaluator = new IntEvaluator();

                @Override
                public void onSpringUpdate(Spring spring) {
                    float f = (float) spring.getCurrentValue();
                    int jX = intEvaluator.evaluate(f, point.x, 0);
                    int jY = intEvaluator.evaluate(f, point.y, 0);
                    if (state.get()) moveHelper.move(jX, jY);
                }

                @Override
                public void onSpringAtRest(Spring spring) {
                    super.onSpringAtRest(spring);
                    if (state.get()) resetWindowHelper();
                }

                @Override
                public void onSpringEndStateChange(Spring spring) {
                    super.onSpringEndStateChange(spring);
                }
            });
            spring.setEndValue(1);
        }
    }
}
