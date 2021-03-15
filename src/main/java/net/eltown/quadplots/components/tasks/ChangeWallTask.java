package net.eltown.quadplots.components.tasks;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.Task;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.data.PlotGeneratorInfo;

import java.util.concurrent.CompletableFuture;

public class ChangeWallTask extends Task {

    private final Level level;
    private final int height;
    private final Block block;
    private final Vector3 plotBeginPos;
    private final double xMax, zMax;

    public ChangeWallTask(Plot plot, Block block, Level level) {
        final PlotGeneratorInfo gen = QuadPlots.getApi().getProvider().getGeneratorInfo();
        this.level = level;
        this.plotBeginPos = QuadPlots.getApi().getPosition(plot.getX(), plot.getZ());
        this.xMax = plotBeginPos.x + gen.getPlotSize() - 1;
        this.zMax = plotBeginPos.z + gen.getPlotSize() - 1;
        this.height = gen.getHeight();
        this.block = block;
    }

    @Override
    public void onRun(int i) {
        CompletableFuture.runAsync(() -> {
            for (double x = plotBeginPos.x; x <= xMax; x++) {
                for (int y = 1; y <= height; y++) {
                    level.setBlock(new Vector3(x, y, plotBeginPos.z), block);
                    level.setBlock(new Vector3(x, y, zMax), block);
                }
            }
            for (double z = plotBeginPos.z; z <= zMax; z++) {
                for (int y = 1; y <= height; y++) {
                    level.setBlock(new Vector3(plotBeginPos.x, y, z), block);
                    level.setBlock(new Vector3(xMax, y, z), block);
                }
            }
        });
    }
}
