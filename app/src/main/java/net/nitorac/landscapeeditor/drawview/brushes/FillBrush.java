package net.nitorac.landscapeeditor.drawview.brushes;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;

import androidx.annotation.NonNull;

import net.nitorac.landscapeeditor.drawview.DrawingView;
import net.nitorac.landscapeeditor.drawview.QueueLinearFloodFiller;

/**
 * Created by Nitorac.
 */
public class FillBrush extends Brush {

    private DrawingView view;

    public FillBrush(DrawingView view, int minSizePx, int maxSizePx) {
        super(minSizePx, maxSizePx);
        this.view = view;
    }

    public void fillPoint(float x, float y) {
        Bitmap bitmap = view.exportDrawing();
        bitmap = bitmap.copy(bitmap.getConfig(), true);
        System.out.println("Point : " + x + "  " + y);
        QueueLinearFloodFiller floodFiller = new QueueLinearFloodFiller(bitmap, bitmap.getPixel((int)x, (int)y), mPaint.getColor());
        Bitmap res = floodFiller.floodFill((int)x, (int)y).getImage();
        view.clear();
        view.setBackgroundImage(res);
    }

    @Override
    public void setColor(int color) {
        mPaint.setColor(color);
    }
}
