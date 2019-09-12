package net.nitorac.landscapeeditor.drawview.brushes;


import android.graphics.Canvas;

import net.nitorac.landscapeeditor.drawview.DrawingEvent;


public interface BrushRenderer {
    void draw(Canvas canvas);
    void onTouch(DrawingEvent drawingEvent);
    void setBrush(Brush brush);
}
