package net.eltown.quadplots.components.data;

import lombok.Data;

@Data
public class PlotGeneratorInfo {

    private final String level;
    private final int height, plotSize, roadWidth;
    private final int[] ground, border, road, fill;

    public int getTotalSize() {
        return plotSize + roadWidth;
    }

}
