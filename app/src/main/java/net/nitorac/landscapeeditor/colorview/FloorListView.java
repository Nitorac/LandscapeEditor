package net.nitorac.landscapeeditor.colorview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import androidx.annotation.Nullable;

/**
 * Created by fenjuly on 16/3/1.
 */
public class FloorListView extends ListView {

    public static final int NORMAL = -1; //not in use now
    public static final int ABOVE = 0;
    public static final int BELOW = 1;

    int mTranstateY = 0;
    boolean isFirstScrolling = true;
    PinnedView mPinnedView;
    private int rHeight = 2;
    private OnFloorChangedListner onFloorChangedListner;
    private int mode = NORMAL;

    Paint paint = new Paint();

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setOnFloorChangedListner(OnFloorChangedListner onFloorChangedListner) {
        this.onFloorChangedListner = onFloorChangedListner;
    }

    public int getrHeight() {
        return rHeight;
    }

    public void setrHeight(int rHeight) {
        this.rHeight = rHeight;
    }

    public FloorListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnScrollListener(l);
        mPinnedView = new PinnedView();

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        View v;
        v = getChildAt(0);
        int l = getPaddingLeft();
        int r = l + v.getWidth();
        int t = getPaddingTop();
        int b = t + v.getHeight();

        if(mode == ABOVE) {
            canvas.clipRect(l, t, r, t + b - mTranstateY-dp2px(rHeight));
        } else if (mode == BELOW) {
            canvas.clipRect(l, t, r, t + b);
        }

        canvas.drawLine(l,t+b,r,t+b,paint);
        canvas.translate(l, t + mTranstateY);
        drawChild(canvas, v, getDrawingTime());

    }

    private final AbsListView.OnScrollListener l = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            View v = getChildAt(0);
            if (isFirstScrolling) {
                isFirstScrolling = false;
                mTranstateY = 0;
            } else {
                if (mPinnedView.position == -1) {
                    mPinnedView.position = 0;
                }
                if (v.getTop() < getPaddingTop() && v.getBottom() > getPaddingTop()) {
                    if (onFloorChangedListner != null) {
                        onFloorChangedListner.OnFloorUpward();
                    }
                    if (mode == ABOVE) {
                        for (int i = 0; i < getChildCount(); i++) {
                            if (i != 0) {
                                getChildAt(i).setElevation(dp2px(1+rHeight));
                            } else {
                                getChildAt(i).setElevation(dp2px(1));
                            }
                        }
                    } else if (mode == BELOW) {
                        /**
                         * complete in future
                         */
                    }
                }
                mTranstateY = v.getHeight() - v.getBottom();

                if (mPinnedView.position != firstVisibleItem) {

                    if (onFloorChangedListner != null) {
                        onFloorChangedListner.OnFloorFalldown();
                    }
                    mPinnedView.position = firstVisibleItem;
                    if (mode == BELOW) {
                        /**
                         * complete in future
                         */
                    }
                    if (mode == ABOVE) {
                        for (int i =  0; i < getChildCount(); i++) {
                            getChildAt(i).setElevation(1);
                        }
                    }
                }
                if (mPinnedView.position == 0) {
                    if (v.getBottom() == v.getHeight()) {
                        for (int i =  0; i < getChildCount(); i++) {
                            getChildAt(i).setElevation(dp2px(1));
                        }
                    }
                }
            }

        }
    };

    @Override
    public void setOnScrollListener(OnScrollListener listener) {
        if (listener == l) {
            super.setOnScrollListener(listener);
        }
    }

    private float dp2px(float dp) {
        final float scale = this.getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    private class PinnedView {
        View view;
        int position;
        public PinnedView(){
            position = -1;
        }
        int top;
        int bottom;
    }

    public interface OnFloorChangedListner {
        public void OnFloorUpward();
        public void OnFloorFalldown();
    }
}