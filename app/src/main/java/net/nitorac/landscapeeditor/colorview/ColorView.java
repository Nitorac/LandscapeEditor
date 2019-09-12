package net.nitorac.landscapeeditor.colorview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Nitorac.
 */
public class ColorView extends View {

    public static final ArrayList<ColorGroup> groupColors = new ArrayList<ColorGroup>() {{
        add(new ColorGroup(0, "Construction", Color.rgb(0, 255, 0)));
        add(new ColorGroup(1, "Sol", Color.rgb(255, 255, 0)));
        add(new ColorGroup(2, "Paysage", Color.rgb(255, 0, 0)));
        add(new ColorGroup(3, "Nature", Color.rgb(0, 0, 255)));
    }};

    public static final ArrayList<ColorItem> itemColors = new ArrayList<ColorItem>() {{
        add(new ColorItem("Pont", Color.rgb(94, 91, 197), 0));
        add(new ColorItem("Barrière", Color.rgb(112, 100, 25), 0));
        add(new ColorItem("Maison", Color.rgb(127, 69, 2), 0));
        add(new ColorItem("Plateforme", Color.rgb(143, 42, 145), 0));
        add(new ColorItem("Mur en brique", Color.rgb(170, 209, 106), 0));
        add(new ColorItem("Mur en pierre", Color.rgb(174, 41, 116), 0));
        add(new ColorItem("Mur en bois", Color.rgb(176, 193, 195), 0));
        add(new ColorItem("Terre", Color.rgb(110, 110, 40), 1));
        add(new ColorItem("Gravier", Color.rgb(124, 50, 200), 1));
        add(new ColorItem("Autres sols", Color.rgb(125, 48, 84), 1));
        add(new ColorItem("Gadoue", Color.rgb(125, 48, 84), 1));
        add(new ColorItem("Pavage", Color.rgb(139, 48, 39), 1));
        add(new ColorItem("Route", Color.rgb(148, 110, 40), 1));
        add(new ColorItem("Sable", Color.rgb(153, 153, 0), 1));
        add(new ColorItem("Nuage", Color.rgb(105, 105, 105), 2));
        add(new ColorItem("Brouillard", Color.rgb(119, 186, 29), 2));
        add(new ColorItem("Colline", Color.rgb(126, 200, 100), 2));
        add(new ColorItem("Montagne", Color.rgb(134, 150, 100), 2));
        add(new ColorItem("Rivière", Color.rgb(147, 100, 200), 2));
        add(new ColorItem("Pierre", Color.rgb(149, 100, 50), 2));
        add(new ColorItem("Mer", Color.rgb(154, 198, 218), 2));
        add(new ColorItem("Ciel", Color.rgb(156, 238, 221), 2));
        add(new ColorItem("Neige", Color.rgb(158, 158, 170), 2));
        add(new ColorItem("Roche", Color.rgb(161, 161, 100), 2));
        add(new ColorItem("Eau", Color.rgb(177, 200, 255), 2));
        add(new ColorItem("Buisson", Color.rgb(96, 110, 50), 3));
        add(new ColorItem("Fleur", Color.rgb(118, 0, 0), 3));
        add(new ColorItem("Herbe", Color.rgb(123, 200, 0), 3));
        add(new ColorItem("Paille", Color.rgb(162, 163, 235), 3));
        add(new ColorItem("Arbre", Color.rgb(168, 200, 50), 3));
        add(new ColorItem("Bois", Color.rgb(181, 123, 0), 3));
    }};

    public Paint mPaint;
    public ColorItem currentColor;

    public ColorView(Context context) {
        this(context, null, 0);
    }

    public ColorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        Collections.sort(itemColors, (c1, c2) -> {
            int temp;
            return (temp = Integer.compare(c1.getGroupId(), c2.getGroupId())) == 0 ? c1.getName().compareTo(c2.getName()) : temp;
        });
        currentColor = itemColors.get(0);
    }

    public ColorItem setColorIndex(int index) {
        ColorItem color = itemColors.get(index);
        setColor(color);
        return color;
    }

    public void setColor(ColorItem color) {
        currentColor = color;
        invalidate();
    }

    public ColorItem getColor() {
        return currentColor;
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        mPaint.setColor(currentColor.getColor());
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setShadowLayer(0.0f, 0.0f, 0.0f, Color.BLACK);
        canvas.drawPath(roundedRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), 10.0f, 10.0f), mPaint);

        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(45.0f);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setStrokeWidth(2.0f);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setShadowLayer(7.0f, 2.0f, 2.0f, Color.BLACK);

        String name = currentColor.getName();

        if(name != null){
            float textHeight = mPaint.descent() - mPaint.ascent();
            float textOffset = (textHeight / 2) - mPaint.descent();
            canvas.drawText(name, getWidth() / 2f, getHeight() / 2f + textOffset, mPaint);
        }
    }

    public void update(){
        invalidate();
    }

    static public Path roundedRect(float left, float top, float right, float bottom, float rx, float ry) {
        Path path = new Path();
        if (rx < 0) rx = 0;
        if (ry < 0) ry = 0;
        float width = right - left;
        float height = bottom - top;
        if (rx > width/2) rx = width/2;
        if (ry > height/2) ry = height/2;
        float widthMinusCorners = (width - (2 * rx));
        float heightMinusCorners = (height - (2 * ry));

        path.moveTo(right, top + ry);
        path.rQuadTo(0, -ry, -rx, -ry);//top-right corner
        path.rLineTo(-widthMinusCorners, 0);
        path.rQuadTo(-rx, 0, -rx, ry); //top-left corner
        path.rLineTo(0, heightMinusCorners);

        path.rQuadTo(0, ry, rx, ry);//bottom-left corner
        path.rLineTo(widthMinusCorners, 0);
        path.rQuadTo(rx, 0, rx, -ry); //bottom-right corner

        path.rLineTo(0, -heightMinusCorners);

        path.close();//Given close, last lineto can be removed.

        return path;
    }

    public static ColorItem getColorFromInt(int color) {
        for (ColorItem i : itemColors) {
            if (i.getColor() == color) {
                return i;
            }
        }
        return null;
    }
}
