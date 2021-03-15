package net.eltown.quadplots.components.tasks;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.Task;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.data.PlotGeneratorInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ChangeBorderTask extends Task {

    private final Level level;
    private final int height;
    private final Block block;
    private final Vector3 plotBeginPos;
    private final double xMax, zMax;

    public ChangeBorderTask(Plot plot, Block block, Level level) {
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
                level.setBlock(new Vector3(x, height + 1, plotBeginPos.z), block);
                level.setBlock(new Vector3(x, height + 1, zMax), block);
            }
            for (double z = plotBeginPos.z; z <= zMax; z++) {
                level.setBlock(new Vector3(plotBeginPos.x, height + 1, z), block);
                level.setBlock(new Vector3(xMax, height + 1, z), block);
            }
        });
    }

}
