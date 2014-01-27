package com.voltazor.bluetooth_test;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Dmitriy Dovbnya on 27.01.14.
 */
public class KnobProcessor implements View.OnTouchListener {

    private static final int PARTS = 4;

    public static final int BOTH = 0;
    public static final int HORIZONTAL = 1;
    public static final int VERTICAL = 2;

    private int orientation;

    private float posX;
    private float posY;
    private int parentTop;
    private int parentLeft;
    private int parentRight;
    private int parentBottom;
    private float deltaTop;
    private float deltaLeft;
    private float deltaRight;
    private float deltaBottom;

    private Context context;
    private Vibrator vibrator;

    private int forward;
    private int backward;
    private int left;
    private int right;

    public KnobProcessor() {
        this(null);
    }

    public KnobProcessor(Context context) {
        this(context, BOTH);
    }

    public KnobProcessor(int orientation) {
        this(null, orientation);
    }

    public KnobProcessor(Context context, int orientation) {
        this.orientation = orientation;
        this.context = context;
        if (context != null) {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                vibrate();
                init(view, x, y);
                return true;
            case MotionEvent.ACTION_MOVE:
                if (orientation != HORIZONTAL) {
                    if (checkValueY(y, parentBottom, parentTop, deltaBottom, deltaTop)) {
                        float deltaY = y - posY;
                        view.setTranslationY(deltaY);

                        int value = calculateDec(parentTop, (parentTop + view.getTop() + view.getHeight() / 2), y, PARTS);
                        if (value >= 0 && value != forward) {
                            forward = value;
                            Log.d("forward: ", String.valueOf(forward));
                        }
                        value = calculateInc((parentTop + view.getTop() + view.getHeight() / 2), parentBottom, y, PARTS);
                        if (value >= 0 && value != backward) {
                            backward = value;
                            Log.d("backward: ", String.valueOf(backward));
                        }
                    }

//                    if (orientation != BOTH && checkPointerX(view, x)) {
//                        reset(view);
//                        return false;
//                    }
                }
                if (orientation != VERTICAL) {
                    if (checkValueX(x, parentLeft, parentRight, deltaLeft, deltaRight)) {
                        float deltaX = x - posX;
                        view.setTranslationX(deltaX);
                    }

                    int value = calculateDec(parentLeft, (parentLeft + view.getLeft() + view.getWidth() / 2), x, PARTS);
                    if (value >= 0 && value != left) {
                        left = value;
                        Log.d("left: ", String.valueOf(left));
                    }
                    value = calculateInc((parentLeft + view.getLeft() + view.getWidth() / 2), parentRight, x, PARTS);
                    if (value >= 0 && value != right) {
                        right = value;
                        Log.d("right: ", String.valueOf(right));
                    }
                      
//                    if (orientation != BOTH && checkPointerY(view, y)) {
//                        reset(view);
//                        return false;
//                    }
                }
                view.invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                reset(view);
                return true;
        }
        return false;
    }

    private void reset(View view) {
        forward = 0;
        backward = 0;
        left = 0;
        right = 0;
        posX = 0;
        posY = 0;
        view.setTranslationX(0);
        view.setTranslationY(0);
        view.invalidate();
    }

    private void init(View view, float x, float y) {
        posX = x;
        posY = y;

        ViewGroup parent = (ViewGroup) view.getParent();
        parentTop = parent.getTop();
        parentLeft = parent.getLeft();
        parentRight = parent.getRight();
        parentBottom = parent.getBottom();

        deltaLeft = posX - (parent.getLeft() + view.getLeft());
        deltaRight = (parent.getLeft() + view.getRight()) - posX;

        deltaBottom = (parent.getTop() + view.getBottom()) - posY;
        deltaTop = posY - (parent.getTop() + view.getTop());
    }

    private boolean checkValueX(float x, int parentLeft, int parentRight, float deltaLeft, float deltaRight) {
        return ((x - deltaLeft) > parentLeft) && ((x + deltaRight) < parentRight);
    }

    private boolean checkValueY(float y, int parentBottom, int parentTop, float deltaBottom, float deltaTop) {
        return (y - deltaTop) >= parentTop && (y + deltaBottom) <= parentBottom;
    }

    private boolean checkPointerX(View view, float x) {
        return x > view.getRight() || x < view.getLeft();
    }

    private boolean checkPointerY(View view, float y) {
        return y < view.getTop() || y > view.getBottom();
    }

    private int calculateDec(int min, int max, float pos, int parts) {
        float step = (max - min) / parts;
        for (int i = parts; i >= 0; i--) {
            if (pos < max - ((i - 1) * step)) {
                return i;
            }
        }
        return 0;
    }

    private int calculateInc(int min, int max, float pos, int parts) {
        float step = (max - min) / parts;
        for (int i = 0; i < parts; i++) {
            if (pos < ((i + 1) * step) + min) {
                return i;
            }
        }
        return 0;
    }

    private void vibrate() {
        if (vibrator != null) {
            vibrator.vibrate(50);
        }
    }

    public interface KnobVerticalListener {
        
        public void forward();

        public void back();

    }

    public interface KnobHorizontalListener {

        public void left();

        public void right();

    }

}
