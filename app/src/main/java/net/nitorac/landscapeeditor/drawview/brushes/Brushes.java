package net.nitorac.landscapeeditor.drawview.brushes;


import android.content.res.Resources;
import android.graphics.BitmapFactory;

import net.nitorac.landscapeeditor.R;
import net.nitorac.landscapeeditor.drawview.DrawingView;
import net.nitorac.landscapeeditor.drawview.brushes.androidpathbrushes.Eraser;
import net.nitorac.landscapeeditor.drawview.brushes.androidpathbrushes.Pen;
import net.nitorac.landscapeeditor.drawview.brushes.stampbrushes.BitmapBrush;
import net.nitorac.landscapeeditor.drawview.brushes.stampbrushes.Calligraphy;
import net.nitorac.landscapeeditor.drawview.brushes.stampbrushes.RandRotBitmapBrush;

public class Brushes {

    public static final int PEN = 0;
    public static final int PENCIL = 1;
    public static final int CALLIGRAPHY = 2;
    public static final int AIR_BRUSH = 3;
    public static final int ERASER = 4;
    public static final int FILL = 5;
    public static final int PICK = 6;


    private BrushSettings mBrushSettings;
    private Brush[] mBrushes;

    public Brushes(DrawingView view, Resources resources){
        mBrushes = new Brush[7];
        
        mBrushes[PEN] = new Pen(
                resources.getDimensionPixelSize(R.dimen.pen_min_stroke_size),
                resources.getDimensionPixelSize(R.dimen.pen_max_stroke_size));

        RandRotBitmapBrush pencil = new RandRotBitmapBrush(
                BitmapFactory.decodeResource(resources, R.drawable.brush_pencil),
                resources.getDimensionPixelSize(R.dimen.pencil_min_stroke_size),
                resources.getDimensionPixelSize(R.dimen.pencil_max_stroke_size),
                6);
        mBrushes[PENCIL] = pencil;

        mBrushes[ERASER] = new Eraser(
                resources.getDimensionPixelSize(R.dimen.pen_min_stroke_size),
                resources.getDimensionPixelSize(R.dimen.pen_max_stroke_size));

        BitmapBrush airBrush = new BitmapBrush(
                BitmapFactory.decodeResource(resources,R.drawable.brush_0),
                resources.getDimensionPixelSize(R.dimen.brush0_min_stroke_size),
                resources.getDimensionPixelSize(R.dimen.brush0_max_stroke_size),
                6);
        mBrushes[AIR_BRUSH] = airBrush;

        Calligraphy calligraphy = new Calligraphy(
                resources.getDimensionPixelSize(R.dimen.calligraphy_min_stroke_size),
                resources.getDimensionPixelSize(R.dimen.calligraphy_max_stroke_size),
                20);
        mBrushes[CALLIGRAPHY] = calligraphy;

        mBrushes[FILL] = new FillBrush(view, 1, 1);

        mBrushes[PICK] = new PickBrush(view, 1, 1);

        for (Brush brush : mBrushes) {
            brush.setSizeInPercentage(0.5f);
            brush.setColor(0xff000000);
        }

        mBrushSettings = new BrushSettings(this);
    }

    public Brush getBrush(int brushID){
        if (brushID >= mBrushes.length || brushID < 0)
            throw new IllegalArgumentException("There is no brush with id = " + brushID + " in " + getClass());
        return mBrushes[brushID];
    }

    Brush[] getBrushes() {
        return mBrushes;
    }

    public BrushSettings getBrushSettings() {
        return mBrushSettings;
    }
}
