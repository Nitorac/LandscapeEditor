package net.nitorac.landscapeeditor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.util.List;

public class StyleListAdapter extends ArrayAdapter<Integer> {
    private InputFragment frag;
    private List<Integer> items;

    public StyleListAdapter(@NonNull InputFragment frag, List<Integer> mItems) {
        super(MainActivity.getInstance(), 0, mItems);
        items = mItems;
        this.frag = frag;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Integer getItem(int position) {
        return items.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String style = position == 0 ? "random" : String.valueOf(position - 1);
        convertView = LayoutInflater.from(MainActivity.getInstance()).inflate(R.layout.style_item, parent, false);
        ((ImageView) convertView.findViewById(R.id.styleItemImage)).setImageResource(frag.getResources().getIdentifier("s" + style, "drawable", MainActivity.getInstance().getPackageName()));
        convertView.setOnClickListener(v -> {
            frag.updateStyle(style);
            if (frag.getStyleDialog() != null && frag.getStyleDialog().isShowing()) {
                frag.getStyleDialog().dismiss();
            }
        });
        return convertView;
    }
}
