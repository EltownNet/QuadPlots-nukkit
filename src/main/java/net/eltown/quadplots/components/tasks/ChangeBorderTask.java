package net.eltown.quadplots.components.tasks;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.Task;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.data.PlotGeneratorInfo;
import net.eltown.quadplots.components.math.Direction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ChangeBorderTask extends Task {

    private final Level level;
    private final int height;
    private final Block block;
    private final Vector3 plotBeginPos;
    private final double xMax, zMax;
    private final Direction[] directions;
    private final boolean edges;

    public ChangeBorderTask(Plot plot, Block block, Level level, boolean edges, Direction... directions) {
        final PlotGeneratorInfo gen = QuadPlots.getApi().getProvider().getGeneratorInfo();
        this.level = level;
        this.plotBeginPos = QuadPlots.getApi().getPosition(plot.getX(), plot.getZ());
        this.xMax = plotBeginPos.x + gen.getPlotSize() - 1;
        this.zMax = plotBeginPos.z + gen.getPlotSize() - 1;
        this.height = gen.getHeight();
        this.block = block;
        this.edges = edges;
        if (directions.length == 0) {
            this.directions = new Direction[]{Direction.ALL};
        } else this.directions = directions;
    }

    @Override
    public void onRun(int i) {
        CompletableFuture.runAsync(() -> {

            for (double x = plotBeginPos.x + (edges ? 0 : 1); x <= xMax - (edges ? 0 : 1); x++) {
                // NORTH
                if (contains(Direction.NORTH)) level.setBlock(new Vector3(x, height + 1, plotBeginPos.z), block);
                // SOUTH
                if (contains(Direction.SOUTH)) level.setBlock(new Vector3(x, height + 1, zMax), block);
            }
            for (double z = plotBeginPos.z + (edges ? 0 : 1); z <= zMax - (edges ? 0 : 1); z++) {
                // WEST
                if (contains(Direction.WEST)) level.setBlock(new Vector3(plotBeginPos.x, height + 1, z), block);
                // EAST
                if (contains(Direction.EAST)) level.setBlock(new Vector3(xMax, height + 1, z), block);
            }
        });
    }

    private boolean contains(final Direction direction) {
        return Arrays.stream(directions).anyMatch(i -> i == direction || i == Direction.ALL);
    }

}
