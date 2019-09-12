package net.nitorac.landscapeeditor.colorview;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ColorGroup {
    private int groupId;
    private String name;
    private int color;
}
