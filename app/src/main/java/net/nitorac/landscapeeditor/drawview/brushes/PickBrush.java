package net.nitorac.landscapeeditor.drawview.brushes;

import android.graphics.Bitmap;

import net.nitorac.landscapeeditor.MainActivity;
import net.nitorac.landscapeeditor.colorview.ColorItem;
import net.nitorac.landscapeeditor.colorview.ColorView;
import net.nitorac.landscapeeditor.drawview.DrawingView;

public class PickBrush extends Brush {

    private DrawingView view;

    public PickBrush(DrawingView view, int minSizePx, int maxSizePx) {
        super(minSizePx, maxSizePx);
        this.view = view;
    }

    public void pickPoint(float x, float y) {
        Bitmap bitmap = view.exportDrawing();
        System.out.println("Point : " + x + "  " + y);
        ColorItem color = ColorView.getColorFromInt(bitmap.getPixel((int) x, (int) y));
        if (color != null) {
            MainActivity.getInstance().inputFragment.updateColor(color);
        }
        MainActivity.getInstance().inputFragment.drawingView.getBrushSettings().setSelectedBrush(MainActivity.getInstance().inputFragment.lastPipetteBrush);
    }

    @Override
    public void setColor(int color) {
        mPaint.setColor(color);
    }
}
