package net.nitorac.landscapeeditor.drawview.brushes.androidpathbrushes;

import android.graphics.Paint;

import net.nitorac.landscapeeditor.drawview.brushes.Brush;

public abstract class PathBrush extends Brush {


    PathBrush(int minSizePx, int maxSizePx) {
        super(minSizePx, maxSizePx);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(false);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    public void setSizeInPercentage(float sizePercentage) {
        super.setSizeInPercentage(sizePercentage);
        mPaint.setStrokeWidth(getSizeInPixel());
    }

    @Override
    public int getSizeForSafeCrop() {
        return super.getSizeForSafeCrop() * 2;
    }
}
