package net.nitorac.landscapeeditor.colorview;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ColorItem {
    private String name;
    private int color;
    private int groupId;
}
