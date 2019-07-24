package com.advanced.canvasexample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

class MyView extends View {

    private final Paint mPaint = new Paint();
    private final Path mPath = new Path();
    private final int mBackgroundColor;
    private Canvas mCanvas;
    private Bitmap mBitmap;

    private float mLastX, mLastY;
    private static final float TOUCH_TOLERANCE = 4;

    MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, AttributeSet attributeSet) {
        super(context);

        mBackgroundColor = ResourcesCompat.getColor(getResources(), R.color.opaque_orange, null);
        int drawColor = ResourcesCompat.getColor(getResources(), R.color.opaque_yellow, null);

        mPaint.setColor(drawColor);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE); // default: FILL
        mPaint.setStrokeJoin(Paint.Join.ROUND); // default: MITER
        mPaint.setStrokeCap(Paint.Cap.ROUND); // default: BUTT
        mPaint.setStrokeWidth(12);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(mBackgroundColor);
    }

    // SOS: every time onDraw is called, we have to draw everything from scratch. This can be seen
    // in dev.essential's DragAndDraw app where we draw all the boxes every time. An alternative to
    // this is to draw stuff incrementally on another surface (mBitmap here) using another canvas
    // (mCanvas) and then draw that bitmap on the screen inside onDraw.
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchStop();
                break;
            default:
                // Do nothing.
        }
        return true;
    }

    private void touchStart(float x, float y) {
        mPath.moveTo(x, y);
        mLastX = x;
        mLastY = y;
    }

    // SOS: I tried to draw only the part of the Path that is added, but it doesn't work (line has
    // holes in it). IOW, this is the right way to draw the path followed by the finger.
    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mLastX);
        float dy = Math.abs(y - mLastY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            // quadratic bezier, not quite getting it, maybe read theory?
            mPath.quadTo(mLastX, mLastY, (x + mLastX) / 2, (y + mLastY) / 2);
            mLastX = x;
            mLastY = y;
            mCanvas.drawPath(mPath, mPaint);
        }
    }

    // while the finger moves, we draw the uncompleted path again and again (a bit longer each time).
    // When the finger is lifted, the whole path has been drawn, so we just reset the path.
    private void touchStop() {
        mPath.reset();
    }
}
