package net.nitorac.landscapeeditor.colorview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import net.nitorac.landscapeeditor.InputFragment;
import net.nitorac.landscapeeditor.MainActivity;
import net.nitorac.landscapeeditor.R;

import java.util.List;

/**
 * Created by Nitorac.
 */
public class ColorListAdapter extends ArrayAdapter<ColorItem> {

    private InputFragment frag;
    private List<ColorItem> items;

    public ColorListAdapter(@NonNull InputFragment frag, List<ColorItem> mItems) {
        super(MainActivity.getInstance(), 0, mItems);
        items = mItems;
        this.frag = frag;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public ColorItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(MainActivity.getInstance()).inflate(R.layout.color_item, parent, false);
        ((ColorView)convertView.findViewById(R.id.colorViewItem)).setColorIndex(position);
        convertView.setOnClickListener(v -> {
            frag.updateColor(((ColorView) v.findViewById(R.id.colorViewItem)).getColor());
            if (frag.getColorDialog() != null && frag.getColorDialog().isShowing()) {
                frag.getColorDialog().dismiss();
            }
        });
        return convertView;
    }
}
